/**
 * This purpose of this fixture is to a temporary table based on a database query or using by specifying columns
 * By design, the table name is ALWAYS prefixed
 * The input parameters are provided by a script in the FitNesse wiki. 
 * @author Jac Beekers
 * @since 10 May 2015
 * @version 20160106.1
 * 
 */
package nl.consag.testautomation.database;

import java.sql.*;

import java.text.*;

import java.util.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.Decrypt;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.supporting.Parameters;

import static nl.consag.testautomation.supporting.Constants.propFileErrors;

public class CreateTable {
    private String className = "CreateTable";
    private static String version ="20180404.1";

    private int logLevel =3;
    private int logEntries =0;

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;

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
    private String idaaName =Constants.NOT_PROVIDED;
    private String databaseName =Constants.NOT_PROVIDED;
    private String actualDatabase = Constants.NOT_FOUND;
    private String tableName =Constants.NOT_PROVIDED;
    private boolean errorIndicator =false;

    private List<colDefinition> columns = new ArrayList<colDefinition>();

    private String errorMessage = Constants.NO_ERRORS;
    private String errorCode=Constants.OK;

    public CreateTable() {
        //Constructors
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = className;
        logFileName = startDate + "." + className ;

    }

    public CreateTable(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className +"." + context;

    }

    public String createTable(String tableName, String columnList) {
        String myName="createTable";
        String myArea="run";
        String logMessage=Constants.NOT_INITIALIZED;
        String sqlStatement =Constants.NOT_INITIALIZED;
        String commentStatement =Constants.NOT_INITIALIZED;
        String rc =Constants.OK;
        
        sqlStatement="CREATE TABLE " +tableName +"(" +columnList +")";
        //If DB2 and Database name provided, add it
        //If DB2 and Accelerator name is provided, add it
        if(databaseType.equals("DB2")) {
            if(getDatabase().equals(Constants.NOT_PROVIDED)) {
                if(getUseSchema() && ! Constants.DEFAULT_PROPVALUE.equals(getDatabaseSchema())) {
                    log(myName,Constants.DEBUG, myArea, "UseSchema was set. Added IN DATABASE " + getDatabaseSchema());
                    sqlStatement += " IN DATABASE " + getDatabaseSchema();
                } else {
                    log(myName, Constants.DEBUG, myArea, "getDatabase not provided and useSchema not set or DatabaseSchema not sset in propfile.");
                }
            } else {
                sqlStatement +=" IN DATABASE " + getDatabase();
                log(myName, Constants.DEBUG, myArea, "getDatabase provided. Added IN DATABASE.");
            }
            if(!getAccelerator().equals(Constants.NOT_PROVIDED)) {
                sqlStatement +=" IN ACCELERATOR " + getAccelerator();
            }
        }

        logMessage="Generated SQL statement is >" + sqlStatement +"<.";
        log(myName,Constants.DEBUG, myArea, logMessage);
        
        commentStatement = "comment on table " + tableName + " is '" + getTableComment() + "'";
        logMessage="Comment statement is >" + commentStatement +"<.";
        log(myName,Constants.DEBUG, myArea, logMessage);

        rc=execSQL(sqlStatement,commentStatement);
        return rc;
    }


    /**
     * @param inTableName
     * @param inDatabase - database connection (Note: this is NOT the database name as known in e.g. DB2, but the database connection in the properties file)
     * @param inSelectStmt: The select statement that will be used to create the table
     * @return
     */
    public boolean tableExistsInDatabaseUsing(String inTableName, String inDatabase, String inSelectStmt) {
        String myName = "tableExistsInDatabaseUsing";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;
        String sqlStatement = Constants.NOT_INITIALIZED;
        String commentStatement = Constants.NOT_INITIALIZED;
        String rc = Constants.OK;
        String tableName = Constants.TABLE_PREFIX + inTableName;

        setDatabaseConnection(inDatabase);

            sqlStatement = "create table " + tableName + " as " + inSelectStmt;
            commentStatement = "comment on table " + tableName + " is '" + getTableComment() + "'";
        
        rc=execSQL(sqlStatement, commentStatement);

        if(Constants.OK.equals(rc))
            return true; else
        return false;
    }

