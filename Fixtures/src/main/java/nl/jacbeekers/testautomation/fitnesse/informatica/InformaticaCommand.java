/**
 * This purpose of this fixture is to run Informatica DQ Mappings
 * @author Jac Beekers
 * @since 12 July 2016
 * @version 20160712.0 - initial version
 * @version 20170107.0 - RunScript introduced StopTest
 * @version 20170225.0 - Scripts can be set in fixture.properties file
 */
package nl.jacbeekers.testautomation.fitnesse.informatica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import nl.jacbeekers.testautomation.fitnesse.supporting.*;
import nl.jacbeekers.testautomation.fitnesse.scripts.ExecuteScript;
import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.ERRCODE_INFACMD_ERROR;

public class InformaticaCommand {
    private static final String version = "20200330.0";

    private String className = InformaticaCommand.class.getName()
            .substring(InformaticaCommand.class.getName().lastIndexOf(".")+1);
    private String context ="";
    private String logFilename = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel =3;
    private int logEntries =0;
    private String abortYesNo = Constants.YES;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;
    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    private boolean userSetAbortOnError = false;

//    private String featureName =null;

    private ArrayList<String> environment = new ArrayList<String>();
    //informatica
    private String infaTool = Constants.NOT_FOUND;
    private String infaToolOption = Constants.NOT_FOUND;
    private String infaEnvironment = Constants.NOT_PROVIDED;
    private String infaHome = Constants.NOT_PROVIDED;
    private String infacmd = Constants.NOT_PROVIDED;
    private String infaMRS = Constants.NOT_PROVIDED;
    private String infaDIS = Constants.NOT_PROVIDED;
    private String infaOSProfile = Constants.NOT_PROVIDED;
    private String commandLineOptions ="";

    public InformaticaCommand() {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
        setLogFilename(startDate + "." + className);

    }

