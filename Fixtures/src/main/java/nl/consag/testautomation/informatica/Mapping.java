/**
 * This purpose of this fixture is to run Informatica DQ Mappings
 * @author Jac Beekers
 * @since 12 July 2016
 * @version 20160712.0 - initial version
 * @version 20170107.0 - RunScript introduced StopTest
 * @version 20170225.0 - Scripts can be set in fixture.properties file
 */
package nl.consag.testautomation.informatica;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.GetDatabaseTable;

//import static supporting.ListUtility.list;
import nl.consag.testautomation.supporting.Logging;

import nl.consag.testautomation.scripts.ExecuteScript;

import nl.consag.testautomation.scripts.ExecuteScript.ExecuteScriptStopTest;

import org.apache.commons.lang3.StringUtils;

public class Mapping {
    private static final String version = "20170510.0"; // removed incorrect references

    private String className = "Mapping";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private int logLevel =3;
    private int logEntries =0;
    private String abortYesNo = Constants.YES;
    private String errorCode = Constants.OK;
    private String errorMessage = Constants.OK;
    
    private int locRefreshProfile =-1;
    private String refreshProfileResult = Constants.OK;
    private String profileListResult = Constants.OK;
    private String numberOfProfilesResult = Constants.OK;
    private List<List<String>> profileList=new ArrayList<List<String>>();

    // script names
    private String profileScript =Constants.RUNIDQPROFILE_DEFAULT_SCRIPT;
    private String mappingScript =Constants.RUNIDQMAPPING_DEFAULT_SCRIPT;

    //Queries taken from Informatica Support DocId 322200
    private String sqlMRProfiles ="SELECT /* FitNesse Fixture Profile (c) Consag Consultancy Services B.V. */ " +
        "A.POP_NAME AS PROFILE_NAME,\n" + 
    "  P.PROJECT_NAME||T.OBJECT_PATH AS FOLDER_PATH,\n" + 
    "  P.PROJECT_NAME  AS PROJECT_NAME,\n" + 
    "  'Profile' AS OBJECT_TYPE\n" + 
    "FROM MRX_PROJECTS P,\n" + 
    "  MRX_PARENT_OBJECTS T,\n" + 
    "  PO_PROFILEDEFINITION A\n" + 
    "WHERE T.CHILD_TYPE LIKE 'com.informatica.metadata.profile.impl.ProfileDefinitionImpl'\n" + 
    "AND P.PROJECT_NAME IS NOT NULL\n" + 
    "AND T.CHILD_TYPE   IS NOT NULL\n" + 
    "AND T.ROOT_CID      = P.FOLDER_CID\n" + 
    "AND A.PSP_OPID      = T.CHILD_PID";
    public static final List<String> sqlColumnsMRProfiles = Collections.unmodifiableList(Arrays.asList(
        "Profile Name", "Folder Path","Project Name","Object Type"));

    private String databaseName = Constants.NOT_INITIALIZED;
    private String project = Constants.NOT_PROVIDED;
    private String folderName = Constants.ALL;
    private String mappingName = Constants.ALL;
    private String applicationName = Constants.NOT_PROVIDED;

    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;

    private boolean userSetAbortOnError = false;