    private String execSQL(String sqlStatement, String commentStatement) {
        String myName="ExecSQL";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        int sqlResult = 0;
        String rc=Constants.OK;

//        readParameterFile();

        myArea="Check dbtype";
        if ("Oracle".equals(getDatabaseType()) || "DB2".equals(getDatabaseType())) {
        } else {
            logMessage = "databaseType >" + getDatabaseType() + "< not yet supported";
            log(myName, Constants.ERROR, myArea, logMessage);
            setError(Constants.ERROR,logMessage);
            return getErrorCode();
        }
    
    try {
        myArea = "SQL Execution";
        logMessage = "Connecting to >" + getDatabaseConnection() + "< using userID (table owner) >" + getDatabaseTableOwner() + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        connection = DriverManager.getConnection(getDatabaseUrl(), getDatabaseTableOwner(), getDatabaseTableOwnerPassword());
        statement = connection.createStatement();
        logMessage = "SQL >" + sqlStatement + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        sqlResult = statement.executeUpdate(sqlStatement);
        logMessage = "SQL returned >" + Integer.toString(sqlResult) + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        statement.close();

        statement = connection.createStatement();
        logMessage = "SQL >" + commentStatement + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        sqlResult = statement.executeUpdate(commentStatement);
        logMessage = "SQL returned >" + Integer.toString(sqlResult) + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        statement.close();

        connection.close();
        rc = Constants.OK;
        setError(Constants.OK,Constants.NO_ERRORS);
        
    } catch (SQLException e) {
        myArea = "Exception handling";
        logMessage = "SQLException at >" + myName + "<. Error =>" + e.toString() + "<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        setError(Constants.ERROR, logMessage);
        rc = Constants.ERROR;
    }
    return rc;
    
    }

    public String errorMessage() {
        return errorMessage;
    }

    public String tableNameFor(String inTableName) {
        return Constants.TABLE_PREFIX + inTableName;
    }

    public boolean tableExists(String inTableName) {
        String myName = "tableExists";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;
        String tableName = Constants.NOT_INITIALIZED;
        String nrTablesFound = Constants.NOT_INITIALIZED;
        
        if(tableName.indexOf(Constants.TABLE_PREFIX) >-1) {
            tableName = inTableName;
        } else {
            tableName = Constants.TABLE_PREFIX + inTableName;
        }
        
        readParameterFile();
        if(getErrorIndicator()) return false;

        /*
         * need to compile for 1.6, so can't use switch :(
         * 
        switch (databaseType) {
        case "Oracle":
            query = "SELECT count(*) tblcount FROM user_tables WHERE table_name ='" + tableName + "'";
            break;
        case "DB2":
            //TODO: Decide how to handle DB2 LUW and DB2 z/OS. For now: assume z/OS.
            query = "SELECT count(*) tblcount FROM SYSIBM.SYSTABLES WHERE name ='" + tableName + "'";
            break;
        default:
            logMessage = "databaseType >" + databaseType + "< not yet supported";
            log(myName, "info", myArea, logMessage);
            setError(Constants.ERROR,logMessage);
            return false;
        }
*/
        if("Oracle".equals(databaseType)) {
            query = "SELECT count(*) tblcount FROM user_tables WHERE table_name ='" + tableName + "'";
        }
        else {
            if ("DB2".equals(databaseType)) {
                query = "SELECT count(*) tblcount FROM SYSIBM.SYSTABLES WHERE name ='" + tableName + "'";
                }
            else {
            logMessage = "databaseType >" + databaseType + "< not yet supported";
            log(myName, "info", myArea, logMessage);
            setError(Constants.ERROR,logMessage);
            return false;
            }
        }
        
        GetSingleValue dbCol = new GetSingleValue(className);
        dbCol.setDatabaseName(databaseConnection);
        dbCol.setQuery(query);
        dbCol.setLogLevel(getLogLevel());
        nrTablesFound = dbCol.getColumn();

        if (nrTablesFound.equals("1"))
            return true;
        setError(Constants.OK,"Table not found, or multiple occurrences found. NrTablesFound =>" +nrTablesFound +"<.");
        return false;

    }

    public boolean userHasPrivilege(String inUser, String inTableName) {

        return false;
    }

    public String synonymForUser(String inTableName, String inUserName) {

        return Constants.NOT_IMPLEMENTED;
    }

