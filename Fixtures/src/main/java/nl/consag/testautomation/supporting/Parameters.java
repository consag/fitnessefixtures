package nl.consag.testautomation.supporting;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public class Parameters {

    private  String errorMessage;
    private  String dbHostname =Constants.NOT_FOUND;
    private  String dbPort =Constants.NOT_FOUND;
    private  String dbLocation =Constants.NOT_FOUND;
    private  String dbUserId =Constants.NOT_FOUND;
    private  String dbEncryptedPassword =Constants.NOT_FOUND;
    private  String dbPassword =Constants.NOT_FOUND;
    
    private String result =Constants.OK;

    public String getCollectionDb() {
        return getPropertyVal(Constants.ZOS_PROPERTIES, Constants.IDAA_STATS_COLLECTION_DBPROP);
    }
    
    public String getDatabaseParameters(String databaseName) {

        setDbHostname(getPropertyVal(Constants.ZOS_PROPERTIES, databaseName + ".hostname"));
        setDbPort(getPropertyVal(Constants.ZOS_PROPERTIES, databaseName + ".port"));
        setDbLocation(getPropertyVal(Constants.ZOS_PROPERTIES, databaseName + ".location"));
        setDbUserId(getPropertyVal(Constants.ZOS_PROPERTIES, databaseName + ".monitor.userid"));

        //Decrypt decrypt = new Decrypt();
        setDbPassword(Decrypt.decrypt(getPropertyVal(Constants.ZOS_PROPERTIES, databaseName + ".monitor.password")));
        
        if (Constants.ERROR.equals(Decrypt.getResult())) {
            setErrorMessage("Decrypt returned an error: " + Decrypt.getErrorMessage());
            setDbPassword(Constants.NOT_FOUND);
            return Constants.ERROR;
        }

        if (Constants.NOT_FOUND.equals(dbHostname) || Constants.NOT_FOUND.equals(dbPort) ||
            Constants.NOT_FOUND.equals(dbLocation) || Constants.NOT_FOUND.equals(dbUserId)) {
                setErrorMessage("Database connectivity information incomplete.");
                return Constants.ERROR;
            }
        else
            return Constants.OK;
    }
    
    public  String getPropertyVal(String key) {
        return getPropertyVal(Constants.ZOS_PROPERTIES, key);
    }
    
    public String getPropertyVal(String propFilename, String key) {
        String val = Constants.DEFAULT_PROPVALUE;
        Properties prop = new Properties();
        /*
              * Get mapping
              */
        InputStream input = null;
        try {
            input = Parameters.class.getClassLoader().getResourceAsStream(propFilename);
            if (input == null) {
                setResult(Constants.ERRCODE_PROPFILE);
                return val;
            }

            prop.load(input);

            val = prop.getProperty(key, Constants.DEFAULT_PROPVALUE);
            if(Constants.DEFAULT_PROPVALUE.equals(val))
                setResult(Constants.ERRCODE_PROP_NOT_FOUND);
        } catch (FileNotFoundException e) {
            setResult(Constants.ERRCODE_PROPFILE_NOTFOUND);
            val = Constants.ERRCODE_PROPFILE_NOTFOUND;
        } catch (IOException e) {
            setResult(Constants.ERRCODE_PROPFILE_IOERROR);
            val = Constants.ERRCODE_PROPFILE_IOERROR;
        }
        return val;
    }

    private  void setDbHostname(String hostname) {
        dbHostname = hostname;
    }
    public  String getDbHostname() {
        return dbHostname;
    }

    private  void setDbPort(String port) {
        dbPort = port;
    }
    public  String getDbPort() {
        return dbPort;
    }
    
    private  void setDbLocation(String location) {
        dbLocation = location;
    }
    public  String getDbLocation(){
        return dbLocation;
    }

    private  void setDbUserId(String userId) {
        dbUserId = userId;
    }
    public  String getDbUserId() {
        return dbUserId;
    }

    private  void setDbEncryptedPassword(String encryptedPassword) {
        dbEncryptedPassword = encryptedPassword;
    }
    private  String getDbEncryptedPassword() {
        return dbEncryptedPassword;
    }

    private  void setDbPassword(String password) {
        dbPassword = password;
    }
    public  String getDbPassword() {
        return dbPassword;
    }

    private void setResult(String code) {
        result = code;
    }
    public String getResult() {
        return result;
    }

    private void setErrorMessage(String msg) {
        setResult(Constants.ERROR);
        errorMessage =msg;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
