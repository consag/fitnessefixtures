/*
 * @author Jac. Beekers
 * @version 0.1
 * @since July 2015
 */
package nl.consag.testautomation.supporting;

import java.io.*;

import java.text.SimpleDateFormat;

public class CheckFixtureProperties {
    

  //27-07-2013 Change to new log mechanism, preventing java heap space error with fitnesse stdout
    private String m_className="CheckFixtureProperties";

    private String m_context=Constants.DEFAULT;
    private String m_startDate=Constants.NOT_INITIALIZED;
    private boolean m_firstTime=true;
  //27-07-2013

    private String errorCode=Constants.NOT_INITIALIZED;
    private String errorMessage=Constants.NOT_INITIALIZED;
    private String delimiter=Constants.DATABASE_PROPERTIES_DELIMITER;
    
  private static String curFields[];

    private static final String version = "20170123.0";

  

  
// holding values for this instance
    private static String m_database =Constants.NOT_INITIALIZED;
//
  
    public CheckFixtureProperties() {
      java.util.Date started=new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      m_startDate = sdf.format(started);

    }
  public CheckFixtureProperties(String context) {
    java.util.Date started=new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    m_startDate = sdf.format(started);
  m_context=context;
    
  }

    public static String forDatabaseName(String dbName) {
        m_database =dbName;
        return Constants.OK;
    }
    
  // Check login user
  public  boolean loginUser (String userId) {
      if(GetDatabaseUserName(m_database).equals(userId)) 
          return true;          
      else 
          return false;          
  }
  public   String loginUser () {
      return GetDatabaseUserName(m_database);
  }

    // Check table owner
    public  boolean loginTableOwner (String userId) {
        if(GetDatabaseTableOwnerName(m_database).equals(userId)) 
            return true;          
        else 
            return false;          
    }
    public   String loginTableOwner () {
        return GetDatabaseTableOwnerName(m_database);
    }

    // Check login user
    public  boolean dbConnection (String dbConnection) {
        if(GetDatabaseConnectionDefinition(m_database).equals(dbConnection))
        return true;
        else
            return false;
    }
    
    public  String dbConnection() {
        return GetDatabaseConnectionDefinition(m_database);
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
            log("ReadParameter","error","exception handling","Error reading file: " + e.getMessage());
        }
      return sResult;
    
    }

      
  //27-07-2013 New log mechanism 
  public void log(String name, String level, String area, String logMessage) {
     
     String logFileName =Constants.NOT_INITIALIZED;
     
  
     if(m_context.equals(Constants.DEFAULT)) {
     logFileName=m_startDate+"." + m_className;
   Logging.LogEntry(logFileName, name,level,area,logMessage);
     } else {
         logFileName=m_context +"." +m_startDate;
       Logging.LogEntry(logFileName,name,level,area,logMessage);          
     }

   if(m_firstTime) {
     m_firstTime = false;
     System.out.print("\nLog file =>" + logFileName +"<.");          
   } 
   
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

}