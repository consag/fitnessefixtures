/*
 * @author Jac. Beekers
 * @since   January 2016
 */

package nl.jacbeekers.testautomation.fitnesse;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.zip.ZipEntry;

import ml.options.Options;

import nl.jacbeekers.testautomation.fitnesse.supporting.Constants;
import nl.jacbeekers.testautomation.fitnesse.supporting.Encrypt;

public class FitNesseFixtures {
    private static final String className="FitNesseFixtures";
    private static String jarPrefix = "DataIntegrationFixtures";
    private static final String version = "20200515.1";
    private static int logLevel = 3;
    private static int logEntries = 0;
    private static boolean noClassFilter =true;
    private static String keystoreFile = Constants.NOT_PROVIDED;

    private static String pathName =".";
    private static String jarExtension = "jar";

    /**
     * @param args
     */
    public static void main(String[] args) {

        FitNesseFixtures fitNesseFixtures = new FitNesseFixtures();
        Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ZERO_OR_ONE);
        opt.getSet().addOption("version")
            .addOption("help")
            .addOption("?")
            .addOption("listfixtures")
            .addOption("fixturelist")
            .addOption("noclassfilter")
            .addOption("loglevel", Options.Separator.BLANK)
            .addOption("fixtureversion", Options.Separator.BLANK)
            .addOption("encrypt", Options.Separator.BLANK)
            .addOption("keystorefile", Options.Separator.BLANK)
            .addOption("jarlibpath", Options.Separator.BLANK)
             ;
        
        if(!opt.check(false, false)) {
            errMsg("Invalid arguments specified. Found >" + Integer.toString(args.length) +"< command line argument(s).");
            errMsg(opt.getCheckErrors());
//            errMsg(opt.toString());
            usage();
            System.exit(1);
        }

        if(opt.getSet().isSet("jarlibpath")) {
            setPathName(opt.getSet().getOption("jarlibpath").getResultValue(0));
        }

