package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.File;

import java.io.IOException;

import java.net.URL;

import java.nio.charset.Charset;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.security.Security;

import java.text.SimpleDateFormat;

import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class Decrypt {

    private static String currDate = Constants.NOT_INITIALIZED;
    private static final Charset PLAIN_TEXT_ENCODING = Charset.forName("UTF-8");
    private static String encryptedString = Constants.NOT_INITIALIZED;
    private static String keystoreFile = Constants.DEFAULT_KEYSTORE;
    private static String errorMessage = Constants.NO_ERRORS;
    private static String result = Constants.OK;
    private static String errorCode = Constants.OK;

    public Decrypt() {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currDate = sdf.format(started);
    }

    public Decrypt(String encrypted) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currDate = sdf.format(started);
        keystoreFile = Constants.DEFAULT_KEYSTORE;
        encryptedString = encrypted;
    }

    public Decrypt(String keystore, String encrypted) {
        java.util.Date started = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currDate = sdf.format(started);
        keystoreFile = keystore;
        encryptedString = encrypted;
    }

//    private static SecretKey secretKey ;

//    private static final String saltedPrefix = "Salted__";

    //    private static  int saltLength = 0;
    private static byte[] salt = new byte[0];
    private static int offset = 0;

    public static String decrypt() {
        return decrypt(Constants.DEFAULT_KEYSTORE, encryptedString);
    }

    public static String decrypt(String stringToDecrypt) {
        return decrypt(Constants.DEFAULT_KEYSTORE, stringToDecrypt);
    }

    public static String decrypt(String keystoreFilename, String stringToDecrypt) {

        if (stringToDecrypt == null || keystoreFilename == null) {
            return null;
        }
        setKeystoreFilename(keystoreFilename);

        Security.addProvider(new BouncyCastleProvider());
        SecretKeyFactory keyFactory;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBEWITHMD5AND256BITAES-CBC-OPENSSL", "BC");
            getTheKey();
            if (Constants.ERROR.equals(getResult())) {
                handleException("getkey - " + "keyfile >" + getKeystoreFile() + "< - " + getErrorMessage());
                return getErrorMessage();
            }
            PBEKeySpec keySpec = new PBEKeySpec(getTheKey());
            byte[] encryptedTextBytes = DatatypeConverter.parseBase64Binary(stringToDecrypt);
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 0);
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keyFactory.generateSecret(keySpec), paramSpec);
                byte[] decryptedTextBytes = null;
                try {
                    decryptedTextBytes = cipher.doFinal(encryptedTextBytes, offset, encryptedTextBytes.length - offset);
                    return new String(decryptedTextBytes, PLAIN_TEXT_ENCODING);

                } catch (IllegalBlockSizeException e) {
                    handleException("decrypt.cipher.doFinal - " + e.toString());
                } catch (BadPaddingException e) {
                    handleException("decrypt.cipher.doFinal - " + e.toString());
                }

            } catch (Exception f) {
                handleException("decrypt.cipher.getinstance.init - " + f.toString());
            }

        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            handleException("decrypt.secretkeyfactory.getinstance - " + e.toString());
        }

        return getErrorCode();

    }

    public static String getErrorCode() {
        return errorCode;
    }

    private static void setErrorCode(String errCode) {
        errorCode = errCode;
    }

    private static void handleException(String msg) {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currDate = sdf.format(now);
        setErrorCode(ResultMessages.ERRCODE_DECRYPT);
        setErrorMessage(currDate + " - " + msg);

    }

    public static String getErrorMessage() {
        return errorMessage;
    }

    private static void setErrorMessage(String err) {
        errorMessage = err;
        setResult(Constants.ERROR);
    }

    private static void setResult(String msg) {
        result = msg;
    }

    public static String getResult() {
        return result;
    }

    private static char[] getTheKey() {
        File inFile = null;
        String theKeyAsString = Constants.NOT_INITIALIZED;

        //System.out.print("getTheKey - keyfile is >" + getKeystoreFile() + "<.");
        URL u = Decrypt.class.getClassLoader().getResource(getKeystoreFile());
        if (u == null) {
            // try to read it directly
            inFile = new File(getKeystoreFile());
        } else {
            inFile = new File(u.getFile());

        }

        try (Scanner scanner = new Scanner(inFile)) {
            theKeyAsString = scanner.nextLine();
        } catch (IOException i) {
            setErrorMessage("Error reading key from keystore.");
            return Constants.ERROR.toCharArray();
        }
        return theKeyAsString.toCharArray();

    }

    public static void setKeystoreFilename(String filename) {
        keystoreFile = filename;
    }

    private static String getKeystoreFile() {
        return keystoreFile;
    }
}
