package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.ERRCODE_DECRYPT;
import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.propFileErrors;

public class ConnectionProperties {

    private String className = "ConnectionProperties";
    private static String version = "20200516.0";

    private int logLevel =3;
    private boolean firstTime=true;
    private String context = Constants.DEFAULT;
    private String logFilename = Constants.NOT_INITIALIZED;
    private String logUrl = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;

    private boolean errorIndicator;
    private String errorMessage = Constants.NO_ERRORS;
    private String errorCode=Constants.OK;

    /*
     Database properties
     */
    private String databaseDriver;
    private String databaseUrl;
    private String databaseUrlOptions;
    private String databaseUsername;
    private String databaseUsernamePassword;
    private String databaseConnection;
    private String databaseType;
    private String databaseTableOwner;
    private String databaseTableOwnerPassword;
    private String tableOwnerTablePrefix;
    private String tableOwnerUseTablePrefix; //from connections.properties
    private boolean useTablePrefix =true;
    private String databaseSchema;
    private boolean useSchema =false;
    private boolean useTableOwner =true;
    private String idaaName =Constants.NOT_PROVIDED;
    private String databaseName =Constants.NOT_PROVIDED;
    private String actualDatabase = Constants.NOT_FOUND;

    public ConnectionProperties() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFilename = startDate + "." + className;

    }
    public String getClassName() {
        return this.className;
    }
    public String getLogFilenameLink() {
        if(getLogUrl().startsWith("http"))
            return "<a href=\"" +getLogUrl() + this.logFilename +".log\" target=\"_blank\">" + this.logFilename + "</a>";
        else
            return getLogUrl() + this.logFilename + ".log";
    }
    public String getLogFilename() {
        return this.logFilename;
    }
    public void setLogFilename(String logFilename) {
        log("ConnectionProperties.setLogFilename", Constants.DEBUG, "setter", "logFilename set to >" + logFilename +"<.");
        this.logFilename = logFilename;
    }

    public String getLogUrl() {
        return this.logUrl;
    }

    public void setLogUrl(String logUrl){
        if(Constants.NOT_FOUND.equals(logUrl)) {
            String myName="setLogUrl";
            String myArea ="run";
            String logMessage ="Properties file does not contain LogURL value.";
            log(myName, Constants.WARNING, myArea, logMessage);
        } else {
            this.logUrl = logUrl;
        }
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public void getConnectionProperties(String databaseConnection) {
        refreshConnectionProperties(databaseConnection);
    }
    public boolean refreshConnectionProperties(String fitnesseDatabaseName) {
        // database connection string has to be set by calling party
        String myName = "refreshConnectionProperties";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String result = Constants.NOT_FOUND;

        File file = new File(Constants.CONNECTION_PROPERTIES);
        try {
            log(myName, Constants.DEBUG, myArea, "Properties file is >" + Constants.CONNECTION_PROPERTIES
                    + "< which is >" + file.getCanonicalPath() + "<.");
        } catch (IOException e) {
            log(myName, Constants.DEBUG, myArea, "Properties file is >" + Constants.CONNECTION_PROPERTIES
                    + "< which is >" + file.getAbsolutePath() + "<.");
        }
        if(! file.exists()) {
            log(myName, Constants.ERROR, myArea, "Properties file does not exist.");
            return false;
        }
        log(myName, Constants.DEBUG, myArea, "Using fitnesseDatabaseName >" + fitnesseDatabaseName +"<.");
        setDatabaseConnection(fitnesseDatabaseName);
        log(myName, Constants.DEBUG, myArea, "databaseConnection has been set to >" + getDatabaseConnection() +"<.");

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".database", true);
        if(getErrorIndicator()) {
            log(myName, Constants.DEBUG, myArea, "Error getting property >" + getDatabaseConnection() +".database" +"<. Error: "
                + result);
            return false;
        }
        else {
            setActualDatabase(result);
            log(myName, Constants.DEBUG, myArea, "actualDatabase is >" + getActualDatabase() +"<.");
        }

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".databasetype", true);
        if(getErrorIndicator())
            return false;
        else setDatabaseType(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".driver", true);
        if(getErrorIndicator())
            return false;
        else setDatabaseDriver(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".url", true);
        if(getErrorIndicator())
            return false;
        else setDatabaseUrl(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".url.options", false);
        if(getErrorIndicator())
            return false;
        else setDatabaseUrlOptions(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".username", true);
        if(getErrorIndicator())
            return false;
        else setDatabaseUsername(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".username.password", true);
        if(getErrorIndicator())
            return false;
        else setDatabaseUsernamePassword(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection()+".tableowner", false);
        if(getErrorIndicator())
            return false;
        else setDatabaseTableOwner(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".tableowner.password", false);
        if(getErrorIndicator())
            return false;
        else setDatabaseTableOwnerPassword(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".tableowner.usetableprefix", false);
        if(!getErrorIndicator())
            setTableOwnerUseTablePrefix(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".tableowner.tableprefix", false);
        if(!getErrorIndicator())
            setTableOwnerTablePrefix(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".schemaname", false);
        if(!getErrorIndicator())
            setDatabaseSchema(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".databasename", false);
        if(!getErrorIndicator())
            setDatabaseName(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".accelerator", false);
        if(!getErrorIndicator())
            setAccelerator(result);

        if(Constants.FALSE.equalsIgnoreCase(getTableOwnerUseTablePrefix())) {
            setTableOwnerUseTablePrefix(false);
        } else {
            setTableOwnerUseTablePrefix(true);
        }
        if(useTablePrefix) log(myName, Constants.DEBUG, myArea, "useTablePrefix has been set to >true<");
        else log(myName, Constants.DEBUG, myArea, "useTablePrefix has been set to >false<");

        if(Constants.DEFAULT_PROPVALUE.equals(getDatabaseSchema())) {
            setUseSchema(false);
        } else {
            setUseSchema(true);
        }
        if(useSchema) log(myName, Constants.DEBUG, myArea, "useSchema has been set to >true<.");
        else log(myName, Constants.DEBUG, myArea, "useSchema has been set to >false<");

        if(Constants.DEFAULT_PROPVALUE.equals(getDatabaseTableOwner())) {
            setUseTableOwner(false);
        } else {
            setUseTableOwner(true);
        }
        if(useTableOwner) log(myName, Constants.DEBUG, myArea, "useTableOwner has been set to >true<.");
        else log(myName, Constants.DEBUG, myArea, "useTableOwner has been set to >false<.");

        if(Constants.DEFAULT_PROPVALUE.equals(getTableOwnerTablePrefix())) {
            setTableOwnerTablePrefix(Constants.TABLE_PREFIX);
            log(myName, Constants.VERBOSE, myArea, "Table prefix has been set to >" + getTableOwnerTablePrefix() +"<.");
        }
        return true;

    }

    private String getProperty(String propertiesFile, String key, boolean mustExist) {
        String myName ="getProperty";
        String myLocation="start";
        String result =Constants.NOT_FOUND;
        Parameters parameters = new Parameters();

        log(myName, Constants.VERBOSE, myLocation, "Retrieving value for property >"
                + key +"< from >" + propertiesFile +"<.");
        result =parameters.getPropertyVal(propertiesFile, key);
        log(myName, Constants.VERBOSE, myLocation, "search for property >" +key + "< returned result code >" +parameters.getResult() +"<.");
        if(mustExist && propFileErrors.contains(parameters.getResult())) {
            setError(result, "Error retrieving property >" + key + "< from >" + propertiesFile + "<.");
            setErrorIndicator(true);
            return parameters.getResult();
        }
        setErrorIndicator(false);
        return result;
    }
    private void setErrorIndicator(boolean indicator) {
        errorIndicator =indicator;
    }
    private boolean getErrorIndicator(){
        return this.errorIndicator;
    }
    
    private void setTableOwnerUseTablePrefix(boolean b) {
        this.useTablePrefix =b;
    }
    public boolean getUseTablePrefix() {
        return useTablePrefix;
    }

    private void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    private String getDatabaseUrl() {
        return databaseUrl;
    }

    private void setDatabaseUrlOptions(String databaseUrlOptions) {
        this.databaseUrlOptions = databaseUrlOptions;
    }
    private String getDatabaseUrlOptions() {
        return databaseUrlOptions;
    }

    public void setDatabase(String dbName) {
        this.databaseName =dbName;
    }
    public String getDatabase() {
        return this.databaseName;
    }


    /**
     * @param accName - The name of the IBM DB2 Analytics Accelerator (IDAA)
     */
    public void setAccelerator(String accName) {
        this.idaaName = accName;
    }
    public String getAccelerator() {
        return this.idaaName;
    }

    public void setDatabaseConnection(String dbConn) {
        this.databaseConnection=dbConn;
    }
    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseUsername(String username) {
        this.databaseUsername = username;
    }
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabaseTableOwner() {
        return this.databaseTableOwner;
    }
    public void setDatabaseTableOwner(String databaseTableOwner) {
        this.databaseTableOwner = databaseTableOwner;
    }

    private String getDatabaseTableOwnerPassword() {
        String myName ="getDatabaseTableOwnerPassword";
        String myLocation ="start";
        Decrypt decrypt = new Decrypt();
        String result = Decrypt.decrypt(databaseTableOwnerPassword);
        if(Constants.OK.equals(decrypt.getErrorCode())) {
            setErrorIndicator(false);
            log(myName, Constants.DEBUG, myLocation, "Owner password decryption successful.");
        }
        else {
            setErrorIndicator(true);
            String logMessage = "Owner password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.";
            setError(ERRCODE_DECRYPT, logMessage);
            log(myName, Constants.ERROR, myLocation, logMessage);
        }
        return result;

    }
    public void setDatabaseTableOwnerPassword(String tableOwnerPassword) {
        this.databaseTableOwnerPassword = tableOwnerPassword;
    }

    public void setDatabaseUsernamePassword(String usernamePassword) {
        this.databaseUsernamePassword = usernamePassword;
    }
    private String getDatabaseUsernamePassword() {
        String myName ="getDatabaseUsernamePassword";
        String myLocation ="start";
        Decrypt decrypt = new Decrypt();
        String result = Decrypt.decrypt(databaseUsernamePassword);
        if(Constants.OK.equals(decrypt.getErrorCode())) {
            setErrorIndicator(false);
            log(myName, Constants.DEBUG, myLocation, "User password decryption successful.");
        }
        else {
            setErrorIndicator(true);
            String logMessage = "User password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.";
            setError(ERRCODE_DECRYPT, logMessage);
            log(myName, Constants.ERROR, myLocation, logMessage);
        }
        return result;

    }

    public String getIdaaName() {
        return idaaName;
    }
    public void setIdaaName(String idaaName) {
        this.idaaName = idaaName;
    }

    public String getDatabaseName() {
        return databaseName;
    }
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }
    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public void setActualDatabase(String actualDatabase) {
        this.actualDatabase = actualDatabase;
    }
    public String getActualDatabase() {
        return actualDatabase;
    }

    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }
    public String getDatabaseSchema() {
        return this.databaseSchema;
    }

    public void setTableOwnerUseTablePrefix(String tableOwnerUseTablePrefix) {
        this.tableOwnerUseTablePrefix = tableOwnerUseTablePrefix;
    }
    public String getTableOwnerUseTablePrefix() {
        return this.tableOwnerUseTablePrefix;
    }

    public void setTableOwnerTablePrefix(String tableOwnerTablePrefix) {
        this.tableOwnerTablePrefix = tableOwnerTablePrefix;
    }
    public String getTableOwnerTablePrefix() {
        return this.tableOwnerTablePrefix;
    }

    private void setUseSchema(boolean useSchema) {
        this.useSchema = useSchema;
    }
    public boolean getUseSchema() {
        return this.useSchema;
    }

    private void setUseTableOwner(boolean useTableOwner) {
        this.useTableOwner = useTableOwner;
    }
    private boolean getUseTableOwner() {
        return this.useTableOwner;
    }

    private void setError(String errorCode, String errorMessage) {
        this.errorCode =errorCode;
        this.errorMessage = errorMessage;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public Integer getIntLogLevel() {
        return logLevel;
    }

    private void log(String name, String level, String area, String logMessage) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        if (firstTime) {
            firstTime = false;
            Logging.LogEntry(getLogFilename(), getClassName() +"-" +name, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(getLogFilename(), getClassName() + "-" +name, level, area, logMessage);

    }

    public static String getVersion() {
        return version;
    }

    private Connection getConnection(String username, String password) throws SQLException {
        String myName="getConnection";
        String myArea="start";
        String logMessage =Constants.NOT_INITIALIZED;
        String url =getDatabaseUrl();

        if(getDatabaseUrlOptions() != null && ! getDatabaseUrlOptions().isEmpty()) {
            if(! getDatabaseUrlOptions().equalsIgnoreCase(Constants.DEFAULT_PROPVALUE)) {
                url= url + ":" + getDatabaseUrlOptions();
            }
        }
        log(myName, Constants.DEBUG, myArea, "Database URL for user >" +username +"< is >"
                + url +"<.");

        Connection connection = DriverManager.getConnection(url
                , username
                , password);

        if(getUseSchema()) {
            myArea = "setSchema";
            switch (getDatabaseType()) {
                case Constants.DATABASETYPE_ORACLE:
                    Statement sessionStatement = connection.createStatement();
                    String sqlStatement ="ALTER SESSION SET CURRENT_SCHEMA=" + getDatabaseSchema();
                    logMessage="Session SQL >" + sqlStatement +"<.";
                    log(myName, Constants.INFO, myArea, logMessage);
                    int result =sessionStatement.executeUpdate(sqlStatement);
                    if(result ==0) {
                        log(myName, Constants.INFO, myArea, "SQL executed with rc=>" + result +"<.");
                    } else {
                        log(myName, Constants.WARNING, myArea, "Session SQL failed. Schema may not be set correctly.");
                    }
                    sessionStatement.close();
                    break;
                default:
                    logMessage="Session SQL not yet supported for database type > "
                        + getDatabaseType() +"<.";
                    log(myName, Constants.WARNING, myArea, logMessage);
                    break;
            }

        }

        return connection;
    }

    public Connection getUserConnection() throws SQLException {
        String myName = "getUserConnection";
        String myArea = "decrypt";
        String password = getDatabaseUsernamePassword();
        if(getErrorIndicator()) {
            log(myName, Constants.ERROR, myArea, getErrorMessage());
            return null;
        } else {
            myArea = "connection";
            return getConnection(getDatabaseUsername(), password);
        }
    }

    public Connection getOwnerConnection() throws SQLException {
        if(getErrorIndicator()) {
            return null;
        } else {
            return getConnection(getDatabaseTableOwner(), getDatabaseTableOwnerPassword());
        }
    }
}
