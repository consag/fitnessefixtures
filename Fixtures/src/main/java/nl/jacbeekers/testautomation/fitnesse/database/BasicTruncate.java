/*
 * The input parameters are provided by a Script table in the FitNesse wiki.
 * @author Jac. Beekers
 * @version 4 August 2017
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BasicTruncate {

    private static String version = "20181015.0";
    ConnectionProperties connectionProperties = new ConnectionProperties();
    private String className = "BasicTruncate";
    private String logFileName = Constants.NOT_INITIALIZED;
    private boolean logFileNameAlreadySet = false;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 4;
    private String logUrl = Constants.LOG_DIR;
    private boolean firstTime = true;
    private String databaseName;
    private String connectAs = Constants.DBCONN_ASUSER;
    private String databaseType;

    private String tableName;
    private boolean dataTypesDetermined = false;

    private String errorMessage = Constants.NOERRORS;
    private String errorLevel = Constants.OK;


    public BasicTruncate() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;
        logFileNameAlreadySet = false; // allow override

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public BasicTruncate(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + context;
        logFileNameAlreadySet = false; // allow override
        setContext(context);
    }

    public String getConnectAs() {
        return this.connectAs;
    }

    public void setConnectAs(String connectAs) {
        this.connectAs = connectAs;
    }

    public static String getVersion() {
        return version;
    }

    private void setContext(String context) {
        this.context = context;
    }

    private String getContext() {
        return this.context;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        String myName = "setDatabaseType";
        String myarea = "run";
        this.databaseType = databaseType;
        log(myName, Constants.DEBUG, myarea, "databaseType set to >" + this.databaseType + "<.");
    }

    public String getClassName() {
        return this.className;
    }

    //auto-called by fitnesse for each row
    public void reset() {
        setDatabaseName(Constants.NOT_PROVIDED);
        setTableName((Constants.NOT_PROVIDED));
    }

    public void set(String col, String val) {
        col = col.toLowerCase().replaceAll("\\s", "");

        switch (col) {
            case "databasename":
                setDatabaseName(val);
                break;
            case "loglevel":
                setLogLevel(val);
                break;
            case "tablename":
                setTableName(val);
                break;
            default:
                // it's a column name for the insert statement
                break;
        }

    }

    public String get(String whatToGet) {
        String val = Constants.NOT_INITIALIZED;
        whatToGet = whatToGet.toLowerCase().replaceAll("\\s", "");

        switch (whatToGet) {
            case "result":
                val = result();
                break;
            case "logfile":
                val = getLogFilename();
                break;
            case "errormessage":
                val = getErrorMessage();
                break;
            default:
                val = Constants.UNKNOWN;
                break;
        }
        return val;
    }

    //Compatibility to obsoleted truncateTable
    public String truncateTable() {
        return result();
    }

    public String result() {

        if(result(Constants.OK))
                return Constants.OK;
        else
            return Constants.ERROR;
    }

    /**
     * @param expected - the expected result. This will be compared to the actual result and determines the outcome of the method call.
     * @return
     */
    public boolean result(String expected) {
        String myArea = "init";
        String myName = "result";

        myArea = "readParameterFile";
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Setting logFileName for connectionProperties to >" + logFileName + "<.");
        connectionProperties.setLogFilename(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());
        log(myName, Constants.DEBUG, myArea, "connectionProperties-dbType is >" + connectionProperties.getDatabaseType() + "<.");
        setDatabaseType(connectionProperties.getDatabaseType());

        submitTruncateStatement();

        if (getErrorLevel().equals(expected))
            return true;
        else
            return false;

    }

    /**
     * @param input_row
     */
    public void setDatabaseNameFromTestTable(List<String> input_row) {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "setDatabaseNameFromTestTable";
        String myArea = "init";
        setDatabaseName(input_row.get(1)); //read first row second column

        logMessage = "database name: " + getDatabaseName();
        log(myName, Constants.INFO, myArea, logMessage);

    }

    /**
     * @param input_row
     */
    public void setTableNameFromTestTable(List<String> input_row) {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "setTableNameFromTestTable";
        String myArea = "init";

        setTableName(input_row.get(1)); //read first row second column
        logMessage = "table name: " + getTableName();
        log(myName, Constants.INFO, myArea, logMessage);

    }

    //----------------------------------------------------------
    //Function to submit statement based on input in fitnesse table row >=3
    //----------------------------------------------------------
    private String submitTruncateStatement() {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "submitTruncateStatement";
        String myArea = "init";
        boolean rcSql=false;

        String sqlStatement = "TRUNCATE TABLE " + getTableName();

        rcSql = runSQL(sqlStatement);
        if(!rcSql) {
            sqlStatement = "DELETE FROM " + getTableName();
            rcSql = runSQL(sqlStatement);
        }

        if(rcSql) {
            logMessage="Table >" + getTableName()
                    + "< successfully truncated or deleted all data from it.";
            setErrorMessage(Constants.OK, logMessage);
            log(myName, Constants.DEBUG, myArea, getErrorMessage());
        } else {
            setErrorMessage(Constants.ERROR, getErrorMessage());
            log(myName, Constants.ERROR, myArea, getErrorMessage());
        }

        return getErrorLevel();
    }

    private boolean runSQL(String sqlStatement) {
        String myName ="runSQL";
        String logMessage = Constants.NOT_INITIALIZED;
        String myArea = "Processing";
        boolean rc=false;

        Connection connection = null;
        Statement statement = null;

        logMessage = "SQL: " + sqlStatement;
        log(myName, Constants.INFO, myArea, logMessage);
        try {
            // Create a connection to the database
            if (Constants.DBCONN_ASOWNER.equals(getConnectAs())) {
                connection = connectionProperties.getOwnerConnection();
            } else { // connect as user
                connection = connectionProperties.getUserConnection();
            }

            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();

            statement.executeUpdate(sqlStatement);

            statement.close();
            connection.close();
            rc=true;
        } catch (SQLException e) {
            logMessage = "An error occurred executing statement >" + sqlStatement + "<. SQLException: " + e.toString();
            log(myName, Constants.ERROR, myArea, logMessage);
            setErrorMessage(Constants.ERROR, logMessage);
            rc=false;
        }
        return rc;

    }
    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String propVal = Constants.NOT_FOUND;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + databaseName + "<.");
        connectionProperties.setLogFilename(getLogFileNameOnly());
        connectionProperties.setLogLevel(getIntLogLevel());
        connectionProperties.setDatabaseName(databaseName);
        connectionProperties.refreshConnectionProperties(databaseName);
        setDatabaseType(connectionProperties.getDatabaseType());

        setLogUrl(GetParameters.GetLogUrl());

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
            Logging.LogEntry(logFileName, getClassName() + "-" + name, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(logFileName, getClassName() + "-" + name, level, area, logMessage);
    }

    public String getLogUrl() {
        return this.logUrl;
    }

    public void setLogUrl(String logUrl) {
        if (Constants.NOT_FOUND.equals(logUrl)) {
            String myName = "setLogUrl";
            String myArea = "run";
            String logMessage = "Properties file does not contain LogURL value.";
            log(myName, Constants.WARNING, myArea, logMessage);
        } else {
            this.logUrl = logUrl;
        }
    }

    /**
     * @return Log file name. If the LogUrl starts with http, a hyperlink will be created
     */
    public String getLogFilename() {
        if (getLogUrl().startsWith("http"))
            return "<a href=\"" + getLogUrl() + this.logFileName + ".log\" target=\"_blank\">" + this.logFileName + "</a>";
        else
            return getLogUrl() + this.logFileName + ".log";
    }

    public String getLogFileNameOnly() {
        return this.logFileName;
    }

    public void setLogFileName(String logFileName) {
        if (!logFileNameAlreadySet) {
            this.logFileName = logFileName;
        }
        this.logFileNameAlreadySet = true;
    }
    /**
     * @param level to which logging should be set. Must be VERBOSE, DEBUG, INFO, WARNING, ERROR or FATAL. Defaults to INFO.
     */

    /**
     * @return - the log level
     */
    public String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
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
     * @return - the log level as Integer data type
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }

    /**
     * @return - the database name the fixture runs against
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * @param databaseName to run the fixture against. This logical name must exist in database.properties
     */
    public void setDatabaseName(String databaseName) {
        String myName = "setDatabaseName";
        String myArea = "run";
        String logMessage = Constants.NOT_INITIALIZED;
        this.databaseName = databaseName;

        logMessage = "Database name has been set to >" + this.databaseName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    /**
     * @return - the database table name used by the fixture
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        String myName = "setTableName";
        String myArea = "run";
        String logMessage = Constants.NOT_INITIALIZED;
        this.tableName = tableName;

        logMessage = "Table name has been set to >" + this.tableName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    private void setErrorMessage(String level, String logMessage) {
        setErrorMessage(logMessage);
        setErrorLevel(level);
    }

    /**
     * @return - the error message (if any). If not "No errors encountered"  is returned (Check Constants for the actual value)
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private void setErrorMessage(String errMessage) {
        String myName = "setErrorMessage";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorMessage = errMessage;
        logMessage = "Error message has been set to >" + this.errorMessage + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    /*
     * Methods needed for FitNesse as it does not call set/get methods
     */
    public void databaseName(String databaseName) {
        setDatabaseName(databaseName);
    }

    /**
     * @param tableName
     */
    public void tableName(String tableName) {
        setTableName(tableName);
    }

    /**
     * @return
     */
    public String getErrorLevel() {
        return errorLevel;
    }

    private void setErrorLevel(String level) {
        String myName = "setModifyValue";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorLevel = level;
        logMessage = "Error level has been set to >" + this.errorLevel + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

}
