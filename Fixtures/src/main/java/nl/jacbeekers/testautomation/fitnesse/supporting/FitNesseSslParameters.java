package nl.jacbeekers.testautomation.fitnesse.supporting;
import fitnesse.socketservice.SslParameters;

import static nl.jacbeekers.testautomation.fitnesse.supporting.GetParameters.getPropertyVal;

public class FitNesseSslParameters extends SslParameters{
    String version = "20190628.0";

    public FitNesseSslParameters() {
        super();

        getSetMyKeyStoreFilename();
        if(Constants.OK.equals(getSetMyKeyStorePassword())) {
            getSetMyTrustStoreFilename();
        } else {
            System.out.println("Error determining password.");
        }
    }

    private String getSetMyKeyStoreFilename(){
        String propVal = getMyKeyStoreFilename();
        if(Constants.DEFAULT_PROPVALUE.equals(propVal)) {
            setKeyStoreFilename(Constants.DEFAULT_FITNESSE_KEYSTORE_FILENAME);
        } else {
            setKeyStoreFilename(propVal);
        }
        return Constants.OK;
    }

    private String getMyKeyStoreFilename(){
        return getPropertyVal(Constants.SECURITY_PROPERTIES, Constants.PARAM_FITNESSE_KEYSTORE_FILENAME);
    }

    private String getSetMyKeyStorePassword() {
        String propVal = getPropertyVal(Constants.SECURITY_PROPERTIES, Constants.PARAM_FITNESSE_KEYSTORE_PASSWORD);
        Decrypt decrypt = new Decrypt();
        String result = Decrypt.decrypt(propVal);
        if(Constants.OK.equals(decrypt.getErrorCode())) {
//            System.out.println("Password decryption successful.");
            setKeyStorePassword(result);
        }
        else {
            System.out.println("Password decryption failed >" + decrypt.getErrorCode() + " - " + decrypt.getErrorMessage() + "<.");
            return ResultMessages.ERRCODE_DECRYPT;
        }
        return Constants.OK;
    }

    private void getSetMyTrustStoreFilename(){
        String propVal = getPropertyVal(Constants.SECURITY_PROPERTIES, Constants.PARAM_FITNESSE_TRUSTSTORE_FILENAME);
        if(Constants.DEFAULT_PROPVALUE.equals(propVal)) {
            setKeyStoreFilename(Constants.DEFAULT_FITNESSE_TRUSTSTORE_FILENAME);
        } else {
            setKeyStoreFilename(propVal);
        }
    }

    public String getVersion() {
        return version;
    }

}
