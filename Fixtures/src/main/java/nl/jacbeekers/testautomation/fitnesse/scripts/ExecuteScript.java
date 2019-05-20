/**
 * This purpose of this fixture is to call a script with a variable amount of parameters using the FitNesse slim 'script' table.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain / Jac. Beekers
 * @since October 2014
 * @version 20150119.0
 * @version 20160705.0 - Added setError
 * @version 20170107.0 - Added possibility to store fixture outcome in a result file and specifically collect a result value from the file.
 * @version 20170121.0 - Renamed RunScript to ExecuteScript
 * @version 20170304.0 - if a parameter is null, get the value for null values
 */
package nl.jacbeekers.testautomation.fitnesse.scripts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;
import nl.jacbeekers.testautomation.fitnesse.supporting.RunProcess;

public class ExecuteScript {
    private String className = ExecuteScript.class.getName()
            .substring(ExecuteScript.class.getName().lastIndexOf(".")+1);
    private static final String version = "20190520.0";

    private String scriptName = Constants.NOT_PROVIDED;
    private List<String> parameterList = new ArrayList<String>();
    private String[] environment =null;
    private String startDate = Constants.NOT_INITIALIZED;
    private String logFilename = Constants.NOT_INITIALIZED;
    private String context = Constants.NOT_PROVIDED;

    private int logLevel =3;
    private int logEntries =0;
    private String errCode =Constants.OK;
    private String errorMessage =Constants.NOERRORS;
    private String captureErrors =Constants.NO;
    private String captureOutput =Constants.NO;
    private ArrayList<String> stdOut = new ArrayList<String>();

    private String baseDir = Constants.NOT_INITIALIZED;
    private String scriptLoc = Constants.NOT_INITIALIZED; // provided on test page as <baseloc> <subdir>
    private String scriptBaseDir = Constants.NOT_INITIALIZED; // from directory.properties
    private boolean determinePath = true;

    private String resultFileName = Constants.NOT_PROVIDED;
    private String resultFileLocation = Constants.NOT_PROVIDED;

    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;

    private String abortYesNo = Constants.NO;
    private boolean userSetAbortOnError = false;
    
    private String valueForNull = Constants.DEFAULT_VALUE_FOR_NULL;

