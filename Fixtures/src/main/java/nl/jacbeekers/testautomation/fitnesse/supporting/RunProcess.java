package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.OS_COMMAND_ERROR;

public class RunProcess {

    private String resultCode=Constants.OK;
    private String resultMessage=Constants.NO_ERRORS;
    private int rc=0;

    public int runAndWait(String command) {
        String s;
        Process p;
        System.out.println("Running command >" + command +"<.");

        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            rc = p.exitValue();
//            System.out.println("Completed command >" + command +"< with exit code>" + rc +"<.");
            p.destroy();
        } catch (Exception e) {
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

    private void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public String getResultMessage() { return this.resultMessage; }

    private void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultCode() { return this.resultCode; }

}
