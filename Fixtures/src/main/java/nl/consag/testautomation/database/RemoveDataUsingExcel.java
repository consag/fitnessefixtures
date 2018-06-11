/**
 * This purpose of this fixture is to delete a number of database rows using the FitNesse 'decision' table and an excel spreadsheet.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain
 * @version 25 May 2015
 */
package nl.consag.testautomation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.List;

import nl.consag.testautomation.supporting.Attribute;
import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.ExcelFile;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

public class RemoveDataUsingExcel {
    private String className="RemoveDataUsingExcel";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private boolean firstTime=true;
    private boolean verbose=false;

    private String logDirAccess = Constants.LOG_DIR;
    private String yes = Constants.YES;

    private String driver;
    private String url;
    private String userId;
    private String password;
    
    private String tableOwner = Constants.NOT_INITIALIZED;
    private String tableOwnerPassword = Constants.NOT_INITIALIZED;
    private String tableName;
    private String databaseName;
    private String query;
    
    private String inputFile;
    private String databaseType;
    private String databaseConnDef;

    private List<List<Attribute>> tableExcelFile;
    private String worksheetName;
    private String filterFieldName;
    private int columnNumber = Integer.MAX_VALUE;
    
    public String returnMessage = ""; //text message that is returned to FitNesse  
    
    public RemoveDataUsingExcel(String pContext) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context=pContext;
    }
    
    public RemoveDataUsingExcel() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context=className;
    }

    public void setDatabaseName(String databasename) {
    String myName="setDatabaseName";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.databaseName=databasename;  
        logMessage="database name >" + databaseName +"<.";
        log(myName, "info", myArea, logMessage);
      }
              
    public void setTableName(String tableName) {
        String myName="setTableName";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.tableName=tableName;  
        logMessage="table name >" + tableName +"<.";
        log(myName, "info", myArea, logMessage);
      }  

    public void setInputFile(String inputFile) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.inputFile=inputFile;  
        logMessage="input file >" + inputFile +"<.";
        log(myName, "info", myArea, logMessage);
      }  

    public void setWorksheetName (String worksheetName) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.worksheetName=worksheetName;
        logMessage="worksheet name >" + worksheetName +"<.";
        log(myName, "info", myArea, logMessage);
      }  
    
    public void setFilterFieldName (String filterFieldName) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.filterFieldName=filterFieldName;
        logMessage="filter field name>" + filterFieldName +"<.";
        log(myName, "info", myArea, logMessage);
    }  
      
    public String result ()  {
    //Function submit the delete SQL statement
        String myName="deleteQuery-result";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
        String fieldData;

        readParameterFile();
        readExcelFile();
        getDatabaseColumnNumber();
                    
        logMessage="Method: DeleteQuery.";
        log(myName, "info", myArea, logMessage);
        if (columnNumber == Integer.MAX_VALUE){
            returnMessage="NOK; Entered filter field name not found in excel.";                
        }
        else {      
            Connection connection=null;
            Statement statement = null;
            int updateQuery = 0; 
            
            try {
                myArea="SQL Execution";
                logMessage="Connecting to >" + databaseConnDef +"< using userID >" + tableOwner + "<.";
                log(myName, "info", myArea, logMessage);
                connection=DriverManager.getConnection(url, tableOwner, tableOwnerPassword);    
                logMessage="SQL >" + "Delete database rows " +"<.";
                log(myName, "info", myArea, logMessage);
        
                for (int row=1; row < tableExcelFile.size(); row++) { 
                    Attribute attribute = new Attribute();
                    attribute = tableExcelFile.get(row).get(columnNumber);
                    if (attribute.getFormat()=="NUMERIC"){
                        fieldData=String.valueOf(attribute.getNumber()); 
                    }
                    else{
                        fieldData=attribute.getText();
                    }

                    PreparedStatement st = connection.prepareStatement("DELETE FROM " + tableName + " WHERE "+filterFieldName+" = ?");
                    st.setString(1,fieldData);
                    st.executeUpdate(); 
                }
                connection.close();
                returnMessage = "OK";
            }  
            catch (SQLException e) {
                myArea="Exception handling";
                logMessage="SQLException at DELETE >" + e.toString();
                log(myName, "WARNING", myArea, logMessage);
                returnMessage=logMessage;
            }
        }
        logMessage="Message returning to FitNesse > " + returnMessage + "<.";     
        log(myName, "info", myArea, logMessage);
        return returnMessage;
      }

    public void getDatabaseColumnNumber (){
            //Function to get the column number based on the filter field name provided in FitNesse      
        Attribute attribute = new Attribute();
        String s;
        for (int i=0; i < tableExcelFile.get(0).size(); i++)  {
        // next column names
            attribute=tableExcelFile.get(0).get(i); //first column name
            if (attribute.getFormat()=="NUMERIC"){
                s=String.valueOf(attribute.getNumber()); 
            }
            else{ //String, boolean and others
                s=attribute.getText();
            }
            
            if (s.equals(filterFieldName)){
                columnNumber=i;
            }
        }               
      }
    
    public void readParameterFile(){
        //Function to read the parameters in a parameter file
        String myName="readParameterFile";
        String myArea="reading";
        String logMessage = Constants.NO;

        databaseType= GetParameters.GetDatabaseType(databaseName);
        logMessage="Database type: " + databaseType;
        log(myName, "info", myArea, logMessage);

        databaseConnDef=GetParameters.GetDatabaseConnectionDefinition(databaseName);
        logMessage="Database connection definition: " + databaseConnDef;
        log(myName, "info", myArea, logMessage);

        driver=GetParameters.GetDatabaseDriver(databaseType);
        logMessage="driver: " + driver;
        log(myName, "info", myArea, logMessage);
        url=GetParameters.GetDatabaseURL(databaseConnDef);

        tableOwner=GetParameters.GetDatabaseTableOwnerName(databaseName);
        logMessage="Table Owner UserID >" + tableOwner + "<.";
        log(myName, "info", myArea, logMessage);

        tableOwnerPassword=GetParameters.GetDatabaseTableOwnerPWD(databaseName);         
        logMessage="Password for user >" + tableOwner + "< retrieved.";
        log(myName, "info", myArea, logMessage);
    
        userId=GetParameters.GetDatabaseUserName(databaseName);
        logMessage="User UserID >" + userId + "<.";
        log(myName, "info", myArea, logMessage);
        password=GetParameters.GetDatabaseUserPWD(databaseName);
        logMessage="Password for user >" + userId + "< retrieved.";
        log(myName, "info", myArea, logMessage);
    }
    
    public void readExcelFile(){
            //Function to read the excel file
        String myName="readExcelFile";
        String myArea="reading";
        String logMessage = Constants.NO;

        ExcelFile excelFile=new ExcelFile();
        tableExcelFile=excelFile.readWorksheetWithNameIncludingFormat(inputFile, worksheetName);
        logMessage="Excel file >" + inputFile + "< retrieved.";
        log(myName, "info", myArea, logMessage);
    }
    
    public void log(String name, String level, String area, String logMessage) {
        if (firstTime) {
            firstTime=false;
            if (context.equals(Constants.DEFAULT)) {
                    logFileName=startDate + "." + className;
            } 
            else {
                    logFileName=context + "." + startDate;
            }
        }
        Logging.LogEntry(logFileName, name, level, area, logMessage);
    }

    public String getLogFilename() {
        return "<a href=" + logDirAccess + logFileName + ".log>"  + logFileName +"</a>";
    }

    public void setVerboseLogging(String val) {
        if(val.equals(yes)) {
            verbose=true; 
        } 
        else {
            verbose=false;
        }
    }
}