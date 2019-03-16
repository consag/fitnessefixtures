/**
 * The purpose of this fixture is to compare the expected outcome of a database query with the actual outcome using the FitNesse slim 'table' table.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac. Beekers
 * @version 20161111.0 #- Added some comments for java docs
 * @since   10 May 2015
 */ 
package nl.consag.testautomation.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;

import java.sql.*;
import java.text.*;
import java.util.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.linux.CheckFile;

public class BasicQuery {
	private String className = "BasicQuery";
    private static String version ="20180619.1";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;
	private String query;
	private int rowUnequalValues=0;
	private int rowEqualValues=0;
	private int NO_FITNESSE_ROWS_TO_SKIP = 3;
        private int nrRecordsFound=0;

	private int numberOfTableColumns;
	private String concatenatedColumnNames; // variables used to create select query

	private List<List<String>> returnTable = new ArrayList<List<String>>(); //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")
	  
	private String returnMessage = ""; //text message that is returned to FitNesse
        private String result = Constants.OK;
        private String resultMessage = Constants.NOERRORS;
        private String errorMessage = Constants.NOERRORS;
        
        //20151121
        private String sqlFileName = Constants.NOT_PROVIDED;

	public BasicQuery() {
		//Constructors
	      	java.util.Date started = new java.util.Date();
	      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	      	startDate = sdf.format(started);
	      	this.context=className;
	        logFileName = startDate + "." + className;

	    }

    /**
     * @param context
     */
    public BasicQuery(String context) {
	    	java.util.Date started = new java.util.Date();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	startDate = sdf.format(started);
	    	this.context=context;
	        logFileName = startDate + "." + className +"." + context;

	    }

    /**
     * @param context
     * @param logLevel
     */
    public BasicQuery(String context, String logLevel) {
                java.util.Date started = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                startDate = sdf.format(started);
                this.context=context;
                logFileName = startDate + "." + className +"." + context;
                setLogLevel(logLevel);
                this.logLevel = getIntLogLevel();
            }

    /**
     * @param inputTable
     * @return FitNesse table with results
     */
    public List doTable(List<List<String>> inputTable) { 
		//Main function; checks input table and populates output table		  											 
			String logMessage = Constants.NOT_INITIALIZED;
		    String myName="doTable";
		    String myArea="Initialization";
		    
			logMessage="number of rows in select FitNesse table: " + Integer.toString(inputTable.size()); 
		    log(myName, Constants.DEBUG, myArea, logMessage);
			numberOfTableColumns = inputTable.get(2).size();
			logMessage="number of columns in FitNesse table: " + Integer.toString(numberOfTableColumns);
		    log(myName, Constants.DEBUG, myArea, logMessage);      

		    retrieveDatabaseName (inputTable.get(0)); //read first row in FitNesse table
 			readParameterFile();
			getQuery (inputTable.get(1)); 		 //read second row in FitNesse table
			getColumnNames (inputTable.get(2));  //read third row in FitNesse table

	 		CompareExpectedTableWithDatabaseTable(inputTable, getDatabaseTable(true));  
	
		    List<String> addRow = new ArrayList<String>();
		    addRow.add("report:log file");
		    addRow.add("report:"+getLogFilename());
		    addRowToReturnTable (addRow);	
			return returnTable;
		  }

    /**
     * @return How many column values in row are correct
     */
    public String columnsOk() {
	    // returns rowEqualValues
	      	returnMessage =Integer.toString(rowEqualValues);  
	      	return returnMessage;
	    }

    /**
     * @return How many column values are incorrect
     */
    public String columnsNotOk() {
	    // returns rowUnequalValues
	      	returnMessage =Integer.toString(rowUnequalValues);  
	      	return returnMessage;
	    }

    /**
     * @param inputRow
     */
    private void retrieveDatabaseName (List<String> inputRow){
		//Function to read first row of table and set database name
			String myName="retreiveDatabaseName";
			String myArea="Initialization";
			String logMessage = Constants.NOT_INITIALIZED;
      
			List<String> return_row = new ArrayList<String>();
			setDatabaseName(inputRow.get(1)); //read first row second column
			logMessage="database name: " + getDatabaseName();
			log(myName, Constants.VERBOSE, myArea, logMessage);      

			addRowToReturnTable (return_row);
	  }

    /**
     * @param inputRow
     */
    private void getQuery (List<String> inputRow){
		//Function to read second row of table and set database table name
	    	String myName="getQuery";
	    	String myArea="Initialization";
	    	String logMessage = Constants.NOT_INITIALIZED;
	    	List<String> return_row = new ArrayList<String>();
      
	    	query = inputRow.get(1); //read first row second column
	    	logMessage="Query: " + query;
	    	log(myName, Constants.DEBUG, myArea, logMessage);      

	    	addRowToReturnTable (return_row);
	  }

