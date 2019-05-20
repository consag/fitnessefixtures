package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.ERRMSG_NOTFOUND;
import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.getMessage;

public class Logging {
	  private static String logFile = Constants.DEFAULT;
      private static String logMsg = Constants.NOT_INITIALIZED;

    /**
     * @param nameLog
     * @param logLevel
     * @param location
     * @param logText
     */
    public static void LogEntry (String nameLog, String logLevel, String location, String logText) {
        LogEntry(nameLog, Constants.OK,logLevel,location,logText);
	  }
	  
    public static void LogEntry (String nameLog, String methodName, String logLevel, String location, String logText) {
	      logFile = Constants.LOG_DIR + nameLog + ".log";
	      
	      try {
	      FileWriter fLog = new FileWriter( logFile, true);
	      BufferedWriter out = new BufferedWriter(fLog);
	      Date date = new Date();
	      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      String formattedDate = sdf.format(date);
	      try {
	      logMsg = getMessage(logText);
	      if(logMsg.startsWith(ERRMSG_NOTFOUND)) {
	          logMsg=logText;
	      }
	       } catch (Exception e) {
	          out.write("\n" + formattedDate + Constants.LOG_FILE_DELIMITER + nameLog + Constants.LOG_FILE_DELIMITER +
                          Constants.ERROR + Constants.LOG_FILE_DELIMITER + location + Constants.LOG_FILE_DELIMITER
	                    +"Error accessing HashMap - error =>" + e.toString() + "<.");
	      //                    e.printStackTrace();
	      }
	      out.write("\n" + formattedDate + Constants.LOG_FILE_DELIMITER 
	                + methodName + Constants.LOG_FILE_DELIMITER 
	                + logLevel + Constants.LOG_FILE_DELIMITER 
	                + location + Constants.LOG_FILE_DELIMITER 
	                 +logMsg);
	      out.close();
	      }
	      catch (Exception e) {
	      System.err.println("Error writing to log >" + logFile +"<. Error: " + e.getMessage());
	      }

	  }
          
}