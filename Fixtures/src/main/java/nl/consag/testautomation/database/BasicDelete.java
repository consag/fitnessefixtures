/*
 * The input parameters are provided by a Script table in the FitNesse wiki.
 * @author Jac. Beekers
 * @version 4 August 2017
*/
package nl.consag.testautomation.database;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class BasicDelete {

    private String className = "BasicDelete";
    private static String version = "20180619.1";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;

    private String tableName;
    private String ignore0Records = Constants.NO;

    private String filterColumn = Constants.NOT_PROVIDED;
    private String filterValue = Constants.NOT_PROVIDED;
    private String modifyColumn = Constants.NOT_PROVIDED;
    private String modifyValue = Constants.NOT_PROVIDED;
    private String numberOfRecordsDeleted = Constants.NONE;

    private String errorMessage = Constants.NOERRORS;
    private String errorLevel = Constants.OK;


    public BasicDelete() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public BasicDelete(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + context;
    }


    /**
     * @param expected - the expected result. This will be compared to the actual result and determines the outcome of the method call.
     * @return
     */
    public boolean result(String expected) {

        submitDeleteStatement();

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
    private String submitDeleteStatement() {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "submitDeleteStatement";
        String myArea = "init";

        Connection connection = null;
        Statement statement = null;
        int nrRecords = 0;
        String updateString = Constants.NOT_INITIALIZED;
        
        try {
            myArea="readParameterFile";
            readParameterFile();
            log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName +"<.");
            connectionProperties.setLogFileName(logFileName);
            connectionProperties.setLogLevel(getIntLogLevel());
            connection = connectionProperties.getUserConnection();
//            connection = DriverManager.getConnection(url, userId, password);
            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to submit update SQL statement into database.
            updateString =
                "Delete FROM " + tableName + " WHERE " + getDeleteFilter()  ;

            logMessage = "SQL: " + updateString;
            log(myName, Constants.INFO, myArea, logMessage);
            nrRecords =statement.executeUpdate(updateString);
            setNumberOfRecordsDeleted(nrRecords);

            if (nrRecords == 0) {
                if(getIgnore0Records().equals(Constants.NO)) {
                    setErrorMessage(Constants.ERROR,"Delete statement did not Delete any records while Ignore0Records had been set to >" + getIgnore0Records() +"<.");
                } else {
                    logMessage="Delete returned >0<. OK as Ignore0Records had been set to >" + getIgnore0Records() +"<.";
                    log(myName, Constants.INFO, myArea, logMessage);
                }
            } else {
                logMessage="Delete statement resulted in >" +getNumberOfRecordsDeleted() + "< record(s) Deleted.";
                log(myName, Constants.INFO, myArea, logMessage);
            }

            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            myArea = "exception handling";
            logMessage = "An error occurred executing statement >"+ updateString +"<. SQLException: " + e.toString(); // return "fail: SQLException : " + e;
            setErrorMessage(Constants.ERROR,logMessage);
            log(myName, Constants.ERROR, myArea, logMessage);
        }
        
        logMessage="Process completed with error level >" +getErrorLevel() +"<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return getErrorLevel();
    }

    private void readParameterFile() {
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

    /**
     * @param databaseName to run the fixture against. This logical name must exist in database.properties
     */
    public void setDatabaseName(String databaseName) {
        String myName="setDatabaseName";
        String myArea="run";
        String logMessage = Constants.NOT_INITIALIZED;
        this.databaseName = databaseName;

        logMessage="Database name has been set to >" + this.databaseName + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - the database name the fixture runs against
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        String myName="setTableName";
        String myArea="run";
        String logMessage = Constants.NOT_INITIALIZED;
        this.tableName = tableName;

        logMessage="Table name has been set to >" + this.tableName + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - the database table name used by the fixture
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param ignoreIt - whether or not an update that results in 0 records updated should be treated as OK or NOTOK
     * @return - returns ERROR is the parameter was not Yes or No
     */
    public String setIgnore0Records(String ignoreIt) {
        String myName = "setIgnore0Records";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        String rc = Constants.OK;

        if (Constants.YES.equalsIgnoreCase(ignoreIt) || Constants.NO.equalsIgnoreCase(ignoreIt)) {
            this.ignore0Records = ignoreIt;
            logMessage="Ignore 0 Records has been set to >" + this.ignore0Records + "<.";
            log(myName, Constants.VERBOSE,myArea,logMessage);
        } else {
            rc = Constants.ERROR;
            logMessage = "Wrong value >" + ignoreIt + "< for Ignore0Records supplied. Needs to be Yes or No";
            setErrorMessage(Constants.ERROR, logMessage);
            log(myName, Constants.ERROR, myArea, logMessage);
        }
        return rc;
    }

    /**
     * @return - Whether the update should return OK even when no records are updated.
     */
    public String getIgnore0Records() {
        return ignore0Records;
    }


    private void setErrorMessage(String errMessage) {
        String myName = "setErrorMessage";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        
        this.errorMessage = errMessage;
        logMessage="Error message has been set to >" + this.errorMessage + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
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

    /**
     * @param colName: The column that should have its value changed
     */
    public void setDeleteFilter(String colName) {
        String myName = "setDeleteFilter";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.modifyColumn = colName;

        logMessage="Delete filter has been set to >" + this.modifyColumn + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - the column that gets modified by the update statement
     */
    public String getDeleteFilter() {
        return this.modifyColumn;
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
     * @param ignoreIt
     */
    public void Ignore0Records(String ignoreIt) {
        setIgnore0Records(ignoreIt);
    }

    /**
     * @param colName
     */
    public void columnList(String colName) {
        setDeleteFilter(colName);
    }

    private void setErrorLevel(String level) {
        String myName = "setModifyValue";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorLevel = level;
        logMessage="Error level has been set to >" + this.errorLevel + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return
     */
    public String getErrorLevel() {
        return errorLevel;
    }
    
    private void setNumberOfRecordsDeleted(int i) {
        this.numberOfRecordsDeleted = Integer.toString(i);
    }

    /**
     * @return
     */
    public String getNumberOfRecordsDeleted() {
        return numberOfRecordsDeleted;
    }

}
