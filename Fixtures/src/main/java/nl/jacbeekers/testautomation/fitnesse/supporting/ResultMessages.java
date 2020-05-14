package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.util.*;


public class ResultMessages {

    //generic
    public static final String ERR_FILE_NOTFOUND ="CNSG-GEN-0001";
    public static final String ERRMSG_NOTFOUND ="Error message could not be found.";
    public static final String OS_COMMAND_ERROR ="CNSG-GEN-0002";

    //fixtures
    public static final String ERRCODE_NOT_IMPLEMENTED="CNSG-FIT-0001";

    //Database errors or unexpected results
    public static final String ERRCODE_DBCONNECTION ="CNSG-DB-0001";
    public static final String ERRCODE_DBMETADATA  ="CNSG-DB-0002";
    public static final String ERRCODE_DBMETADATA_COLUMNS  ="CNSG-DB-0003";
    public static final String ERRCODE_TABLENOTFOUND ="CNSG-DB-0004";
    public static final String ERRCODE_UNSUPPORTED_DATATYPE ="CNSG-DB-0005";
    public static final String ERRCODE_SQL_IDAASQLERROR ="CNSG-DB-0006";
    public static final String ERRCODE_SQL_IDAAGETRESULT ="CNSG-DB-0007";
    public static final String ERRCODE_UNSUPPORTED_DATABASE ="CNSG-DB-0008";
    public static final String ERRCODE_SQLERROR ="CNSG-DB-0009";
    public static final String ERRCODE_NOTALLOWED_QUERYSQL = "CNSG-DB-0010";

    //security
    public static final String ERRCODE_DECRYPT ="CNSG-SEC-0001";

    //properties
    public static final String ERRCODE_PROPERTIES ="CNSG-PROP-0001";
    public static final String ERRCODE_PROPFILE ="CNSG-PROP-0002";
    public static final String ERRCODE_PROPFILE_NOTFOUND ="CNSG-PROP-0003";
    public static final String ERRCODE_PROPFILE_IOERROR ="CNSG-PROP-0004";
    public static final String ERRCODE_PROP_NOT_FOUND ="CNSG-PROP-0005";
    public static final List<String> propFileErrors = Collections.unmodifiableList(Arrays.asList(
            ERRCODE_PROPERTIES
            ,ERRCODE_PROPFILE
            ,ERRCODE_PROPFILE_NOTFOUND
            ,ERRCODE_PROPFILE_IOERROR
            ,ERRCODE_PROP_NOT_FOUND
    ));

    //powercenter
    public static final String ERRCODE_PWCCONNECTION ="INFA-PWC-0001";

    //platform
    public static final String ERRCODE_INFACMD_ERROR ="INFA-PLF-0001";

    private static final Map<String, MessageStructure> msgList = createMap();
    private static Map<String, MessageStructure> createMap() {
        HashMap<String, MessageStructure> result = new HashMap<String, MessageStructure>();
        result.put(ERR_FILE_NOTFOUND, new MessageStructure(ERR_FILE_NOTFOUND,ERR_FILE_NOTFOUND, Constants.FATAL,
                "File not found"));
        result.put(OS_COMMAND_ERROR, new MessageStructure(OS_COMMAND_ERROR,OS_COMMAND_ERROR, Constants.ERROR,
                "Error running OS process."));
        result.put(ERRCODE_NOT_IMPLEMENTED, new MessageStructure(ERRCODE_NOT_IMPLEMENTED, ERRCODE_NOT_IMPLEMENTED, Constants.FATAL,
                "Not implemented"));

        result.put(ERRCODE_INFACMD_ERROR, new MessageStructure(ERRCODE_INFACMD_ERROR, ERRCODE_INFACMD_ERROR, Constants.ERROR,
                "infacmd reported an error"));

        result.put(ERRCODE_SQLERROR, new MessageStructure(ERRCODE_SQLERROR, ERRCODE_SQLERROR, Constants.ERROR,
                "An SQL error occurred."));
        result.put(ERRCODE_NOTALLOWED_QUERYSQL, new MessageStructure(ERRCODE_NOTALLOWED_QUERYSQL, ERRCODE_NOTALLOWED_QUERYSQL, Constants.ERROR,
                "An invalid SQL was provided. Must start with WITH or SELECT"));

        result.put(ERRCODE_PWCCONNECTION, new MessageStructure(ERRCODE_PWCCONNECTION ,ERRCODE_PWCCONNECTION, Constants.FATAL,
                                        "Could not establish connection to PowerCenter."));

        return Collections.unmodifiableMap(result);
    }

    public static String getMessage(String code) {

    MessageStructure val = msgList.get(code);
    if(val==null) 
        return ERRMSG_NOTFOUND + " Code: " +code;
    else
        return val.message;
}

public static int getNrMessages() {
    return msgList.size();
}

public static List<List<String>> getAllMessages() {
    
    List<List<String>> s = new ArrayList<List<String>>();
    
    for (Map.Entry<String, MessageStructure> entry : msgList.entrySet()) {
        List<String> r = new ArrayList<String>();
        String key = entry.getKey();
        MessageStructure msg = entry.getValue();
        r.add(key);
        r.add(msg.message);
        s.add(r);
    }
    return s;
}

}
