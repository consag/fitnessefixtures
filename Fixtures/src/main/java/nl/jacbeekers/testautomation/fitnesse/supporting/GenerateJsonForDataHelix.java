package nl.jacbeekers.testautomation.fitnesse.supporting;


import java.text.SimpleDateFormat;

public class GenerateJsonForDataHelix {

    private static final String version = "20190511.0";
    private String logFileName = Constants.NOT_INITIALIZED;
    private boolean logFileNameAlreadySet = false;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 4;
    private String logUrl = Constants.LOG_DIR;
    private boolean firstTime = true;
    private String className =Constants.NOT_INITIALIZED;

    private String schemaVersion = Constants.DEFAULT_SCHEMA_VERSION;

    public GenerateJsonForDataHelix() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        className = GenerateJsonForDataHelix.class.getName();
        context = className;
        logFileName = startDate + "." + context;
        logFileNameAlreadySet = false; // allow override

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public GenerateJsonForDataHelix(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        className = GenerateJsonForDataHelix.class.getName();
        logFileName = startDate + "." + className + "." + context;
        logFileNameAlreadySet = false; // allow override
        setContext(context);
    }

    public String result() {
        String returnMessage = Constants.OK;
        String myName = "result";
        String myArea = "init";

        returnMessage = Constants.NOT_IMPLEMENTED;
        log(myName, Constants.INFO, myArea, "Message returning to FitNesse > " + returnMessage + "<.");
        return returnMessage;

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

    public String getLogFileName() { return logFileName; }
    public void setLogFileName(String logFileName) { this.logFileName = logFileName; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public int getLogLevel() { return logLevel; }
    public void setLogLevel(int logLevel) { this.logLevel = logLevel; }
    /**
     * @return - the log level as Integer data type
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }


    public String getLogUrl() { return logUrl; }
    public void setLogUrl(String logUrl) { this.logUrl = logUrl; }

    public boolean isLogFileNameAlreadySet() { return logFileNameAlreadySet; }
    public void setLogFileNameAlreadySet(boolean logFileNameAlreadySet) { this.logFileNameAlreadySet = logFileNameAlreadySet; }

    public String getVersion() { return this.version; }
    public String getClassName() { return this.className; }

    public String getSchemaVersion() { return this.schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }

}
