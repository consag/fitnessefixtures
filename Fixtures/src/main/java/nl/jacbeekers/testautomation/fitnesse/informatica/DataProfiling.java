package nl.jacbeekers.testautomation.fitnesse.informatica;

import nl.jacbeekers.testautomation.fitnesse.supporting.*;

import java.text.SimpleDateFormat;

import static nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools.INFA_FUNCTION_RUNPROFILE;
import static nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools.INFA_FUNCTION_RUNSCORECARD;

public class DataProfiling {
    private static final String version = "20200330.0";
    private String className = DataProfiling.class.getName()
            .substring(DataProfiling.class.getName().lastIndexOf(".")+1);

    public DataProfiling() {
        setLogLevel(Constants.DEBUG);
    }

    public DataProfiling(String infaEnvironment, String projectName, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        setLogFilename( startDate + "." + projectName + "." + className);

        setInfaEnvironment(infaEnvironment);
        setProjectName(projectName);
        setLogLevel(logLevel);
    }

    private String startDate;
    private int logLevel =3;
    private int logEntries=0;
    private String logUrl=Constants.LOG_DIR;
    private String logFilename = DataProfiling.class.getName() +".log";
    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    //informatica
    private String infaEnvironment = Constants.NOT_PROVIDED;
    private String infaMRS = Constants.NOT_FOUND;
    private String infaDIS = Constants.NOT_FOUND;
    private String infaOSProfile = Constants.NOT_PROVIDED;

    private String projectName = Constants.NOT_PROVIDED;
    private String folderName = Constants.NOT_PROVIDED;
    private String profileName = Constants.NOT_PROVIDED;
    private String scorecardName = Constants.NOT_PROVIDED;

    public void getParameters() {
        InfaParameters infaParameters = new InfaParameters();
        infaParameters.setInfaEnvironment(getInfaEnvironment());
        infaParameters.setLogFilename(getLogFilename());
        infaParameters.setLogLevel(getLogLevel());

        setInfaDIS(infaParameters.getInfaDIS());
        setInfaMRS(infaParameters.getInfaMRS());
        // If already set on Test Page, don't look in properties
        if(Constants.NOT_PROVIDED.equals(getOSProfile())) {
            setOSProfile(infaParameters.getInfaOSProfile());
        }

        setLogUrl(GetParameters.GetLogUrl());
    }

    public String runProfile() throws InformaticaCommand.InformaticaCommandStopTest {

        getParameters();

        InformaticaCommand informaticaCommand = new InformaticaCommand(getProfileName(),getLogLevel());
        informaticaCommand.setLogFilename(getLogFilename());
        informaticaCommand.setLogLevel(getLogLevel());
        informaticaCommand.setInfaEnvironment(getInfaEnvironment());
        informaticaCommand.setInfaTool(InfaCmdTools.getTool(INFA_FUNCTION_RUNPROFILE));
        informaticaCommand.setInfaToolOption(InfaCmdTools.getToolOption(INFA_FUNCTION_RUNPROFILE));
        String objectPath=null;
        if(Constants.NOT_PROVIDED.equals(getFolderName()) || "".equals(getFolderName())) {
            objectPath = getProjectName() +"/" +getProfileName();
        } else {
            objectPath = getProjectName() +"/" + getFolderName() +"/"+ getProfileName();
        }

        String cmdOptions = " -ObjectType " + "profile" + " -MrsServiceName " + getInfaMRS() + " -DsServiceName " + getInfaDIS()
                + " -ObjectPathAndName " + objectPath + " " + InfaCmdTools.getWaitOption(INFA_FUNCTION_RUNPROFILE);

        if(!Constants.NOT_FOUND.equals(getOSProfile())) {
            cmdOptions += " -osp " + getOSProfile();
        }
        informaticaCommand.setCommandLineOptions(cmdOptions);

        informaticaCommand.runInformaticaCommand();
        setResultCode(informaticaCommand.getResultCode());
        setResultMessage(informaticaCommand.getResultMessage());

        return getResultCode();
    }