    public InformaticaCommand(String context) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
    setContext(context);
    setLogFilename( startDate + "." + context + "." + className);

    }

    public InformaticaCommand(String context, String loglevel) {
        // Constructor
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        setContext(context);
        setLogFilename( startDate + "." + context + "." + className);
        setLogLevel(loglevel);
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

    public void setLogFilename(String logFileName) { this.logFilename = logFileName; }
    public String getLogFilename() { return this.logFilename; };

    public void setLogLevel(String level) {
       String myName ="setLogLevel";
       String myArea ="determineLevel";
       logLevel = Constants.logLevel.indexOf(level.toUpperCase());
       if (logLevel <0) {
           log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
           logLevel =3;
       }
//       log(myName, Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
    }

    public String getLogLevel() { return Constants.logLevel.get(getIntLogLevel()); }
    public Integer getIntLogLevel() { return logLevel; }

    private void setError(String code, String msg) { setResultCode(code); setResultMessage(msg); }

    public String result() { return getResult(); }
    public String getResult() { return getResultCode(); }

    public String runInformaticaCommand() throws InformaticaCommandStopTest {
        String myName="runInformaticaCommand";
        String myArea ="init";
        setParameters();
        outParameters();

        String command = getInfacmd() + " " + getInfaTool() + " " + getInfaToolOption()
                + " " + getCommandLineOptions();

        myArea="executeScript";
        ExecuteScript executeScript = new ExecuteScript(command, getContext());
        executeScript.setDeterminePath(false);
        executeScript.setEnvironment(getEnvironment().toArray(new String[0]));
        executeScript.setLogFilename(getLogFilename());
        executeScript.setLogLevel(getLogLevel());
        executeScript.setCaptureOutput(Constants.YES);
        executeScript.setCaptureErrors(Constants.YES);
        
        try {
            executeScript.runScriptReturnCode();
            //initially check script exit code
            setResultMessage(executeScript.getErrorMessage());
            setResultCode(executeScript.getErrorCode());
            //with infacmd, the rc=0 even though the profile, scorecard or workflow failed
            if(Constants.OK.equals(getResultCode())) {
                parseOutputForErrors(executeScript.getStdOut());
            }
        } catch (ExecuteScript.ExecuteScriptStopTest e) {
            if (Constants.YES.equals(getAbortOnError())) {
                throw new InformaticaCommandStopTest(getResultMessage());
            }

        }


        return getResultCode();
        
    }

    private void parseOutputForErrors(ArrayList<String> cmdOutput) {
        String result = Constants.UNKNOWN;
        String getit[] = { "1", "2"};
        if(cmdOutput != null) {
            for(String line : cmdOutput) {
                //Profile run status =FAILURE
                if(line.contains("run status")) {
                    getit = line.split("=", 2);
                    if(getit.length >1)
                        result = getit[1];
                    switch(result) {
                        case "FAILURE":
                            setResultMessage("infacmd returned 0, but output reported FAILURE");
                            setResultCode(ERRCODE_INFACMD_ERROR);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void setParameters() {
        InfaParameters infaParameters = new InfaParameters();
        infaParameters.setInfaEnvironment(getInfaEnvironment());

        setInfaHome(infaParameters.getInfaHome());
        setInfacmd(infaParameters.getInfacmd());
        setInfaMRS(infaParameters.getInfaMRS());
        setInfaDIS(infaParameters.getInfaDIS());
        setInfaOSProfile(infaParameters.getInfaOSProfile());

        setEnvironment(infaParameters);
    }
    private void setEnvironment(InfaParameters infaParameters) {
        environment.add(infaParameters.PARAM_INFA_DOMAINS_FILE +"=" + infaParameters.getInfaDomainsFile());
        environment.add(infaParameters.PARAM_INFA_DEFAULT_DOMAIN + "=" + infaParameters.getInfaDefaultDomain());
        environment.add(infaParameters.PARAM_INFA_DOMAIN_USER + "=" + infaParameters.getInfaDefaultDomainUser());
        environment.add(infaParameters.PARAM_INFA_DOMAIN_PASSWORD + "=" + infaParameters.getInfaDefaultDomainPassword());
    }

    public void outParameters(){
        String myName="outParameters";
        String myArea="run";

        outEnvironment(getEnvironment());
//        log(myName, Constants.DEBUG,myArea,"")
    }
    public void outEnvironment(ArrayList<String> environment){
        String myName="outEnvironment";
        String myArea="run";
        for(String var : environment) {
            log(myName, Constants.DEBUG, myArea, "Environment variable >" + var +"<.");
        }
    }
    public ArrayList<String> getEnvironment() { return environment; }

    public void setContext(String context) { this.context = context; }
    public String getContext() { return this.context; }

    public void setInfaTool(String infaTool) { this.infaTool = infaTool; }
    public String getInfaTool() { return this.infaTool; }

    public void setInfaToolOption(String infaToolOption) { this.infaToolOption = infaToolOption; }
    public String getInfaToolOption() { return this.infaToolOption; }

    public void setInfaEnvironment(String infaEnvironment) { this.infaEnvironment = infaEnvironment; }
    public String getInfaEnvironment() { return this.infaEnvironment; }

    public void setInfaHome(String infaHome) { this.infaHome = infaHome; }
    public String getInfaHome() { return this.infaHome; }

    public void setInfacmd(String infacmd) { this.infacmd = infacmd; }
    public String getInfacmd() { return this.infacmd; }

    public void setInfaMRS(String infaMRS) { this.infaMRS = infaMRS;}
    public String getInfaMRS() { return this.infaMRS; }

    public void setInfaDIS(String infaDIS) { this.infaDIS = infaDIS; }
    public String getInfaDIS() { return this.infaDIS; }

    public void setInfaOSProfile(String infaOSProfile) { this.infaOSProfile = infaOSProfile; }
    public String getInfaOSProfile() { return this.infaOSProfile; }


//    public void setFeatureName(String featureName) { this.featureName = featureName; }
//    public String getFeatureName() { return this.featureName; }

    public void setCommandLineOptions(String commandLineOptions) { this.commandLineOptions = commandLineOptions;}
    public String getCommandLineOptions() { return this.commandLineOptions; }

    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

    public void setResultMessage(String resultMessage){ this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; }

    public void setAbortOnError(String abortYesNo) throws ExecuteScript.ExecuteScriptStopTest {
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
                throw new ExecuteScript.ExecuteScriptStopTest(logMessage);
            }
        }
    }

    public String getAbortOnError() {
        return this.abortYesNo;
    }


    public static String getVersion() {
        return version;
    }

    static class InformaticaCommandStopTest extends Exception {
         @SuppressWarnings("compatibility:-5661641983890468252")
         static final long serialVersionUID = 34985952947L;
            public InformaticaCommandStopTest(){
            }

         /**
          * @param msg
          */
         public InformaticaCommandStopTest(String msg){
           super(msg);
       }

     }

}
