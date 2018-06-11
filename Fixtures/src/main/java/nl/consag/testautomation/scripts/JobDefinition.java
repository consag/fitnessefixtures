/**
 * @author Jac. Beekers
 * @since   November 2016
 * @version 20161225.0 - initial version - Skeleton
 * @version 20170113.0 - implemented IDQ Mapping
 * @version 20170225.0 - scripts can be configured in fixture.properties
 * @version 20170325.0 - support for new step type wait
 * @version 20170909.0 - OnlyGenerate, so fixture does not execute generated job file. Code cleanup
 */ 

package nl.consag.testautomation.scripts;

import java.io.BufferedWriter;

import java.io.FileWriter;

import java.text.*;
import java.util.*;

import java.util.stream.Collectors;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.scripts.ExecuteScript.ExecuteScriptStopTest;

public class JobDefinition {
    private static String version ="20170909.0";

    private String className = "JobDefinition";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;

    private String url;
    private String databaseConnDef;

    private int logLevel =4;
    private int logEntries =0;
        
    private List<List<String>> returnTable = new ArrayList<List<String>>(); //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")
	  
    private String result = Constants.OK;
    private String resultMessage = Constants.NOERRORS;
    private String errorMessage = Constants.NOERRORS;
    private List<String> resultRow= new ArrayList<String>();
    
    List<List<String>> orderedInputTable = new ArrayList<List<String>>();
    
    private String applicationName =Constants.NOT_PROVIDED;
    private String jobName =Constants.NOT_PROVIDED;

    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;
    // script names
    private String jobScript =Constants.RUNIDQJOB_DEFAULT_SCRIPT;
    private String profileScript =Constants.RUNIDQPROFILE_DEFAULT_SCRIPT;
    private String mappingScript =Constants.RUNIDQMAPPING_DEFAULT_SCRIPT;
    private String disObjectScript =Constants.RUNIDQDISOBJECT_DEFAULT_SCRIPT;
    
    private String logicalScriptDirLocation =Constants.LOGICAL_SCRIPTDIR_IDQ;
    private String logicalLocationIdqSubDir = Constants.LOGICAL_LOCATION_IDQSUBDIR;
    private String jobDir =Constants.DEFAULT_JOBDIR;
    private String resultDir =Constants.DEFAULT_RESULTDIR;
    
    private boolean appendToFile=false;
    
    private String jobVersion=Constants.NOT_PROVIDED;
    private String jobPersistence=Constants.NOT_PROVIDED;
    private String jobMaxConcurrency=Constants.NOT_PROVIDED;
    private String jobIDQConfig=Constants.NOT_PROVIDED;
    private String jobPWCConfig=Constants.NOT_PROVIDED;
    private String jobOracleConfig=Constants.NOT_PROVIDED;
    private String jobDB2Config=Constants.NOT_PROVIDED;
    private String jobDACConfig=Constants.NOT_PROVIDED;
    private String jobScriptConfig=Constants.NOT_PROVIDED;

    private int stepNumber=0;
    private String stepName=Constants.NOT_PROVIDED;
    private String stepType=Constants.NOT_PROVIDED;
    private String stepProjectName=Constants.NOT_PROVIDED;
    private String stepFolderName=Constants.NOT_PROVIDED;
    private String stepApplicationName=Constants.NOT_PROVIDED;
    private String stepObjectName=Constants.NOT_PROVIDED;
    private String stepMappingName=Constants.NOT_PROVIDED;
    private String stepExpectedResult=Constants.NOT_PROVIDED;
    private String stepAbortOnError=Constants.NOT_PROVIDED;
    private String stepOnErrorAction=Constants.NOT_PROVIDED;
    private String stepWait=Constants.NOT_PROVIDED;
    private String stepJobMustWaitOnStepCompletion=Constants.NOT_PROVIDED;
    
    // IDQ Jobs
    private String stepIdqObjectType=Constants.NOT_PROVIDED;
    
    private String onlyGenerate=Constants.DEFAULT_ONLY_GENERATE_JOBFILE;

    public JobDefinition() {
	//Constructors
      	java.util.Date started = new java.util.Date();
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      	startDate = sdf.format(started);
      	this.context=className;
        logFileName = startDate + "." + className;
    }

    /**
     * @param context
     */
public JobDefinition(String context) {
    	java.util.Date started = new java.util.Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	startDate = sdf.format(started);
    	this.context=context;
        logFileName = startDate + "." + className +"." + context;
   }

