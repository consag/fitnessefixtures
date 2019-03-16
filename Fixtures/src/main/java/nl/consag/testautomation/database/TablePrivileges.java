/**
 * This purpose of this fixture is to grant privileges to a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki.
 *
 * @author Jac Beekers
 * @version 10 May 2015
 */
package nl.consag.testautomation.database;

import java.sql.*;
import java.text.*;
import java.util.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetDatabaseTable;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

public class TablePrivileges {
    private String className = "TablePrivileges";

    private static String version = "20180621.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl = Constants.LOG_DIR;
    private String errorMessage = Constants.NO_ERRORS;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;


    public TablePrivileges() {
        //Constructors
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = className;
        logFileName = startDate + "." + className;

    }

    public TablePrivileges(String context) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className + "." + context;

    }

    public TablePrivileges(String context, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        setLogLevel(logLevel);
        this.logLevel = getIntLogLevel();
        logFileName = startDate + "." + className + "." + context;

    }

    public boolean userHasPrivilegeOnAllIn(String userName, String privilege, String objectType, String database) {
        String myName = "userHasPrivilegeOnAllIn";
        String logMessage = Constants.NOERRORS;
        String myArea = "init";

        List<List<String>> objectList = new ArrayList<List<String>>();
        String query = Constants.NOT_PROVIDED;
        int numberOfTableColumns = 1;
        boolean useTableOwner = true;

        myArea = "Determine objectType";
        if ("tables".equalsIgnoreCase(objectType)) {
            query = "SELECT table_name FROM user_tables";
        } else if ("views".equalsIgnoreCase(objectType)) {
            query = "SELECT view_name FROM user_views";
        } else {
            logMessage = "Invalid object type >" + objectType + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            errorMessage = logMessage;
            return false;
        }
        log(myName, Constants.INFO, myArea, "Object Type is >" + objectType + "<. Query will be >" + query + "<.");

        myArea = "getObjectList";
        GetDatabaseTable getObjectList = new GetDatabaseTable(context + "-" + myName, Integer.toString(logLevel));
        getObjectList.setDatabaseName(database);
        objectList = getObjectList.getQueryResult(query, numberOfTableColumns, useTableOwner);
        if (Constants.ERROR.equals(getObjectList.getErrorCode())) {
            log(myName, Constants.ERROR, myArea, "getObjectList returned >" + getObjectList.getErrorMessage() + "<.");
            errorMessage = getObjectList.getErrorMessage();
            return false;
        }

        log(myName, Constants.INFO, myArea, "Number of objects in list is >" + objectList.size() + "<.");

        Iterator objectIterator = objectList.iterator();
        List<String> object = new ArrayList<String>();
        boolean privGranted = true;
        int grantsOk = 0;
        int grantsNotOk = 0;
        while (objectIterator.hasNext()) {
            object = (List<String>) objectIterator.next();
            logMessage = "Granting >" + privilege + "< on object >" + object.get(0) + "< to user >" + userName + "<.";
            log(myName, Constants.INFO, myArea, logMessage);
            privGranted = userHasPrivilegeOnObjectIn(userName, privilege, object.get(0), database);
            if (privGranted) {
                grantsOk++;
                log(myName, Constants.INFO, myArea, "Granted.");
            } else {
                grantsNotOk++;
                log(myName, Constants.INFO, myArea, "Grant failed. Error message: " + errorMessage);
            }
        }

        log(myName, Constants.INFO, myArea, "Successful grants: " + Integer.toString(grantsOk));
        log(myName, Constants.INFO, myArea, "Unsuccessful grants: " + Integer.toString(grantsNotOk));
        if (grantsNotOk > 0)
            return false;
        else
            return true;

    }


    public boolean userHasPrivilegeOnObjectIn(String userName, String privilege, String objectName, String database) {
        String myName = "userHasPrivilegeOnObjectIn";
        String myArea = "init";
        String logMessage = Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String sqlStatement = Constants.NOT_INITIALIZED;
        int sqlResult = 0;
        boolean rc = false;

        myArea = "check db type";
        databaseName = database;
        readParameterFile();
        log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName + "<.");
        connectionProperties.setLogFileName(logFileName);
        connectionProperties.setLogLevel(getIntLogLevel());

        switch (connectionProperties.getDatabaseType()) {
            case Constants.DATABASETYPE_ORACLE:
            case Constants.DATABASETYPE_DB2:
                sqlStatement = "grant " + privilege + " on " + objectName + " to " + userName;
                break;
            default:
                logMessage = "databaseType >" + connectionProperties.getDatabaseType() + "< not yet supported";
                log(myName, Constants.INFO, myArea, logMessage);
                errorMessage = logMessage;
                return false;
        }

        try {
            myArea = "SQL Execution";
            logMessage = "Connecting to >" + connectionProperties.getActualDatabase() + "< using table owner >"
                    + connectionProperties.getDatabaseTableOwner() + "<.";
            log(myName, Constants.INFO, myArea, logMessage);
            connection = connectionProperties.getOwnerConnection();
//                connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);
            statement = connection.createStatement();
            logMessage = "SQL >" + sqlStatement + "<.";
            log(myName, Constants.INFO, myArea, logMessage);
            sqlResult = statement.executeUpdate(sqlStatement);
            logMessage = "SQL returned >" + Integer.toString(sqlResult) + "<.";
            log(myName, Constants.INFO, myArea, logMessage);

            statement.close();
            connection.close();
            rc = true;
        } catch (SQLException e) {
            myArea = "Exception handling";
            logMessage = "SQLException at >" + myName + "<. Error =>" + e.toString() + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            rc = false;
            errorMessage = logMessage;
        }

        return rc;
    }

    public String errorMessage() {
        return errorMessage;
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +databaseName +"<.");
        if(connectionProperties.refreshConnectionProperties(databaseName)) {
            log(myName, Constants.DEBUG, myArea,"username set to >" + connectionProperties.getDatabaseUsername() +"<.");
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
    /**
     * @return Log file name. If the LogUrl starts with http, a hyperlink will be created
     */
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