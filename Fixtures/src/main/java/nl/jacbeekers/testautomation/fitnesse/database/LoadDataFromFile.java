package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.text.SimpleDateFormat;

public class LoadDataFromFile {

    public LoadDataFromFile(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
    }

    /**
     * @param context: Determines part of the log file name
     * @param logLevel: set to FATAL, ERROR, WARNING, INFO, DEBUG, VERBOSE
     */
    public LoadDataFromFile(String context, String logLevel) {
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
    public LoadDataFromFile(String context, String logLevel, String appName) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
        setApplication(appName);
    }

    public LoadDataFromFile() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
    }

    private String className = LoadDataFromFile.class.getName();
    private static String version = "20190511.0";

    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;
    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NOERRORS;
    private String appName = Constants.NOT_INITIALIZED;

    private boolean firstTime = true;

    private int commitSize =Constants.DEFAULT_COMMIT_SIZE_INSERT;
    private int arraySize =Constants.DEFAULT_ARRAY_SIZE_UPDATE;
    private boolean specificCommitSet=false;
    private String databaseName = Constants.NOT_INITIALIZED;
    private String tableName = Constants.NOT_INITIALIZED;
    private String inputFile = Constants.NOT_INITIALIZED;



    public String result() {
        String returnMessage = Constants.OK;
        String myName = "result";
        String myArea = "init";

        returnMessage = Constants.NOT_IMPLEMENTED;
        log(myName, Constants.INFO, myArea, "Message returning to FitNesse > " + returnMessage + "<.");
        return returnMessage;

    }






    public String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
    }
    public Integer getIntLogLevel() {
        return logLevel;
    }
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
    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
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

    public static String getVersion() {
        return version;
    }

    private void setResult(String err, String logMessage) {
        resultCode = err;
        resultMessage = logMessage;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    public String getResultCode() {
        return resultCode;
    }

    public void setApplication(String appName) { this.appName = appName; }
    public String getApplication() { return  this.appName; }

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

    public void setInputFile(String inputFile) {
        String myName = "setInputFile";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.inputFile = determineCompleteFileName(inputFile);
        logMessage = "inputFile >" + inputFile + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
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

}