    public void setDatabaseConnection(String dbConn) {
        this.databaseConnection=dbConn;
    }
    public String getDatabaseConnection() {
        return databaseConnection;
    }
    public void setTableComment(String comm) {
        this.tableComment = comm;
    }
    public String getTableComment() {
        return tableComment;
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

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".databasename", false);
        if(!getErrorIndicator())
            setDatabaseName(result);

        result =getProperty(Constants.CONNECTION_PROPERTIES, getDatabaseConnection() +".accelerator", false);
        if(!getErrorIndicator())
            setAccelerator(result);

        log(myName, Constants.INFO, myArea, "databaseType ..........>" + getDatabaseType() + "<.");
        log(myName, Constants.INFO, myArea, "databaseConnection ....>" + getDatabaseConnection() + "<.");
        log(myName, Constants.INFO, myArea, "databaseDriver ........>" + getDatabaseDriver() + "<.");
        log(myName, Constants.INFO, myArea, "databaseUrl ...........>" + getDatabaseUrl() + "<.");
        log(myName, Constants.INFO, myArea, "databaseUserId ........>" + getDatabaseUserId() + "<.");
        log(myName, Constants.INFO, myArea, "databaseTableOwner ....>" + getDatabaseTableOwner() + "<.");
        log(myName, Constants.INFO, myArea, "databaseSchema ........>" + getDatabaseSchema() + "<.");
        log(myName, Constants.INFO, myArea, "databaseName ..........>" + getDatabaseName() + "<.");
        log(myName, Constants.INFO, myArea, "db2Accelerator ........>" + getAccelerator() + "<.");
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

    private void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
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

    /**
     * @since 20160106.0
     * @return fixture version number
     */
    public static String getVersion() {
        return version;
    }

    public void setTableName(String tblName) {
        this.tableName=tblName;
    }
    public String getTableName() {
        return this.tableName;
    }

    /**
     * @param dbName for DB2, the database name for the table
     */
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
    public boolean addColumnDataType(String colName, String dataType) {
        String myName="addColumnDataType";
        String myArea="run";
        String logMessage=Constants.NO_ERRORS;
        
        logMessage="Adding column >" +colName+"< to list of columns...";
        log(myName, Constants.DEBUG, myArea, logMessage);
        colDefinition colDef = new colDefinition();
        colDef.colName=colName;
        colDef.dataType=dataType;
        columns.add(colDef);
        
        logMessage="Done. Current # columns: >" +columns.size() +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        return true;    
    }
    
    public boolean createTableIs(String tableName, String expectedResult) {
        String myName="createTableIs";
        String myArea="run";
        String logMessage=Constants.NO_ERRORS;
        String sqlStatement=Constants.NOT_INITIALIZED;
        String commentStatement=Constants.NOT_INITIALIZED;
        String rc=Constants.OK;
        String colList=Constants.NOT_PROVIDED;
        
        if(!getTableName().equalsIgnoreCase(Constants.NOT_PROVIDED) && !tableName.equalsIgnoreCase(getTableName())) {
            logMessage = "Table name has already been set to >" +getTableName() +"<, hence >" +tableName +"< is not applicable";
            if(expectedResult.equals(Constants.ERROR)) {
                logMessage="Expected error: " +logMessage;
                log(myName, Constants.INFO, myArea, logMessage);
                return true;
            }
            log(myName, Constants.ERROR, myArea, logMessage);
            setError(Constants.ERROR,logMessage);
            return false;
        }
        
        if(tableName.indexOf(Constants.TABLE_PREFIX) <0) {
            tableName = Constants.TABLE_PREFIX + tableName;
        }
        
        readParameterFile();
        if(getErrorIndicator()) return false;
        
        myArea="Build create stmt";
        //Loop through columns
        Iterator iCol =columns.iterator();
        int i=0;
        while(iCol.hasNext()) {
            colDefinition cd =(colDefinition) iCol.next();
            i++;
            if(i==1) {
                colList=cd.colName +" " + cd.dataType;                
            } else {
                colList+="," + cd.colName +" " + cd.dataType;
            }
        }
        logMessage="Build column list for create statement =>" + colList +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        
        rc =createTable(tableName, colList);
        commentStatement = "comment on table " + tableName + " is '" + getTableComment() + "'";
        
        logMessage="createTable for >" +sqlStatement +"< returned >" +rc +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        if (expectedResult.equals(rc)) {
                logMessage="Provided expectedResult >" +expectedResult +"< equals createTable result.";
                log(myName, Constants.DEBUG, myArea, logMessage);
                return true;
            }
        logMessage="Provided expectedResult >" +expectedResult +"< does not match createTable result >" +rc +"<.";
        log(myName, Constants.ERROR, myArea, logMessage);
         return false;

    }

