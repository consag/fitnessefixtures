/**
 * This purpose of this fixture is to run checks
 * @author Jac. Beekers
 * @version 10 May 2015
 */ 
package nl.jacbeekers.testautomation.fitnesse.database;

import java.math.BigInteger;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.sql.*;
import java.text.ParseException;
 import java.text.SimpleDateFormat;

 import java.util.*;

public class CheckQuery {
    
    
    //TODO: Change to variable values
               private int numberOfTableColumns =7; // first column stores timestamp and is not populated by user data.
        private String columnNames ="CHECK_DATE, TABLE_NAME, COLUMN_NAME, CHECK1, CHECK2, CHECK3, CHECK4, CHECK5";

          private String className = "CheckQuery";
          private String logFileName = Constants.NOT_INITIALIZED;
          private String context = Constants.DEFAULT;
          private String startDate = Constants.NOT_INITIALIZED;

          private int logLevel =3;

          private String driver;
          private String url;
          private String userId;
          private String password;
          private String databaseName;
          private String query;
          private String databaseType;
          private String databaseConnDef;
          
          private int rowUnequalValues=0;
          private int rowEqualValues=0;
          private String rowsFound="0";  // must be "0" as used to add up query results

 private String queryDatabaseName = Constants.NOT_PROVIDED;
 private String variableList = Constants.NOT_PROVIDED;
 private String checkDatabaseName = Constants.NOT_PROVIDED;
 private String checkTable = Constants.DEFAULT_CHECK_TABLE;
 private String columnValues = Constants.NOT_INITIALIZED;
 
     private String sNoRecordColumnValue ="no col value";
        private String sDelimiter =", ";

     //initially, we only process VAR01 and VAR02. VAR03 etc to be implemented
     private String sVar01 ="#VAR01#";
     private String sVar02 ="#VAR02#";
     private String sVar ="#VAR";
     private boolean bContainsVar=false;
     private String m_queryVars[];
     private int m_nrPairs =0;
     private int nrVarsInQuery =0;

    private String checkType = Constants.NOT_PROVIDED;

     private String sNotNull ="#ANYVALUE#";

     private String m_defaultDateFormat ="yyyy-MM-dd HH:mm:ss";
     private String m_dateFormat =m_defaultDateFormat; // default if not specified
     private String sql_dateFormat = "yyyy-MM-dd-HH.mm.ss";
     private String cFitConvertDt = "FITCONVERTDT";
     private List<String> m_dbColumnTypes;
     private int iInvalidColType =-1;
     private boolean bDateFormatRequested =false;
     

 // variables used to create select query
           private String m_concatenated_column_names;
 //        private String m_concatenated_column_values;

 //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")
           private List<List<String>> return_table = new ArrayList<List<String>>();
 //        private List<String> m_return_row = new ArrayList<String>();
           
    private String return_message = Constants.NOT_INITIALIZED; //text message that is returned to fitnesse  
    private String errorMessage = Constants.NOERRORS;
    private String errorCode = Constants.OK;
    private String rc = Constants.NOT_INITIALIZED;

 //Constructors
     
     //Constructor without any arguments
     //No context provided: setting default to class name
 public CheckQuery() {
       bDateFormatRequested =false;
       setContext(className);
         logFileName = startDate + "." + className ;

     }

 //Constructor with context
 //use context in log file name

    /**
     * @param context - used in log file name
     */
    public CheckQuery(String context) {
       bDateFormatRequested =false;
       setContext(context);
         logFileName = startDate + "." + className +"." + context;

     }

 //Constructor with context and dateFormat

    /**
     * @param context - used in log file name
     * @param dateFormat - used in log file name
     */
    public CheckQuery(String context, String dateFormat) {
   
   bDateFormatRequested =true;

   if(dateFormat.equals(Constants.DEFAULT) || dateFormat.equals(Constants.DEFAULT)) {
     setDateFormat(m_defaultDateFormat);
   } else {
     setDateFormat(dateFormat);    
   }

  setContext(context);
   
 }

 private void setContext(String context) {
   java.util.Date started = new java.util.Date();
   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
   startDate = sdf.format(started);
   this.context=context;
   
 }

    /**
     * @param dateFormat 
     */
    public void setDateFormat(String dateFormat) {
            m_dateFormat = dateFormat;

        }


 //----------------------------------------------------------      
 // Another main function: 
     // returns m_row_equal_values

    /**
     * @return
     */
    public String columnsOk() {
       String returnMessage="Unknown";
     returnMessage =Integer.toString(rowEqualValues);  
       return returnMessage;
     }
 //----------------------------------------------------------      

 //----------------------------------------------------------      
 // Another main function: 
     // returns m_row_unequal_values

    /**
     * @return
     */
    public String columnsNotOk() {
       String returnMessage="Unknown";
     returnMessage =Integer.toString(rowUnequalValues);  
       return returnMessage;
     }
 //----------------------------------------------------------

    /**
     * @return - returns the logical databae name
     */
    public String getDatabaseName (){
 return databaseName;
           }

 
           private boolean hasVar(String query, String varName) {
               if(query.contains(varName)) return true; 
               else return false;
           }
 
