/**
 * This purpose of this fixture is to insert a number of database rows using the FitNesse 'decision' table and an excel spreadsheet.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain
 * @since 21 March 2015
 * @version 20160108.0 : Added result of readWorksheet to log and check on ResultMessage. Removed some copy-paste bugs in log messages.
 * @version 20170908.0 : bugfix for empty excel cells
 * @version 20170909.0 : Made commitsize configurable
 * @version 20170910.0 : Added onlyCols and exceptCols to not process all Excel columns
 */
package nl.consag.testautomation.database;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.supporting.Attribute;
import nl.consag.testautomation.supporting.ExcelFile;
import nl.consag.testautomation.supporting.GetParameters;

public class LoadDataFromExcel {

    private static String version = "20180608.0";
    private int logLevel = 3;

    private String className = "LoadDataFromExcel";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;

    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private boolean firstTime = true;

    private String driver;
    private String url;
    private String userId;
    private String password;

    private String tableOwner = Constants.NOT_INITIALIZED;
    private String tableOwnerPassword = Constants.NOT_INITIALIZED;
    private String tableName;
    private String databaseName;
    private String inputFile;
    private int numberOfColumns;
    private String concatenatedDatabaseColumnNames; // variables used to create insert query
    private String concatenatedBindVariables; // variables used to create insert query
    private String databaseType;
    private String databaseConnDef;
    private int commitSize =Constants.DEFAULT_COMMIT_SIZE_INSERT;
    private int arraySize =Constants.DEFAULT_ARRAY_SIZE_UPDATE;

    private List<List<Attribute>> tableExcelFile;
    private String worksheetName;

    public String returnMessage = "";
    private String errorCode = Constants.OK;
    private String errorMessage = Constants.NOERRORS;
    private HashMap<Integer,String> previousCellFormatList =new HashMap<Integer,String>();
    
    private String appName=Constants.UNKNOWN;
    private boolean specificCommitSet=false;

    private String[] onlyCols;
    private String[] exceptCols;
    private List<String> onlyColsList = new ArrayList<String>();
    private List<String> exceptColsList =new ArrayList<String>();
    private List<Integer> includedCols =new ArrayList<Integer>();
    
    int colCounter =0;

    /**
     * @param context: Determines part of the log file name
     */
    public LoadDataFromExcel(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
    }

    /**
     * @param context: Determines part of the log file name
     * @param logLevel: set to FATAL, ERROR, WARNING, INFO, DEBUG, VERBOSE
     */
    public LoadDataFromExcel(String context, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
    }

    /**
     * @param context: Determines part of the log file name
     * @param logLevel: set to FATAL, ERROR, WARNING, INFO, DEBUG, VERBOSE
     * @param appName: use if you want to set application level properties
     */
    public LoadDataFromExcel(String context, String logLevel, String appName) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
        setAppName(appName);
    }

    public LoadDataFromExcel() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
    }

    /**
     * @param databasename: Must match an entry in database.properties
     */
    public void setDatabaseName(String databasename) {
        String myName = "setDatabaseName";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.databaseName = databasename;
        logMessage = "database name >" + databaseName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
    }

    /**
     * @param tableName: database tabel to load the data into
     */
    public void setTableName(String tableName) {
        String myName = "setTableName";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.tableName = tableName;
        logMessage = "table name >" + tableName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
    }

    /**
     * @param inputFile: logicalLocation subdir/filename. logicalLocation must exist in directory.properties
     */
    public void setInputFile(String inputFile) {
        String myName = "setInputFile";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.inputFile = determineCompleteFileName(inputFile);
        logMessage = "inputFile >" + inputFile + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
    }

    /**
     * @param worksheetName: Excel worksheet name containing the data to be loaded
     */
    public void setWorksheetName(String worksheetName) {
        String myName = "setWorksheetName";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.worksheetName = worksheetName;
        logMessage = "worksheet name >" + worksheetName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
    }

    /**
     * @return - OK = No errors encountered. Otherwise most of the time the SQL error message returned by the database.
     */
    public String result() {
        String myName = "result";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;
        String rc = Constants.OK;
        Connection connection = null;
//        PreparedStatement preparedStatement = null;
        String insertTableSQL;
        int commitCounter =0;
        int arrayCounter =0;
        int rowCounter =0;
        int errorCounter =0;
        returnMessage = Constants.OK;

        myArea = "read Parameter";
        readParameterFile();
        myArea = "read Excel";
        rc = readExcelFile();
        if (tableExcelFile.size() == 0 || !Constants.OK.equals(rc)) {
            logMessage =
                "No data available, Excel file or sheet not found. Message from Excel read method >" + rc + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            setErrorMessage(Constants.ERROR, logMessage);
            return Constants.ERROR;
        }
        getDatabaseColumnNames();
        if(!Constants.OK.equals(getErrorCode())) {
            log(myName, Constants.ERROR, myArea, getErrorMessage());
            return Constants.ERROR;
        }

        logMessage = "Number of columns to be included in insertQuery: >" + Integer.toString(colCounter) +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        try {
            myArea = "SQL Execution";
            logMessage = "Connecting to >" + databaseConnDef + "< using userID >" + tableOwner + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);
            connection.setAutoCommit(false); //commit transaction manually*
            insertTableSQL =
                "INSERT INTO " + tableName + " (" + concatenatedDatabaseColumnNames + ")  VALUES (" +
                concatenatedBindVariables + ")";
            logMessage = "SQL >" + insertTableSQL + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);

            PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
