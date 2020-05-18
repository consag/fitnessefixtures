package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.FitNesseTest;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import org.apache.commons.text.CaseUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DropTableTest {
    //Logging
    private Logger logger;
    private String method;

    private String result = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    @Test
    void tableDoesNotExistInDatabaseIgnoreError() {
        setMethod("tableDoesNotExistInDatabase");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
        executeDropTableTest("/UnitTests/database/DropTableIgnoreError.wiki");
        getLogger().info(getResult());

        assertTrue(Constants.OK.equals(getResult())
                , "Drop Table failed: " + getResultMessage() );

    }

    @Test
    void tableDoesNotExistInDatabase() {
        setMethod("tableDoesNotExistInDatabase");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
        executeDropTableTest("/UnitTests/database/DropTable.wiki");
        getLogger().info("'does not exist' is an expected error. " + getResultMessage());

        assertTrue(Constants.OK.equals(getResult()) || getResultMessage().contains("does not exist")
                , "Drop Table failed: " + getResultMessage() );

    }

    private String executeDropTableTest(String testPage) {
        DropTable dropTable = new DropTable();
        dropTable.setLogLevel(Constants.DEBUG);
        FitNesseTest fitNesseTest = new FitNesseTest(testPage);
        String testfilename = fitNesseTest.getPathTestPage();
        List<List<String>> inputTable = fitNesseTest.createTestTable(testfilename);
        assertTrue(inputTable != null && inputTable.size() > 0
                , "Input Table is empty. File >" + testfilename + "< exists?");
        boolean ensureFound = false;
        boolean rc = false;
        for(List<String> record : inputTable) {
            String firstCol = CaseUtils.toCamelCase(record.get(0), false);
            switch(firstCol) {
                case "setDatabase":
                    getLogger().info("setDatabase for >" + record.get(1) + "<.");
                    dropTable.setDatabaseName(record.get(1));
                    break;
                case "ignoreError":
                    getLogger().info("ignoreError is >" + record.get(1) + "<.");
                    dropTable.ignoreError(record.get(1));
                    break;
                case "ensure":
                    ensureFound = true;
                    getLogger().info("ensure for >" + record.get(1) +"<.");
                    if("table".equals(CaseUtils.toCamelCase(record.get(1), false))) {
                        rc = dropTable.tableDoesNotExistInDatabase(record.get(2), record.get(4));
                    }
                    break;
                default:
                    getLogger().finest("Ignoring >" + record.get(0) +"<.");
            }
        }
        if(ensureFound) {
            if(rc) {
                setResult(dropTable.getResult());
                setResultMessage(dropTable.getResultMessage());
            } else {
                setResult(Constants.ERROR);
                setResultMessage("Drop returned an error: " + dropTable.getResultMessage());
            }
        } else {
            setResult(Constants.ERROR);
            setResultMessage("No ensure row found in test table. Table creation did not happen.");
        }
        return getResult();
    }


    /*
     * Getters and Setters
     */

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}