    /**
     * @param context
     * @param logLevel
     */
public JobDefinition(String context, String logLevel) {
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
    this.context=context;
    logFileName = startDate + "." + className +"." + context;
    setLogLevel(logLevel);
    this.logLevel = getIntLogLevel();
    }


/**
 * @param FitNesse input table
 * @return FitNesse table with results
 */
public List doTable(List<List<String>> inputTable) { 
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="doTable";
    String myArea="Initialization";
    List<String> tableAsListOfStrings = new ArrayList<String>();
    List<String> orderedStrings = new ArrayList<String>();
    List<List<String>> orderedInputTable = new ArrayList<List<String>>();
		    
    logMessage="number of rows in select FitNesse table: " + Integer.toString(inputTable.size()); 
    log(myName, Constants.DEBUG, myArea, logMessage);
    readParameterFile();
    for(int i=0; i < inputTable.size(); i++) {
        tableAsListOfStrings.add(makeDelimitedString(inputTable.get(i),Constants.FITNESSE_DELIMITER));
    }
    orderedStrings = orderRowsByStepNumber(tableAsListOfStrings, Constants.FITNESSE_DELIMITER);
    for(int i=0; i < orderedStrings.size(); i++) {
        List<String> row = new ArrayList<String>();
        row =splitRow(orderedStrings.get(i), "\\"+Constants.FITNESSE_DELIMITER);
        orderedInputTable.add(row);
    }
    
    //First row is the job row for the application
    //Second row only contains the word "Steps" - may change to set parameters for all steps, but not for the job
    if(orderedInputTable.size() > 1) {
        processJobRow(orderedInputTable.get(0));
        outputJobSettings();
        addRowToReturnTable(getResultRow());
        processStepsRow(orderedInputTable.get(1));
        addRowToReturnTable(getResultRow());
    }

    for(int i=2; i < orderedInputTable.size(); i++) {
        processRow(orderedInputTable.get(i));            
        addRowToReturnTable(getResultRow());
    }

    if(Constants.NO.equalsIgnoreCase(getOnlyGenerate())) {
        readParameterFile();
    
        ExecuteScript script = new ExecuteScript(getJobScript(), "JobDefinition");
        script.setLogLevel(getLogLevel());
        script.setScriptLocation(getLogicalScriptDirLocation() + " " + getLogicalLocationIdqSubDir() );
        script.setLogLevel(Constants.DEBUG);
        script.setCaptureErrors(Constants.YES);
        script.setCaptureOutput(Constants.YES);
        script.setResultFileName(getJobName()+".result");
        script.setResultLocation(Constants.LOGICAL_BASE_DIR + " results");
        script.addParameter("-log2parent");
        script.addParameter(Constants.YES);
        script.addParameter("-jobfile");
        script.addParameter(getJobDir() + getApplicationName() + '-' + getJobName() +".job");
        script.addParameter("-resultfile");
        script.addParameter(getResultDir() + getJobName() +".result");
        List<String> resultRow = new ArrayList<String>();
        String rc=Constants.UNKNOWN;
        String errMsg=Constants.UNKNOWN;

        try {
            rc = script.runScriptReturnCode();
            errMsg = script.getResultValue("result");
            if("0".equals(rc)) {
                log(myName, Constants.INFO, "Script result","Script " + getJobScript() + "  returned >0<. Script log file is >" + script.getLogFilename() +"<.");
                setError(Constants.OK, Constants.NOERRORS);
                resultRow.add(Constants.FITNESSE_PREFIX_PASS + Constants.NOERRORS);
            } else {
                logMessage="Script >" + getJobScript() + "< returned >" + rc +"<. log file >" + script.getLogFilename() +"<.";
                log(myName, Constants.ERROR, "Script result", logMessage);
                setError(Constants.ERROR,logMessage);
                resultRow.add(Constants.FITNESSE_PREFIX_FAIL + "Script >" + getJobScript() + "< returned >" + rc +"< with message >" +errMsg +"<." );
            }
        } catch (ExecuteScriptStopTest e) {
            logMessage="Error running >" + getJobScript() +"<. Exception: " + e.toString();
            log(myName, Constants.ERROR, "Exception handler", logMessage);
            setError(Constants.ERROR,logMessage);            
            resultRow.add(Constants.FITNESSE_PREFIX_FAIL + "rc=>" +rc +"< - errMsg=>" +errMsg+"<. Exception =>" + e.toString() +"<.");
        }

        addRowToReturnTable (resultRow);   
        //add a row for the link to the script log file
        List<String> logFileRow = new ArrayList<String>();
        logFileRow = new ArrayList<String>();
        logFileRow.add(Constants.FITNESSE_PREFIX_REPORT +"script log file");
        logFileRow.add(Constants.FITNESSE_PREFIX_REPORT + script.getLogFilename());
        addRowToReturnTable (logFileRow);   
    }
    
    List<String> logFileRow = new ArrayList<String>();
    logFileRow.add(Constants.FITNESSE_PREFIX_REPORT +"log file");
    logFileRow.add(Constants.FITNESSE_PREFIX_REPORT +getLogFilename());
    addRowToReturnTable (logFileRow);	

    return getReturnTable();

}

private List<String> splitRow(String src, String delimiter) {
    String myName ="splitRow";
    String[] splittedString;

    log(myName, Constants.DEBUG, "init", "Splitting up >" +src +"< using delimiter >" + delimiter + "<.");
    splittedString = src.split(delimiter);
    log(myName, Constants.DEBUG, "result","String split up in >" + splittedString.length +"< item(s).");
    return Arrays.asList(splittedString);
    
}

private String makeDelimitedString(List<String> src, String delimiter) {
    String result=Constants.NOT_INITIALIZED;
    
    for(int i=0; i < src.size(); i++) {
        if(i==0) {
            result=src.get(i);
        } else {
            result=result.concat(delimiter).concat(src.get(i));
        }
    }
    
    return result;
}
    

private List<String> orderRowsByStepNumber(List<String> inputTable, String delimiter) {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="orderRowsByStepNumber";
    String myArea="Init";
    List<String> orderedList; // = new ArrayList<String>();
    
    orderedList=inputTable.stream().skip(2).collect(Collectors.toList());
    
    Collections.sort(orderedList, new Comparator<String>() {
        public int compare(String o1, String o2) {
            return extractInt(o1) - extractInt(o2);
        }

        int extractInt(String s) {
            //String num = s.replaceAll("\\D", "");
            String num = s.substring(0, s.indexOf(Constants.FITNESSE_DELIMITER) -1);
            // return 0 if no digits found
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        }
    });

//    Collections.sort(orderedList);
    orderedList.add(0,inputTable.get(0));
    orderedList.add(1,inputTable.get(1));
    
    return orderedList;
}

private void outputJobSettings() {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="outputJobSettings";
    String myArea="processing";
    
    logMessage="Job settings"; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="=================================="; log(myName, Constants.INFO, myArea, logMessage);      

    logMessage="Application Name ................>" +getApplicationName() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Job Name ........................>" +getJobName() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Job Version .....................>" +getJobVersion() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Persistence .....................>" +getJobPersistence() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Max Concurrency .................>" +getJobMaxConcurrency() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="IDQ Configuration file ..........>" +getJobIDQConfig() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="PowerCenter Configuration file ..>" +getJobPWCConfig() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Oracle Configuration file .......>" +getJobOracleConfig() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="DB2 Configuration file ..........>" +getJobDB2Config() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="DAC Configuration file ..........>" +getJobDACConfig() +"<."; log(myName, Constants.INFO, myArea, logMessage);      
    logMessage="Script Configuration file .......>" +getJobScriptConfig() +"<."; log(myName, Constants.INFO, myArea, logMessage);      

    logMessage="=================================="; log(myName, Constants.INFO, myArea, logMessage);      
    
    
}

private String processJobRow(List<String> row) {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="processJobRow";
    String myArea="init";
    
    List<String> returnRow = new ArrayList<String>();  
    returnRow=row.stream().skip(0).collect(Collectors.toList());

    List<String> outputRow = new ArrayList<String>();  
    outputRow=row.stream().skip(0).collect(Collectors.toList());

    /*Format of the application row:
     * 
     * |IDQ|IDQ Example Job|Version=20161113.0|Persistence=Yes|Max Concurrent=2|IDQConfig=/rabo/sserole/tws/config/idq.cfg|PWCConfig=/rabo/sserole/tws/config/pwc_idq.cfg|
     * |Application Name|Job Name |VersionInfo|Param1=...|Param2=...| etc.
     */
    
    if(row.size() <3) {
        logMessage="Insufficient arguments >" + row.size() +"<."; 
        log(myName, Constants.ERROR, myArea, logMessage);      
        setError(Constants.ERROR, logMessage);
        return Constants.ERROR;
    }
    
    setApplicationName(row.get(0));
    logMessage="Application Name >" + getApplicationName() +"<."; 
    log(myName, Constants.INFO, myArea, logMessage);      

    setJobName(row.get(1));
    logMessage="Job Name >" + getJobName() +"<."; 
    log(myName, Constants.INFO, myArea, logMessage);      

    for(int i=0; i < returnRow.size(); i++) {
        String[] splitted =returnRow.get(i).split(Constants.PROPERTY_DELIMITER, 2);
        if(splitted.length <2) continue;
        
        switch(splitted[0].toLowerCase().replaceAll(" ", "")) {
        case Constants.JOB_PARAM_ONLYGENERATE:
            setOnlyGenerate(splitted[1]);
            outputRow.remove(i);
            break;
        case Constants.JOB_PARAM_VERSION:
            setJobVersion(splitted[1]);
            break;
        case Constants.JOB_PARAM_PERSISTENCE:
            setJobPersistence(splitted[1]);
            break;
        case Constants.JOB_PARAM_MAX_CONCURRENCY:
        case Constants.JOB_PARAM_MAX_CONCURRENT:
            setJobMaxConcurrency(splitted[1]);
            break;
        case Constants.JOB_PARAM_IDQCONFIG:
            setJobIDQConfig(splitted[1]);
            break;
        case Constants.JOB_PARAM_PWCCONFIG:
            setJobPWCConfig(splitted[1]);
            break;
        case Constants.JOB_PARAM_ORACLECONFIG:
            setJobOracleConfig(splitted[1]);
            break;
        case Constants.JOB_PARAM_DB2CONFIG:
            setJobDB2Config(splitted[1]);
            break;
        case Constants.JOB_PARAM_DACCONFIG:
            setJobDACConfig(splitted[1]);
            break;
        case Constants.JOB_PARAM_SCRIPTCONFIG:
            setJobScriptConfig(splitted[1]);
            break;
        default:
            break;
        }
    }
    
    returnRow=formatRow(returnRow,Constants.FITNESSE_PREFIX_REPORT);
    setResultRow(returnRow);
    //FitNesse omits the Table: header, but it's needed in the job file
    List<String> header = new ArrayList<String>();
    header.add(Constants.JOB_DEFINITION);
    writeToJobFile(header);
    writeToJobFile(outputRow);

    return Constants.OK;

}

public void setJobVersion(String version) {
    jobVersion=version;
}

public String getJobVersion(){
    return this.jobVersion;
}

public void setApplicationName(String appName) {
    this.applicationName =appName.replaceAll(" ", "");
}

public String getApplicationName() {
    return this.applicationName;
}

public void setJobName(String jobName){
    this.jobName=jobName.replaceAll(" ", "");
}

public String getJobName() {
     return this.jobName;
}

private String processStepsRow(List<String> row) {
    List<String> returnRow = new ArrayList<String>();  
    returnRow=row.stream().skip(0).collect(Collectors.toList());

    returnRow=formatRow(returnRow,Constants.FITNESSE_PREFIX_REPORT);
    setResultRow(returnRow);
    writeToJobFile(row);

    return Constants.OK;

}

/*
 * processRow: process the row, typically an Informatica request. Produces a resultRow
 * 
 */
private String processRow(List<String> row) {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="processRow";
    String myArea="init";

    List<String> returnRow = new ArrayList<String>();  
    returnRow=row.stream().skip(0).collect(Collectors.toList());
    
    // row does not have a step number, therefore it cannot be a step that has to be executed
    // eg. The first and second row have the application name and the word 'Steps' respectively
    if(! isStepNrNumeric(row)) {
        returnRow=formatRow(returnRow,Constants.FITNESSE_PREFIX_REPORT);
        returnRow.add(0,Constants.FITNESSE_PREFIX_IGNORE.concat("Not a step"));
        setResultRow(returnRow);
        writeToJobFile(row);
        logMessage="Row >" + row.get(0) +"< is not numeric and is ignored as task.";
        log(myName, Constants.INFO, myArea, logMessage);      
        return Constants.OK;
    }
    
    // Fixture supports or will support only specific tasks
    if(! isValidStepType(row)) {
        returnRow=formatRow(returnRow,Constants.JOB_STEPTYPE_COLNR,Constants.FITNESSE_PREFIX_FAIL);
        returnRow=formatRow(returnRow,Constants.FITNESSE_PREFIX_REPORT);
        returnRow.add(0,Constants.FITNESSE_PREFIX_FAIL +" Invalid step type");
        setResultRow(returnRow);
        logMessage="Row >" + row.get(0) +"< does not contain a valid step type.";
        log(myName, Constants.ERROR, myArea, logMessage);      
        return Constants.ERROR;
    }
    
    // Fixture may not yet have a specific task implemented
    if(isImplementedStepType(row)) {
        logMessage="Job Step type >" + row.get(2) +"< is valid and implemented."; 
        log(myName, Constants.DEBUG, myArea, logMessage);      

        setStepNumber(Integer.parseInt(row.get(Constants.JOB_STEP_COLNR)));
        setStepName(row.get(Constants.JOB_STEPNAME_COLNR));
        setStepType(row.get(Constants.JOB_STEPTYPE_COLNR));
        
        setResult(Constants.UNKNOWN);
            
    } else {
        returnRow=formatRow(returnRow,Constants.JOB_STEPTYPE_COLNR,Constants.FITNESSE_PREFIX_IGNORE);
        returnRow=formatRow(returnRow,Constants.FITNESSE_PREFIX_REPORT);
        returnRow.add(0,Constants.JOB_STEPTYPE_NOT_IMPLEMENTED);
        setResultRow(returnRow);
        writeToJobFile(row);
        logMessage="Row >" + row.get(0) +"< contains a valid step type, but step type has not yet been implemented.";
        log(myName, Constants.WARNING, myArea, logMessage);      
        return Constants.WARNING;
    }
   
    returnRow.add(Constants.JOB_STEPRESULT_COLNR,getResult());  // first column will contain result
    switch (getResult()) {
    case Constants.OK:
        returnRow=formatRow(returnRow,Constants.JOB_STEPRESULT_COLNR,Constants.FITNESSE_PREFIX_PASS);
        break;
    case Constants.ERROR:
        returnRow=formatRow(returnRow,Constants.JOB_STEPRESULT_COLNR,Constants.FITNESSE_PREFIX_FAIL);
        break;
    case Constants.WARNING:
        returnRow=formatRow(returnRow,Constants.JOB_STEPRESULT_COLNR,Constants.FITNESSE_PREFIX_IGNORE);
        break;
    default:
        returnRow=formatRow(returnRow,Constants.JOB_STEPRESULT_COLNR,Constants.FITNESSE_PREFIX_IGNORE);
        break;
    }
    for(int i=1; i < returnRow.size(); i++) {
        returnRow.set(i,Constants.FITNESSE_PREFIX_REPORT.concat(returnRow.get(i)));
    }

    setResultRow(returnRow);
    writeToJobFile(row);
    
    return getResult();
}

private List<String> formatRow(List<String> theInputRow, String formatToApply) {
    String myName="formatRow ls-s";
    String myArea="init";
    String logMessage=Constants.NOT_PROVIDED;

    List<String> outputRow = theInputRow.stream().skip(0).collect(Collectors.toList());
    
    logMessage="OutputRow before format =>" + outputRow.toString() +"<.";
    log(myName, Constants.VERBOSE, myArea, logMessage);
    
    for(int i=0; i < outputRow.size(); i++) {
        if(outputRow.get(i).startsWith(Constants.FITNESSE_PREFIX_ERROR)) continue;
        if(outputRow.get(i).startsWith(Constants.FITNESSE_PREFIX_FAIL)) continue;
        if(outputRow.get(i).startsWith(Constants.FITNESSE_PREFIX_IGNORE)) continue;
        if(outputRow.get(i).startsWith(Constants.FITNESSE_PREFIX_PASS)) continue;
        if(outputRow.get(i).startsWith(Constants.FITNESSE_PREFIX_REPORT)) continue;
        outputRow=formatRow(outputRow, i, formatToApply);
    }
    myArea="result";
    logMessage="OutputRow after format =>" + outputRow.toString() +"<.";
    log(myName, Constants.VERBOSE, myArea, logMessage);      
    
    return outputRow;
}

private List<String> formatRow(List<String> theInputRow, int colnr, String formatToApplyToColumn) {
    String myName="formatRow ls-i-s";
    String myArea="init";
    String logMessage=Constants.NOT_PROVIDED;

    List<String> outputRow = theInputRow.stream().skip(0).collect(Collectors.toList());
    
    if(colnr > outputRow.size() -1) {
        return outputRow;
    }

    logMessage="String before format =>" + outputRow.get(colnr) +"<.";
    log(myName, Constants.VERBOSE, myArea, logMessage);      

    outputRow.set(colnr, formatToApplyToColumn.concat(outputRow.get(colnr)));
    logMessage="String after format =>" + outputRow.get(colnr) +"<.";
    log(myName, Constants.VERBOSE, myArea, logMessage);      
    
    return outputRow;
}

private boolean isStepNrNumeric(List<String> row) {
    
    if (row.size() <= Constants.JOB_STEP_COLNR) {
        return false;
    }
    try {
        Integer.parseInt(row.get(Constants.JOB_STEP_COLNR));
    } catch(NumberFormatException e) {
        return false;
    } 

 return true;
 
}

private boolean isValidStepType(List<String> row) {
    boolean rc=false;

    if (row.size() <= Constants.JOB_STEPTYPE_COLNR) {
        return false;
    }

    if(Constants.JOB_STEPTYPES.contains(row.get(Constants.JOB_STEPTYPE_COLNR).toLowerCase().replaceAll(" ",""))) {
        rc=true;
    } else {
        rc=false;
    }
    
    return rc;
}

private boolean isImplementedStepType(List<String> row) {
    boolean rc=false;

    if (row.size() <= Constants.JOB_STEPTYPE_COLNR) {
        return false;
    }

    if(Constants.JOB_IMPLEMENTED_STEPTYPES.contains(row.get(Constants.JOB_STEPTYPE_COLNR).toLowerCase().replaceAll(" ", ""))) {
        rc=true;
    } else {
        rc=false;
    }
    
    return rc;
}

/*
 * 
 */
private List<String> getResultRow() {
    return resultRow;
}

private void setResultRow(List<String> outRow) {
    this.resultRow=outRow;
}
/**
 * 
 */
private List<List<String>> getReturnTable() {
    return returnTable;
}

    
private void addRowToReturnTable (List <String> row) {
    //Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
    getReturnTable().add(row);
} 

private void readParameterFile(){	 
    String myName="readParameterFile";
    String myArea="reading parameters";
    String logMessage = Constants.NOT_INITIALIZED;
    String result =Constants.NOT_FOUND;

    url = GetParameters.GetDatabaseURL(databaseConnDef);
    logMessage="database url >" + url +"<.";       log(myName, Constants.VERBOSE, myArea, logMessage);

    logUrl = GetParameters.GetLogUrl();
    logMessage = "logURL >" + logUrl +"<.";   log(myName, Constants.DEBUG, myArea, logMessage);

    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RESULT_FORMAT);
    if(Constants.NOT_FOUND.equals(result)) {
        setResultFormat(Constants.DEFAULT_RESULT_FORMAT);
    }
    else {
        setResultFormat(result);
    }
    log(myName, Constants.DEBUG, myArea, "resultFormat >" + getResultFormat() +"<.");

