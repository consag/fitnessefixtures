/**
 * This purpose of this fixture is to run Informatica DQ Mappings
 * @author Jac Beekers
 * @since January 2017
 * @version 20170107.0 - Initial version, based on Mapping
 * @version 20170225.0 - scripts can be configured in fixture.properties
 */

package nl.consag.testautomation.informatica;

import java.text.SimpleDateFormat;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;

//import static supporting.ListUtility.list;
import nl.consag.testautomation.supporting.Logging;

import nl.consag.testautomation.scripts.ExecuteScript;

import nl.consag.testautomation.scripts.ExecuteScript.ExecuteScriptStopTest;

public class DISObject {
    private static final String version = "20170225.0";

    private String className = "DISObject";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private int logLevel =3;
    private int logEntries =0;
    private String abortYesNo = Constants.YES;
    private String errorCode = Constants.OK;
    private String errorMessage = Constants.OK;
    
    private String databaseName = Constants.NOT_INITIALIZED;
    private String project = Constants.NOT_PROVIDED;
    private String folderName = Constants.ALL;
    private String objectName = Constants.ALL;
    private String applicationName = Constants.NOT_PROVIDED;
    private String configFile = Constants.NOT_PROVIDED;

    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;

    // script names
    private String disObjectScript =Constants.RUNIDQDISOBJECT_DEFAULT_SCRIPT;

    private boolean userSetAbortOnError = false;

    public DISObject() {
    // Constructor
            java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
    context=className;
        logFileName = startDate + "." + className;

    }

