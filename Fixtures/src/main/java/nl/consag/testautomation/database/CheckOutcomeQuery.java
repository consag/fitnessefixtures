/**
 * This purpose of this fixture is to compare the expected outcome of a query with the actual outcome using the FitNesse slim 'table' table.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain
 * @version 11 October 2014
 */
package nl.consag.testautomation.database;

import java.io.*;
import java.sql.*;
import java.util.*;

public class CheckOutcomeQuery {
	  	  private boolean error=false;
	  	  private String errorMessage="";
	
		  private int NO_FITNESSE_ROWS_TO_SKIP = 2;
				  
		  private String databaseName;
		  private String driver;
		  private String url;
		  private String query;
		  
		  private int numberOfRowsFitNesseTable;
		  private int numberOfTableColumnsFitNesseTable;

		  private List<List<String>> returnTableToFitNesse = new ArrayList<List<String>>();  
		  //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")

		  public List doTable(List<List<String>> inputTable) {    //the input table contains the rows (=first index) and column (second index) of the FitNesse table
			//Main function; checks input table and populates output table
			  numberOfRowsFitNesseTable = inputTable.size(); 
			  numberOfTableColumnsFitNesseTable = inputTable.get(1).size();
			  List<String> rowOfErrormessage = new ArrayList<String>();
			  
			  getDatabaseName (inputTable.get(0));  		  	
			  getQuery (inputTable.get(1));  

			  CompareExpectedTableWithDatabaseTable(inputTable, getDatabaseTable());  

			  if (error){
				  rowOfErrormessage.add(errorMessage);
				  addRowToOutputFitNesse(rowOfErrormessage);		
			  }		  
			  return returnTableToFitNesse;
		  }

		  public void getDatabaseName (List<String> inputFitNesseRow){
			  List<String> returnRowToFitNesse = new ArrayList<String>();
			  databaseName = inputFitNesseRow.get(1); //read first row second column of FitNesse table
			  returnRowToFitNesse.add("pass"); //return "pass" after for first cell
			  returnRowToFitNesse.add("pass"); //return "pass" after for second cell
			  addRowToOutputFitNesse(returnRowToFitNesse);
		  }
		  
		  public void getQuery (List<String> inputFitNesseRow){
			  List<String> returnRowToFitNesse = new ArrayList<String>();
			  query = inputFitNesseRow.get(1); //read second row second column of FitNesse table
			  returnRowToFitNesse.add("pass"); //return "pass" after for first cell
			  returnRowToFitNesse.add("pass"); //return "pass" after for second cell
			  addRowToOutputFitNesse (returnRowToFitNesse);
		  }
		  
