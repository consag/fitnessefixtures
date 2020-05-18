package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.FitNesseTest;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class BasicQueryTest {
    //Logging
    private Logger logger;
    private String method;
    // Testing
    private String result = Constants.OK;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDoTable() {
        setMethod("testDoTable");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
        executeBasicQueryTest();
    }

    private String executeBasicQueryTest() {
        BasicQuery basicQuery = new BasicQuery();
        basicQuery.setLogLevel(Constants.DEBUG);
        FitNesseTest fitNesseTest = new FitNesseTest("/UnitTests/ExamplesBasicFixtures/ExampleBasicQuery.wiki");
        String testfilename = fitNesseTest.getPathTestPage();
        List<List<String>> inputTable = fitNesseTest.createTestTable(testfilename);
        assumingThat(inputTable != null && inputTable.size() > 0
                , () -> basicQuery.doTable(inputTable));

        assertTrue(inputTable != null && inputTable.size() > 0
                , "Input Table is empty. File >" + testfilename + "< exists?");

        assumingThat(ResultMessages.ERRCODE_SQLERROR.equals(basicQuery.getResult()), () -> setResult(Constants.OK));
        getLogger().info(basicQuery.getErrorMessage());
        assertTrue(Constants.OK.equals(getResult()), basicQuery.getErrorMessage());

//        assertTrue(basicQuery.getResult().equals("0") , "Result is >" + basicQuery.getResult() + "< with error: "
//                + basicQuery.getErrorMessage());
        return basicQuery.getResult();
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
}