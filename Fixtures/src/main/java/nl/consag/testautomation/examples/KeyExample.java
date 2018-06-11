/**
 * This fixture for demo purposes only. Always returns "pass" to fitnesse table using the FitNesse slim 'table' table.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain
 * @version 11 October 2014
 */
package nl.consag.testautomation.examples;

import java.util.ArrayList;
import java.util.List;


public class KeyExample {	  

	  private List<List<String>> returnTableToFitNesse = new ArrayList<List<String>>();  

	  public List doTable(List<List<String>> input_table) {   
		  //Main function; checks input table and populates output table
		  for (int i = 0; i < input_table.size(); ++i)  {
			  getTextAndReturnPass (input_table.get(i));
		  }
		  return returnTableToFitNesse;
	  }

	  public void getTextAndReturnPass (List<String> inputFitNesseRow){	 
		  List<String> returnRowToFitNesse = new ArrayList<String>();	 

		  returnRowToFitNesse.add("no change"); //return "no change" in first cell

		  for (int i = 1; i < inputFitNesseRow.size(); ++i)  
		  {	
			  returnRowToFitNesse.add("pass"); //return "pass" in next cell
		  } 		
		  addRowToOutputFitNesse (returnRowToFitNesse); //return row with outcomes; pass/fail
	  }  

	  public void addRowToOutputFitNesse (List <String> row) {
		  returnTableToFitNesse.add(row);
	  } 
}
