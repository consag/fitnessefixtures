/**
 * This purpose of this fixture is to drop a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac Beekers
 * @version 10 May 2015
 */
package nl.consag.testautomation.database;

import java.sql.*;
import java.text.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

public class DropSynonym {

	private String className = "DropSynonym";
	private String logFileName = Constants.NOT_INITIALIZED;
	private String context = Constants.DEFAULT;
	private String startDate = Constants.NOT_INITIALIZED;
    private String errorMessage = Constants.NO_ERRORS;
    private int logLevel =3;

	private String driver;
	private String url;
	private String userId;
	private String password;
	private String databaseName;
	private String query;
	private String databaseType;
	private String databaseConnDef;
        private String tableOwner;
        private String tableOwnerPassword;
    private boolean ignoreErrorOnDrop =false;
        
    private String tablePrefix = Constants.TABLE_PREFIX;
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

        String synonymName = tablePrefix + inSynonymName;
        databaseName=inDatabase;
        myArea="check db type";
        readParameterFile();
        
        if("Oracle".equals(databaseType) || "DB2".equals(databaseType)) {
            sqlStatement = "drop synonym " + synonymName;  
            commentStatement ="select distinct comments from all_tab_comments where table_name ='" + synonymName +"'";
        } else {
            logMessage="databaseType >" + databaseType +"< not yet supported";       log(myName, "info", myArea, logMessage);  
            errorMessage=logMessage;
            return false;
        }
         
        try {
        myArea="SQL Execution";
           logMessage = "Connecting to >" + databaseConnDef +"< using userID >" + tableOwner + "<.";
           log(myName, "info", myArea, logMessage);
                connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);      
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
                if(e.toString().contains("ORA-01434") && ignoreErrorOnDrop) {
                    errorMessage=Constants.NO_ERRORS;
                    rc =true;
                } else {
                 rc =false;
                 errorMessage=logMessage;
                }
              }
    
         return rc;
    }
    
    public String tableNameFor (String inTableName) {
        return tablePrefix + inTableName;

    }
    
    public String errorMessage() {
        return errorMessage;
    }

    public boolean synonymDoesNotExist(String inSynonymName) {
        String myName="synonymDoesNotExist";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;

        String synonymName = tablePrefix + inSynonymName;
        String nrTablesFound =Constants.NOT_INITIALIZED;
        
        myArea="check db type";
        readParameterFile();
        if(databaseType.equals("Oracle")) {
            query="SELECT count(*) syncount FROM user_synonyms WHERE synonym_name ='" +synonymName +"'";
        } else {
            logMessage="databaseType >" + databaseType +"< not yet supported";       log(myName, "info", myArea, logMessage);            
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
    
    



	public void readParameterFile(){	 
        String myName="readParameterFile";
        String myArea="reading parameters";
        String logMessage=Constants.NOT_INITIALIZED;

        databaseType = GetParameters.GetDatabaseType(databaseName);
        databaseConnDef = GetParameters.GetDatabaseConnectionDefinition(databaseName);
        driver = GetParameters.GetDatabaseDriver(databaseType);
        url = GetParameters.GetDatabaseURL(databaseConnDef);
        userId = GetParameters.GetDatabaseUserName(databaseName);
        password = GetParameters.GetDatabaseUserPWD(databaseName);
        tableOwner = GetParameters.GetDatabaseTableOwnerName(databaseName);
        tableOwnerPassword =GetParameters.GetDatabaseTableOwnerPWD(databaseName);

        logMessage="databaseType >" + databaseType +"<.";       log(myName, "info", myArea, logMessage);
        logMessage="connection >" + databaseConnDef +"<.";       log(myName, "info", myArea, logMessage);
        logMessage="driver >" + driver +"<.";       log(myName, "info", myArea, logMessage);
        logMessage="url >" + url +"<.";       log(myName, "info", myArea, logMessage);
        logMessage="userId >" + userId +"<.";       log(myName, "info", myArea, logMessage);
        logMessage="tblowner >" + tableOwner +"<."; log(myName, "info", myArea, logMessage);
        
	}

    private void log(String name, String level, String location, String logText) {
           if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
           }

            Logging.LogEntry(logFileName, name, level, location, logText);
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

	public String getLogFilename() {
		return logFileName + ".log";
       }

}