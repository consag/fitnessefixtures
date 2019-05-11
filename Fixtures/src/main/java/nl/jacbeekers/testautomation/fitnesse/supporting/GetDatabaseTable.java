package nl.jacbeekers.testautomation.fitnesse.supporting;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

public class GetDatabaseTable {
    private String className = "GetDatabaseTable";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel =3;
    private String errorMessage = Constants.OK;
    private String result = Constants.OK;
    private String errorCode = Constants.OK;

    private String driver;
    private String url;
    private String userId;
    private String password;
    private String databaseName;
    private String databaseType;
    private String databaseConnDef;
    private String tableOwner;
    private String tableOwnerPassword;


    /**
     * @param context
     */
    public GetDatabaseTable(String context) {
                java.util.Date started = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                startDate = sdf.format(started);
                this.context=context;
                logFileName = startDate + "." + className +"." + context;

            }

    public GetDatabaseTable(String context, String logLevel) {
                java.util.Date started = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                startDate = sdf.format(started);
                this.context=context;
                setLogLevel(logLevel);
                this.logLevel = getIntLogLevel();
                logFileName = startDate + "." + className +"." + context;
            }


    /**
     * @return
     */
    public List<List<String>> getQueryResult (String query, int numberOfTableColumns){
        return getQueryResult(query, numberOfTableColumns, false);
    }
    
    /*
     * 
     */
    public List<List<String>> getQueryResult (String query, int numberOfTableColumns, boolean useTableOwner){
               String myName="getQueryResult";
               String myArea="Initialization";
               String logMessage = Constants.NOT_INITIALIZED;
               List<List<String>> databaseTable = new ArrayList<List<String>>();
                                                                          
               Connection connection = null;
               Statement statement = null;
               ResultSet resultset = null;
               
               readParameterFile(getDatabaseName());
                          
               try {
                            // Create a connection to the database
                   if(useTableOwner) {
                       logMessage="Connecting to >" + url + "< with userid >" + tableOwner +"<.";
                       log(myName, Constants.DEBUG, myArea, logMessage);
                       connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);
                   } else {
                       logMessage="Connecting to >" + url + "< with userid >" + tableOwner +"<.";
                       log(myName, Constants.DEBUG, myArea, logMessage);
                       connection = DriverManager.getConnection(url, userId, password);
                   }
                            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
                            statement = connection.createStatement();
                            // sql query of string type to read database
                            logMessage="Query >" + query +"<.";
                            log(myName, Constants.DEBUG, myArea, logMessage);
                
                            resultset = statement.executeQuery(query);    
                            
                                //Loop through the results
                            while (resultset.next()) {
                                List<String> database_row = new ArrayList<String>(); // initialize list to be reused
                                //Add db result row (=multiple field) into fitnesse results array                               
                                for (int j = 1; j <= numberOfTableColumns; ++j)  
                                {
                                        if (resultset.getString(j) == null) {
                                                database_row.add(""); //string should be filled with empty spaces otherwise a java null exception is created
                                        }
                                        else {
                                                database_row.add(resultset.getString(j));
                                        }                                                               
                                }       
                                databaseTable.add(database_row);
                                 }
                            myArea="db query completed";
                            logMessage="Number of database rows found: " + Integer.toString(databaseTable.size());
                            log(myName, Constants.INFO, myArea, logMessage);
                        
                            statement.close();
                            connection.close();             
                                } 
                        catch (SQLException e) {
                                myArea="exception handling";
                                logMessage="SQLException: " + e.toString();
                                log(myName, Constants.ERROR, myArea, logMessage);
                                setError(Constants.ERROR,"SQLException : " + e);
                        }
               
                        return databaseTable;
                  }     


    private void readParameterFile() {
        readParameterFile(getDatabaseName());    
    }
    
    private void readParameterFile(String databaseName){        
    String myName="readParameterFile";
    String myArea="reading parameters";
    String logMessage = Constants.NOT_INITIALIZED;

    databaseType = GetParameters.GetDatabaseType(databaseName);
    databaseConnDef = GetParameters.GetDatabaseConnectionDefinition(databaseName);
    driver = GetParameters.GetDatabaseDriver(databaseType);
    url = GetParameters.GetDatabaseURL(databaseConnDef);
    userId = GetParameters.GetDatabaseUserName(databaseName);
    password = GetParameters.GetDatabaseUserPWD(databaseName);
    tableOwner = GetParameters.GetDatabaseTableOwnerName(databaseName);
    tableOwnerPassword =GetParameters.GetDatabaseTableOwnerPWD(databaseName);

    logMessage="databaseType >" + databaseType +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
    logMessage="connection >" + databaseConnDef +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
    logMessage="driver >" + driver +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
    logMessage="url >" + url +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
    logMessage="userId >" + userId +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    private void log(String name, String level, String area, String logMessage) {
        
        if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }

        Logging.LogEntry(logFileName, name, level, area, logMessage);
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
       logLevel =3;
    }
    
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

    private void setErrorMessage(String msg) {
        errorMessage=msg;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }

    private void setError(String code, String msg) {
        setErrorCode(code);
        setErrorMessage(msg);
    }
    private void setErrorCode(String code) {
        errorCode=code;
    }
    public String getErrorCode() {
        return this.errorCode;
    }
    public void setDatabaseName(String dbName) {
        this.databaseName=dbName;
    }
    public String getDatabaseName() {
        return this.databaseName;
    }
    
}
