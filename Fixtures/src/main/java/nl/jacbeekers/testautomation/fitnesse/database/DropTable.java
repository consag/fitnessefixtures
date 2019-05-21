/**
 * This purpose of this fixture is to drop a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac Beekers
 * @version 10 May 2015
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import java.sql.*;
import java.text.*;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;
import nl.jacbeekers.testautomation.fitnesse.supporting.Parameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.propFileErrors;

public class DropTable {
	private String className = "DropTable";
    private static String version ="20190521.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String errorMessage = Constants.NO_ERRORS;
    private String query;

    private String databaseName =Constants.NOT_PROVIDED;
    private String actualDatabase = Constants.NOT_FOUND;
    private String tableName =Constants.NOT_PROVIDED;
    private String tableComment = Constants.TABLE_COMMENT;

    private String errorCode =Constants.OK;
    private boolean errorIndicator =false;

    private boolean ignoreErrorOnDrop =false;
    
	private int NO_FITNESSE_ROWS_TO_SKIP = 3;

	public DropTable() {
		//Constructors
	      	java.util.Date started = new java.util.Date();
	      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	      	startDate = sdf.format(started);
	      	this.context=className;
	        logFileName = startDate + "." + className ;
	        setLogFilename(logFileName);
	    }
	
	public DropTable(String context) {
	    	java.util.Date started = new java.util.Date();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	startDate = sdf.format(started);
	    	this.context=context;
	        logFileName = startDate + "." + className +"." + context;
	        setLogFilename(logFileName);

	    }

    public DropTable(String context, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context=context;
        logFileName = startDate + "." + className +"." + context;
        setLogFilename(logFileName);
        setLogLevel(logLevel);

    }

    public void ignoreErrorOnDrop(String yesNo) {
	    ignoreError(yesNo);
    }

    public void ignoreError(String yesNo) {
        if(yesNo.equals(Constants.YES))
            ignoreErrorOnDrop=true;
        else
            ignoreErrorOnDrop=false;
    }

    public boolean getIgnoreError() {
	    return this.ignoreErrorOnDrop;
    }
    
    public boolean tableDoesNotExistInDatabase(String inTableName, String inDatabase) {
        String myName="tableDoesNotExistInDatabase";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        String sqlStatement=Constants.NOT_INITIALIZED;
        String commentStatement=Constants.NOT_INITIALIZED;
        String existStatement=Constants.NOT_INITIALIZED;
        int sqlResult =0;
        boolean rc =false;
        String commentFound=Constants.NOT_FOUND;
        String tableName;

        databaseName=inDatabase;
        myArea="check db type";

        readParameterFile();

        if(getErrorIndicator()) {
            setError(ResultMessages.ERRCODE_PROPERTIES,"An error occurred while determining connection properties");
            return false;
        }

        if(connectionProperties.getUseTablePrefix()) {
            tableName = connectionProperties.getTableOwnerTablePrefix() + inTableName;
            tableName = tableName.toUpperCase();
            setTableName(tableName);
            log(myName, Constants.DEBUG, myArea, "useTablePrefix is >true<. Table name set to >" + getTableName() +"<.");
        } else {
            tableName = inTableName.toUpperCase();
            setTableName(tableName);
            log(myName, Constants.DEBUG, myArea, "useTablePrefix is >false<. Table name is >" + getTableName() +"<.");
        }

        switch (connectionProperties.getDatabaseType()) {
            case Constants.DATABASETYPE_ORACLE:
            case Constants.DATABASETYPE_DB2:
                sqlStatement = "drop table " + tableName;
                log(myName, Constants.DEBUG, myArea,"SQL statement is >" +sqlStatement +"<.");
                commentStatement ="select comments from user_tab_comments where table_name ='" + tableName +"'";
                existStatement="select count(*) from user_tables where table_name='" + tableName +"'";
                break;
            default:
                logMessage="databaseType >" + connectionProperties.getDatabaseType() +"< not yet supported";
                log(myName, Constants.ERROR, myArea, logMessage);
                setErrorMessage(logMessage);
                return false;
                //break;
        }

        /* We limit the drop to tables previously created using the CreateTable fixture
         * The CreateTable fixture uses a specific table comment (Oracle). If the comment is different or does not exist,
         * the DropTable fixture will NOT drop the table
         */
        GetSingleValue dbCol= new GetSingleValue(context);
        dbCol.setDatabaseName(databaseName);
        dbCol.setQuery(existStatement);
        commentFound = dbCol.getColumn();
        
        if (commentFound.equals("0")) {
            if(ignoreErrorOnDrop) {
                logMessage="Table >" + tableName +"< does not exist. Error will be ignored.";
                log(myName, Constants.INFO, myArea, logMessage);
                return true;
            } else {
                logMessage="Table >" + tableName +"< does not exist";
                log(myName, Constants.ERROR, myArea, logMessage);
                errorMessage="Table >" + inTableName +"< does not exist.";
                return false;
            }
        }

        if(Constants.DATABASETYPE_ORACLE.equals(connectionProperties.getDatabaseType())) {
            dbCol.setDatabaseName(databaseName);
            dbCol.setQuery(commentStatement);
            commentFound = dbCol.getColumn();
        
            if (commentFound == null || commentFound.isEmpty() || commentFound.equals("0")){
                errorMessage="A table comment could not be found. If the table exists, it will be dropped anyway.";
            } else {
                if( ! commentFound.equals(tableComment) && ! getIgnoreError()) {
                    errorMessage="A table comment matching the comment issued by the CreateTable fixture was not found. The table will NOT be dropped.";
                    return false;
                }
            }
        }
         
        try {
        myArea="SQL Execution";
           logMessage = "Connecting to >" + connectionProperties.getDatabase() +"< using userID >" + connectionProperties.getDatabaseTableOwner() + "<.";
           log(myName, Constants.INFO, myArea, logMessage);
           connection = connectionProperties.getOwnerConnection();
//                connection = DriverManager.getConnection(getDatabaseUrl(), getDatabaseTableOwner(), getDatabaseTableOwnerPassword());
                statement = connection.createStatement();
                logMessage="SQL >" + sqlStatement +"<.";
                log(myName, Constants.INFO, myArea, logMessage);
                sqlResult= statement.executeUpdate(sqlStatement);
            logMessage="SQL returned >" + Integer.toString(sqlResult) + "<.";
           log(myName, Constants.INFO, myArea, logMessage);

          statement.close();
          connection.close();      
            rc=true;
              }  catch (SQLException e) {
                myArea="Exception handling";
                logMessage = "SQLException at >" + myName + "<. Error =>" + e.toString() +"<.";
                log(myName, "ERROR", myArea, logMessage);
                if((e.toString().contains("ORA-00942")
                        || /*DB2*/ e.toString().contains("SQLCODE=-204, SQLSTATE=42704"))
                        && ignoreErrorOnDrop) {
                    setErrorMessage(Constants.NO_ERRORS);
                    rc=true;
                } else {
                    setErrorMessage(logMessage);
                 rc =false;
                }
              }
    
         return rc;
    }

    private void setErrorMessage(String errorMessage) {
	    this.errorMessage = errorMessage;
    }

    public String tableNameFor (String inTableName) {
	    if(connectionProperties.getUseTablePrefix()) {
	        return connectionProperties.getTableOwnerTablePrefix() +"." + inTableName;
        } else {
	        return inTableName;
        }

    }
    
    public boolean tableDoesNotExist(String inTableName) {
        String myName="tableDoesNotExist";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;

        String tableName;
        String nrTablesFound =Constants.NOT_INITIALIZED;
        
        myArea="check db type";
        readParameterFile();
        if(connectionProperties.getUseTablePrefix()) {
            tableName = connectionProperties.getTableOwnerTablePrefix() + inTableName;
        } else {
            tableName = inTableName;
        }
        
        if(Constants.DATABASETYPE_ORACLE.equals(connectionProperties.getDatabaseType())) {
            query="SELECT count(*) tblcount FROM user_tables WHERE table_name ='" +tableName +"'";
        } else {
            logMessage="databaseType >" + connectionProperties.getDatabaseType() +"< not yet supported";       
            log(myName, Constants.INFO, myArea, logMessage);  
            errorMessage=logMessage;
            return false;
        }
        GetSingleValue dbCol= new GetSingleValue(className);
        dbCol.setDatabaseName(databaseName);
        dbCol.setQuery(query);
        nrTablesFound = dbCol.getColumn();
        
        if (nrTablesFound.equals("0") ) 
        return true;

        errorMessage="Count on tables returned >" + nrTablesFound +"<.";
        return false;
    }
    
    

    public String errorMessage() {
        return errorMessage;
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

    public static String getVersion() {
        return version;
    }

    /**
     * @return Log file name. If the LogUrl starts with http, a hyperlink will be created
     */
    public String getLogFilename() { return this.logFileName; }
    public void setLogFilename(String logFilename) { this.logFileName = logFilename;}

     public String getLogFilenameLink() {
     if(logUrl.startsWith("http"))
     return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
     else
     return logUrl+logFileName + ".log";
     }

    public void setLogLevel(String level) {
        String myName ="setLogLevel";
        String myArea ="determineLevel";

        logLevel =Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel <0) {
            log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
            logLevel =3;
        }

        log(myName,Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
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

    private String getProperty(String propertiesFile, String key, boolean mustExist) {
        String myName ="getProperty";
        String myLocation="start";
        String result =Constants.NOT_FOUND;
        Parameters parameters = new Parameters();

        log(myName, Constants.VERBOSE, myLocation, "Retrieving value for property >"
                + key +"< from >" + propertiesFile +"<.");
        result =parameters.getPropertyVal(propertiesFile, key);
        log(myName, Constants.VERBOSE, myLocation, "search for property >" +key + "< returned result code >" +parameters.getResult() +"<.");
        if(mustExist && propFileErrors.contains(parameters.getResult())) {
            setError(result, "Error retrieving property >" + key + "< from >" + propertiesFile + "<.");
            setErrorIndicator(true);
            return parameters.getResult();
        }
        setErrorIndicator(false);
        return result;
    }

    private void setErrorIndicator(boolean indicator) {
        errorIndicator =indicator;
    }
    private boolean getErrorIndicator(){
        return this.errorIndicator;
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "Setting log file for connectionProperties to >" + getLogFilename() +"<.");
        connectionProperties.setLogFilename(getLogFilename());
        connectionProperties.setLogLevel(getIntLogLevel());

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +databaseName +"<.");
        if(connectionProperties.refreshConnectionProperties(databaseName)) {
            log(myName, Constants.DEBUG, myArea,"username set to >" + connectionProperties.getDatabaseUsername() +"<.");
        } else {
            log(myName, Constants.ERROR, myArea, "Error retrieving parameter(s): " + connectionProperties.getErrorMessage());
        }

    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getActualDatabase() {
        return actualDatabase;
    }

    public void setActualDatabase(String actualDatabase) {
        this.actualDatabase = actualDatabase;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isErrorIndicator() {
        return errorIndicator;
    }

    private void setError(String errorCode, String errorMessage) {
        this.errorCode =errorCode;
        this.errorMessage = errorMessage;
    }

}