package nl.consag.testautomation.database;

import java.text.SimpleDateFormat;

import nl.consag.testautomation.supporting.Constants;
import nl.consag.testautomation.IDAATools.TableManagement;

public class IdaaProcedures {

    private static final Object className ="IdaaProcedures";
    private static String resultMessage =Constants.NOT_INITIALIZED;
    private String startDate =Constants.NOT_INITIALIZED;
    private Object context =Constants.NOT_INITIALIZED;
    private String databasename =Constants.NOT_INITIALIZED;
    private String tablename =Constants.NOT_INITIALIZED;
    private String idaaname =Constants.NOT_INITIALIZED;
    private String action =Constants.NOT_INITIALIZED;
    private String schemaname =Constants.NOT_INITIALIZED;
    
    private String result =Constants.OK;
    private String errorMessage =Constants.NO_ERRORS;

    public IdaaProcedures() {
          java.util.Date started = new java.util.Date();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          this.startDate = sdf.format(started);
          this.context=className;
        }

    public IdaaProcedures(String context) {
          java.util.Date started = new java.util.Date();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          this.startDate = sdf.format(started);
          this.context=context;
        }

    public void setDatabaseName(String databaseName) {
      this.databasename = databaseName;
    }
    public String getDatabaseName(){
        return this.databasename;
    }

    public void setTableName(String tableName) {
        
        if(tableName.contains(".")) {
            setSchemaName(tableName.substring(0, tableName.indexOf(".") -1));
            this.tablename = tableName.substring(tableName.indexOf(".") +1);
        } else {
            this.tablename =tableName;
        }
    }
    public String getTableName(){
        return this.tablename;
    }
    
    public void setIdaaName(String idaaName) {
        this.idaaname =idaaName;
    }
    public String getIdaaName() {
        return this.idaaname;
    }

    public void setAction(String action) {
        this.action = action;
    }
    private String getAction() {
        return this.action;
    }

    public String result() {
        
        TableManagement idaaMgmt = new TableManagement();
        if(verifyAction(getAction()).equals(Constants.OK)) {
            setResult(idaaMgmt.loadTable(getIdaaName(), getDatabaseName(), getSchemaName(), getTableName()));
            if(idaaMgmt.getResult().equals(Constants.OK)) {
                setResultMessage(Constants.OK, idaaMgmt.getResultMessage());
            }    else {
                setErrorMessage(Constants.ERROR, idaaMgmt.getErrorMessage());
            }
        } else {
            setErrorMessage("Invalid IDAA Action >" + getAction() +"< specified.");
        }
        return getResult();
    }

    private void setErrorMessage(String err) {
        setResult(Constants.ERROR);
        this.errorMessage = err;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    private void setResult(String result) {
        this.result =result;
    }
    public String getResult() {
        return result;
    }

    public void setSchemaName(String schemaName) {
        this.schemaname = schemaName;
    }
    public String getSchemaName() {
        return this.schemaname;
    }

    private String verifyAction(String providedAction) {
        boolean actionValid =false;
        
        switch (providedAction.toUpperCase()) {
        case "SYNC":
            actionValid =true;
            break;
        default:
            actionValid =false;
        }
        
        if(actionValid) {
            return Constants.OK;
        } else {
            return Constants.ERROR;
        }
    }

    private void setResultMessage(String msg) {
        resultMessage =msg;
    }
    public String getResultMessage() {
        return resultMessage;
    }

    private void setResultMessage(String code, String msg) {
        setResult(code);
        setResultMessage(msg);
    }

    private void setErrorMessage(String code, String msg) {
        setResult(code);
        setErrorMessage(msg);
    }
}