        if(opt.getSet().isSet("version")) {
            outMsg("Version: " +getVersion());
        }
        if(opt.getSet().isSet("help") || opt.getSet().isSet("?")) {
            usage();
        }
        if(opt.getSet().isSet("loglevel")) {
            logLevel =Integer.parseInt(opt.getSet().getOption("loglevel").getResultValue(0));
        }
        if(opt.getSet().isSet("noclassfilter")) {
            noClassFilter=true;
        } else {
            noClassFilter=false;
        }
        if(opt.getSet().isSet("listfixtures") || opt.getSet().isSet("fixturelist")) {
            outFixtureList();
        }
        if(opt.getSet().isSet("keystorefile")) {
            keystoreFile =opt.getSet().getOption("keystorefile").getResultValue(0);
        }
        if(opt.getSet().isSet("encrypt")) {
            encryptString(opt.getSet().getOption("encrypt").getResultValue(0));
        }
        if(opt.getSet().isSet("fixtureversion")) {
            String fixtureName= opt.getSet().getOption("fixtureversion").getResultValue(0);
            if(Constants.ALL.equalsIgnoreCase(fixtureName)) {
                outVersionForAllFixtures();
            } else {
                    outMsg(getFixtureVersion(fixtureName));                
            }
        }

    }

    public static String encryptString(String plainTextString) {
        if(Constants.NOT_PROVIDED.equals(keystoreFile)) {
            return Encrypt.encrypt(plainTextString);
        } else {
            return Encrypt.encrypt(keystoreFile, plainTextString);
        }

    }
    public static String getVersion() {
        return version;
    }

    private String getLogLevel() {
        return Constants.logLevel.get(getIntLogLevel());
    }

    /**
     * @return
     */
    private static Integer getIntLogLevel() {
        return logLevel;
    }


    private static void log(String level, String logText) {
        if (Constants.logLevel.indexOf(level.toUpperCase()) > getIntLogLevel()) {
            return;
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);

        //Level FATAL and ERROR should go to stderr
        if(Constants.logLevel.indexOf(level.toUpperCase()) < 2) {
            errMsg(formattedDate + ": " 
                      + level + " - " 
                       +logText);
            
        } else {
            outMsg(formattedDate + ": " 
                  + level + " - " 
                   +logText);
        }
    }

    private static void outMsg(String msg) {
        System.out.println(msg);
    }
    private static void errMsg(String msg) {
        System.err.println(msg);
    }

    private static List<String> getFixtureList(String prefixFilter) {

        List<String> resultList = new ArrayList<String>();
        File dir = new File(getPathName());

        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith(getJarPrefix()) && name.endsWith("." + getJarExtension());
            }
        });
        File jarFilename = null;
        if(matches != null && matches.length > 0) {
            Arrays.sort(matches);
            // Take the last entry
            jarFilename = matches[matches.length - 1];
        } else {
            log(Constants.ERROR, "Could not find any DataIntegrationFixtures jar file in directory >" + getPathName() + "< being >" +
                    Paths.get(getPathName()).toAbsolutePath().normalize().toString() + "<.");
            return resultList;
        }
        
        try {
            JarFile jar = new JarFile(jarFilename);
            Enumeration<? extends JarEntry> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry =enumeration.nextElement();
                String className =zipEntry.getName();
                className =className.replace("/",".");
                if(className.endsWith(".class") && (Constants.ALL.equals(prefixFilter) || (className.startsWith(prefixFilter) && !className.contains("$")))) {
                    resultList.add(className.replace(".class",""));
                    log(Constants.DEBUG, "Added >" +className.replace(".class","") +"< to list.");
                }
            }
            
        } catch (IOException e) {
            errMsg("Could not find jar file >" +jarFilename +"<.");
            return resultList;
        }
        return resultList;
        
    }
    
    private static List<String> getFixtureList() {

        if(noClassFilter)
            return getFixtureList(Constants.ALL);
            else
            return getFixtureList(Constants.DEFAULT_CLASS_PREFIX);
        
    }

    private static void usage() {
        outMsg("The following command line options are available:");
        outMsg("-help - This message.");
        outMsg("-version - The version of this class. For version info about fixtures, use -fixtureversion");
        outMsg("-listfixtures|-fixturelist - A list of available Consag FitNesse fixtures");
        outMsg("-fixtureversion <fixturename>|" +Constants.ALL +" - Shows the version of a specified fixture (or all)");
        outMsg("-noclassfilter - When processing the class list, do not filter on >" + Constants.DEFAULT_CLASS_PREFIX +"<.");
        outMsg("-encrypt <plaintextpassword> - Encrypts password for use in connection.properties");
    }

    private static String getFixtureVersion(String fixtureName) {
        String rc =Constants.UNKNOWN;
        double rcd=0;
        
        try {
            Class<?> c = Class.forName(fixtureName);
            Method getVersion = c.getDeclaredMethod("getVersion");
            rc = (String) getVersion.invoke(null);
            rc ="Fixture >" +fixtureName +"< is at version >" +rc +"<.";
        }
                catch (ClassNotFoundException e) {
                   rc= "Unknown fixture >" +fixtureName+"<. Error =>" +e.toString() +"<.";
                }
         catch (NoSuchMethodException e) {
            rc="Class >" +fixtureName+"< does not (yet) have a getVersion method.";
        } catch (IllegalAccessException e) {
            rc= "Cannot access fixture class >" +fixtureName +"<. Error =>" +e.toString() + "<.";
        } catch (InvocationTargetException e) {
            rc= "Could not invoke 'getVersion' method of fixture class >" +fixtureName +"<. Error =>" +e.toString() + "<.";
        } catch (NullPointerException e) {
            rc= "Could not determine version for >" +fixtureName +"<. A null pointer exception occurred.";
        }  catch (NoClassDefFoundError e) {
            rc= "Could not determine version for >" + fixtureName +"<. A class not found error occurred. Error >" + e.toString() +"<.";
        }
        return rc;
        
    }

    private static void outVersionForAllFixtures() {
        
        for (String fixture: getFixtureList(Constants.DEFAULT_CLASS_PREFIX)) {
            outMsg(getFixtureVersion(fixture));
        }
        
    }

    private static void outFixtureList() {
        
        for(String fixture: getFixtureList()) {
            outMsg(fixture);
        }
        
    }

    public static String getPathName() {
        return pathName;
    }

    public static void setPathName(String pathName) {
        FitNesseFixtures.pathName = pathName;
    }

    public static String getJarPrefix() {
        return jarPrefix;
    }

    public static void setJarPrefix(String jarPrefix) {
        FitNesseFixtures.jarPrefix = jarPrefix;
    }

    public static String getJarExtension() {
        return jarExtension;
    }

    public static void setJarExtension(String jarExtension) {
        FitNesseFixtures.jarExtension = jarExtension;
    }
}
