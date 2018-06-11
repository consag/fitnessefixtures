/**
 * This fixture for demo purposes only. Always returns the contents of an excel-file in the xlsx-format.
 * @author Edward Crain
 * @version 22 February 2015
 */
package nl.consag.testautomation.excel;

import java.util.ArrayList;
import java.util.List;

import nl.consag.testautomation.supporting.ExcelFile;

public class ReadExcelFileWithWorksheetNumber {
	private List<List<String>> tableExcelFile;
	private List<List<String>> returnTableToFitNesse = new ArrayList<List<String>>();  


        public List doTable(List<List<String>> inputTable) { 
            //Main function; checks input table and populates output table                                                                                                   
            ExcelFile excelFile = new ExcelFile();
            String fileName = inputTable.get(0).get(0);
            String excelTabNo = inputTable.get(0).get(1);

            tableExcelFile = excelFile.readWorksheetWithNumber(fileName, Integer.parseInt(excelTabNo));
 
            for (int i = 0; i < tableExcelFile.size(); ++i)  {
                  getTextAndReturnPass (tableExcelFile.get(i));
            }   
            return returnTableToFitNesse;
        }
        
          public void getTextAndReturnPass (List<String> inputRow){      
                  List<String> returnRowToFitNesse = new ArrayList<String>();    

                  for (int i = 0; i < inputRow.size(); ++i)  
                  {     
                          returnRowToFitNesse.add("report: " + inputRow.get(i)); //return "ignore" in next cell + value
                  }             
                  addRowToOutputFitNesse (returnRowToFitNesse); //return row with outcomes; pass/fail
          }  

          public void addRowToOutputFitNesse (List <String> row) {
                  returnTableToFitNesse.add(row);
          } 

}
