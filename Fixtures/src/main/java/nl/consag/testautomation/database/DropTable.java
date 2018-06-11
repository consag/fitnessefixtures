/**
 * This purpose of this fixture is to drop a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac Beekers
 * @version 10 May 2015
 */
package nl.consag.testautomation.database;

import java.sql.*;
import java.text.*;
import java.util.ArrayList;
import java.util.List;

import nl.consag.testautomation.supporting.*;

import static nl.consag.testautomation.supporting.Constants.propFileErrors;

public class DropTable {
    private static String version ="20180309.0";
	private String className = "DropTable";

    private String logFileName = Constants.NOT_INITIALIZED;
	private String context = Constants.DEFAULT;
	private String startDate = Constants.NOT_INITIALIZED;
    private String notInitialized = Constants.NOT_INITIALIZED;
    private String errorMessage = Constants.NO_ERRORS;
    private int logLevel =3;
    private int logEntries =0;

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
    private String tableOwnerUseTablePrefix; //from connetions.properties
    private boolean useTablePrefix =true;
    private String databaseSchema;
    private boolean useSchema =false;
    private boolean useTableOwner =true;
    private String tableComment = Constants.TABLE_COMMENT;

    private String databaseName =Constants.NOT_PROVIDED;
    private String actualDatabase = Constants.NOT_FOUND;
    private String tableName =Constants.NOT_PROVIDED;

    private String errorCode =Constants.OK;
    private boolean errorIndicator =false;


    private boolean ignoreErrorOnDrop =false;
    
	private int NO_FITNESSE_ROWS_TO_SKIP = 3;


	public DropTable() {
		//Constructors
	      	java.util.Date started = new java.util.Date();
	      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	      	startDate = sdf.format(started);
	      	this.context=className;
	        logFileName = startDate + "." + className ;

	    }
	
	public DropTable(String context) {
	    	java.util.Date started = new java.util.Date();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	startDate = sdf.format(started);
	    	this.context=context;
	        logFileName = startDate + "." + className +"." + context;

	    }

    public void ignoreError(String yesNo) {
        if(yesNo.equals(Constants.YES))
            ignoreErrorOnDrop=true;
        else
            ignoreErrorOnDrop=false;
    }
    private boolean getIgnoreError() {
	    return this.ignoreErrorOnDrop;
    }
    
    public boolean tableDoesNotExistInDatabase(String inTableName, String inDatabase) {
        String myName="tableDoesNotExistInDatabaseInSchema";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        String sqlStatement=Constants.NOT_INITIALIZED;
        String commentStatement=Constants.NOT_INITIALIZED;
        String existStatement=Constants.NOT_INITIALIZED;
        int sqlResult =0;
        boolean rc =false;
        String commentFound=Constants.NOT_FOUND;
        String tableName;

        databaseName=inDatabase;
        myArea="check db type";

        setDatabaseConnection(inDatabase);
        readParameterFile();

        if(useTablePrefix) {
            if (Constants.DEFAULT_PROPVALUE.equals(getTableOwnerTablePrefix())) {
                setTableOwnerTablePrefix(Constants.TABLE_PREFIX);
                tableName = getTableOwnerTablePrefix() + inTableName;
            } else {
                tableName = getTableOwnerTablePrefix() + inTableName;
            }
        } else {
            tableName =inTableName;
        }
        tableName = tableName.toUpperCase();

        if("Oracle".equals(databaseType) || "DB2".equals(databaseType)) {
            sqlStatement = "drop table " + tableName;
            log(myName, Constants.DEBUG, myArea,"SQL statement is >" +sqlStatement +"<.");
            commentStatement ="select comments from user_tab_comments where table_name ='" + tableName +"'";
            existStatement="select count(*) from user_tables where table_name='" + tableName +"'";
        } else {
            logMessage="databaseType >" + databaseType +"< not yet supported";
            log(myName, Constants.ERROR, myArea, logMessage);
            errorMessage=logMessage;
            return false;
        }
         
        /* We limit the drop to tables previously created using the CreateTable fixture
         * The CreateTable fixture uses a specific table comment (Oracle). If the comment is different or does not exist,
         * the DropTable fixture will NOT drop the table
         */
        GetSingleValue dbCol= new GetSingleValue(context);
        dbCol.setDatabaseName(databaseName);
        dbCol.setQuery(existStatement);
        commentFound = dbCol.getColumn();
        
        if (commentFound.equals("0")) {
            if(ignoreErrorOnDrop) {
                logMessage="Table >" + tableName +"< does not exist. Error will be ignored.";
                log(myName, Constants.INFO, myArea, logMessage);
                return true;
            } else {
                logMessage="Table >" + tableName +"< does not exist";
                log(myName, Constants.ERROR, myArea, logMessage);
                errorMessage="Table >" + inTableName +"< does not exist.";
                return false;
            }
        }

        if("Oracle".equals(databaseType)) {
            dbCol.setDatabaseName(databaseName);
            dbCol.setQuery(commentStatement);
            commentFound = dbCol.getColumn();
        
            if (commentFound == null || commentFound.isEmpty() || commentFound.equals("0")){
                errorMessage="A table comment could not be found. If the table exists, it will be dropped anyway.";
            } else {
                if( ! commentFound.equals(tableComment) && ! getIgnoreError()) {
                    errorMessage="A table comment matching the comment issued by the CreateTable fixture was not found. The table will NOT be dropped.";
                    return false;
                }
            }
        }
         
        try {
        myArea="SQL Execution";
           logMessage = "Connecting to >" + getDatabaseConnection() +"< using userID >" + getDatabaseTableOwner() + "<.";
           log(myName, "info", myArea, logMessage);
                connection = DriverManager.getConnection(getDatabaseUrl(), getDatabaseTableOwner(), getDatabaseTableOwnerPassword());
                statement = connection.createStatement();
                logMessage="SQL >" + sqlStatement +"<.";
                log(myName, "info", myArea, logMessage);
                sqlResult= statement.executeUpdate(sqlStatement);
            logMessage="SQL returned >" + Integer.toString(sqlResult) + "<.";
           log(myName, "info", myArea, logMessage);

          statement.close();
          connection.close();      
            rc=true;
              }  catch (SQLException e) {
                myArea="Exception handling";
                logMessage = "SQLException at >" + myName + "<. Error =>" + e.toString() +"<.";
                log(myName, "ERROR", myArea, logMessage);
                if(e.toString().contains("ORA-00942") && ignoreErrorOnDrop) {
                    errorMessage=Constants.NO_ERRORS;
                    rc=true;
                } else {
                errorMessage=logMessage;
                 rc =false;
                }
              }
    
         return rc;
    }
    
