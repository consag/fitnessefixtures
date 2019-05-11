package nl.jacbeekers.testautomation.fitnesse.IDAATools;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import nl.jacbeekers.testautomation.fitnesse.database.ConnectionProperties;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TableManagement {
    private String className = "tableManagement";
    private String version = "20180706.4";

    private String databaseName = Constants.NOT_PROVIDED;
    private String accelerator = Constants.NOT_PROVIDED;

    private int logLevel = 3;
    private int logEntries = 0;

    private String logFileName = Constants.NOT_INITIALIZED;

    private String errorMessage = Constants.NO_ERRORS;
    private String errorCode = Constants.OK;

    private String startDate = Constants.NOT_INITIALIZED;
    private Object context = Constants.NOT_INITIALIZED;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    public TableManagement() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;

    }

    /**
     * @param context in which the fixture is called, used in log file name to more easily identify the area. Also used in reporting.
     */
    public TableManagement(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + className + "." + context;
    }

    public String checkTable(String accelName, String dbName, String schemaName, String tableName) {
        String myName = "checkTable";
        String myArea = "init";
        Connection connection = null;
        CallableStatement stmt = null;
        String xmlResult= Constants.NOT_INITIALIZED;

        setDatabaseName(dbName);
        setAccelerator(accelName);

        myArea = "readParameterFile";
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());
        try {
            connection = connectionProperties.getUserConnection();
        } catch (Exception e) {
            setError(Constants.ERROR, "The connection failed.  Check your connection parameters in >" + Constants.CONNECTION_PROPERTIES
                    + "<. database=>" + connectionProperties.getDatabase() + "< user=>" + connectionProperties.getDatabaseUsername()
                    + "<. Error: " + e.toString());
            return Constants.ERROR;
        }
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<aqttables:tableSet xmlns:aqttables=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\">"
                + "<table name=\"" + tableName + "\" schema=\"" + schemaName + "\" />"
                + "</aqttables:tableSet>";
        try {
            stmt = connection.prepareCall("CALL SYSPROC.ACCEL_GET_TABLES_INFO('" + getAccelerator() + "', ?, ?)");

            stmt.setString(1, xml);     // input xml
            stmt.registerOutParameter(2, java.sql.Types.CHAR);  // output xml message
            executeStatement(connection, xml, stmt);
            xmlResult =stmt.getString(2);
            log(myName, Constants.DEBUG, myArea, "xmlResult is ==>" + xmlResult +"<==");
        } catch (SQLException e) {
            setError(Constants.ERROR, "Error Preparing SQL statement >" + xml + "<.");
        }



        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            setError(Constants.INFO, "Could not commit/close the connection.");
        }

        return getErrorMessage();

    }

    private String executeStatement(Connection connection, String inputXML, CallableStatement stmt) {

        String loadResult;
        String loadResultRaw;

        try {
            stmt.execute();
            loadResult = null;
            try {
                loadResultRaw = stmt.getString(2);
                loadResult = parseResult(loadResultRaw);
                if (loadResultRaw.contains("<action></action>")) {
                    setError(Constants.OK, loadResult);
                } else {
                    setError(Constants.ERROR, loadResult);
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
                setError(Constants.ERRCODE_SQL_IDAAGETRESULT, "Could not get result: " + e.toString());
            }
            stmt.close();
        } catch (SQLException e) {
            setError(Constants.ERRCODE_SQL_IDAASQLERROR, "SQL Error >" + e.toString() + "<.");
        }
        return getErrorCode();

    }

    private String parseResult(String rawResult) {
        String myName = "parseResult";
        String myArea = "Process";
        String result = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "rawXML ==>" + rawResult + "<.");

        try {
            org.w3c.dom.Document document = loadXMLFromString(rawResult);
            result = document.getElementsByTagName("text").item(0).getTextContent();
        } catch (Exception e) {
            setError(Constants.ERROR, "XML parse error");
            result = rawResult;
        }

        log(myName, Constants.DEBUG, myArea, "parsed outcome ==>" + result + "<.");
        return result;
    }

    private org.w3c.dom.Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public String loadTable(String accelName, String dbName, String schemaName, String tableName) {
        String myName = "loadTable";
        String myArea = "init";
        Connection connection = null;
        CallableStatement stmt = null;
        String xml = Constants.NOT_INITIALIZED;


        setDatabaseName(dbName);
        setAccelerator(accelName);

        myArea = "readParameterFile";
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());

        try {
            connection = connectionProperties.getUserConnection();
        } catch (Exception e) {
            setError(Constants.ERROR, "The connection failed.  Check your connection parameters in >" + Constants.CONNECTION_PROPERTIES
                    + "<. database=>" + connectionProperties.getDatabase() + "< user=>" + connectionProperties.getDatabaseUsername()
                    + "<. Error: " + e.toString());
            return Constants.ERROR;
        }

        try {
            xml =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:tableSetForLoad xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\">"
                            + "<table name =\"" + tableName + "\" schema =\"" + schemaName + "\"></table>"
                            + "</dwa:tableSetForLoad>";
            stmt = connection.prepareCall("CALL SYSPROC.ACCEL_LOAD_TABLES('" + accelName + "', 'NONE', ?, ?)");
            stmt.setString(1, xml);     // input xml
            stmt.registerOutParameter(2, java.sql.Types.CHAR);  // output xml message

        } catch (SQLException e) {
            setError(Constants.ERROR, "Error Preparing SQL statement >" + xml + "<.");
        }

        executeStatement(connection, xml, stmt);

        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            setError(Constants.INFO, "Could not commit/close the connection.");
        }

        return getErrorMessage();

    }


    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
    }

    public String getAccelerator() {
        return this.accelerator;
    }

    private void setError(String errCode, String msg) {
        setErrorCode(errCode);
        setErrorMessage(msg);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }


    private void setErrorCode(String errCode) {
        errorCode = errCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    private void log(String name, String level, String location, String logText) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        logEntries++;
        if (logEntries == 1) {
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version", getVersion());
        }

        Logging.LogEntry(logFileName, name, level, location, logText);
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public String getLogFilename() {
        return logFileName + ".log";
    }

    /**
     * @param level
     */
    public void setLogLevel(String level) {
        String myName = "setLogLevel";
        String myArea = "determineLevel";

        logLevel = Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, Constants.WARNING, myArea, "Wrong log level >" + level + "< specified. Defaulting to level 3.");
            logLevel = 3;
        }

        log(myName, Constants.INFO, myArea, "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
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

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + databaseName + "<.");
        connectionProperties.refreshConnectionProperties(databaseName);

    }

}
