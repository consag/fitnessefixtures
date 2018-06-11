/**
 * This purpose of this fixture is to grant privileges to a temporary table previously created by CreateTable
 * * The input parameters are provided by a table in the FitNesse wiki. 
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
	private String logFileName = Constants.NOT_INITIALIZED;
	private String context = Constants.DEFAULT;
	private String startDate = Constants.NOT_INITIALIZED;
    private String errorMessage = Constants.NO_ERRORS;
    private int logLevel=3;

	private String driver;
	private String url;
	private String userId;
	private String password;
	private String databaseName;
	private String databaseType;
	private String databaseConnDef;
        private String tableOwner;
        private String tableOwnerPassword;
        

	public TablePrivileges() {
		//Constructors
	      	java.util.Date started = new java.util.Date();
	      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	      	startDate = sdf.format(started);
	      	this.context=className;
	        logFileName = startDate + "." + className ;

	    }
	
	public TablePrivileges(String context) {
	    	java.util.Date started = new java.util.Date();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	startDate = sdf.format(started);
	    	this.context=context;
	        logFileName = startDate + "." + className +"." + context;

	    }

    public TablePrivileges(String context, String logLevel) {
            java.util.Date started = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            startDate = sdf.format(started);
            this.context=context;
            setLogLevel(logLevel);
            this.logLevel = getIntLogLevel();
            logFileName = startDate + "." + className +"." + context;

        }

    public boolean userHasPrivilegeOnAllIn(String userName, String privilege, String objectType, String database) {
        String myName="userHasPrivilegeOnAllIn";
        String logMessage =Constants.NOERRORS;
        String myArea ="init";
        
        List<List<String>> objectList =new ArrayList<List<String>>();
        String query =Constants.NOT_PROVIDED;
        int numberOfTableColumns =1;
        boolean useTableOwner =true;
        
        myArea="Determine objectType";
        if("tables".equalsIgnoreCase(objectType)) {
            query="SELECT table_name FROM user_tables";
        } else if("views".equalsIgnoreCase(objectType)) {
            query="SELECT view_name FROM user_views";            
        } else {
            logMessage = "Invalid object type >" + objectType + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            errorMessage=logMessage;
            return false;
        }
        log(myName, Constants.INFO, myArea, "Object Type is >" +objectType +"<. Query will be >" +query +"<.");
        
        myArea="getObjectList";
        GetDatabaseTable getObjectList = new GetDatabaseTable(context+"-"+myName, Integer.toString(logLevel));
        getObjectList.setDatabaseName(database);
        objectList=getObjectList.getQueryResult(query, numberOfTableColumns, useTableOwner);
        if(Constants.ERROR.equals(getObjectList.getErrorCode())) {
            log(myName, Constants.ERROR, myArea, "getObjectList returned >" +getObjectList.getErrorMessage() +"<.");
            errorMessage=getObjectList.getErrorMessage();
            return false;
        }
        
        log(myName, Constants.INFO, myArea, "Number of objects in list is >" +objectList.size() +"<.");
        
        Iterator objectIterator = objectList.iterator();
        List<String> object =new ArrayList<String>();
        boolean privGranted =true;
        int grantsOk =0;
        int grantsNotOk =0;
        while(objectIterator.hasNext()) {
            object = (List<String>) objectIterator.next();
            logMessage="Granting >" +privilege +"< on object >" +object.get(0) +"< to user >" +userName +"<.";
            log(myName, Constants.INFO, myArea, logMessage);
            privGranted =userHasPrivilegeOnObjectIn(userName, privilege, object.get(0), database);
            if(privGranted) {
                grantsOk++;
                log(myName, Constants.INFO, myArea, "Granted.");
            } else {
                grantsNotOk++;
                log(myName, Constants.INFO, myArea, "Grant failed. Error message: " +errorMessage);
            }
        }
        
        log(myName, Constants.INFO, myArea, "Successful grants: " +Integer.toString(grantsOk));
        log(myName, Constants.INFO, myArea, "Unsuccessful grants: " +Integer.toString(grantsNotOk));
        if(grantsNotOk >0) 
            return false;
        else
            return true;
        
    }
    
    
    public boolean userHasPrivilegeOnObjectIn(String userName, String privilege, String objectName, String database) {
        String myName="userHasPrivilegeOnObjectIn";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String sqlStatement=Constants.NOT_INITIALIZED;
        int sqlResult =0;
        boolean rc =false;

        myArea="check db type";
        databaseName =database;
        readParameterFile();
        
        if(Constants.NOT_FOUND.equals(databaseType)) {
            logMessage="databaseType >" + databaseType +"<.";       log(myName, Constants.FATAL, myArea, logMessage);  
            errorMessage=logMessage;
            return false;
        }
        
        if("Oracle".equals(databaseType) || "DB2".equals(databaseType)) {
            sqlStatement = "grant " + privilege + " on " + objectName + " to " + userName;  
        } else {
            logMessage="databaseType >" + databaseType +"< not yet supported";       log(myName, "info", myArea, logMessage);  
            errorMessage=logMessage;
            return false;
        }

        try {
        myArea="SQL Execution";
           logMessage = "Connecting to >" + databaseConnDef +"< using userID >" + tableOwner + "<.";
           log(myName, Constants.INFO, myArea, logMessage);
                connection = DriverManager.getConnection(url, tableOwner, tableOwnerPassword);      
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
                 rc =false;
                 errorMessage=logMessage;
              }
    
         return rc;
    }
    
    public String errorMessage() {
        return errorMessage;
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

        logMessage="databaseType >" + databaseType +"<.";       log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage="connection >" + databaseConnDef +"<.";       log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage="driver >" + driver +"<.";       log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage="url >" + url +"<.";       log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage="userId >" + userId +"<.";       log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage="tblowner >" + tableOwner +"<."; log(myName, Constants.DEBUG, myArea, logMessage);
        if(Constants.NOT_FOUND.equals(tableOwnerPassword)) {
            logMessage="tableOwnerPassword was not found.";
        } else {
            logMessage="tableOwnerPassword has been set.";
        }
        log(myName, Constants.DEBUG, myArea, logMessage);
	    
	}

    private void log(String name, String level, String location, String logText) {
           if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
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
    
    logLevel =Constants.logLevel.indexOf(level.toUpperCase());
    if (logLevel <0) {
       logLevel =3;
    }
    
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


}