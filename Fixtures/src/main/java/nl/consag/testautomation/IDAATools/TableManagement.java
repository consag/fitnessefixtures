package nl.consag.testautomation.IDAATools;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.DatabaseConnection;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.supporting.Parameters;

import static nl.consag.testautomation.supporting.Constants.propFileErrors;

public class TableManagement {
    private String className = "tableManagement";
    private String version ="20180313.0";


    private int logLevel =3;
    private int logEntries =0;

    private String logFileName = Constants.NOT_INITIALIZED;

    private static String errorMessage =Constants.NO_ERRORS;
    private static String errorCode =Constants.OK;
    private static String result =Constants.OK;
    private static String resultMessage =Constants.NO_ERRORS;

    public String loadTable(String accelName, String dbName, String schemaName, String tableName) {


        DatabaseConnection dbc = new DatabaseConnection(Constants.CONNECTION_PROPERTIES, dbName);
        dbc.setLogFileName(getLogFilename());
        dbc.setIntLogLevel(getIntLogLevel());

        Connection connection = null;

        if (!Constants.OK.equals(result)) {
            setErrorMessage(Constants.ERROR, "Error retrieving db connection information for >" + dbName + "<.");
            return Constants.ERROR;
        }

        try {
            connection =
                DriverManager.getConnection(dbc.getDatabaseUrl() + ":specialRegisters=CURRENT QUERY ACCELERATION=ALL;retrieveMessagesFromServerOnGetMessage=true;"
                                            , dbc.getDatabaseUserId(), dbc.getDatabasePassword());
        } catch (Exception e) {
            setErrorMessage(Constants.ERROR, "The connection failed.  Check your connection parameters in >" + Constants.CONNECTION_PROPERTIES
                    + "<. urlt=>" + dbc.getDatabaseUrl() + "< user=>" + dbc.getDatabaseUserId()
                    + "<. Error: " + e.toString());
            return Constants.ERROR;
        }
        
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:tableSetForLoad xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\">"
            + "<table name =\"" + tableName + "\" schema =\"" + schemaName + "\"></table>"
            + "</dwa:tableSetForLoad>";

        CallableStatement stmt =null;
        String loadResult;
        
        try {
            stmt = connection.prepareCall("CALL SYSPROC.ACCEL_LOAD_TABLES('" + accelName + "', 'NONE', ?, ?)");
            stmt.setString(1, xml);     // input xml
            stmt.registerOutParameter(2, java.sql.Types.CHAR);  // output xml message
            stmt.execute();
            loadResult = null;
            try {
                loadResult = stmt.getString(2);
                if(loadResult.contains("The operation was completed successfully.")) {
                    setResultMessage(Constants.OK, loadResult);
                } else {
                    setErrorMessage(Constants.ERROR, loadResult);
                }
                /*
                 * <?xml version="1.0" encoding="UTF-8" ?><dwa:messageOutput xmlns:dwa="http://www.ibm.com/xmlns/prod/dwa/2011" version="1.0"> 
                 * <message severity="informational" reason-code="AQT20014I"><text>The following datawas transferred to the &quot;SIEBIDQ&quot;.&quot;IDAA_QUERY_HIST&quot; table: 
                 * Scope: Full table, number of rows: 10997, amount of data: 4 MB, time: 0 seconds.</text><description>This message, which is issued for each transaction of the same
                 *  type, reports information about the table data that was transferred by the SYSPROC.ACCEL_LOAD_TABLES or the SYSPROC.ACCEL_ARCHIVE_TABLES stored procedure.</description>
                 *  <action></action></message><message severity="informational" reason-code="AQT10000I"><text>The operation was completed successfully.</text><description>
                 *  Success message for the XML MESSAGE output parameter of each stored procedure.</description><action></action></message></dwa:messageOutput>
                 */
                //System.out.println("Accelerator load table returned: " + getLoadResult);
            } catch (Exception e) {
                setErrorMessage(Constants.ERRCODE_SQL_IDAAGETRESULT,"Could not get result: " +e.toString());
            }
            stmt.close();
        } catch (SQLException e) {
            setErrorMessage(Constants.ERRCODE_SQL_IDAASQLERROR,"SQL Error >" +e.toString() +"<.");
        }

        return getResult();        
        
    }


    private void setErrorMessage(String errCode, String msg) {
        setErrorCode(errCode);
        setResult(Constants.ERROR);
        errorMessage =msg;
    }
    public String getErrorMessage(){
        return errorMessage;
    }


    private void setErrorCode(String errCode) {
        setResult(Constants.ERROR);
        errorCode = errCode;
    }
    public String getErrorCode(){
        return errorCode;
    }

    private void setResult(String resultFound) {
        result = resultFound;
    }
    public String getResult(){
        return result;
    }

    private void setResultMessage(String code, String msg) {
        setResult(code);
        setResultMessage(msg);
    }

    private void setResultMessage(String msg) {
        resultMessage =msg;
    }
    public String getResultMessage() {
        return resultMessage;
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
    public String getVersion() {
        return version;
    }

}
