package nl.jacbeekers.testautomation.fitnesse.data;

import nl.jacbeekers.testautomation.fitnesse.supporting.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class GenerateData {

    public final String version ="20190511.0";
    private String className = GenerateData.class.getName();

    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;
    private String application = Constants.NOT_PROVIDED;
    private String jsonFile = Constants.NOT_PROVIDED;
    private String outputFile = Constants.NOT_PROVIDED;
    private String nrRecordsAsString = "0";
    private int nrRecords =0;

    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NO_ERRORS;

    public GenerateData(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
    }

    public GenerateData(String context, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
    }

    public GenerateData(String context, String logLevel, String appName) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
        setApplication(appName);
    }

    public GenerateData() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
    }

    public String entity = Constants.NOT_INITIALIZED;



    public String generateFile() {

        if(Constants.NO.equals(jsonFileExists())) {
            return ResultMessages.getMessage(ResultMessages.ERR_FILE_NOTFOUND) +": " + getJsonFile();
        }

        RunProcess runProcess = new RunProcess();
        String command = "java -jar lib/generator.jar generate --quiet --max-rows="
                + getNrRecords() + " --replace " + getJsonFile() +" " + getOutputFile();
        int returnCode = runProcess.runAndWait(command);
        setResultCode(runProcess.getResultCode());
        setResultMessage(runProcess.getResultMessage());

        if(returnCode ==0)
            return getResultCode();
        else
            return getResultCode() + ": " + getResultMessage();
    }


    public String jsonFileExists() {
        return checkFileExistence(getJsonFile());
    }
    public String outputFileExists() {
        return checkFileExistence(getOutputFile());
    }
    public String checkFileExistence(String file) {
        if(Files.exists(Paths.get(file))) return Constants.YES; else return Constants.NO;
    }
    public int linesInOutputFile() {
        if(Constants.NO.equals(checkFileExistence(getOutputFile()))) {
            return -1;
        }

        return new LinesInFile().count(getOutputFile());

    }

    public void setEntity(String entity) { this.entity = entity; }
    public String getEntity() { return this.entity; }

    public String getVersion() { return this.version; }

    public void setApplication(String application) { this.application = application; }
    public String getApplication() { return this.application; }

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

    public void setJsonFile(String jsonFile) { this.jsonFile = jsonFile; }
    public String getJsonFile() { return this.jsonFile; }

    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
    public String getOutputFile() { return this.outputFile; }

    public void setNrRecords(String nrRecords) { this.nrRecordsAsString = nrRecords; this.nrRecords = Integer.parseInt(nrRecords); };
    public String getNrRecords() { return this.nrRecordsAsString; }

    private void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

    private void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; };
}