    //Constructors
    public ExecuteScript() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = className;
        setLogFilename(startDate + "." + className);
    }

    public ExecuteScript(String scriptName) {
        setScriptName(scriptName);
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        this.startDate = sdf.format(started);
        setLogFilename(startDate + "." + className) ;
    }
     
    public ExecuteScript(String scriptName, String context) {    
            this.scriptName = scriptName;    
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      this.startDate = sdf.format(started);
      this.context=context;
      setLogFilename(startDate  +"." + context + "." + className);
    }

    /**
     * @param abortYesNo
     * @throws ExecuteScriptStopTest
     */
    public void setAbortOnError(String abortYesNo) throws ExecuteScriptStopTest {
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
                throw new ExecuteScriptStopTest(logMessage);
            }
        }
    }

    public String getAbortOnError() {
        return this.abortYesNo;
    }

    public void nameScript(String scriptName) {
        this.scriptName = scriptName;
    }
    public void addParameter(String parameter) {
        if(parameter == null) {
            parameter=getValueForNull();
        }
        parameterList.add(parameter);
    }

    public void setDeterminePath(boolean determinePath) { this.determinePath = determinePath ; }
    public boolean getDeterminePath() { return this.determinePath; }

    public String runScriptReturnCode() throws ExecuteScriptStopTest {
        String myName ="runScriptReturnCode";
        String myArea ="init";
        Process process;
        String returnMessage = Constants.OK;
        String s = null;
        String fullPathScript=Constants.NOT_FOUND;
        
        //get base dir
        readParameterFile();
        
        if(determinePath) {
            if (Constants.NOT_INITIALIZED.equals(scriptLoc)) {
                fullPathScript = baseDir + "/" + scriptName;
            } else {
                fullPathScript =
                        DetermineCompleteFileName(scriptLoc, scriptName);
            }
        } else {
            fullPathScript = scriptName;
        }
        
        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(fullPathScript);
        cmdLine.addAll(parameterList);
        String command = String.join(" ", cmdLine);

        log(myName, Constants.INFO, myArea,"Script name is >" + scriptName +"<.");
        printUsedParameters();
        log(myName, Constants.INFO, myArea,"Command line is >" + fullPathScript + "< with the following arguments >" + parameterList.toString() + "<.");

        myArea="RunProcess";
        RunProcess runProcess = new RunProcess(command);
        runProcess.setEnvironment(getEnvironment());
        runProcess.setLogFileName(getLogFilename());
        runProcess.setCaptureOutput(getRerouteStdOut());
        int returnCode = runProcess.runAndWait();
        if (returnCode !=0) {
            log(myName, Constants.ERROR, myArea,"runProcess returned exit code >" + returnCode +"< with error >" + runProcess.getResultMessage() +"<.");
        }
        if(getRerouteStdOut()) {
            setStdOut(runProcess.getCapturedOutput());
        }
        setError(runProcess.getResultCode(), runProcess.getResultMessage());

        return getErrorMessage();
    }

    private void setStdOut(ArrayList<String> stdOut) { this.stdOut = stdOut; }
    public ArrayList<String> getStdOut() { return this.stdOut; }

    private void printUsedParameters() {
        String myName ="printUsedParameters";
        String myArea ="run";
        log(myName, Constants.DEBUG, myArea, "======================" + className + " fixture ======================");
        log(myName, Constants.INFO, myArea, "Entered parameters: ");            
        parameterList.forEach(((s) -> {
            log(myName, Constants.INFO, myArea, s);            
        }));
    }


    private void log(String name, String level, String location, String logText) {
        if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
        }
        logEntries++;
        if(logEntries ==1) {
            Logging.LogEntry(getLogFilename(), className, Constants.INFO, "Fixture version", getVersion());
        }
        Logging.LogEntry(getLogFilename(), name, level, location, logText);
       }

    public void setLogFilename(String logFilename) { this.logFilename = logFilename; }
    public String getLogFilename() { return this.logFilename; }

    public String getLogFilenameLink() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+getLogFilename() +".log\" target=\"_blank\">" + getLogFilename() + "</a>";
        else
            return logUrl+getLogFilename() + ".log";
    }

    public void setLogLevel(String level) {
    String myName ="setLogLevel";
    String myArea ="determineLevel";
    
    logLevel = Constants.logLevel.indexOf(level.toUpperCase());
    if (logLevel <0) {
       log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
       logLevel =3;
    }
    }

    public String getLogLevel() { return Constants.logLevel.get(getIntLogLevel()); }
    public Integer getIntLogLevel() { return logLevel; }

    public static String getVersion() { return version; }

    private void setError(String errCode, String errMsg) {
        setErrorCode(errCode);
        setErrorMessage(errMsg);
    }
    
    private void setErrorCode(String errCode) { this.errCode =errCode; }
    public String getErrorCode() { return this.errCode; }
    private void setErrorMessage(String errMsg) { this.errorMessage =errMsg; }
    public String getErrorMessage() { return this.errorMessage; }

    private boolean getRerouteStdOut() {
        if(Constants.YES.equals(getCaptureOutput()))
        return true;
        else return false;
    }

    private String getCaptureErrors() {
        return this.captureErrors;
    }
    private String getCaptureOutput() {
        return this.captureOutput;
    }
    
    public void setCaptureErrors(String yesNo) {
        this.captureErrors =yesNo;
    }
    public void setCaptureOutput(String yesNo) {
        this.captureOutput =yesNo;
    }
    public ArrayList<String> getCapturedOutput() {
        if(getRerouteStdOut())
            return this.stdOut;
        else
            return null;
    }

    private void readParameterFile() {
      String logMessage = Constants.NOT_INITIALIZED;
      String myName="readParamterFile";
      String myArea="param search result";
      String result =Constants.NOT_FOUND;
      
            baseDir = GetParameters.GetPhysicalSourceDir(Constants.LOGICAL_BASE_DIR);
            scriptBaseDir = GetParameters.GetPhysicalSourceDir(Constants.LOGICAL_SCRIPT_DIR);
        log(myName, Constants.VERBOSE,myArea,"BaseDir =>" + baseDir +"<.");
        log(myName, Constants.VERBOSE,myArea,"ScriptDir =>" + scriptBaseDir +"<.");
        
        logUrl = GetParameters.GetLogUrl();
        logMessage = "logURL >" + logUrl +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
        if(Constants.NOT_FOUND.equals(resultFormat))
            setResultFormat(Constants.DEFAULT_RESULT_FORMAT);
        else
            setResultFormat(result);
        
        logMessage = "resultFormat >" + getResultFormat() +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_DEFAULT_VALUE_FOR_NULL);
        if(Constants.NOT_FOUND.equals(resultFormat))
            setValueForNull(Constants.DEFAULT_VALUE_FOR_NULL);
        else
            setValueForNull(result);
        
        logMessage = "resultFormat >" + resultFormat +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

    }
 
    private String DetermineCompleteFileName(String directory,
                                             String fileName) throws ExecuteScriptStopTest {
        String myName="DetermineCompleteFileName";
        String myArea="init";
        
        String[] sFrom;
        String sCompleteFileName;
        String sRootDir = Constants.NOT_PROVIDED;

        myArea="Splitting";
        sFrom = directory.split(" ", 2);
        if(sFrom.length != 2) {
            log(myName, Constants.DEBUG, myArea,"directory parameter >" +directory +"< could not be split into 2 parts.");
            if (Constants.YES.equals(getAbortOnError())) {
                throw new ExecuteScriptStopTest(getErrorMessage());
            }
            return Constants.ERRCODE_INVALID_LOCATION;
        }
        myArea="Result";
        
        sRootDir = GetParameters.GetPhysicalSourceDir(sFrom[0]);
        sCompleteFileName = sRootDir + "/" + sFrom[1] + "/" + fileName;

        return sCompleteFileName;

    }

    public void setScriptName(String scriptName) { this.scriptName = scriptName; }
    public String getScriptName() { return this.scriptName; }
    public void setScriptLocation(String loc) { this.scriptLoc = loc; }

    public void setResultFileName(String resultFileName) { this.resultFileName = resultFileName; }
    public void setResultLocation(String location) { this.resultFileLocation =location; }
    public String getResultFileLocation() { return this.resultFileLocation; }
    public String getResultFileName() { return this.resultFileName; }
    public String getResultValue(String resultProperty) throws ExecuteScriptStopTest {
        return getResultValue(getResultFileName(), resultProperty);
    }
    
    public String getResultValue(String resultFileName, String resultProperty) throws ExecuteScriptStopTest {
        String myName="getResultValue";
        String myArea="Init";
        String val =Constants.NOT_FOUND;

        String fullPathResultFile =
                DetermineCompleteFileName(getResultFileLocation(), resultFileName);
        
        log(myName, Constants.DEBUG, myArea,"resultFileName=>" + resultFileName + "<.");
        log(myName, Constants.DEBUG, myArea,"resultProperty=>" + resultProperty + "<.");

//        GetParameters getParam = new GetParameters();
        myArea="result";
        val= GetParameters.getPropertyVal(fullPathResultFile, resultProperty);
        
        if(Constants.OK.equals(GetParameters.getErrorCode())) {
            log(myName, Constants.DEBUG, myArea, "GetParameters returned OK");
            return val;
        } else {
            log(myName, Constants.DEBUG, myArea, "GetParameters returned >" + GetParameters.getErrorCode() +"<.");
            return GetParameters.getErrorCode() +": " + GetParameters.getErrorMessage();
        }
        
    }

    public void setValueForNull(String nullValue) { this.valueForNull = nullValue; }
    public String getValueForNull() { return valueForNull; }

    public void setResultFormat(String resultFormat) { this.resultFormat = resultFormat; }
    public String getResultFormat() { return resultFormat; }

    public void setEnvironment(String [] environment) { this.environment = environment; }
    public String[] getEnvironment() { return this.environment; }

    static public class ExecuteScriptStopTest extends Exception {
        @SuppressWarnings("compatibility:-1205507658112959053")
        private static final long serialVersionUID = 1L;

        public ExecuteScriptStopTest() {
        }

        /**
         * @param msg
         */
        public ExecuteScriptStopTest(String msg) {
            super(msg);
        }

    }

}
