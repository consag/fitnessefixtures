package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.OS_COMMAND_ERROR;

public class RunProcess {

    public String version ="20190512.0";

    private String command= Constants.NOT_PROVIDED;
    private String[] environment = { Constants.NOT_PROVIDED };
    private String resultCode=Constants.OK;
    private String resultMessage=Constants.NO_ERRORS;
    private int rc=0;
    private String startDate;
    private String logFileName = RunProcess.class.getName();

    //constructor
    public RunProcess() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + RunProcess.class.getName();
    }

    public RunProcess(String command) {
        setCommand(command);
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        logFileName = startDate + "." + RunProcess.class.getName();
    }

    public int runAndWait() {
        return runAndWait(getCommand());
    }

    public int runAndWait(String command) {
        String procName ="runAndWait";
        String s;
        Process p;
        Logging.LogEntry(getLogFileName(), Constants.DEBUG, procName, "Running command >" + command +"<." );

        try {
            if(getEnvironment() == null || Constants.NOT_PROVIDED.equals(getEnvironment()[0])) {
                p = Runtime.getRuntime().exec(command);
            } else {
                p = Runtime.getRuntime().exec(command, getEnvironment());
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                Logging.LogEntry(getLogFileName(), Constants.DEBUG, procName, "command output: " + s );
            p.waitFor();
            rc = p.exitValue();
            Logging.LogEntry(getLogFileName(), Constants.DEBUG, procName, "Completed command >" + command +"< with exit code>" + rc +"<.");
            p.destroy();
        } catch (Exception e) {
            Logging.LogEntry(getLogFileName(), Constants.DEBUG, procName, "Exception occurred running >" + command + " : " + e.toString());
            setResultMessage(e.toString());
            setResultCode(OS_COMMAND_ERROR);
            rc=-1;
        }
        if(rc != 0) {
            setResultCode(OS_COMMAND_ERROR);
            setResultMessage("Command >" + command + "< failed with exit code >" + rc +"<.");
        }
        return rc;
    }

    //getters and setters
    public void setEnvironment(String[] env) { this.environment = env; }
    public String[] getEnvironment() { return this.environment; }
    public void setCommand(String command) { this.command = command; }
    public String getCommand() { return this.command; }

    private void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; }

    private void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

    public String getVersion() { return this.version; }

    public void setLogFileName(String logFileName) { this.logFileName=logFileName; }
    public String getLogFileName() { return logFileName; }

}
