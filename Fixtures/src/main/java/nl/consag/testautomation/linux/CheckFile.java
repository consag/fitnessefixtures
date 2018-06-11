/*
 * @author Jac. Beekers
 * @version 21 November 2015
 * @since July 2015
 */

package nl.consag.testautomation.linux;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.supporting.GetParameters;
import nl.consag.testautomation.supporting.Logging;

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.*;


public class CheckFile {

  private static String version ="20151121.0";
  private String className = "CheckFile";

  private String logFileName = Constants.NOT_INITIALIZED;
  private String context = Constants.DEFAULT;
  private String startDate = Constants.NOT_INITIALIZED;
  private int logLevel =3;
  private int logEntries =0;
  protected String result = Constants.OK;
  protected String errorMessage = Constants.NO_ERRORS;
  protected String resultMessage = Constants.OK;
  
  
  //check for parameters
  protected String paramDelimiter = Constants.PARAM_DELIMITER;
  private String delimiter = Constants.NOT_FOUND;
//  protected String force="Force=";
  
  protected String sAnyValue ="#ANYVALUE#";

    private String fileName = Constants.NOT_INITIALIZED;
    private String logicalDirectory = Constants.NOT_INITIALIZED;
    private String testdata;
    private File inFile;
    private String filePath = Constants.NOT_INITIALIZED;
    private boolean fileOK=false;
    private String escapedDelimiter = Constants.NOT_INITIALIZED;  // needed to Java regexp functions
    private String filter = Constants.NOT_INITIALIZED;
    private List<String[]> filterValuePairs = new ArrayList<String[]>(); //list with all filter value pairs
    private String[] headerFieldsArray;
    private List<String> headerFieldsList;
    List<String> indexFilterFields = new ArrayList<String>();
    
    private int rowCount=0;
    private int nrHeaderFields=0;
    private int nrRowsMatched=0;
    private String expectedResult = Constants.OK;
    private boolean expectedResultProvided =false;

    //the return table, returns the outcome of fixture (="pass", "fail", "ignore", "no change")
    private List<List<String>> return_table = new ArrayList<List<String>>();


  // ==========================================================================
  public CheckFile() {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      context=className;
      logFileName = startDate + "." + className +"." + context;
      this.expectedResult = Constants.OK;
      this.expectedResultProvided=false;

  }

  // ==========================================================================
  public CheckFile(String context) {
    java.util.Date started = new java.util.Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    startDate = sdf.format(started);
    this.context=context;
      logFileName = startDate + "." + className +"." + context;
      this.expectedResult = Constants.OK;
      this.expectedResultProvided=false;
  }

    // ==========================================================================
    public CheckFile(String context, String logLevel) {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      this.context=context;
        logFileName = startDate + "." + className +"." + context;
        setLogLevel(logLevel);
        this.expectedResult = Constants.OK;
        this.expectedResultProvided=false;
    }

    // ==========================================================================
    public CheckFile(String context, String logLevel, String expectedResult) {
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      this.context=context;
        logFileName = startDate + "." + className +"." + context;
        setLogLevel(logLevel);
      this.expectedResult =expectedResult;
      this.expectedResultProvided=true;
    }

    //----------------------------------------------------------
    //Main function; checks input table and populates output table
    //----------------------------------------------------------