     // function to run same query for a list of arguments
     //
     private String processQueryWithArguments(List<List<String>> resultTable) {
       String myName="processQueryWithArguments";
       String myArea="Initialization";
       String logMessage = Constants.NOT_INITIALIZED;
       String myRc = Constants.OK;
       String orgQuery = query;
       String sQuery = orgQuery;

       myArea="proc";
       
       if (! hasVar(sQuery,sVar01)) {
         logMessage="No variable #VAR01# found in query =>" + sQuery + "<.";
         setErrorMessage(logMessage);
         log(myName, Constants.ERROR, myArea, logMessage);  
         myRc = Constants.ERROR;
         rc = Constants.ERROR;
         return myRc;
       } else {
           setNrVarsInQuery(1);
       }

         if ( hasVar(sQuery,sVar02)) {
            setNrVarsInQuery(2);
           logMessage="Variable #VAR02# found in query =>" + sQuery + "<.";
           log(myName, Constants.DEBUG, myArea, logMessage);  
         }
       
       //current limitation: Only for 1 variable
       List<List<String>> dbIntermediateTable;
       
       m_queryVars = variableList.split(sDelimiter);
       
       if((m_queryVars.length % getNrVarsInQuery()) == 0) {
       m_nrPairs = m_queryVars.length / getNrVarsInQuery();
       logMessage="Number of parameter sets for query variables =>" + m_nrPairs + "<.";
       log(myName, Constants.DEBUG, myArea, logMessage);  
       // loop through the list to collect query result in dbTable
       
       if(getNrVarsInQuery() ==1) {
           for (int i=0; i < m_nrPairs; i++) {
               logMessage="Query var =>" +m_queryVars[i] +"<.";
               log(myName, Constants.DEBUG, myArea, logMessage);
               
             sQuery=orgQuery.replace(sVar01,m_queryVars[i]);
             logMessage="After VAR01 - Query modified to =>" + sQuery + "<.";
             log(myName, Constants.DEBUG, myArea, logMessage);
           setDatabaseName(getQueryDatabase());
           readParameterFile();
           dbIntermediateTable = getDatabaseTable(sQuery);
           if(rc.equals(Constants.OK)) {
                resultTable.addAll(dbIntermediateTable); 
                myRc=rc;
           } else {
            //Error on SQL statement or driver
            logMessage=return_message;
            log(myName, Constants.ERROR, myArea, logMessage);  
             setErrorMessage(logMessage);
            myRc = Constants.ERROR;
            List<String> resultRow = new ArrayList<String>();
            resultRow.add(m_queryVars[i]);
            resultRow.add(return_message);
            resultTable.add(resultRow);
           }
           }
           
       } else { 
             // there are 2 variables in the query
       for (int i=0; i < m_nrPairs *2; i+=2) {
           logMessage="Query var =>" +m_queryVars[i] +"<.";
           log(myName, Constants.DEBUG, myArea, logMessage);
           
         sQuery=orgQuery.replace(sVar01,m_queryVars[i]);
               sQuery=sQuery.replace(sVar02,m_queryVars[i+1]);
          //     i++;
           logMessage="After VAR01/VAR02 - Query modified to =>" + sQuery + "<.";
           log(myName, Constants.DEBUG, myArea, logMessage);
           
           setDatabaseName(getQueryDatabase());
           readParameterFile();
         dbIntermediateTable = getDatabaseTable(sQuery);
         if(rc.equals(Constants.OK)) {
           resultTable.addAll(dbIntermediateTable); 
           myRc=rc;
         } else {
           //Error on SQL statement or driver
           logMessage=return_message;
           log(myName, Constants.ERROR, myArea, logMessage);  
             setErrorMessage(logMessage);
           myRc = Constants.ERROR;
           List<String> resultRow = new ArrayList<String>();
           resultRow.add(m_queryVars[i]);
           resultRow.add(return_message);
           resultTable.add(resultRow);
         }
       }
       }
         
      } else {
         logMessage="Mismatch in amount of variables used and provided parameter sets. Vars# =>" + Integer.toString(getNrVarsInQuery()) + "<. Parameter# =>" + m_queryVars.length + "<.";
         log(myName, Constants.ERROR, myArea, logMessage);  
         setErrorMessage(logMessage);
         myRc = Constants.ERROR;
       }
   
       return myRc;

     }

    /**
     * @return - returns the current query
     */
    public String getQuery (){
               return query;
           }
     
     
 //----------------------------------------------------------      
 //Function to read third row of table and set column names
 //----------------------------------------------------------

    /**
     * @param input_row
     */
    private void getColumnNames (List<String> input_row){   
             String myName="getColumnNames";
             String myArea="Initialization";
             String logMessage = Constants.NOT_INITIALIZED;
       
                   List<String> return_row = new ArrayList<String>();     
                   //return_row.add("pass"); //return "pass" in first cell  
           
                   for (int i = 1; i < input_row.size(); ++i)  
                   {     // next column names
                          if (i==1){
                                  m_concatenated_column_names = input_row.get(i); //first column name
                          }
                          else{
                                  m_concatenated_column_names = m_concatenated_column_names + "," + input_row.get(i);
                          }
                          //return_row.add("pass"); //return "pass" in next cell
                   }             
       myArea="concatenated";
                   logMessage="column names: " + m_concatenated_column_names;
             log(myName, "info", myArea, logMessage);      
                   addRowToReturnTable (return_row); //return row with outcomes; pass/fail
           }  
 //----------------------------------------------------------
 //Function to compare input table with database table
 //----------------------------------------------------------

