# Data Integeration FitNesse Fixtures
The FitNesse Fixtures are created as part of an overall solution for automating the test cycle.

## Linux installation ReadMe
The readme for Linux can be found in the directory 'linuxscripts', here on github: https://github.com/consag/fitnessefixtures/tree/master/linuxscripts

## Download
You can download the jar with all compiled fixtures from the release tab.

## Fixture documentation
Check the properties files, [Properties files on gist](https://gist.github.com/jacbeekers), here on github with examples for the fixture.properties, application.properties and other important properties files.

### Examples
#### Command Line
*   Get current version

    ```sh
    java -jar DataIntegrationFixtures.jar -version
    ```

*   Get fixtures list
    ```sh
    java -jar DataIntegrationFixtures.jar -listfixtures -jarlibpath Fixtures/target
    ```
    The example uses the option -jarlibpath to specify the location of the DataIntegrationFixtures jar to be used.

*   Get fixtures version numbers
    You can get the version of a specific fixture by specifiying its name. The example below lists the version numbers of all fixtures.
    
    ```sh
    java -jar DataIntegrationFixtures.jar -fixtureversions All -jarlibpath Fixtures/target
    ```
    The example uses the option -jarlibpath to specify the location of the DataIntegrationFixtures jar to be used.
    
#### Test Pages
Example test pages can be found in [resources/FrontPage](https://github.com/consag/fitnessefixtures/tree/master/Fixtures/src/main/resources/FrontPage "GitHub")

## FitNesse Configuration
Fixtures rely on various parameters that need to be set up in properties files. Not all fixtures use all properties files.
Check our [wiki](https://github.com/consag/fitnessefixtures/wiki/Fixture-configuration) for more information.

## Fixtures - Properties file examples
If you're wondering what you need to put in the properties files, check out our [examples on GitHub Gist](https://gist.github.com/search?utf8=%E2%9C%93&q=user%3Ajacbeekers+properties&ref=searchresults).

Examples can also be found in config folder in this repository.
Some scripts, like encrypt_for_fitnesse.sh, also use these properties files.

## Password encryption
Password encryption is enabled for connection.properties (used by database fixtures) and security.properties (used only if you enable HTTPS). 
Use the encrypt_for_fitnesse.sh script (adjust it for Windows) to encrypt passwords.
Note: encrypt_for_fitnesse.sh requires the file config/siteKey. This file must contain one line only and will be used as random seed for the encryption of passwords.

## HTTPS enablement
You can find the example configuration in config/security.properties, linuxscripts/plugins-https.properties and linuxscripts/startFitNesse-https.sh
The example uses myfitnesse.jks, which was generated with the following command:
keytool -genkey -keyalg RSA -alias selfsigned -keystore myfitnesse.jks -storepass <somethingYouNeedToKeepSecret> -validity 365.
Use a command that fits your needs and/or use a certificate from your favourite CA and place it in the jks.
  
Use OpenJDK to avoid Oracle license fees and to avoid JCE Export restrictions which lead to errors as we are using bouncycastle. If you cannot use OpenJDK, download the Unlimited Strength jars from Oracle (Google for java jce policy). The 1.8 files and how-to install are here: https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
