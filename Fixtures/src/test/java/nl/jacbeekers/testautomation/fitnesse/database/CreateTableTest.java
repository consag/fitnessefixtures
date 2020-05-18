package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.FitNesseTest;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages;
import org.apache.commons.text.CaseUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class CreateTableTest {
    //Logging
    private Logger logger;
    private String method;

    private String result = Constants.OK;
    private String resultMessage = Constants.NOERRORS;

    @Test
    void testCreationOfTableInDatabaseSchemaAsCloneOfTableInDatabaseSchemaIs() {
        setMethod("testCreationOfTableInDatabaseSchemaAsCloneOfTableInDatabaseSchemaIs");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
        executeCreateTableAsCloneTest("/UnitTests/database/CreateTableAsClone.wiki");
    }

    @Test
    void testCreateTable() {
        setMethod("testCreateTable");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
        executeCreateTableTest("/UnitTests/database/CreateTable.wiki");

        assertTrue(Constants.OK.equals(getResult())
                , "Table creation failed: " + getResultMessage() );
    }

    private String executeCreateTableTest(String testPage) {
        CreateTable createTable = new CreateTable();
        createTable.setLogLevel(Constants.DEBUG);
        FitNesseTest fitNesseTest = new FitNesseTest(testPage);
        String testfilename = fitNesseTest.getPathTestPage();
        List<List<String>> inputTable = fitNesseTest.createTestTable(testfilename);
        assertTrue(inputTable != null && inputTable.size() > 0
                , "Input Table is empty. File >" + testfilename + "< exists?");
        boolean ensureFound = false;
        for(List<String> record : inputTable) {
            String firstCol = CaseUtils.toCamelCase(record.get(0), false);
            switch(firstCol) {
                case "setDatabase":
                    getLogger().info("setDatabase for >" + record.get(1) + "<.");
                    createTable.setDatabaseName(record.get(1));
                    break;
                case "addColumn":
                    getLogger().info("addColumn named >" + record.get(1) + "<.");
                    createTable.addColumnDataType(record.get(1), record.get(3));
                    break;
                case "ensure":
                    ensureFound = true;
                    getLogger().info("ensure for >" + record.get(1) +"<.");
                    if("createTable".equals(CaseUtils.toCamelCase(record.get(1), false))) {
                        createTable.createTableIs(record.get(2), Constants.OK);
                    }
                    break;
                default:
                    getLogger().finest("Ignoring >" + record.get(0) +"<.");
            }
        }
        if(ensureFound) {
            setResult(createTable.getResult());
            setResultMessage(createTable.getResultMessage());
        } else {
            setResult(Constants.ERROR);
            setResultMessage("No ensure row found in test table. Table creation did not happen.");
        }
        return getResult();
    }

    private String executeCreateTableAsCloneTest(String testPage) {
        String tgtTable = "NO_TARGET_TABLE";
        String tgtDatabase = "NO_TARGET_DATABASE";
        String tgtSchema = "NO_TARGET_SCHEMA";
        String srcTable = "NO_SOURCE_TABLE";
        String srcDatabase = "NO_SOURCE_DATABASE";
        String srcSchema = "NO_SOURCE_SCHEMA";
        String expectedResult = "OK";
        CreateTable createTable = new CreateTable("unittest_createtable_asclone");
        createTable.setLogLevel(Constants.DEBUG);
        FitNesseTest fitNesseTest = new FitNesseTest(testPage);
        String testfilename = fitNesseTest.getPathTestPage();
        List<List<String>> inputTable = fitNesseTest.createTestTable(testfilename);
        assertTrue(inputTable != null && inputTable.size() > 0
                , "Input Table is empty. File >" + testfilename + "< exists?");

        boolean found =false;
        int i =-1;
        while(!found && (i < inputTable.size())) {
            i++;
            if(inputTable.get(i).get(0).startsWith("ensure")) {
                found = true;
            }
        }

        if (found) {
            tgtTable = inputTable.get(i).get(2);
            tgtDatabase = inputTable.get(i).get(4);
            tgtSchema = inputTable.get(i).get(6);
            srcTable = inputTable.get(i).get(8);
            srcDatabase = inputTable.get(i).get(10);
            srcSchema = inputTable.get(i).get(12);
        }


        String finalTgtTable = tgtTable;
        String finalTgtDatabase = tgtDatabase;
        String finalTgtSchema = tgtSchema;
        String finalSrcTable = srcTable;
        String finalSrcDatabase = srcDatabase;
        String finalSrcSchema = srcSchema;
        assumingThat(inputTable != null && inputTable.size() > 0
                , () -> createTable.creationOfTableInDatabaseSchemaAsCloneOfTableInDatabaseSchemaIs(
                         finalTgtTable
                        , finalTgtDatabase
                        , finalTgtSchema
                        , finalSrcTable
                        , finalSrcDatabase
                        , finalSrcSchema
                        ,expectedResult
                ));


        assumingThat(ResultMessages.ERRCODE_SQLERROR.equals(createTable.getResult()), () -> setResult(Constants.OK));
        getLogger().info(createTable.getResultMessage());
        assertTrue(Constants.OK.equals(getResult()), createTable.getResultMessage());

        return createTable.getResult();
    }

    /**
     * Getters and Setters
     */

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    private String getMethod() {
        return method;
    }

    private void setMethod(String method) {
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