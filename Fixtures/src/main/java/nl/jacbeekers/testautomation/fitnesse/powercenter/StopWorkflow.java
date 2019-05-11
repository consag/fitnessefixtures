/**
 * This purpose of this fixture is to stop a PowerCenter workflow or instance via a webservice call.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * Based on StartWorkflow
 * @author Jac Beekers
 * @version 20161120.0
 * @since November 2016
 */
package nl.jacbeekers.testautomation.fitnesse.powercenter;

import com.informatica.wsh.*;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.io.*;


import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceRef;


public class StopWorkflow {

    private static String version = "20161120.0";
    private String className = "StopWorkflow";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private int logLevel = 3;
    private int logEntries = 0;
    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;


    private String folder;
    private String application;
    //connectivity
    private String workflowName;
    private String domainName;
    private String repoName;
    private String serviceName;
    private String userName;
    private String passWord;

    //wsdl
    private String WsdlUrl;
    private static URL wsdlLocationURL;
    DataIntegrationService diService;

    private String abortYesNo = Constants.YES;
    private boolean userSetAbortOnError = false;
    private String reportErrorIfNotRunning = Constants.NO;
    private boolean userSetReportErrorIfNotRunning = false;

    List<String> appProps = new ArrayList<String>();

    private String parameterFileName;
    private String parameterDirectory;
    //	protected String return_message = Constants.NOT_INITIALIZED;
    private String workflowResult, FirstErr, LastErr, FirstErrCode =
        Constants.NOT_INITIALIZED;

    private int numStartTime=0;
    private int numEndTime=0;
    private int numDuration=0;
    
    private String workflowInstance = Constants.NOT_PROVIDED;
    
    List<String> iterationReturnCodes = new ArrayList<String>();

    @WebServiceRef
    private static MetadataService metadataService;

    public StopWorkflow() {
        // Constructor
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context = className;
        logFileName = startDate + "." + className;
        readParameterFile();
        readInfaProcessProperties();

    }

    /**
     * @param context
     */
    public StopWorkflow(String context) {
        // Constructor
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        this.context = context;
        logFileName = startDate + "." + className + "." + context;
        readParameterFile();
        readInfaProcessProperties();

    }