    /**
     * @param expected_table
     * @param database_table
     */
    private void CompareExpectedTableWithDatabaseTable(List<List<String>> expected_table, List<List<String>> database_table){  
                   //empty row is used, when row is filled with nulls.
       String myName="CompareExpectedTableWithDatabaseTable";
       String myArea="Start";
       String logMessage = Constants.NOT_INITIALIZED;
       
                   List<String> empty_row = new ArrayList<String>();     
       List<String> first_row = new ArrayList<String>();
       List<String> first_row_substituted_vars = new ArrayList<String>();
       
                   for (int i = 0; i < numberOfTableColumns; ++i)  
                   {     // fill empty row with spaces 
                           empty_row.add(sNoRecordColumnValue);
                   }
                   
       if (database_table.size() ==0) {
         List<String> return_row = new ArrayList<String>();  
         return_row.add(0,"Fail:No records in database.");
         addRowToReturnTable (return_row); //return row with outcomes; pass/fail
         return;
       }
                   // Expected result is equal or greater than result rows from database, the expected table size is - 3, since first 3 rows contain database name, query and column names
                   logMessage="expected #rows=>" +  Integer.toString(expected_table.size()-3) +"<. Database #rows found=>" + Integer.toString(database_table.size()) + "<.";
       log(myName, "debug", myArea,logMessage);
       
       //if there is only one expected row with values, this could contain variables like #VAR01#
       myArea="check variables for query repitition";
       bContainsVar=false;
       if(expected_table.size()-3 == 1) {
         logMessage="Precisely 1 expected row. Checking for variables. row 4 contains >" + expected_table.get(3).size() + "< entries.";
         log(myName,"info", myArea, logMessage);   
         for (int i =0; i < expected_table.get(3).size() ; ++i) {
           if(expected_table.get(3).get(i) != null) {
             if(expected_table.get(3).get(i).contains(sVar)) {
               //Yes, this row contains #VAR
               bContainsVar=true;
               first_row = expected_table.get(3);
             }}
         }
         logMessage="Completed checking for variables.";
         log(myName,"debug", myArea, logMessage);

       } else {
         logMessage="More than 1 expected row. Variables will not be interpreted.";
         log(myName,"info", myArea, logMessage);        
       }
       
       myArea="Comparing input with db";
       if((expected_table.size()-3) >= database_table.size()){
         logMessage="more or equal expected rows than database rows. Database #rows found=>" + Integer.toString(database_table.size()) + "<.";
         log(myName,"debug", myArea, logMessage);
                     myArea="Processing expectedrows>=dbrows";
                           for (int i = 0; i < (expected_table.size()-3); ++i)  
                           {     // less rows in database than expected
                                         if ((database_table.size() != 0) && (i < database_table.size())){
 //                                        m_row_unequal_values=0;
                     if(bContainsVar) {
             //Location=First expected row:Replace #VAR with value from list 
             first_row_substituted_vars.clear();
             first_row_substituted_vars.addAll(first_row);
             for (int j=0; j < first_row_substituted_vars.size(); ++j) {
               first_row_substituted_vars.set(j,first_row.get(j).replace(sVar01,m_queryVars[i]));
             }
             CompareExpectedRowWithDatabaseRow(first_row_substituted_vars, database_table.get(i));
           } else { 
             CompareExpectedRowWithDatabaseRow(expected_table.get(i+3), database_table.get(i));
           }
           }
                                         else{
 //                                              m_row_unequal_values=0;
               CompareExpectedRowWithDatabaseRow(expected_table.get(i+3), empty_row);
                                         }
                           }
                   }
                   else{
                     logMessage="more number of rows in db than expected. db records=>" + Integer.toString(database_table.size()) +"<.";
                     log(myName,"debug", myArea, logMessage);
         myArea="Processing dbrows>expectedrows";
                           for (int i = 0; i < database_table.size(); ++i)  
                           {     // more rows in database than expected
                                         if (i < (expected_table.size()-3)){
 //                                              m_row_unequal_values=0;
           logMessage="Processing expected row# >" + Integer.toString(i) +"<.";
           log(myName,"debug", myArea,logMessage);
           if(bContainsVar) {
             //Location=First expected row:Replace #VAR with value from list 
             first_row_substituted_vars.clear();
             first_row_substituted_vars.addAll(first_row);
             for (int j=0; j < first_row_substituted_vars.size(); ++j) {
               first_row_substituted_vars.set(j,first_row.get(j).replace(sVar01,m_queryVars[i]));
             }
             CompareExpectedRowWithDatabaseRow(first_row_substituted_vars, database_table.get(i));
           } else { 
             CompareExpectedRowWithDatabaseRow(expected_table.get(i+3), database_table.get(i));
           }
                                         }
                                         else{ //processing rows from database that do not have a corresponding expected row. This may be okay when variables for query repitition are used.
                                         //      m_row_unequal_values=0;
             if(bContainsVar) {
               logMessage="Processing db row# >" + Integer.toString(i) + "< with variable list found and no more expected rows specified.";
               log(myName,"debug", myArea,logMessage);
               first_row_substituted_vars.clear();
               first_row_substituted_vars.addAll(first_row);
               for (int j=0; j < first_row_substituted_vars.size(); ++j) {
 //                logMessage="Replacing variables for column =>" + Integer.toString(j) + "<. Variable pairs =>" +Integer.toString(m_nrPairs) + "<.";
 //                log(myName,"debug", myArea,logMessage);
 //                String sBefore =first_row_substituted_vars.get(j);
                 first_row_substituted_vars.set(j,first_row.get(j).replace(sVar01,m_queryVars[i]));
 //                String sAfter =first_row_substituted_vars.get(j);
 //                logMessage="First row has =>" + first_row.get(j) + "<. Before =>" + sBefore + "<. After =>" + sAfter + "<. sVar01=>" +sVar01 + "<. m_queryVars[k] =>" + m_queryVars[i] +"<.";
 //                log(myName,"debug", myArea,logMessage);
               }

               CompareExpectedRowWithDatabaseRow(first_row_substituted_vars, database_table.get(i));
             } else {
               logMessage="Processing db surplus row# >" + Integer.toString(i) +"<.";
               log(myName,"debug", myArea,logMessage);
               CompareExpectedRowWithDatabaseRow(empty_row, database_table.get(i));
             }
                                         }
                           }
                   }
           }
 //----------------------------------------------------------
 //Function to compare input row with database row
 //----------------------------------------------------------
           private void CompareExpectedRowWithDatabaseRow(List<String> expected_row, List<String> database_row){ 
                           List<String> return_row = new ArrayList<String>();    
         String myName="CompareExpectedRowWithDatabaseRow";
         String myArea="Initialization";
         String logMessage = Constants.NOT_INITIALIZED;

     rowEqualValues=0;
     rowUnequalValues=0;
     
     
     myArea="Processing";
               //Compare cell for cell if expected equals outcome
               for (int i = 1; i < numberOfTableColumns; ++i) {         
                 
                 String expected = Constants.NOT_INITIALIZED;
                 String db = Constants.NOT_INITIALIZED;
                   if(expected_row.get(i) == null) { expected="=>NULL<"; }
                   if(database_row.get(i) == null) { db="=>NULL<."; }
                 if(expected_row.get(i) != null) { 
                   if (expected_row.get(i).equals(sNoRecordColumnValue)) { 
                   expected="=>" + sNoRecordColumnValue + "<"; 
                   } else expected="=NOT NULL=>" +expected_row.get(i) +"<."; 
                   }
                 if(database_row.get(i) != null) { 
                   if (database_row.get(i).equals(sNoRecordColumnValue)) { 
                   db="=>" + sNoRecordColumnValue + "<"; 
                   } else if(database_row.get(i).isEmpty()) { 
                         db="=EMPTY=>"; 
                       } else { 
                         db="=NOT NULL=>" + database_row.get(i) +"<.";
                     } 
                   }
                       
                 String sColType = Constants.NOT_INITIALIZED;
                 int iColType=iInvalidColType;
                 
                 if(m_dbColumnTypes==null) {
                   sColType="No DB Result";
                   iColType=iInvalidColType;
                 } else {
                     sColType=m_dbColumnTypes.get(i-1);
                     iColType = Integer.valueOf(sColType);
                 }
                 
 //             logMessage="Processing column #>" + Integer.toString(i) + "<. expected" +expected+" db" +db + ". Type=>" +sColType +"<.";
 //                log(myName,"debug", myArea,logMessage);

       //both are NULL
                 if (expected_row.get(i) == null) {
                 // expected is null
                   if(database_row.get(i) == null) { // both are null=OK
                   return_row.add("pass");
                   rowEqualValues++;
                   } else {
                     //expected is null, but db is not
                     //check if db is actually empty and then decide this is also ok
                     if(database_row.get(i).isEmpty()) {
                       return_row.add("pass");
                       rowEqualValues++;
                     } else {
                     return_row.add("fail:expected null, got >" + database_row.get(i) + "<");
                     rowUnequalValues++;
                     }
                   }
                 } else {
                   //expected is not null
                    if(database_row.get(i) == null) {
                     //expected is not null and db is null
                       return_row.add("fail:expected >" +expected_row.get(i) +"<, got: null");
                       rowUnequalValues++;
                     } else {
                       //expected is not null and db is not null
                      //if expected = #ANYVALUE# than it does not matter what value is in the db
                      if(sNotNull.equals(expected_row.get(i))) {
                        return_row.add("pass:" +database_row.get(i));
                        rowEqualValues++;
                      } else {
                       if(expected_row.get(i).equals(database_row.get(i))) {
                         //same values
                         return_row.add("pass:" +expected_row.get(i));
                         rowEqualValues++;
                       } else {
                         //special processing for date/time columns
                         if( bDateFormatRequested && (iColType == Types.DATE || iColType == Types.TIME || iColType == Types.TIMESTAMP)) {
                           logMessage="Column is date, time or timestamp. Requested dateformat=>" + m_dateFormat +"<.";
                           log(myName, "debug", myArea, logMessage); 
                          
                           java.util.Date sE = null;
                           sE = new java.util.Date();
                           java.util.Date sD = null;
                           sD = new java.util.Date();
                           String errMsg =null;
                           
                           try {
                           SimpleDateFormat sdfExp = new SimpleDateFormat(m_dateFormat);
                           sE = sdfExp.parse(expected_row.get(i));
                           } catch(ParseException e) {
                             errMsg =e.toString();
                             logMessage="ExpectedValue=>"+ expected_row.get(i) +"<. date format=>" + m_dateFormat +"< - ERROR=>" + e.toString() +"<.";
                             log(myName, Constants.ERROR, myArea, logMessage); 
                             setErrorMessage(logMessage);
                           }

                           try {
                           SimpleDateFormat sdfDb = new SimpleDateFormat(m_dateFormat);
                           sD = sdfDb.parse(database_row.get(i));
                           } catch(ParseException e) {
                             errMsg =e.toString();
                             logMessage="DBValue=>" + database_row.get(i) +"<. date format=>" + m_dateFormat +"< - ERROR=>" + e.toString() +"<.";
                             log(myName, Constants.ERROR, myArea, logMessage);  
                             setErrorMessage(logMessage);
                           }
                           
 //                          if(sE !=null) {
   //                        logMessage="Expected=>" + expected_row.get(i) + "< was converted successfully";
     //                      log(myName, "debug", myArea, logMessage);   
       //                    }
         //                  if(sD !=null) {
           //                  logMessage="Database=>" + database_row.get(i) + "< was converted successfully";
             //                log(myName, "debug", myArea, logMessage);   
               //            }
                           if(sD != null && sE != null && sD.compareTo(sE) ==0) {
                             return_row.add("pass");
                           } else {
                             if(errMsg == null) {
                               return_row.add("fail:expected=>" +expected_row.get(i) + "<, db=>" + database_row.get(i) +"<. ");                              
                             } else {
                           return_row.add("fail:expected=>" +expected_row.get(i) + "<, db=>" + database_row.get(i) +"<. " + errMsg);
                           }
                             }
                         } else {
                         //values are not the same
                         return_row.add("fail:expected=>" +expected_row.get(i) + "<, db=>" + database_row.get(i) +"<.");
                         rowUnequalValues++;
                         }
                       }
                     }   
                     }}
               }       
         myArea="db record read";
                     logMessage="equal: " + rowEqualValues + " unequal: " + rowUnequalValues;
                     log(myName, "info", myArea, logMessage);
                     logMessage="return row >" + return_row +"<.";
                     log(myName, "info", myArea, logMessage);
         
         // remain backward compatible: column 0 contains "column values" in previous version
         if (expected_row.get(0).equals("column values")) {
           return_row.add(0,"Pass");
         } else {
           if(rowUnequalValues >0) {
             return_row.add(0,"Fail:expected: >" + expected_row.get(0) + "< got: >" + rowEqualValues + "<.");
             } else {
               return_row.add(0,"Pass: " +rowEqualValues);
             }         
         }
                     
        addRowToReturnTable (return_row); //return row with outcomes; pass/fail  
                   
     }
           
/*
 * 
 * 
 */
        private List<List<String>> getDatabaseTable (String query){
        String myName="getDatabaseTable";
        String myArea="Initialization";
        String logMessage = Constants.NOT_INITIALIZED;
        
                 //attributes for internal database table 
                          List<List<String>> database_table = new ArrayList<List<String>>();
                                                                           
       rc = Constants.OK;
       
                          //attributes for reading database  
                          Connection m_connection = null;
                          Statement m_statement = null;
                          ResultSet m_resultset = null;
                           
                          try {
                             // Create a connection to the database
                             m_connection = DriverManager.getConnection(url, userId, password);
                             // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
                             m_statement = m_connection.createStatement();
                             // sql query of string type to read database
                             logMessage="Query >" + query +"<.";
                             log(myName, Constants.VERBOSE, myArea, logMessage);

                             m_resultset = m_statement.executeQuery(query);    
                                     

            m_dbColumnTypes = null;
            m_dbColumnTypes = new ArrayList<String>();
                                 //Loop through the results
                             while (m_resultset.next()) {
                                 List<String> databaseRow = new ArrayList<String>(); // initialize list to be reused
//                                 databaseRow.add("column value"); //first cell will consist of a fixed value "column name"
                                 //Add db result row (=multiple field) into fitnesse results array                               
                                 int iColType;
                                 int nrCols=m_resultset.getMetaData().getColumnCount();
                                 logMessage="Number of columns in query =>" +Integer.toString(nrCols) +"<.";
                                      log(myName, Constants.DEBUG, myArea, logMessage);

                                 for (int j = 1; j <= nrCols; ++j)  
                                 {
                                         if (m_resultset.getString(j) == null) {
                                           iColType =-1;
                                                 databaseRow.add(""); //string should be filled with empty spaces otherwise a java null exception is created
                                         }
                                         else {
                                           ResultSetMetaData meta= m_resultset.getMetaData();
                                           iColType =meta.getColumnType(j);
                                                 databaseRow.add(m_resultset.getString(j));
                                         }                                                               
                                   m_dbColumnTypes.add(Integer.toString(iColType));
                                 }       
                                 database_table.add(databaseRow);
                                  }
           myArea="db query completed";
                              addToRowsFound(database_table.size());
                                   logMessage="Number of database rows found >" + getRowsFound() +"<.";
                                   log(myName, Constants.INFO, myArea, logMessage);

          
                         m_statement.close();
                         m_connection.close();               
                                 } 
                          catch (SQLException e) {
                                   myArea="exception handling";
                                   logMessage="SQLException: " + e.toString();
                                   setErrorMessage(logMessage);
                                   log(myName, Constants.ERROR, myArea, logMessage);
                                         return_message = "SQLException : " + e;
           rc = Constants.ERROR;
                                 }
                                 return database_table;
                   }     
 //----------------------------------------------------------
 //Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
 //----------------------------------------------------------
           private void addRowToReturnTable (List <String> row) {
                   return_table.add(row);
           } 
     
     
     
