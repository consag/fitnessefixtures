/**
 * This purpose of this fixture is to drop a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac Beekers
 * @version 10 May 2015
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import java.sql.*;
import java.text.*;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

public class DropSynonym {

	private String className = "DropSynonym";
    private static String version = "20190704.0";

    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl = Constants.LOG_DIR;
    private boolean logFileNameAlreadySet = false;
    private String errorMessage = Constants.NOERRORS;

    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;
    private String query;
    private boolean ignoreErrorOnDrop =false;
        
    private String tableComment = Constants.TABLE_COMMENT;
	private int NO_FITNESSE_ROWS_TO_SKIP = 3;


	public DropSynonym() {
		//Constructors
	      	java.util.Date started = new java.util.Date();
	      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	      	startDate = sdf.format(started);
	      	this.context=className;
	        logFileName = startDate + "." + className;

	    }
	
	public DropSynonym(String context) {
	    	java.util.Date started = new java.util.Date();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	startDate = sdf.format(started);
	    	this.context=context;
	        logFileName = startDate + "." + className +"." + context;
	    }

public void ignoreErrorOnDrop(String yesNo) {
	    ignoreError(yesNo);
}

    public void ignoreError(String yesNo) {
        if(yesNo.equals(Constants.YES))
            ignoreErrorOnDrop=true;
        else
            ignoreErrorOnDrop=false;
    }
	
    public boolean synonymDoesNotExistInDatabase(String inSynonymName, String inDatabase) {
        String myName="synonymDoesNotExistInDatabase";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String sqlStatement=Constants.NOT_INITIALIZED;
        String commentStatement=Constants.NOT_INITIALIZED;
        int sqlResult =0;
        boolean rc =false;
        String commentFound=Constants.NOT_FOUND;

        databaseName=inDatabase;
        readParameterFile();

        String synonymName;
        if(connectionProperties.getUseTablePrefix()) {
            synonymName = connectionProperties.getTableOwnerTablePrefix() + inSynonymName;
        } else {
            synonymName = inSynonymName;
        }
        myArea="check db type";

        if (Constants.DATABASETYPE_ORACLE.equals(connectionProperties.getDatabaseType())
            || Constants.DATABASETYPE_DB2.equals(connectionProperties.getDatabaseType())) {
            sqlStatement = "drop synonym " + synonymName;
            commentStatement ="select distinct comments from all_tab_comments where table_name ='" + synonymName +"'";
        } else {
            logMessage="databaseType >" + connectionProperties.getDatabaseType() +"< not yet supported";
            log(myName, Constants.INFO, myArea, logMessage);
            errorMessage=logMessage;
            return false;
        }
         
        try {
        myArea="SQL Execution";
           logMessage = "Connecting to >" + connectionProperties.getDatabaseType() +"< using userID >" + connectionProperties.getDatabaseTableOwner() + "<.";
           log(myName, Constants.INFO, myArea, logMessage);
           connection =connectionProperties.getOwnerConnection();
//                connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);
                statement = connection.createStatement();
                logMessage="SQL >" + sqlStatement +"<.";
                log(myName, Constants.INFO, myArea, logMessage);
                sqlResult= statement.executeUpdate(sqlStatement);
            logMessage="SQL returned >" + Integer.toString(sqlResult) + "<.";
           log(myName, Constants.INFO, myArea, logMessage);

          statement.close();
          connection.close();      
            rc=true;
              }  catch (SQLException e) {
                myArea="Exception handling";
                logMessage = "SQLException at >" + myName + "<. Error =>" + e.toString() +"<.";
                log(myName, Constants.ERROR, myArea, logMessage);
                if((/*Oracle*/ e.toString().contains("ORA-01434")
                        || /*DB2*/ e.toString().contains("SQLCODE=-204, SQLSTATE=42704") )
                        && ignoreErrorOnDrop) {
                    errorMessage=Constants.NO_ERRORS;
                    rc =true;
                } else {
                 rc =false;
                 errorMessage=logMessage;
                }
              }
    
         return rc;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public boolean synonymDoesNotExist(String inSynonymName) {
        String myName="synonymDoesNotExist";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;

        String synonymName;
        String nrTablesFound =Constants.NOT_INITIALIZED;
        
        myArea="check db type";
        readParameterFile();

        if(connectionProperties.getUseTablePrefix()) {
            synonymName = connectionProperties.getTableOwnerTablePrefix() + inSynonymName;
        } else {
            synonymName = inSynonymName;
        }

        if(Constants.DATABASETYPE_ORACLE.equals(connectionProperties.getDatabaseType())) {
            query="SELECT count(*) syncount FROM user_synonyms WHERE synonym_name ='" +synonymName +"'";
        } else {
            logMessage="databaseType >" + connectionProperties.getDatabaseType() +"< not yet supported";
            log(myName, Constants.INFO, myArea, logMessage);
            return false;
        }
        GetSingleValue dbCol= new GetSingleValue(className);
        dbCol.setDatabaseName(databaseName);
        dbCol.setQuery(query);
        nrTablesFound = dbCol.getColumn();
        
        if (nrTablesFound.equals("0") ) 
            return true;
        else errorMessage = "Count on synonyms returned =>" + nrTablesFound + "<.";

        return false;
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea, "getting properties for >" + databaseName + "<.");
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
    public void setDatabaseName(String databaseName) {
        String myName = "setDatabaseName";
        String myArea = "run";
        String logMessage = Constants.NOT_INITIALIZED;
        this.databaseName = databaseName;

        logMessage = "Database name has been set to >" + this.databaseName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }

}