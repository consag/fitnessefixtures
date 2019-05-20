/*
 * Compare Table data to file
 * @author Jac. Beekers
 * @sersion 0.2
 * @since July 2015
 * 
 */

package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Diff;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import nl.jacbeekers.testautomation.fitnesse.linux.FileOperation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.*;

import java.text.*;

import java.util.*;

import java.text.ParseException;

import org.apache.commons.io.FileUtils;


public class CompareTableDataToFile {

    private String className = "CompareTableDataToFile";
    private static String version = "20180619.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;

    private boolean verbose=false;


// File Handling
            DataOutputStream streamOut1;
  String fileOut1 = Constants.NOT_INITIALIZED;
  String tmpDir = Constants.NOT_INITIALIZED;
  
  private boolean compareDone =false;
    //
  private String nrOfDifferences = Constants.NOT_INITIALIZED;
  private String nrDbRecords = Constants.NOT_INITIALIZED;
  private String nrSrcDbRecords = Constants.NOT_INITIALIZED;
  private String nrTgtFileRecords = Constants.NOT_INITIALIZED;
    private int colsResultSetDummy = 9999;
    // for deliveries to DWHD user can use "LATEST" to apply a default filter sLatestDeliveryFilter
    private String latest = Constants.LATEST;
    private String latestDeliveryFilter =
        "delivery_sqn=(select max(delivery_sqn) from ";
    
    private String siebelPK="ROW_ID";
    private String dacPK="ROW_WID";
    private String SiebelIntId="INTEGRATION_ID";
    private String uid = Constants.NOT_INITIALIZED;
    private int insertedRows=0;
    
    //columns to be excluded from comparison.
    //at the moment, 22-07-2013 these are DELIVERY_SQN and LOAD_DTS, cols in the dwhd landing zone
    private ArrayList<String> listExcludeCols = new ArrayList<String>();
    private ArrayList<String> listMapCols = new ArrayList<String>();
    private ArrayList<String> listMappedToCols = new ArrayList<String>();
    //private List<int> ListExcludedColIds = new List<int>();
    private ArrayList listExcludedColIds = new ArrayList();
    private ArrayList<Integer> listMappedColIds = new ArrayList<Integer>();

    private String srcTable = Constants.NOT_INITIALIZED;
    private String srcFilter = Constants.NOT_INITIALIZED;
    private String srcSQL = Constants.NOT_PROVIDED;
    private boolean useSQL =false;              // will be set to true only if explicite setSQL is used on test page
    private String tgtDirectory = Constants.NOT_INITIALIZED;
    private String tgtFileName = Constants.NOT_INITIALIZED;
    private String tgtFilter = Constants.NOT_INITIALIZED;
    
    private String delimiter = Constants.QUERY_DELIMITER;

    private List<String> queryColumnNames = new ArrayList<String>();
    private List<String> srcQueryColumnNames = new ArrayList<String>();

    private String srcDatabaseName = Constants.NOT_INITIALIZED;
    private String srcDatabaseType = Constants.NOT_INITIALIZED;

    private String dbConnection = Constants.NOT_INITIALIZED;
    private String tablefilter;
    private String check = "unknown check";

    private String columnName;
    private String value;
    private String rowFilter;
    private String allTables;
    private String dacRepository;
    private String dateFormat = "YYYYMMDD";

    private String sqlDateFormat = "yyyy-MM-dd-HH.mm.ss";

    private FileOperation fileOperation;
  
    //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")
    private List<List<String>> return_table;

    private String returnMessage =
        ""; //text message that is returned to fitnesse