        private String convertText(String text) {
            String myName = "convertText";
            String myArea = "Initialization";
            String logMessage = "start";
            String convFilter = Constants.NOT_INITIALIZED;
            java.util.Date dtConvFilter;
            String rcFilter = Constants.NOT_INITIALIZED;
            int sFoundStartIdent = -1;
            int sFoundStart = -1;
            int sFoundEnd = -1;

            //FITCONVERTDT not in filter
            if (text.indexOf(cFitConvertDt) == -1) {
                //          logMessage="No date conversion required in filter >" +filter+"< as it does not contain >" +cFitConvertDt +"<.";
                //      Logging.LogEntry(myName, "debug", myArea, logMessage);
                return text;
            }

            sFoundStartIdent = text.indexOf(cFitConvertDt);
            sFoundStart =
                    text.indexOf("(", sFoundStartIdent + cFitConvertDt.length());
            if (sFoundStart == -1) {
                logMessage =
                        ">" + cFitConvertDt + "< found, but no brackert found after it in filter >" +
                        text + "<.";
                log(myName, "WARNING", myArea, logMessage);
                return text;
            }
            sFoundEnd = text.indexOf(")", sFoundStart + 1);
            if (sFoundEnd == -1) {
                logMessage =
                        ">" + cFitConvertDt + "< found, and opening brackert found, but no closing bracket found in >" +
                        text + "<.";
                log(myName, "WARNING", myArea, logMessage);
                return text;
            }

            convFilter = text.substring(sFoundStart + 1, sFoundEnd);
            //     convFilter="2000-01-01-01.02.03.123";
            //   m_dateFormat="yyyy-MM-dd-HH.mm.ss.SSS";
            //       logMessage="Trying to convert >" +convFilter +"< to a timestamp using >" +m_dateFormat +"<.";

            //     log(myName, "debug", myArea, logMessage);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(m_dateFormat);
            try {
                dtConvFilter = simpleDateFormat.parse(convFilter);

            }

            catch (ParseException e) {
                myArea = "CaughtParseException";
                logMessage = e.toString();
                log(myName, Constants.ERROR, myArea, logMessage);
                setErrorMessage(logMessage);
                return text;
            }


            // so now from the date, converted from the fitnesse format, to an sql date
            SimpleDateFormat sqlDateFormat = new SimpleDateFormat(sql_dateFormat);
            convFilter = sqlDateFormat.format(dtConvFilter);

            rcFilter =
                    text.substring(0, sFoundStartIdent) + "'" + convFilter + "'" +
                    text.substring(sFoundEnd + 1);

            //        logMessage=">" +cFitConvertDt +"< found, including opening and closing brackets in >" +filter+"<.";
            //        log(myName, "debug", myArea, logMessage);
            logMessage = ">" + text + "< converted to >" + rcFilter + "<.";
            log(myName, "info", myArea, logMessage);

            return rcFilter;
        }


