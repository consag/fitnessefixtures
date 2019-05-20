/*
 * @author Jac. Beekers
 * @version 0.1
 * @since July 2015
 */
package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.*;

import java.text.SimpleDateFormat;

public class SetFixtureProperties {
    
    private static final String className="SetFixtureProperties";
    private static final String version = "20170123.0";

    private String startDate=Constants.NOT_INITIALIZED;
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.NOT_PROVIDED;

    private int logLevel =Constants.LOGLEVEL_INFO;
    private int logEntries =0;
    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;

    private String errorCode=Constants.NOT_INITIALIZED;
    private String errorMessage=Constants.NOT_INITIALIZED;
    private String delimiter=Constants.DATABASE_PROPERTIES_DELIMITER;
    
  private static String curFields[];

// holding values for this instance
    private String connectionAlias =Constants.NOT_INITIALIZED;
    private String databaseConnection =Constants.NOT_INITIALIZED;
    private String loginUser =Constants.NOT_INITIALIZED;
    private String userPassword =Constants.NOT_INITIALIZED;
    private String loginTableOwner = Constants.NOT_INITIALIZED;
    private String tableOwnerPassword = Constants.NOT_INITIALIZED;
    private String propertiesFile = Constants.NOT_INITIALIZED;
    private String actionIfNotExists = Constants.NOT_INITIALIZED;
    private String comment = Constants.NOT_INITIALIZED;
    private String forceDelete =Constants.NO;
    private boolean booleanForceDelete = false;
    private String action =Constants.NOT_INITIALIZED;
    //
  
    //Constructors
    public SetFixtureProperties() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = className;
        logFileName = startDate + "." + className;
    }

    public SetFixtureProperties(String context) {
      this.context =context;
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      this.startDate = sdf.format(started);
        logFileName = startDate + "." + className +"." +this.context;
    }

    public String forDatabaseName(String dbName) {
        setConnectionAlias(dbName);
        return Constants.OK;
    }
    
    public void setConnectionAlias(String dbAlias) {
        this.connectionAlias =dbAlias;
    }
    
    public String getConnectionAlias() {
        return this.connectionAlias;
    }
    
  // Check login user
  public  boolean loginUser (String userId) {
      if(GetDatabaseUserName(getConnectionAlias()).equals(userId)) 
          return true;          
      else 
          return false;          
  }
  public   String loginUser () {
      return GetDatabaseUserName(getConnectionAlias());
  }

    // Check table owner
    public  boolean loginTableOwner (String userId) {
        if(GetDatabaseTableOwnerName(getConnectionAlias()).equals(userId)) 
            return true;          
        else 
            return false;          
    }
    public   String loginTableOwner () {
        return GetDatabaseTableOwnerName(getConnectionAlias());
    }

    // Check login user
    public  boolean dbConnection (String dbConnection) {
        if(GetDatabaseConnectionDefinition(getConnectionAlias()).equals(dbConnection))
        return true;
        else
            return false;
    }
    
    public  String dbConnection() {
        return GetDatabaseConnectionDefinition(getConnectionAlias());
    }


    public boolean connectionToDatabaseIsSuccessful(String userid) {
        setError(Constants.ERROR, Constants.NOT_IMPLEMENTED);
        return false;
    }

      public String connectionToDatabaseIsSuccessful() {
          return getErrorMessage();
      }

      private void setError(String errCode, String errMsg) {
          setErrorCode(errCode);
          setErrorMessage(errMsg);
      }

      private void setErrorCode(String errCode) {
          this.errorCode = errCode;
      }
      
      private void setErrorMessage(String errMsg) {
          this.errorMessage = errMsg;
      }
      
      //in FitNesse show error message sounds better than show get error message
    public String errorMessage() {
        return getErrorMessage();
    }
      public String getErrorMessage() {
          return this.errorMessage;
      }
      public String getErrorCode() {
          return this.errorCode;
      }
  // application name is provided
  // appwsh contains applications and which wsh object to use
  // wsh contains the definition of the wsh object to use
  public  String getWshUrl(String searchFor) {
      String logicalWSH ="Unknown";
          
      
      // first find the app in appwsh
      
      logicalWSH =FindParameter(Constants.APPWSH_PROPERTIES, searchFor, Constants.INDEX_APPWSH_WSHNAME);
      if (Constants.NOT_FOUND.equals(logicalWSH)) {
        return "application >" + searchFor + "< not found in >" + Constants.APPWSH_PROPERTIES +"<."; 
      }
      
      // now go look for the wsh
      return FindParameter(Constants.WSH_PROPERTIES, logicalWSH);
  }
        
  /**
   * GetEnvironment: Determine which environment fitnesse is running
   * @return
   */
  public  String GetEnvironment() {
    return FindParameter(Constants.ENVIRONMENT_PROPERTIES, Constants.ENVIRONMENT);
  }

  /**
   * GetEnvironment: Determine which environment fitnesse is running
   * @return
   */
  public  String GetLogDir() {
    return FindParameter(Constants.FILEOPERATION_PROPERTIES, Constants.LOG_DIR);
  }

