package nl.consag.testautomation.supporting;

import static nl.consag.testautomation.supporting.Constants.propFileErrors;

public class DatabaseConnection {
    private String className ="DatabaseConnection";
    private String version ="20180313.0";


    private int logLevel =3;
    private int logEntries =0;

    private String logFileName = Constants.NOT_INITIALIZED;

    private String databaseDriver;
    private String databaseUrl;
    private String databaseUserId;
    private String databasePassword;
    private String databaseConnection;
    private String query;
    private String databaseType;
    private String databaseTableOwner;
    private String databaseTableOwnerPassword;
    private String tableOwnerTablePrefix;
    private String tableOwnerUseTablePrefix; //from connections.properties
    private boolean useTablePrefix =true;
    private String databaseSchema;
    private boolean useSchema =false;
    private boolean useTableOwner =true;
    private String tableComment = Constants.TABLE_COMMENT;
    private String idaaName =Constants.NOT_PROVIDED;
    private String databaseName =Constants.NOT_PROVIDED;
    private String actualDatabase = Constants.NOT_FOUND;
    private String tableName =Constants.NOT_PROVIDED;
    private boolean errorIndicator =false;

    private String errorCode = Constants.OK;
    private String errorMessage = Constants.NOERRORS;

    //constructors
    public DatabaseConnection (String propertiesFile, String databaseConnection) {
        setDatabaseConnection(databaseConnection);
        getDatabaseProperties(propertiesFile);
    }

    public void getDatabaseProperties(String propertiesFile) {

        // database connection string has to be set by calling party
        String myName = "readPropertiesFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String result = Constants.NOT_FOUND;

        result =getProperty(propertiesFile, getDatabaseConnection() +".database", true);
        if(getErrorIndicator())
            return;
        else setActualDatabase(result);

        result =getProperty(propertiesFile, getActualDatabase() +".databasetype", true);
        if(getErrorIndicator())
            return;
        else setDatabaseType(result);

        result =getProperty(propertiesFile, getActualDatabase() +".driver", true);
        if(getErrorIndicator())
            return;
        else setDatabaseDriver(result);

        result =getProperty(propertiesFile, getActualDatabase() +".url", true);
        if(getErrorIndicator())
            return;
        else setDatabaseUrl(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".username", false);
        if(getErrorIndicator())
            return;
        else setDatabaseUserId(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".username.password", false);
        if(getErrorIndicator())
            return;
        else setDatabasePassword(result);

        result =getProperty(propertiesFile, getDatabaseConnection()+".tableowner", false);
        if(getErrorIndicator())
            return;
        else setDatabaseTableOwner(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".tableowner.password", false);
        if(getErrorIndicator())
            return;
        else setDatabaseTableOwnerPassword(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".tableowner.usetableprefix", false);
        if(!getErrorIndicator())
            setTableOwnerUseTablePrefix(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".tableowner.tableprefix", false);
        if(!getErrorIndicator())
            setTableOwnerTablePrefix(result);

        result =getProperty(propertiesFile, getDatabaseConnection() +".schemaname", false);
        if(!getErrorIndicator())
            setDatabaseSchema(result);

        log(myName, Constants.INFO, myArea, "databaseType ..........>" + getDatabaseType() + "<.");
        log(myName, Constants.INFO, myArea, "databaseConnection ....>" + getDatabaseConnection() + "<.");
        log(myName, Constants.INFO, myArea, "databaseDriver ........>" + getDatabaseDriver() + "<.");
        log(myName, Constants.INFO, myArea, "databaseUrl ...........>" + getDatabaseUrl() + "<.");
        log(myName, Constants.INFO, myArea, "databaseUserId ........>" + getDatabaseUserId() + "<.");
        log(myName, Constants.INFO, myArea, "databaseTableOwner ....>" + getDatabaseTableOwner() + "<.");
        log(myName, Constants.INFO, myArea, "databaseSchema ........>" + getDatabaseSchema() + "<.");
        log(myName, Constants.INFO, myArea, "tablePrefix ...........>" + getTableOwnerTablePrefix() + "<.");
        log(myName, Constants.INFO, myArea, "useTablePrefix ........>" + getTableOwnerUseTablePrefix() +"<.");
        if(Constants.FALSE.equalsIgnoreCase(getTableOwnerUseTablePrefix())) {
            setUseTablePrefix(false);
        } else {
            setUseTablePrefix(true);
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
            log(myName, Constants.INFO, myArea, "Table prefix has been set to >" + getTableOwnerTablePrefix() +"<.");
        }


    }

