package nl.consag.testautomation.supporting;

import java.io.*;
import java.text.SimpleDateFormat;

public class SupportingRunScript {
    
    private String className = "RunScript";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private Integer logLevel =3;
    private String abortYesNo = Constants.YES;
    private String scriptResult = Constants.OK;
    private String errorCode = Constants.OK;
    private String errorMessage = Constants.OK;


  private String baseDir = Constants.NOT_INITIALIZED;
  private String scriptLoc = Constants.NOT_INITIALIZED; // provided on test page as <baseloc> <subdir>
  private String scriptDir = Constants.NOT_INITIALIZED; // calculated based on provided scriptLoc
  private String scriptBaseDir = Constants.NOT_INITIALIZED; // from directory.properties

  private String scriptName = Constants.NOT_INITIALIZED;
  private String parameter = Constants.NOT_INITIALIZED;
    
  public String return_message = Constants.OK;

//----------------------------------------------------------
//Constructor, it gets the name of the script from row in fitnesse table
//----------------------------------------------------------	  
	  public SupportingRunScript(String scriptName) {    
		  this.scriptName = scriptName;    
	    java.util.Date started = new java.util.Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    this.startDate = sdf.format(started);
	      logFileName = startDate + "." + className +"." +this.scriptName;
	  }
	   
	  public SupportingRunScript(String scriptName, String context) {    
		  this.scriptName = scriptName;    
	    java.util.Date started = new java.util.Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    this.startDate = sdf.format(started);
	    this.context=context;
	      logFileName = startDate + "." + className +"."+this.scriptName +"." + context;
	  }
//----------------------------------------------------------
//Function to get parameter from table row in fitnesse table
//----------------------------------------------------------
	  public void addParameter (String parameter) {
			  this.parameter = parameter;
	  }	  

    public void setScriptLocation(String loc) {
      this.scriptLoc = loc;

      
    }
    
  private String DetermineCompleteFileName(String directory,
                                           String fileName) {
      String[] sFrom;
      String sCompleteFileName;
      String sRootDir = Constants.NOT_PROVIDED;

      sFrom = directory.split(" ", 2);
      sRootDir = GetParameters.GetPhysicalSourceDir(sFrom[0]);
      sCompleteFileName = sRootDir + "/" + sFrom[1] + "/" + fileName;

      return sCompleteFileName;

  }


  public String TryOutrunScriptReturnCode() {
      String myName="runScriptReturnCode";
      String command ="/Users/Ik1/consag/fitnesse/testscripts/idq/runidqprofile.sh Demo/Profile_CUSTOMERS";
      String rc = Constants.OK;
      StringBuffer output = new StringBuffer();
      Process p;
      try {
        p = Runtime.getRuntime().exec(command);
          p.waitFor();
          BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
          String line ="";
          while((line = reader.readLine()) != null) {
              output.append(line +"\n");
          }
          log(myName, Constants.DEBUG,"command output",output.toString());

      } catch(Exception e) {
          rc = Constants.FATAL;
          log(myName, Constants.FATAL,"exception","Could not start process for command >"+command+"<. Error: " + e.toString());
      }
      
      return rc;
      
  }
//----------------------------------------------------------
//Function to run the script and give a return code.
//----------------------------------------------------------
	  public String runScriptReturnCode () {
	    String myName = "runScriptReturnCode";
	    String myArea = "initialization";
	    String logMessage = "Start.";
	      log(myName, Constants.DEBUG, myArea, logMessage);        
      String rc = Constants.OK;
		  Process process;
	      String s = null;
        
      //get base dir
      readParameterFile();
      
      if (Constants.NOT_INITIALIZED.equals(scriptLoc)) {
        scriptDir = baseDir +"/" + scriptName;
      } else {
	    scriptDir =
	            DetermineCompleteFileName(scriptLoc, scriptName);
      }

		  try
		  {
			  Runtime rt = Runtime.getRuntime(); 

			  logMessage="Process >" + scriptDir  + " " + parameter + "<.";
		    log(myName, Constants.INFO, myArea, logMessage);        
			  process = rt.exec(scriptDir  +" " + parameter);
			  
	          BufferedReader stdInput = new BufferedReader(new 
		                 InputStreamReader(process.getInputStream()));

		      BufferedReader stdError = new BufferedReader(new 
		                 InputStreamReader(process.getErrorStream()));

		      // read the output from the command
          myArea="script output";
		      logMessage="Here is the standard output of the command:\n";
  		    log(myName, Constants.INFO, myArea, logMessage);        
          
		      while ((s = stdInput.readLine()) != null) {
		                logMessage=s;
		              log(myName, Constants.INFO, myArea, logMessage);        
		            }

		      // read any errors from the attempted command
          myArea="script error output";
		      logMessage ="Here is the standard error of the command (if any):\n";
		    log(myName, Constants.ERROR, myArea, logMessage);        
		      while ((s = stdError.readLine()) != null) {
		              logMessage =s;
		              log(myName, Constants.ERROR, myArea, logMessage);
		            }
		      //wait for process to return a return code
		      myArea="script error output";
		      logMessage ="End of error output (if any)";
		      process.waitFor();
		      //Put return message in the error string to return to fitnesse
              if(process.exitValue() ==0) {
                 rc = Constants.OK; 
              } else {
                  rc = Constants.ERROR;
              }
                      
		  }
		  catch(Exception e)
		  {
		    myArea = "Exception";
		    logMessage="Exception: "+ e.toString();
		    log(myName, Constants.ERROR, myArea, logMessage);
                    rc = Constants.ERROR;
        return_message=e.toString();
		  }
		  return rc;
	  }
          
    private void log(String name, String level, String area, String logMessage) {
        
        if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }

        Logging.LogEntry(logFileName, name, level, area, logMessage);
    }


    /**
     * @return
     */
    public String getLogFilename() {
       return logFileName;
    }

    /**
     * @param level
     */
    public void setLogLevel(String level) {
       String myName ="setLogLevel";
       String myArea ="determineLevel";
       
       
       logLevel = Constants.logLevel.indexOf(level.toUpperCase());
       if (logLevel <0) {
           log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
           logLevel =3;
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

    private void setError(String code, String msg) {
        errorCode=code;
        errorMessage=msg;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

private void readParameterFile() {
  String logMessage = Constants.NOT_INITIALIZED;
  String myName="readParamterFile";
  String myArea="param search result";
        baseDir = GetParameters.GetPhysicalSourceDir(Constants.LOGICAL_BASE_DIR);
        scriptBaseDir = GetParameters.GetPhysicalSourceDir(Constants.LOGICAL_SCRIPT_DIR);
    log(myName, Constants.VERBOSE,myArea,"BaseDir =>" + baseDir +"<.");
    log(myName, Constants.VERBOSE,myArea,"ScriptDir =>" + scriptBaseDir +"<.");
}

}