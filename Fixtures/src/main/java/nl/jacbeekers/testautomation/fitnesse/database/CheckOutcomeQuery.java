/**
 * This purpose of this fixture is to compare the expected outcome of a query with the actual outcome using the FitNesse slim 'table' table.
 * The input parameters are provided by a table in the FitNesse wiki.
 *
 * @author Edward Crain
 * @version 11 October 2014
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckOutcomeQuery {

    private String className = "CheckOutcomeQuery";
    private static String version = "20180619.2";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl = Constants.LOG_DIR;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;
    private String query;

    private boolean error = false;
    private String errorMessage = "";

    private int NO_FITNESSE_ROWS_TO_SKIP = 2;

    private int numberOfRowsFitNesseTable;
    private int numberOfTableColumnsFitNesseTable;

    private List<List<String>> returnTableToFitNesse = new ArrayList<List<String>>();
    //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")

    public CheckOutcomeQuery() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;

    }

    public void getDatabaseName(List<String> inputFitNesseRow) {
        List<String> returnRowToFitNesse = new ArrayList<String>();
        setDatabaseName(inputFitNesseRow.get(0)); //read first column
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void retrieveQuery(List<String> inputFitNesseRow) {
        List<String> returnRowToFitNesse = new ArrayList<String>();
        setQuery(inputFitNesseRow.get(1)); //read second row second column of FitNesse table
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public String getColumn() {
        return getDatabaseTable();

    }

    private String getDatabaseTable() {
        String myName = "getDatabaseTable";
        String myArea = "init";
        String result = "";

        //attributes for internal database table

        //attributes for reading database
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            myArea = "readParameterFile";
            readParameterFile();
            //Function to check the database based on input in FitNesse table row
            printUsedParameters();
            log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");
            connectionProperties.setLogFileName(logFileName);
            connectionProperties.setLogLevel(getIntLogLevel());
            connection = connectionProperties.getUserConnection();

            // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
            // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
            statement = connection.createStatement();
            // sql query of string type to read database
            resultSet = statement.executeQuery(query);
            if( resultSet.next()) {
                if (resultSet.getString(1) == null) {
                    result = "";
                } else {
                    result = resultSet.getString(1);
                }

            } else {
                result="";
            }


            statement.close();
            connection.close();
        } catch (SQLException e) {
            error = true;
            log(myName, Constants.ERROR, myArea, "SQLException : " + e.toString());
        }
        return result;
    }

    public void printUsedParameters() {
        String myName = "printUsedParameters";
        String myArea = "output";
        log(myName, Constants.INFO, myArea, "Database: " + databaseName);
        log(myName, Constants.INFO, myArea, "Query: " + query);
        log(myName, Constants.INFO, myArea, "Number of expected database fields: " + Integer.toString(numberOfTableColumnsFitNesseTable - 1));
        log(myName, Constants.INFO, myArea, "Number of rows expected in database: " + Integer.toString(numberOfRowsFitNesseTable - NO_FITNESSE_ROWS_TO_SKIP));
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + databaseName + "<.");
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

    /**
     * @return Log file name. If the LogUrl starts with http, a hyperlink will be created
     */
    public String getLogFilename() {
        if (logUrl.startsWith("http"))
            return "<a href=\"" + logUrl + logFileName + ".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl + logFileName + ".log";
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