    // ==========================================================================
    public List<List<String>> doTable(List<List<String>> input_table) { //the input table contains the rows (=first index) and column (second index) of the fitnesse table
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="doTable";
    String myArea="input";
    String rcProc = Constants.NOT_INITIALIZED;

    if(input_table.size() <3) {
      List<String> add_row = new ArrayList<String>();
      logMessage="input table has not enough rows. Found =>" + Integer.toString(input_table.size()) + "<. Expected at least =>3<.";
      log(myName, Constants.ERROR, myArea, logMessage);
      setError(Constants.ERROR, logMessage);
      add_row.add("fail:" +logMessage);
      addRowToReturnTable (add_row);
      addLogFilename();
      return return_table;
      
    }

        //read first row in fitnesse table
    if(input_table.get(0).size() < 3) {
      List<String> add_row = new ArrayList<String>();
      logMessage="input table has not enough columns in file name row. Found =>" + Integer.toString(input_table.get(0).size()) + "<. Expected =>3<.";
      log(myName, Constants.ERROR, myArea, logMessage);
      setError(Constants.ERROR, logMessage);
      add_row.add("fail:" +logMessage);
      addRowToReturnTable (add_row);
      addLogFilename();
      return return_table;
    }


      //0= "file name"
      //1= the actual file name
      //2= logical directory name
      //3= delimiter - default =";"
        fileName = input_table.get(0).get(1);
      logicalDirectory = input_table.get(0).get(2);
      logMessage="input file name =>" + fileName + "<. Logical directory =>" + logicalDirectory +"<.";
      log(myName, Constants.DEBUG, myArea, logMessage);
      
        //additional parameters specified
      //set defaults
      delimiter = Constants.FIELD_DELIMITER;
      escapedDelimiter = Constants.FIELD_DELIMITER;
      
        for(int i=3; i < input_table.get(0).size() ; ++i) {
          if(input_table.get(0).get(i).startsWith(Constants.PARAM_DELIMITER)) {
            delimiter = input_table.get(0).get(i).substring(Constants.PARAM_DELIMITER.length());
            escapedDelimiter = escape_for_regexp(delimiter);
          }
        }
        logMessage="Delimiter has been set to =>" + delimiter + "<.";
      log(myName, Constants.DEBUG, myArea, logMessage);
      
        fileOK =false;
        rowCount=0;

      // check if input table has a filter specified
      // the filter must be a row AFTER the file name row in the fitnesse table
      // the first word in the row must be "filter"
        filter=determineFilter(input_table.get(1));

        //check input file
        addRowToReturnTable(checkInputFile(logicalDirectory,fileName));
        List<String> add_row = new ArrayList<String>();
        if (fileOK) {
          // read entire file, determine result, and generate fitnesse table rows
          rcProc = processFile(input_table); 
          addRowToReturnTable (add_row);
        } else {
          List<String> empty_row = new ArrayList<String>();
          addRowToReturnTable (empty_row);
          addRowToReturnTable (empty_row);
          addRowToReturnTable (empty_row);

        }

        if(fileOK && (nrRowsMatched ==0)) {
          logMessage="No row matched the specified filter.";
          log(myName, Constants.ERROR, myArea, logMessage);
          setError(Constants.ERROR, logMessage);
          List<String> err_row = new ArrayList<String>();
          if(getExpectedResult().equals(Constants.ERROR)) {
            err_row.add("pass:" +logMessage);
          } else {
              err_row.add("fail:" +logMessage);      
          }
          addRowToReturnTable (err_row);
        } 
        
      logMessage="Process completed.";
      log(myName, Constants.INFO, myArea, logMessage);
    
      if(expectedResultProvided) {
          addResult();
      }
    addLogFilename();
    
    return return_table;
    }

// ==========================================================================
// determineFilter
    private String determineFilter (List<String> filter_row) {
  // check if input table has a filter specified
  // the filter must be a row AFTER the file name row in the fitnesse table
  // the first word in the row must be "filter"
  String logMessage = Constants.NOT_INITIALIZED;
  String myName="determineFilter";
  String myArea="input";
  String possibleCriteria = Constants.NOT_INITIALIZED;
  boolean operatorFound=false;
  String [] filterValuePair;
  String rc = Constants.NOT_PROVIDED;

  if(Constants.FILTER_INDICATOR.equalsIgnoreCase(filter_row.get(0))) {
    possibleCriteria = filter_row.get(1);
    operatorFound = possibleCriteria.contains("=");
    while (operatorFound) {
        filterValuePair =possibleCriteria.split("=", 2);
        if(filterValuePair[1].contains(" #AND# ")) {
            String[] thisValue = filterValuePair[1].split(" #AND# ", 2);
            filterValuePair[1]=thisValue[0];
            possibleCriteria =thisValue[1];
        } else {
            possibleCriteria =filterValuePair[1];
        }
        logMessage="Found filter column =>" +filterValuePair[0] +"< and value =>" + filterValuePair[1] +"<.";
        log(myName, Constants.DEBUG, myArea, logMessage);
        filterValuePairs.add(filterValuePair);

        operatorFound = possibleCriteria.contains("=");
    }
    rc=filterValuePairs.toString();
    
  }
  else {
    rc = Constants.NOT_PROVIDED;
    logMessage="No filter specified";                
    log(myName, Constants.INFO, myArea, logMessage);        
  }
  
  return rc;
}

// ==========================================================================
//adds log file name to fitnesse table
    private void addLogFilename () {

      List<String> add_row = new ArrayList<String>();
      add_row.add("report:log file");
      add_row.add("report:"+getLogFilename());

      addRowToReturnTable (add_row);
      
    }
//
    