    private String convertArrayToString(List<String> arrayString) {
        String myName ="convertArrayToString";
        String myLocation="concatenate columns";
        String concatenatedColumns=" ";
        int i=0;
        while(i < arrayString.size()) {
            if(i==0) {
                concatenatedColumns = arrayString.get(i);
            } else {
                concatenatedColumns += ", " + arrayString.get(i);
            }
            log(myName, Constants.VERBOSE, myLocation, "Column >" + arrayString.get(i) +"<.");
            log(myName, Constants.VERBOSE, myLocation, "Column list so far >" + concatenatedColumns +"<.");
            i++;
        }
        return concatenatedColumns;
    }

    public boolean creationOfTableInDatabaseSchemaAsCloneOfTableInDatabaseSchemaIs(
            String tgtTable
            ,String tgtDatabase
            ,String tgtSchema
            ,String srcTable
            ,String srcDatabase
            ,String srcSchema
            ,String expectedResult
    ) {
        String myName="creationOfTableInDatabaseSchemaAsCloneOfTableInDatabaseSchemaIs";
        String myLocation="Start";
        Connection connection = null;
        List<String> columnList =null;
        String rc =Constants.OK;
        String targetTableName;
        String targetObjectName;

        log(myName, Constants.DEBUG, myLocation, "Database connection set to >" + srcDatabase +"<.");

        setDatabaseConnection(tgtDatabase);
        readParameterFile();

        if(getErrorIndicator()) {
            setError(Constants.ERRCODE_PROPERTIES,"An error occurred while determining connection properties");
            return false;
        }

        if(useTablePrefix) {
            targetTableName = getTableOwnerTablePrefix() + tgtTable;
            log(myName, Constants.DEBUG, myLocation, "useTablePrefix is >true<. Table name set to >" + targetTableName +"<.");
        } else {
            targetTableName = tgtTable;
            log(myName, Constants.DEBUG, myLocation, "useTablePrefix is >false<. Table name is >" + targetTableName +"<.");
        }

        if(!tgtSchema.isEmpty()) {
            targetObjectName = tgtSchema + Constants.DATABASE_OBJECT_DELIMITER + targetTableName;
            log(myName, Constants.DEBUG, myLocation, "Provided schema is >" + tgtSchema +"<. Object name set to >" + targetObjectName +"<.");
        } else {
            if(useSchema) {
                targetObjectName = getDatabaseSchema() + Constants.DATABASE_OBJECT_DELIMITER + targetTableName;
                log(myName, Constants.DEBUG, myLocation, "No schema provided >" + tgtSchema
                        +"<. Schema name " + getDatabaseSchema() +" taken from properties. Object name set to >" + targetObjectName +"<.");
            } else {
                targetObjectName = targetTableName;
                log(myName, Constants.DEBUG, myLocation, "No schema provided >" + tgtSchema
                        +"< and no schema in properties file. Object name set to >" + targetObjectName +"<.");
            }
        }

        log(myName, Constants.INFO, myLocation, "Target object name is >" + targetObjectName +"<.");

        setDatabaseConnection(srcDatabase);
        readParameterFile();

        try {
            myLocation="getConnection";
            log(myName, Constants.DEBUG, myLocation, "Connecting to >" + getDatabaseUrl() +"< as user >" + getDatabaseTableOwner() +"<.");
            connection = DriverManager.getConnection(getDatabaseUrl(), getDatabaseTableOwner(), getDatabaseTableOwnerPassword());
            DatabaseMetaData databaseMetaData = null;
            try {
                myLocation="getMetaData";
                databaseMetaData = connection.getMetaData();
                columnList = getSourceTableDefinition(databaseMetaData, srcSchema.toUpperCase(), srcTable.toUpperCase());
                if(columnList ==null) {
                    setError(Constants.ERRCODE_TABLENOTFOUND, "Column list is empty, probably because the table does not exist.");
                    log(myName, Constants.ERROR, myLocation, getErrorMessage());
                } else {
                    log(myName, Constants.DEBUG, myLocation, "Table has >" + columnList.size() + "< columns.");
                }
                rc = createTable(tgtDatabase, targetObjectName, convertArrayToString(columnList));
                if(!Constants.OK.equals(rc)) {
                    setError(rc,"Error creating table >" + targetObjectName +"<. Error: " + getErrorMessage());
                }
            } catch (SQLException e) {
                setError(Constants.ERRCODE_DBMETADATA, "Error getting metadata. Exception=>"
                        + e.toString() +"<.");
            }

        } catch (SQLException e) {
            setError(Constants.ERRCODE_DBCONNECTION, "Error creating connection. Exception=>"
            + e.toString() +"<.");
        }

        myLocation="Conclusion";
        log(myName,Constants.INFO,myLocation,"Procedure completes with error code >" +getErrorCode() +"<.");
        if(Constants.OK.equals(getErrorCode())) return true;
        else return false;

    }

