package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {
    private static String className = "Utilities";
    private static String version = "20180822.0";

    private String errorMessage = Constants.NO_ERRORS;

    public static String convertListToString(List<String> list, String separator, List<String> keywordList) {
        String myName ="convertListToString";
        String myArea ="run";
        String logMessage = Constants.NOT_INITIALIZED;
        List<String> listForString = new ArrayList<String>();
        listForString.addAll(list);
        String listAsString = Constants.NOT_INITIALIZED;

        if(!Constants.NOT_FOUND.equals(checkForKeyword(listForString, keywordList))) {
            //remove the keyword
            listForString.remove(0);
        }
        listAsString = convertListToString(listForString, separator);

        return listAsString;

    }

    public static String convertListToString(List<String> list, String separator) {
        String myName ="convertListToString";
        String myArea ="run";
        String logMessage = Constants.NOT_INITIALIZED;
        List<String> listForString = new ArrayList<String>();
        listForString.addAll(list);

        String listAsString = listForString
                .stream()
                .map(a -> String.valueOf(a))
                .collect(Collectors.joining(separator));

        myArea="result";
        return listAsString;
    }


    public static String getVersion() {
        return version;
    }

    public static String checkForKeyword(List<String> row, List<String> keywordList) {
        String myName="checkForKeyword";
        String myArea="init";
        String logMessage= Constants.NOT_INITIALIZED;
        String theWord = Constants.NOT_FOUND;

        if(row.size()>0) {
            theWord=row.get(0).toLowerCase().replaceAll("\\s","");
            if(!keywordList.contains(theWord)) {
                theWord = Constants.NOT_FOUND;
            }
        } else {
            theWord = Constants.NOT_FOUND;
        }
        return theWord;
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public boolean isDateValid(String date, String dateFormat)
    {
        try {
            LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));

            return true;
        } catch (DateTimeParseException e) {
            setErrorMessage(e.toString());
            return false;
        }
    }

    public static String convertStringToDateFunction(String theDate, String format, String dbType) {
        String forDatabase = Constants.ERRCODE_UNSUPPORTED_DATABASE;

        switch (dbType) {
            case Constants.DATABASETYPE_DB2:  // DB2 z/OS
            case Constants.DATABASETYPE_ORACLE:
            case Constants.DATABASETYPE_SQLSERVER: //estimated guess this actually won't work TODO: next version
            case Constants.DATABASETYPE_NETEZZA:
            case Constants.DATABASETYPE_NONSTOP:
            default:
                forDatabase ="TO_DATE('" + theDate + "','" + format + "')";
                break;
        }

        return forDatabase;

    }

    public static String convertStringToTimestampFunction(String theTimestamp, String format, String dbType) {
        String forDatabase = Constants.ERRCODE_UNSUPPORTED_DATABASE;
        switch (dbType) {
            case Constants.DATABASETYPE_DB2:  // DB2 z/OS
                forDatabase ="TIMESTAMP_FORMAT('" + theTimestamp + "','" + format + "')";
                break;
            case Constants.DATABASETYPE_ORACLE:
            case Constants.DATABASETYPE_SQLSERVER: //estimated guess this actually won't work TODO: next version
            case Constants.DATABASETYPE_NETEZZA:
            case Constants.DATABASETYPE_NONSTOP:
            default:
                forDatabase ="TO_TIMESTAMP('" + theTimestamp + "','" + format + "')";
                break;
        }

        return forDatabase;

    }
    public static boolean checkForFunction(String val) {
        if (val == null) return false;
        if(val.contains("(")) {
            return true;
        } else {
            return false;
        }
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }

}