    public CompareTableDataToFile() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context=className;
        logFileName = startDate + "." + className +"." + context;

    }
  public CompareTableDataToFile(String context) {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      this.context=context;
      logFileName = startDate + "." + className +"." + context;

  }

    // set the check conducted. e.g. number of recrods or same percentage

    public void setCheck(String check) {
        this.check = check;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setSourceDatabaseName(String databaseName) {
        this.srcDatabaseName = databaseName;
        srcDatabaseType = GetParameters.GetDatabaseType(databaseName);

    }


    public void setDacRepository(String dacrep) {
        this.dacRepository = dacrep;
    }

    public void setTableFilter(String tableFilter) {
        this.tablefilter = tableFilter;
    }

    public void setColumn(String column) {
        this.columnName = column;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setRowFilter(String filter) {
        this.rowFilter = filter;
    }

    public void setListAllTables(String alltables) {
        this.allTables = alltables;
    }


    public void setDateFormat(String dateFormat) {
        dateFormat = dateFormat;

    }

    // on a page, you can specify a DB table filter using the word FITCONVERTDT
    // this has to be translated into a string to datetime conversion SQL that has to run on Oracle, DB2 and SQLServer

    private String convertFilter(String filter) {
        String myName = "CheckNumberOfRecords.convertFilter";
        String myArea = "Initialization";
        String logMessage = "start";
        String convFilter = Constants.NOT_INITIALIZED;
        java.util.Date dtConvFilter;
        String rcFilter = Constants.NOT_INITIALIZED;
        int sFoundStartIdent = -1;
        int sFoundStart = -1;
        int sFoundEnd = -1;

        //FITCONVERTDT not in filter
        if (filter.indexOf(Constants.FITCONVERTDT) == -1) {
            //          logMessage="No date conversion required in filter >" +filter+"< as it does not contain >" +cFitConvertDt +"<.";
            //      Logging.LogEntry(myName, Constants.DEBUG, myArea, logMessage);
            return filter;
        }

        sFoundStartIdent = filter.indexOf(Constants.FITCONVERTDT);
        sFoundStart =
                filter.indexOf("(", sFoundStartIdent + Constants.FITCONVERTDT.length());
        if (sFoundStart == -1) {
            logMessage =
                    ">" + Constants.FITCONVERTDT + "< found, but no brackert found after it in filter >" +
                    filter + "<.";
            log(myName, "WARNING", myArea, logMessage);
            return filter;
        }
        sFoundEnd = filter.indexOf(")", sFoundStart + 1);
        if (sFoundEnd == -1) {
            logMessage =
                    ">" + Constants.FITCONVERTDT + "< found, and opening brackert found, but no closing bracket found in >" +
                    filter + "<.";
            log(myName, "WARNING", myArea, logMessage);
            return filter;
        }

        convFilter = filter.substring(sFoundStart + 1, sFoundEnd);
        //     convFilter="2000-01-01-01.02.03.123";
        //   m_dateFormat="yyyy-MM-dd-HH.mm.ss.SSS";
        //       logMessage="Trying to convert >" +convFilter +"< to a timestamp using >" +m_dateFormat +"<.";

        //     log(myName, Constants.DEBUG, myArea, logMessage);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            dtConvFilter = simpleDateFormat.parse(convFilter);

        }

        catch (ParseException e) {
            myArea = "CaughtParseException";
            logMessage = e.toString();
            log(myName, "ERROR", myArea, logMessage);
            return filter;
        }


        // so now from the date, converted from the fitnesse format, to an sql date
        SimpleDateFormat sqlDate = new SimpleDateFormat(sqlDateFormat);
        convFilter = sqlDate.format(dtConvFilter);

        rcFilter =
                filter.substring(0, sFoundStartIdent) + "'" + convFilter + "'" +
                filter.substring(sFoundEnd + 1);

        //        logMessage=">" +cFitConvertDt +"< found, including opening and closing brackets in >" +filter+"<.";
        //        log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage = ">" + filter + "< converted to >" + rcFilter + "<.";
        log(myName, "info", myArea, logMessage);

        return rcFilter;
    }
    //
    //
    //source number of records?

    public String sourceCheckSumOracle() {
        String myName = "sourceCheckSumOracle";
        String myArea = "Initialization";
        String logMessage = "start";
        String sQuery = "Undetermined";
        int numCols = 0;

        List<List<String>> dbTable = new ArrayList<List<String>>();

        //		      log(myName, "info", myArea, logMessage);

        //Get parameters from file
        if (Constants.NOT_INITIALIZED.equals(srcDatabaseName)) {
            dbConnection = databaseName;
        } else {
            dbConnection = srcDatabaseName;
        }
        readParameterFile();
        // query to determine tables

        if (Constants.NOT_INITIALIZED.equals(srcFilter)) {
            sQuery =
                    "select hash_allcols('" + srcTable + "','1=1') from dual";
            logMessage = "srcFilter is empty";
            log(myName, "info", myArea, logMessage);
        } else {
            // FITCONVERTDT must be translated in valid SQL filter. May occur multiple times
            String tmpFilter = srcFilter;
            int i = 0;
            while ((tmpFilter.indexOf(Constants.FITCONVERTDT) != -1) && (i < 10)) {
                i++;
                logMessage =
                        "Found >" + Constants.FITCONVERTDT + "< in >" + tmpFilter + "<. Occurrence: >" +
                        Integer.toString(i) + "<.";
                log(myName, Constants.DEBUG, myArea, logMessage);
                tmpFilter = convertFilter(tmpFilter);
            }
            sQuery =
                    "select hash_allcols('" + srcTable + "','" + tmpFilter + "') from dual";

        }

        logMessage = "Oracle Source table query =>" + sQuery + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        //Get the #records
        numCols = 1;
        dbTable = executeQuery(sQuery, numCols);
        if (Constants.OK.equals(returnMessage)) {
            nrSrcDbRecords = nrDbRecords;
            return dbTable.get(0).get(0);
        } else {
            return returnMessage;
        }

    }

    private void setupMapping() {
      
      listExcludeCols.add("DELIVERY_SQN");
      listExcludeCols.add("LOAD_DTS");
      // the next columns might not always exist in target or might have a different name in source
      listExcludeCols.add("DATASOURCE_NUM_ID"); // does not exist in source
      listExcludeCols.add("DELETE_FLG"); // does not exist in source

      
      listMappedToCols.add("ROW_ID");
      listMapCols.add("INTEGRATION_ID"); // if exists, the source will have ROW_ID
      listMappedToCols.add("INTEGRATION_ID");
      listMapCols.add("ROW_ID"); // if exists, the source will have ROW_ID
      listMappedToCols.add("INTEGRATION_ID");
      listMapCols.add("X_SBL_INTEGRATION_ID"); // if exists, the source will have ROW_ID
      

    }

// Compare data, using checksum in two tables
    //
    public String sameData() {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "sameData";
        String myArea = "initialization";
        String sResult = Constants.OK;
        int insertedSourceValues =0;

        setupMapping();     
        // indicate that file comparison has not been done
        compareDone=false;
        
            myArea = "Processing source data";
        dbConnection = srcDatabaseName;

             readParameterFile();
              fileOut1=tmpDir+"/"+srcDatabaseName+"_1.tmp";
              if(createFiles().equals(Constants.ERROR)) {
                  closeFiles();
                  return Constants.ERROR;
              }
              
            //TODO: not Oracle. Need to read ourselves and hash/compare as DB2 v9.5 does not have a hash function :(
            //- get source data - hash
            //- get target data - hash
            //- compare hashes
            // String sSrcTableDef;
            // String sTgtTableDef;
            String sQuery = Constants.NOT_INITIALIZED;
            String srcConcatenatedCols;
          int colLocationInSrc=-1;
 
        if(useSQL) 
            sQuery=getSourceSql();
        else
            sQuery = "SELECT * FROM " + srcTable + " WHERE 1=2";

            srcQueryColumnNames = determineQueryColumns(sQuery);

          myArea="source processing";
//Find if known PK is in the query column list     
          
        colLocationInSrc =  srcQueryColumnNames.indexOf(uid);
        if (colLocationInSrc == -1) 
          colLocationInSrc =  srcQueryColumnNames.indexOf(siebelPK);
          if (colLocationInSrc == -1) 
           colLocationInSrc = srcQueryColumnNames.indexOf(dacPK);
          if (colLocationInSrc == -1) 
            colLocationInSrc = srcQueryColumnNames.indexOf(SiebelIntId);
            if (colLocationInSrc == -1 && (! useSQL)) {
              logMessage="Could not find a primary key in source. Tried >" +siebelPK +"<, >" +dacPK+"<, >" + SiebelIntId +"<.";
                        log(myName, Constants.ERROR, myArea, logMessage);
              return Constants.NOPRIMKEY;
            }


        String sSrcQuery = Constants.NOT_INITIALIZED;
            
              srcConcatenatedCols = concatenateCols(srcQueryColumnNames, Constants.COLUMN_DELIMITER, Constants.SRC);
            
            if(srcConcatenatedCols.equals(Constants.ERROR)) {
              logMessage="Processing aborted due to error in concatenation of source columns.";
                        log(myName, Constants.ERROR, myArea, logMessage);
              return Constants.ERROR;
            }
            if(useSQL) {
                sSrcQuery =getSourceSql();
            } else
            {  
              if (Constants.NOT_INITIALIZED.equals(srcFilter) ) {
                sSrcQuery="SELECT " +srcQueryColumnNames.get(colLocationInSrc) + " pk, " + srcConcatenatedCols + " from " + srcTable;
              } else {
                if (srcFilter.equals(latest)) {
                  sSrcQuery="SELECT " +srcQueryColumnNames.get(colLocationInSrc) + " pk, " + srcConcatenatedCols + " from " + srcTable 
                            + " WHERE " + latestDeliveryFilter + srcTable + ")";
                } else {
                  sSrcQuery="SELECT " +srcQueryColumnNames.get(colLocationInSrc) + " pk, " + srcConcatenatedCols + " from " + srcTable
                            + " WHERE " + convertFilter(srcFilter);
                }              
               }
                sSrcQuery = sSrcQuery + " ORDER BY pk ";
              }
            if(Constants.DATABASETYPE_DB2.equals(srcDatabaseType)) {
                  sSrcQuery = sSrcQuery + " with ur for fetch only";
                }  

            logMessage="Source Query =>" + sSrcQuery + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            
              sResult= insertValuesTempFile(1, srcDatabaseName,sSrcQuery);
              logMessage="Inserting source values in temp file returned >" + sResult + "<.";
                      log(myName, Constants.DEBUG, myArea, logMessage);
                    if(sResult.equals(Constants.ERROR) || sResult.equals(Constants.STOP)) {
                      return Constants.ERROR;
                    }
                    nrSrcDbRecords=sResult;
                   insertedSourceValues =insertedRows;
                    logMessage="Inserted >" + Integer.toString(insertedSourceValues) + "< source value(s)";
                    log(myName, Constants.DEBUG, myArea, logMessage);
//
              closeFiles();

              myArea="comparing src-tgt";
            
// Compare the two files       
            compareDone=true;
            sResult =compareSourceToTarget(fileOut1,getFileName());
        logMessage="compareSourceToTarget for file >" +fileOut1 +"< and file >" + getFileName() +"< returned >" + sResult + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
            if(sResult.equals(Constants.NO)) {
              setNrOfDifferences (determineNrOfDifferences());
            } else {
                if(sResult.equals(Constants.YES)) {
                    setNrOfDifferences("0");
                } else {
              setNrOfDifferences(Constants.UNKNOWN);
                }
            }
        
        logMessage="nrOfDifferences has been set to >" + nrOfDifferences + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        
        return sResult;
    }

    public String getDifferences() {
        String myName="getDifferences";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
        
        String diffAsString = Constants.OK;
        
        if(getNrOfDifferences().equals("0"))
            return Constants.NO_DIFFERENCES;

        List<String> diffs = new ArrayList<String>();
            try {
                Diff.readersAsText(fileOut1, getFileName(), diffs);
                StringBuilder diffStringB = new StringBuilder();
                for (String s : diffs) {
                    diffStringB.append(diffs);
                    diffStringB.append("\n");
                }
                diffAsString = diffStringB.toString();
            } catch (IOException e) {
                logMessage=e.toString();
                log(myName, Constants.DEBUG, myArea, logMessage);  
                diffAsString="I/O Error during comparison: " + logMessage;
            }

        return diffAsString;
        }
    
    private void setNrOfDifferences(String nr) {
        this.nrOfDifferences=nr;
    }
    public String getNrOfDifferences() {
        return this.nrOfDifferences;
    }
    
    public String getOutputFile() {
        if (compareDone) {
      return fileOperation.getOutputFile();
        } else
            return Constants.NOT_COMPARED;
    }
    
    public String getErrorFile() {
        if (compareDone) {
      return fileOperation.getErrorFile();
        } else {
          return Constants.NOT_COMPARED;
        }
    }
    
    private String determineNrOfDifferences() {
      String myName="determineNrOfDifferences";
      String myArea="init";
      String logMessage = Constants.NOT_INITIALIZED;
      String sResult = Constants.NOT_INITIALIZED;
      
    // String cmdOutput=fileOperation.getOutputFile();
     
     
     sResult = Constants.NOT_IMPLEMENTED;
      return sResult;
      
    }

    private String compareSourceToTarget(String fileName1, String fileName2) {
    //  
    String myName="compareSourceToTarget";
    String logMessage = Constants.NOT_INITIALIZED;
    String myArea="init";
        boolean bResult= false;
        String rc = Constants.NO;
    
    File f1 = new File(fileName1);
    File f2 = new File(fileName2);
    
    if(!f1.exists()) {
        logMessage="file >" +fileName1 +"< (first file) does not exist.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return Constants.ERROR;
    }
    if(!f2.exists()) {
        logMessage="file >" +fileName2 +"< (second file) does not exist.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return Constants.ERROR;        
    }
        
    myArea="compare";
        try {
            bResult = FileUtils.contentEqualsIgnoreEOL(f1, f2, null);
            if(bResult)
            rc = Constants.YES;
                else rc = Constants.NO;
        } catch (IOException e) {
            logMessage=e.toString();
            log(myName, Constants.INFO, myArea, logMessage);
            rc = Constants.ERROR;
        }
     return rc;
    }
    

//
// insert records into a temp file to compare later on
    private String insertValuesTempFile(int fileNr, String sDatabase, String sQuery) {
       return insertValuesTempFile(fileNr, sDatabase, sQuery, -9999);            
    }
        
    
  // insert values into temp file to compare later on
    private String insertValuesTempFile(int fileNr, String sDatabase, String sQuery, int srcNrRecords) {
    String myName="insertValuesTempFile";
    String myArea="init";
    String logMessage = Constants.NOT_INITIALIZED;
    String sResult = Constants.NOT_INITIALIZED;
    Connection srcConnection =null;
    Statement insStatement = null;
    Statement srcStatement = null;
    ResultSet rs = null;
    ResultSetMetaData dbResultMetaSet = null;

    int numColumnsInResultSet=0;
    readParameterFile();
            
      dbConnection =sDatabase;
        readParameterFile();
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());

        try {    
//        Class.forName(m_driver);
      logMessage="before create connection";      log(myName, Constants.DEBUG, myArea, logMessage);

      // Create a connection to the database
            srcConnection = connectionProperties.getUserConnection();
      logMessage="before create statement";        log(myName, Constants.DEBUG, myArea, logMessage);
      // createStatement() is used for create statement object that is used for sending sql statements to the specified database.


      logMessage="before executing query >" +sQuery +"<.";
      log(myName, Constants.DEBUG, myArea, logMessage);
      // run query
        
        if(Constants.DATABASETYPE_ORACLE.equals(connectionProperties.getDatabaseType())) {  
            logMessage="alter session to set date format...";
            log(myName, Constants.DEBUG, myArea, logMessage);
      srcStatement = srcConnection.createStatement();
      String sAlterSession="alter session set nls_date_format='YYYY-MM-DD HH24:MI:SS'";
      srcStatement.executeUpdate(sAlterSession);
      srcStatement.close();
        }
        
        logMessage="Executing statement...";
        log(myName, Constants.DEBUG, myArea, logMessage);
      srcStatement = srcConnection.createStatement();
      rs = srcStatement.executeQuery(sQuery);    

    myArea="After query execution";
      dbResultMetaSet = rs.getMetaData();
      numColumnsInResultSet = dbResultMetaSet.getColumnCount();
            logMessage="There are >" + Integer.toString(numColumnsInResultSet) + "< columns in query >" +sQuery +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

      insertedRows=0;
          //Loop through the results
        sResult = Constants.OK;
        int r =0;
        String sConcat = Constants.NOT_INITIALIZED;
        int iCol=0;
      // loop through the table to be placed into the temp oracle table
      while ((! sResult.equals(Constants.ERROR)) && (! sResult.equals(Constants.STOP)) && rs.next()) {
             r++;
          iCol=0;
          
          if((srcNrRecords != -9999) && (r > srcNrRecords) ) {
              logMessage="Source #records =>" + Integer.toString(srcNrRecords) + "< and now found record# >" + Integer.toString(r) + "<. So source<>target.";
            log(myName, Constants.STOP, myArea, logMessage);
            sResult = Constants.STOP;
          } else {
              
          logMessage="15-" + Integer.toString(r);               log(myName, Constants.VERBOSE, myArea, logMessage);
              String delimiter =getDelimiter();
            for (int j = 1; j <= numColumnsInResultSet; ++j)  
            {
                    if (rs.getString(j) == null) {
                    //ignore
                    }
                    else {
                        //treat timestamp special
                        if(dbResultMetaSet.getColumnType(j) == Types.TIMESTAMP) {
                          //ignore
                        } else { if(dbResultMetaSet.getColumnName(j).equals("DCKING_NUM")) 
                                 { 
                                     //ignore 
                                     } else { 
                            
                        iCol++;
                        if(iCol == 1) {
                          sConcat=rs.getString(j);
                        } else {
                            sConcat =sConcat +delimiter +rs.getString(j);
                        }
                        }}
                    }                                                               
            }       
              logMessage="17-" + Integer.toString(r) +": >" + sConcat + "<.";              log(myName, Constants.VERBOSE, myArea, logMessage);
          } 

            if(fileNr==1) {
                  String putResult=outputFile(streamOut1,sConcat);
                  if(! putResult.equals(Constants.ERROR))  insertedRows++;
              } 
  
          }
        
      logMessage="20-Closing sql statement...";        log(myName, Constants.DEBUG, myArea, logMessage);        
      srcStatement.close();
        logMessage="22-Closing db connection...";        log(myName, Constants.DEBUG, myArea, logMessage); 
      srcConnection.close();
        logMessage="23-DB Connection closed.";        log(myName, Constants.DEBUG, myArea, logMessage); 
        
    } 
    catch (SQLException e) {
      myArea="exception handling";
      logMessage="SQLException for >"+ dbConnection + "< userID=>" + connectionProperties.getDatabaseUsername() + "< db=>" + connectionProperties.getDatabase() + "< Error=>" +e.toString() + "<.";
      log(myName, Constants.ERROR, myArea, logMessage);
      sResult = Constants.ERROR;
    }

    return sResult;
  }

