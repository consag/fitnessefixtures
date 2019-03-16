/*
 * The input parameters are provided by a Script table in the FitNesse wiki.
 * @author Jac. Beekers
 * @version 4 August 2017
*/
package nl.consag.testautomation.database;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.supporting.Utilities;

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class InsertData {

    private String className = "InsertData";
    private static String version = "20180820.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    private String databaseType;
    private String databaseName;
    private String connectAs = Constants.DBCONN_ASUSER;

    private String tableName;
    private String ignore0Records = Constants.NO;
    private String numberOfRecordsInserted = Constants.NONE;

    private List<String> columnList = new ArrayList<String>();
    private String columnListAsString = Constants.NOT_INITIALIZED;
    private String valuesAsString = Constants.NOT_INITIALIZED;

    private String errorMessage = Constants.NOERRORS;
    private String errorCode = Constants.OK;

    public InsertData() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public InsertData(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + context;
        setContext(context);
    }

    public void setContext(String context) {
        this.context = context;
    }
    public String getContext() {
        return this.context;
    }

    public List<List<String>> doTable(List<List<String>> table) {
        readParameterFile();

        List<List<String>> resultTable = new ArrayList<List<String>>();
        int nrRows=0;
        String theKeyword = Constants.NOT_FOUND;

        for (List<String>row : table) {
            nrRows++;
            theKeyword =rowProcess(row, nrRows);
            if(!Constants.NOT_FOUND.equals(theKeyword)) {
                List<String> keywordRow = new ArrayList<String>();
                resultTable.add(keywordRow);
            } else {
                resultTable.add(rowResult(row, nrRows));
            }

        }
        addLogInfo(resultTable);

        return resultTable;
    }
    private void addLogInfo(List<List<String>> resultTable) {
        List<String> logRow = new ArrayList<String>();
        logRow.add(Constants.FITNESSE_PREFIX_REPORT + "log file");
        logRow.add(Constants.FITNESSE_PREFIX_REPORT + getLogFilename());
        resultTable.add(logRow);

    }
    private String rowProcess(List<String> row, int rowNr) {
        String myName="rowProcess";
        String myArea="init";
        String logMessage= Constants.NOT_INITIALIZED;
        String theKeyword =Constants.NOT_FOUND;

        logMessage="Processing row# >" + rowNr +"<. Row has >" + row.size() + "< entries.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        myArea="checkForKeyword";
        theKeyword =Utilities.checkForKeyword(row, Constants.DBFIXTURE_KEYWORDLIST);
        if(Constants.NOT_FOUND.equals(theKeyword)) {
            myArea="insertDatabaseRow";
//            setValuesAsString(Utilities.convertListToString(row, ",", Constants.DBFIXTURE_KEYWORDLIST));
            insertDatabaseRow(row);
            logMessage="insertDatabaseRow returned >" + geterrorCode() +"< with message >" + getErrorMessage() +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
        } else {
            switch(theKeyword) {
                case Constants.LOGLEVEL:
                        setLogLevel(row.get(1));
                        break;
                case Constants.DATABASE_NAME:
                    setDatabaseName(row.get(1));
                    break;
                case Constants.CONNECT_AS:
                    setConnectAs(row.get(1));
                    break;
                case Constants.TABLE_NAME:
                    setTableName(row.get(1));
                    break;
                case Constants.COLUMN_NAME:
                case Constants.COLUMN_NAMES:
                    setColumnListAsString(Utilities.convertListToString(row, ",", Constants.DBFIXTURE_KEYWORDLIST));
                    setColumnList(row);
                    break;
            }
        }

    return theKeyword;
    }

    private void setColumnList(List<String> columnList) {
        this.columnList =columnList;
    }
    public List<String> getColumnList() {
        return this.columnList;
    }

    private String insertDatabaseRow(List<String> row) {
        List<String> rowForInsert = new ArrayList<String>();
        rowForInsert.addAll(row);
        rowForInsert.remove(0);

        BasicInsert insert = new BasicInsert(getContext());
        insert.setLogLevel(getLogLevel());
        insert.setLogFileName(getLogFileNameOnly());
        insert.setDatabaseName(getDatabaseName());
        insert.setConnectAs(getConnectAs());
        insert.setTableName(getTableName());
        insert.setColumnList(getColumnListAsString());
        if(insert.setColumnDataTypes(getColumnList())) {
            insert.setValuesRow(rowForInsert);
            insert.result(Constants.OK);
        }
        setErrorMessage(insert.getErrorLevel(), insert.getErrorMessage());

        return geterrorCode();

    }

    public String getConnectAs() {
        return this.connectAs;
    }
    public void setConnectAs(String connectAs) {
        this.connectAs = connectAs;
    }

    private List<String> rowResult(List<String> row, int rowNr) {
        String myName="rowResult";
        String myArea="run";
        String logMessage= Constants.NOT_INITIALIZED;
        List<String> outputRow = new ArrayList<String>();

        if(Constants.NOT_FOUND.equals(Utilities.checkForKeyword(row, Constants.DBFIXTURE_KEYWORDLIST))) {
            logMessage="Row# >" +rowNr + "< has result >" + getErrorMessage() + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);

            switch(geterrorCode()) {
                case Constants.OK:
                    outputRow.add(Constants.FITNESSE_PREFIX_PASS +Constants.OK);
                    log(myName, Constants.INFO, myArea, logMessage);
                    break;
                case Constants.ERROR:
                    outputRow.add((Constants.FITNESSE_PREFIX_FAIL) +Constants.ERROR +" : " + getErrorMessage());
                    log(myName, Constants.ERROR, myArea, logMessage);
                    break;
                default:
                    outputRow.add(Constants.FITNESSE_PREFIX_FAIL +Constants.FAILED);
                    log(myName, Constants.ERROR, myArea, logMessage);
            }
        } else {
            log(myName, Constants.DEBUG, myArea, "Row had keyword. Not processed for data insertion.");
        }

        return outputRow;

    }

    private String getColumnListAsString() {
        return columnListAsString;
    }
    private void setColumnListAsString(String columnListAsString) {
        this.columnListAsString = columnListAsString;
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String propVal = Constants.NOT_INITIALIZED;

        setLogUrl(GetParameters.GetLogUrl());

    }

    public String getClassName() {
        return this.className;
    }

    public void setLogUrl(String logUrl){
        String myName = "setLogUrl";
        String myArea = "determine";
        String logMessage = Constants.NOT_INITIALIZED;
        if(Constants.NOT_FOUND.equals(logUrl)) {
            logMessage ="Properties file does not contain LogURL value.";
            log(myName, Constants.WARNING, myArea, logMessage);
        } else {
            this.logUrl = logUrl;
        }
        logMessage ="logUrl has been set to >" + this.logUrl +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

    }

    private void log(String name, String level, String area, String logMessage) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }

        if (firstTime) {
            firstTime = false;
            Logging.LogEntry(logFileName, getClassName() +"-" +name, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(logFileName, getClassName() + "-" + name, level, area, logMessage);
    }

    public String getLogFilename() {
        if(getLogUrl().startsWith("http"))
            return "<a href=\"" +getLogUrl() + this.logFileName +".log\" target=\"_blank\">" + this.logFileName + "</a>";
        else
            return getLogUrl() + this.logFileName + ".log";
    }
    public String getLogFileNameOnly() {
        return this.logFileName;
    }

    public String getLogUrl() {
        return this.logUrl;
    }

    public static String getVersion() {
        return version;
    }
    /**
     * @param level to which logging should be set. Must be VERBOSE, DEBUG, INFO, WARNING, ERROR or FATAL. Defaults to INFO.
     */

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
        seterrorCode(level);
    }

    /**
     * @return - the error message (if any). If not "No errors encountered"  is returned (Check Constants for the actual value)
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param valuesAsString:
     *
     *
    private void setValuesAsString(String valuesAsString) {
        String myName = "setValuesAsString";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;
        this.valuesAsString = valuesAsString;

        logMessage="Column values are set to >" + this.valuesAsString + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }
*/
    /**
     * @return - The value to which the modifyColumn should be changed (or changed)
     */
    private String getValuesAsString() {
        return this.valuesAsString;
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

    private void seterrorCode(String level) {
        String myName = "seterrorCode";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorCode = level;
        logMessage="Error level has been set to >" + this.errorCode + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return
     */
    public String geterrorCode() {
        return errorCode;
    }

    private void setNumberOfRecordsInserted(int i) {
        this.numberOfRecordsInserted = Integer.toString(i);
    }

    /**
     * @return
     */
    public String getNumberOfRecordsInserted() {
        return numberOfRecordsInserted;
    }

}