    public String tableNameFor (String inTableName) {
        return getTableOwnerTablePrefix() + inTableName;

    }
    
    public boolean tableDoesNotExist(String inTableName) {
        String myName="tableDoesNotExist";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;

        String tableName = getTableOwnerTablePrefix() + inTableName;
        String nrTablesFound =notInitialized;
        
        myArea="check db type";
        readParameterFile();
        if(databaseType.equals("Oracle")) {
            query="SELECT count(*) tblcount FROM user_tables WHERE table_name ='" +tableName +"'";
        } else {
            logMessage="databaseType >" + databaseType +"< not yet supported";       log(myName, "info", myArea, logMessage);  
            errorMessage=logMessage;
            return false;
        }
        GetSingleValue dbCol= new GetSingleValue(className);
        dbCol.setDatabaseName(databaseName);
        dbCol.setQuery(query);
        nrTablesFound = dbCol.getColumn();
        
        if (nrTablesFound.equals("0") ) 
        return true;

        errorMessage="Count on tables returned >" + nrTablesFound +"<.";
        return false;
    }
    
    

    public String errorMessage() {
        return errorMessage;
    }

    private void log(String name, String level, String location, String logText) {
           if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
           }
        logEntries++;
        if(logEntries ==1) {
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version", getVersion());
        }
            Logging.LogEntry(logFileName, name, level, location, logText);
       }

    /**
    * @return
    */
    public String getLogFilename() {
            return logFileName + ".log";
    }

    /**
    * @param level
    */
    public void setLogLevel(String level) {
    String myName ="setLogLevel";
    String myArea ="determineLevel";
    
    logLevel =Constants.logLevel.indexOf(level.toUpperCase());
    if (logLevel <0) {
       log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
       logLevel =3;
    }
    
    log(myName,Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
    }

    /**
    * @return
    */
    public String getLogLevel() {
    return Constants.logLevel.get(getIntLogLevel());
    }

    /**
    * @return
    */
    public Integer getIntLogLevel() {
    return logLevel;
    }

        public static String getVersion() {
            return version;
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

    public void readParameterFile() {
        // database connection string has to be set by calling party
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;
        String result = Constants.NOT_FOUND;

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".database", true);
        if(getErrorIndicator())
            return;
        else setActualDatabase(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".databasetype", true);
        if(getErrorIndicator())
            return;
        else setDatabaseType(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".driver", true);
        if(getErrorIndicator())
            return;
        else setDatabaseDriver(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getActualDatabase() +".url", true);
        if(getErrorIndicator())
            return;
        else setDatabaseUrl(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".username", false);
        if(getErrorIndicator())
            return;
        else setDatabaseUserId(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".username.password", false);
        if(getErrorIndicator())
            return;
        else setDatabasePassword(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection()+".tableowner", false);
        if(getErrorIndicator())
            return;
        else setDatabaseTableOwner(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".tableowner.password", false);
        if(getErrorIndicator())
            return;
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
            log(myName, Constants.INFO, myArea, "Table prefix has been set to >" + getTableOwnerTablePrefix() +"<.");
        }

    }

    private void setTableOwnerUseTablePrefix(boolean b) {
        this.useTablePrefix =b;
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

    private String getDatabaseTableOwnerPassword() {
        String myName ="getDatabaseTableOwnerPassword";
        String myLocation ="start";
        Decrypt decrypt = new Decrypt();
        String result = Decrypt.decrypt(databaseTableOwnerPassword);
        if(Constants.OK.equals(decrypt.getErrorCode())) {
            log(myName, Constants.VERBOSE, myLocation, "Password decryption successful.");
        }
        else {
            setError(result,Constants.ERRCODE_DECRYPT);
            log(myName, Constants.ERROR, myLocation, "Password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.");
        }
        return result;

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

    private void setError(String errorCode, String errorMessage) {
        this.errorCode =errorCode;
        this.errorMessage = errorMessage;
    }

}