    /*
     * processFileStatements
     * Purpose: 
     */
// ==========================================================================
    // process rows in file
    // check them
    // report back to fitnesse
  private String processFile(List<List<String>> input_table) {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="inputFile";
    String myArea="initialization";
    String theLine = Constants.NOT_INITIALIZED;
    String[] sRowFields;
    List<String> rowFields;
    String msg = Constants.OK;

    List<String> return_row = new ArrayList<String>();
    List<String> filter_row = new ArrayList<String>();
    List<String> record_row;
    
    boolean matches =true;
    
//      String sToRead;
//      StringBuilder sb = new StringBuilder();      
//      for (String s : pToRead) {
//        sb.append(s);
//        sb.append(m_delimiter);
//      }
//      sToRead = sb.toString();
      
      boolean bOK = false;

      myArea="ReadFromFile";

      bOK=true;
      try {
          BufferedReader ins = new BufferedReader(new FileReader(filePath));
          //read header first
        if((theLine=ins.readLine()) != null) {
          headerFieldsArray = theLine.split(escapedDelimiter);
          
          headerFieldsList = Arrays.asList(headerFieldsArray);
          nrHeaderFields =headerFieldsList.size();
          logMessage="Found >" + nrHeaderFields + "< header fields in this line =>" + theLine + "<. The first one =>" + headerFieldsList.get(0) + "<.";
          log(myName, Constants.INFO, myArea, logMessage);
        }
        else {
          logMessage="Could not read first line. File empty?";
          msg=logMessage;
          log(myName, Constants.ERROR, myArea, logMessage);
          setError(Constants.ERROR, logMessage);
          bOK=false;
        }
        
        if(bOK && ( !Constants.NOT_PROVIDED.equals(filter))) {
        // Check if filter column actually is one of the header fields in the file
          if(checkFilterColumn()) {
            filter_row.add("");
            filter_row.add("pass");
            addRowToReturnTable(filter_row); 
            setResult(Constants.OK, Constants.NOERRORS);
          } else {
          msg="Field in filter NOT found in file.";
          filter_row.add("");
            if(getExpectedResult().equals(Constants.ERROR)) {
              filter_row.add("pass:" +logMessage);
            } else {
                filter_row.add("fail:" + msg);
            }
          addRowToReturnTable(filter_row);    
          bOK=false;
          setError(Constants.ERROR, msg);
        }
        }

      // the next line in the fitnesse table is the line with the headers.
        // need to output this as-is
        record_row = new ArrayList<String>();
        addRowToReturnTable(record_row);    
        
        rowCount=0;
        while ((theLine = ins.readLine()) != null) {
          ++rowCount;
            logMessage="processing file record >" + rowCount + "<.";
              log(myName, Constants.DEBUG, myArea, logMessage);

          sRowFields=theLine.split(escapedDelimiter);
//          if(sRowFields.length != m_nrHeaderFields) {
//            logMessage="Row >" + m_rowCount + "< contains >" + sRowFields.length + "< fields, but there were >" + m_nrHeaderFields + "< header fields. This may occur if the last field is empty.";
//            log(myName, sWarning, myArea, logMessage);
//          }
                    

// Check if row in file matches filter
            matches =checkRowOnFilter(theLine);
            if(matches) {
//          if(sRowFields[indexField].equals(filterValuePair[1])) {
            //row matches filter
            ++nrRowsMatched;
            logMessage="Record >" + rowCount + "< passed filter.";
              log(myName, Constants.DEBUG, myArea, logMessage);

            record_row = new ArrayList<String>();
            //compare fitnesse columns with field values in file
            myArea="compare record";
            logMessage="fields in file record =>" + sRowFields.length + "<. FitNesse table row 3 contains >" + input_table.get(3).size() + "<.";
              log(myName, Constants.DEBUG, myArea, logMessage);

            for(int i=0; i < sRowFields.length ; ++i) {
//fitnesse table has only 1 row to check              
              if(i<input_table.get(3).size()) {
                if ( input_table.get(3).get(i).equals(sAnyValue) || sRowFields[i].equals(input_table.get(3).get(i))) {
                  record_row.add("pass:" + sRowFields[i]);
                } else {
                  //22-Oct-14: Request by Jaap Wille: If file field is empty, an expected value of ' ' (one space) should also be okay.
                  if(sRowFields[i].isEmpty() && input_table.get(3).get(i).equals(" ")) {
                    record_row.add("pass:FieldIsEmpty");
                  } else {
                      logMessage="Found >" +sRowFields[i] + "<, expected >" + input_table.get(3).get(i) +"<.";
                      if(getExpectedResult().equals(Constants.ERROR)) {
                        record_row.add("pass:" +logMessage);
                      } else {
                          record_row.add("fail:" +logMessage);
                      }
                        setError(Constants.ERROR,"values found do not match expectation");
                  }
                }
              } else {
                //more fields in file than in input table and this is okay
                logMessage="Row >" + rowCount + "< contains more fields than listed in fitnesse table. This is considered to be okay.";
                log(myName, Constants.WARNING, myArea, logMessage);
                  setResult(Constants.WARNING, logMessage);
              }
            }
            addRowToReturnTable(record_row);                
 
          } else {
            //row does not match filter -- ignore
            logMessage="Row >" +rowCount + "< does not match the specified filter. Row will be ignored.";
            log(myName, Constants.DEBUG, myArea, logMessage);
          }
          
          } 



      } catch (FileNotFoundException e) {
        myArea="Exception Handler";
        bOK =false;
        logMessage= "File =>" +  filePath + "< could not be handled. Error =>" + e.getMessage() +"<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return_row.add("fail: File handle error");
        setError(Constants.ERROR, logMessage);
      } 
      catch (IOException e) {
        myArea="Exception Handler";
          bOK =false;
        logMessage= "Could not read from file =>" +  filePath + "<. Error =>" + e.getMessage() +"<.";
        log(myName, Constants.ERROR, myArea, logMessage);
        return_row.add("fail: Could not read from file");
        setError(Constants.ERROR, logMessage);
      }

      if (bOK) {
        return_row.add("pass");
      } else {
        return_row.add("fail:" + msg);
      }

    return msg;
    
  }