    /**
    * @param databaseName
    */
    public DISObject(String databaseName) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);

    this.databaseName=databaseName;
    this.project = Constants.ALL;
        logFileName = startDate + "." + className +"." +this.databaseName +"." +this.project;

    }

    /**
    * @param 
    */
    public DISObject(String databaseName, String project) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);

    this.databaseName=databaseName;
    this.project=project;
        logFileName = startDate + "." + className +"." +this.databaseName +"." +this.project;

    }

    /**
    * @param context
    */
    public DISObject(String databaseName, String project, String loglevel) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);

    this.databaseName=databaseName;
    this.project=project;
        logLevel = Constants.logLevel.indexOf(loglevel.toUpperCase());
        logFileName = startDate + "." + className +"." +this.databaseName +"." +this.project;

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
    
    public void projectName(String proj) {
     setProjectName(proj);   
    }
    public void setProjectName(String project) {
        this.project=project;
    }
    public String getProjectName() {
        return this.project;
    }
    public String result() {
        return getResult();
    }
    public String getResult() {
        return getErrorCode();
    }

    public void folderName(String folderName) {
        setFolderName(folderName);
    }
    public void setFolderName(String folderName) {
        this.folderName =folderName;
    }
    public String getFolderName() {
        return this.folderName;
    }

    public void setObjectName(String name) {
        this.objectName =name;
    }
    public String getObjectName() {
        return this.objectName;
    }

    public void setApplicationName(String appName) {
        this.applicationName =appName;
    }
    public String getApplicationName() {
        return this.applicationName;
    }

    public void setConfigFile(String configFile) {
        this.configFile =configFile;
    }
    public String getConfigFile() {
        return this.configFile;
    }

    public String runDISObject() throws DISObjectStopTest {
        return runDISObject(getObjectWithPath(), getObjectName());
    }
    
    private String runDISObject(String objectPath, String objectName) throws DISObjectStopTest {
        String myName="runDISObject";
        String myArea ="init";
        readParameterFile();
                                 
        String logMessage="Running DIS Object >" + objectName +"< with path >" + objectPath +"<.";
            log(myName, Constants.DEBUG,myArea,logMessage);

        ExecuteScript rs = new ExecuteScript(getDisObjectScript(),className + "-" + myName + "-" + objectName);
        rs.setLogLevel(getLogLevel());
        rs.setScriptLocation(Constants.LOGICAL_SCRIPT_DIR + " " + Constants.LOGICAL_LOCATION_IDQSUBDIR);
        rs.setCaptureOutput(Constants.YES);
        rs.setCaptureErrors(Constants.YES);
        
        rs.addParameter(getConfigFile());
        rs.addParameter(getApplicationName());
        rs.addParameter(getObjectName());
        try {
            rs.runScriptReturnCode();
        } catch (ExecuteScriptStopTest e) {
            if (Constants.YES.equals(getAbortOnError())) {
                throw new DISObjectStopTest(getErrorMessage());
            }

        }

        String outCode =parseScriptOutput(rs.getCapturedOutput());
        String errCode =parseScriptOutput(rs.getCapturedErrors());
        String rsCode=rs.getErrorCode();
        
        if (Constants.OK.equals(outCode)) {
            if(Constants.OK.equals(errCode)) {
                setError(rsCode,rs.getErrorMessage());
                return rsCode;
            }
            else {
                setError(errCode,rs.getCapturedErrors());
                return errCode;
            }
        } else {
            setError(outCode, rs.getCapturedOutput());
            return outCode;
        }
        
    }

    /**
     * @param abortYesNo
     * @throws RunScriptStopTest
     */
    public void setAbortOnError(String abortYesNo) throws DISObjectStopTest {
        //Function to set abort on error, i.e. if RunScriptStopTest should be thrown in case of exceptions
        userSetAbortOnError = true;
        if (abortYesNo == null || abortYesNo.isEmpty()) {
            this.abortYesNo = Constants.DEFAULT_ABORTONERROR;
        } else {
            if (Constants.YES.equals(abortYesNo) || Constants.NO.equals(abortYesNo)) {
                this.abortYesNo = abortYesNo;
            } else {
                String myName = "setAbortOnError";
                String myArea = "Error";
                String logMessage =
                    "Invalid value =>" + abortYesNo + "< specified for abort on error. Must be =>" + Constants.YES +
                    "< or =>" + Constants.NO + "<.";
                log(myName, Constants.FATAL, myArea, logMessage);
                throw new DISObjectStopTest(logMessage);
            }
        }
    }

    public String getAbortOnError() {
        return this.abortYesNo;
    }

    private String getObjectWithPath() {
        
        return getProjectName() + Constants.IDQ_PATH_SEPARATOR + getFolderName() + Constants.IDQ_PATH_SEPARATOR
            + getObjectName();
    }

    private String parseScriptOutput(String scriptOutput) {
        String myName ="parseScriptOutput";
        String myArea ="init";
                                 
        String logMessage="Parsing script output for errors in >" + scriptOutput +"<.";
            log(myName, Constants.DEBUG,myArea,logMessage);

        if(scriptOutput.contains("failed with error")
            || scriptOutput.contains("IOException"))
          setError(Constants.ERROR,scriptOutput);
        else
          setError(Constants.OK,Constants.NOERRORS);
        
        return getErrorCode();
        
    }
    
    private void readParameterFile() {
      String logMessage = Constants.NOT_INITIALIZED;
      String myName="readParamterFile";
      String myArea="param search result";
      String result=Constants.NOT_FOUND;
        
        logUrl = GetParameters.GetLogUrl();
        logMessage = "logURL >" + logUrl +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        resultFormat =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
        if(Constants.NOT_FOUND.equals(resultFormat))
            resultFormat =Constants.DEFAULT_RESULT_FORMAT;
        logMessage = "resultFormat >" + resultFormat +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        //Script to use for RunIdqDisObject
        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQDISOBJECT_SCRIPT);
        if(Constants.NOT_FOUND.equals(result)) {
            setDisObjectScript(Constants.RUNIDQDISOBJECT_DEFAULT_SCRIPT);
        }
        else {
            setDisObjectScript(result);
        }
        log(myName, Constants.DEBUG, myArea, "DisObjectScript (if used) will be >" + getDisObjectScript() +"<.");


        log(myName, Constants.DEBUG, "end", "End of readParameterFile");

    }

    /**
     * @return fixture version info
     */
    public static String getVersion() {
        return version;
    }

    public void setDisObjectScript(String disobjectScript) {
        this.disObjectScript = disobjectScript;
    }

    public String getDisObjectScript() {
        return disObjectScript;
    }

    public static class DISObjectStopTest extends Exception {
        @SuppressWarnings("compatibility:4820516936526848471")
        private static final long serialVersionUID = -4189225956320764570L;

        public DISObjectStopTest(){
            }

         /**
          * @param msg
          */
         public DISObjectStopTest(String msg){
           super(msg);
       }

     }

}
