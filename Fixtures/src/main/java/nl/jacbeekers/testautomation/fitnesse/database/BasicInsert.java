/*
 * The input parameters are provided by a Script table in the FitNesse wiki.
 * @author Jac. Beekers
 * @version 4 August 2017
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;
import nl.jacbeekers.testautomation.fitnesse.supporting.Utilities;

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BasicInsert {

    private static String version = "20180822.1";
    ConnectionProperties connectionProperties = new ConnectionProperties();
    private String className = "BasicInsert";
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
    private String ignore0Records = Constants.NO;

    private String columnList = Constants.NOT_PROVIDED;
    private String valuesAsString = Constants.NOT_PROVIDED;
    private int colnr = 0;
    private String dateFormat = Constants.DEFAULT_DATE_FORMAT;
    private String timestampFormat = Constants.DEFAULT_TIMESTAMP_FORMAT;

    private boolean dataTypesDetermined = false;
    private List<String> columns = new ArrayList<String>();
    private List<List<String>> colDataType = new ArrayList<List<String>>();

    private String databaseDateFormat = Constants.DATABASE_DEFAULT_DATE_FORMAT;
    private String databaseTimestampFormat = Constants.DATABASE_DEFAULT_TIMESTAMP_FORMAT;

    private String numberOfRecordsInserted = Constants.NONE;

    private String errorMessage = Constants.NOERRORS;
    private String errorLevel = Constants.OK;


    public BasicInsert() {
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
    public BasicInsert(String context) {
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

    public String getDatabaseDateFormat() {
        return databaseDateFormat;
    }

    private void setDatabaseDateFormat(String databaseDateFormat) {
        this.databaseDateFormat = databaseDateFormat;
    }

    public String getDatabaseTimestampFormat() {
        return databaseTimestampFormat;
    }

    private void setDatabaseTimestampFormat(String databaseTimestampFormat) {
        this.databaseTimestampFormat = databaseTimestampFormat;
    }

    public String getClassName() {
        return this.className;
    }

    private int getColnr() {
        return this.colnr;
    }

    private void setColnr(int i) {
        colnr = i;
    }

    //auto-called by fitnesse for each row
    public void reset() {
        setDatabaseName(Constants.NOT_PROVIDED);
        setTableName((Constants.NOT_PROVIDED));
        setColumnList("");
        setValues("");
        setNumberOfRecordsInserted(0);
        setColnr(0);
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
                setColnr(getColnr() + 1);
                if (getColnr() == 1) {
                    setColumnList(col);
                    setValues(val);
                } else {
                    setColumnList(getColumnList() + "," + col);
                    setValues(getValues() + "," + val);
                }
        }

    }

    public String get(String whatToGet) {
        String val = Constants.NOT_INITIALIZED;
        whatToGet = whatToGet.toLowerCase().replaceAll("\\s", "");

        switch (whatToGet) {
            case "result":
                if (result()) {
                    val = Constants.OK;
                } else {
                    val = Constants.ERROR;
                }
                break;
            case "logfile":
                val = getLogFilename();
                break;
            case "errormessage":
                val = getErrorMessage();
                break;
            case "numberofrecordsinserted":
                val = getNumberOfRecordsInserted();
                break;
            default:
                val = Constants.UNKNOWN;
                break;
        }
        return val;
    }

    public boolean result() {
        return result(Constants.OK);
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
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());
        log(myName, Constants.DEBUG, myArea, "connectionProperties-dbType is >" + connectionProperties.getDatabaseType() + "<.");
        setDatabaseType(connectionProperties.getDatabaseType());

        submitInsertStatement();

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

    private String determineDataType(String databaseType, String database, String table, String column) {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "determineDataType";
        String myArea = "run";

        String result = Constants.NOT_FOUND;
        String sqlStatement = Constants.NOT_INITIALIZED;
        String upperTable = table.toUpperCase();
        String upperColumn = column.toUpperCase();

        switch (databaseType) {
            case Constants.DATABASETYPE_ORACLE:
                sqlStatement = "SELECT data_type FROM user_tab_columns "
                        + " WHERE table_name ='" + upperTable + "'"
                        + "   AND column_name ='" + upperColumn + "'";
                break;
            case Constants.DATABASETYPE_DB2:  // DB2 z/OS
                sqlStatement = "SELECT coltype FROM SYSIBM.SYSCOLUMNS "
                        + " WHERE tbname ='" + upperTable + "'"
                        + "   AND name ='" + upperColumn + "'"
                        + "   AND tbcreator =current_schema"
                        + " with ur";
                break;
            default:
                result = Constants.NOT_FOUND;
                return result;
        }
        logMessage = "SQL Statement to retrieve datatype is >" + sqlStatement + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        GetSingleValue getSingleValue = new GetSingleValue(getContext());
        getSingleValue.setLogLevel(getLogLevel());
        getSingleValue.setDatabaseName(database);
        getSingleValue.setConnectAs(Constants.DBCONN_ASOWNER);
        getSingleValue.setQuery(sqlStatement);
        result = getSingleValue.getColumn();
        result=result.trim();

        log(myName, Constants.DEBUG, myArea, "Colum data type determined as >" + result + "<.");


        return result;
    }

    //----------------------------------------------------------
    //Function to submit statement based on input in fitnesse table row >=3
    //----------------------------------------------------------
    private String submitInsertStatement() {
        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "submitInsertStatement";
        String myArea = "init";

        Connection connection = null;
        Statement statement = null;
        int nrRecords = 0;
        String updateString = Constants.NOT_INITIALIZED;

        try {
            updateString =
                    "INSERT INTO " + tableName + " ( " + getColumnList() + " ) VALUES ( " + getValues() + " )";

            logMessage = "SQL: " + updateString;
            log(myName, Constants.INFO, myArea, logMessage);
            // Create a connection to the database
            if (Constants.DBCONN_ASOWNER.equals(getConnectAs())) {
                connection = connectionProperties.getOwnerConnection();
            } else { // connect as user
                connection = connectionProperties.getUserConnection();
            }

            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();

            nrRecords = statement.executeUpdate(updateString);
            setNumberOfRecordsInserted(nrRecords);

            if (nrRecords == 0) {
                if (getIgnore0Records().equals(Constants.NO)) {
                    setErrorMessage(Constants.ERROR, "Insert statement did not insert any records while Ignore0Records had been set to >" + getIgnore0Records() + "<.");
                } else {
                    logMessage = "Insert returned >0<. OK as Ignore0Records had been set to >" + getIgnore0Records() + "<.";
                    log(myName, Constants.INFO, myArea, logMessage);
                }
            } else {
                logMessage = "Insert statement resulted in >" + getNumberOfRecordsInserted() + "< record(s) inserted.";
                log(myName, Constants.INFO, myArea, logMessage);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            myArea = "exception handling";
            logMessage = "An error occurred executing statement >" + updateString + "<. SQLException: " + e.toString(); // return "fail: SQLException : " + e;
            setErrorMessage(Constants.ERROR, logMessage);
            log(myName, Constants.ERROR, myArea, logMessage);

        }

        logMessage = "Process completed with error level >" + getErrorLevel() + "<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return getErrorLevel();
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String propVal = Constants.NOT_FOUND;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + databaseName + "<.");
        connectionProperties.setLogFileName(getLogFileNameOnly());
        connectionProperties.setLogLevel(getIntLogLevel());
        connectionProperties.setDatabaseName(databaseName);
        connectionProperties.refreshConnectionProperties(databaseName);
        setDatabaseType(connectionProperties.getDatabaseType());

        setLogUrl(GetParameters.GetLogUrl());

        propVal = GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getClassName(), Constants.PARAM_DATE_FORMAT);
        if (Constants.NOT_FOUND.equals(propVal)) {
            log(myName, Constants.VERBOSE, myArea, "Parameter >" + Constants.PARAM_DATE_FORMAT
                    + "< not found in parameter file >" + Constants.FIXTURE_PROPERTIES + "<. Using default >"
                    + Constants.DEFAULT_DATE_FORMAT + "<.");
            propVal = Constants.DEFAULT_DATE_FORMAT;
        }
        setDateFormat(propVal);
        log(myName, Constants.DEBUG, myArea, "dateFormat set to >" + getDateFormat() + "<.");

        propVal = GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getClassName(), Constants.PARAM_TIMESTAMP_FORMAT);
        if (Constants.NOT_FOUND.equals(propVal)) {
            log(myName, Constants.VERBOSE, myArea, "Parameter >" + Constants.PARAM_TIMESTAMP_FORMAT
                    + "< not found in parameter file >" + Constants.FIXTURE_PROPERTIES + "<. Using default >"
                    + Constants.DEFAULT_TIMESTAMP_FORMAT + "<.");
            propVal = Constants.DEFAULT_TIMESTAMP_FORMAT;
        }
        setTimestampFormat(propVal);
        log(myName, Constants.DEBUG, myArea, "timestampFormat set to >" + getTimestampFormat() + "<.");

        propVal = GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getClassName(), Constants.PARAM_DATABASE_DATE_FORMAT);
        if (Constants.NOT_FOUND.equals(propVal)) {
            log(myName, Constants.VERBOSE, myArea, "Parameter >" + Constants.PARAM_DATABASE_DATE_FORMAT
                    + "< not found in parameter file >" + Constants.FIXTURE_PROPERTIES + "<. Using default >"
                    + Constants.DATABASE_DEFAULT_DATE_FORMAT + "<.");
            propVal = Constants.DATABASE_DEFAULT_DATE_FORMAT;
        }
        setDatabaseDateFormat(propVal);
        log(myName, Constants.DEBUG, myArea, "dateFormat set to >" + getDateFormat() + "<.");

        propVal = GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getClassName(), Constants.PARAM_DATABASE_TIMESTAMP_FORMAT);
        if (Constants.NOT_FOUND.equals(propVal)) {
            log(myName, Constants.VERBOSE, myArea, "Parameter >" + Constants.PARAM_DATABASE_TIMESTAMP_FORMAT
                    + "< not found in parameter file >" + Constants.FIXTURE_PROPERTIES + "<. Using default >"
                    + Constants.DATABASE_DEFAULT_TIMESTAMP_FORMAT + "<.");
            propVal = Constants.DATABASE_DEFAULT_TIMESTAMP_FORMAT;
        }
        setDatabaseTimestampFormat(propVal);
        log(myName, Constants.DEBUG, myArea, "timestampFormat set to >" + getTimestampFormat() + "<.");

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
            logMessage = "Ignore 0 Records has been set to >" + this.ignore0Records + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
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

    /**
     * @return - the columns used by the insert statement
     */
    public String getColumnList() {
        return this.columnList;
    }

    /**
     * @param columnList: The column list to be used
     */
    public void setColumnList(String columnList) {
        String myName = "setColumnList";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.columnList = columnList;

        logMessage = "Column list has been set to >" + this.columnList + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    public boolean getDataTypesDetermined() {
        return this.dataTypesDetermined;
    }

    private void setDataTypesDetermined(boolean done) {
        this.dataTypesDetermined = done;
    }

    public boolean setColumnDataTypes(List<String> columnNames) {
        String myName = "setColumnDataTypes";
        String myArea = "run";
        String logMessage = Constants.NOT_INITIALIZED;
        int cntColumn = 0;
        String dataType = Constants.NOT_FOUND;
        boolean result = true;

        /*
        The fixture can contain multiple records, but the column data types only have to be determined once
        The fixture cannot contain inserts for multiple tables
         */
        if (getDataTypesDetermined()) {
            return true;
        }

        readParameterFile();
        log(myName, Constants.VERBOSE, myArea, "Setting logFileName for connectionProperties to >" + logFileName + "<.");
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());
        log(myName, Constants.DEBUG, myArea, "connectionProperties reports dbType >" + connectionProperties.getDatabaseType() + "<.");
        setDatabaseType(connectionProperties.getDatabaseType());

        if (getDatabaseType() == null) {
            logMessage = "DatabaseType is empty. Check properties file.";
            setErrorMessage(Constants.ERROR, logMessage);
            log(myName, Constants.ERROR, myArea, logMessage);
            return false;
        }
        logMessage = "dbType >" + getDatabaseType() + "<, dbName >" + getDatabaseName() + "<"
                + ", table >" + getTableName() + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        myArea = "determineColDataTypes";
        for (cntColumn = 1; cntColumn < columnNames.size(); cntColumn++) {
            log(myName, Constants.DEBUG, myArea, "Column >" + Integer.toString(cntColumn) + "< named >"
                    + columnNames.get(cntColumn) + "<.");
            dataType = determineDataType(getDatabaseType(), getDatabaseName(), getTableName(), columnNames.get(cntColumn));
            if (Constants.NOT_FOUND.equals(dataType) || Constants.ERROR.equals(dataType)
                    || "0".equals(dataType)) {
                logMessage = "Column >" + columnNames.get(cntColumn) + "< not found in table >" + getTableName()
                        + "< in database >" + getDatabaseName() + "<.";
                log(myName, Constants.ERROR, myArea, logMessage);
                setErrorMessage(Constants.ERROR, logMessage);
            }
            addColumn(columnNames.get(cntColumn));
            addColDataType(columnNames.get(cntColumn), dataType.trim());
        }
        setDataTypesDetermined(result);
        return result;
    }

    private void addColumn(String colName) {
        this.columns.add(colName);
    }

    private String getColumn(int i) {
        return this.columns.get(i);
    }

    private void addColDataType(String colName, String colDataType) {
        List<String> entry = new ArrayList<String>();
        entry.add(colName);
        entry.add(colDataType);
        this.colDataType.add(entry);
    }

    private String getColDataType(String colName) {
        String myName ="getColDataType";
        String myArea ="run";

        log(myName, Constants.VERBOSE, myArea, "colDataType array >" + colDataType.toString());
        for (int i=0; i < colDataType.size() ; i++) {
            if(colDataType.get(i).get(0).equals(colName)) {
                return colDataType.get(i).get(1);
            }
        }
            return Constants.ERROR;
    }

    public void setValuesRow(List<String> valuesRow) {
        String myName = "setValuesRow";
        String myArea = "run";
        String logMessage = Constants.NOT_INITIALIZED;
        String valueList = Constants.NOT_INITIALIZED;
        String providedColumnValue = Constants.NOT_PROVIDED;
        String useColumnValue = Constants.NOT_INITIALIZED;
        int cntValue = 0;
        String dataType = Constants.NOT_INITIALIZED;

//        readParameterFile();

        Utilities utilities = new Utilities();
        myArea = "readParameterFile";
        readParameterFile();
        log(myName, Constants.VERBOSE, myArea, "Setting logFileName for connectionProperties to >" + logFileName + "<.");
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());
        setDatabaseType(connectionProperties.getDatabaseType());

        for (cntValue = 0; cntValue < valuesRow.size(); cntValue++) {
            providedColumnValue = valuesRow.get(cntValue);
            useColumnValue = providedColumnValue;
            myArea = "DatatypeCheck";
            log(myName, Constants.DEBUG, myArea,"Checking value >" + providedColumnValue +"<.");

            if(Utilities.checkForFunction(providedColumnValue)){
                log(myName, Constants.DEBUG, myArea,"Value >" +providedColumnValue +"< contains function. Data type processing n/a.");
            } else {

                dataType = getColDataType(getColumn(cntValue));
                log(myName, Constants.DEBUG, myArea, "datatype of column >" + Integer.toString(cntValue)
                        + "<, name >" + getColumn(cntValue) + "<, with value >" + useColumnValue + "< is >" + dataType + "<.");
                switch (dataType) {
                    case Constants.COLUMN_DATATYPE_DECIMAL:
                    case Constants.COLUMN_DATATYPE_NUMBER:
                        if (useColumnValue.isEmpty()) {
                            useColumnValue = "NULL";
                        }
                        break;
                    case Constants.COLUMN_DATATYPE_DATE:
                        if (useColumnValue.isEmpty()) {
                            useColumnValue = "NULL";
                        } else {
                            useColumnValue = utilities.convertStringToTimestampFunction(useColumnValue, getDatabaseDateFormat(), getDatabaseType());
                        }
                        break;
                    case Constants.COLUMN_DATATYPE_TIMESTMP: // DB2 z/OS
                    case Constants.COLUMN_DATATYPE_TIMESTAMP:
                    case Constants.COLUMN_DATATYPE_TIMESTAMP6:
                    case Constants.COLUMN_DATATYPE_TIMESTAMP9:
                        if (useColumnValue.isEmpty()) {
                            useColumnValue = "NULL";
                        } else {
                            useColumnValue = Utilities.convertStringToTimestampFunction(useColumnValue, getDatabaseTimestampFormat(), getDatabaseType());
                        }
                        break;
                    case Constants.COLUMN_DATATYPE_VARCHAR2:
                    case Constants.COLUMN_DATATYPE_VARCHAR:
                    case Constants.COLUMN_DATATYPE_CHAR:
                        if (useColumnValue.isEmpty()) {
                            useColumnValue = "NULL";
                        } else {
                            useColumnValue = "'" + useColumnValue + "'";
                        }
                        break;
                    default:
                        break;
                }
            }
            log(myName, Constants.DEBUG, myArea, "Column transformation result >" + useColumnValue + "<");

            if (cntValue == 0) {
                valueList = useColumnValue;
            } else {
                valueList += "," + useColumnValue;
            }
        }
        setValuesAsString(valueList);

        myArea = "Conclusion";
        logMessage = "Column values are set to >" + getValuesAsString() + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    /**
     * @return - The values used by the insert statement
     */
    public String getValuesAsString() {
        return valuesAsString;
    }

    public void setValuesAsString(String valuesAsString) {
        this.valuesAsString = valuesAsString;
    }

    public String getValues() {
        return getValuesAsString();
    }

    /**
     * @param values: The values for the columns in the insert statement
     */
    public void setValues(String values) {
        String myName = "setValues";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.valuesAsString = values;

        logMessage = "Column values are set to >" + this.valuesAsString + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

    String getDateFormat() {
        return this.dateFormat;
    }

    void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    String getTimestampFormat() {
        return this.timestampFormat;
    }

    void setTimestampFormat(String format) {
        this.timestampFormat = format;
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
        setColumnList(colName);
    }

    /**
     * @param colValue
     */
    public void values(String colValue) {
        setValues(colValue);
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

    /**
     * @return
     */
    public String getNumberOfRecordsInserted() {
        return numberOfRecordsInserted;
    }

    private void setNumberOfRecordsInserted(int i) {
        this.numberOfRecordsInserted = Integer.toString(i);
    }

}
