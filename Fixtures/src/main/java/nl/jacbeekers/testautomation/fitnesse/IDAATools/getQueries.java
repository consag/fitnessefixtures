package nl.jacbeekers.testautomation.fitnesse.IDAATools;

/*
 * @version: 20160709.0 - deactivated IDAA-Only tables on request of DBA's: the monitor seems to generate too many SQL's, so their own monitor has difficulties
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import nl.jacbeekers.testautomation.fitnesse.database.ConnectionProperties;
import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Parameters;

public class getQueries {
    private static String version = "20180706.0";

    private static String errorMessage;
    private static boolean error;

    private static Connection insertConnection = null;
    private static Connection deleteConnection = null;
    private static Connection queryConnection = null;
    private static boolean storeHistory = true;
    private static String getQueriesResult = null;
    private static Parameters globalParam = new Parameters();
    private static String startDateForDB2;
    private static String collectionDB =Constants.IDAA_STATS_COLLECTION_DB;

    private static ConnectionProperties connectionProperties = new ConnectionProperties();

    private static String databaseName;

    public static void main(String[] args) throws IOException, Exception {
        System.out.println(collectQueryNumbers());
    }

    public static String syncIDAAMonitor(String collectionTableName
                                         //, String collectionAOTTableName
                                         ) {
        String rc =Constants.OK;
        //Taken from http://www-01.ibm.com/support/docview.wss?uid=swg27039739
        String    insertStatement = "INSERT INTO " + collectionTableName;

        CallableStatement insert =null;
        TableManagement tableManagement = new TableManagement();


            try {
                rc =tableManagement.loadTable(Constants.IDAA_STATS_COLLECTION_IDAA, collectionDB, Constants.IDAA_STATS_COLLECTION_SCHEMA, collectionTableName);
                System.out.println("IDAASync returned >" +rc+"<. Error (if any): " +tableManagement.getErrorMessage());
/*                insertStatement ="INSERT INTO " + collectionAOTTableName + " SELECT * FROM " + collectionTableName;
                System.out.println("Insert statement for AOT table >" + insertStatement +"<.");
                insert = insertConnection.prepareCall(insertStatement);
                int result = insert.executeUpdate();
                System.out.println("Insert into AOT table >" +collectionAOTTableName +"< statement returned >" + result + "<.");
                insert.close();
                try {
                    CallableStatement delete = null;
                    String deleteStatement ="DELETE FROM " + collectionTableName + " WHERE measured = TIMESTAMP('" + startDateForDB2 +"')" ;
                    delete = deleteConnection.prepareCall(deleteStatement);
                    result = delete.executeUpdate();
                    System.out.println("Cleanup statement for >" + collectionTableName + "< returned >" + result + "<.");
                    delete.close();
                    rc =tableManagement.loadTable(Constants.IDAA_STATS_COLLECTION_IDAA, collectionDB, Constants.IDAA_STATS_COLLECTION_SCHEMA, collectionTableName);
                    System.out.println("IDAASync (after cleanup) returned >" +rc+"<. Error (if any): " +tableManagement.getErrorMessage());
                } catch (Exception e) {
                    System.out.println("syncIDAAMonitor.cleanup - Exception >" + e.toString());
                    rc=Constants.ERROR;
                }
*/            } catch (Exception e) {
                System.out.println("syncIDAAMonitor.insertselect - Exception >" + e.toString());
                rc=Constants.ERROR;
                if(insert != null)
                try {
                    insert.close();
                } catch (SQLException f) {
                    System.out.println("syncIDAAMonitor.close - Exception >" + e.toString());
                    rc=Constants.ERROR;
                }
 
                }

        return rc;
        
    }
    
    public static String collectQueryNumbers() throws IOException, Exception {
        String myArea="init";

        String zOs = getZosMachineName();
        if (Constants.NOT_FOUND.equals(zOs)) {
            return "zOS machine name not found.";
        } else {
            System.out.println("z/OS machine is >" + getZosMachineName() + "<.");
        }

        String fn = "list.txt";
        PrintWriter out = new PrintWriter(new FileWriter(fn));
        List<String> idaaList = new ArrayList<String>();
        List<String> db2DatabaseList = new ArrayList<String>();

        idaaList = getAccelerators(zOs);
        System.out.println("Property file contains >" + idaaList.size() + "< accelerator entry/entries.");
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startDate = sdf.format(started);
        SimpleDateFormat sdfdb2 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        startDateForDB2 = sdfdb2.format(started);

        storeHistory = true;

        Parameters paramCollection = new Parameters();
        collectionDB =paramCollection.getCollectionDb();
        if(Constants.NOT_FOUND.equals(collectionDB)) {
            collectionDB =Constants.IDAA_STATS_COLLECTION_DB;
        }
        paramCollection.getDatabaseParameters(collectionDB);

        if(Constants.ERROR.equals(paramCollection.getResult()) || Constants.NOT_FOUND.equals(paramCollection.getResult())) {
            System.out.println("Error getting password information: " + paramCollection.getErrorMessage());
            storeHistory = false;
        }
        if(storeHistory) {
            try {
                myArea="readParameterFile";
                readParameterFile();
                insertConnection = connectionProperties.getUserConnection();
                deleteConnection = connectionProperties.getUserConnection();
                queryConnection = connectionProperties.getUserConnection();
            } catch (Exception e) {
                System.out.println("The connection for data collection failed.  Check your connection parameters! ");
                System.out.println(e.toString());
                storeHistory = false;
            }
        }

        for (int i = 0; i < idaaList.size(); ++i) {
            db2DatabaseList = getIdaaDatabaseList(idaaList.get(i));
            System.out.println("Property file contains >" + db2DatabaseList.size() +
                               "< database(s) configured for IDAA system >" + idaaList.get(i) + "<.");
            for (int d = 0; d < db2DatabaseList.size(); ++d) {
                Parameters param = new Parameters();
                String result = param.getDatabaseParameters(db2DatabaseList.get(d));
                if (Constants.NOT_FOUND.equals(result) || Constants.ERROR.equals(result)) {
                    System.out.println( "Error retrieving db connection information for >" + db2DatabaseList.get(d) + "<. Error: " + param.getErrorMessage());
                    continue;
                }
                int nrActiveQueries = getNrQueries(idaaList.get(i), "active", param);
                int nrAllQueries = getNrQueries(idaaList.get(i), "all", param);
                out.println(startDate + ":" + param.getDbLocation() + ":" + nrAllQueries + ":" + nrActiveQueries + ":");
                param=null;
                }
        }

        boolean doCollectionSync =true;
        String rc1 = Constants.OK;
        String rc2 = Constants.OK;

        if(storeHistory) {
            if(doCollectionSync) {
                //This may lead to contention if code runs on more than 1 location
                String collectionTableName =Constants.IDAA_STATS_COLLECTION_TABLE_HIST;
//                String collectionAOTTableName =Constants.IDAA_STATS_COLLECTION_TABLE_HIST_AOT;
//                rc1=syncIDAAMonitor(collectionTableName, collectionAOTTableName);
                rc1=syncIDAAMonitor(collectionTableName);
                collectionTableName =Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE;
//                collectionAOTTableName =Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE_AOT;
//                rc2=syncIDAAMonitor(collectionTableName, collectionAOTTableName);
                rc2=syncIDAAMonitor(collectionTableName);
            }
            insertConnection.commit();
            insertConnection.close();
            if(queryConnection != null)
                queryConnection.close();
        }
        out.close();
        
        if(Constants.ERROR.equals(rc1) || Constants.ERROR.equals(rc2))
            return Constants.ERROR;
        else 
        return Constants.OK;

    }

    private static void readParameterFile() {
        String myName = "readParameterFile";
        String myArea = "reading parameters";
        String logMessage = Constants.NOT_INITIALIZED;

        connectionProperties.refreshConnectionProperties(databaseName);

    }

    private static String getZosMachineName() {
        return globalParam.getPropertyVal("zOS");
    }

    private static List<String> getzOsDatabaseList(String zOs) {
        List<String> dbList = new ArrayList<String>();
        if (zOs == null) {
            return dbList;
        }

        String dbs = globalParam.getPropertyVal(zOs + ".db2list");
        if (Constants.NOT_FOUND.equals(dbs)) {
            return dbList;
        }

        dbList = Arrays.asList(dbs.split("\\s*"));

        return dbList;

    }

    private static List<String> getIdaaDatabaseList(String idaaName) {
        List<String> dbList = new ArrayList<String>();
        if (idaaName == null) {
            return dbList;
        }

        String dbs = globalParam.getPropertyVal(idaaName + ".db2list");
        if (Constants.NOT_FOUND.equals(dbs)) {
            return dbList;
        }

        //        dbList = Arrays.asList(dbs.split("\\s*"));
        dbList = Arrays.asList(dbs.split(" "));

        return dbList;

    }

    private static List<String> getAccelerators(String zOs) {
        List<String> acceleratorList = new ArrayList<String>();
        if (zOs == null) {
            return acceleratorList;
        }

        String acc = globalParam.getPropertyVal(zOs + ".accelerators");
        if (Constants.NOT_FOUND.equals(acc)) {
            return acceleratorList;
        }

        acceleratorList = Arrays.asList(acc.split(" "));

        return acceleratorList;

    }


    private static void setErrorMessage(String msg) {
        errorMessage = msg;
        error = true;
    }

    private static String getErrorMessage() {
        return errorMessage;
    }

    private static String getSQL(Pair pair, Connection con, String accelName) throws Exception {
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:querySelection xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\"> <filter scope=\"all\" /> </dwa:querySelection>";
        CallableStatement stmt =
            con.prepareCall("CALL SYSPROC.ACCEL_GET_QUERY_DETAILS('" + accelName + "', " + pair.id + ", ?)");
        stmt.setNull(1, java.sql.Types.CHAR);
        //ResultSet rs = stmt.executeQuery();
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        StringBuilder buffer = new StringBuilder();
        while (rs.next()) {
            buffer.append(rs.getString(2));
        }

        rs.close();
        stmt.close();
        try {
            String sql = buffer.toString();
            int end = sql.indexOf("<<<<<<<<<<< NPS VERSION >>>>>>>>>>");
            if (end != -1) {
                sql = sql.substring(0, end);
                sql = sql.replaceAll("\\s+", " ").trim();
                if (sql.startsWith("DECLARE")) {
                    int x = sql.indexOf("CURSOR FOR ");
                    sql = sql.substring(x + 11);
                    x = sql.lastIndexOf(' ');
                    String temp = sql.substring(0, x);
                    if (temp.endsWith("QUERYNO")) {
                        sql = sql.substring(0, x - 8);
                    }
                }
            } else {
                sql = sql.replaceAll("\\s+", " ").trim();
                if (sql.startsWith("DECLARE")) {
                    int x = sql.indexOf("CURSOR FOR ");
                    sql = sql.substring(x + 11);
                    x = sql.lastIndexOf(' ');
                    String temp = sql.substring(0, x);
                    if (temp.endsWith("QUERYNO")) {
                        sql = sql.substring(0, x - 8);
                    }
                }
            }

            return sql.toUpperCase() + "\n";
        } catch (Exception e) {
            System.out.println(" Unexpected response received from ACCEL_GET_QUERY_DETAILS");
            String fn = System.currentTimeMillis() + ".txt";
            System.out.println(" The reponse in error will be written to " + fn);
            PrintWriter out = new PrintWriter(new FileWriter(fn));
            out.println(buffer.toString());
            out.println("");
            e.printStackTrace(out);
            out.close();
            System.exit(1);
            return null;
        }
    }

    private static HashSet<Pair> getQueries(Connection con, String accelName, String scope
                                            , String fromTimestamp
                                            , String toTimestamp) throws Exception {
        HashSet<Pair> retval = new HashSet<Pair>();
        
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <dwa:querySelection xmlns:dwa=\"http://www.ibm.com/xmlns/prod/dwa/2011\" version=\"1.0\">" +
            "<filter scope=\"" + scope + "\" " + "fromTimestamp=\"" + fromTimestamp + "\" " + "toTimestamp=\"" +
            toTimestamp + "\" " + "/> </dwa:querySelection>";
        CallableStatement stmt = con.prepareCall("CALL SYSPROC.ACCEL_GET_QUERIES('" + accelName + "', ?, ?, ?)");
        stmt.setString(1, xml);
        stmt.registerOutParameter(2, java.sql.Types.CHAR);
        stmt.setNull(3, java.sql.Types.CHAR);
        stmt.execute();
        getQueriesResult = null;
        try {
            getQueriesResult = stmt.getString(2);
        } catch (Exception e) {
            System.out.println("An error occurred while trying to get the query list.  Maybe the accelerator name was wrong?");
            return retval;
        }
        stmt.close();
        
        if(getQueriesResult == null) {
            System.out.println("Queries result is null. Check the xml filter:");
            System.out.println(xml);
        } else {
            int i = getQueriesResult.indexOf("planID=\"");
            while (i != -1) {
                int e = getQueriesResult.indexOf('"', i + 8);
                int id = Integer.parseInt(getQueriesResult.substring(i + 8, e));
                i = getQueriesResult.indexOf("<sql><![CDATA[", e);
                e = getQueriesResult.indexOf("]]></sql>", i);
                String sql = getQueriesResult.substring(i + 14, e);
                sql = sql.replaceAll("\\s+", " ").trim();
                if (sql.startsWith("DECLARE")) {
                    int x = sql.indexOf("CURSOR FOR ");
                    sql = sql.substring(x + 11);
                    x = sql.lastIndexOf(' ');
                    String temp = sql.substring(0, x);
                    if (temp.endsWith("QUERYNO")) {
                        sql = sql.substring(0, x - 8);
                    }
                }
                retval.add(new Pair(id, sql));
                i = getQueriesResult.indexOf("planID=\"", e);
            }
        }

        if(retval.size() == 0  && !("active".equalsIgnoreCase(scope))) {
            System.out.println("There were no queries found. You may want to verify the filter:");
            System.out.println(xml);
        }
        return retval;
    }

    private static int getNrQueries(String accelName, String scope, Parameters param) throws IOException, Exception {
        String host = param.getDbHostname();
        String port = param.getDbPort();
        String location = param.getDbLocation();
        String collectionTableName =Constants.NOT_PROVIDED;
//        String collectionAOTTableName =Constants.NOT_PROVIDED;

        String fromTimestamp = "2016-01-01T00:00:00Z";
        String toTimestamp = "2016-12-31T00:00:00Z";

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:db2://" + host + ":" + port + "/" + location, param.getDbUserId(), param.getDbPassword());
        } catch (Exception e) {
            System.out.println("The connection failed.  Check your connection parameters! host=>" + host + "< port=>" +
                               port + "< database =>" + location + "< user=>" + param.getDbUserId() + "<");
            System.out.println(e.toString());
            return -1;
        }
        
        if (storeHistory) {
            String queryStatement =null;
            String deleteStatement =null;
            int retentionTimeInDays = Constants.IDAA_STATS_RETENTION_IN_DAYS;
            if("all".equalsIgnoreCase(scope)) {
                queryStatement ="SELECT max(submitTimestamp + 1 microsecond) fromTimestamp, current timestamp + 1 day as toTimestamp "
//                    + " FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_HIST_AOT
                    + " FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_HIST
                    + " WHERE locationname like '" + location + "%' with ur";
//                deleteStatement ="DELETE FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_HIST_AOT
                deleteStatement ="DELETE FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_HIST
                    + " WHERE locationname like '" + location + "%'";
            }
            if("active".equalsIgnoreCase(scope)) {
                queryStatement ="SELECT '2016-01-01 00:00:00' fromTimestamp, current timestamp + 1 day as toTimestamp FROM SYSIBM.SYSDUMMY1 with ur";
//                deleteStatement ="DELETE FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE_AOT
                deleteStatement ="DELETE FROM " + Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE
                    + " WHERE locationname like '" + location + "%'";
            }
            
            //Cleanup tables
            CallableStatement deleteQuery = insertConnection.prepareCall(deleteStatement);
            int nrDeleted = deleteQuery.executeUpdate();
            System.out.println("DELETE returned >" + nrDeleted + "<. Statement was >" + deleteStatement +"<.");
            deleteQuery.close();
            
            //Determine FROM - TO
            CallableStatement timestampQuery = insertConnection.prepareCall(queryStatement);
            ResultSet rs =timestampQuery.executeQuery();
            //rs.first();
            rs.next();
            Timestamp sqlFromTimestamp = rs.getTimestamp("FROMTIMESTAMP");
            Timestamp sqlToTimestamp = rs.getTimestamp("TOTIMESTAMP");
            rs.close();
            timestampQuery.close();
            
            if(sqlFromTimestamp == null) {
                fromTimestamp = "2016-01-01T00:00:00Z";
            } else {
                fromTimestamp = sqlFromTimestamp.toString().substring(0,sqlFromTimestamp.toString().indexOf(" ")) + "T" 
                                + sqlFromTimestamp.toString().substring(sqlFromTimestamp.toString().indexOf(" ") +1);
            }
            if(sqlToTimestamp == null) {
                toTimestamp = "2100-01-01T00:00:00Z";
            } else {
                toTimestamp = sqlToTimestamp.toString().substring(0,sqlToTimestamp.toString().indexOf(" ")) +"T00:00:00Z" ;
            }
        }

        HashSet<Pair> pairs = getQueries(con, accelName, scope, fromTimestamp, toTimestamp);
        con.commit();
        con.close();

        if (storeHistory) {
            String insertStatement = null;
            //Taken from http://www-01.ibm.com/support/docview.wss?uid=swg27039739
            if("all".equalsIgnoreCase(scope)) {
                collectionTableName =Constants.IDAA_STATS_COLLECTION_TABLE_HIST;
//                collectionAOTTableName =Constants.IDAA_STATS_COLLECTION_TABLE_HIST_AOT;
                insertStatement = "INSERT INTO " + collectionTableName;
            }
            if("active".equalsIgnoreCase(scope)) {
                collectionTableName =Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE;
//                collectionAOTTableName =Constants.IDAA_STATS_COLLECTION_TABLE_ACTIVE_AOT;
                insertStatement = "INSERT INTO " + collectionTableName;
            }

            insertStatement +=  " select TIMESTAMP('" + startDateForDB2 +"'), X.* from " 
                               + " (select xmlparse(document cast(? as CLOB(4M))) as history_xml from sysibm.sysdummy1) P," 
                               + " XMLTABLE(" 
                               + " XMLNAMESPACES('http://www.ibm.com/xmlns/prod/dwa/2011' AS \"dwa\")," 
                               + " '$d/dwa:queryList/query' passing p.history_xml as \"d\" " 
                               + " COLUMNS" 
                               + " planID INTEGER PATH '@planID'" 
                               + " ,user CHAR(8) PATH '@user' " 
                               + " ,productID CHAR(8) PATH 'clientInfo/@productID'" 
                               + " ,clientUser CHAR(8) PATH 'clientInfo/@user'," 
                               + " workstation CHAR(18) PATH 'clientInfo/@workstation'," 
                               + " application CHAR(20) PATH 'clientInfo/@application'," 
                               + " locationName CHAR(16) PATH 'clientInfo/@locationName'," 
                               + " connName CHAR(8) PATH 'clientInfo/@connName'," 
                               + " connType CHAR(8) PATH 'clientInfo/@connType'," 
                               + " corrID CHAR(12) PATH 'clientInfo/@corrID'," 
                               + " authID CHAR(8) PATH 'clientInfo/@authID'," 
                               + " planName CHAR(8) PATH 'clientInfo/@planName'," 
                               + " accounting VARCHAR(201) PATH 'clientInfo/@accounting'," 
                               + " subSystemID CHAR(8) PATH 'clientInfo/@subSystemID'," 
                               + " state CHAR(20) PATH 'execution/@state'," 
                               + " submitTimestamp timestamp with timezone PATH 'execution/@submitTimestamp'," 
                               + " waitTimeSec INTEGER PATH 'execution/@waitTimeSec'," 
                               + " fetchTimeSec INTEGER PATH 'execution/@fetchTimeSec', " 
                               + " cpuTimeSec INTEGER PATH 'execution/@cpuTimeSec'," 
                               + " elapsedTimeSec INTEGER PATH 'execution/@elapsedTimeSec'," 
                               + " priority CHAR(20) PATH 'execution/@priority'," 
                               + " resultRows BIGINT PATH 'execution/@resultRows'," 
                               + " resultBytes BIGINT PATH 'execution/@resultBytes'," 
                               + " errorDescription VARCHAR(256) PATH 'execution/@errorDescription'," 
                               + " task INTEGER PATH 'task/@id'," 
                               + " sqltext VARCHAR(128) PATH 'sql' " 
                               + " ) AS X";

            CallableStatement insert = insertConnection.prepareCall(insertStatement);
            insert.setString(1, getQueriesResult);
            int result = insert.executeUpdate();
            System.out.println("Insert statement returned >" + result + "<. Current records per location >" + getNrQueriesPerLocation(collectionTableName) +"< in table >" + collectionTableName + "<.");
            insert.close();
            
        }

        System.out.println("Found " + pairs.size() + " queries within scope >" + scope + "< on >" + accelName +
                           "< for database >" + location + "<.");
        return pairs.size();

    }

    private static String getNrQueriesPerLocation(String collectionTableName) {
        
      String queryStatement ="SELECT rtrim(ltrim(locationname)) locationname, count(*) nrqueries "
            + " FROM " + collectionTableName
            + " GROUP BY rtrim(ltrim(locationname))";
      String result ="";
        
        CallableStatement groupQuery;
        try {
            groupQuery = queryConnection.prepareCall(queryStatement);
            ResultSet rs =groupQuery.executeQuery();
            
            String loc=null;
            int nr=0;
            int i=0;
            while(rs.next()) {
                ++i;
                if(i == 1) 
                    result="";
                else
                    result +=";";
                loc = rs.getString("LOCATIONNAME");
                nr = rs.getInt("NRQUERIES");
                result+=loc +":" + nr;
            }
            rs.close();
            groupQuery.close();

        } catch (SQLException e) {
            return e.toString();
        }
        
        return result;
    }

    private static class Pair {
        private int id;
        private String sql;

        public Pair(int id, String sql) {
            this.id = id;
            this.sql = sql;
        }

        public int hashCode() {
            return sql.hashCode();
        }

        public boolean equals(Object r) {
            Pair rhs = (Pair) r;
            return sql.equals(rhs.sql);
        }
    }
    public static String getVersion() {
        return version;
    }


}

