/*
 * The input parameters are provided by a Script table in the FitNesse wiki.
 * @author Jac. Beekers
 * @version 4 June 2015
*/
package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class BasicUpdate {

    private String className = "BasicUpdate";
    private static String version = "20180823.0";

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
    private List<String> modifyColumnNameList = new ArrayList<String>();
    private List<String> modifyColumnValueList = new ArrayList<String>();
    private String numberOfRecordsUpdated = Constants.NONE;

    private String errorMessage = Constants.NOERRORS;
    private String errorLevel = Constants.OK;


    public BasicUpdate() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public BasicUpdate(String context) {
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

        submitUpdateStatement();

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
    private String submitUpdateStatement() {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "submitUpdateStatement";
        String myArea = "init";

        Connection connection = null;
        Statement statement = null;
        int nrRecords = 0;
        String updateString = Constants.NOT_INITIALIZED;
        String setClause =" SET ";
        
        try {
            myArea="readParameterFile";
            readParameterFile();
            log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName +"<.");
            connectionProperties.setLogFileName(logFileName);
            connectionProperties.setLogLevel(getIntLogLevel());
            connection = connectionProperties.getUserConnection();

            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to submit update SQL statement into database.
            setClause =getSetValList();
            updateString =
                    /*"UPDATE " + tableName + " SET " + getModifyColumn() + "=" + getModifyValue() + "  WHERE " +
                getFilterColumn() + " IN (" + getFilterValue() +")";
*/
                    "UPDATE " + tableName + setClause + " WHERE " +
                            getFilterColumn() + " IN (" + getFilterValue() +")";

            logMessage = "SQL > " + updateString +"<.";
            log(myName, Constants.INFO, myArea, logMessage);
            nrRecords =statement.executeUpdate(updateString);
            setNumberOfRecordsUpdated(nrRecords);

            if (nrRecords == 0) {
                if(getIgnore0Records().equals(Constants.NO)) {
                    setErrorMessage(Constants.ERROR,"Update statement did not update any records while Ignore0Records had been set to >" + getIgnore0Records() +"<.");
                } else {
                    logMessage="Update Query returned >0<. OK as Ignore0Records had been set to >" + getIgnore0Records() +"<.";
                    log(myName, Constants.INFO, myArea, logMessage);
                }
            } else {
                logMessage="Update statement resulted in >" +getNumberOfRecordsUpdated() + "< record(s) updated";
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

    private String getSetValList(){
        String result =" SET ";
        String myName = "getSetValList";
        String myArea = "run";

        if (modifyColumnValueList.size() != modifyColumnNameList.size()){
            return "#Columns does not match #value";
        }
        for(int i=0; i < modifyColumnNameList.size() ; i++) {
            if(i!=0) {
                result +=", ";
            }
            result += modifyColumnNameList.get(i) +"=" + modifyColumnValueList.get(i);
            log(myName, Constants.VERBOSE, myArea, "set claus so far >" +result +"<.");
        }
        log(myName, Constants.DEBUG, myArea, "Final generated set clause >"+ result +"<.");

        return result;
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +databaseName +"<.");
        if(connectionProperties.refreshConnectionProperties(databaseName)) {
            log(myName, Constants.DEBUG, myArea,"username set to >" + connectionProperties.getDatabaseUsername() +"<.");
        } else {
            log(myName, Constants.ERROR, myArea, "Error retrieving parameter(s): " + connectionProperties.getErrorMessage());
        }

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
     * @param colName: On which column in the database table a filter for the update statement should be applied
     */
    public void setFilterColumn(String colName) {
        String myName = "setFilterColumn";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.filterColumn = colName;

        logMessage="Column to filter on has been set to >" + this.filterColumn + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - on which column the filter for the update statement is defined.
     */
    public String getFilterColumn() {
        return filterColumn;
    }

    /**
     * @param colValue: To which value the column filter should be set. This can also be a subselect clause. Basically anything that is valid SQL after an IN operator
     */
    public void setFilterValue(String colValue) {
        String myName = "setFilterColumn";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.filterValue = colValue;

        logMessage="Value to filter on has been set to >" + this.filterValue + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - the filter expression used by the update statement to filter on.
     */
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * @param colName: The column that should have its value changed
     */
    public void setModifyColumn(String colName) {
        String myName = "setModifyColumn";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.modifyColumn = colName;

        logMessage="Column to modify on has been set to >" + this.modifyColumn + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);

        addToModifyColumnNameList(colName);
    }

    private void addToModifyColumnNameList(String colName) {
        String myName = "addToModifyColumnNameList";
        String myArea ="run";
        String logMessage=Constants.NOT_INITIALIZED;

        this.modifyColumnNameList.add(colName);
        logMessage="Column has been added to modifyList >" + this.modifyColumnNameList.toString() + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);

    }

    /**
     * @return - the column that gets modified by the update statement
     */
    public String getModifyColumn() {
        return this.modifyColumn;
    }

    /**
     * @param colValue: The value to which the modifyColumn should be changed. This can also be a subselect. Basically any valid SQL construct after the ==sign of the SET clause of an UPDATE statement
     */
    public void setModifyValue(String colValue) {
        String myName = "setModifyValue";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.modifyValue = colValue;

        logMessage="Value to modify column to has been set to >" + this.modifyValue + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);

        addToModityColumnValueList(colValue);
    }

    private void addToModityColumnValueList(String value) {
        String myName ="addToModityColumnValueList";
        String myArea ="run";
        String logMessage=Constants.NOT_INITIALIZED;
        this.modifyColumnValueList.add(value);

        logMessage="Column has been added to valueList >" + this.modifyColumnValueList.toString() + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return - The value to which the modifyColumn should be changed (or changed)
     */
    public String getModifyValue() {
        return modifyValue;
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
    public void FilterOnColumn(String colName) {
        setFilterColumn(colName);
    }

    /**
     * @param colValue
     */
    public void withValue(String colValue) {
        setFilterValue(colValue);
    }

    /**
     * @param colName
     */
    public void modifyColumn(String colName) {
        setModifyColumn(colName);
    }

    /**
     * @param colValue
     */
    public void setToValue(String colValue) {
        setModifyValue(colValue);
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
    
    private void setNumberOfRecordsUpdated(int i) {
        this.numberOfRecordsUpdated = Integer.toString(i);
    }

    /**
     * @return
     */
    public String getNumberOfRecordsUpdated() {
        return numberOfRecordsUpdated;
    }

}
