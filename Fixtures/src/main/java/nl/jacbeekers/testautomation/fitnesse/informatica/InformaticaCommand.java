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

//import static supporting.ListUtility.list;

import nl.jacbeekers.testautomation.fitnesse.scripts.ExecuteScript;

import org.apache.commons.lang3.StringUtils;

public class InformaticaCommand {
    private static final String version = "20190512.0"; // removed incorrect references

    private String className = InformaticaCommand.class.getName();
    private String logFileName = Constants.NOT_INITIALIZED;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel =3;
    private int logEntries =0;
    private String abortYesNo = Constants.YES;
    private String logUrl=Constants.LOG_DIR;
    private String resultFormat =Constants.DEFAULT_RESULT_FORMAT;
    private String resultCode = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    private boolean userSetAbortOnError = false;

    private String featureName =null;

    private ArrayList<String> environment = new ArrayList<String>();
    //informatica
    private String infaTool = Constants.NOT_FOUND;
    private String infaToolOption = Constants.NOT_FOUND;
    private String infaEnvironment = Constants.NOT_PROVIDED;
    private String infaHome = Constants.NOT_PROVIDED;
    private String infacmd = Constants.NOT_PROVIDED;
    private String infaMRS = Constants.NOT_PROVIDED;
    private String infaDIS = Constants.NOT_PROVIDED;
    private String commandLineOptions ="";

    public InformaticaCommand() {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
        logFileName = startDate + "." + className;

    }

    public InformaticaCommand(String context) {
    // Constructor
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
    setLogFilename( startDate + "." + context + "." + className);

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

    public void setLogFilename(String logFileName) { this.logFileName = logFileName; }
    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
    }

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

    public String getLogLevel() { return Constants.logLevel.get(getIntLogLevel()); }
    public Integer getIntLogLevel() { return logLevel; }

    private void setError(String code, String msg) { setResultCode(code); setResultMessage(msg); }

    public String result() { return getResult(); }
    public String getResult() { return getResultCode(); }

    public String runInformaticaCommand() throws InformaticaCommandStopTest {
        String myName="runInformaticaCommand";
        String myArea ="init";
        readParameterFile();

        String command = getInfacmd() + " " + getInfaTool() + " " + getInfaToolOption()
                + " " + getCommandLineOptions();


        ExecuteScript rs = new ExecuteScript(command);
        rs.setDeterminePath(false);
        rs.setEnvironment(getEnvironment().toArray(new String[0]));
        rs.setLogLevel(getLogLevel());
        rs.setCaptureOutput(Constants.YES);
        rs.setCaptureErrors(Constants.YES);
        
        try {
            rs.runScriptReturnCode();
        } catch (ExecuteScript.ExecuteScriptStopTest e) {
            if (Constants.YES.equals(getAbortOnError())) {
                throw new InformaticaCommandStopTest(getResultMessage());
            }

        }

        setResultMessage(rs.getErrorMessage());
        setResultCode(rs.getErrorCode());
        
        return getResultCode();
        
    }

    public void readParameterFile() {
        InfaParameters infaParameters = new InfaParameters();
        infaParameters.setFeatureName(getFeatureName());

        setInfaHome(infaParameters.getInfaHome());
        setInfacmd(infaParameters.getInfacmd());
        setInfaMRS(infaParameters.getInfaMRS());
        setInfaDIS(infaParameters.getInfaDIS());

        buildEnvironment(infaParameters);
    }
    private void buildEnvironment(InfaParameters infaParameters) {
        environment.add(infaParameters.PARAM_INFA_DOMAINS_FILE +"=" + infaParameters.getInfaDomainsFile());
        environment.add(infaParameters.PARAM_INFA_DEFAULT_DOMAIN + "=" + infaParameters.getInfaDefaultDomain());
        environment.add(infaParameters.PARAM_INFA_DOMAIN_USER + "=" + infaParameters.getInfaDefaultDomainUser());
        environment.add(infaParameters.PARAM_INFA_DOMAIN_PASSWORD + "=" + infaParameters.getInfaDefaultDomainPassword());
    }
    public ArrayList<String> getEnvironment() { return environment; }

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

    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public String getFeatureName() { return this.featureName; }

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
        
        return getResultCode();
        
    }
    
    /**
     * @return fixture version info
     */
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