    public Mapping() {
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
    public Mapping(String databaseName) {
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
    public Mapping(String databaseName, String project) {
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
    public Mapping(String databaseName, String project, String loglevel) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);

    this.databaseName=databaseName;
    this.project=project;
        logLevel = Constants.logLevel.indexOf(loglevel.toUpperCase());
        logFileName = startDate + "." + className +"." +this.databaseName +"." +this.project;

    }


    /*
     * Will provide the profile list itself.
     */
    public String getProfileList(String expectedVal) {
        
        if(getProfileList().equals(Constants.OK)) {
            return profileList.toString();
        } else {
            return Constants.ERROR;
        }
    }
    
    /*
     * Will provide wether or not getting the list was okay or not.
     */
    public String getProfileList() {
        String myName="getProfileList";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
        String sqlMR = Constants.NOT_INITIALIZED;
        
        GetDatabaseTable profQuery = new GetDatabaseTable(className +"-" + myName);
        profQuery.setLogLevel(getLogLevel());
        profQuery.setDatabaseName(getDatabaseName());
        myArea="calling GetDatabaseTable";
        
        if(getProjectName().equalsIgnoreCase(Constants.ALL)) {
            sqlMR=sqlMRProfiles;
            }
        else {
            //Add filter on project
            sqlMR=sqlMRProfiles +" AND p.project_name = '" + getProjectName() +"'";
        }
        
        if(!Constants.ALL.equalsIgnoreCase(getFolderName())) {
            int nrSlashesInArg= StringUtils.countMatches(getFolderName(),Constants.IDQ_PATH_SEPARATOR);
            logMessage="Occurrences of >" + Constants.IDQ_PATH_SEPARATOR + "< in >" + getFolderName() +"< is >" + Integer.toString(nrSlashesInArg) +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            // Users can specify wildcard % themselves
            sqlMR =sqlMR.concat("\n AND t.object_path LIKE '" + Constants.IDQ_PATH_SEPARATOR + getFolderName() 
                                + Constants.IDQ_PATH_SEPARATOR + "%'")
                +("\n AND instr(t.object_path,'" + Constants.IDQ_PATH_SEPARATOR + "',1,"+ Integer.toString(3+nrSlashesInArg) +") =0");
        }

        if(!Constants.ALL.equalsIgnoreCase(getMappingName())) {
            // Users can specify wildcard % themselves
            sqlMR =sqlMR.concat(" AND t.child_type LIKE '" + getMappingName() + "'");
        }

        profileList=profQuery.getQueryResult(sqlMR, sqlColumnsMRProfiles.size());
        myArea="Processing GetQueryResult";
        logMessage="Result from getQueryResult =>" + profileList.toString() +"<.";
        log(myName, Constants.VERBOSE, myArea, logMessage);
        setError(profQuery.getErrorCode(),profQuery.getErrorMessage());
        setProfileListResult(getErrorCode());
        
        return getProfileListResult();
    }

    public String numberOfProfiles() {
        String myName="numberOfProfiles";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
        
        String nr = Constants.ERROR;
        
        if(getProfileListResult().equals(Constants.OK)) {
            nr= Integer.toString(profileList.size());
            logMessage="Number of profiles =>" + nr +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
        }
        
        setNumberOfProfilesResult(nr);
        return nr;
    }

    /**
     * @return
     */
    public List<List<List<String>>> query() throws MappingStopTest {
    String myName="query";
    String myArea="init";
    String logMessage = Constants.NOT_INITIALIZED;
    List<List<String>> profiles=new ArrayList<List<String>>();
    String sqlMR = Constants.NOT_INITIALIZED;
    
    myArea="get object GetDatabaseTable";
    GetDatabaseTable profQuery = new GetDatabaseTable(className +"-" + myName);
    profQuery.setLogLevel(getLogLevel());
    profQuery.setDatabaseName(getDatabaseName());
    myArea="calling GetDatabaseTable";
    
    if(getProjectName().equalsIgnoreCase(Constants.ALL)) {
        sqlMR=sqlMRProfiles;
        }
    else {
        //Add filter on project
        sqlMR=sqlMRProfiles +" AND p.project_name = '" + getProjectName() +"'";
    }

    if(!Constants.ALL.equalsIgnoreCase(getFolderName())) {
        int nrSlashesInArg= StringUtils.countMatches(getFolderName(),Constants.IDQ_PATH_SEPARATOR);
        logMessage="Occurrences of >" + Constants.IDQ_PATH_SEPARATOR + "< in >" + getFolderName() +"< is >" + Integer.toString(nrSlashesInArg) +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        // Users can specify wildcard % themselves
        sqlMR =sqlMR.concat("\n AND t.object_path LIKE '" + Constants.IDQ_PATH_SEPARATOR + getFolderName() 
                            + Constants.IDQ_PATH_SEPARATOR + "%'")
            +("\n AND instr(t.object_path,'" + Constants.IDQ_PATH_SEPARATOR + "',1,"+ Integer.toString(3+nrSlashesInArg) +") =0");
    }

    if(!Constants.ALL.equalsIgnoreCase(getMappingName())) {
        // Users can specify wildcard % themselves
        sqlMR =sqlMR.concat(" AND t.child_type LIKE '" + getMappingName() + "'");
    }
    
    sqlMR = sqlMR.concat(" ORDER BY t.object_path");

    profiles=profQuery.getQueryResult(sqlMR, sqlColumnsMRProfiles.size());
    myArea="Processing GetQueryResult";
    logMessage="Result from getQueryResult =>" + profiles.toString() +"<.";
    log(myName, Constants.VERBOSE, myArea, logMessage);
    setError(profQuery.getErrorCode(),profQuery.getErrorMessage());
        
    return createResultSet(profiles,sqlColumnsMRProfiles);
    
    }

    /*
     * Need to match query result (rows with column values) to columns as expected in FitNesse test page
     * Each column value must be preceded with the column name. For each record.
     */
    private List<List<List<String>>> createResultSet(List<List<String>> queryResult, List<String> colNames) throws MappingStopTest {
        String myName="createResultSet";
        String myArea="init";
        String logMessage=Constants.NOT_INITIALIZED;
        
        List<List<List<String>>> out = new ArrayList<List<List<String>>>();
        
        for(List<String> row : queryResult) {
            List<List<String>> rowColValPairs = new ArrayList<List<String>>();
            String currentProfilePathName = Constants.NOT_FOUND;
            for(int i=0 ; i< row.size() ; i++) {  
                List<String> colAndVal = new ArrayList<String>();
                colAndVal.add(colNames.get(i));
                colAndVal.add(row.get(i));
                if(colNames.get(i).equals("Folder Path")) {
                    currentProfilePathName=row.get(i);
                }
                rowColValPairs.add(colAndVal);
            }
            if(getLocRefreshProfile() > -1 ) {
                List<String> colAndVal = new ArrayList<String>();
                colAndVal.add(Constants.REFRESH_PROFILE);
                
                ExecuteScript rs = new ExecuteScript(getProfileScript(),className + "-" + myName);
                rs.setLogLevel(getLogLevel());
                rs.setScriptLocation("scripts idq");
                rs.addParameter(currentProfilePathName);
                String rc;
                try {
                    rc = rs.runScriptReturnCode();
                    colAndVal.add(rc);
                } catch (ExecuteScriptStopTest e) {
                    myArea="Processing script returncode";
                    logMessage="Result from RunScript =>" + e.toString() +"<.";
                    log(myName, Constants.VERBOSE, myArea, logMessage);
                    setError(rs.getErrorCode(),rs.getErrorMessage());

                    if (Constants.YES.equals(getAbortOnError())) {
                        throw new MappingStopTest(getErrorMessage());
                    }

                }
                rowColValPairs.add(colAndVal);
            }
            out.add(rowColValPairs);
            
        }
        
        return out;
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
    
    public void databaseName(String db) {
         setDatabaseName(db);
    }
    public void setDatabaseName(String db) {
        this.databaseName =db;
    }
    public String getDatabaseName() {
        return this.databaseName;
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

    public void table(List<List<String>> fitnesseTable ) {
        setLocRefreshProfile(-1);
        for(int i=0 ; i <fitnesseTable.get(0).size() ; i++)
        if(fitnesseTable.get(0).get(i).equals(Constants.REFRESH_PROFILE)) {
            setLocRefreshProfile(i);
        }
    }

    private void setLocRefreshProfile(int i) {
        this.locRefreshProfile=i;
    }
    private int getLocRefreshProfile() {
        return this.locRefreshProfile;
    }

    public String getRefreshProfileResult() {
        return refreshProfileResult;
    }
    private void setRefreshProfileResult(String result) {
        this.refreshProfileResult =result;
    }
    
    public String refreshProfilesInList(String onError) throws MappingStopTest {
        String myName="refreshProfilesInList";
        String myArea="init";
        String rc = Constants.OK;
        String logMessage = Constants.NOT_INITIALIZED;
        boolean runProfiles=true;
        
        if(onError.equalsIgnoreCase("not on error")) {
            if(getProfileListResult().equals(Constants.OK) && ! getNumberOfProfilesResult().equals(Constants.ERROR)) {
                runProfiles=true;
            } else {
                runProfiles=false;
            }
        } else {
            runProfiles =true;
        }
        
        if(runProfiles) {
            for(int i=0 ; i < profileList.size() ; i++) {
                //the object path is the 2nd entry
                //the profile name is the first entry
                String profilePath=profileList.get(i).get(1);
                String rcSub= runMapping(profilePath, profileList.get(i).get(0));
                if(rcSub.equals(Constants.OK)) {
                   logMessage="Profile >"+profilePath+"< completed successfully. Script return code =>" +rcSub+"<.";
                   log(myName, Constants.OK,myArea,logMessage);
                } else {
                   logMessage="Profile >"+profilePath+"< failed. Script return code =>" +rcSub+"<. Error: " + getErrorMessage();
                   log(myName, Constants.ERROR,myArea,logMessage);
                   rc = Constants.ERROR;
                   setError(rc, logMessage);
                }
            }
        } else {
            rc = Constants.OK;
        }
        return rc;
    }

    private void setProfileListResult(String result) {
        this.profileListResult =result;
    }
    public String getProfileListResult() {
        
        return profileListResult;
    }

    private void setNumberOfProfilesResult(String result) {
        this.numberOfProfilesResult = result;
    }
    public String getNumberOfProfilesResult() {
        return numberOfProfilesResult;
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

    public void setMappingName(String mappingName) {
        this.mappingName =mappingName;
    }
    public String getMappingName() {
        return this.mappingName;
    }

    public void setApplicationName(String appName) {
        this.applicationName =appName;
    }
    public String getApplicationName() {
        return this.applicationName;
    }

    public String runMapping() throws MappingStopTest {
        return runMapping(getObjectWithPath(), getMappingName());
    }
    
    private String runMapping(String objectPath, String mappingName) throws MappingStopTest {
        String myName="runMapping";
        String myArea ="init";
        readParameterFile();
                                 
        String logMessage="Run mapping >" + mappingName +"< with path >" + objectPath +"<.";
            log(myName, Constants.DEBUG,myArea,logMessage);

        ExecuteScript rs = new ExecuteScript(getMappingScript(),className + "-" + myName + "-" + mappingName);
        rs.setLogLevel(getLogLevel());
        rs.setScriptLocation("scripts idq");
        rs.setCaptureOutput(Constants.YES);
        rs.setCaptureErrors(Constants.YES);
        
        rs.addParameter(getApplicationName());
        rs.addParameter(getMappingName());
        try {
            rs.runScriptReturnCode();
        } catch (ExecuteScriptStopTest e) {
            if (Constants.YES.equals(getAbortOnError())) {
                throw new MappingStopTest(getErrorMessage());
            }

        }

        String outCode =parseScriptOutput(rs.getCapturedOutput());
        String errCode =parseScriptOutput(rs.getCapturedErrors());
        String rsCode=rs.getErrorCode();
        
        if (Constants.OK.equals(outCode)) {
            if(Constants.OK.equals(errCode))
            return rsCode;
            else {
                setError(errCode,rs.getErrorMessage());
                return errCode;
            }
        }
        return outCode;
        
    }

    /**
     * @param abortYesNo
     * @throws RunScriptStopTest
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

    private String getObjectWithPath() {
        
        return getProjectName() + Constants.IDQ_PATH_SEPARATOR + getFolderName() + Constants.IDQ_PATH_SEPARATOR
            + getMappingName();
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

        //Script to use for RunIdqProfile
        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQPROFILE_SCRIPT);
        if(Constants.NOT_FOUND.equals(result)) {
            setProfileScript(Constants.RUNIDQPROFILE_DEFAULT_SCRIPT);
        }
        else {
            setProfileScript(result);
        }
        log(myName, Constants.DEBUG, myArea, "profileScript (if used) will be >" + getProfileScript() +"<.");

        //Script to use for RunIdqMapping
        result =GetParameters.getPropertyVal(Constants.FIXTURE_PROPERTIES, Constants.PARAM_RUNIDQMAPPING_SCRIPT);
        if(Constants.NOT_FOUND.equals(result)) {
            setMappingScript(Constants.RUNIDQMAPPING_DEFAULT_SCRIPT);
        }
        else {
            setMappingScript(result);
        }
        log(myName, Constants.DEBUG, myArea, "mappingScript (if used) will be >" + getMappingScript() +"<.");

    }

    /**
     * @return fixture version info
     */
    public static String getVersion() {
        return version;
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


    static class MappingStopTest extends Exception {
         @SuppressWarnings("compatibility:-5661641983890468252")
         static final long serialVersionUID = 34985952947L;
            public MappingStopTest(){
            }

         /**
          * @param msg
          */
         public MappingStopTest(String msg){
           super(msg);
       }

     }

}
