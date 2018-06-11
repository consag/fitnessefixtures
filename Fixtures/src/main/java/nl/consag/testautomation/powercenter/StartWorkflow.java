/**
 * This purpose of this fixture is to call a PowerCenter workflow via a webservice call.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Jac Beekers
 * @version 20160122.0
 * @since March 2014
 */
package nl.consag.testautomation.powercenter;

import com.informatica.wsh.*;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

import java.io.*;


import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceRef;


public class StartWorkflow {

    private static String version = "20160122.0";
    private String className = "StartWorkflow";
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

    List<String> appProps = new ArrayList<String>();


    private String parameterFileName;
    private String parameterDirectory;
    //	protected String return_message = Constants.NOT_INITIALIZED;
    private String workflowResult, FirstErr, LastErr, FirstErrCode, RowsTarget, RowsSource, Duration =
        Constants.NOT_INITIALIZED;
    private int NumSrcFailedRows, NumSrcSuccessRows, NumDuration, NumTables, NumTgtFailedRows, NumTgtSuccessRows, NumTransErrors, NumEndTime, NumStartTime 
        =0;

    private int repeat=1;
    List<String> iterationReturnCodes = new ArrayList<String>();

    @WebServiceRef
    private static MetadataService metadataService;

    public StartWorkflow() {
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
    public StartWorkflow(String context) {
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
    
    /**
     * @return
     */
    public String sourceRows() {
        if (Constants.UNKNOWN.equals(RowsSource)) {
            return Integer.toString(NumSrcSuccessRows);
        } else {
            return RowsSource;
        }
    }

    /**
     * @return
     */
    public String duration() {
        NumDuration = NumEndTime - NumStartTime;
        return Integer.toString(NumDuration);
    }

    /**
     * @return
     */
    public String startTime() {
        return Integer.toString(NumStartTime);
    }

    /**
     * @return
     */
    public String endTime() {
        return Integer.toString(NumEndTime);
    }

    /**
     * @return
     */
    public String targetRows() {
        if (Constants.UNKNOWN.equals(RowsTarget)) {
            return Integer.toString(NumTgtSuccessRows);
        } else {
            return RowsTarget;
        }
    }

    /**
     * @return
     */
    public int sourceSuccessRows() {
        return NumSrcSuccessRows;
    }

    /**
     * @return
     */
    public int sourceFailedRows() {
        return NumSrcFailedRows;
    }

    /**
     * @return
     */
    public int numTables() {
        return NumTables;
    }

    /**
     * @return
     */
    public int targetFailedRows() {
        return NumTgtFailedRows;
    }

    /**
     * @return
     */
    public int targetSuccessRows() {
        return NumTgtSuccessRows;
    }

    /**
     * @return
     */
    public int tranformationErrors() {
        return NumTransErrors;
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
                throw new WorkflowStopTest(logCode);
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
     * @return
     * @throws Fault
     * @throws WorkflowStopTest
     */
    public String workflowSuccessful() throws WorkflowStopTest {
        // startWorkflow
        String myName = "workflowSuccessful";
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

        if (parameterDirectory == null || parameterDirectory.isEmpty()) {
            this.parameterDirectory = Constants.NOT_PROVIDED;
            logMessage = "no parameter directory provided.";
        } else {
            logMessage = "Provided parameter directory =>" + parameterDirectory + "<.";
        }
        log(myName, Constants.DEBUG, myArea, logMessage);

        if (parameterFileName == null || parameterFileName.isEmpty()) {
            this.parameterFileName = Constants.NOT_PROVIDED;
            logMessage = "no parameter file provided.";
        } else {
            logMessage = "parameter file =>" + parameterFileName + "<.";
        }
        log(myName, Constants.DEBUG, myArea, logMessage);


        if (!(Constants.NOT_PROVIDED.equals(parameterDirectory) || Constants.NOT_PROVIDED.equals(parameterFileName))) {
            String paramFile = DetermineCompleteFileName(parameterDirectory, parameterFileName);
            logMessage = "Transformed parameter file name to =>" + paramFile + "<.";
            log(myName, Constants.VERBOSE, myArea, logMessage);
            if (directoryContainsFile(parameterDirectory, parameterFileName).equals("Yes")) {
                wfRequest.setParameterFileName(paramFile);
            } else {
                logMessage = "Parameter file =>" + paramFile + "< could not be found.";
                log(myName, Constants.FATAL, myArea, logMessage);
                throw new WorkflowStopTest(logMessage);
            }
        } else {
            logMessage = "ParameterFileName has not been set as no parameter file/directory had been specified.";
            log(myName, Constants.INFO, myArea, logMessage);
        }

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
            "starting workflow =>" + folder + "/" + workflowName + "< in domain =>" + domainName + " on IS =>" +
            serviceName + "<.";
        log(myName, Constants.INFO, myArea, logMessage);
        try {
            diInterface.startWorkflow(wfRequest, sessHeader);
        } catch (Fault fault) {
            handleInformaticaFault(myName, myArea, fault, Constants.FATAL);
            workflowResult = Constants.FATAL;
            if (Constants.YES.equals(abortYesNo)) {
                throw new WorkflowStopTest("Workflow start exception occurred on workflow =>" + folder + "/" +
                                           workflowName + "<.");
            }
        } catch (Exception e) {
            logMessage =
                "Exception starting workflow =>" + folder + "/" + workflowName + "< in domain =>" + domainName +
                " on IS =>" + serviceName + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            logMessage = "Exception =>" + e.toString() + "<.";
            log(myName, Constants.FATAL, myArea, logMessage);
            workflowResult = Constants.FATAL;
            if (Constants.YES.equals(abortYesNo)) {
                throw new WorkflowStopTest("Workflow start exception occurred on workflow =>" + folder + "/" +
                                           workflowName + "<.");
            }
        }
        try {
            diInterface.waitTillWorkflowComplete(wfRequest, sessHeader);
        } catch (Fault fault) {
            handleInformaticaFault(myName, myArea, fault, Constants.ERROR);
            //throw new WorkflowStopTest("Workflow >=" +folder+"/"+workflowName+"< failed.");
        } catch (Exception e) {
            logMessage =
                "Exception waiting on completion of workflow =>" + folder + "/" + workflowName + "< in domain =>" +
                domainName + " on IS =>" + serviceName + "<.";
            log(myName, Constants.ERROR, myArea, logMessage);
            logMessage = "Exception =>" + e.toString() + "<.";
            log(myName, Constants.FATAL, myArea, logMessage);
            workflowResult = Constants.FATAL;
            if (Constants.YES.equals(abortYesNo)) {
                throw new WorkflowStopTest("Waiting on workflow completion exception occurred on workflow >=" + folder +
                                           "/" + workflowName + "<.");
            }
        }

        myArea = "result determination";

        try {
            WorkflowDetails wfDetails = diInterface.getWorkflowDetails(wfRequest, sessHeader);
            DIServerDate endTime = wfDetails.getEndTime();
            DIServerDate startTime = wfDetails.getStartTime();
            NumEndTime = endTime.getUTCTime();
            NumStartTime = startTime.getUTCTime();

            wfErrorCode = wfDetails.getRunErrorCode();
            wfErrorMsg = wfDetails.getRunErrorMessage();
            logMessage = "Workflow error code =>" + wfErrorCode + "<.";
            log(myName, Constants.INFO, myArea, logMessage);
            logMessage = "Workflow error message =>" + wfErrorMsg + "<.";
            log(myName, Constants.INFO, myArea, logMessage);
        } catch (Fault fault) {
            handleInformaticaFault(myName, myArea, fault, Constants.ERROR);
        }

        if (wfErrorCode == 0 && workflowResult.equals(Constants.YES)) {

            GetSessionStatisticsRequest sessStatReq = new GetSessionStatisticsRequest();
            sessStatReq.setDIServiceInfo(disi);
            sessStatReq.setFolderName(folder);
            //                sessStatReq.setWorkflowName(workflowName);


            /*Assume session name equals workflow name (prefix wf_ removeD). Also: no worklets supported
        	String sessName ="s_" + workflowName.substring(3);
        	sessStatReq.setTaskInstancePath(sessName);
        	sessStatReq.setWorkflowName(workflowName);
        	try {  SessionStatistics sessStat =diInterface.getSessionStatistics(sessStatReq, sessHeader);
        		NumSrcFailedRows=sessStat.getNumSrcFailedRows();
        		NumSrcSuccessRows=sessStat.getNumSrcSuccessRows();
        		NumTables=sessStat.getNumTables();

        		NumTgtFailedRows=sessStat.getNumTgtFailedRows();
        		NumTgtSuccessRows=sessStat.getNumTgtSuccessRows();
        		NumTransErrors=sessStat.getNumTransErrors();
        	}
        	catch (Fault fault) {
        		logMessage="Could not determine session level results. Likely the session name is different from the workflow name =>" +workflowName +"<.";
        		log(myName, Constants.WARNING, myArea, logMessage);
                        workflowResult=Constants.WARNING;
        	}
*/
        }

        myArea = "finalization";
        closeSession(sessionID);

        logMessage = "end of =>" + myName + "<. returning =>" + workflowResult + "<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        return workflowResult;
    }

    private void resetWFInfo() {
        workflowResult = Constants.YES;
        FirstErr = Constants.NOERRORS;
        LastErr = Constants.NOERRORS;
        FirstErrCode = Constants.NOERRORS;
        RowsTarget = Constants.NOT_INITIALIZED;
        RowsSource = Constants.NOT_INITIALIZED;
        Duration = Constants.NOT_INITIALIZED;
        NumSrcFailedRows = 0;
        NumSrcSuccessRows = 0;
        NumDuration = 0;
        NumTables = 0;
        NumTgtFailedRows = 0;
        NumTgtSuccessRows = 0;
        NumTransErrors = 0;
        NumEndTime = 0;
        NumStartTime = 0;
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
        logMessage = "error handling completed.";
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


    public String setRepeat(String repeat) {
        this.repeat =Integer.parseInt(repeat);
        
        return Constants.NOT_IMPLEMENTED;
    }
    
    public String getRepeat(){
        return Integer.toString(repeat);
    }

    public String allWorkflowsSuccessful() throws WorkflowStopTest {
        String myName ="allWorkflowsSuccessful";
        String myArea ="init";
        String logMessage =Constants.OK;
        String overallReturnCode =Constants.YES;
        String latestReturnCode =Constants.YES;
        
        logMessage = "Start.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        readParameterFile();
        readInfaProcessProperties();

        myArea="running";
        for(int i=0 ; i < repeat ; i++) {
            String thisRunReturnCode =Constants.OK;
            logMessage = "Iteration >" + Integer.toString(i+1) +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            
            thisRunReturnCode = workflowSuccessful();
            
            logMessage ="Iteration >" + Integer.toString(i+1) +"< returned >" + thisRunReturnCode +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            
            setReturnCodeForIteration(i,thisRunReturnCode);
            if(! Constants.YES.equals(thisRunReturnCode)) {
                overallReturnCode =Constants.ERROR;
                latestReturnCode =thisRunReturnCode;
                if(getAbortOnError().equals(Constants.YES)) {
                    throw new WorkflowStopTest(logMessage);
                }
            }
        }

        myArea="completion";
        
        logMessage = "End with return code >" +overallReturnCode +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);

        return overallReturnCode;
        
    }

    private void setReturnCodeForIteration(int iteration, String iterationReturnCode) {
        iterationReturnCodes.add(iteration, iterationReturnCode);
    }

    public String getReturnCodeForIteration(String iteration) {
        String myName="getReturnCodeForIteration";
        String myArea="run";
        String logMessage=Constants.OK;
        int nrIteration =1;
        String rc =Constants.YES;
        
        try {
           nrIteration =Integer.parseInt(iteration);
        }
        catch (NumberFormatException e) {
            logMessage ="Invalid iteration value >" + iteration +"< specified. Must be numeric";
            log(myName, Constants.FATAL, myArea, logMessage);   
            return "Non-numeric iteration number specified";
        }

        if(nrIteration > iterationReturnCodes.size()) {
            logMessage ="Invalid iteration value >" + iteration 
                        +"< specified. Number of iterations with a return code =>" +Integer.toString(iterationReturnCodes.size()) +"<.";
            log(myName, Constants.ERROR, myArea, logMessage);   
            return "Iteration not found";
        }
        
        rc =iterationReturnCodes.get(nrIteration);
        logMessage ="Iteration >" +iteration +"< has return code >" +rc +"<.";
        log(myName, Constants.FATAL, myArea, logMessage);   

        return rc;
        
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