  private boolean checkRowOnFilter(String theLine) {
    String [] rowFields;
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="checkRowOnFilter";
    String myArea="init";
    int match=0;
    boolean rc =false;
    
    rowFields=theLine.split(escapedDelimiter);
    for( int i =0 ; i < indexFilterFields.size() ; i++ ) {
        if (rowFields[i].equals(filterValuePairs.get(i)[1])) {
            logMessage="row field# >" +Integer.toString(i) +"< has value >" + rowFields[i] 
                       +"< which matches filter field >" +filterValuePairs.get(i)[0] +"< that has value >" + filterValuePairs.get(i)[1] +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            match++;
        }
    }
    
    if(match == indexFilterFields.size()) {
        rc =true;
    } else {
        rc =false;
    }

      return rc;
      
  }
  // ==========================================================================
// Function to check whether field mentioned in filter is actually one of the header fields in the file
  // Also determines the position of the field
  private boolean checkFilterColumn() {
    boolean bResult=false;
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="checkFilterColumn";
    String myArea="initialization";
    int indexField=0;
   
   
    for(int i =0 ; i < filterValuePairs.size() ; i++ ) {
        if(headerFieldsList.contains(filterValuePairs.get(i)[0])) {
            bResult=true;
            indexField =headerFieldsList.indexOf(filterValuePairs.get(i)[0]);
            logMessage="Field used in Filter >" + filterValuePairs.get(i)[0] + "< found in the header list at position >" + Integer.toString(indexField) +"<.";
            log(myName, Constants.DEBUG, myArea, logMessage);
            indexFilterFields.add(filterValuePairs.get(i)[0] + Constants.COLUMN_DELIMITER + Integer.toString(indexField));
        }
    }
    if(!bResult) {
      logMessage="Field used in Filter was NOT found in the header list of fields. Processing aborted.";
      log(myName, Constants.ERROR, myArea, logMessage);
      setError(Constants.ERROR, logMessage);
    }
    
    return bResult;
  }
  
