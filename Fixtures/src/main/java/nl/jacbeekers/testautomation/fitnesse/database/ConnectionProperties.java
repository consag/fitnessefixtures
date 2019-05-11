package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Decrypt;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;
import nl.jacbeekers.testautomation.fitnesse.supporting.Parameters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static nl.jacbeekers.testautomation.fitnesse.supporting.Constants.propFileErrors;

public class ConnectionProperties {

    private String className = "ConnectionProperties";
    private static String version = "20180820.0";

    private int logLevel =3;
    private boolean firstTime=true;
    private String context = Constants.DEFAULT;
    private String logFileName = Constants.NOT_INITIALIZED;
    private boolean logFileNameAlreadySet=false;
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
        logFileName = startDate + "." + className;

    }
    public String getClassName() {
        return this.className;
    }
    public void setLogFileName(String logFileName) {
        String myName="setLogFileName";
        String myArea ="run";

        if(!logFileNameAlreadySet) {
            this.logFileName = logFileName;
            log(myName, Constants.DEBUG, myArea, "log file set to >" + getLogFileNameOnly() +"<.");
        } else {
            log(myName, Constants.VERBOSE, myArea, "log file name was already set to >" + getLogFileNameOnly() +"<.");
        }
        this.logFileNameAlreadySet =true;
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

        log(myName, Constants.DEBUG, myArea, "Using databaseName >" + fitnesseDatabaseName +"<.");
        setDatabaseConnection(fitnesseDatabaseName);
        log(myName, Constants.DEBUG, myArea, "databaseConnection is >" + getDatabaseConnection() +"<.");
        log(myName, Constants.DEBUG, myArea, "actualDatabase is >" + getActualDatabase() +"<.");

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".database", true);
        if(getErrorIndicator())
            return false;
        else setActualDatabase(result);

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

        log(myName, Constants.VERBOSE, myArea, "databaseType ..........>" + getDatabaseType() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseConnection ....>" + getDatabaseConnection() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseDriver ........>" + getDatabaseDriver() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseUrl ...........>" + getDatabaseUrl() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseUrlOptions ....>" + getDatabaseUrlOptions() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseUsername ......>" + getDatabaseUsername() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseTableOwner ....>" + getDatabaseTableOwner() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseSchema ........>" + getDatabaseSchema() + "<.");
        log(myName, Constants.VERBOSE, myArea, "databaseName ..........>" + getDatabaseName() + "<.");
        log(myName, Constants.VERBOSE, myArea, "db2Accelerator ........>" + getAccelerator() + "<.");
        log(myName, Constants.VERBOSE, myArea, "tablePrefix ...........>" + getTableOwnerTablePrefix() + "<.");
        log(myName, Constants.VERBOSE, myArea, "useTablePrefix ........>" + getTableOwnerUseTablePrefix() +"<.");
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
            log(myName, Constants.DEBUG, myLocation, "Password decryption successful.");
        }
        else {
            setError(result,Constants.ERRCODE_DECRYPT);
            log(myName, Constants.ERROR, myLocation, "Password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.");
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
            log(myName, Constants.DEBUG, myLocation, "Password decryption successful.");
        }
        else {
            setError(result,Constants.ERRCODE_DECRYPT);
            log(myName, Constants.ERROR, myLocation, "Password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.");
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
            if (context.equals(Constants.DEFAULT)) {
                logFileName = startDate + "." + className;
            } else {
                logFileName = startDate + "." + context;
            }
            Logging.LogEntry(logFileName, getClassName() +"-" +name, Constants.INFO, "Fixture version >" + getVersion() + "<.");
        }
        Logging.LogEntry(logFileName, getClassName() + "-" +name, level, area, logMessage);

    }

    public static String getVersion() {
        return version;
    }

    private Connection getConnection(String username, String password) throws SQLException {
        String myName="getConnection";
        String myLocation="start";
        String url =getDatabaseUrl();
        if(getDatabaseUrlOptions() != null && ! getDatabaseUrlOptions().isEmpty()) {
            if(! getDatabaseUrlOptions().equalsIgnoreCase(Constants.DEFAULT_PROPVALUE)) {
                url= url + ":" + getDatabaseUrlOptions();
            }
        }
        log(myName, Constants.DEBUG, myLocation, "Database URL for user >" +username +"< is >"
                + url +"<.");

        Connection connection = DriverManager.getConnection(url
                , username
                , password);
        return connection;
    }

    public Connection getUserConnection() throws SQLException {
        return getConnection(getDatabaseUsername(),getDatabaseUsernamePassword());
    }

    public Connection getOwnerConnection() throws SQLException {
        return getConnection(getDatabaseTableOwner(),getDatabaseTableOwnerPassword());
    }
}