    /**
     * @param inputRow
     */
    private void getColumnNames (List<String> inputRow){	 
		//Function to read third row of table and set column names
	    	String myName="getColumnNames";
	    	String myArea="Initialization";
	    	String logMessage = Constants.NOT_INITIALIZED;
      
	    	List<String> return_row = new ArrayList<String>();	 
	  
	    	for (int i = 1; i < inputRow.size(); ++i)  
	    	{	// next column names
	    		if (i==1){
	    			concatenatedColumnNames = inputRow.get(i); //first column name
	    		}
	    		else{
	    			concatenatedColumnNames = concatenatedColumnNames + "," + inputRow.get(i);
	    		}
	    	} 		
	    	myArea="concatenated";
	    	logMessage="column names: " + concatenatedColumnNames;
	    	log(myName, Constants.DEBUG, myArea, logMessage);      
	    	
	    	addRowToReturnTable (return_row); //return row with outcomes; pass/fail
	  }  
	
	private void CompareExpectedTableWithDatabaseTable(List<List<String>> expected_table, List<List<String>> databaseTable){  
		//Function to compare input table with database table
			String myName="CompareExpectedTableWithDatabaseTable";
			String myArea="Start";
			String logMessage = Constants.NOT_INITIALIZED;
      
			List<String> empty_row = new ArrayList<String>();	 
			for (int i = 0; i < numberOfTableColumns; ++i)  
			{	// fill empty row with spaces 
				empty_row.add("no record");
			}
			logMessage="expected #rows=>" +  Integer.toString(expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP) +"<. Database #rows found=>" + Integer.toString(databaseTable.size()) + "<.";
			log(myName, Constants.VERBOSE, myArea,logMessage);
      
			myArea="Comparing input with db";
			if((expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP) >= databaseTable.size()){
				logMessage="more or equal number of rows expected compared to database rows.";
				log(myName, Constants.DEBUG, myArea, logMessage);
				myArea="Processing expectedrows>=dbrows";
				for (int i = 0; i < (expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP); ++i)  
				{	// less rows in database than expected
					if (i < databaseTable.size()){
						CompareExpectedRowWithDatabaseRow(expected_table.get(i + NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
					}
					else{
						CompareExpectedRowWithDatabaseRow(expected_table.get(i + NO_FITNESSE_ROWS_TO_SKIP), empty_row);
					}
				}
			}
			else{
				logMessage="more number of rows in db than expected.";
				log(myName,"debug", myArea, logMessage);
				myArea="Processing dbrows>expectedrows";
				for (int i = 0; i < databaseTable.size(); ++i)  
				{	// more rows in database than expected
					if (i < (expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP)){
						logMessage="Processing expected row# >" + Integer.toString(i) +"<.";
						log(myName, Constants.DEBUG, myArea,logMessage);
						CompareExpectedRowWithDatabaseRow(expected_table.get( i + NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
					}
					else{
						logMessage="Processing db surplus row# >" + Integer.toString(i) +"<.";
						log(myName, Constants.DEBUG, myArea,logMessage);
						CompareExpectedRowWithDatabaseRow(empty_row, databaseTable.get(i));
					}
			  }
		  }
	  }

	private void CompareExpectedRowWithDatabaseRow(List<String> expected_row, List<String> database_row){ 
		//Function to compare input row with database row
			List<String> return_row = new ArrayList<String>();	
	        String myName="CompareExpectedRowWithDatabaseRow";
	        String myArea="Initialization";
	        String logMessage = Constants.NOT_INITIALIZED;	
	        rowEqualValues=0;
	        rowUnequalValues=0;
	    
	        for (int i = 1; i < numberOfTableColumns; ++i) {                     
	            //Compare cell for cell if expected equals outcome
	              if ((expected_row.get(i) != null &&    // JVA KLANTEB 2185 Check for NULL or empty string 
	                  (database_row.get(i) != null) || !database_row.get(i).isEmpty())) {
	                  if (expected_row.get(i).equals(database_row.get(i))){
	                      return_row.add("pass"); //return "pass" in next cell if expected = result
	                      rowEqualValues++;
	                  }
	                  else {
	                      return_row.add("fail:expected: >" + expected_row.get(i) + "< found: >" + database_row.get(i) +"<."); //return "fail" in next cell with the found value  
	                      rowUnequalValues++;
	                  }
	              }
	              else {
	                  if ((expected_row.get(i) == null &&    // JVA KLANTEB 2185 Check for NULL or empty string
	                      (database_row.get(i) == null) || database_row.get(i).isEmpty())) {                                            
	                      return_row.add("pass"); //return "pass" in next cell if expected = result
	                      rowEqualValues++;   
	                  }
	                  else {
	                      return_row.add("fail:expected: >" + expected_row.get(i) + "< found: >" + database_row.get(i) + "<."); //return "fail" in next cell with the found value  
	                      rowUnequalValues++;                    
	                  }                                                                  
	              }
	          }
	         
	        myArea="db record read";
			logMessage="equal: " + rowEqualValues + " unequal: " + rowUnequalValues;
		    log(myName, Constants.INFO, myArea, logMessage);
		    logMessage="return row >" + return_row +"<.";
		    log(myName, Constants.VERBOSE, myArea, logMessage);
	    
	        if (expected_row.get(0).equals("column values")) {  // remain backward compatible: column 0 contains "column values" in previous version
	        	return_row.add(0,"Pass");
	        } 
	        else {
	        	if(rowUnequalValues >0) {
		            return_row.add(0,"Fail:expected: >" + expected_row.get(0) + "< got: >" + rowEqualValues + "<.");
		            } 
	        	else {
		              return_row.add(0,"Pass: " +rowEqualValues);
		         }         
	        }
	 		    
	       addRowToReturnTable (return_row); //return row with outcomes; pass/fail  
    }

    private List<List<String>> getDatabaseTable (boolean collectResultRows){
	       String myName="getDatabaseTable";
	       String myArea="Initialization";
	       String logMessage = Constants.NOT_INITIALIZED;
	       List<List<String>> databaseTable = new ArrayList<List<String>>();
			 			  			  
	       Connection connection = null;
	       Statement statement = null;
	       ResultSet resultset = null;
			  
	       try {
               myArea="readParameterFile";
               readParameterFile();
               log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName +"<.");
               connectionProperties.setLogFileName(logFileName);
               connectionProperties.setLogLevel(getIntLogLevel());
               connection = connectionProperties.getUserConnection();

               // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
//			    Class.forName(driver);
		  // Create a connection to the database
//		    connection = DriverManager.getConnection(url, userId, password);
		    // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
		    statement = connection.createStatement();
		    // sql query of string type to read database
		    logMessage="Query >" + query +"<.";
		    log(myName, Constants.DEBUG, myArea, logMessage);
		    resultset = statement.executeQuery(query);    
			    
                    //Loop through the results
                    while (resultset.next()) {
                        if(collectResultRows) {
			    	List<String> database_row = new ArrayList<String>(); // initialize list to be reused
			    	database_row.add("column value"); //first cell will consist of a fixed value "column name"
			    	//Add db result row (=multiple field) into fitnesse results array			    	
			    	for (int j = 1; j < numberOfTableColumns; ++j)  
			    	{
			    		if (resultset.getString(j) == null) {
			    			database_row.add(""); //string should be filled with empty spaces otherwise a java null exception is created
			    		}
			    		else {
			    			database_row.add(resultset.getString(j));
			    		}   						    		
			    	}     	
			    	databaseTable.add(database_row);
                                nrRecordsFound++;
			 }
                        else {
                            nrRecordsFound++;
                        }
                     }
		    myArea="db query completed";
		    logMessage="Number of database rows found: " + Integer.toString(databaseTable.size());
		    log(myName, Constants.INFO, myArea, logMessage);
		 	
		    statement.close();
		    connection.close();	
                   setResult(Constants.OK,logMessage);
		} 
		catch (SQLException e) {
			myArea="exception handling";
			logMessage="SQLException: " + e.toString();
			log(myName, Constants.ERROR, myArea, logMessage);
                        setError(Constants.ERROR,logMessage);
			returnMessage = "SQLException : " + e;
		}
	       
           if(collectResultRows) {
                return databaseTable;
           } else {
                return null;
           }
        }     

	private void addRowToReturnTable (List <String> row) {
		//Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
			returnTable.add(row);
	  } 

	private void readParameterFile(){
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +databaseName +"<.");
        connectionProperties.refreshConnectionProperties(databaseName);

	}

    private void log(String name, String level, String area, String logMessage) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }

        if (firstTime) {
            firstTime = false;
            if (context.equals(Constants.DEFAULT)) {
                logFileName = startDate + "." + className;
            } else {
                logFileName = startDate + "." + context;
            }
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(logFileName, name, level, area, logMessage);
    }

    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
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
     * @return Log level as String: FATAL,ERROR,WARNING,INFO,DEBUG,VERBOSE
     */
    public String getLogLevel() {
       return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return Log level as integer
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }
    
    /* 
     * @since 20151121.0
     *
    */
    public static String getVersion() {
        return version;
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
    public void setDatabaseName(String dbname) {
        this.databaseName = dbname;
    }
    
    public void setSqlFile(String logicalDirAndFilename) {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName="setSqlFile";
        String myArea="init";
        
        String dirAndFile = logicalDirAndFilename;
        String[] dirFileArray =dirAndFile.split("/",2);
        if(dirFileArray.length != 2) {
            logMessage="Invalid directory/filename >" + logicalDirAndFilename +"<. Must be able to split in two parts, separated by at least one /";
            log(myName, Constants.ERROR, myArea, logMessage);
           return;   
        }
        String dir =dirFileArray[0];
        String fileName =dirFileArray[1];
        
        dirFileArray =dirAndFile.split(" ",2);
        
        if(dirFileArray.length != 2) {
            logMessage="Invalid directory/filename >" + logicalDirAndFilename +"<. Must be able to split in two parts, separated by one space";
            log(myName, Constants.ERROR, myArea, logMessage);
           return;   
        }
        
        CheckFile checkSqlFile = new CheckFile(context, getLogLevel());
        sqlFileName =checkSqlFile.DetermineCompleteFileName(dir, fileName);
        
    }
    public String getSqlFile() {
        return sqlFileName;
    }
    
    public String queryExecution() {
        String logMessage="init";
        String myName="queryExecution";
        String myArea="processing";
        log(myName, Constants.DEBUG, myArea, logMessage);
        
        String sqlStatement = Constants.NOT_INITIALIZED;
        
        log(myName, Constants.DEBUG, myArea, "reading parameter file...");
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Done.");

        log(myName, Constants.DEBUG, myArea, "determining sql statement...");
        sqlStatement =determineSQLStatement();
        log(myName, Constants.DEBUG, myArea, "Done.");

        setQuery(sqlStatement);

        log(myName, Constants.DEBUG, myArea, "Getting database result...");
        getDatabaseTable(false);
        log(myName, Constants.DEBUG, myArea, "Done");
        log(myName, Constants.DEBUG, myArea, "Database returned >" +nrRecordsFound +"< records.");

        log(myName, Constants.DEBUG, myArea, "Process resulted in >" + getErrorMessage() +"<.");

        if(Constants.NOERRORS.equals(getErrorMessage())) {
            return Constants.OK;
        } else {
            return Constants.ERROR;
        }
        
        
    }
        
    private String determineSQLStatement() {
            
        String logMessage = Constants.NOT_INITIALIZED;
        String myName="determineSQLStatement";
        String myArea="init";
        String theLine = Constants.NOT_INITIALIZED;
        String sqlStatement="/* BasicQuery - queryExecution */ ";
        
        try {
            BufferedReader ins = new BufferedReader(new FileReader(getSqlFile()));
            int rowCount=0;
            myArea="Reading file";
            try {
                while ((theLine = ins.readLine()) != null) {
                      ++rowCount;
                      logMessage="processing line >" + rowCount + "<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    sqlStatement =sqlStatement.concat(theLine);
                    sqlStatement =sqlStatement.concat(" ");
                }
            } catch (IOException e) {
                logMessage="Error reading from file >" + getSqlFile() +"<. Error =>" + e.toString() +"<.";
                log(myName, Constants.ERROR, myArea, logMessage);
                setError(Constants.ERROR, logMessage);
                setResult(Constants.ERROR);
                try {
                    ins.close();
                } catch (IOException f) {
                    logMessage="Error closing file >" + getSqlFile() +"<. Error =>" + f.toString() +"<.";
                    log(myName, Constants.ERROR, myArea, logMessage);
                }
                return Constants.ERROR;
            }
            logMessage="SQL statement determined as >" + sqlStatement +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            sqlStatement =sqlStatement.trim();
            if(sqlStatement.endsWith(Constants.STATEMENT_DELIMITER)) {
                sqlStatement =sqlStatement.substring(0, sqlStatement.length()-1);
            }
            logMessage="SQL statement after removal of separator >" + Constants.STATEMENT_DELIMITER +"< is >" + sqlStatement +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            
            setResult(Constants.OK);
            ins.close();
        } catch (FileNotFoundException e) {
            logMessage="Could not open file >" + getSqlFile() +"< for reading. Error =>" +e.toString() +"<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            setError(Constants.ERROR, logMessage);
            setResult(Constants.ERROR);
            return Constants.ERROR;
        } catch (IOException e) {
            logMessage="Error closing file >" + getSqlFile() +"<. Error =>" + e.toString() +"<.";
            log(myName, Constants.ERROR, myArea, logMessage);
        }

        return sqlStatement;
    }
        
    public String rowsReturned() {
        return Integer.toString(nrRecordsFound);
    }
    
    public String getResult() {
        return result;
    }
    private void setResult(String result) {
        this.result =result;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    private void setErrorMessage(String error) {
        this.errorMessage=error;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    private void setResultMessage(String result) {
        this.resultMessage =result;
    }
    private void setError(String errCode, String errMsg) {
        setResult(errCode);
        setErrorMessage(errMsg);
    }
    private void setResult(String code, String msg) {
        setResult(code);
        setResultMessage(msg);
    }
    public void setQuery(String sqlStatement) {
        query=sqlStatement;
    }

}