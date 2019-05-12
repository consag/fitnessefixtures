package nl.jacbeekers.testautomation.fitnesse.informatica;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools;
import nl.jacbeekers.testautomation.fitnesse.supporting.InfaParameters;

import static nl.jacbeekers.testautomation.fitnesse.supporting.InfaCmdTools.INFA_FUNCTION_RUNMAPPING;

public class Mapping {

    public Mapping() {
        setLogLevel(Constants.DEBUG);
    }

    public Mapping(String infaConnection, String projectName, String logLevel) {
        setInfaConnection(infaConnection);
        setProjectName(projectName);
        setLogLevel(logLevel);
    }

    private String logLevel = Constants.DEBUG;
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
        InformaticaCommand informaticaCommand = new InformaticaCommand(getApplicationName());
        informaticaCommand.setInfaEnvironment(getInfaConnection());
        informaticaCommand.setInfaTool(InfaCmdTools.getTool(INFA_FUNCTION_RUNMAPPING));
        informaticaCommand.setInfaToolOption(InfaCmdTools.getToolOption(INFA_FUNCTION_RUNMAPPING));
        informaticaCommand.setCommandLineOptions("-ServiceName " + infaParameters.getInfaMRS() + " -Application " + getApplicationName()
                + " -Mapping " + getMappingName());

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

    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    public String getLogLevel() { return logLevel; }

    public void setLogFilename(String logFilename) { this.logFilename = logFilename; }
    public String getLogFilename() { return logFilename; }

    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
    public String getApplicationName() { return this.applicationName; }

    public void setFolderName(String folderName) { this.folderName = folderName; }
    public String getFolderName() { return this.folderName; }

    public void setMappingName(String mappingName) { this.mappingName = mappingName; }
    public String getMappingName() { return this.mappingName; }

}
