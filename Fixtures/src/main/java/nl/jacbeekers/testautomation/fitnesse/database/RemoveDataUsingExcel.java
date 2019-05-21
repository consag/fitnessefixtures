/**
 * This purpose of this fixture is to delete a number of database rows using the FitNesse 'decision' table and an excel spreadsheet.
 * The input parameters are provided by a table in the FitNesse wiki. 
 * @author Edward Crain
 * @version 25 May 2015
 */
package nl.jacbeekers.testautomation.fitnesse.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.List;

import nl.jacbeekers.testautomation.fitnesse.supporting.Attribute;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.ExcelFile;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

public class RemoveDataUsingExcel {
    private String className="RemoveDataUsingExcel";
    private static String version = "20180621.2";
    private String logFileName = Constants.NOT_INITIALIZED;
    private String context = Constants.DEFAULT;
    private String startDate = Constants.NOT_INITIALIZED;
    private int logLevel = 3;
    private String logUrl=Constants.LOG_DIR;
    private String errorLevel = Constants.OK;
    private String errorMessage = Constants.NOERRORS;
    private boolean firstTime = true;

    ConnectionProperties connectionProperties = new ConnectionProperties();

    private String databaseName;
    private String tableName = Constants.NOT_PROVIDED;

    private String inputFile;

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
        log(myName, Constants.INFO, myArea, logMessage);
      }
              
    public void setTableName(String tableName) {
        String myName="setTableName";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.tableName=tableName;  
        logMessage="table name >" + tableName +"<.";
        log(myName, Constants.INFO, myArea, logMessage);
      }  

    public void setInputFile(String inputFile) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.inputFile=inputFile;  
        logMessage="input file >" + inputFile +"<.";
        log(myName, Constants.INFO, myArea, logMessage);
      }  

    public void setWorksheetName (String worksheetName) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;

        this.worksheetName=worksheetName;
        logMessage="worksheet name >" + worksheetName +"<.";
        log(myName, Constants.INFO, myArea, logMessage);
      }  
    
    public void setFilterFieldName (String filterFieldName) {
        String myName="setInputFile";
        String myArea="init";
        String logMessage = Constants.NOT_INITIALIZED;
    
        this.filterFieldName=filterFieldName;
        logMessage="filter field name>" + filterFieldName +"<.";
        log(myName, Constants.INFO, myArea, logMessage);
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
        log(myName, Constants.INFO, myArea, logMessage);
        if (columnNumber == Integer.MAX_VALUE){
            returnMessage="NOK; Entered filter field name not found in excel.";
            setErrorMessage(returnMessage);
        }
        else {      
            Connection connection=null;
            Statement statement = null;
            int updateQuery = 0; 
            
            try {
                myArea="readParameterFile";
                readParameterFile();
                log(myName, Constants.DEBUG, myArea, "Setting logFileName to >" + logFileName +"<.");
                connectionProperties.setLogFilename(logFileName);
                connectionProperties.setLogLevel(getIntLogLevel());

                myArea="SQL Execution";
                logMessage="Connecting to >" + connectionProperties.getActualDatabase() +"< using user >"
                        + connectionProperties.getDatabaseUsername() + "<.";
                log(myName, Constants.INFO, myArea, logMessage);

                connection = connectionProperties.getUserConnection();

                logMessage="SQL >" + "Deleting database rows " +"<.";
                log(myName, Constants.INFO, myArea, logMessage);
        
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
        log(myName, Constants.INFO, myArea, logMessage);
        return returnMessage;
      }

    public void getDatabaseColumnNumber (){
        String myName ="getDatabaseColumnNumber";
        String myArea ="run";

            //Function to get the column number based on the filter field name provided in FitNesse      
        Attribute attribute = new Attribute();
        String s;
        if(tableExcelFile.size() >0) {
            for (int i = 0; i < tableExcelFile.get(0).size(); i++) {
                // next column names
                attribute = tableExcelFile.get(0).get(i); //first column name
                if (attribute.getFormat() == "NUMERIC") {
                    s = String.valueOf(attribute.getNumber());
                } else { //String, boolean and others
                    s = attribute.getText();
                }

                if (s.equals(filterFieldName)) {
                    columnNumber = i;
                }
            }
        } else {
            log(myName, Constants.ERROR, myArea, "No rows found (tableExcelFile.size is 0).");
        }
      }

       private void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        log(myName, Constants.DEBUG, myArea,"getting properties for >" +databaseName +"<.");
        if(connectionProperties.refreshConnectionProperties(databaseName)) {
            log(myName, Constants.DEBUG, myArea,"username set to >" + connectionProperties.getDatabaseUsername() +"<.");
        } else {
            log(myName, Constants.ERROR, myArea, "Error retrieving parameter(s): " + connectionProperties.getErrorMessage());
        }

    }

    public void readExcelFile(){
            //Function to read the excel file
        String myName="readExcelFile";
        String myArea="reading";
        String logMessage = Constants.NO;

        ExcelFile excelFile=new ExcelFile();
        tableExcelFile=excelFile.readWorksheetWithNameIncludingFormat(inputFile, worksheetName);
        logMessage="Excel file >" + inputFile + "< retrieved with result >" +  excelFile.getReturnMessage() + "<.";
        log(myName, Constants.INFO, myArea, logMessage);
    }
    
    public void log(String name, String level, String area, String logMessage) {
        if (firstTime) {
            firstTime=false;
            if (context.equals(Constants.DEFAULT)) {
                    logFileName=startDate + "." + className;
            } 
            else {
                    logFileName=startDate +"." + context;
            }
        }
        Logging.LogEntry(logFileName, name, level, area, logMessage);
    }

    public String getLogFilename_v1() {
        return "<a href=" + logUrl + logFileName + ".log>"  + logFileName +"</a>";
    }

    public String getLogFilename() {
        if(logUrl.startsWith("http"))
            return "<a href=\"" +logUrl+logFileName +".log\" target=\"_blank\">" + logFileName + "</a>";
        else
            return logUrl+logFileName + ".log";
    }

    public void setLogLevel(String level) {
        String myName = "setLogLevel";
        String myArea = "determineLevel";

        logLevel = Constants.logLevel.indexOf(level.toUpperCase());
        if (logLevel < 0) {
            log(myName, Constants.WARNING, myArea, "Wrong log level >" + level + "< specified. Defaulting to level 3.");
            logLevel = 3;
        }

        log(myName, Constants.INFO, myArea,
                "Log level has been set to >" + level + "< which is level >" + getIntLogLevel() + "<.");
    }
    /**
     * @return - the log level
     */
    public String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return - the log level as Integer data type
     */
    public Integer getIntLogLevel() {
        return logLevel;
    }
    private void setErrorMessage(String errMessage) {
        String myName = "setErrorMessage";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorMessage = errMessage;
        logMessage="Error message has been set to >" + this.errorMessage + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    private void setErrorMessage(String level, String logMessage) {
        setErrorMessage(logMessage);
        setErrorLevel(level);
    }

    /**
     * @return - the error message (if any). If not "No errors encountered"  is returned (Check Constants for the actual value)
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private void setErrorLevel(String level) {
        String myName = "setModifyValue";
        String myArea = "run";
        String logMessage = Constants.NOT_PROVIDED;

        this.errorLevel = level;
        logMessage="Error level has been set to >" + this.errorLevel + "<.";
        log(myName, Constants.VERBOSE,myArea,logMessage);
    }

    /**
     * @return
     */
    public String getErrorLevel() {
        return errorLevel;
    }
    public static String getVersion() {
        return version;
    }

}