    private String createTable(String tgtDatabase, String tgtTable, String columnList) {
        String myName = "createTable3args";
        String myLocation ="start";

        log(myName, Constants.DEBUG, myLocation,"Database connection set to >" + tgtDatabase +"<.");
        setDatabaseConnection(tgtDatabase);
        readParameterFile();
        if(getErrorIndicator()) return Constants.ERRCODE_PROPERTIES;

        return createTable(tgtTable, columnList);
    }

    private String getDatabaseUrl() {
        return databaseUrl;
    }

    private List<String> getSourceTableDefinition(DatabaseMetaData databaseMetaData
            ,String schema
            ,String tableName) {
        String myName="getSourceTableDefinition";
        String myLocation="start";
        String logMessage=Constants.NOT_INITIALIZED;

        if(databaseMetaData == null) {
            log(myName, Constants.ERROR, myLocation, "Database connection is null.");
            return null;
        }
        int colCount=0;
        List<colDefinition> columnList = new ArrayList<colDefinition>();
        List<String> columnListAsString = new ArrayList<String>();

        ResultSet columns = null;
        try {
            myLocation="getColumns";
            log(myName, Constants.DEBUG, myLocation, "Getting table columns for table >" +tableName +"<.");
            columns = databaseMetaData.getColumns(null,schema, tableName, null);
            while(columns.next())
            {
                String colDefinitionString;
                colCount++;
                colDefinition col = new colDefinition();
                col.setColName(columns.getString("COLUMN_NAME"));
                col.setDataType(columns.getString("DATA_TYPE"));
                col.setTypeName(columns.getString("TYPE_NAME"));
                col.setColumnSize(columns.getString("COLUMN_SIZE"));
                col.setDecimalDigits(columns.getString("DECIMAL_DIGITS"));
                col.setIsNullable(columns.getString("IS_NULLABLE"));
                //Printing results
                logMessage =col.getColName() + "---" + col.getDataType() + "---" + col.getTypeName() + "---"
                        + col.getColumnSize() + "---" + col.getDecimalDigits() + "---" + col.getIsNullable();
                log(myName,Constants.VERBOSE,myLocation,logMessage);
                colDefinitionString=col.getColName() +" ";
                log(myName, Constants.VERBOSE, myLocation,"Column type is >" +col.getTypeName() +"<.");
                switch (col.getTypeName()) {
                    case Constants.COLUMN_DATATYPE_VARCHAR:
                    case Constants.COLUMN_DATATYPE_VARCHAR2:
                    case Constants.COLUMN_DATATYPE_CHAR:
                    case Constants.COLUMN_DATATYPE_DECIMAL:
                    case Constants.COLUMN_DATATYPE_NUMBER:
                        if(col.getColumnSize()==0) {
                            colDefinitionString += col.getTypeName();
                        } else
                        {
                                colDefinitionString += col.getTypeName() + "(" + col.getColumnSize();
                                if(col.getDecimalDigits() != 0) {
                                    colDefinitionString += "," + col.getDecimalDigits();
                                }
                                colDefinitionString+=")";
                        }
                        break;
                    case Constants.COLUMN_DATATYPE_TIMESTAMP:
                    case Constants.COLUMN_DATATYPE_TIMESTAMP6:
                    case Constants.COLUMN_DATATYPE_TIMESTAMP9:
                    case Constants.COLUMN_DATATYPE_DATE:
                        colDefinitionString += col.getTypeName();
                        break;

                    default:
                        log(myName, Constants.ERROR, myLocation,"Unsupported column type name >" +col.getTypeName() +"<.");
                        setError(Constants.ERRCODE_UNSUPPORTED_DATATYPE, Constants.UNSUPPORTED_DATATYPE
                                + Constants.LOG_SEPARATOR + col.getTypeName());
                        break;
                }
                log(myName, Constants.VERBOSE, myLocation, "column definition determined as >" + colDefinitionString +"<.");
                columnList.add(col);
                columnListAsString.add(colDefinitionString);
            }
        } catch (SQLException e) {
            setError(Constants.ERRCODE_DBMETADATA_COLUMNS,"Error retrieving column information. Exception=>"
            +e.toString() +"<.");
        }
        myLocation="Conclusion";
        log(myName, Constants.INFO, myLocation ,"Found >"
                + Integer.toString(colCount) +"< columns for table >"
                + schema +"." + tableName +"<.");

        return columnListAsString;

    }