// create file for db table
    private String createFiles() {
    String myName="createFiles";
    String myArea="create files";
    String logMessage = Constants.NOT_INITIALIZED;
    
      try {
      streamOut1 = new DataOutputStream(new FileOutputStream(fileOut1,false));
      }
      catch(IOException e) {
        myArea="Exception handling file#1";
        logMessage="File could not be created: " + e.getMessage();
        log(myName, Constants.ERROR, myArea, logMessage);
        return Constants.ERROR;
      }
            

    return Constants.OK;      
    }

  private String outputFile(DataOutputStream fileHandle, String pToWrite) {
      String logMessage = Constants.NOT_INITIALIZED;
      String myArea="initialization";
      String myName="outputFile";
      
      String sResult = Constants.NOT_INITIALIZED;
      String sToWrite;
      sToWrite = pToWrite;

      try {
          sToWrite=sToWrite+ "\n";
        fileHandle.write(sToWrite.getBytes());
          sResult = Constants.OK;
      } catch (FileNotFoundException e) {
        myArea="Exception handling";
        logMessage="File could not be handled: " + e.getMessage();
        log(myName, "ERROR", myArea, logMessage);
        sResult = Constants.ERROR;
      
      } catch (IOException e) {
        myArea="Exception handling";
          logMessage="Could not write to file: " + e.getMessage();
          log(myName, "ERROR", myArea, logMessage);
          sResult = Constants.ERROR;
      }
              
    return sResult;
  }

  private String closeFiles() {
      String sResult = Constants.OK;
      String myName="closeFiles";
      String myArea="closing files";
      String logMessage = Constants.NOT_INITIALIZED;
      
    try {
        if(! (streamOut1 == null)) {
            myArea="closing file#1";
            streamOut1.close();
        }
    } catch(IOException e) {
      myArea="Exception handling";
        logMessage="Could not close file: " + e.getMessage();
        log(myName, "ERROR", myArea, logMessage);
        sResult = Constants.ERROR;
    }
    return sResult;
  }
  
  private String removeFiles() {
      String sResult = Constants.OK;
      String myName="removeFiles";
      String myArea="removing files";
      String logMessage = Constants.NOT_INITIALIZED;
      File f;
      
    try {
    myArea="removing file#1";
    
      f = new File(fileOut1);
      f.delete();
    } catch(SecurityException e) {
      myArea="Exception handling";
      logMessage="Could not remove file >" + fileOut1 +"<. Error: " + e.getMessage();
        log(myName, "ERROR", myArea, logMessage);
        sResult = Constants.ERROR;
    }
    return sResult;
  }