    //Script to use for RunIdqJob
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQJOB_SCRIPT);
    if(Constants.NOT_FOUND.equals(result)) {
        setJobScript(Constants.RUNIDQJOB_DEFAULT_SCRIPT);
    }
    else {
        setJobScript(result);
    }
    log(myName, Constants.DEBUG, myArea, "jobScript (if used) is >" + getJobScript() +"<.");

    //Script to use for RunIdqProfile
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQPROFILE_SCRIPT);
    if(Constants.NOT_FOUND.equals(result)) {
        setProfileScript(Constants.RUNIDQPROFILE_DEFAULT_SCRIPT);
    }
    else {
        setProfileScript(result);
    }
    log(myName, Constants.DEBUG, myArea, "profileScript (if used) is >" + getProfileScript() +"<.");

    //Script to use for RunIdqMapping
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQMAPPING_SCRIPT);
    if(Constants.NOT_FOUND.equals(result)) {
        setMappingScript(Constants.RUNIDQMAPPING_DEFAULT_SCRIPT);
    }
    else {
        setMappingScript(result);
    }
    log(myName, Constants.DEBUG, myArea, "mappingScript (if used) is >" + getMappingScript() +"<.");

    //Script to use for RunIdqDisObject
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQDISOBJECT_SCRIPT);
    if(Constants.NOT_FOUND.equals(result)) {
        setDisObjectScript(Constants.RUNIDQDISOBJECT_DEFAULT_SCRIPT);
    }
    else {
        setDisObjectScript(result);
    }
    log(myName, Constants.DEBUG, myArea, "DisObjectScript (if used) is >" + getDisObjectScript() +"<.");

    //Logical location, configured in directory.properties 
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_LOGICAL_SCRIPTDIR_IDQ);
    if(Constants.NOT_FOUND.equals(result)) {
        setLogicalScriptDirLocation(Constants.LOGICAL_SCRIPTDIR_IDQ);
    }
    else {
        setLogicalScriptDirLocation(result);
    }
    log(myName, Constants.DEBUG, myArea, "Logical IDQ Script Location (if used) is >" + getLogicalScriptDirLocation() +"<.");

    //sub directory in logical location, configured in directory.properties, in which IDQ scripts are located 
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_LOGICAL_LOCATION_IDQSUBDIR);
    if(Constants.NOT_FOUND.equals(result)) {
        setLogicalLocationIdqSubDir(Constants.LOGICAL_LOCATION_IDQSUBDIR);
    }
    else {
        setLogicalLocationIdqSubDir(result);
    }
    log(myName, Constants.DEBUG, myArea, "Logical Location sub directory for idq scripts (if used) is >" + getLogicalLocationIdqSubDir() +"<.");

    //
    //Job directory where to create the job file
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getApplicationName(), this.className, Constants.PARAM_JOBDIR);
    if(Constants.NOT_FOUND.equals(result)) {
        setJobDir(Constants.DEFAULT_JOBDIR);
    }
    else {
        setJobDir(result);
    }
    log(myName, Constants.DEBUG, myArea, "Job directory >" + getJobDir() +"<.");
    
    //Result Directory
    result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, getApplicationName(), this.className, Constants.PARAM_RESULTDIR);
    if(Constants.NOT_FOUND.equals(result)) {
        setResultDir(Constants.DEFAULT_RESULTDIR);
    }
    else {
        setResultDir(result);
    }
    log(myName, Constants.DEBUG, myArea, "Result directory >" + getResultDir() +"<.");


    log(myName, Constants.DEBUG, "end", "End of readParameterFile");
    
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
     * @return log file name, including .log extension
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
     * @return Log level as String: FATAL,ERROR,WARNING,INFO,DEBUG,VERBOSE
     */
    public String getLogLevel() {
       return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return Log level as integer
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }
    
    /* 
     * @since 20151121.0
     *
    */
    public static String getVersion() {
        return version;
    }
    
    public String getResult() {
        return result;
    }
    private void setResult(String result) {
        this.result =result;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    private void setErrorMessage(String error) {
        this.errorMessage=error;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    private void setError(String errCode, String errMsg) {
        setResult(errCode);
        setErrorMessage(errMsg);
    }

private void writeToJobFile(List<String> row) {
    String myName ="writeToJobFile";
    String myArea ="init";
    String logMessage = Constants.NOT_INITIALIZED;
    String parsedRow = Constants.NOT_INITIALIZED;

    for (int i = 0; i < row.size(); ++i){
        if (i==0) {
            parsedRow=Constants.JOB_COLUMN_SEPARATOR + row.get(i);
            } else {
                parsedRow=parsedRow + Constants.JOB_COLUMN_SEPARATOR + row.get(i);
            }
        }
    parsedRow = parsedRow+Constants.JOB_COLUMN_SEPARATOR;
    
    String jobFile = getJobDir() + getApplicationName() + "-" + getJobName() + ".job";
        
    try {
        FileWriter jobWriter = new FileWriter( jobFile, appendToFile);
        if(!appendToFile) appendToFile=true;
        BufferedWriter out = new BufferedWriter(jobWriter);
        out.write(parsedRow);
        out.newLine();
        out.close();
        }
        catch (Exception e) {
            logMessage="Error writing to job file >" + jobFile + "<. Error: " + e.toString();
            log(myName, Constants.DEBUG, myArea, logMessage);      
        }
}

    public void setJobPersistence(String jobPersistence) {
        this.jobPersistence = jobPersistence;
    }

    public String getJobPersistence() {
        return jobPersistence;
    }

    public void setJobMaxConcurrency(String jobMaxConcurrency) {
        this.jobMaxConcurrency = jobMaxConcurrency;
    }

    public String getJobMaxConcurrency() {
        return jobMaxConcurrency;
    }

    public void setJobIDQConfig(String jobIDQConfig) {
        this.jobIDQConfig = jobIDQConfig;
    }

    public String getJobIDQConfig() {
        return jobIDQConfig;
    }

    public void setJobPWCConfig(String jobPWCConfig) {
        this.jobPWCConfig = jobPWCConfig;
    }

    public String getJobPWCConfig() {
        return jobPWCConfig;
    }

    public void setJobOracleConfig(String jobOracleConfig) {
        this.jobOracleConfig = jobOracleConfig;
    }

    public String getJobOracleConfig() {
        return jobOracleConfig;
    }

    public void setJobDB2Config(String jobDB2Config) {
        this.jobDB2Config = jobDB2Config;
    }

    public String getJobDB2Config() {
        return jobDB2Config;
    }

    public void setJobDACConfig(String jobDACConfig) {
        this.jobDACConfig = jobDACConfig;
    }

    public String getJobDACConfig() {
        return jobDACConfig;
    }

    public void setJobScriptConfig(String jobScriptConfig) {
        this.jobScriptConfig = jobScriptConfig;
    }

    public String getJobScriptConfig() {
        return jobScriptConfig;
    }

    public void setStepProjectName(String stepProjectName) {
        this.stepProjectName = stepProjectName;
    }

    public String getStepProjectName() {
        return stepProjectName;
    }

    public void setStepApplicationName(String stepApplicationName) {
        this.stepApplicationName = stepApplicationName;
    }

    public String getStepApplicationName() {
        return stepApplicationName;
    }

    public void setStepObjectName(String stepObjectName) {
        this.stepObjectName = stepObjectName;
    }

    public String getStepObjectName() {
        return stepObjectName;
    }

    public void setStepFolderName(String stepFolderName) {
        this.stepFolderName = stepFolderName;
    }

    public String getStepFolderName() {
        return stepFolderName;
    }

    public void setStepMappingName(String stepMappingName) {
        this.stepMappingName = stepMappingName;
    }

    public String getStepMappingName() {
        return stepMappingName;
    }

    public void setStepExpectedResult(String stepExpectedResult) {
        this.stepExpectedResult = stepExpectedResult;
    }

    public String getStepExpectedResult() {
        return stepExpectedResult;
    }

    public void setStepAbortOnError(String stepAbortOnError) {
        this.stepAbortOnError = stepAbortOnError;
    }

    public String getStepAbortOnError() {
        return stepAbortOnError;
    }

    public void setStepOnErrorAction(String stepAbortOnError) {
        this.stepOnErrorAction = stepAbortOnError;
    }

    public String getStepOnErrorAction() {
        return stepOnErrorAction;
    }

    public void setStepWait(String stepWait) {
        this.stepWait = stepWait;
    }

    public String getStepWait() {
        return stepWait;
    }

    public void setStepJobMustWaitOnStepCompletion(String stepJobMustWaitOnStepCompletion) {
        this.stepJobMustWaitOnStepCompletion = stepJobMustWaitOnStepCompletion;
    }

    public String getStepJobMustWaitOnStepCompletion() {
        return stepJobMustWaitOnStepCompletion;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepIdqObjectType(String stepIdqObjectType) {
        this.stepIdqObjectType = stepIdqObjectType;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public String getStepIdqObjectType() {
        return stepIdqObjectType;
    }

    public void setJobScript(String jobScript) {
        this.jobScript = jobScript;
    }

    public String getJobScript() {
        return jobScript;
    }

    public void setProfileScript(String profileScript) {
        this.profileScript = profileScript;
    }

    public String getProfileScript() {
        return profileScript;
    }

    public void setMappingScript(String mappingScript) {
        this.mappingScript = mappingScript;
    }

    public String getMappingScript() {
        return mappingScript;
    }

    public void setDisObjectScript(String disobjectScript) {
        this.disObjectScript = disobjectScript;
    }

    public String getDisObjectScript() {
        return disObjectScript;
    }

    public void setLogicalScriptDirLocation(String logicalScriptDirLocation) {
        this.logicalScriptDirLocation = logicalScriptDirLocation;
    }

    public String getLogicalScriptDirLocation() {
        return logicalScriptDirLocation;
    }

    public void setJobDir(String jobDir) {
        this.jobDir = jobDir;
    }

    public String getJobDir() {
        return jobDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getResultDir() {
        return resultDir;
    }

    public void setLogicalLocationIdqSubDir(String logicalLocationIdqSubDir) {
        this.logicalLocationIdqSubDir = logicalLocationIdqSubDir;
    }

    public String getLogicalLocationIdqSubDir() {
        return logicalLocationIdqSubDir;
    }

    private void setOnlyGenerate(String yesNo) {
        this.onlyGenerate =yesNo;
    }
    
    private String getOnlyGenerate() {
        return onlyGenerate;
    }
}
