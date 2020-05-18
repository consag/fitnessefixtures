package nl.jacbeekers.testautomation.fitnesse;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FitNesseTest {
    // FitNesse pages
    private String fitnesseFrontPage="src/test/resources/UnitTestRoot/FrontPage";
    private String testPage = null;
    private String pathTestPage = null;

    public FitNesseTest() {

    }
    public FitNesseTest(String testPage) {
        setTestPage(testPage);
        setPathTestPage(getFitnesseFrontPage() + getTestPage());
    }

    public List<List<String>> createTestTable() {
        return createTestTable(getPathTestPage());
    }

    public List<List<String>> createTestTable(String testfilename) {
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
                ArrayList<String> errorRow = new ArrayList<>();
                errorRow.add(Constants.ERROR);
                errorRow.add(Constants.FITNESSE_PREFIX_ERROR + e.toString());
                inputTable.add(errorRow);
            }
        }

        inputTable.remove(0); // contains input for the constructor

        return inputTable;
    }

    /*
     * Getters + Setters
     */
    public String getFitnesseFrontPage() {
        return fitnesseFrontPage;
    }

    public void setFitnesseFrontPage(String fitnesseFrontPage) {
        this.fitnesseFrontPage = fitnesseFrontPage;
    }

    public String getTestPage() {
        return testPage;
    }

    public void setTestPage(String testPage) {
        this.testPage = testPage;
    }

    public String getPathTestPage() {
        return pathTestPage;
    }

    public void setPathTestPage(String pathTestPage) {
        this.pathTestPage = pathTestPage;
    }
}