    //----------------------------------------------------------
    //Function to add row to return table; a row contains cells with either "pass" (= green), or "fail" (= red).
    //----------------------------------------------------------

    public void addRowToReturnTable(List<String> row) {
        return_table.add(row);
    }


   
   
   
  public List<String> checkInputFile(String logicalDirectory, String fileName) {
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="checkInputFile";
    String myArea="initialization";

    String msg = Constants.NOT_INITIALIZED;

      List<String> return_row = new ArrayList<String>();
      readParameterFile();
      filePath =DetermineCompleteFileName(logicalDirectory, fileName);
      inFile = new File(filePath);
      myArea="check file";
      fileOK=true;
        if(inFile.isFile()) {
               fileOK=true;
          } else {
               fileOK=false;
          }
        
      if (fileOK) {
          return_row.add("");
          return_row.add("pass"); //return "pass" after for second cell
          logMessage= "File =>" +  filePath + "< found.";
          log(myName, Constants.INFO, myArea, logMessage);
      } else {
          msg="File =>" +  fileName + "< not found in >" +logicalDirectory+"< (" + filePath +")";
          return_row.add("");
          if(getExpectedResult().equals(Constants.ERROR)) {
              return_row.add("pass:" +msg);
          } else {
              return_row.add("fail:" +msg);
          }
          logMessage=msg;
          log(myName, Constants.ERROR, myArea, logMessage);
          setError(Constants.ERROR, logMessage);
      }
      //            addRowToReturnTable (return_row);;
      return return_row;
  }

    public String getData(List<String> input_row) {
        List<String> return_row = new ArrayList<String>();
        String s = ""; //string variable
            s = input_row.get(0);
            //If "NULL" is entered in the fitnesse table it needs to be interpreted as "".
            if (s.equals("NULL")) {
                s = "";
            }
                return s; //first column value
        }
    


    //----------------------------------------------------------
    //Function to read first row of table and set database name
    //----------------------------------------------------------

    public String getFileName(List<String> input_row) {
        String sResult;
      String logMessage = Constants.NOT_INITIALIZED;
      String myName="getFileName";
      String myArea="initialization";

        List<String> return_row = new ArrayList<String>();
        sResult = input_row.get(1); //read first row 2nd column
        logMessage= "File name =>" +  sResult +"<.";
        log(myName, Constants.INFO, myArea, logMessage);
        return_row.add("pass"); //return "pass" after for first cell
        //return_row.add("pass"); //return "pass" after for second cell
        //              addRowToReturnTable (return_row);
        return sResult;
    }