    /**
     * @param abortYesNo
     * @throws WorkflowStopTest
     */
    public void setAbortOnError(String abortYesNo) throws WorkflowStopTest {
        //Function to set abort on error, i.e. if workflowstoptest should be thrown in case of exceptions
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
                throw new WorkflowStopTest(logMessage);
            }
        }
    }

    public String getAbortOnError() {
        return this.abortYesNo;
    }

    /**
     * @param application
     */
    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return this.application;
    }

    /**
     * @param folder
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * @param workflowName
     */
    public void setWorkflowName(String workflowName) {
        //Function to get workflow name or DAC run name from row in fitnesse table
        this.workflowName = workflowName;
    }

    /**
     * @param parameterFile
     */
    public void setParameterFile(String parameterFile) {
        this.parameterFileName = parameterFile;
    }

    /**
     * @param parameterDirectory
     */
    public void setParameterDirectory(String parameterDirectory) {
        this.parameterDirectory = parameterDirectory;
    }

    /**
     * @return
     */
    public String error() {
        //let's get the first error msg from pwc repository
        if ("0".equals(FirstErrCode)) {
            return LastErr;
        } else {
            return FirstErr;
        }
    }

    private void setError(String errCode, String errMsg) {
        FirstErrCode = errCode;
        FirstErr = errMsg;
        log("setError", Constants.INFO, "set error code",
            "FirstErrCode set to >" + errCode + "< and FirstErr set to >" + errMsg);
    }

    public String getErrorCode() {
        return FirstErrCode;
    }
    

    private String getSession() throws WorkflowStopTest {
        // create a session with PowerCenter web service hub
        String myName = "getSession";
        String myArea = "undetermined";
        String logMessage = "none";
        String sessionID = Constants.NOT_INITIALIZED;
        String rc = Constants.OK;

        myArea = "initialization";
        logMessage = "start";
        log(myName, Constants.INFO, myArea, logMessage);


        try {
            myArea = "Create Session";
            String infaWsh = "http://www.informatica.com/wsh";
            String dataIntService = "DataIntegrationService";
            logMessage = "Getting QN using >" + infaWsh + "< and >" + dataIntService + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            QName qn = new QName(infaWsh, dataIntService);
            logMessage = "Created QN. String =>" + qn.toString() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            logMessage = "Created QN. Prefix =>" + qn.getPrefix() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            logMessage = "Created QN. LocalPart =>" + qn.getLocalPart() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            logMessage = "Created QN. Namespace =>" + qn.getNamespaceURI() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);

            logMessage = "Getting DataIntegrationService for URL >" + WsdlUrl + "< and QN >" + qn.toString() + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            wsdlLocationURL = new URL(WsdlUrl);
            logMessage = "wsdlLocationURL url is >" + wsdlLocationURL.toString() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);

            diService = new DataIntegrationService(wsdlLocationURL, qn);
            logMessage = "Created diService for wsdlLocationURL =>" + wsdlLocationURL.toString() + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);


        } catch (Exception e) {
            myArea = "Exception GetSession";
            logMessage = "Exception occurred in CreateSession. Error >" + e.toString() + "<.";
            rc = Constants.FATAL;
            String logCode = "INFAPWC0001";
            setError(logCode, e.toString());
            log(myName, Constants.FATAL, myArea, logCode);
            if (Constants.YES.equals(abortYesNo)) {
                workflowResult = Constants.FATAL;
                throw new WorkflowStopTest(logCode + " - " + e.toString());
            }
        }

        if (rc.equals(Constants.OK)) {
            logMessage = "LoginRequest instantiation...";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            LoginRequest loginReq = new LoginRequest();
            logMessage = "Getting diService Data Integration reference...";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            DataIntegrationInterface diInterface = diService.getDataIntegration();

            logMessage = "setting user name for Login to =>" + userName + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            loginReq.setUserName(userName);
            logMessage = "setting password for Login.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            loginReq.setPassword(passWord);
            logMessage = "setting Domain to =>" + domainName + "< for Login.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            loginReq.setRepositoryDomainName(domainName);
            logMessage = "setting Repository Name to =>" + repoName + "< for Login.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            loginReq.setRepositoryName(repoName);
            myArea = "execution";
            logMessage = "About to login...";
            log(myName, Constants.DEBUG, myArea, logMessage);

            try {
                sessionID = diInterface.login(loginReq, null);
            } catch (Fault fault) {
                rc = Constants.FATAL;
                handleInformaticaFault(myName, myArea, fault, Constants.FATAL);
                if (Constants.YES.equals(abortYesNo)) {
                    throw new WorkflowStopTest("Login with user name >=" + userName + "< failed.");
                } else {
                    rc = Constants.FATAL;
                    logMessage = "Login with userid =>" + userName + "< failed =>" + fault.toString() + "<.";
                    log(myName, Constants.ERROR, myArea, logMessage);
                }
            }
            logMessage = "After login... sessionID =>" + sessionID + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
        }

        myArea = "finalization";

        if (rc.equals(Constants.OK)) {
            logMessage = "end. returning =>" + sessionID + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            return sessionID;
        } else {
            logMessage = "end. returning =>" + rc + "<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            return rc;
        }
    }

    private String closeSession(String sessionID) {
        // closes the session (with sessionID)
        String myName = "closeSession";
        String myArea = "undetermined";
        String logMessage = "none";
        String myReturnMsg = "OK";
        // DataIntegrationService diService = new PWCDataIntegrationService();
        DataIntegrationInterface diInterface = diService.getDataIntegration();

        myArea = "initialization";
        logMessage = "start";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        VoidRequest voidReq = new VoidRequest();
        SessionHeader sessHeader = new SessionHeader();
        //diInterface.setHeader(createSessionHeader(sessionID));
        sessHeader.setSessionId(sessionID);
        logMessage = "Logging out sessionID =>" + sessionID + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        try {
            diInterface.logout(voidReq, sessHeader);
        } catch (Fault fault) {
            handleInformaticaFault(myName, myArea, fault, Constants.FATAL);
        }
        logMessage = "Logged out.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        myArea = "finalization";
        logMessage = "end. returning =>" + myReturnMsg + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        return myReturnMsg;
    }

    /**
     * @return Yes if workflow instance stopped
     * @throws Fault
     * @throws WorkflowStopTest
     */
    public String workflowInstanceStopped() throws WorkflowStopTest {
        return workflowStopped();
    
    }

    /**
     * @return Yes if workflow stopped
     * @throws Fault
     * @throws WorkflowStopTest
     */
    public String workflowStopped() throws WorkflowStopTest {
        String myName = "workflowStopped";
        String myArea = "init";
        String logMessage = "none";
        String sessionID = Constants.NOT_INITIALIZED;
        int wfErrorCode = 0;
        String wfErrorMsg = Constants.OK;
        workflowResult = Constants.YES;
        FirstErr = Constants.NOERRORS;
        LastErr = Constants.NOERRORS;

        readParameterFile();
        readInfaProcessProperties();

        // DataIntegrationService dataIntegrationService;
        resetWFInfo();
        myArea = "initialization";
        logMessage = "=================================================================";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        logMessage = "start of =>" + myName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        logMessage = "=================================================================";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        if (userSetAbortOnError) {
            log(myName, Constants.DEBUG, myArea, "User specified the AbortOnError value >" + getAbortOnError() + "<.");
        } else {
            abortYesNo = getDefaultAbortOnErrorValue(getApplication());
            log(myName, Constants.DEBUG, myArea, "AbortOnError has been set to >" + getAbortOnError() + "<.");
        }

        if (userSetReportErrorIfNotRunning) {
            log(myName, Constants.DEBUG, myArea, "User specified the ReportErrorIfNotRunning value >" + getReportErrorIfNotRunning() + "<.");
        } else {
            abortYesNo = getDefaultReportErrorIfNotRunning(getApplication());
            log(myName, Constants.DEBUG, myArea, "ReportErrorIfNotRunning has been set to >" + getReportErrorIfNotRunning() + "<.");
        }

        sessionID = getSession();
        if (Constants.FATAL.equals(sessionID) || Constants.ERROR.equals(sessionID)) {
            if (Constants.YES.equals(abortYesNo)) {
                workflowResult = Constants.FATAL;
                throw new WorkflowStopTest("Session not initialized. Internal error getSession.");
            } else {
                logMessage = "Error occurred while connecting to PowerCenter";
                log(myName, Constants.FATAL, myArea, logMessage);
                return Constants.FATAL;
            }
        }

        WorkflowRequest wfRequest = new WorkflowRequest();
        DIServiceInfo disi = new DIServiceInfo();
        //ETaskRunMode taskRunMode = new ETaskRunMode();

        myArea = "preparation";
        logMessage = "setting folder to =>" + folder + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        wfRequest.setFolderName(folder);
        logMessage = "setting workflow to =>" + workflowName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        wfRequest.setWorkflowName(workflowName);

        logMessage = "setting domain to =>" + domainName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        disi.setDomainName(domainName);
        logMessage = "setting service name to =>" + serviceName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        disi.setServiceName(serviceName);

        wfRequest.setRequestMode(ETaskRunMode.NORMAL);
        wfRequest.setDIServiceInfo(disi);

        myArea = "execution";
        SessionHeader sessHeader = new SessionHeader();
        sessHeader.setSessionId(sessionID);
        logMessage = "setting sessionID to =>" + sessionID + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        //DataIntegrationService diService = new PWCDataIntegrationService();
        // diService is created in the getSession method
        DataIntegrationInterface diInterface = diService.getDataIntegration();

        logMessage =
            "Stopping workflow =>" + folder + "/" + workflowName + "< in domain =>" + domainName + " on IS =>" +
            serviceName + "<.";
        log(myName, Constants.INFO, myArea, logMessage);
        try {
            
            if(Constants.NOT_PROVIDED.equals(getInstance())) {
                logMessage="Stopping Workflow...";
                log(myName, Constants.INFO, myArea, logMessage);
            } else {
                logMessage="Stopping workflow instance >" + getInstance() +"<.";
                log(myName, Constants.INFO, myArea, logMessage);
                wfRequest.setWorkflowRunInstanceName(getInstance());
            }
            diInterface.stopWorkflow(wfRequest, sessHeader);
            diInterface.waitTillWorkflowComplete(wfRequest, sessHeader);

            try {
                WorkflowDetails wfDetails = diInterface.getWorkflowDetails(wfRequest, sessHeader);
                DIServerDate endTime = wfDetails.getEndTime();
                DIServerDate startTime = wfDetails.getStartTime();
                numEndTime = endTime.getUTCTime();
                numStartTime = startTime.getUTCTime();

                wfErrorCode = wfDetails.getRunErrorCode();
                wfErrorMsg = wfDetails.getRunErrorMessage();
                logMessage = "After stop request, waited for workflow to complete. The workflow error code is >" + wfErrorCode + "<.";
                log(myName, Constants.INFO, myArea, logMessage);
                logMessage = "The workflow error message =>" + wfErrorMsg + "<.";
                log(myName, Constants.INFO, myArea, logMessage);
            } catch (Fault fault) {
                handleInformaticaFault(myName, myArea, fault, Constants.ERROR);
            }

        } catch (Fault fault) {
            handleInformaticaFault(myName, myArea, fault, Constants.ERROR);
            if (Constants.YES.equals(abortYesNo)) {
                throw new WorkflowStopTest("Workflow stop exception occurred on workflow =>" + folder + "/" +
                                           workflowName + "<. Error =>" + getWorkflowResult()+"<. Instance was >" +getInstance() +"<.");
            }
        } catch (Exception e) {
            logMessage =
                "Exception stopping workflow =>" + folder + "/" + workflowName + "< in domain =>" + domainName +
                " on IS =>" + serviceName + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            logMessage = "Exception =>" + e.toString() + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            workflowResult = Constants.ERROR;
            if (Constants.YES.equals(abortYesNo)) {
                throw new WorkflowStopTest("Workflow Stop exception occurred on workflow =>" + folder + "/" +
                                           workflowName + "<. Error =>" + getWorkflowResult()+"<. Instance was >" +getInstance() +"<.");
            }
        }
        myArea = "finalization";
        closeSession(sessionID);

        logMessage = "end of =>" + myName + "<. returning =>" + getWorkflowResult() + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        return getWorkflowResult();
    }
    

    private String getWorkflowResult() {

        switch (workflowResult) {
        case "LMWSH_95219":
            if (Constants.NO.equals(getReportErrorIfNotRunning())) {
                workflowResult=Constants.YES;
                    } else {
                workflowResult="Not Running";
                }
            break;
        default:
            break;
        }

        return workflowResult;
    }
    
    public String getReportErrorIfNotRunning() {
        return reportErrorIfNotRunning;
    }
    
    public void setReportErrorIfNotRunning(String reportErrorIfNotRunning) throws WorkflowStopTest {
        //Function to set abort on error, i.e. if workflowstoptest should be thrown in case of exceptions
        userSetReportErrorIfNotRunning = true;
        if (reportErrorIfNotRunning == null || reportErrorIfNotRunning.isEmpty()) {
            this.reportErrorIfNotRunning = Constants.DEFAULT_REPORTERRORIFNOTRUNNING;
        } else {
            if (Constants.YES.equals(reportErrorIfNotRunning) || Constants.NO.equals(reportErrorIfNotRunning)) {
                this.reportErrorIfNotRunning = reportErrorIfNotRunning;
            } else {
                String myName = "setReportErrorIfNotRunning";
                String myArea = "Error";
                String logMessage =
                    "Invalid value =>" + reportErrorIfNotRunning + "< specified for 'report error if not running'. Must be =>" + Constants.YES +
                    "< or =>" + Constants.NO + "<.";
                log(myName, Constants.FATAL, myArea, logMessage);
                throw new WorkflowStopTest(logMessage);
            }
        }
    }

    private void resetWFInfo() {
        workflowResult = Constants.YES;
        FirstErr = Constants.NOERRORS;
        LastErr = Constants.NOERRORS;
        FirstErrCode = Constants.NOERRORS;
        numEndTime = 0;
        numStartTime = 0;
        numDuration =0;
    }

    private String DetermineCompleteFileName(String directory, String fileName) {
        //Method: DetermineCompleteFileName
        //Purpose: Construct the file name including path
        String[] sFrom;
        String sCompleteFileName;
        String sRootDir = "FROMROOTDIR_UNKNOWN";

        if (directory.isEmpty()) {
            return "";
        }
        sFrom = directory.split(" ", 2);
        sRootDir = GetParameters.GetRootDir(sFrom[0]);
        sCompleteFileName = sRootDir + "/" + sFrom[1] + "/" + fileName;
        return sCompleteFileName;
    }

    /**
     * @param directory
     * @param fileName
     * @return
     */
    public String directoryContainsFile(String directory, String fileName) {
        String sCompleteFileName = DetermineCompleteFileName(directory, fileName);
        File f; // file pointer for file move/copy/remove
        f = new File(sCompleteFileName);

        //This method can be called directly, hence we need to set the file pointer
        if (f.exists()) {
            return Constants.YES;
        } else {
            return Constants.NO;
        }
    }

    private void readParameterFile() {
        String myName = "readParameterFile";
        String logMessage = "start";
        String myArea = "initialization";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        myArea = "getValues";

        domainName = GetParameters.getDomainName(application);
        logMessage = "Domain determined =>" + domainName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        repoName = GetParameters.getRepoService(application);
        logMessage = "Repository determined =>" + repoName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        serviceName = GetParameters.getIntService(application);
        logMessage = "Integration Service determined =>" + serviceName + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        userName = GetParameters.getUsername(application);
        logMessage = "User name determined =>" + userName + "<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        passWord = GetParameters.getPassword(application);
        logMessage = "Password determined.";
        log(myName, Constants.VERBOSE, myArea, logMessage);

        WsdlUrl = GetParameters.getWshUrl(application);
        logMessage = "Read from parameter file url=>" + WsdlUrl + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        logUrl = GetParameters.GetLogUrl();
        logMessage = "logURL >" + logUrl +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        
        resultFormat =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
        if(Constants.NOT_FOUND.equals(resultFormat))
            resultFormat =Constants.DEFAULT_RESULT_FORMAT;
        logMessage = "resultFormat >" + resultFormat +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        myArea = "Finalizing";
        logMessage = "end";
        log(myName, Constants.VERBOSE, myArea, logMessage);
    }


    private void handleInformaticaFault(String method, String area, Fault fault, String severity) {
        String myName = "handleInformaticaFault";
        String myArea = "undetermined";
        String logMessage = "none";

        myArea = "errorhandling";
        logMessage = "An error occurred in method >" + method + "<, area >" + area + "<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        FaultDetails faultDetails = fault.getFaultInfo();
        logMessage = "details =>" + faultDetails.getExtendedDetails() + "<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        setError(faultDetails.getErrorCode(), fault.toString());
        myArea = "finalizing";
        logMessage = "error handling completed. Result set to >" + getErrorCode() +"<.";
        log(myName, Constants.INFO, myArea, logMessage);

        workflowResult = getErrorCode();
    }

    private void log(String name, String level, String location, String logText) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        logEntries++;
        if (logEntries == 1) {
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version", getVersion());
        }

        Logging.LogEntry(logFileName, name, level, location, logText);
    }


    /**
     * @return
     */
    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">log</a>";
        else
            return logUrl+logFileName + ".log";
    }

    /**
     * @param level
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

    public String formatResult(String msg) {
        String formatted =msg;
        /*
         * need to compile for 1.6, so can't use switch :(
         * 
        switch (resultFormat) {
        case Constants.RESULT_AS_HTML:
            formatted="<b>"+msg+"</b>";
            break;
        case Constants.RESULT_AS_JSON:
            formatted="\"result\":\"" +msg +"\"";
            break;
        default:
            break;
        }
    */
        if(Constants.RESULT_AS_HTML.equals(resultFormat)) {
            formatted="<b>"+msg+"</b>";
        } else {
            if(Constants.RESULT_AS_JSON.equals(resultFormat)) {
                formatted="\"result\":\"" +msg +"\"";
            }
        }
        return formatted;
    }


    /*
         * read Informatica process properties file
         */
    private void readInfaProcessProperties() {
        String myName = "readInfaProcessProperties";
        String myArea = "init";

        try {
            myArea = "Reading properties";
            File file = new File(Constants.INFA_PROCESS_PROPERTIES);
            FileInputStream fileInput = new FileInputStream(file);
            Properties infaProcessProp = new Properties();
            infaProcessProp.load(fileInput);
            fileInput.close();

            Enumeration<?> enumKeys = infaProcessProp.propertyNames();

            while (enumKeys.hasMoreElements()) {
                String key = (String) enumKeys.nextElement();
                String val = infaProcessProp.getProperty(key, Constants.NOT_FOUND);
                appProps.add(key);
                appProps.add(val);
            }
        } catch (FileNotFoundException e) {
            log(myName, Constants.INFO, myArea,
                "Properties file >" + Constants.INFA_PROCESS_PROPERTIES + "< not found. Defaults will be used.");
        } catch (IOException e) {
            log(myName, Constants.INFO, myArea,
                "I/O error reading properties file >" + Constants.INFA_PROCESS_PROPERTIES +
                "<. Defaults will be used.");
        }

        log(myName, Constants.VERBOSE, myArea, "infaprocess properties are >" + appProps.toString() + "<.");
    }

    private String getDefaultAbortOnErrorValue(String appName) {
        String myName = "getDefaultAbortOnErrorValue";
        String myArea = "init";
        String yesNo = getPropValue(appName.concat(Constants.APPPROP_DELIMITER).concat(Constants.PROP_ABORTONERROR));

        if (Constants.NOT_FOUND.equals(yesNo)) {
            log(myName, Constants.DEBUG, myArea,
                "Default AbortOnError value for application >" + appName + "< not found." + " System default value >" +
                Constants.DEFAULT_ABORTONERROR + "< will be used.");
            return Constants.DEFAULT_ABORTONERROR;
        } else {
            if (Constants.YES.equals(yesNo) || Constants.NO.equals(yesNo)) {
                return yesNo;
            } else {
                log(myName, Constants.DEBUG, myArea,
                    "Default AbortOnError value for application >" + appName + "< has a wrong value >" + yesNo + "<." +
                    " System default value >" + Constants.DEFAULT_ABORTONERROR + "< will be used.");
                return Constants.DEFAULT_ABORTONERROR;
            }
        }

    }

    private String getDefaultReportErrorIfNotRunning(String appName) {
        String myName = "getDefaultReportErrorIfNotRunning";
        String myArea = "init";
        String yesNo = getPropValue(appName.concat(Constants.APPPROP_DELIMITER).concat(Constants.PROP_REPORTERRORIFNOTRUNNING));

        if (Constants.NOT_FOUND.equals(yesNo)) {
            log(myName, Constants.DEBUG, myArea,
                "Default AbortOnError value for application >" + appName + "< not found." + " System default value >" +
                Constants.DEFAULT_REPORTERRORIFNOTRUNNING + "< will be used.");
            return Constants.DEFAULT_REPORTERRORIFNOTRUNNING;
        } else {
            if (Constants.YES.equals(yesNo) || Constants.NO.equals(yesNo)) {
                return yesNo;
            } else {
                log(myName, Constants.DEBUG, myArea,
                    "Default ReportErrorIfNotRunning value for application >" + appName + "< has a wrong value >" + yesNo + "<." +
                    " System default value >" + Constants.DEFAULT_REPORTERRORIFNOTRUNNING + "< will be used.");
                return Constants.DEFAULT_REPORTERRORIFNOTRUNNING;
            }
        }

    }

    private String getPropValue(String appProp) {
        String myName = "getPropValue";
        String myArea = "init";
        String appKey = Constants.NOT_FOUND;
        String appKeyVal = Constants.NOT_FOUND;

        if (appProps.contains(appProp)) {
            appKey = appProps.get(appProps.indexOf(appProp));
            appKeyVal = appProps.get(appProps.indexOf(appProp) + 1);
            log(myName, Constants.DEBUG, myArea, "appKey is >" + appKey + "< and its value is >" + appKeyVal + "<.");
        } else {
            log(myName, Constants.DEBUG, myArea, "appProp >" + appProp + "< not found.");
        }

        return appKeyVal;
    }

    public static String getVersion() {
        return version;
    }

    /**
     * @return
     */
    public String duration() {
        numDuration = numEndTime - numStartTime;
        return Integer.toString(numDuration);
    }

    /**
     * @return
     */
    public String startTime() {
        return Integer.toString(numStartTime);
    }

    /**
     * @return
     */
    public String endTime() {
        return Integer.toString(numEndTime);
    }

    public void setInstance(String wfInstance) {
        String myName="setInstance";
        String myArea="init";
        
        this.workflowInstance =wfInstance;
        log(myName, Constants.DEBUG, myArea, "Instance set to >" + this.workflowInstance + "<.");
    }

    public String getInstance() {
        return this.workflowInstance;
    }
    
    static class WorkflowStopTest extends Exception {
        @SuppressWarnings("compatibility:849567740548138166")
        static final long serialVersionUID = 34985952947L;

        public WorkflowStopTest() {
        }

        /**
         * @param msg
         */
        public WorkflowStopTest(String msg) {
            super(msg);
        }

    }
}