//
      //
    private String concatenateCols(List<String> cols, String delimiter, String srctgt) {
        //if this is target, the cols need to be concatenated in order of the src
        int i = 0;
        String sConcatenated = Constants.NOT_INITIALIZED;
        String myName = "concatenateCols";
        String myArea = "Looping";
        String logMessage = Constants.NOT_INITIALIZED;
      String colName = Constants.NOT_INITIALIZED;

        if(verbose) {logMessage ="Number of columns =>" + Integer.toString(cols.size()) + "< in db=>" +srctgt +"<.";     log(myName, Constants.DEBUG, myArea, logMessage); }

        int colLocationInSrc = -1;
        int numCols =0;
        String sDummy="SuperDuperDummy";

      numCols = cols.size();
        if(srctgt.equals(Constants.SRC) && (numCols < srcQueryColumnNames.size())) {
          numCols = srcQueryColumnNames.size();
        }

        String[] colValues = new String[numCols];
      if(verbose) {logMessage ="Array colValues length =>" + Integer.toString(colValues.length) + "<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
        
            for (i = 0; i < (cols.size()); ++i) {
                if (listExcludedColIds.contains(new Integer(i))) {
                  if(verbose) {logMessage ="ColumnId skipped =>" + Integer.toString(i) + "<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                    // skip this col
                } else {
                    myArea = "Processing column (not excluded)";
                    //we cannot directly concatenate.
                    //the target column order has to follow the source column order
                    //find where in the src table the column is
                        String colDetermined = determineMappedColumn(cols.get(i),srctgt);
                        if(colDetermined.equals(cols.get(i))) {
                          if(verbose) {logMessage ="Column is not excluded and not mapped. ColumnId=>" + Integer.toString(i) + "<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                          colLocationInSrc = srcQueryColumnNames.indexOf(colDetermined);
                          if (colLocationInSrc == -1) {
                               //column not in source table
                               logMessage =
                                       "Column >" + cols.get(i) + "< could not be found in source table.";
                               log(myName, "ERROR", myArea, logMessage);
                             return Constants.ERROR;
                          } else {
                            if(verbose) {logMessage ="Found column in >" + srctgt + "<. ColumnId=>" + Integer.toString(i) +  "<. In source =>" + colLocationInSrc +"<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                              if(colLocationInSrc >colValues.length) {
                                logMessage ="Source table has more columns than target table. Target column is at position >" + Integer.toString(colLocationInSrc) +"< in the Source table. Target table has only >" + Integer.toString(colValues.length) + "< columns to be compared." ;
                                log(myName, "ERROR", myArea, logMessage); 
                                  return Constants.ERROR;
                                }
                          }
                          if(verbose) {logMessage ="Getting columnId >"+ Integer.toString(i) + "< from cols. cols has size >" + Integer.toString(cols.size()) +"<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                          colName =cols.get(i);
                          if(verbose) {logMessage ="Storing column in colValues at location >" + Integer.toString(colLocationInSrc) +"<. colValues has >" + Integer.toString(colValues.length) + "< spots." ;     log(myName, Constants.DEBUG, myArea, logMessage); }
                          colValues[colLocationInSrc] = colName;
                          if(verbose) {logMessage ="Column stored in colValues.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                        } else {
                              if(verbose) {logMessage ="Column is mapped. Will skip this in this version. ColumnId=>" + Integer.toString(i) + "<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
                          //for now ignore this column, i.e. if it the column is mapped to a different columns
                            } 
                        }
                 // colValues[colLocationInSrc] = cols.get(i);
                  
                }
          
      logMessage ="Excluding and Mapping completed.";     log(myName, Constants.DEBUG, myArea, logMessage);

      logMessage ="Starting concatenation...";     log(myName, Constants.DEBUG, myArea, logMessage); 
      //now concatenate in the source column order (if srctgt this is the same thing
      for (int c = 0; c < (cols.size()); ++c) {
          if(colValues[c] == null ||  colValues[c].isEmpty()) {
            colValues[c] =sDummy;
          }
      }
      logMessage ="Starting sort...";     log(myName, Constants.DEBUG, myArea, logMessage);
      Arrays.sort(colValues);
      logMessage ="Sort completed.";     log(myName, Constants.DEBUG, myArea, logMessage); 
      // exchange column according to mapping
      
      for (int c = 0; c < numCols; ++c) {
          if(colValues[c].equals(sDummy)) {
            if(verbose) {logMessage ="Placeholder found at index =>" + Integer.toString(c) +"<.";     log(myName, Constants.DEBUG, myArea, logMessage); }
            //ignore this value
          } else {
              if(sConcatenated.equals(Constants.NOT_INITIALIZED)) {
                sConcatenated=colValues[c];
              } else {
                sConcatenated = sConcatenated + delimiter + colValues[c];
              }
          }
      }
      logMessage ="Concatenation completed.";     log(myName, Constants.DEBUG, myArea, logMessage); 

            myArea="return value";
            logMessage="for db =>" + srctgt + "< concatenated column list =>" + sConcatenated +"<.";    log(myName, Constants.VERBOSE, myArea, logMessage); 
            
        return sConcatenated;
    }


    private String determineMappedColumn(String org, String srctgt) {
        String myName="determineMappedColumn";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
        int colLocationInSrc =-1;
        
//        if(srctgt.equals(sSrc)) {
//          return org;
//        }
        
      if(listMapCols.contains(org)) {
        myArea="Processing mapped columns";
        String mappedToSrcCol = Constants.NOT_INITIALIZED;
        int colLocationInMapping=listMapCols.indexOf(org);
        if (colLocationInMapping == -1) {
          logMessage="Column >" + org + "< is a mapped column in >" + srctgt + "<, but could not be found in mapping list.";
          log(myName, "ERROR", myArea, logMessage);
          return Constants.ERROR;
        }
      
        mappedToSrcCol=listMappedToCols.get(colLocationInMapping);

        colLocationInSrc=srcQueryColumnNames.indexOf(mappedToSrcCol);
        if (colLocationInSrc == -1) {
            logMessage =
                    "Column >" + org + "< was mapped to >" + mappedToSrcCol+"< in >" + srctgt + "<, but could not be found in source table.";
            log(myName, "ERROR", myArea, logMessage);
          return Constants.ERROR;
        } 
        logMessage="Column >" + org + "< is a mapped column in >" + srctgt + "<. It maps to >" + srcQueryColumnNames.get(colLocationInSrc) + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        
        return srcQueryColumnNames.get(colLocationInSrc);
      } else {
          //not mapped, return original column name
        return org;
      }
      
    }

    public void setSourceTable(String srcTable) {
        this.srcTable = srcTable;
    }

    public void setSourceFilter(String srcFilter) {
        this.srcFilter = srcFilter;
    }


    public void setTargetFilter(String tgtFilter) {
        this.tgtFilter = tgtFilter;
    }
    public String getTargetFilter() {
        return tgtFilter;
    }


    //----------------------------------------------------------	
    //Function to read first row of table and set database name
    //----------------------------------------------------------

    public void getDatabaseName(List<String> input_row) {
        List<String> return_row = new ArrayList<String>();
        databaseName = input_row.get(1); //read first row second column
        //System.out.println("database name: " + m_databasename);
        //return_row.add("pass"); //return "pass" after for first cell
        //return_row.add("pass"); //return "pass" after for second cell
        addRowToReturnTable(return_row);
    }

  public List<String> determineQueryColumns(String sQuery) {
      String myName = "determineQueryColumns";
      String myArea = "initialization";
      String logMessage = "Start.";
      int numColumnsInResultSet = 0;
     List<String> queryColumnNames = new ArrayList<String>();

    logMessage="dtqc-01-before reading param file";               
    log(myName, Constants.DEBUG, myArea, logMessage);

      readParameterFile();

      logMessage="dtqc-01-after reading param file";               log(myName, Constants.DEBUG, myArea, logMessage); 

      returnMessage = Constants.OK;
  
      //attributes for reading database
      Connection m_connection = null;
      Statement m_statement = null;
      ResultSet m_resultset = null;
      ResultSetMetaData dbResultMetaSet = null;
      try {
          myArea = "Executing query";
          queryColumnNames.clear();
  //            readParameterFile();
          // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
//          Class.forName(m_driver);
          // Create a connection to the database
          if(verbose) { logMessage="creating connection.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          m_connection = connectionProperties.getUserConnection();
          // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
          if(verbose) { logMessage="creating statement.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          m_statement = m_connection.createStatement();
          // sql query of string type to read database

          logMessage =
                  "Query >" + sQuery + "< for db >" + connectionProperties.getDatabase() +
                  "< with userid >" + connectionProperties.getDatabaseUsername() + "<.";
          log(myName, Constants.DEBUG, myArea, logMessage);

        if(verbose) { logMessage="executing statement =>" + sQuery +"<.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          m_resultset = m_statement.executeQuery(sQuery);
        if(verbose) { logMessage="getting metadata of result.";               log(myName, Constants.DEBUG, myArea, logMessage); }
              dbResultMetaSet = m_resultset.getMetaData();
        if(verbose) { logMessage="getting number of columns.";               log(myName, Constants.DEBUG, myArea, logMessage); }
              numColumnsInResultSet = dbResultMetaSet.getColumnCount();
        if(verbose) { logMessage="number of columns=>" +Integer.toString(numColumnsInResultSet) + "<.";               log(myName, Constants.DEBUG, myArea, logMessage); }

              for (int i = 1; i <= (numColumnsInResultSet); ++i) {
                  String colname = dbResultMetaSet.getColumnName(i);
                  queryColumnNames.add(i - 1, colname);
              }
        if(verbose) { logMessage="column name list was built.";               log(myName, Constants.DEBUG, myArea, logMessage); }
        if(verbose) { logMessage="building excluded and mapped column list.";               log(myName, Constants.DEBUG, myArea, logMessage); }
        myArea="Getting column names";
              //get ids for columns to be excluded. this way, in the data string concatenation, they can be excluded
              for (int i = 1; i <= (numColumnsInResultSet); ++i) {
                  if (listExcludeCols.contains(dbResultMetaSet.getColumnName(i))) {
                      listExcludedColIds.add(new Integer(i - 1));
                  }
                  //some columns are named differently in the target
                  if (listMapCols.contains(dbResultMetaSet.getColumnName(i))) {
                      logMessage="Column name >" + dbResultMetaSet.getColumnName(i) + "< is a mapped column. Adding as >" + Integer.toString(i-1) +"<.";
                      listMappedColIds.add(new Integer(i - 1));
                  }
              }
        if(verbose) { logMessage="excluded column and mapped column lists were built.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          
         myArea = "finalization";
        if(verbose) { logMessage="closing statement.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          m_statement.close();
        if(verbose) { logMessage="closing connection.";               log(myName, Constants.DEBUG, myArea, logMessage); }
          m_connection.close();

      }  catch (SQLException e) {
          returnMessage = "SQLException >" + e.toString() + "<.";
        nrDbRecords="ERROR";
          myArea = "Exception handling";
          logMessage = "db=>"+ connectionProperties.getDatabase() + "< UserID=>" + connectionProperties.getDatabaseUsername() + "< Error=>" + e.toString() + "<.";
          log(myName, "ERROR", myArea, logMessage);
      }

    if(verbose) { logMessage="return code >" + queryColumnNames.toString() + "<.";               log(myName, Constants.DEBUG, myArea, logMessage); }
      return queryColumnNames;
  }



    public List<List<String>> executeQuery(String sQuery) {
        return executeQuery(sQuery, colsResultSetDummy);
    }
    //----------------------------------------------------------
    //Function to select statement based on input in fitnesse table
    //----------------------------------------------------------

    public List<List<String>> executeQuery(String sQuery,
                                           int nColumnsInResultSet) {
        //attributes for internal database table
        List<List<String>> database_table = new ArrayList<List<String>>();
        String myName = "executeQuery";
        String myArea = "initialization";
        String rcMsg = "not initialized";
        String logMessage = "Start.";
        int numColumnsInResultSet = 0;

        readParameterFile();
        returnMessage = Constants.OK;
  
        //attributes for reading database
        Connection m_connection = null;
        Statement m_statement = null;
        ResultSet m_resultset = null;
        ResultSetMetaData dbResultMetaSet = null;
        try {
            myArea = "Executing query";
            queryColumnNames.clear();
//            readParameterFile();
            // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
            //Class.forName(m_driver);
            // Create a connection to the database
            m_connection = connectionProperties.getUserConnection();

            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            m_statement = m_connection.createStatement();
            // sql query of string type to read database

            logMessage =
                    "Query >" + sQuery + "< for db >" + connectionProperties.getDatabase() +
                    "< with userid >" + connectionProperties.getDatabaseUsername() + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);

            m_resultset = m_statement.executeQuery(sQuery);
            if (nColumnsInResultSet == colsResultSetDummy) {
                dbResultMetaSet = m_resultset.getMetaData();
                numColumnsInResultSet = dbResultMetaSet.getColumnCount();
            } else {
              numColumnsInResultSet = nColumnsInResultSet;
            }

                for (int i = 1; i <= (numColumnsInResultSet); ++i) {
                    String colname = dbResultMetaSet.getColumnName(i);
                    queryColumnNames.add(i - 1, colname);
                }
                //get ids for columns to be excluded. this way, in the data string concatenation, they can be excluded
                for (int i = 1; i <= (numColumnsInResultSet); ++i) {
                    if (listExcludeCols.contains(dbResultMetaSet.getColumnName(i))) {
//                        logMessage =
//                                "columnid >" + Integer.toString(i) + "< to be excluded.";
//                        log(myName, Constants.DEBUG, myArea, logMessage);
                        listExcludedColIds.add(new Integer(i - 1));
                    }
                }
            
            //Loop through the results
            while (m_resultset.next()) {
                List<String> m_database_row =
                    new ArrayList<String>(); // initialize list to be reused
                //				      m_database_row.add("column value"); //first cell will consist of a fixed value "column name"
                //Add db result row (=multiple field) into fitnesse results array
                for (int j = 1; j < 1 + numColumnsInResultSet; ++j) {
                    if (m_resultset.getString(j) == null) {
                        m_database_row.add(""); //string should be filled with empty spaces otherwise a java null exception is created
                    } else {
                        //columns must be in a specific order
                        m_database_row.add(m_resultset.getString(j));
                    }
                }
                database_table.add(m_database_row);
            }

            myArea = "finalization";
            nrDbRecords = Integer.toString(database_table.size());
            logMessage =
                    "Number of database rows found =>" + nrDbRecords +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);


            m_statement.close();
            m_connection.close();

        } 
         catch (SQLException e) {
            returnMessage = "SQLException : " + e;
          nrDbRecords="ERROR";
            myArea = "SQLException";
            logMessage = e.toString();
            log(myName, "ERROR", myArea, logMessage);
        }
        return database_table;
    }

    //----------------------------------------------------------
    //Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
    //----------------------------------------------------------

    public void addRowToReturnTable(List<String> row) {
        return_table.add(row);
    }
    //----------------------------------------------------------
    //Function to read the parameters in a parameter file
    //----------------------------------------------------------

    private void readParameterFile(){        
    String myName="readParameterFile";
    String myArea="reading parameters";
    String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +dbConnection +"<.");
        if(connectionProperties.refreshConnectionProperties(dbConnection)) {
            log(myName, Constants.DEBUG, myArea,"username set to >" + connectionProperties.getDatabaseUsername() +"<.");
        } else {
            log(myName, Constants.ERROR, myArea, "Error retrieving parameter(s): " + connectionProperties.getErrorMessage());
        }


        tmpDir =GetParameters.GetTemp();
        logMessage="tmpDir >" + tmpDir +"<.";    log(myName, Constants.VERBOSE, myArea, logMessage);

    }

    public String numberOfDifferences() {
      return nrOfDifferences;
    }
    public String numberOfSourceRecords() {
      return nrSrcDbRecords;
    }
    public String numberOfTargetRecords() {
      return nrTgtFileRecords;
    }
    
    public String logFilename() {
        return logFileName +".log";
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
    /**
     * @return Log file name. If the LogUrl starts with http, a hyperlink will be created
     */
    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
    }

    public static String getVersion() {
        return version;
    }

    /**
     * @param level to which logging should be set. Must be VERBOSE, DEBUG, INFO, WARNING, ERROR or FATAL. Defaults to INFO.
     */
    public void setLogLevel(String level) {
        String myName = "setLogLevel";
        String myArea = "determineLevel";

        logLevel = Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, Constants.WARNING, myArea, "Wrong log level >" + level + "< specified. Defaulting to level 3.");
            logLevel = 3;
        }

        log(myName, Constants.INFO, myArea,
                "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
    }

    /**
     * @return - the log level
     */
    public String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return - the log level as Integer data type
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }

    public void setVerboseLogging(String yesNo) {
        if(Constants.YES.equalsIgnoreCase(yesNo)) {
            this.verbose=true;
            setLogLevel(Constants.VERBOSE);
        } else {
            this.verbose=false;
        }
    }
    
    public void setDirectory(String targetDir) {
        this.tgtDirectory =targetDir;
    }

    public void setFileName(String fileName) {
        String dir = Constants.NOT_FOUND;
        String parts[];
        
        parts=fileName.split(" ", 2);
        dir=GetParameters.FileOperationGetEntry(parts[0]);
        
        this.tgtFileName = dir + "/" + parts[1];
    }
    public String getFileName() {
        return this.tgtFileName;
    }

    public void setUniqueIdentifier(String uid) {
        this.uid = uid;
    }
    public String getUniqueIdentifier() {
        return uid;
    }
    public String getDelimiter() {
        return delimiter;
    }
    public void setDelimiter(String delimiter) {
        this.delimiter=delimiter;
    }
    public void setSourceSql(String sqlStmt) {
        this.srcSQL = sqlStmt;
        this.useSQL =true;
    }
    public String getSourceSql() {
        return srcSQL;
    }
    public String tempFileRemoval() {
        return removeFiles();
    }
}