          private void readParameterFile(){        
          String myName="readParameterFile";
          String myArea="reading parameters";
          String logMessage = Constants.NOT_INITIALIZED;

          databaseType = GetParameters.GetDatabaseType(databaseName);
          databaseConnDef = GetParameters.GetDatabaseConnectionDefinition(databaseName);
          driver = GetParameters.GetDatabaseDriver(databaseType);
          url = GetParameters.GetDatabaseURL(databaseConnDef);
          userId = GetParameters.GetDatabaseUserName(databaseName);
          password = GetParameters.GetDatabaseUserPWD(databaseName);

          logMessage="databaseName >" + databaseName +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          logMessage="databaseType >" + databaseType +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          logMessage="connection >" + databaseConnDef +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          logMessage="driver >" + driver +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          logMessage="url >" + url +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          logMessage="userId >" + userId +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
          }

          private void log(String name, String level, String location, String logText) {
                 if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
                     return;
                 }

        Logging.LogEntry(logFileName, name, level, location, logText);
             }

          /**
          * @return
          */
          public String getLogFilename() {
                  return logFileName + ".log";
          }

          /**
          * @param level
          */
          public void setLogLevel(String level) {
          String myName ="setLogLevel";
          String myArea ="determineLevel";
          
          logLevel = Constants.logLevel.indexOf(level.toUpperCase());
          if (logLevel <0) {
             log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
             logLevel =3;
          }
          
          log(myName, Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
          }

          /**
          * @return
          */
          public String getLogLevel() {
          return Constants.logLevel.get(getIntLogLevel());
          }

          /**
          * @return
          */
          public Integer getIntLogLevel() {
          return logLevel;
          }

    /**
     * @param dbname - set the logical databasename of the database that will be queried.
     */
    public void setQueryDatabase(String dbname) {
        queryDatabaseName =dbname;          
          }

    /**
     * @return returns the logical database name of the database being queried.
     */
    public String getQueryDatabase() {
              return queryDatabaseName;          
                }

    /**
     * @param dbname - do not use. Use setQueryDatabase instead.
     */
    public void queryDatabase(String dbname) {
            setQueryDatabase(dbname);  
          }

    /**
     * @param qry - sets the query to be executed.
     */
    public void setQuery(String qry) {
              query =qry;   
              if(hasVar(qry,sVar01)) setNrVarsInQuery(1);
              if(hasVar(qry,sVar02)) setNrVarsInQuery(2);
              
                }

    /**
     * @param qry. Do not use. Use setQuery instead.
     */
    public void query(String qry) {
                  setQuery(qry);  
                }

    /**
     * @param varList - provides the list that can be used to iterate over in a query.
     */
    public void setVariableList(String varList) {
              variableList =varList;          
                }

    /**
     * @return returns the values curently in the list
     */
    public String getVariableList() {
                    return variableList;          
                      }

    /**
     * @param sqlForList - the SQL statement that will generate a list. setVariableList will also be called to store the result.
     * @return returns OK
     */
    public String variableList(String sqlForList) {
              String myName="variableList";
              String varList = Constants.NOT_FOUND;
              String rc = Constants.OK;
              
              GetSingleValue tableList = new GetSingleValue(className +"-"+myName);
              tableList.setDatabaseName(getQueryDatabase());
              tableList.setQuery(sqlForList);
              varList=tableList.getList(Integer.toString(getNrVarsInQuery()));
              
                  setVariableList(varList);  
              
              
              return rc;
                }

    /**
     * @param dbname - Sets the logical database name for the database in which the check results will be stored.
     */
    public void setCheckDatabase(String dbname) {
              checkDatabaseName =dbname;          
                }

    /**
     * @return returns the logical database name of the database that stores the check results.
     */
    public String getCheckDatabase() {
                    return checkDatabaseName;          
                      }

    /**
     * @param dbname. Do not use. Use setCheckDatabase instead.
     */
    public void checkDatabase(String dbname) {
                  setCheckDatabase(dbname);  
                }


    /**
     * @param tblname - The table to be used to store the check results
     */
    public void setCheckTable(String tblname) {
              checkTable =tblname;          
                }

    /**
     * @return returns the table that stores the check results.
     */
    public String getCheckTable() {
                    return checkTable;          
                      }

    /**
     * @param tblname. Do not use. Use setCheckTable instead.
     */
    public void checkTable(String tblname) {
                  setCheckTable(tblname);  
                }
          
          private void setErrorMessage(String msg) {
              errorMessage =msg;
          }
          private String getErrorMessage() {
              return errorMessage;
          }

    /**
     * @return - returns OK or an error depending on the result of storing the check results in the database
     */
    public String load() {
              String myName="load";
              String myArea="init";
              
              List<List<String>> results = new ArrayList<List<String>>();
              String rc = Constants.OK;
              
              log(myName, Constants.VERBOSE, myArea, "query database =>" + getQueryDatabase() +"<.");
              log(myName, Constants.VERBOSE, myArea, "query =>" + getQuery() +"<.");
              log(myName, Constants.VERBOSE, myArea, "variable list =>" + getVariableList() +"<.");
              log(myName, Constants.VERBOSE, myArea, "check database =>" + getCheckDatabase() +"<.");
              log(myName, Constants.VERBOSE, myArea, "check table =>" + getCheckTable() +"<.");
              
              rc=processQueryWithArguments(results);
              if(Constants.OK.equals(rc)) {
//                 log(myName,Constants.VERBOSE, myArea,"result table from query processing =>" + results.toString() +"<.");
                  if(getCheckFunction().equalsIgnoreCase(Constants.HASH)) {
                      List<List<String>>hashedResults =createHash(results);
                      rc=storeResultInDatabase(hashedResults);
                  } else {
                        rc=storeResultInDatabase(results);
                  }
              }

              return rc;
          }

    /**
     * @param expectedNum
     * @return
     */
    public boolean numberOfRecordsLoaded(String expectedNum) {
              if(rowsFound.equals(expectedNum)) {
                  return true;
              } else {
                  return false;
              }
          }

    /**
     * @return
     */
    public String result() {
              return getErrorMessage();
          }

    private String storeResultInDatabase(List<List<String>> resultTable) {
        
          String logMessage = Constants.NOT_INITIALIZED;
          String myName = "storeResultInDatabase";
          String myArea = "Initialization";
          String rc = Constants.OK;
          boolean tableExists=true;

          Connection connection = null;
          Statement statement = null;
          int updateQuery = 0;

        // check whether or not check table exists. if not, create it.
          CreateTable tbl = new CreateTable(className +"-" +myName);
          tbl.setLogLevel(getLogLevel());
          tbl.setDatabaseName(getDatabaseName());
            tbl.setLogLevel(getLogLevel());
            myArea="Check table existence";
          tableExists=  tbl.tableExists(getCheckTable());
          rc=tbl.getErrorCode();
          if(Constants.OK.equals(rc)) {
              if(! tableExists ) {
                  //table not found. create it
                  rc= tbl.createTable(getCheckTable(), Constants.CHECK_TABLE_COLUMNS_ORA);
              }
          } else {
                log(myName, Constants.ERROR, myArea, "Check on existence of table >" + getCheckTable() + "< failed. Error =>" +tbl.getErrorMessage()+"<.");             
            }
          if(!Constants.OK.equals(rc)) {
            setError(tbl.getErrorCode(),tbl.getErrorMessage());
            return Constants.ERROR;
          }

          try {
              myArea="Inserting";
            // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
              setDatabaseName(getCheckDatabase());
              readParameterFile();

                      

            connection = DriverManager.getConnection(url, userId, password);
            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to insert into database.
            String QueryString;
              
              for (int row=0; row < resultTable.size() ; row++) {
                  // first column is current date as string
                  java.util.Date date = new java.util.Date();
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  String formattedDate = sdf.format(date);

                  columnValues="'"+ formattedDate + "'";
                  int c=0;
                  while ((c < resultTable.get(row).size()) && (c < numberOfTableColumns)) {
                      columnValues+=  ",'" + resultTable.get(row).get(c) +"'";   
                      c++;
                  }
                  while( c < numberOfTableColumns){
                      columnValues+=  ",null";     
                      c++;
                  }
                  
               QueryString =
                  "INSERT INTO " + getCheckTable() + " (" + columnNames +
                  ")  VALUES (" + columnValues + ")";
            logMessage = "INSERT query =>" + QueryString + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            updateQuery = statement.executeUpdate(QueryString);

            if (updateQuery != 0) {
                log(myName, Constants.VERBOSE,myArea,"Record inserted.");
            } else {
                rc = Constants.ERROR;
                logMessage="fail: Query failed. return value=>" +
                               Integer.toString(updateQuery) +
                               "<.";
                log(myName, Constants.ERROR,myArea,logMessage); 
                setErrorMessage(logMessage);
                 }
                  
              }
            statement.close();
            connection.close();
          } catch (SQLException e) {
            myArea = "SQL Exception handling";
            logMessage =
                "fail: SQLException: " + e.toString(); // return "fail: SQLException : " + e;
            log(myName, Constants.FATAL, myArea, logMessage);
            setErrorMessage(logMessage);
          }
          return rc;
        }


    private void setDatabaseName(String db) {
        databaseName=db;
    }

    private void setRowsFound(int i) {
        rowsFound =Integer.toString(i);
    }

    /**
     * @return returns the number of rows found.
     */
    public String getRowsFound() {
        return rowsFound;
    }

    private void addToRowsFound(int i) {
        setRowsFound(Integer.parseInt(getRowsFound()) + i);
    }


    /**
     * @param tbl - the check result table name to be truncated.
     */
    public void checkTableToClear(String tbl) {
    setCheckTable(tbl);    
}

    /**
     * @return returns the outcome of the truncate table command (which it will execute on the table set with setTableName).
     */
    public String clearTable() {
    String myName="clearTable";
    String rc = Constants.OK;
    
    TruncateTable truncTbl = new TruncateTable(className + "-" + context + "-" + myName);
    truncTbl.setDatabaseName(getCheckDatabase());
    truncTbl.setTableName(getCheckTable());
    truncTbl.setLogLevel(getLogLevel());
    rc=truncTbl.truncateTable();
    return rc;
}

    /**
     * @param expectedVal - the expected number of records in the check result table
     * @return returns whether or not the expected number matches the actual number of records in the database table.
     */
    public boolean numberOfRecordsInCheckTable(String expectedVal) {
    String myName = "numberOfRecordsInCheckTable";
    
    GetSingleValue getVal = new GetSingleValue(className + "-" + context +"-" + myName);
    getVal.setDatabaseName(getCheckDatabase());
    getVal.setQuery("SELECT count(*) FROM " + getCheckTable());
    rc=getVal.getColumn();
    
    if(rc.equals(expectedVal)) {
        return true;
    } else {
        return false;
    }
}


    private void setNrVarsInQuery(int i) {
        nrVarsInQuery=i;
    }

    private int getNrVarsInQuery() {
        return nrVarsInQuery;
    }

    /**
     * @param checkType. Do not use. Use setCheckFunction instead.
     */
    public void checkFunction (String checkType) {
        setCheckFunction(checkType);
    }

    /**
     * @param checkType 
     */
    public void setCheckFunction(String checkType) {
        this.checkType=checkType;
    }

    /**
     * @return returns the check to be conducted or conducted
     */
    public String getCheckFunction() {
        return this.checkType;
    }

    private List<List<String>> createHash(List<List<String>> results) {
        String myName ="createHash";
        String logMessage = Constants.NOT_INITIALIZED;
        String myArea ="proc";
        
        List<List<String>> hashedResults = new ArrayList<List<String>>();
        
            try {
        MessageDigest m;
        m = MessageDigest.getInstance("MD5");

        for(int i=0 ; i<results.size() ; i++) {
            List<String> row = new ArrayList<String>();
            row.add(results.get(i).get(0)); //table_name
            row.add(results.get(i).get(1)); //method
            row.add(results.get(i).get(2)); //row_id
                m.reset();
                m.update(results.get(i).toString().getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                String hashtext = bigInt.toString(16);
                while(hashtext.length() <32) {
                    hashtext ="0"+hashtext;
                }
            row.add(hashtext);
            hashedResults.add(row);
        }
            } catch (NoSuchAlgorithmException e) {
                logMessage="Wrong MessageDigest Algorithm. Error>" + e.toString() + "<.";
                log(myName, Constants.FATAL, myArea, logMessage);
            }
            
        
        return hashedResults;
    }

    private void setError(String errorCode, String errorMessage) {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