    public String runScorecard() throws InformaticaCommand.InformaticaCommandStopTest {

        getParameters();

        InformaticaCommand informaticaCommand = new InformaticaCommand(getProfileName(),getLogLevel());
        informaticaCommand.setLogFilename(getLogFilename());
        informaticaCommand.setLogLevel(getLogLevel());
        informaticaCommand.setInfaEnvironment(getInfaEnvironment());
        informaticaCommand.setInfaTool(InfaCmdTools.getTool(INFA_FUNCTION_RUNSCORECARD));
        informaticaCommand.setInfaToolOption(InfaCmdTools.getToolOption(INFA_FUNCTION_RUNSCORECARD));
        String objectPath=null;
        if(Constants.NOT_PROVIDED.equals(getFolderName()) || "".equals(getFolderName())) {
            objectPath = getProjectName() +"/" +getScorecardName();
        } else {
            objectPath = getProjectName() +"/" + getFolderName() +"/"+ getScorecardName();
        }
        String cmdOptions = " -ObjectType " + "scorecard" + " -MrsServiceName " + getInfaMRS() + " -DsServiceName " + getInfaDIS()
                + " -ObjectPathAndName " + objectPath + " " + InfaCmdTools.getWaitOption(INFA_FUNCTION_RUNSCORECARD);
        if(! Constants.NOT_FOUND.equals(getOSProfile())){
            cmdOptions += " -osp " + getOSProfile();
        }
        informaticaCommand.setCommandLineOptions(cmdOptions);
        informaticaCommand.runInformaticaCommand();
        setResultCode(informaticaCommand.getResultCode());
        setResultMessage(informaticaCommand.getResultMessage());

        return getResultCode();

    }

    //getters and setters
    public void setOSProfile(String infaOSProfile) { this.infaOSProfile = infaOSProfile; }
    public String getOSProfile() { return this.infaOSProfile; }

    public void setInfaDIS(String infaDIS) { this.infaDIS = infaDIS; }
    public String getInfaDIS() { return this.infaDIS; }

    public void setInfaMRS(String infaMRS) { this.infaMRS = infaMRS; }
    public String getInfaMRS() { return this.infaMRS; }

    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

    public void setResultMessage(String resultMessage){ this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; }

    public void setInfaEnvironment(String infaEnvironment) { this.infaEnvironment = infaEnvironment; }
    public String getInfaEnvironment() { return infaEnvironment; }

    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectName() { return projectName; }

    public void setLogLevel(String level) {
        String myName ="setLogLevel";
        String myArea ="determineLevel";
        logLevel = Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel <0) {
            log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
            logLevel =3;
        }
//        log(myName, Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
    }

    public String getLogLevel() { return Constants.logLevel.get(getIntLogLevel()); }
    public Integer getIntLogLevel() { return logLevel; }

    public void setLogUrl(String logUrl) { this.logUrl = logUrl; }
    public String getLogUrl() { return this.logUrl; }

    public void setLogFilename(String logFilename) { this.logFilename = logFilename; }
    public String getLogFilename() { return logFilename; }
    public String getLogFilenameLink() {
        if(getLogUrl().startsWith("http"))
            return "<a href=\"" +logUrl+getLogFilename() +".log\" target=\"_blank\">" + getLogFilename() + "</a>";
        else
            return getLogUrl()+getLogFilename() + ".log";
    }


    public void setFolderName(String folderName) { this.folderName = folderName; }
    public String getFolderName() { return this.folderName; }

    public void setProfileName(String profileName) { this.profileName = profileName; }
    public String getProfileName() { return this.profileName; }

    public void setScorecardName(String scorecardName) { this.scorecardName = scorecardName; }
    public String getScorecardName() { return this.scorecardName; }

    public static String getVersion() { return version; }
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

}
