/**
 * The purpose of this fixture is to compare the expected outcome of a database query with the actual outcome using the FitNesse slim 'table' table.
 * The input parameters are provided by a table in the FitNesse wiki.
 *
 * @author Jac. Beekers
 * @version 20161111.0 #- Added some comments for java docs
 * @since 10 May 2015
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;

import java.sql.*;
import java.text.*;
import java.util.*;

import nl.jacbeekers.testautomation.fitnesse.supporting.*;
import nl.jacbeekers.testautomation.fitnesse.linux.CheckFile;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.*;

public class BasicQuery {
    private String className = BasicQuery.class.getName()
            .substring(BasicQuery.class.getName().lastIndexOf(".") + 1);

    private static String version = "20200514.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl = Constants.LOG_DIR;
    private boolean logFileNameAlreadySet =false;
    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;
    private String tableName;
    private String query;
    private int rowUnequalValues = 0;
    private int rowEqualValues = 0;
    private int NO_FITNESSE_ROWS_TO_SKIP = 3;
    private int nrRecordsFound = 0;

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
        this.context = className;
        logFileName = startDate + "." + className;

    }

    /**
     * @param context
     */
    public BasicQuery(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className + "." + context;

    }

    /**
     * @param context
     * @param logLevel
     */
    public BasicQuery(String context, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className + "." + context;
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
        String myName = "doTable";
        String myArea = "Initialization";

        logMessage = "number of rows in select FitNesse table: " + Integer.toString(inputTable.size());
        log(myName, Constants.DEBUG, myArea, logMessage);
        numberOfTableColumns = inputTable.get(2).size();
        logMessage = "number of columns in FitNesse table: " + Integer.toString(numberOfTableColumns);
        log(myName, Constants.DEBUG, myArea, logMessage);

        retrieveDatabaseName(inputTable.get(0)); //read first row in FitNesse table
        readParameterFile();
        setQuery(getQueryFromTable(inputTable.get(1)));         //read second row in FitNesse table
        boolean preCheckResult = true;
        if (checkQuery(getQuery())) {
            addRowToReturnTable(inputTable.get(1));
        } else {
            log(myName, Constants.ERROR, myArea, "Query is invalid or not allowed. "
                    + ERRCODE_NOTALLOWED_QUERYSQL + ": " + getQuery() );
            setError(ERRCODE_NOTALLOWED_QUERYSQL, getMessage(ERRCODE_NOTALLOWED_QUERYSQL));
            List<String> addRow = new ArrayList<String>();
            addRow.add("fail:" + ERRCODE_NOTALLOWED_QUERYSQL);
            addRow.add("fail:" + getErrorMessage());
            addRowToReturnTable(addRow);
            addRow = new ArrayList<String>();
            addRow = inputTable.get(1);
            addRow.add(0, "Fail");
            addRowToReturnTable(addRow);
            preCheckResult = false;
        }

        if(preCheckResult) {
            getColumnNames(inputTable.get(2));  //read third row in FitNesse table

            List<String> empty_row = new ArrayList<String>();
            for (int i = 0; i < numberOfTableColumns; ++i) {    // fill empty row with spaces
                empty_row.add("");
            }

            String result = Constants.OK;
            List<List<String>> dbResult = getDatabaseTable(true);
            if (dbResult == null || Constants.ERROR.equals(getResult())) {
                for (int i = 0; i < (inputTable.size() - NO_FITNESSE_ROWS_TO_SKIP); ++i) {    // less rows in database than expected
                    addRowToReturnTable(empty_row);
                }
                List<String> addRow = new ArrayList<String>();
                addRow.add("fail:" + "SQL Error");
                addRow.add("fail:" + getErrorMessage());
                addRowToReturnTable(addRow);
                setResult(ERRCODE_SQLERROR, getErrorMessage());
            } else {
                result = CompareExpectedTableWithDatabaseTable(inputTable, dbResult);
                setResult(result);
            }
        }

        List<String> addRow = new ArrayList<String>();
        addRow.add("report:log file");
        addRow.add("report:" + getLogFilename());
        addRowToReturnTable(addRow);
        return returnTable;
    }

    /**
     * @return How many column values in row are correct
     */
    public String columnsOk() {
        // returns rowEqualValues
        returnMessage = Integer.toString(rowEqualValues);
        return returnMessage;
    }

    /**
     * @return How many column values are incorrect
     */
    public String columnsNotOk() {
        // returns rowUnequalValues
        returnMessage = Integer.toString(rowUnequalValues);
        return returnMessage;
    }

    /**
     * @param inputRow
     */
    private void retrieveDatabaseName(List<String> inputRow) {
        //Function to read first row of table and set database name
        String myName = "retreiveDatabaseName";
        String myArea = "Initialization";
        String logMessage = Constants.NOT_INITIALIZED;

        List<String> return_row = new ArrayList<String>();
        setDatabaseName(inputRow.get(1)); //read first row second column
        logMessage = "database name: " + getDatabaseName();
        log(myName, Constants.VERBOSE, myArea, logMessage);

        addRowToReturnTable(return_row);
    }

    /**
     * @param inputRow
     */
    private String getQueryFromTable(List<String> inputRow) {
        //Function to read second row of table and set database table name
        String myName = "getQuery";
        String myArea = "Initialization";
        String logMessage = Constants.NOT_INITIALIZED;
        List<String> return_row = new ArrayList<String>();

        String theQuery = inputRow.get(1); //read first row second column
        logMessage = "Query: " + theQuery;
        log(myName, Constants.DEBUG, myArea, logMessage);

        return theQuery;
    }

    private boolean checkQuery(String query) {

        String cleansed = query.replaceAll("[\\p{Ps}\\p{Pe}]", "").toUpperCase();
        if(cleansed.indexOf("SELECT") >= 0 || cleansed.indexOf("WITH") >= 0 ) {
            return true;
        }
        return false;
    }

    /**
     * @param inputRow
     */
    private void getColumnNames(List<String> inputRow) {
        //Function to read third row of table and set column names
        String myName = "getColumnNames";
        String myArea = "Initialization";
        String logMessage = Constants.NOT_INITIALIZED;

        List<String> return_row = new ArrayList<String>();

        for (int i = 1; i < inputRow.size(); ++ i) {    // next column names
            if (i == 1) {
                concatenatedColumnNames = inputRow.get(i); //first column name
            } else {
                concatenatedColumnNames = concatenatedColumnNames + "," + inputRow.get(i);
            }
        }
        myArea = "concatenated";
        logMessage = "column names: " + concatenatedColumnNames;
        log(myName, Constants.DEBUG, myArea, logMessage);

        addRowToReturnTable(return_row); //return row with outcomes; pass/fail
    }

    private String CompareExpectedTableWithDatabaseTable(List<List<String>> expected_table, List<List<String>> databaseTable) {
        //Function to compare input table with database table
        String myName = "CompareExpectedTableWithDatabaseTable";
        String myArea = "Start";
        String logMessage = Constants.NOT_INITIALIZED;

        List<String> empty_row = new ArrayList<String>();
        for (int i = 0; i < numberOfTableColumns; ++ i) {    // fill empty row with spaces
            empty_row.add("no record");
        }
        logMessage = "expected #rows=>" + Integer.toString(expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP) + "<. Database #rows found=>" + Integer.toString(databaseTable.size()) + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        myArea = "Comparing input with db";
        String overallResult = Constants.OK;
        if ((expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP) >= databaseTable.size()) {
            logMessage = "more or equal number of rows expected compared to database rows.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            myArea = "Processing expectedrows>=dbrows";
            for (int i = 0; i < (expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP); ++ i) {    // less rows in database than expected
                String rc = Constants.OK;
                if (i < databaseTable.size()) {
                    rc = CompareExpectedRowWithDatabaseRow(expected_table.get(i + NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
                } else {
                    rc = Constants.ERROR; // too many rows
                    CompareExpectedRowWithDatabaseRow(expected_table.get(i + NO_FITNESSE_ROWS_TO_SKIP), empty_row);
                }
                if(!Constants.OK.equals(rc)) {
                    overallResult = rc;
                }
            }
        } else {
            overallResult = Constants.ERROR; // too may rows in db result
            logMessage = "more number of rows in db than expected.";
            log(myName, "debug", myArea, logMessage);
            myArea = "Processing dbrows>expectedrows";
            for (int i = 0; i < databaseTable.size(); ++ i) {    // more rows in database than expected
                if (i < (expected_table.size() - NO_FITNESSE_ROWS_TO_SKIP)) {
                    logMessage = "Processing expected row# >" + Integer.toString(i) + "<.";
                    log(myName, Constants.DEBUG, myArea, logMessage);
                    CompareExpectedRowWithDatabaseRow(expected_table.get(i + NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
                } else {
                    logMessage = "Processing db surplus row# >" + Integer.toString(i) + "<.";
                    log(myName, Constants.DEBUG, myArea, logMessage);
                    CompareExpectedRowWithDatabaseRow(empty_row, databaseTable.get(i));
                }
            }
        }
        return overallResult;
    }

    private String CompareExpectedRowWithDatabaseRow(List<String> expected_row, List<String> database_row) {
        //Function to compare input row with database row
        List<String> return_row = new ArrayList<String>();
        String myName = "CompareExpectedRowWithDatabaseRow";
        String myArea = "Initialization";
        String logMessage = Constants.NOT_INITIALIZED;
        rowEqualValues = 0;
        rowUnequalValues = 0;

        for (int i = 1; i < numberOfTableColumns; ++ i) {
            //Compare cell for cell if expected equals outcome
            if ((expected_row.get(i) != null &&    // JVA KLANTEB 2185 Check for NULL or empty string
                    (database_row.get(i) != null) || ! database_row.get(i).isEmpty())) {
                if (expected_row.get(i).equals(database_row.get(i))) {
                    return_row.add("pass"); //return "pass" in next cell if expected = result
                    rowEqualValues++;
                } else {
                    return_row.add("fail:expected: >" + expected_row.get(i) + "< found: >" + database_row.get(i) + "<."); //return "fail" in next cell with the found value
                    rowUnequalValues++;
                }
            } else {
                if ((expected_row.get(i) == null &&    // JVA KLANTEB 2185 Check for NULL or empty string
                        (database_row.get(i) == null) || database_row.get(i).isEmpty())) {
                    return_row.add("pass"); //return "pass" in next cell if expected = result
                    rowEqualValues++;
                } else {
                    return_row.add("fail:expected: >" + expected_row.get(i) + "< found: >" + database_row.get(i) + "<."); //return "fail" in next cell with the found value
                    rowUnequalValues++;
                }
            }
        }

        myArea = "db record read";
        logMessage = "equal: " + rowEqualValues + " unequal: " + rowUnequalValues;
        log(myName, Constants.INFO, myArea, logMessage);
        logMessage = "return row >" + return_row + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        if (expected_row.get(0).equals("column values")) {  // remain backward compatible: column 0 contains "column values" in previous version
            return_row.add(0, "Pass");
        } else {
            if (rowUnequalValues > 0) {
                return_row.add(0, "Fail:expected: >" + expected_row.get(0) + "< got: >" + rowEqualValues + "<.");
            } else {
                return_row.add(0, "Pass: " + rowEqualValues);
            }
        }

        addRowToReturnTable(return_row); //return row with outcomes; pass/fail
        if( rowUnequalValues > 0) {
            return Constants.ERROR;
        } else {
            return Constants.OK;
        }
    }

    public String countRecords() {
        String myName = "countRecords";
        String myArea = "init";
        query = "SELECT COUNT(*) nr_recs FROM " + getTableName();
        List<List<String>> result = getDatabaseTable(false, true);

        if (result != null && result.size() > 0)
            return result.get(0).get(0);
        else
            return ResultMessages.getMessage(ERRCODE_SQLERROR);

    }

    private List<List<String>> getDatabaseTable(boolean collectResultRows) {
        return getDatabaseTable(collectResultRows, false);
    }

    private List<List<String>> getDatabaseTable(boolean collectResultRows, boolean firstRowOnly) {
        String myName = "getDatabaseTable";
        String myArea = "Initialization";
        String logMessage = Constants.NOT_INITIALIZED;
        List<List<String>> databaseTable = new ArrayList<List<String>>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            myArea = "readParameterFile";
            readParameterFile();
            if(!Constants.OK.equals(getResult())) {
                log(myName, Constants.DEBUG, myArea, "An error occurred reading properties. See error above.");
                return null;
            }
            log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");
            connectionProperties.setLogFilename(logFileName);
            connectionProperties.setLogLevel(getIntLogLevel());
            connection = connectionProperties.getUserConnection();
            if(connection == null) {
                setError(connectionProperties.getErrorCode(), "Could not create connection. Error: " + connectionProperties.getErrorMessage());
                return null;
            }


            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to read database
            logMessage = "Query >" + query + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                if (collectResultRows) {
                    List<String> database_row = new ArrayList<String>(); // initialize list to be reused
                    database_row.add("column value"); //first cell will consist of a fixed value "column name"
                    //Add db result row (=multiple field) into fitnesse results array
                    for (int j = 1; j < numberOfTableColumns; ++ j) {
                        if (resultset.getString(j) == null) {
                            database_row.add(""); //string should be filled with empty spaces otherwise a java null exception is created
                        } else {
                            database_row.add(resultset.getString(j));
                        }
                    }
                    databaseTable.add(database_row);
                    nrRecordsFound++;
                    if (firstRowOnly && nrRecordsFound > 0) {
                        break;
                    }
                } else {
                    nrRecordsFound++;
                }
            }
            myArea = "db query completed";
            logMessage = "Number of database rows found: " + Integer.toString(databaseTable.size());
            log(myName, Constants.INFO, myArea, logMessage);

            statement.close();
            connection.close();
            setResult(Constants.OK, logMessage);
        } catch (SQLException e) {
            myArea = "exception handling";
            logMessage = "SQLException: " + e.toString();
            log(myName, Constants.ERROR, myArea, logMessage);
            setError(Constants.ERROR, logMessage);
            returnMessage = "SQLException : " + e.toString();
            databaseTable = null;
        }

        if (collectResultRows) {
            return databaseTable;
        } else {
            return null;
        }
    }

    private void addRowToReturnTable(List<String> row) {
        //Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
        returnTable.add(row);
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + getDatabaseName() + "<.");
        connectionProperties.setLogFilename(getLogFileNameOnly());
        connectionProperties.setLogLevel(getIntLogLevel());
        connectionProperties.setDatabaseName(getDatabaseName());
        connectionProperties.refreshConnectionProperties(getDatabaseName());
        if(!Constants.OK.equals(connectionProperties.getErrorCode())) {
            log(myName, Constants.ERROR, myArea, connectionProperties.getErrorMessage());
            setResult(connectionProperties.getErrorCode(), connectionProperties.getErrorMessage());
        }

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
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(logFileName, name, level, area, logMessage);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
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

    public void setLogLevel(String level) {
        String myName = "setLogLevel";
        String myArea = "determineLevel";

        logLevel = Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, Constants.WARNING, myArea, "Wrong log level >" + level + "< specified. Defaulting to level 3.");
            logLevel = 3;
        }

        log(myName, Constants.INFO, myArea, "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
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
        String myName = "setSqlFile";
        String myArea = "init";

        String dirAndFile = logicalDirAndFilename;
        String[] dirFileArray = dirAndFile.split("/", 2);
        if (dirFileArray.length != 2) {
            logMessage = "Invalid directory/filename >" + logicalDirAndFilename + "<. Must be able to split in two parts, separated by at least one /";
            log(myName, Constants.ERROR, myArea, logMessage);
            return;
        }
        String dir = dirFileArray[0];
        String fileName = dirFileArray[1];

        dirFileArray = dirAndFile.split(" ", 2);

        if (dirFileArray.length != 2) {
            logMessage = "Invalid directory/filename >" + logicalDirAndFilename + "<. Must be able to split in two parts, separated by one space";
            log(myName, Constants.ERROR, myArea, logMessage);
            return;
        }

        CheckFile checkSqlFile = new CheckFile(context, getLogLevel());
        sqlFileName = checkSqlFile.DetermineCompleteFileName(dir, fileName);

    }

    public String getSqlFile() {
        return sqlFileName;
    }

    public String queryExecution() {
        String logMessage = "init";
        String myName = "queryExecution";
        String myArea = "processing";
        log(myName, Constants.DEBUG, myArea, logMessage);

        String sqlStatement = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "reading parameter file...");
        readParameterFile();
        if(!Constants.OK.equals(getResult())) {
            log(myName, Constants.DEBUG, myArea, "An error occurred reading properties. See error above.");
            return getResult();
        }
        log(myName, Constants.DEBUG, myArea, "Done.");

        log(myName, Constants.DEBUG, myArea, "determining sql statement...");
        sqlStatement = determineSQLStatement();
        log(myName, Constants.DEBUG, myArea, "Done.");

        setQuery(sqlStatement);

        log(myName, Constants.DEBUG, myArea, "Getting database result...");
        getDatabaseTable(false);
        log(myName, Constants.DEBUG, myArea, "Done");
        log(myName, Constants.DEBUG, myArea, "Database returned >" + nrRecordsFound + "< records.");

        log(myName, Constants.DEBUG, myArea, "Process resulted in >" + getErrorMessage() + "<.");

        if (Constants.NOERRORS.equals(getErrorMessage())) {
            return Constants.OK;
        } else {
            return Constants.ERROR;
        }


    }

    private String determineSQLStatement() {

        String logMessage = Constants.NOT_INITIALIZED;
        String myName = "determineSQLStatement";
        String myArea = "init";
        String theLine = Constants.NOT_INITIALIZED;
        String sqlStatement = "/* BasicQuery - queryExecution */ ";

        try {
            BufferedReader ins = new BufferedReader(new FileReader(getSqlFile()));
            int rowCount = 0;
            myArea = "Reading file";
            try {
                while ((theLine = ins.readLine()) != null) {
                    ++ rowCount;
                    logMessage = "processing line >" + rowCount + "<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    sqlStatement = sqlStatement.concat(theLine);
                    sqlStatement = sqlStatement.concat(" ");
                }
            } catch (IOException e) {
                logMessage = "Error reading from file >" + getSqlFile() + "<. Error =>" + e.toString() + "<.";
                log(myName, Constants.ERROR, myArea, logMessage);
                setError(Constants.ERROR, logMessage);
                setResult(Constants.ERROR);
                try {
                    ins.close();
                } catch (IOException f) {
                    logMessage = "Error closing file >" + getSqlFile() + "<. Error =>" + f.toString() + "<.";
                    log(myName, Constants.ERROR, myArea, logMessage);
                }
                return Constants.ERROR;
            }
            logMessage = "SQL statement determined as >" + sqlStatement + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            sqlStatement = sqlStatement.trim();
            if (sqlStatement.endsWith(Constants.STATEMENT_DELIMITER)) {
                sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 1);
            }
            logMessage = "SQL statement after removal of separator >" + Constants.STATEMENT_DELIMITER + "< is >" + sqlStatement + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);

            setResult(Constants.OK);
            ins.close();
        } catch (FileNotFoundException e) {
            logMessage = "Could not open file >" + getSqlFile() + "< for reading. Error =>" + e.toString() + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            setError(Constants.ERROR, logMessage);
            setResult(Constants.ERROR);
            return Constants.ERROR;
        } catch (IOException e) {
            logMessage = "Error closing file >" + getSqlFile() + "<. Error =>" + e.toString() + "<.";
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
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void setErrorMessage(String error) {
        this.errorMessage = error;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    private void setResultMessage(String result) {
        this.resultMessage = result;
    }

    private void setError(String errCode, String errMsg) {
        setResult(errCode);
        setErrorMessage(errMsg);
    }

    private void setResult(String code, String msg) {
        setResult(code);
        setResultMessage(msg);
    }

    public String getQuery() {
        return this.query;
    }
    public void setQuery(String sqlStatement) {
        query = sqlStatement;
    }

}