    private String getProperty(String propertiesFile, String key, boolean mustExist) {
        String result =Constants.NOT_FOUND;
        Parameters parameters = new Parameters();

        result =parameters.getPropertyVal(propertiesFile, key);
        if(mustExist && propFileErrors.contains(parameters.getResult())) {
            setError(result, "Error retrieving property >" + key + "< from >" + propertiesFile + "<.");
            setErrorIndicator(true);
            return parameters.getResult();
        }
        setErrorIndicator(false);
        return result;
    }

    private boolean getErrorIndicator(){
        return this.errorIndicator;
    }

    private void setError(String errCode, String errMessage) {
        setErrorCode(errCode);
        setErrorMessage(errMessage);
    }
    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUserId() {
        return databaseUserId;
    }

    public void setDatabaseUserId(String databaseUserId) {
        this.databaseUserId = databaseUserId;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(String databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseTableOwner() {
        return databaseTableOwner;
    }

    public void setDatabaseTableOwner(String databaseTableOwner) {
        this.databaseTableOwner = databaseTableOwner;
    }

    public String getDatabaseTableOwnerPassword() {
        return databaseTableOwnerPassword;
    }

    public void setDatabaseTableOwnerPassword(String databaseTableOwnerPassword) {
        this.databaseTableOwnerPassword = databaseTableOwnerPassword;
    }

    public String getTableOwnerTablePrefix() {
        return tableOwnerTablePrefix;
    }

    public void setTableOwnerTablePrefix(String tableOwnerTablePrefix) {
        this.tableOwnerTablePrefix = tableOwnerTablePrefix;
    }

    public String getTableOwnerUseTablePrefix() {
        return tableOwnerUseTablePrefix;
    }

    public void setTableOwnerUseTablePrefix(String tableOwnerUseTablePrefix) {
        this.tableOwnerUseTablePrefix = tableOwnerUseTablePrefix;
    }

    public boolean isUseTablePrefix() {
        return useTablePrefix;
    }

    public void setUseTablePrefix(boolean useTablePrefix) {
        this.useTablePrefix = useTablePrefix;
    }

    public String getDatabaseSchema() {
        return databaseSchema;
    }

    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }

    public boolean isUseSchema() {
        return useSchema;
    }

    public void setUseSchema(boolean useSchema) {
        this.useSchema = useSchema;
    }

    public boolean isUseTableOwner() {
        return useTableOwner;
    }

    public void setUseTableOwner(boolean useTableOwner) {
        this.useTableOwner = useTableOwner;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
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

    public String getActualDatabase() {
        return actualDatabase;
    }

    public void setActualDatabase(String actualDatabase) {
        this.actualDatabase = actualDatabase;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isErrorIndicator() {
        return errorIndicator;
    }

    public void setErrorIndicator(boolean errorIndicator) {
        this.errorIndicator = errorIndicator;
    }

    private void log(String name, String level, String location, String logText) {
        if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        logEntries++;
        if(logEntries ==1) {
            Logging.LogEntry(getLogFileName(), className, Constants.INFO, "Fixture version", getVersion());
        }

        Logging.LogEntry(getLogFileName(), name, level, location, logText);
    }

    public String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }
    public void setIntLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }
    public String getVersion() {
        return version;
    }

    public String getLogFileName() {
        return this.logFileName;
    }
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

}
