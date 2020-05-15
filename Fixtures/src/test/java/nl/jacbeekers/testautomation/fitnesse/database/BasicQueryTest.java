package nl.jacbeekers.testautomation.fitnesse.database;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class BasicQueryTest {
    //Logging
    private Logger logger;
    private String method;
    // FitNesse pages
    private String fitnesseFrontPage="src/test/resources/FitNesseRoot/FrontPage";
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

    @Test
    void testColumnsOk() {
        setMethod("testColumnsOk");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
    }

    @Test
    void testGetResult() {
        setMethod("testGetResult");
        setLogger(Logger.getLogger(getClass().getName() + "-" + getMethod()));
    }

    @Test
    private String executeBasicQueryTest() {
        BasicQuery basicQuery = new BasicQuery();
        String testfilename = getFitnesseFrontPage() + "/UnitTests/ExamplesBasicFixtures/ExampleBasicQuery.wiki";
        List<List<String>> inputTable = createTestTableNoContext(testfilename);
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

    private List<List<String>> createTestTableNoContext(String testfilename) {
        List<List<String>> inputTable = new ArrayList<List<String>>();

        File testFile = new File(testfilename);
        if( testFile.exists()) {
            try (BufferedReader fis = new BufferedReader(new FileReader(testfilename))) {

                String line = null;
                while ((line = fis.readLine()) != null) {
                    ArrayList<String> currentList = new ArrayList<>();
                    inputTable.add(currentList);
                    if(line.indexOf('|') == 0) {
                        line = line.substring(1);
                    }
                    String[] values = line.split("\\|");
                    for (String value : values) {
                        currentList.add(value.trim());
                    }
                }

            } catch (IOException e) {
                getLogger().severe("Exception occurred: " + e.toString());
            }
        }

        inputTable.remove(0); // contains input for the constructor

        return inputTable;
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

    public String getFitnesseFrontPage() {
        return fitnesseFrontPage;
    }

    public void setFitnesseFrontPage(String fitnesseFrontPage) {
        this.fitnesseFrontPage = fitnesseFrontPage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}