/*
 * Determine root directory
 */
  public  String GetRootDir(String area){
    
    return FindParameter(Constants.FILEOPERATION_PROPERTIES, area, Constants.INDEX_FILEOPERATIONS_DIRECTORY);
  }
  
    /**
     * Get info for File Operations
     * Determine Incoming directory
     * @return
     */
    public  String GetIncoming() {
      return FindParameter(Constants.FILEOPERATION_PROPERTIES, Constants.INCOMING);
    }
  /**
   * Get info for File Operations
   * Determine Outgoing directory
   * @return
   */
    public   String GetOutgoing() {
      return FindParameter(Constants.FILEOPERATION_PROPERTIES, Constants.OUTGOING);
    }
  /**
   * Get info for File Operations
   * Determine Temp directory
   * @return
   */
    public  String GetTemp() {
    return FindParameter(Constants.FILEOPERATION_PROPERTIES, Constants.TEMP);
  }

    /**
     * Get info for File Operation
     * Determine base directory for test data
     * @return
     */
    public  String GetTestdata() {
    return FindParameter(Constants.FILEOPERATION_PROPERTIES, Constants.TESTDATA);
  }

  /**
   * ==================================================================================
   * Area: PowerCenter parameters
   */
    
  /**
   * Get PowerCenter info
   * Determine Doamin Name
   * @return
   */
   public  String getDomainName(String pConnectionName) {
   return FindParameter(Constants.POWERCENTER_PROPERTIES, pConnectionName, Constants.INDEX_INFA_DOMAINNAME);
   }
  /**
   * Get PowerCenter info
   * Determine Repository Service Name
   * @return
   */
  public  String getRepoService(String pConnectionName) {
  return FindParameter(Constants.POWERCENTER_PROPERTIES, pConnectionName, Constants.INDEX_INFA_REPOSITORYSERVICE);
  }

  /**
   * Get PowerCenter info
   * Determine Integration Service Name
   * @return
   */
  public  String getIntService(String pConnectionName) {
  return FindParameter(Constants.POWERCENTER_PROPERTIES, pConnectionName, Constants.INDEX_INFA_INTEGRATIONSERVICE);
  }

  /**
   * Get PowerCenter info
   * Determine User Name
   * @return
   */
  public  String getUsername(String pConnectionName) {
  return FindParameter(Constants.POWERCENTER_PROPERTIES, pConnectionName, Constants.INDEX_INFA_USERNAME);
  }

  /**
   * Get PowerCenter info
   * Determine Passsword
   * @return
   */
  public  String getPassword(String pConnectionName) {
  return FindParameter(Constants.POWERCENTER_PROPERTIES, pConnectionName, Constants.INDEX_INFA_USER_PASSWORD);
  }

  /**
   * ==================================================================================
   * Area: Database parameters
   */
    
  /**
   * Get Database info
   * Determine database type
   * Use this method to retrieve the value to be used in GetDatabaseDriver
   * @return
   */
  public  String GetDatabaseType(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_TYPE);
  }

  /**
   * Get Database info
   * Determine database Connection Definition, e.g. SRV0OPWL101
   * Call this method to retrieve the value to be used in GetDatabaseURL
   * @return
   */
  public  String GetDatabaseConnectionDefinition(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_CONNECTION);
  }

  /**
   * Get Database info
   * Determine database user name
   * @return
   */
  public  String GetDatabaseUserName(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_USERNAME);
  }
  public  String GetDatabaseTableOwnerName(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_TABLE_OWNER);
  }

  /**
   * Get Database info
   * Determine database user password
   * @return
   */
  public  String GetDatabaseUserPWD(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_USER_PASSWORD);
  }
  public  String GetDatabaseTableOwnerPWD(String pConnectionName) {
  return FindParameter(Constants.DATABASE_PROPERTIES, pConnectionName, Constants.INDEX_DATABASE_TABLE_OWNER_PASSWORD);
  }

  /**
   * Get Database info
   * Determine database Driver
   * Database Type has to be a value determined by GetDatabaseType
   * @return
   */
  public  String GetDatabaseDriver(String pDatabaseType) {
  return FindParameter(Constants.JDBC_PROPERTIES, pDatabaseType);
  }

  /**
   * Get Database info
   * Determine database Driver
   * The argument has to be a value  determined by GetDatabaseConnectionDefinition
   * @return
   */
  public  String GetDatabaseURL(String pDatabaseConn) {
  return FindParameter(Constants.JDBC_PROPERTIES, pDatabaseConn);
  }