		  public void CompareExpectedTableWithDatabaseTable(List<List<String>> expectedTableFitNesse, List<List<String>> databaseTable){  
			  //Compare input FitNesse table with database table
			  List<String> emptyRow = new ArrayList<String>();	 	  
			  if (!error){			 //Skip section if an database error occurred
				  for (int i = 0; i < numberOfTableColumnsFitNesseTable; i++)  
				  {	// fill empty row with spaces 
					  emptyRow.add("");
				  }
				  if((expectedTableFitNesse.size()-NO_FITNESSE_ROWS_TO_SKIP) >= databaseTable.size()){
					  // Expected result is equal or greater than result rows from database, the expected table size is - 3, since first 3 rows contain database query and column names
					  for (int i = 0; i < (expectedTableFitNesse.size()-NO_FITNESSE_ROWS_TO_SKIP); i++)  
					  {	// less rows in database than expected
						  	if (i < databaseTable.size()){
								CompareExpectedRowWithDatabaseRow(expectedTableFitNesse.get(i+NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
						  	}
							else{
								CompareExpectedRowWithDatabaseRow(expectedTableFitNesse.get(i+NO_FITNESSE_ROWS_TO_SKIP), emptyRow);
							}
					  }
				  }
				  else{
					  for (int i = 0; i < databaseTable.size(); i++)  
					  {	// more rows in database than expected
							if (i < (expectedTableFitNesse.size()-NO_FITNESSE_ROWS_TO_SKIP)){
								CompareExpectedRowWithDatabaseRow(expectedTableFitNesse.get(i+NO_FITNESSE_ROWS_TO_SKIP), databaseTable.get(i));
							}
							else{
								CompareExpectedRowWithDatabaseRow(emptyRow, databaseTable.get(i));
							}
					  }
				  }	  
			  }
		  }

		  public void CompareExpectedRowWithDatabaseRow(List<String> expectedRowFitNesse, List<String> databaseRow){ 
			  //Function to compare input row of FitNesse table with database row
			  List<String> returnRowToFitNesse = new ArrayList<String>();	 		  		
				  if (expectedRowFitNesse.get(0).equals("")) {
				 	  if (databaseRow.get(0).equals("")) {
						  //do nothing
					  }
					  else {
						  returnRowToFitNesse.add("fail: Found extra row"); 				//return "failed" to first cell 
						  for (int i = 1; i < numberOfTableColumnsFitNesseTable; ++i)  
						  {	// set value for next column cell 
							  returnRowToFitNesse.add("fail: surplus: " + databaseRow.get(i)); //return "fail" in next cell with the found value
						  }
					  }		  
				  }
				  else {
					  returnRowToFitNesse.add("pass"); //return "pass" in first cell
					  if (databaseRow.get(0).equals("")) {
						  for (int i = 1; i < numberOfTableColumnsFitNesseTable; ++i)  
						  {	// set value for next column cell
							  returnRowToFitNesse.add("fail: expected: " + expectedRowFitNesse.get(i)); //return "fail" in next cell with the found value
						  } 		
					  }
					  else
					  {	//Compare cell for cell if expected equals outcome
						  for (int i = 1; i < numberOfTableColumnsFitNesseTable; ++i)  
						  {	// set value for next column cell
							  if (expectedRowFitNesse.get(i).equals(databaseRow.get(i))){
								  returnRowToFitNesse.add("pass"); //return "pass" in next cell if expected = result
							  }
							  else {
								  returnRowToFitNesse.add("fail: expected: " + expectedRowFitNesse.get(i) + " found: " + databaseRow.get(i)); //return "fail" in next cell with the found value  
							  }
						  } 				  
					  }
				  }	  
				  addRowToOutputFitNesse (returnRowToFitNesse);
			  }

		  public List<List<String>> getDatabaseTable (){
			  //Function to check the database based on input in FitNesse table row
 			  	getDatabaseParameters();
			  	printUsedParameters();
			  
			  //attributes for internal database table 
				 List<List<String>> databaseTable = new ArrayList<List<String>>();

				 //attributes for reading database
				 Connection connection = null;
				 Statement statement = null;
				 ResultSet resultSet = null;
				  
				 try {
				    // Load the JDBC driver or oracle.jdbc.driver.OracleDriver or sun.jdbc.odbc.JdbcOdbcDriver
				    Class.forName(driver);
				    // Create a connection to the database
				    connection = DriverManager.getConnection(url);
				    // createStatement() is used for create statement object that is used for sending sql statements to the specified database.
				    statement = connection.createStatement();
				    // sql query of string type to read database
				    resultSet = statement.executeQuery(query);    
					    
					//Loop through the results
				    while (resultSet.next()) {

				    	List<String> databaseRow = new ArrayList<String>(); // initialize list to be reused
				    	databaseRow.add("result query"); //first cell will consist of a fixed value "column name"
				    	//Add db result row (=multiple field) into FitNesse results array			    	
				    	for (int j = 1; j < numberOfTableColumnsFitNesseTable; j++)  
				    	{
				    		if (resultSet.getString(j) == null) {
				    			databaseRow.add(""); //string should be filled with empty spaces otherwise a java null exception is created
				    		}
				    		else {
				    			databaseRow.add(resultSet.getString(j));
				    		}   						    		
				    	}     	
				    	databaseTable.add(databaseRow);
					 }

				 	System.out.println("Number of database rows found: " + Integer.toString(databaseTable.size()));
				 	
			    	statement.close();
			    	connection.close();		    
					} 
				 	catch (ClassNotFoundException e) {
				 		error=true;
				 		System.out.println("Class not found : " + e);
					 	errorMessage = errorMessage + " \nClass no found: " + e; 
					} 
				 	catch (SQLException e) {
				 		error=true;
				 		System.out.println("SQLException : " + e);
				 		errorMessage = errorMessage + " \nSQLException : " + e;
					}
				 	return databaseTable;
			  }     

		  public void addRowToOutputFitNesse (List <String> row) {
  			  //A row contains cells with either "pass" (= green), or "fail" (= red).
			  returnTableToFitNesse.add(row);
		  } 

		  public void printUsedParameters() {
			  System.out.println("\n====================== Check database table fixture ======================");
			  System.out.println("Database: " + databaseName);
			  System.out.println("Query: " + query);
			  System.out.println("Number of expected database fields: " + Integer.toString(numberOfTableColumnsFitNesseTable-1));
			  System.out.println("Number of rows expected in database: " + Integer.toString(numberOfRowsFitNesseTable-NO_FITNESSE_ROWS_TO_SKIP)); 
		  }	    
			  	  
		  public void getDatabaseParameters(){	 
			  	try{
			  		File file = new File("conf/database.properties");
					FileInputStream fileInput = new FileInputStream(file);
					Properties properties = new Properties();
					properties.load(fileInput);
					fileInput.close();

					Enumeration enuKeys = properties.keys();
					String temp[];  	
					while (enuKeys.hasMoreElements()) {
						String key = (String) enuKeys.nextElement();
						String databaseProperties = properties.getProperty(key);
						  temp = databaseProperties.split(";");						 
						  if (temp[0].equals(databaseName)){
							  driver = temp [1];
							  url = temp [2];									  
						  		}		  		
							}
					    }
				catch (Exception e){//Catch exception if any
					  error=true;
					  System.err.println("Error reading database parameter file: " + e.getMessage());
					  errorMessage= errorMessage + " \nFile error : Reading database parameter file: " + e.getMessage();
				}
			 }	 
}
