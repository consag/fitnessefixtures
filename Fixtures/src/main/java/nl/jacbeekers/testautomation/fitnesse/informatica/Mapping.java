package nl.jacbeekers.testautomation.fitnesse.informatica;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools;
import nl.jacbeekers.testautomation.fitnesse.supporting.InfaParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.text.SimpleDateFormat;

import static nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools.INFA_FUNCTION_RUNMAPPING;

public class Mapping {
    private static final String version = "20190513.0";
    private String className = Mapping.class.getName()
            .substring(Mapping.class.getName().lastIndexOf(".")+1);

    public Mapping() {
        setLogLevel(Constants.DEBUG);
    }

    public Mapping(String infaConnection, String projectName, String logLevel) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        setLogFilename( startDate + "." + projectName + "." + className);

        setInfaConnection(infaConnection);
        setProjectName(projectName);
        setLogLevel(logLevel);
    }

    private String startDate;
    private int logLevel =3;
    private int logEntries=0;
    private String logUrl=Constants.LOG_DIR;
    private String logFilename = Mapping.class.getName() +".log";
    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    //informatica
    private String infaConnection = Constants.NOT_PROVIDED;
    private String projectName = Constants.NOT_PROVIDED;
    private String folderName = Constants.NOT_PROVIDED;
    private String applicationName = Constants.NOT_PROVIDED;
    private String mappingName = Constants.NOT_PROVIDED;

    public String runMapping() throws InformaticaCommand.InformaticaCommandStopTest {

        InfaParameters infaParameters = new InfaParameters();
        InformaticaCommand informaticaCommand = new InformaticaCommand(getApplicationName(),getLogLevel());
        informaticaCommand.setLogFilename(getLogFilename());
        informaticaCommand.setInfaEnvironment(getInfaConnection());
        informaticaCommand.setInfaTool(InfaCmdTools.getTool(INFA_FUNCTION_RUNMAPPING));
        informaticaCommand.setInfaToolOption(InfaCmdTools.getToolOption(INFA_FUNCTION_RUNMAPPING));
        informaticaCommand.setCommandLineOptions("-ServiceName " + infaParameters.getInfaDIS() + " -Application " + getApplicationName()
                + " -Mapping " + getMappingName() + " " + InfaCmdTools.getWaitOption(INFA_FUNCTION_RUNMAPPING));

        informaticaCommand.runInformaticaCommand();
        setResultCode(informaticaCommand.getResultCode());
        setResultMessage(informaticaCommand.getResultMessage());

        return getResultCode();
    }

    //getters and setters
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

    public void setResultMessage(String resultMessage){ this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; }

    public void setInfaConnection(String infaConnection) { this.infaConnection = infaConnection; }
    public String getInfaConnection() { return infaConnection; }

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

    public void setLogFilename(String logFilename) { this.logFilename = logFilename; }
    public String getLogFilename() { return logFilename; }
    public String getLogFilenameLink() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+getLogFilename() +".log\" target=\"_blank\">" + getLogFilename() + "</a>";
        else
            return logUrl+getLogFilename() + ".log";
    }


    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
    public String getApplicationName() { return this.applicationName; }

    public void setFolderName(String folderName) { this.folderName = folderName; }
    public String getFolderName() { return this.folderName; }

    public void setMappingName(String mappingName) { this.mappingName = mappingName; }
    public String getMappingName() { return this.mappingName; }

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