    private void log(String name, String level, String location, String logText) {
           if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
           }
           logEntries++;
           if(logEntries ==1) {
            Logging.LogEntry(logFileName, className, Constants.INFO, "Fixture version", getVersion());
           }


        Logging.LogEntry(logFileName, name, level, location, logText);     
       }

    /**
    * @return
    */
    public String getLogFilename() {
            return logFileName + ".log";
    }

    /**
    * @param level
    */
    public void setLogLevel(String level) {
    String myName ="setLogLevel";
    String myArea ="determineLevel";
    
    logLevel = Constants.logLevel.indexOf(level.toUpperCase());
    if (logLevel <0) {
       log(myName, Constants.WARNING, myArea,"Wrong log level >" + level +"< specified. Defaulting to level 3.");
       logLevel =3;
    }
    
    log(myName, Constants.INFO,myArea,"Log level has been set to >" + level +"< which is level >" +getIntLogLevel() + "<.");
    }

    /**
    * @return
    */
    public String getLogLevel() {
    return Constants.logLevel.get(getIntLogLevel());
    }

    /**
    * @return
    */
    public Integer getIntLogLevel() {
    return logLevel;
    }



  //--------------------------
  //Function to read the parameters in a parameter file
  //----------------------------------------------------------
      public void readParameterFile(){   
          String myName="readParameterFile";
          String myArea="reading parameters";
          String logMessage = Constants.NOT_INITIALIZED;


          testdata = GetParameters.GetTestdata();
          logMessage="Test data directory from properties file =>" + testdata + "<.";
          log(myName, Constants.INFO, myArea, logMessage);


        }

// build file name with directory
  public String DetermineCompleteFileName(String directory,
                                           String fileName) {
      String[] sFrom;
      String sCompleteFileName;
    String logMessage = Constants.NOT_INITIALIZED;
    String myName="DetermineCompleteFileName";
    String myArea="result";
    String sRootDir = Constants.NOT_INITIALIZED;

      sFrom = directory.split(" ", 2);

    sRootDir = GetParameters.GetRootDir(sFrom[0]);

      sCompleteFileName = sRootDir + "/" + sFrom[1] + "/" + fileName;
    logMessage="Complete file name set to =>" + sCompleteFileName + "<.";
    log(myName, Constants.INFO, myArea, logMessage);

      return sCompleteFileName;

  }

  private String escape_for_regexp(String in) {
    // java function split uses regexp
    // a delimiter, which we want to use to split up a line, in a file can containt regexp characters
    //    these have to be escaped
    // DWHF uses ;| (whoever thought of this should be @#*&!)
    String out;
    out= in.replace("|", "\\|");
    
    
    return out;
    
  }
  
  public String getResult() {
      return result;
  }
  private void setResult(String result) {
      this.result =result;
  }
  public String getErrorMessage() {
      return errorMessage;
  }
  private void setErrorMessage(String error) {
      this.errorMessage=error;
  }
  public String getResultMessage() {
      return resultMessage;
  }
  private void setResultMessage(String result) {
      this.resultMessage =result;
  }
  private void setError(String errCode, String errMsg) {
      setResult(errCode);
      setErrorMessage(errMsg);
  }
  private void setResult(String code, String msg) {
      setResult(code);
      setResultMessage(msg);
  }
  
  public String getExpectedResult() {
      return this.expectedResult;
  }

    private void addResult() {

        List<String> add_row = new ArrayList<String>();
        if (getExpectedResult().equals(getResult())) {
            add_row.add("pass:" +getResult());
        }
        else {
            if(getResultMessage().equals(Constants.NOERRORS)) {
                add_row.add("fail:" +getResult() + " - " + getErrorMessage());                 
            } else {
                add_row.add("fail:" +getResult() + " - " + getResultMessage()); 
            }
            }
        addRowToReturnTable (add_row);

    }
    
    public static String getVersion() {
        return version;
    }
    
}