//            preparedStatement.clearBatch();

            for (int row = 1; row < tableExcelFile.size(); row++) {
                commitCounter++;
                arrayCounter++;
                rowCounter++;
                myArea="Processing row#" + Integer.toString(rowCounter);
                logMessage = "preparing statement for cell no >" + String.valueOf(row) + "<.";
                log(myName, Constants.VERBOSE, myArea, logMessage);
                
                logMessage = "prepared.";
                log(myName, Constants.VERBOSE, myArea, logMessage);
                int bindVariableNo = 0;
                int currentRowSize = tableExcelFile.get(row).size();
                logMessage = "Row #" + Integer.toString(rowCounter) + " has >"  + Integer.toString(currentRowSize) + "< column(s) populated.";
                log(myName, Constants.VERBOSE, myArea, logMessage);
//                for (int cell = 0; cell < numberOfColumns; cell++) {
                for (int cell = 0; cell < currentRowSize; cell++) {
                    if(!includedCols.contains(bindVariableNo)) {
                        continue;
                    }
                    bindVariableNo++;
                    logMessage = "Binding variable# >" + Integer.toString(bindVariableNo) + "<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    Attribute attribute = new Attribute();
                    attribute = tableExcelFile.get(row).get(cell);
                    //if a cell is empty, treat is as NULL value if the format is not varchar
                    //if ("".equals(tableExcelFile.get(row).get(cell))) {
                    //    log(myName, Constants.DEBUG, myArea, "Empty string detected. Setting column to NULL.");
                    //    attribute.setText(Constants.SIEBEL_NULL_VALUE);
                    //}
                    log(myName,Constants.VERBOSE, myArea,"Cell string value >" + attribute.getText() +"<.");
                    log(myName,Constants.VERBOSE, myArea,"Cell number value >" + attribute.getNumber() +"<.");
                    log(myName,Constants.VERBOSE, myArea,"Cell date value >" + attribute.getDate() +"<.");
                    log(myName,Constants.VERBOSE, myArea,"Cell format >" + attribute.getFormat() +"<.");
                    if (Constants.EXCEL_CELLFORMAT_UNKNOWN.equals(attribute.getFormat())) {
                        log(myName,Constants.DEBUG, myArea,"Using previous cell format >" + getPreviousCellFormat(bindVariableNo) + "<for bindvariable >" +Integer.toString(bindVariableNo)
                                                           +"<.");
                        attribute.setFormat(getPreviousCellFormat(bindVariableNo));
                    } else {
                        setPreviousCellFormat(bindVariableNo,attribute.getFormat());
                    }
                    if (Constants.NULL_VALUE.equals(attribute.getText())) {
                        log(myName, Constants.DEBUG, myArea, "Null string detected in bind col >" + Integer.toString(bindVariableNo) +"<. Setting column to NULL.");
                        switch (attribute.getFormat()) {
                        case Constants.EXCEL_CELLFORMAT_NUMERIC:
                            preparedStatement.setNull(bindVariableNo, Types.DOUBLE);
                            break;
                        case Constants.EXCEL_CELLFORMAT_DATE:
                            preparedStatement.setNull(bindVariableNo, Types.DATE);
                            break;
                        default:
                            preparedStatement.setNull(bindVariableNo, Types.VARCHAR);
                            break;
                        }
                    } else {
                        switch (attribute.getFormat()) {
                        case Constants.EXCEL_CELLFORMAT_NUMERIC:
                            preparedStatement.setDouble(bindVariableNo, attribute.getNumber());
                            break;
                        case Constants.EXCEL_CELLFORMAT_DATE:
                            if(attribute.getDate() == null) {
                                preparedStatement.setNull(bindVariableNo, Types.DATE);
                            } else {
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(attribute.getDate().getTime());
                                preparedStatement.setTimestamp(bindVariableNo, timestamp);
                            }
                            break;
                        default:
                                myArea="binding";
                                log(myName, Constants.VERBOSE, myArea, "length >" + attribute.getText().length() +"<. getText =>" +attribute.getText() +"<.");
                                log(myName, Constants.VERBOSE, myArea, "Trimmed length >" + attribute.getText().trim().length() +"<. getText Trimmed =>" +attribute.getText().trim() +"<.");
                                if (Constants.DATABASETYPE_DB2.equals(getDatabaseType())) {
                                    // JDBC Driver Db2 does not yet support setNString
                                    log(myName, Constants.VERBOSE, myArea, "Database type is >" + getDatabaseType() +"<. Using setString.");
                                    preparedStatement.setString(bindVariableNo, attribute.getText().trim());
                                } else {
                                    log(myName, Constants.VERBOSE, myArea, "Database type is >" + getDatabaseType() +"<. Using setNString.");
                                    preparedStatement.setNString(bindVariableNo, attribute.getText().trim());
                                }
                            break;
                            }
                        
                    }
                    logMessage = "Done.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                } //for all populated columns

                //remaining columns
                for (int cell = currentRowSize; cell < numberOfColumns; cell++) {
                    bindVariableNo++;
                    logMessage = "Setting remaining bind variable# >" + Integer.toString(bindVariableNo) + "< to NULL.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    //TODO: Determine datatype
                    preparedStatement.setNull(bindVariableNo, Types.VARCHAR);
                }
                //for all columns not populated
                
                try {
                    myArea="AddingBatch";
                    logMessage = "All bind variables added. Adding batch...";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    preparedStatement.addBatch();
                    //TODO: enable array inserts by not executing every single record
                
                    myArea="ExecutingBatch";
                    logMessage = "Batch added. Executing batch...";
                    log(myName, Constants.DEBUG, myArea, logMessage);
                    int nrRec =preparedStatement.executeUpdate();
                    myArea="ClearingBatch";
                    logMessage = "Batch executed for >" + Integer.toString(nrRec) +"< records. Clearing batch...";
                    log(myName, Constants.DEBUG, myArea, logMessage);
                    preparedStatement.clearBatch();
                    logMessage = "Batch cleared."; // Close preparedStatement...";
                    log(myName, Constants.DEBUG, myArea, logMessage);
//                    myArea="ClosingStatement";
//                    preparedStatement.close();
//                    logMessage = "preparedStatement closed.";
//                    log(myName, Constants.DEBUG, myArea, logMessage);
                }
                    catch (SQLException e) {
                            myArea = "Exception handling for " +myArea;
                            errorCounter++;
                            logMessage = "SQLException for row >" + Integer.toString(rowCounter) + "< at add/executeBatch. Error=>" + e.toString() + "<. ErrorCounter >" +Integer.toString(errorCounter) +"<.";
                            log(myName, Constants.ERROR, myArea, logMessage);
                            returnMessage = logMessage;
                            setErrorMessage(Constants.ERROR, logMessage);
                            outBatchUpdateExceptions(e);
                        }
                
                myArea="CommitCheck";
                if (commitCounter == commitSize) { // if more than x rows, perform the commit to database
                    logMessage = "Commit for (another) > " + String.valueOf(commitCounter) + " rows<.";
                    log(myName, Constants.INFO, myArea, logMessage);
                    connection.commit();
                    logMessage = "Commit done.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    commitCounter =0;
                    arrayCounter =0;
                }
            }
            
            myArea="Remainder";
            if (commitCounter > 0) {
                logMessage = "Commit to insert remaining rows in database > " + String.valueOf(commitCounter) + " rows<.";
                log(myName, Constants.INFO, myArea, logMessage);
                connection.commit();
                logMessage = "Commit done.";
                log(myName, Constants.DEBUG, myArea, logMessage);

            }
            connection.close();
//            returnMessage = Constants.OK;
        } catch (SQLException e) {
            myArea = "Exception handling" +myArea;
            errorCounter++;
            logMessage = "SQLException processing data for row >" + Integer.toString(rowCounter) +"<. Error=>" + e.toString() + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            returnMessage = logMessage + " Error counter >" + Integer.toString(errorCounter);
            setErrorMessage(Constants.ERROR, logMessage);
        }
        myArea="Conclusion";
        logMessage = "Message returning to FitNesse > " + returnMessage + "<.";
        log(myName, Constants.INFO, myArea, logMessage);
        return returnMessage;
    }

    private void getDatabaseColumnNames() {
        //Function to read the names of the database columns	
        Attribute attribute = new Attribute();
        //reset
        colCounter=0;
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        for (int i = 0; i < tableExcelFile.get(0).size(); i++) { // next column names
            attribute = tableExcelFile.get(0).get(i); //first column name
            //Check whether column name should be excluded or not
            String checkInclusion=determineColumnInclusion(attribute.getText(), i);
            switch(checkInclusion) {
            case Constants.YES:
                if (colCounter == 0) {
                    if (attribute.getFormat() == "NUMERIC") {
                        concatenatedDatabaseColumnNames = String.valueOf(attribute.getNumber());
                    } else {
                        if (attribute.getFormat() == "DATE") {
                            String text = df.format(attribute.getDate());
                            concatenatedDatabaseColumnNames = text;
                        } else { //String, boolean and others
                            concatenatedDatabaseColumnNames = attribute.getText();
                        }
                    }
                    concatenatedBindVariables = "?";
                    numberOfColumns = 1;
                } else {
                    if (attribute.getFormat() == "NUMERIC") {
                       concatenatedDatabaseColumnNames =
                            concatenatedDatabaseColumnNames + "," + String.valueOf(attribute.getNumber());
                    } else {
                        if (attribute.getFormat() == "DATE") {
                             String text = df.format(attribute.getDate());
                             concatenatedDatabaseColumnNames = concatenatedDatabaseColumnNames + "," + text;
                         } else { //String, boolean and others
                             concatenatedDatabaseColumnNames = concatenatedDatabaseColumnNames + "," + attribute.getText();
                         }
                     }
                     concatenatedBindVariables = concatenatedBindVariables + ",?";
                     numberOfColumns++;
                    }
                colCounter++;
                break;
            case Constants.NO:
                break;
            default:
                setErrorMessage(Constants.ERROR, "Column inclusion could not be determined.");
                break;
            }
        }
    }

    private void readParameterFile() {
        //Function to read the parameters in a parameter file
        String myName = "readParameterFile";
        String myArea = "reading";
        String logMessage = Constants.NO;
        String result =Constants.NOT_FOUND;

        databaseType = GetParameters.GetDatabaseType(databaseName);
        logMessage = "Database type >" + databaseType +"<.";
        log(myName, Constants.INFO, myArea, logMessage);

        databaseConnDef = GetParameters.GetDatabaseConnectionDefinition(databaseName);
        logMessage = "Database connection definition: " + databaseConnDef;
        log(myName, Constants.INFO, myArea, logMessage);

        driver = GetParameters.GetDatabaseDriver(databaseType);
        logMessage = "driver: " + driver;
        log(myName, Constants.INFO, myArea, logMessage);
        url = GetParameters.GetDatabaseURL(databaseConnDef);

        tableOwner = GetParameters.GetDatabaseTableOwnerName(databaseName);
        logMessage = "Table Owner UserID >" + tableOwner + "<.";
        log(myName, Constants.INFO, myArea, logMessage);

        tableOwnerPassword = GetParameters.GetDatabaseTableOwnerPWD(databaseName);
        logMessage = "Password for user >" + tableOwner + "< retrieved.";
        log(myName, Constants.INFO, myArea, logMessage);

        userId = GetParameters.GetDatabaseUserName(databaseName);
        logMessage = "User UserID >" + userId + "<.";
        log(myName, Constants.INFO, myArea, logMessage);
        password = GetParameters.GetDatabaseUserPWD(databaseName);
        logMessage = "Password for user >" + userId + "< retrieved.";
        log(myName, Constants.INFO, myArea, logMessage);

        logUrl = GetParameters.GetLogUrl();
        logMessage = "logURL >" + logUrl +"<.";   log(myName, Constants.DEBUG, myArea, logMessage);

        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
        if(Constants.NOT_FOUND.equals(result)) {
            setResultFormat(Constants.DEFAULT_RESULT_FORMAT);
        }
        else {
            setResultFormat(result);
        }
        log(myName, Constants.DEBUG, myArea, "resultFormat >" + getResultFormat() +"<.");

        //Commit size
        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getAppName(), this.className, Constants.PARAM_COMMIT_SIZE_INSERT);
        if(Constants.NOT_FOUND.equals(result)) {
            setCommitSize(Constants.DEFAULT_COMMIT_SIZE_INSERT);
        }
        else {
            setCommitSize(Integer.parseInt(result));
        }
        log(myName, Constants.DEBUG, myArea, "Commit size >" + Integer.toString(getCommitSize()) +"<.");

    }
    
    public String getDatabaseType() {
        return databaseType;
    }

    private String readExcelFile() {
        //Function to read the excel file
        String myName = "readExcelFile";
        String myArea = "reading";
        String logMessage = Constants.NO;

        ExcelFile excelFile = new ExcelFile();
        tableExcelFile = excelFile.readWorksheetWithNameIncludingFormat(inputFile, worksheetName);
        logMessage = "Excel file >" + inputFile + "< retrieved with message >" + excelFile.getReturnMessage() + "<.";
        log(myName, Constants.INFO, myArea, logMessage);

        return excelFile.getReturnMessage();
    }

    private String determineCompleteFileName(String dirAndFile) {
        String[] dirAndFileSeparated;
        String sCompleteFileName;
        String dir = "FROMROOTDIR_UNKNOWN";

        dirAndFileSeparated = dirAndFile.split(" ", 2);

        dir = GetParameters.GetRootDir(dirAndFileSeparated[0]);
        sCompleteFileName = dir + "/" + dirAndFileSeparated[1];

        return sCompleteFileName;

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

    private void log(String name, String level, String area, String logMessage) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }

        if (firstTime) {
            firstTime = false;
            if (context.equals(Constants.DEFAULT)) {
                logFileName = startDate + "." + className;
            } else {
                logFileName = context + "." + startDate;
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

    private void setErrorMessage(String err, String logMessage) {
        errorCode = err;
        errorMessage = logMessage;
    }

    /**
     * @return - The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return - The result code, eg. OK, ERROR, FATAL
     */
    public String getErrorCode() {
        return errorCode;
    }

    private void setPreviousCellFormat(int bindVariableNo, String cellFormat) {
        previousCellFormatList.put(bindVariableNo, cellFormat);
    }

    private String getPreviousCellFormat(int bindVariableNo) {
        if(previousCellFormatList.containsKey(bindVariableNo)) {
            return previousCellFormatList.get(bindVariableNo);
        } else {
            return Constants.EXCEL_CELLFORMAT_UNKNOWN;
        }
    }

    /**
     * @param commitSize: Sets the commit size for this test. Overrules any commit size settings in properties file.
     */
    public void setCommitSize(String commitSize) {
        setCommitSize(Integer.parseInt(commitSize));
        specificCommitSet=true;
    }
    
    private void setCommitSize(int i) {
        String myName="setCommitSize";
        String myArea="checkTestPageSetCommit";
        
        if(specificCommitSet) {
            log(myName, Constants.INFO, myArea, "Test page has commit set to >" + Integer.toString(getCommitSize()) +"<. Ignoring property file settings.");
        } else {
            commitSize=i;
        }
    }

    private int getCommitSize() {
        return commitSize;
    }

    /**
     * @param appName: Sets the application name. Use this to set application level properties in fixture.properties
     */
    public void setApplication(String appName) {
        setAppName(appName);
    }
    
    private void setAppName(String appName) {
        this.appName=appName;
    }

    private String getAppName() {
        return appName;
    }
    
    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setOnlyExcelColumns(String onlyCols) {
        this.onlyCols=onlyCols.split(",");
        this.onlyColsList =Arrays.asList(this.onlyCols);
    }
    
    public void setExceptExcelColumns(String exceptCols) {
        this.exceptCols=exceptCols.split(",");
        this.exceptColsList =Arrays.asList(this.exceptCols);
    }

    private String determineColumnInclusion(String colName, int position) {
        String myName="determineColumnInclusion";
        String myArea="check inclusion/exclusion";
        boolean includeCol=true;
        boolean excludeCol=false;
        String outcome=Constants.YES;
        
        // if colName is in inclusionList (if available)
        if(onlyColsList.size() >0) {
            if(onlyColsList.contains(colName)) {
                //got it
                includeCol=true;
                log(myName, Constants.VERBOSE, myArea, "onlyCols: Column >" +colName + "< should be included.");
            } else {
                //not in the list
                includeCol=false;
                log(myName, Constants.VERBOSE, myArea, "onlyCols: Column >" +colName + "< should be excluded.");
            }
        } else {
            log(myName, Constants.INFO, myArea, "No inclusion list provided.");
        }

        if(exceptColsList.size() >0) {
            if(exceptColsList.contains(colName)) {
                //don't include it
                excludeCol=true;
                log(myName, Constants.VERBOSE, myArea, "exceptCols: Column >" +colName + "< should be excluded.");
            } else {
                //fine
                excludeCol=false;
                log(myName, Constants.VERBOSE, myArea, "exceptCols: Column >" +colName + "< should be included.");
            }
        } else {
            log(myName, Constants.INFO, myArea, "No exception list provided.");
        }

    //determine outcome
        if(includeCol && ! excludeCol) {
            //both say column should be included
            outcome=Constants.YES;
            includedCols.add(position);
        }
        if(! includeCol &&  excludeCol) {
            //both say column should be excluded
            outcome=Constants.NO;
        }
        if(includeCol && excludeCol) {
            //Conflict
            outcome=Constants.ERROR;
            log(myName, Constants.ERROR, myArea, "onlyCols would include column >" + colName +"<, but exceptCols excludes it. Please make up your mind :)");
        }
        if(!includeCol && !excludeCol) {
            //If the column is not on the include list, it should not be included, even though it might not be on the exclude list
            outcome=Constants.NO;
            log(myName, Constants.INFO, myArea, "onlyCols excludes column >" + colName +"<. Column is not on the exceptCols list. Column will be excluded.");
        }
        
        return outcome;
    }

    private void outBatchUpdateExceptions(SQLException e) {
        String myName="outBatchUpdateExceptions";
        String myArea="Exception Handling";
        int i=0;
        //Code taken from: https://www.ibm.com/support/knowledgecenter/en/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_tjvjdbue.html
        log(myName, Constants.INFO, myArea, "Contents of BatchUpdateException. First error message:");
        log(myName, Constants.ERROR, myArea, "Message ......: " + e.getMessage());
        log(myName, Constants.ERROR, myArea, "SQLSTATE .....: " + e.getSQLState());
        log(myName, Constants.ERROR, myArea, "Error Code ...: " + e.getErrorCode());
        SQLException ex = e.getNextException();
        while (ex != null) {
            i++;
            log(myName, Constants.ERROR, myArea, "SQL Exception >" + Integer.toString(i) +"<.");
            log(myName, Constants.ERROR, myArea, "Message ......: " + ex.getMessage());
            log(myName, Constants.ERROR, myArea, "SQLSTATE .....: " + ex.getSQLState());
            log(myName, Constants.ERROR, myArea, "Error Code ...: " + ex.getErrorCode());
            ex = ex.getNextException();
        }
        
    }
}