/**
 * ==================================================================================
 */
    
  /**
   * FindParameter
   * Finds the value for a specified parameter in a specified file
   * Arguments:
   *  1 Parameter file to look in
   *  2 Parameter to search for
   * @return
   *  Returns the found value or NOTFOUND
   */


/*
* Note: This method only exists for backward compatibility and uses a different delimiter.
*/
   private  String FindParameter(String pFileName, String pSearchFor) {
    String sResult;
    setDelimiter(": ");
    sResult= FindParameter(pFileName, pSearchFor, 1);
    setDelimiter(Constants.DATABASE_PROPERTIES_DELIMITER);
return sResult;
   }

    private  String FindParameter(String pFileName, String pSearchFor, Integer pIndex) {
      
  
        String sSearchFor = pSearchFor;
        String sFileName = pFileName;
        boolean bFound;
        String sResult =Constants.NOT_FOUND;
    //  log("FindParameter","debug","init","Searching in file =>" + pFileName + "< for >" + pSearchFor + "< Index =>" + pIndex +"<.");
              try {
            // Open fitnesse parameter file
            FileInputStream fstream =
                new FileInputStream(sFileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            //Read File Line By Line
            bFound =false;
            while ( !bFound && ((strLine = br.readLine()) != null)) {
                curFields = strLine.split(getDelimiter());
                if (curFields[0].equals(sSearchFor)) {
                    sResult= curFields[pIndex];
                    bFound=true;
                }
            }
            //Close the input stream
            in.close();
        } catch (Exception e) { //Catch exception if any
            log("FindParameter","error","exception handling","Error reading file: " + e.getMessage());
        }
      return sResult;
    
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
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
    }

    /**
    * @param level
    */
    public void setLogLevel(String level) {
    String myName ="setLogLevel";
    String myArea ="determineLevel";
    
    logLevel = Constants.logLevel.indexOf(level.toUpperCase());
    if (logLevel <0) {
       log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level >" + Constants.LOGLEVEL_INFO +"<.");
       logLevel =Constants.LOGLEVEL_INFO;
    }
    
    log(myName, Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
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


  private void setDelimiter(String delimiter) {
      this.delimiter=delimiter;
  }
  private String getDelimiter() {
      return this.delimiter;
  }
  
    public static String getVersion() {
        return version;
    }

    public boolean connectionSuccessfullyAdded() {
        String myName="connectionSuccessfullyAdded";
        setAction(Constants.PROPMGMT_ACTION_ADD);
        if(!checkActionIfNotExists(getActionIfNotExists())) {
            setError(Constants.ERROR, "Invalid action >" + getActionIfNotExists() +"< specified.");
            return false;
        }
        //TODO: Check if alias already exists in properties file
        //If so, don't change the file and report an error
        //If not, add it and include the provided comment or generate your own. Include a timestamp in the comment
        
        outputAllSettingsToLog();
        
        setError(Constants.ERROR, Constants.NOT_IMPLEMENTED);
        return false;
    }

    public boolean connectionSuccessfullyRemoved() {
        setAction(Constants.PROPMGMT_ACTION_REMOVE);
        String myName="connectionSuccessfullyRemoved";
        
        //TODO: Check if alias exists in properties file. 
        //If so, comment out the file and add a comment
        //If forceDelete is true, remove the line, but still add the/a comment
        
        outputAllSettingsToLog();
        
        setError(Constants.ERROR, Constants.NOT_IMPLEMENTED);
        return false;
    }
    
    private void setAction(String action) {
        this.action =action;
    }
    private String getAction(){
        return this.action;
    }

    private void outputAllSettingsToLog() {
        String myName="outputAllSettingsToLog";
        String myArea="output";
        String logMessage=Constants.NOT_PROVIDED;
        
        logMessage="Settings for >" + getAction() +" with startdate >" + startDate + "< and context >" + context +"<.";
        log(myName,Constants.INFO,myArea,logMessage);
        logMessage="Properties file ...................>" + getPropertiesFile() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        logMessage="Delimiter .........................>" + getDelimiter() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        logMessage="Action if file does not exist .....>" + getActionIfNotExists() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        logMessage="Connection Alias ..................>" + getConnectionAlias() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        if(Constants.PROPMGMT_ACTION_ADD.equals(getAction())) {
            logMessage="Database connection ...............>" + getDatabaseConnection() + "<."; log(myName,Constants.INFO,myArea,logMessage);
            logMessage="Login user ........................>" + getLoginUser() + "<."; log(myName,Constants.INFO,myArea,logMessage);
            logMessage="Login Table Owner .................>" + getLoginTableOwner() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        }
        logMessage="Comment to add ....................>" + getComment() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        
        if(Constants.PROPMGMT_ACTION_REMOVE.equals(getAction())) {
            logMessage="Force Delete ......................>" + getForceDelete() + "<."; log(myName,Constants.INFO,myArea,logMessage);
        }

        
    }
    
    public void forPropertiesFile(String propFile) {
        forPropertiesFileUsingDelimiterIfFileDoesNotExist(propFile, Constants.DATABASE_PROPERTIES_DELIMITER, Constants.PROPMGMT_ACTIONERROR_REPORT);
    }

    public void forPropertiesFileUsingDelimiterIfFileDoesNotExist(String propFile, String delimiter, String ifNotExists) {
        String myName ="forPropertiesFileUsingDelimiterIfFileDoesNotExist";
        String myArea ="Init";
        String logMessage =Constants.NOT_INITIALIZED;
        readOurOwnParameterFile();

        logMessage="Provided properties file ........>" + propFile + "<";
        log(myName,Constants.DEBUG,myArea,logMessage);
        logMessage="Provided delimiter ..............>" + delimiter + "<";
        log(myName,Constants.DEBUG,myArea,logMessage);
        logMessage="Provided action if not exists ...>" + ifNotExists + "<";
        log(myName,Constants.DEBUG,myArea,logMessage);
        
        setPropertiesFile(propFile);
        setDelimiter(delimiter);
        setActionIfNotExists(ifNotExists);

    }
    public void addConnectionAlias(String alias) {
        setConnectionAlias(alias);
    }
    public void removeConnectionAlias(String alias) {
        setConnectionAlias(alias);
    }
    public void setLoginUser(String userId) {
        this.loginUser = userId;
    }
    public String getLoginUser(){
        return this.loginUser;
    }
    public void setUserPassword(String password) {
        this.userPassword = password;
    }
    private String getUserPassword() {
        return this.userPassword;
    }
    public void setDbConnection(String dbConnection) {
        setDatabaseConnection(dbConnection);
        // dbconnection as defined in jdbc.properties
    }
    public void setDatabaseConnection(String db) {
        this.databaseConnection = db;
    }
    public String getDatabaseConnection(){
        return this.databaseConnection;
    }
    public void setLoginTableOwner(String tableOwner) {
        this.loginTableOwner = tableOwner;
    }
    public String getLoginTableOwner() {
        return this.loginTableOwner;
    }
    public void setTableOwnerPassword(String password) {
        this.tableOwnerPassword = password;
    }
    private String getTableOwnerPassword(){
        return this.tableOwnerPassword;
    }
    private void setPropertiesFile(String propFile) {
        this.propertiesFile =propFile;
    }
    public String getPropertiesFile() {
        return this.propertiesFile;
    }
    public void setActionIfNotExists(String action) {
        this.actionIfNotExists = action;
    }
    public String getActionIfNotExists() {
        return this.actionIfNotExists;
    }
    private boolean checkActionIfNotExists(String action) {
        if(Constants.PROPMGMT_ACTIONERRORS.contains(action.toLowerCase()))
           return true;
        else
            return false;
    }
    public void addComment(String comment) {
        setComment(comment);
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return this.comment;
    }
    public void setForceDelete(String forceDelete) {
        this.forceDelete = forceDelete;
        if (forceDelete.toLowerCase().equals(Constants.TRUE))
            setBooleanForceDelete(true);
        else
            setBooleanForceDelete(false);
    }
    private void setBooleanForceDelete(boolean what) {
        this.booleanForceDelete = what;
    }
    private boolean getBooleanForceDelete() {
        return this.booleanForceDelete;
    }
    
    public String getForceDelete() {
        if(getBooleanForceDelete())
        return Constants.TRUE;
            else
        return Constants.FALSE;
    }
    
    private void readOurOwnParameterFile() {
      String logMessage = Constants.NOT_INITIALIZED;
      String myName="readOurOwnParameterFile";
      String myArea="param search result";
        
        logUrl = GetParameters.GetLogUrl();
        logMessage = "logURL >" + logUrl +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        resultFormat =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
        if(Constants.NOT_FOUND.equals(resultFormat))
            resultFormat =Constants.DEFAULT_RESULT_FORMAT;
        logMessage = "resultFormat >" + resultFormat +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

    }

}
