/**
 * This purpose of this fixture is to truncate a database table
 * 
 * @author Jac Beekers
 * @version 10 May 2015
 */

package nl.consag.testautomation.database;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

import java.sql.*;
import java.text.SimpleDateFormat;


public class TruncateTable {
  //27-07-2013 Change to new log mechanism, preventing java heap space error with fitnesse stdout
    private String className = "TruncateTable";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;

    private int logLevel =3;

    private String driver;
    private String url;
    private String userId;
    private String password;
    private String databaseName;
    private String databaseType;
    private String databaseConnDef;

      
    private String returnMessage = ""; //text message that is returned to FitNesse  

	  private String tableName;
  private String tableOwner = Constants.NOT_INITIALIZED;
  private String tableOwnerPassword = Constants.NOT_INITIALIZED;


    /**
     * @param pContext
     */
    public TruncateTable(String context) {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      this.startDate = sdf.format(started);
      this.context=context;
        logFileName = startDate + "." + className +"." + context;


    }
    
    public TruncateTable() {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      this.startDate = sdf.format(started);
      this.context=className;
        logFileName = startDate + "." + className ;

    }
//----------------------------------------------------------
//Function to read the database from the fitnesse table
//----------------------------------------------------------

    /**
     * @param databasename
     */
    public void setDatabaseName(String databasename) {
	    String myName="setDatabaseName";
	    String myArea="init";
	    String logMessage = Constants.NOT_INITIALIZED;

            this.databaseName = databasename;  
            logMessage="database name >" + databaseName +"<.";
	    log(myName, Constants.DEBUG, myArea, logMessage);
      
	  }
		  
//----------------------------------------------------------
//Function to get the table name from the fitnesse table
//----------------------------------------------------------

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
	    String myName="setTableName";
	    String myArea="init";
	    String logMessage = Constants.NOT_INITIALIZED;
        
            this.tableName = tableName;  
	    logMessage="table name >" + tableName +"<.";
	    log(myName, Constants.DEBUG, myArea, logMessage);
      
	  }  

//----------------------------------------------------------
//Function submit the truncate SQL statement
//----------------------------------------------------------

    /**
     * @return
     */
    public String truncateTable ()  {
	    String myName="truncateTable";
	    String myArea="init";
	    String logMessage = Constants.NOT_INITIALIZED;
            Connection connection = null;
            Statement statement = null;
	    String QueryString;

		  //Get parameters from file
		  readParameterFile();		  
		  
		  int updateQuery = 0; 
      
	    logMessage="Method: truncateTable.";
	    log(myName, Constants.VERBOSE, myArea, logMessage);
		  
		  try {
                        myArea="SQL Execution";
                        // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
                        //   Class.forName(driver);
                        // Create a connection to the database	        
                            logMessage = "Connecting to >" + databaseConnDef +"< using userID >" + tableOwner + "<.";
                            log(myName, Constants.DEBUG, myArea, logMessage);
                        connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);	
                        // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
                        statement = connection.createStatement();
                        // sql query of string type to create a database.
                        // First try truncate
                        QueryString = "truncate table " + tableName;  
                            logMessage="SQL >" + QueryString +"<.";
                            log(myName, Constants.DEBUG, myArea, logMessage);
                        updateQuery = statement.executeUpdate(QueryString);
                            logMessage="SQL returned >" + Integer.toString(updateQuery) + "<.";
                            log(myName, Constants.DEBUG, myArea, logMessage);
                        returnMessage = Constants.OK;			    
                        statement.close();
                        connection.close();  			 			    
			}  catch (SQLException e) {
			  myArea="Exception handling";
                          logMessage = "SQLException at TRUNCATE >" + e.toString() +"<. Will try a delete instead.";
			  log(myName, Constants.WARNING, myArea, logMessage);
                          returnMessage =logMessage;
			  try {
                            myArea="deleting";
                                logMessage = "Connecting to >" + databaseConnDef +"< using userID >" + userId + "<.";
                                log(myName, Constants.DEBUG, myArea, logMessage);
                            connection = DriverManager.getConnection(url, userId, password);  
                            statement = connection.createStatement();
                            QueryString = "delete from " + tableName;  
                                logMessage="Querystring: " + QueryString;
                                log(myName, Constants.DEBUG, myArea, logMessage);
                            updateQuery = statement.executeUpdate(QueryString);
                            if (updateQuery != 0){ 
                                logMessage="Statement processed. Deleted >" + Integer.toString(updateQuery) + "< records.";
                                log(myName, Constants.INFO, myArea, logMessage);
                                returnMessage = Constants.OK; 
                            }
                            else {
                                logMessage="Statement processed. Deleted >" + Integer.toString(updateQuery) +"<.";
                                log(myName, "info", myArea, logMessage);
                                returnMessage = Constants.OK; //table is empty
                            }                 
                            statement.close();
                            connection.close();                 
                            } catch (SQLException e2) {
                                myArea="Exception handling";
                                logMessage = "SQLException at DELETE : " + e2.toString();
                                log(myName, Constants.ERROR, myArea, logMessage);
                                returnMessage = logMessage;
                            }
			}
		  
                        logMessage = "Message returning to FitNesse > " + returnMessage + "<.";	  
                        log(myName, Constants.DEBUG, myArea, logMessage);

		  return returnMessage; //text message that is passed to fitnesse
	  }
	  
//----------------------------------------------------------
//Function to read the parameters in a parameter file
//----------------------------------------------------------
	  private void readParameterFile(){	 

		   String myName="readParameterFile";
		   String myArea="reading";
		   String logMessage = Constants.NOT_INITIALIZED;

		   databaseType = GetParameters.GetDatabaseType(databaseName);
                    logMessage="Database type: " + databaseType;
                    log(myName, Constants.VERBOSE, myArea, logMessage);
       
		   databaseConnDef = GetParameters.GetDatabaseConnectionDefinition(databaseName);
		   logMessage="Database connection definition: " + databaseConnDef;
		   log(myName, Constants.VERBOSE, myArea, logMessage);
       
		    driver = GetParameters.GetDatabaseDriver(databaseType);
		   logMessage="driver: " + driver;
		   log(myName, Constants.VERBOSE, myArea, logMessage);
		   url = GetParameters.GetDatabaseURL(databaseConnDef);
          
		   tableOwner=GetParameters.GetDatabaseTableOwnerName(databaseName);
                    logMessage="Table Owner UserID >" + tableOwner + "<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                   tableOwnerPassword =GetParameters.GetDatabaseTableOwnerPWD(databaseName);         
                    logMessage="Password for user >" + tableOwner + "< retrieved.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                   userId = GetParameters.GetDatabaseUserName(databaseName);
                    logMessage="User UserID >" + userId + "<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                   password = GetParameters.GetDatabaseUserPWD(databaseName);
                    logMessage="Password for user >" + userId + "< retrieved.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
       
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

}