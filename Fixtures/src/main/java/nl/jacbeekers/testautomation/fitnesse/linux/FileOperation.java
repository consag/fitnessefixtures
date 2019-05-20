/*
 * @author Jac. Beekers
 * @version 0.1
 * @since July 2015
 */
package nl.jacbeekers.testautomation.fitnesse.linux;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters;
import nl.jacbeekers.testautomation.fitnesse.supporting.Logging;

import java.text.SimpleDateFormat;

import java.util.Date;


public class FileOperation {

  private String className = "FileOperation";

  private String logFileName = Constants.NOT_INITIALIZED;
  private String context = Constants.DEFAULT;
  private String startDate = Constants.NOT_INITIALIZED;
  private int logLevel =3;

  private String sError="ERROR";
  private String cmdOutputFile = Constants.NOT_INITIALIZED;
  private String cmdErrorFile = Constants.NOT_INITIALIZED;
  
  private String outputFile = Constants.NOT_INITIALIZED;
  private String errorFile = Constants.NOT_INITIALIZED;
  
    //constants

    //variables
    private String fileOperation; //-- Move/Delete/Copy
    private String appInterface; //-- Application and Interface
    private String src; // source
    private String tgt; // targte (not for "remove"
    private String srcPath; // entire path for source file
    private String tgtPath; // entire path for target file
    private String parameter = "";
    private String outgoing = "";
    private String incoming = "";
    private String testdata = "";
    private String temp = "";
    private List<List<String>> return_table = new ArrayList<List<String>>();
    private List<String> m_return_row = new ArrayList<String>();

    private int numberoflinesinfile =0;
    private String application = Constants.NOT_INITIALIZED;
    private String filename = Constants.NOT_INITIALIZED;
    
    //global variables public
    public String returnMessage = ""; // return message for fitnesse
    //global variables private
    // none

    //----------------------------------------------------------
    //Constructor, it gets the name of the script from row in fitnesse table
    //----------------------------------------------------------	

    //Old 1.0 constructor
    //Deprecated

    public FileOperation(String fileOperation, String appInterface, String src,
                         String tgt) {
        this.fileOperation = fileOperation;
        this.appInterface = appInterface;
        this.src = src;
        this.tgt = tgt;
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      context=className;

    }
    //Old 1.0 constructor
    //Deprecated

    public FileOperation(String fileOperation, String appInterface,
                         String src) {
        this.fileOperation = fileOperation;
        this.appInterface = appInterface;
        this.src = src;
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      context=className;
    }
    // 2.0 contructor

