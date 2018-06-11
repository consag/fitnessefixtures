/**
 * This purpose of this fixture is to store all the generic constants centrally.
 * @author Edward Crain, Jac. Beekers
 * @version 21 November 2015
 * @version 20170908.0 - new parameter for LoadDataFromExcel
 */
package nl.consag.testautomation.supporting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {

    public static final String FITNESSE_PREFIX_REPORT="report:";    // typically no color
    public static final String FITNESSE_PREFIX_PASS="pass:";        // typically green
    public static final String FITNESSE_PREFIX_FAIL="fail:";        // typically red
    public static final String FITNESSE_PREFIX_IGNORE="ignore:";    // typically blue
    public static final String FITNESSE_PREFIX_ERROR="error:";      // typically yellow
    
    public static final String SF_CONNECTIONCACHEFILE="sf_access.json";
    public static final String DEFAULT_CLASS_PREFIX="nl.consag.testautomation";
    public static final String YES = "Yes";
    public static final String Y = "Y";
    public static final String NO = "No";
    public static final String N = "N";
    public static final String STOP = "STOP";
    public static final String OK = "OK";
    public static final String TRUE = "true";
    public static final String FALSE ="false";
    public static final String FAILED = "Failed";
    
    public static final String NOT_FOUND = "NOTFOUND";
    public static final String DEFAULT_PROPVALUE ="Default#Prop#Value";
    public static final String NOT_PROVIDED = "NotProvided";
    public static final String DEFAULT = "default";
    public static final String UNKNOWN = "unknown";
    public static final String NULL_VALUE ="#NULL#"; // in Excel to indicate column should be set NULL

    public static final String ERRCODE_NOT_IMPLEMENTED="CNSG-FIT-0001";

    //Database errors or unexpected results
    public static final String ERRCODE_DBCONNECTION ="CNSG-DB-0001";
    public static final String ERRCODE_DBMETADATA  ="CNSG-DB-0002";
    public static final String ERRCODE_DBMETADATA_COLUMNS  ="CNSG-DB-0003";
    public static final String ERRCODE_TABLENOTFOUND ="CNSG-DB-0004";
    public static final String ERRCODE_UNSUPPORTED_DATATYPE ="CNSG-DB-0005";
    public static final String ERRCODE_SQL_IDAASQLERROR ="CNSG-DB-0006";
    public static final String ERRCODE_SQL_IDAAGETRESULT ="CNSG-DB-0007";

    public static final String ERRCODE_DECRYPT ="CNSG-SEC-0001";

    public static final String ERRCODE_PROPERTIES ="CNSG-PROP-0001";
    public static final String ERRCODE_PROPFILE ="CNSG-PROP-0002";
    public static final String ERRCODE_PROPFILE_NOTFOUND ="CNSG-PROP-0003";
    public static final String ERRCODE_PROPFILE_IOERROR ="CNSG-PROP-0004";
    public static final String ERRCODE_PROP_NOT_FOUND ="CNSG-PROP-0005";
    public static final List<String> propFileErrors = Collections.unmodifiableList(Arrays.asList(
            ERRCODE_PROPERTIES
            ,ERRCODE_PROPFILE
            ,ERRCODE_PROPFILE_NOTFOUND
            ,ERRCODE_PROPFILE_IOERROR
            ,ERRCODE_PROP_NOT_FOUND
    ));

    //Column types as reported by Oracle - Might need special handling
    public static final String COLUMN_DATATYPE_VARCHAR="VARCHAR";
    public static final String COLUMN_DATATYPE_VARCHAR2="VARCHAR2";
    public static final String COLUMN_DATATYPE_CHAR="CHAR";
    public static final String COLUMN_DATATYPE_NUMBER="NUMBER";
    public static final String COLUMN_DATATYPE_DECIMAL="DECIMAL";
    public static final String COLUMN_DATATYPE_TIMESTAMP ="TIMESTAMP";
    public static final String COLUMN_DATATYPE_TIMESTAMP6="TIMESTAMP(6)";
    public static final String COLUMN_DATATYPE_TIMESTAMP9="TIMESTAMP(9)";
    public static final String COLUMN_DATATYPE_DATE="DATE";
    public static final String UNSUPPORTED_DATATYPE="Unsupported Data Type";

    public static final String NOT_IMPLEMENTED = "Not yet implemented.";
    public static final String NOT_INITIALIZED = "Not initialized";
    public static final String NONE = "None";
    public static final String LATEST = "LATEST";
    public static final String FITCONVERTDT = "FITCONVERTDT";
    public static final String SRC = "SRC";
    public static final String TGT ="TGT";
    public static final String NOPRIMKEY ="NoPrimaryKey";
    public static final String NOT_COMPARED ="Not compared";
    public static final String NO_DIFFERENCES ="No differences";
    public static final String FILTER_INDICATOR="filter";
    public static final String OPERATOR_EQUALS ="=";
    public static final String PARAM_DELIMITER ="Delimiter=";
    
    public static final String INCOMING ="incoming";
    public static final String OUTGOING ="outgoing";
    public static final String TESTDATA ="testdata";
    public static final String DEPLOYMENT="deployment";
    public static final String TEMP ="temp";
    public static final String ENVIRONMENT ="Environment";
    public static final String PARAM_LOG_DIR ="LogDir";
    public static final String PARAM_LOG_URL ="LogURL";

    public static final String LOG_DIR = "log/";
    // The log url points to a web server (e.g. Jetty) that is configured to list FitNesse log files from <installdir>/log
    public static final String LOG_URL = "http://localhost:8080/fitnesselogs/";

    //must exist in directory.properties
    public static final String LOGICAL_BASE_DIR = "base";
    public static final String LOGICAL_SCRIPT_DIR ="scripts";
    
    public static final String LOGICAL_LOCATION_IDQSUBDIR ="idq";
    public static final String PARAM_LOGICAL_LOCATION_IDQSUBDIR ="logicalLocationIdqSubDir";
        
    public static final String LOGICAL_SCRIPTDIR_IDQ ="idqscripts";
    public static final String PARAM_LOGICAL_SCRIPTDIR_IDQ="logicalLocationIdqScripts";
    
    public static final String TABLE_PREFIX = "CNSG_";
    public static final String DEFAULT_CHECK_TABLE="CHCK";
    public static final String CHECK_TABLE_COLUMNS_ORA=
        " CHECK_DATE varchar2(30 CHAR)" +
        ",TABLE_NAME varchar2(30 CHAR)" +
        ",COLUMN_NAME varchar2(30 CHAR)" +
        ",CHECK1 varchar2(255 CHAR)" +
        ",CHECK2 varchar2(255 CHAR)" +
        ",CHECK3 varchar2(255 CHAR)" +
        ",CHECK4 varchar2(255 CHAR)" +
        ",CHECK5 varchar2(255 CHAR)";
    public static final String TABLE_COMMENT ="Generated by FitNesse CreateTable fixture, Open Source by consag at https://fitnesse.solutions";
    public static final String NO_ERRORS ="No errors";

    // Delimiters
    public static final String LOG_FILE_DELIMITER = ";";
    public static final String LOG_SEPARATOR = " - ";
    public static final String INPUT_FILE_DELIMITER = ":";
    public static final String QUERY_DELIMITER = ",";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String COLUMN_DELIMITER =",";
    public static final String FIELD_DELIMITER =";";
    public static final String APPPROP_DELIMITER =".";
    public static final String IDQ_PATH_SEPARATOR ="/";
    public static final String FITNESSE_DELIMITER ="|";
    public static final String PROPERTY_DELIMITER ="=";
    public static final String DATABASE_PROPERTIES_DELIMITER =":";
    public static final String DATABASE_OBJECT_DELIMITER =".";
    //TODO: Depend object delimiter on database platform (not all databases may use . as separator between schema and object

    public static final String IDAA_STATS_COLLECTION_DBPROP ="collectionDB";
    public static final String IDAA_STATS_COLLECTION_DB ="DBAJ";
    public static final String IDAA_STATS_COLLECTION_IDAA = "IDAABX02";
    public static final String IDAA_STATS_COLLECTION_SCHEMA = "SIEBIDQ";
    public static final String IDAA_STATS_COLLECTION_TABLE_ACTIVE = "IDAA_QUERY_ACTIVE";
    public static final String IDAA_STATS_COLLECTION_TABLE_HIST = "IDAA_QUERY_HIST";
    //IDAA-Only tables
    public static final String IDAA_STATS_COLLECTION_TABLE_ACTIVE_AOT = "IDAA_QUERY_ACTIVE_AOT";
    public static final String IDAA_STATS_COLLECTION_TABLE_HIST_AOT = "IDAA_QUERY_HIST_AOT";

    public static final int IDAA_STATS_RETENTION_IN_DAYS =30;


    // Location of config files
    public static final String CONFIGDIRECTORY="config/";
    public static final String DEFAULT_KEYSTORE = "siteKey";
    // Logical to physical directory mapping
    public static final String DIRECTORY_PROPERTIES = CONFIGDIRECTORY + "directory.properties";
        // Siebel connectivity
        public static final String ZOS_PROPERTIES = CONFIGDIRECTORY + "zos.properties";
    public static final String FILEOPERATION_PROPERTIES =CONFIGDIRECTORY + "fileoperation.properties";
    public static final String APPLICATION_PROPERTIES =CONFIGDIRECTORY + "application.properties";
    public static final String CONNECTION_PROPERTIES = "connection.properties"; // Will replace database.properties. Contains encrypted passwords.
    public static final String DATABASE_PROPERTIES =CONFIGDIRECTORY + "database.properties"; // LEGACY
    public static final String JDBC_PROPERTIES =CONFIGDIRECTORY + "jdbc.properties";
    public static final String POWERCENTER_PROPERTIES =CONFIGDIRECTORY + "powercenter.properties";
    public static final String APPWSH_PROPERTIES =CONFIGDIRECTORY + "appwsh.properties";
    public static final String WSH_PROPERTIES =CONFIGDIRECTORY + "wsh.properties";
    public static final String DAC_PROPERTIES =CONFIGDIRECTORY + "dac.properties";
    public static final String ENVIRONMENT_PROPERTIES =CONFIGDIRECTORY + "environment.properties";
    public static final String INFA_PROCESS_PROPERTIES =CONFIGDIRECTORY + "infaprocess.properties";
    public static final String FIXTURE_PROPERTIES =CONFIGDIRECTORY + "fixture.properties";

    /**
     * Static values for indexes in Database parameter files
     */
    public static final int INDEX_DATABASE_TYPE=1;
    public static final int INDEX_DATABASE_CONNECTION=2;
    public static final int INDEX_DATABASE_USERNAME=3;
    public static final int INDEX_DATABASE_USER_PASSWORD=4;
    public static final int INDEX_DATABASE_TABLE_OWNER=5;
    public static final int INDEX_DATABASE_TABLE_OWNER_PASSWORD=6;

    /**
     * Static values for indexes in PowerCenter parameter files
     */
    public static final int INDEX_INFA_DOMAINNAME=1;
    public static final int INDEX_INFA_REPOSITORYSERVICE=2;
    public static final int INDEX_INFA_INTEGRATIONSERVICE=3;
    public static final int INDEX_INFA_USERNAME=4;
    public static final int INDEX_INFA_USER_PASSWORD=5;

    /**
     * Static values for indexes in Application WebServiceHub parameter files
     */
    public static final int INDEX_APPWSH_WSHNAME=1;
    
    /**
     * Static values for indexes in file operations parameter files
     */
    public static final int INDEX_FILEOPERATIONS_DIRECTORY=1;

    // Log levels
    public static final String VERBOSE = "VERBOSE";
    public static final String DEBUG ="DEBUG";
    public static final String INFO ="INFO";
    public static final String WARNING ="WARNING";
    public static final String ERROR = "ERROR";
    public static final String FATAL ="FATAL";
    public static final List<String> logLevel = Collections.unmodifiableList(Arrays.asList(FATAL,ERROR,WARNING,INFO,DEBUG,VERBOSE));
    
    public static final int LOGLEVEL_FATAL=0; 
    public static final int LOGLEVEL_ERROR=1; 
    public static final int LOGLEVEL_WARNING=2; 
    public static final int LOGLEVEL_INFO=3; 
    public static final int LOGLEVEL_DEBUG=4; 
    public static final int LOGLEVEL_VERBOSE=5; 
    
    // Properties file management
    public static final String PROPMGMT_ACTION_ADD="add";
    public static final String PROPMGMT_ACTION_REMOVE="remove";
    public static final List<String> PROPMGMT_ACTIONS = Collections.unmodifiableList(Arrays.asList(
        PROPMGMT_ACTION_ADD
        ,PROPMGMT_ACTION_REMOVE
        ));

    public static final String PROPMGMT_ACTIONERROR_IGNORE="ignore it";
    public static final String PROPMGMT_ACTIONERROR_CREATE="create it";
    public static final String PROPMGMT_ACTIONERROR_REPORT="report error";
    public static final List<String> PROPMGMT_ACTIONERRORS = Collections.unmodifiableList(Arrays.asList(
        PROPMGMT_ACTIONERROR_IGNORE
        ,PROPMGMT_ACTIONERROR_CREATE
        ,PROPMGMT_ACTIONERROR_REPORT));
    
    // default timestamp format used by Excel
    public static final String DEFAULT_TIMESTAMP_FORMAT ="yyyy-MM-dd HH:mm:ss";

    // Scheduler constants
    public static final String SCHEDULER_PROPERTIES = CONFIGDIRECTORY + "scheduler_client.properties";
    public static final int MISFIRE_INSTRUCTION_FIRE_NOW = 1;
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2;
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3;
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4;
    public static final int MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5;
    public static final List<String> QUARTZ_MISFIRE_INSTRUCTIONS = Collections.unmodifiableList(Arrays.asList(
	"0 is unknown"
	,"1 - MISFIRE_INSTRUCTION_FIRE_NOW"
	,"2 - MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT"
	,"3 - MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT"
	,"4 - MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT"
	,"5 - MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT"
	));
    
    // Scheduler Time units
    public static final String SCHEDULER_HOUR = "hour";
    public static final String SCHEDULER_DAY ="day";
    public static final String SCHEDULER_WEEK ="week";
    public static final String SCHEDULER_MONTH ="month";
    public static final String SCHEDULER_YEAR = "year";
    public static final List<String> schedulerUnits = Collections.unmodifiableList(Arrays.asList(SCHEDULER_HOUR
                                                                                                 ,SCHEDULER_DAY
                                                                                                 ,SCHEDULER_WEEK
                                                                                                 ));
    public static final List<Integer> schedulerUnitInHours = Collections.unmodifiableList(Arrays.asList(1
                                                                                                 ,24
                                                                                                 ,7*24
                                                                                                 ));

    // Messages
    public static final String ENGLISH = "ENU";
    public static final String DUTCH = "NLD";
    protected static final String[] MESSAGE_LANGUAGES = new String[] { Constants.ENGLISH, Constants.DUTCH
    };
    public static final String NOERRORS="No errors encountered";
    
    // Indicators
    public static final String ALL="All";
    public static final String FIRST_ONLY="FirstOnly";
    
    public static final String HASH="Hash";
    
    public static final String REFRESH_PROFILE="Refresh Profile?";
    
    public static final String DEFAULT_VALUE_FOR_NULL="[NULL]";
    public static final String PARAM_DEFAULT_VALUE_FOR_NULL="DefaultValueForNull";
    
    //Scripts
    public static final String RUNIDQPROFILE_DEFAULT_SCRIPT="runidqprofile.sh";
    public static final String PARAM_RUNIDQPROFILE_SCRIPT="RunIdqProfileScript";
    public static final String RUNIDQMAPPING_DEFAULT_SCRIPT="runidqmapping.sh";
    public static final String PARAM_RUNIDQMAPPING_SCRIPT="RunIdqMappingScript";
    public static final String RUNIDQDISOBJECT_DEFAULT_SCRIPT="runidqdisobject.sh";
    public static final String PARAM_RUNIDQDISOBJECT_SCRIPT="RunIdqDisObjectScript";
    public static final String RUNIDQJOB_DEFAULT_SCRIPT="runidqjob.sh";
    public static final String PARAM_RUNIDQJOB_SCRIPT="RunIdqJobScript";
    
    //Database type
    public static final String DATABASETYPE_DB2 ="DB2";
    public static final String DATABASETYPE_ORACLE ="Oracle";
    public static final String DATABASETYPE_SQLSERVER ="SQLServer";
    public static final String DATABASETYPE_NONSTOP ="Nonstop";
    
    //Informatica Process defaults
    public static final String DEFAULT_ABORTONERROR=Constants.YES;
    public static final String PROP_ABORTONERROR="AbortOnError";

    //Informatica ignore error defaults
    public static final String DEFAULT_REPORTERRORIFNOTRUNNING=Constants.NO;
    public static final String PROP_REPORTERRORIFNOTRUNNING="ReportErrorIfNotRunning";
    
    //FitNesse
    public static final String PARAM_RESULT_FORMAT="ResultFormat";
    public static final String RESULT_AS_HTML="HTML";
    public static final String RESULT_AS_JSON="json";
    public static final String DEFAULT_RESULT_FORMAT=RESULT_AS_HTML;
    public static final List<String> resultFormats = Collections.unmodifiableList(Arrays.asList(RESULT_AS_HTML
                                                                                                 ,RESULT_AS_JSON));
    
    public static final String ERRCODE_INVALID_LOCATION="InvalidLogicalLocation";
    public static final String ERROR_INVALID_LOCATION="An invalid logical location was specified.";
    
    public static final String PARAM_JOBDIR ="jobDir";
    public static final String DEFAULT_JOBDIR="jobs/";
    
    public static final String PARAM_RESULTDIR ="resultDir";
    public static final String DEFAULT_RESULTDIR="results/";

    public static final String JOB_COLUMN_SEPARATOR="|";
    public static final String JOB_DEFINITION = "Table:JobDefinition";
    
    public static final int JOB_STEP_COLNR=0;   // zero-based!   Note: Once the result has been added, StepColNr will be the 2nd column!
    public static final int JOB_STEPRESULT_COLNR=0;
    public static final int JOB_STEPNAME_COLNR=1;   // zero-based!                 
    public static final int JOB_STEPTYPE_COLNR=2;   // zero-based!
    public static final int JOB_TASKTYPE_COLNR=3;

    //Job step types. Keep these all lower case and without any spaces!
    public static final String JOB_STEPTYPE_IDQ = "runidq";
    public static final String JOB_STEPTYPE_PWC = "runpwc";
    public static final String JOB_STEPTYPE_ORACLE = "runoracle";
    public static final String JOB_STEPTYPE_DB2 = "rundb2";
    public static final String JOB_STEPTYPE_DAC = "rundac";
    public static final String JOB_STEPTYPE_SCRIPT = "runscript";
    public static final String JOB_STEPTYPE_WAIT ="wait";

    public static final List<String> JOB_STEPTYPES = Collections.unmodifiableList(Arrays.asList(
         JOB_STEPTYPE_IDQ
        ,JOB_STEPTYPE_PWC
        ,JOB_STEPTYPE_ORACLE
        ,JOB_STEPTYPE_DB2
        ,JOB_STEPTYPE_DAC
        ,JOB_STEPTYPE_SCRIPT
        ,JOB_STEPTYPE_WAIT
        ));

    public static final List<String> JOB_IMPLEMENTED_STEPTYPES = Collections.unmodifiableList(Arrays.asList(
        JOB_STEPTYPE_IDQ
    ,JOB_STEPTYPE_PWC
    ,JOB_STEPTYPE_ORACLE
    ,JOB_STEPTYPE_DB2
    ,JOB_STEPTYPE_DAC
    ,JOB_STEPTYPE_SCRIPT
    ,JOB_STEPTYPE_WAIT
        ));
    public static final String JOB_STEPTYPE_NOT_IMPLEMENTED ="Job Step Type not in the list of implemented types.";
    
    //Job parameter names. Keep these all lower case and without any spaces!
    public static final String JOB_PARAM_VERSION="version";
    public static final String JOB_PARAM_ONLYGENERATE="onlygenerate";
    public static final String JOB_PARAM_PERSISTENCE="persistence";
    public static final String JOB_PARAM_MAX_CONCURRENCY="maxconcurrency";
    public static final String JOB_PARAM_MAX_CONCURRENT="maxconcurrent";  //alternative name for maxconcurrency
    public static final String JOB_PARAM_IDQCONFIG="idqconfig";
    public static final String JOB_PARAM_PWCCONFIG="pwcconfig";
    public static final String JOB_PARAM_ORACLECONFIG="oracleconfig";
    public static final String JOB_PARAM_DB2CONFIG="db2config";
    public static final String JOB_PARAM_DACCONFIG="dacconfig";
    public static final String JOB_PARAM_SCRIPTCONFIG="scriptconfig";

    public static final String JOBSTEP_IDQ_MAPPING="mapping";
    public static final String JOBSTEP_IDQ_SCORECARD="scorecard";
    public static final String JOBSTEP_IDQ_PROFILE="profile";
    public static final String JOBSTEP_IDQ_WORKFLOW="workflow";
    
    public static final String DEFAULT_ONLY_GENERATE_JOBFILE=NO;
        
    public static final List<String> JOBSTEP_IDQ_TYPES = Collections.unmodifiableList(Arrays.asList(
        JOBSTEP_IDQ_MAPPING
        ,JOBSTEP_IDQ_SCORECARD
        ,JOBSTEP_IDQ_PROFILE
        ,JOBSTEP_IDQ_WORKFLOW
        ));
    
    public static final String JOBSTEP_PARAM_IDQ_PROJECT="project";
    public static final String JOBSTEP_PARAM_IDQ_APPLICATION="application";
    public static final String JOBSTEP_PARAM_IDQ_OBJECT="object";
    public static final String JOBSTEP_PARAM_IDQ_MAPPING="mapping";
    public static final String JOBSTEP_PARAM_IDQ_EXPECTEDRESULT="result";
    public static final String JOBSTEP_PARAM_IDQ_ONERROR="abortonerror";  // Yes or No (as with other fixtures)
    public static final String JOBSTEP_PARAM_IDQ_ONERRORACTION="onerror"; // STOP, CONTINUE, ...
    public static final String JOBSTEP_PARAM_IDQ_WAIT="wait";
    public static final String JOBSTEP_PARAM_IDQ_JOBMUSTWAITONSTEPCOMPLETION="jobmustwaitontaskcompletion";

    public static final List<String> JOBSTEP_IDQ_PARAMS_PROFILE = Collections.unmodifiableList(Arrays.asList(
         JOBSTEP_PARAM_IDQ_PROJECT
        ,JOBSTEP_PARAM_IDQ_OBJECT
        ,JOBSTEP_PARAM_IDQ_EXPECTEDRESULT
        ,JOBSTEP_PARAM_IDQ_ONERROR
        ,JOBSTEP_PARAM_IDQ_WAIT
        ,JOBSTEP_PARAM_IDQ_JOBMUSTWAITONSTEPCOMPLETION
        ));
    
    public static final List<String> JOBSTEP_IDQ_PARAMS_MAPPING = Collections.unmodifiableList(Arrays.asList(
         JOBSTEP_PARAM_IDQ_PROJECT
        ,JOBSTEP_PARAM_IDQ_APPLICATION
        ,JOBSTEP_PARAM_IDQ_MAPPING
        ,JOBSTEP_PARAM_IDQ_EXPECTEDRESULT
        ,JOBSTEP_PARAM_IDQ_ONERROR
        ,JOBSTEP_PARAM_IDQ_WAIT
        ,JOBSTEP_PARAM_IDQ_JOBMUSTWAITONSTEPCOMPLETION
        ));
    
    public static final List<String> JOBSTEP_IDQ_PARAMS_WORKFLOW = Collections.unmodifiableList(Arrays.asList(
         JOBSTEP_PARAM_IDQ_PROJECT
        ,JOBSTEP_PARAM_IDQ_APPLICATION
        ,JOBSTEP_PARAM_IDQ_OBJECT
        ,JOBSTEP_PARAM_IDQ_EXPECTEDRESULT
        ,JOBSTEP_PARAM_IDQ_ONERROR
        ,JOBSTEP_PARAM_IDQ_WAIT
        ,JOBSTEP_PARAM_IDQ_JOBMUSTWAITONSTEPCOMPLETION
        ));

    public static final String JOBSTEP_PARAM_STOPACTION_STOP="stop";
    public static final String JOBSTEP_PARAM_STOPACTION_CONTINUE="continue";
    
    public static final List<String> JOBSTEP_PARAMS_ONERRORACTION = Collections.unmodifiableList(Arrays.asList(
         JOBSTEP_PARAM_STOPACTION_STOP
        ,JOBSTEP_PARAM_STOPACTION_CONTINUE
        ));

    public static final int JOB_STEP_IDQ_OBJECTTYPE_COLNR=3;
    
    public static final String EXCEL_CELLFORMAT_UNKNOWN ="UNKNOWN";
    public static final String EXCEL_CELLFORMAT_NUMERIC ="NUMERIC";
    public static final String EXCEL_CELLFORMAT_DATE ="DATE";
    public static final String EXCEL_CELLFORMAT_STRING ="STRING";
 
    //Parameterized commit and array sizes
    public static final String PARAM_COMMIT_SIZE_INSERT="CommitSizeInsert";
    public static final int DEFAULT_COMMIT_SIZE_INSERT=1000;
    public static final String PARAM_COMMIT_SIZE_UPDATE="CommitSizeUpdate";
    public static final int DEFAULT_COMMIT_SIZE_UPDATE=1000;
    public static final String PARAM_ARRAY_SIZE_INSERT="ArraySizeInsert";
    public static final int DEFAULT_ARRAY_SIZE_INSERT=100;
    public static final String PARAM_ARRAY_SIZE_UPDATE="ArraySizeUpdate";
    public static final int DEFAULT_ARRAY_SIZE_UPDATE=100;
    
}
