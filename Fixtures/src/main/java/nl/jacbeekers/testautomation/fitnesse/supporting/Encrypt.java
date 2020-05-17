package nl.jacbeekers.testautomation.fitnesse.supporting;
//package supporting;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Encrypt {
    /*
    private static final String key1 ="oas38seia0d72jvy";
    private static final String key2 ="supeThatIsConsag";
*/
    private static String currDate = Constants.NOT_INITIALIZED;
    private static String keystoreFile = Constants.DEFAULT_KEYSTORE;
    private static String errorMessage = Constants.NO_ERRORS;
    private static String result = Constants.OK;
    private static String errorCode =Constants.OK;

    private static  byte[] salt = new byte[0];

    /**
     * @param value
     * @return
     */
    public static String encrypt(String value) {
        return encrypt(Constants.DEFAULT_KEYSTORE, value);
    }
    public static String encrypt(String keystoreFilename, String plainText) {
        if(plainText == null || keystoreFilename == null) {
            return null;
        }

        setKeystoreFilename(keystoreFilename);

        Security.addProvider(new BouncyCastleProvider());
        SecretKeyFactory keyFactory;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBEWITHMD5AND256BITAES-CBC-OPENSSL", "BC");
            getTheKey();
            if (Constants.ERROR.equals(getResult())) {
                handleException("getkey - " + getErrorMessage());
                return getErrorMessage();
            }
            PBEKeySpec keySpec = new PBEKeySpec(getTheKey());
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 0);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generateSecret(keySpec), paramSpec);
            try {
                byte[] encrypted = cipher.doFinal(plainText.getBytes());
                System.out.println("encrypted string:\n"
                        + Base64.encodeBase64String(encrypted));
                return Base64.encodeBase64String(encrypted);

            } catch (IllegalBlockSizeException | BadPaddingException e) {
                handleException("encrypt.keyfactory.cipher.init.doFinal - " + e.toString());
            }


        } catch (NoSuchProviderException | NoSuchPaddingException | InvalidKeySpecException | NoSuchAlgorithmException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            handleException("encrypt.secretkeyfactory.getinstance - " + e.toString());
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
                 encrypt(args[0]);
        }

    private static char[] getTheKey() {
        File inFile = null;
        String theKeyAsString = Constants.NOT_INITIALIZED;

        URL u =Decrypt.class.getClassLoader().getResource(getKeystoreFile());
        if (u == null) {
            // try to read it directly
            inFile = new File(getKeystoreFile());
        } else {
            inFile = new File(u.getFile());
        }
        if( inFile.exists()) {
            try (Scanner scanner = new Scanner(inFile)) {
                theKeyAsString = scanner.nextLine();
            } catch (IOException i) {
                File file = new File(getKeystoreFile());
                try {
                    setErrorMessage("Error reading key from keystore >" + getKeystoreFile() + "< being >" + file.getCanonicalPath() + "<.");
                } catch (IOException e) {
                    setErrorMessage("Error reading key from keystore >" + getKeystoreFile() + "< being >" + file.getAbsolutePath() + "<.");
                }
                return Constants.ERROR.toCharArray();
            }
        } else {
            try {
                setErrorMessage("Error reading key from keystore >" + getKeystoreFile() + "< being >" + inFile.getCanonicalPath() + "<. File does not exist.");
            } catch (IOException e) {
                setErrorMessage("Error reading key from keystore >" + getKeystoreFile() + "< being >" + inFile.getAbsolutePath() + "<. File does not exist.");
            }
        }

        try (Scanner scanner = new Scanner(inFile)) {
            theKeyAsString =scanner.nextLine();
        } catch(IOException i) {
            return Constants.ERROR.toCharArray();
        }
        return theKeyAsString.toCharArray();

    }
    private static void setKeystoreFilename(String filename) {
        keystoreFile = filename;
    }

    private static String getKeystoreFile() {
        return keystoreFile;
    }

    public static String getErrorMessage() {
        return errorMessage;
    }

    private static void setErrorMessage(String err) {
        errorMessage = err;
        setResult(Constants.ERROR);
    }

    private static void setResult(String msg) {
        result =msg;
    }
    public static String getResult() {
        return result;
    }

    private static void handleException(String msg) {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currDate = sdf.format(now);
        setErrorCode(ResultMessages.ERRCODE_DECRYPT);
        setErrorMessage(currDate  + " - " + msg);
        System.out.println("\nAn exception occurred: " + getErrorMessage() + "\n");


    }
    public static String getErrorCode(){
        return errorCode;
    }
    private static void setErrorCode(String errCode) {
        errorCode =errCode;
    }

}