    public FileOperation(String appInterface) {
        this.appInterface = appInterface; // DKV, AZV, HHB, ...
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context=className;
    }
    public FileOperation() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        startDate = sdf.format(started);
        context=className;
    }
  public FileOperation(String appInterface, String context) {
      this.appInterface = appInterface; // DKV, AZV, HHB, ...
      java.util.Date started = new java.util.Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      startDate = sdf.format(started);
      context=context;
  }

    /*
 * 2.0 Methods
 * Since 21-JUL-2012
 */

    public String linesInFileInDirectory(String filename, String directory) {
      String rc = Constants.NOT_INITIALIZED;
      numberoflinesinfile=0;
      rc=readFile(filename, directory);
      if(Constants.OK.equals(rc)) {
      return Integer.toString(numberoflinesinfile);
      } else
        return rc;
    }

    public void application(String appl) {
      application=appl;
    }

    public void fileName(String filename) {
      this.filename = filename;
    }
    public String result() {
        return returnMessage;
    }


    public boolean renameFileInDirectoryTo(String oldFileName,
                                           String inDirectory,
                                           String newFileName) {
        boolean bResult = false;
        String sCompleteSrcFileName = "SRCFILENAME_UNKNOWN";
        String sCompleteTgtFileName = "TGTFILENAME_UNKNOWN";
        File f; // file pointer for file move/copy/remove

        System.out.println("file operation is rename.");

        sCompleteSrcFileName =
                DetermineCompleteFileName(inDirectory, oldFileName);
        sCompleteTgtFileName =
                DetermineCompleteFileName(inDirectory, newFileName);

        // file does not exist is source - cannot continue
        if (directoryContainsFile(inDirectory, oldFileName) == Constants.NO) {
            returnMessage =
                    "File >" + sCompleteSrcFileName + "< does not exist.";
            bResult = false;
        } else {
            //file already exists in target - cannot continue
            if (directoryContainsFile(inDirectory, newFileName) == Constants.YES) {
                returnMessage =
                        ">" + sCompleteTgtFileName + "< already exists.";
                bResult = false;
            } else {
                if (!isFile(sCompleteSrcFileName)) {
                    returnMessage =
                            "File >" + sCompleteSrcFileName + "< is not a file (probably it is a directory).";
                    bResult = false;
                } else {
                    if (renameFile(sCompleteSrcFileName,
                                   sCompleteTgtFileName)) {
                        bResult = true;
                    } else {
                        bResult = false;
                    }

                }

            }
        }

        return bResult;
    }


  public boolean compareFileInDirectoryToFileInDirectory(String FileName1,
                                         String inDirectory1,
                                         String FileName2,
                                                        String inDirectory2) {
  String logMessage="Unknown error.";
  String myName="compareFileInDirectoryToFileInDirectory";
  String myArea="initialization";
  
      boolean bResult = false;
      String sCompleteSrcFileName = "SRCFILENAME_UNKNOWN";
      String sCompleteTgtFileName = "TGTFILENAME_UNKNOWN";
  //    File f1; // file pointer for file1
  //    File f2; // file pointer for file2

      logMessage="file operation is diff." ;
      log(myName, "info", myArea, logMessage);

      sCompleteSrcFileName =
              DetermineCompleteFileName(inDirectory1, FileName1);
      sCompleteTgtFileName =
              DetermineCompleteFileName(inDirectory2, FileName2);

      // file does not exist is source - cannot continue
      if (directoryContainsFile(inDirectory1, FileName1) == Constants.NO) {
          returnMessage =
                  "File >" + sCompleteSrcFileName + "< does not exist.";
          bResult = false;
      } else {
          //file does not exist in target - cannot continue
          if (directoryContainsFile(inDirectory2, FileName2) == Constants.NO) {
              returnMessage =
                      ">" + sCompleteTgtFileName + "< does not exist.";
              bResult = false;
          } else {
              if (!isFile(sCompleteSrcFileName)) {
                  returnMessage =
                          "File >" + sCompleteSrcFileName + "< is not a file (probably it is a directory).";
                  bResult = false;
              } else {
                if (!isFile(sCompleteTgtFileName)) {
                    returnMessage =
                            "File >" + sCompleteTgtFileName + "< is not a file (probably it is a directory).";
                    bResult = false;
                } else {
                  if (compareFile(sCompleteSrcFileName,
                                 sCompleteTgtFileName)) {
                      bResult = true;
                  } else {
                      bResult = false;
                  }

              }

          }
      }
      }
      
    logMessage=returnMessage;
  myArea="result";
          if(bResult) {
            logMessage=logMessage+" Result=TRUE.";
          } else {
            logMessage=logMessage+" Result=FALSE";
          }
          log(myName, "info", myArea, logMessage);

      return bResult;
  }



    /*
     * copyFromDirectoryToDirectory
     * Purpose:
     *  copy a file from one directory to a different one, using Linux commands
     *  mimics the file operations as conducted by Linux shell scripts for ITWS and C:D
     */

    public boolean copyFromDirectoryToDirectory(String fileName,
                                                String fromDirectory,
                                                String toDirectory) {
        boolean bResult = false;
        String sCompleteSrcFileName = "SRCFILENAME_UNKNOWN";
        String sCompleteTgtFileName = "TGTFILENAME_UNKNOWN";
        File f; // file pointer for file move/copy/remove

        returnMessage = Constants.OK;
        System.out.println("file operation is copy.");

        sCompleteSrcFileName =
                DetermineCompleteFileName(fromDirectory, fileName);
        sCompleteTgtFileName =
                DetermineCompleteFileName(toDirectory, fileName);

        // file does not exist is source - cannot continue
        if (directoryContainsFile(fromDirectory, fileName) == Constants.NO) {
            returnMessage =
                    "File >" + sCompleteSrcFileName + "< does not exist in source directory.";
            bResult = false;
        } else {
            //file already exists in target - cannot continue
            if (directoryContainsFile(toDirectory, fileName) == Constants.YES) {
                returnMessage =
                        ">" + sCompleteTgtFileName + "< already exists in target directory.";
                bResult = false;
            } else {
                if (!isFile(sCompleteSrcFileName)) {
                    returnMessage =
                            "File >" + sCompleteSrcFileName + "< is not a file (probably it is a directory).";
                    bResult = false;
                } else {
                    bResult =
                            copyFile(sCompleteSrcFileName, sCompleteTgtFileName);
                }

            }
        }

        return bResult;
    }
    
    /*
   * Method: copyFile
   * Purpose: copy a file
   * As this is not java 1.7, the file object does not yet have the copyTo
   */

    private boolean copyFile(String srcFile, String tgtFile) {
        Process process;
        String s = null;
        boolean bResult = false;
        String doWhat = "UNKNOWN_COMMAND";
        String doArgs = " ";

        try {
            Runtime rt = Runtime.getRuntime();

            System.out.println("Operation: copy");
            System.out.println("Source: " + srcFile);
            System.out.println("Target: " + tgtFile);

            doWhat = "cp";
            doArgs = "-p " + srcFile + " " + tgtFile;
            process = rt.exec(doWhat + " " + doArgs);

            BufferedReader stdInput =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedReader stdError =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            //wait for process to return a return code
            process.waitFor();
            //Put return message in the error string to return to fitnesse
            returnMessage = Integer.toString(process.exitValue());
            bResult = true;

        } catch (Exception e) {
            returnMessage = "Exception: " + e.toString();
            System.out.println(returnMessage);
            bResult = false;
        }
        return bResult;
    }

  /*
  * Method: compareFile
  * Purpose: compares two files using diff
  */
  public boolean compareFile(String srcFile, String tgtFile) {
  String myName="compareFile";
      String myArea="initialization";
      String logMessage="Unknown Error.";
      SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy-hhmmss.SSS");
      String tmpFile ="compareFile"+ String.format("%s", sdf.format(new Date() )) + ".tmp";
      
      return compareFile(srcFile, tgtFile,tmpFile);
      
  }
  
  public boolean compareFile(String srcFile, String tgtFile,String cmdOutputFile) {
  String myName="compareFile";
      String myArea="initialization";
      String logMessage="Unknown Error.";

      ProcessBuilder builder; 
          Process process;
      String s = null;
      boolean bResult = false;
      List<String> doWhat = new ArrayList<String>();
      //String doArgs = " ";
    String sOutputFile = Constants.NOT_INITIALIZED;
    String sErrorFile =returnMessage;

      try {
//          Runtime rt = Runtime.getRuntime();

    myArea="Before process start";
          logMessage="Operation: diff. Source=>" +srcFile+"<. Target=>" +tgtFile+"<.";
        log(myName, "info", myArea, logMessage);
        //cmdOutputFile = cmdOutputFile;
//there is no separate error file. stderr is redirected to stdout
        cmdErrorFile = cmdOutputFile;
        outputFile = Constants.LOG_DIR + cmdOutputFile;
        //there is no separate error file. stderr is redirected to stdout
        errorFile = Constants.LOG_DIR + cmdOutputFile;
          
        doWhat.add("diff");
//        doWhat.add("--side-by-side");
        doWhat.add("-a");
          doWhat.add(srcFile);
          doWhat.add(tgtFile);
          
          builder = new ProcessBuilder();
          builder.command(doWhat);
          builder.redirectErrorStream(true); // redirect error output to stdout
          
          process = builder.start();

          BufferedReader stdInput =
              new BufferedReader(new InputStreamReader(process.getInputStream()));

//          BufferedReader stdError =
  //            new BufferedReader(new InputStreamReader(process.getErrorStream()));

          // read the output from the command
          myArea="Command output.";
        logMessage="START of the output of the command:";
        log(myName, "info", myArea, logMessage);
          
        BufferedWriter cmdOut;
        try {
        cmdOut = new BufferedWriter(new FileWriter(sOutputFile,true));
        }
        catch(IOException e) {
          myArea="Exception handling fileIO";
          logMessage="File >"+ sOutputFile + "< could not be created: " + e.getMessage();
          log(myName, sError, myArea, logMessage);
          return false;
        }

          while ((logMessage = stdInput.readLine()) != null) {
            log(myName, "info", myArea, logMessage);
              cmdOut.write("\n"+logMessage);
          }
        
        cmdOut.close();
          
        logMessage="END of the output of the command.";
        log(myName, "info", myArea, logMessage);

        // read the output from the command
//        myArea="Command error output.";
 //       logMessage="START of the error output of the command (if any):";
 //       log(myName, "info", myArea, logMessage);

          // read any errors from the attempted command
   //       while ((logMessage = stdError.readLine()) != null) {
    //        log(myName, "info", myArea, logMessage);
      //    }
    //    logMessage="END of the error output of the command.";
  //      log(myName, "info", myArea, logMessage);
          //wait for process to return a return code
          process.waitFor();
          //Put return message in the error string to return to fitnesse
          returnMessage = Integer.toString(process.exitValue());
        logMessage=returnMessage;
          myArea="result";
        log(myName, "info", myArea, logMessage);
          
          if ("0".equals(returnMessage)) {
          bResult = true;
          } else {
            bResult = false;
          }

      } catch (Exception e) {
          returnMessage = "Exception: " + e.toString();
          logMessage=returnMessage;
        log(myName, "info", myArea, logMessage);
          bResult = false;
      }
      return bResult;
  }


    /*
   * moveFromDirectoryToDirectory
   * Purpose:
   *  move a file from one directory to a different one, using Linux commands
   *  mimics the file operations as conducted by Linux shell scripts for ITWS and C:D
   */

    public boolean moveFromDirectoryToDirectory(String fileName,
                                                String fromDirectory,
                                                String toDirectory) {

        Process process;
        String s = null;
        boolean bResult = false;
        String doWhat = "UNKNOWN_COMMAND";
        String doArgs = " ";
        String sCompleteSrcFileName = "Unknown Source File Name";
        String sCompleteTgtFileName = "Unknonw Target File Name";

        bResult = true;
        System.out.println("Operation: move");
        System.out.println("fileName=>" + fileName + "< fromDirectory=>" +
                           fromDirectory + "< toDirectory=>" + toDirectory +
                           "<.");

        sCompleteSrcFileName =
                DetermineCompleteFileName(fromDirectory, fileName);
        sCompleteTgtFileName =
                DetermineCompleteFileName(toDirectory, fileName);

        try {
            Runtime rt = Runtime.getRuntime();

            doWhat = "mv";
            doArgs = sCompleteSrcFileName + " " + sCompleteTgtFileName;
            process = rt.exec(doWhat + " " + doArgs);

            BufferedReader stdInput =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedReader stdError =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            //wait for process to return a return code
            process.waitFor();
            //Put return message in the error string to return to fitnesse
            returnMessage = Integer.toString(process.exitValue());
            bResult = true;

        } catch (Exception e) {
            returnMessage = "Exception: " + e.toString();
            System.out.println(returnMessage);
            bResult = false;
        }

        return bResult;
    }

    private boolean renameFile(String oldFileName, String newFileName) {
        boolean bResult;
        File f = new File(oldFileName);
        File newF = new File(newFileName);

        if (isFile(oldFileName)) {
            if (f.canRead() && f.canWrite()) {
                if (f.renameTo(newF)) {
                    bResult = true;
                    returnMessage = "OK";
                } else {
                    bResult = false;
                    returnMessage =
                            "Could not rename file >" + oldFileName + "< to >" +
                            newFileName + "<.";
                }
            } else {
                bResult = false;
                returnMessage =
                        "Cannot read/write file >" + oldFileName + "<.";
            }
        } else {
            returnMessage =
                    "Provided file >" + oldFileName + " is not a file (probably a directory).";
            return false;
        }
        return bResult;
    }

    private boolean isFile(String filename) {
        boolean bResult;
        File f = new File(filename);

        //cannot remove directories, only files
        if (f.isDirectory()) {
            bResult = false;
        } else {
            bResult = true;
        }
        return bResult;
    }
    /*
 * Methos: DetermineCompleteFileName
 * Purpose: Constrcut the file name including path
 */

    private String DetermineCompleteFileName(String directory,
                                             String fileName) {
        String[] sFrom;
        String sCompleteFileName;
        String sRootDir = "FROMROOTDIR_UNKNOWN";

        sFrom = directory.split(" ", 2);
        // System.out.println("fileName=>" + fileName
        //                    + "< FromArea =>" + sFrom[0] +"< FromDir=>" + sFrom[1] + "<.");

        sRootDir = GetParameters.GetRootDir(sFrom[0]);
        //System.out.println("from root dir =>" + sRootDir +"<.");
        sCompleteFileName = sRootDir + "/" + sFrom[1] + "/" + fileName;
        //ystem.out.println("complete file name =>" + sCompleteFileName +"<.");

        return sCompleteFileName;

    }
    
    /*
   * removeFromDirectoryToDirectory
   * Purpose:
   *  copy a file from one directory to a different one, using Linux commands
   *  mimics the file operations as conducted by Linux shell scripts for ITWS and C:D
   */

    public boolean removeFromDirectory(String fileName, String fromDirectory) {
        boolean bResult = false;
        String sCompleteFileName = "FILENAME_UNKNOWN";
        File f; // file pointer for file move/copy/remove

        returnMessage = Constants.OK;
        System.out.println("file operation is remove.");

        sCompleteFileName = DetermineCompleteFileName(fromDirectory, fileName);
        //if file does not exist: error
        if (directoryContainsFile(fromDirectory, fileName) == Constants.NO) {
            returnMessage =
                    "File >" + sCompleteFileName + "< does not exist.";
            bResult = false;
        } else {
            //cannot remove directories, only files
            if (!isFile(sCompleteFileName)) {
                returnMessage =
                        ">" + sCompleteFileName + "< is a directory. This fixture can only remove files, not directories.";
                bResult = false;
            } else {
                //if we don't have write privileges, refuse to remove the file
                f = new File(sCompleteFileName);
                if (!f.canWrite()) {
                    returnMessage =
                            "Do not write privileges on file >" + sCompleteFileName +
                            "<. Cannot remoe file.";
                } else {
                    //try to remove it
                    bResult = f.delete();
                }
            }

        }
        return bResult;
    }

    public String directoryContainsFile(String directory, String fileName) {

        String sCompleteFileName =
            DetermineCompleteFileName(directory, fileName);
        File f; // file pointer for file move/copy/remove
        f = new File(sCompleteFileName);

        //This method can be called directly, hence we need to set the file pointer
        if (f.exists()) {
            return Constants.YES;
        } else {
            return Constants.NO;
        }
    }

    /*
     * Method: directoryListing
     * Purpose: Output file names in a directory
     */

    public String directoryListing(String directory) {

        return "Not yet implemented";
    }

    /*
   * Method: directoryEntry
   * Purpose: Show the file name in a directory
   */

    public String fileEntry(String directory) {

        return "Not yet implemented";
    }

    /*
   * Method: numberOfFile
   * Purpose: Return the number of files found in the specified location
   */

    public String numberOfFiles(String directory) {

        return "Not yet implemented";
    }

    /*
   * Method: returnMessage
   * Purpose: Output value of returnMessage
   */

    public String returnMessage() {
        return returnMessage;
    }
    /*
     * End of 2.0 Methods
     */

    /*
     * Below are only 1.0 DEPRECATED METHODS
     * Deprecated as if 21-JUL-2012
     */
    //----------------------------------------------------------
    //Function to get parameter from table row in fitnesse table
    //----------------------------------------------------------
    // only 1.0
    // DEPRECATED

    public void addParameter(String parameter) {
        if (parameter == "") {
            this.parameter = parameter;
        } else {
            this.parameter = this.parameter + " " + parameter;
        }
    }

  //----------------------------------------------------------
  //Function to read file to determine nr of lines
    //future: could include filter on header and footer record
  //----------------------------------------------------------
      public String readFile(String filename, String directory){  
           String myName="readFile";
           String myArea="init";
           String logMessage = Constants.NOT_INITIALIZED;
           String rc = Constants.NOT_INITIALIZED;
         String sCompleteFileName =
                 DetermineCompleteFileName(directory, filename);

          try{
            FileInputStream fstream = new FileInputStream(sCompleteFileName);
          // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String strLine;
          //Read file line by line
          myArea="Counting";
            while ((strLine = br.readLine()) != null)   {
              numberoflinesinfile ++;

            }
            //Close the input stream
            in.close();
            rc = Constants.OK;
          }
        catch (Exception e){//Catch exception if any
                              myArea="Exception handling";
                              logMessage="Error reading file: " + e.getMessage();
                              log(myName,"ERROR",myArea,logMessage);
                              rc=logMessage;
          }
  return rc;
       }
        
      
    private void log(String name, String level, String location, String logText) {
           if(Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
               return;
           }

        Logging.LogEntry(getLogFilename(), name, level, location, logText);
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

    public String getOutputFile() {
        return outputFile;
    }
    public String getErrorFile() {
        return errorFile;
    }
    


}
