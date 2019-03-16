package nl.consag.testautomation.database;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.IDAATools.TableManagement;
import nl.consag.testautomation.supporting.Logging;

public class IdaaProcedures {

    private static final String className ="IdaaProcedures";
    private static String version = "20180706.3";
    private String logFileName = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;

    private boolean firstTime = true;

    private String startDate =Constants.NOT_INITIALIZED;
    private Object context =Constants.NOT_INITIALIZED;
    private String databaseName =Constants.NOT_INITIALIZED;
    private String tablename =Constants.NOT_INITIALIZED;
    private String idaaname =Constants.NOT_INITIALIZED;
    private String action =Constants.NOT_INITIALIZED;
    private String schemaname =Constants.NOT_INITIALIZED;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String errorCode =Constants.OK;
    private String errorMessage =Constants.NO_ERRORS;

    public IdaaProcedures() {
          java.util.Date started = new java.util.Date();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          this.startDate = sdf.format(started);
          this.context=className;
        logFileName = startDate + "." + className ;

    }

    public IdaaProcedures(String context) {
          java.util.Date started = new java.util.Date();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          this.startDate = sdf.format(started);
          this.context=context;
        logFileName = startDate + "." + className + "." + context;

    }

    public void setDatabaseName(String databaseName) {
        String myName="setDatabaseName";
        String myArea="Process";
      this.databaseName = databaseName;
      log(myName, Constants.DEBUG, myArea,"DatabaseName set to >" + this.databaseName +"<.");
    }
    public String getDatabaseName(){
        return this.databaseName;
    }

    public void setTableName(String tableName) {
        String myArea="Process";
        String myName="setTableName";
        
        if(tableName.contains(".")) {
            setSchemaName(tableName.substring(0, tableName.indexOf(".") -1));
            this.tablename = tableName.substring(tableName.indexOf(".") +1);
        } else {
            this.tablename =tableName;
        }
        log(myName, Constants.DEBUG, myArea,"tableName set to >" + this.tablename +"<.");
    }
    public String getTableName(){
        return this.tablename;
    }
    
    public void setIdaaName(String idaaName) {
        String myArea="Process";
        String myName="setIdaaName";
        this.idaaname =idaaName;
        log(myName, Constants.DEBUG, myArea,"idaaname set to >" + this.idaaname +"<.");

    }
    public String getIdaaName() {
        return this.idaaname;
    }

    public void setAction(String action) {
        String myArea="Process";
        String myName="setAction";
        this.action = action.toUpperCase();
        log(myName, Constants.DEBUG, myArea,"action set to >" + this.action +"<.");

    }
    private String getAction() {
        return this.action;
    }

    private void reset() {
        setError(Constants.NOT_INITIALIZED,Constants.NOT_INITIALIZED);
    }
    public String result() {
        String myName="result";
        String myArea="init";
        String result = Constants.NOERRORS;

        reset();
        
        TableManagement idaaMgmt = new TableManagement();
        idaaMgmt.setLogLevel(getLogLevel());
        idaaMgmt.setLogFileName(getLogFileNameOnly());

        Connection connection = null;
        readParameterFile();
//        connection = connectionProperties.getUserConnection();


        if(verifyAction(getAction()).equals(Constants.OK)) {
            switch(getAction()) {
                case "SYNC":
                    log(myName,Constants.DEBUG, myArea, "Calling idaaMgmt.loadTable");
                    setErrorMessage((idaaMgmt.loadTable(connectionProperties.getAccelerator(), connectionProperties.getDatabaseName()
                            , connectionProperties.getDatabaseSchema(), getTableName())));
                    if(idaaMgmt.getErrorCode().equals(Constants.OK)) {
                        setError(Constants.OK, idaaMgmt.getErrorMessage());
                    }    else {
                        setError(Constants.ERROR, idaaMgmt.getErrorMessage());
                    }
                    break;
                case "CHECK":
                    log(myName,Constants.DEBUG, myArea, "Calling idaaMgmt.checkTable");
                    result= idaaMgmt.checkTable(connectionProperties.getAccelerator(), connectionProperties.getDatabaseName()
                            , connectionProperties.getDatabaseSchema(), getTableName());
                    if(idaaMgmt.getErrorCode().equals(Constants.OK)) {
                        setError(Constants.OK, idaaMgmt.getErrorMessage());
                    }    else {
                        setError(Constants.ERROR, idaaMgmt.getErrorMessage());
                    }
                    break;
            }
        } else {
            setErrorMessage("Invalid IDAA Action >" + getAction() +"< specified.");
        }
        return getErrorCode();
    }

    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return this.errorCode;
    }
    private void setError(String errorCode, String errorMessage) {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }
    private void setErrorMessage(String err) {
        this.errorMessage = err;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setSchemaName(String schemaName) {
        this.schemaname = schemaName;
    }
    public String getSchemaName() {
        return this.schemaname;
    }

    private String verifyAction(String providedAction) {
        boolean actionValid =false;
        
        switch (providedAction.toUpperCase()) {
        case "SYNC":
            case "CHECK":
            actionValid =true;
            break;
        default:
            actionValid =false;
        }
        
        if(actionValid) {
            return Constants.OK;
        } else {
            return Constants.ERROR;
        }
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

    public String getLogFileNameOnly() {
        return this.logFileName;
    }
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


}
