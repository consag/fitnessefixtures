/**
 * This purpose of this fixture is to truncate a database table
 *
 * @author Jac Beekers
 * @version 10 May 2015
 */

package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.sql.*;
import java.text.SimpleDateFormat;


public class TruncateTable {
    //27-07-2013 Change to new log mechanism, preventing java heap space error with fitnesse stdout
    private String className = "TruncateTable";
    private static String version = "20190704.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl = Constants.LOG_DIR;
    private String returnMessage = ""; //text message that is returned to FitNesse
    private boolean logFileNameAlreadySet = false;
    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;

    private String tableName;

    /**
     * @param context
     */
    public TruncateTable(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        this.startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className + "." + context;


    }

    public TruncateTable() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        this.startDate = sdf.format(started);
        this.context = className;
        logFileName = startDate + "." + className;

    }
//----------------------------------------------------------
//Function to read the database from the fitnesse table
//----------------------------------------------------------

    /**
     * @param databasename
     */
    public void setDatabaseName(String databasename) {
        String myName = "setDatabaseName";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.databaseName = databasename;
        logMessage = "database name >" + databaseName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

    }

//----------------------------------------------------------
//Function to get the table name from the fitnesse table
//----------------------------------------------------------

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        String myName = "setTableName";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.tableName = tableName;
        logMessage = "table name >" + tableName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

    }

//----------------------------------------------------------
//Function submit the truncate SQL statement
//----------------------------------------------------------

    /**
     * @return
     */
    public String truncateTable() {
        String myName = "truncateTable";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        String QueryString;
        myArea = "readParameterFile";
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");

        //Get parameters from file
        readParameterFile();

        int updateQuery = 0;

        logMessage = "Method: truncateTable.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        try {
            myArea = "SQL Execution";
            // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
            //   Class.forName(driver);
            // Create a connection to the database
            logMessage = "Connecting to >" + connectionProperties.getActualDatabase() + "< using table owner >"
                    + connectionProperties.getDatabaseTableOwner() + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            connection = connectionProperties.getOwnerConnection();
//                        connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);
            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to create a database.
            // First try truncate
            QueryString = "truncate table " + tableName;
            logMessage = "SQL >" + QueryString + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            updateQuery = statement.executeUpdate(QueryString);
            logMessage = "SQL returned >" + Integer.toString(updateQuery) + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            returnMessage = Constants.OK;
            statement.close();
            connection.close();
        } catch (SQLException e) {
            myArea = "Exception handling";
            logMessage = "SQLException at TRUNCATE >" + e.toString() + "<. Will try a delete instead.";
            log(myName, Constants.WARNING, myArea, logMessage);
            returnMessage = logMessage;
            try {
                myArea = "deleting";
                logMessage = "Connecting to >" + connectionProperties.getActualDatabase() + "< using user >"
                        + connectionProperties.getDatabaseUsername() + "<.";
                log(myName, Constants.DEBUG, myArea, logMessage);
                connection = connectionProperties.getUserConnection();
//                connection = DriverManager.getConnection(url, userId, password);
                statement = connection.createStatement();
                QueryString = "delete from " + tableName;
                logMessage = "Querystring: " + QueryString;
                log(myName, Constants.DEBUG, myArea, logMessage);
                updateQuery = statement.executeUpdate(QueryString);
                if (updateQuery != 0) {
                    logMessage = "Statement processed. Deleted >" + Integer.toString(updateQuery) + "< records.";
                    log(myName, Constants.INFO, myArea, logMessage);
                    returnMessage = Constants.OK;
                } else {
                    logMessage = "Statement processed. Deleted >" + Integer.toString(updateQuery) + "<.";
                    log(myName, "info", myArea, logMessage);
                    returnMessage = Constants.OK; //table is empty
                }
                statement.close();
                connection.close();
            } catch (SQLException e2) {
                myArea = "Exception handling";
                logMessage = "SQLException at DELETE : " + e2.toString();
                log(myName, Constants.ERROR, myArea, logMessage);
                returnMessage = logMessage;
            }
        }

        logMessage = "Message returning to FitNesse > " + returnMessage + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        return returnMessage; //text message that is passed to fitnesse
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + getDatabaseName() + "<.");
        connectionProperties.setLogFilename(getLogFileNameOnly());
        connectionProperties.setLogLevel(getIntLogLevel());
        connectionProperties.setDatabaseName(getDatabaseName());

        if (connectionProperties.refreshConnectionProperties(getDatabaseName())) {
            log(myName, Constants.DEBUG, myArea, "username set to >" + connectionProperties.getDatabaseUsername() + "<.");
        } else {
            log(myName, Constants.ERROR, myArea, "Error retrieving parameter(s): " + connectionProperties.getErrorMessage());
        }

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

    public String getLogUrl() {
        return this.logUrl;
    }
    public void setLogUrl(String logUrl) {
        if (Constants.NOT_FOUND.equals(logUrl)) {
            String myName = "setLogUrl";
            String myArea = "run";
            String logMessage = "Properties file does not contain LogURL value.";
            log(myName, Constants.WARNING, myArea, logMessage);
        } else {
            this.logUrl = logUrl;
        }
    }

    public String getLogFilename() {
        if (getLogUrl().startsWith("http"))
            return "<a href=\"" + getLogUrl() + this.logFileName + ".log\" target=\"_blank\">" + this.logFileName + "</a>";
        else
            return getLogUrl() + this.logFileName + ".log";
    }
    public String getLogFileNameOnly() {
        return this.logFileName;
    }
    public void setLogFileName(String logFileName) {
        if (!logFileNameAlreadySet) {
            this.logFileName = logFileName;
        }
        this.logFileNameAlreadySet = true;
    }

    public static String getVersion() {
        return version;
    }

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

    public String getDatabaseName() {
        return this.databaseName;
    }


}