    public String tableExistsInDatabaseSchema(
            String tableName
            ,String databaseName
            ,String schemaName
    ) {
        setError(Constants.ERRCODE_NOT_IMPLEMENTED,Constants.NOT_IMPLEMENTED);
        return Constants.NOT_IMPLEMENTED;
    }

    public String doTable() {
        String myName ="doTable";
        String myLocation ="start";

        log(myName, Constants.ERROR, myLocation, Constants.ERRCODE_NOT_IMPLEMENTED + ": " + Constants.NOT_IMPLEMENTED
                + Constants.LOG_SEPARATOR + myName);

        setError(Constants.ERRCODE_NOT_IMPLEMENTED,Constants.NOT_IMPLEMENTED);
        return doTable(myName);
    }

    public String doTable(String context) {
        String myName ="doTableContext";
        String myLocation ="start";

        log(myName, Constants.ERROR, myLocation, Constants.ERRCODE_NOT_IMPLEMENTED + ": " + Constants.NOT_IMPLEMENTED
                + Constants.LOG_SEPARATOR + myName);

        setError(Constants.ERRCODE_NOT_IMPLEMENTED,Constants.NOT_IMPLEMENTED);
        return doTable(context, getLogLevel());
    }

    public String doTable(String context, String logLevel) {
        //TODO: Process a list of table-clone instructions. Use context as part of log filename. Set loglevel to 2nd arg
        String myName ="doTableContextLogLevel";
        String myLocation ="start";

        setLogLevel(logLevel);

        log(myName, Constants.ERROR, myLocation, Constants.ERRCODE_NOT_IMPLEMENTED + ": " + Constants.NOT_IMPLEMENTED
                + Constants.LOG_SEPARATOR + myName);

        setError(Constants.ERRCODE_NOT_IMPLEMENTED,Constants.NOT_IMPLEMENTED);
        return Constants.NOT_IMPLEMENTED;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseUserId(String userId) {
        this.databaseUserId = userId;
    }

    public String getPassword() {
        return databasePassword;
    }

    public void setPassword(String password) {
        this.databasePassword = password;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
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
            log(myName, Constants.VERBOSE, myLocation, "Password decryption successful.");
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

    public String getDatabaseUserId() {
        return databaseUserId;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
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
    private boolean getUseSchema() {
        return this.useSchema;
    }
    private void setUseTableOwner(boolean useTableOwner) {
        this.useTableOwner = useTableOwner;
    }
    private boolean getUseTableOwner() {
        return this.useTableOwner;
    }


    private  class colDefinition {
        private  String colName;
        private  String dataType;
        private String typeName;
        private int columnSize;
        private int decimalDigits;
        private String isNullable;

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getTypeName() {
            return typeName;
        }
        public void setTypeName(String typeName){
            this.typeName = typeName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(String columnSize) {
            if(columnSize ==null)
                this.columnSize=0;
            else
                this.columnSize = Integer.parseInt(columnSize);
        }

        public int getDecimalDigits() {
            return decimalDigits;
        }

        public void setDecimalDigits(String decimalDigits) {
            if(decimalDigits == null)
                this.decimalDigits=0;
            else
                this.decimalDigits = Integer.parseInt(decimalDigits);
        }

        public String getIsNullable() {
            return isNullable;
        }

        public void setIsNullable(String isNullable) {
            this.isNullable = isNullable;
        }
    }

}
