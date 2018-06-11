# Readme for FitNesse on Linux
Updated: 07 June 2015
Tested: 07 June 2015 on Oracle Linux 6.6

This readme shows the steps to start, stop and make FitNesse bootable on Linux.
The scripts in this section were tested on Oracle Linux 6.6 and RedHat Linux 6.6.
In this readme we use 
* /appl/testautomation/fitnesse as installation directory
* /appl/testautomation/testpages as starting point for our test pages

##Steps to install FitNesse
1. Create a directory where you want to install FitNesse and a directory in which you are going to store your test pages (assuming you want to separate the FitNesse files from your test pages)
2. Get FitNesse from http://fitnesse.org
3. Run FitNesse once in order to get the files extracted
4. Put the scripts, mentioned below, in place
5. Start FitNesse

##Step 1 - Create installation and test directory

```shell
mkdir -p /appl/testautomation/fitnesse
mkdir -p /appl/testautomation/testpages
```
##Step 2 - Get FitNesse
From http://fitnesse.org get the fitnesse-standalone.jar and store it in the installation directory.
If you can use wget, the commands would be:

```shell
cd /appl/testautomation/fitnesse
wget http://fitnesse.org/fitnesse-standalone.jar
```
##Step 3 - Extract files
We need to run java with the downloaded jar one time to extract some files. In the next step we will specify a port, directory and some other options to start FitNesse the way we want to.
For now, run:

```shell
cd /appl/testautomation/fitnesse
java -jar fitnesse-standalone.jar
```
You should see some messages about files extracting and probably that you cannot start FitNesse using port 80. Just ignore it. As said before, we will fix it in the next step.

In our setup, we want to separate the FitNesse software from our test pages content. Therefore we need to copy the default pages, installed with FitNesse, as a starting point for our own test pages.
You don't need to do this if you don't need the default FrontPage or help pages, but I advise you to copy them.
Run the following command:

```shell
cp -pr /appl/testautomation/fitnesse/FitNesseRoot /appl/testautomation/testpages/TestPagesRoot
```

##Step 4 - Put scripts in place
After you downloaded the scripts, you will need to make some minor modifications.
Download the scripts from https://github.com/consag/fitnessefixtures/tree/master/linuxscripts.

####Change file procFitNesse
The script procFitNesse contains the installation directory and the owner of the FitNesse installation. In case you did not use /appl/testautomation/fitnesse, you must change the `INSTALLDIR` variable.
If your user is not "oracle" you must change the `FIT_OWNER` variable.
Change the following lines according your installaton:

```shell
FIT_OWNER="oracle"
INSTALLDIR="/appl/testautomation/fitnesse"
```
####Change file startFitNesse
The script startFitNesse contains the installation directory and the directory for your test pages. If you don't want to use the defaults, you need to change the following lines:

```shell
INSTALLDIR="/appl/testautomation/fitnesse"
TESTPAGEDIR="/appl/testautomation/testpages"
```
####Change file stopFitNesse
The script stopFitNesse contains the installation directory. If you're installing FitNesse in a different directory than the default, you need to change the following line:

```shell
INSTALLDIR="/appl/testautomation/fitnesse"
```
####Steps to make FitNesse start at boot time
The steps needed to start FitNesse at boot time of the server are listed in the script makebootable.README.
The script assumes that you have appropriate sudo rights. If that is not the case, you need to ask your sysadmin to run the commands.

```shell
#- copy procFitNesse as fitnesse under init.d
sudo cp -p procFitNesse /etc/init.d/fitnesse

#- root will run this
sudo chown root /etc/init.d/fitnesse

#- Make the service script executable
sudo chmod 750 /etc/init.d/fitnesse
 
#- Associate the fitnesse service with the appropriate run levels and set it to auto-start using the following command.
sudo chkconfig --add fitnesse
```
Note: Even if you decide not to ask your sysadmin, you can still use startFitnesse.sh and stopFitnesse.sh to start and stop your FitNesse server.


##Overview of required files
This section is for reference only.
###file: fitnesse-standalone.jar
Get this file from the FitNesse website: http://fitnesse.org

All files mentioned below are on github: https://github.com/consag/fitnessefixtures/tree/master/linuxscripts

###file: plugins.properties
Not a required file for FitNesse, but highly advised to use. It contains settings about versioning your test pages amongst others.

###file: procFitNesse
This file is the controlling script that will be used to start FitNesse on boot time. 
It can also be used, once you followed all steps as mentioned in 'makebootable.README', to start or stop FitNesse.
Note: You cannot use this script when you did not create the Linux service.

###file: makebootable.README
This file lists the steps you need to go through for making FitNesse bootable. You will need sudo rights or be root yourself.


###file: startFitNesse.sh
Script used by the fitnesse script during boot time to start FitNesse.
You can also use this script to start FitNesse manually or through other means (e.g. with the Linux nohup command).

###file: stopFitNesse.sh
Script used on server shutdown to stop FitNesse. You can also use this script to stop FitNesse manually.

##Potential errors
####Get a Java error?
It could be that java cannot be found or is too old.
Make sure java can be found and it is at least 1.6. Java 1.7 and 1.8 also work fine.
Check the java version with `java -version`.
If you don't have java installed, download it from Oracle: http://www.oracle.com/technetwork/java/javase/downloads/index.html
Personally, I always download the JDK tar.gz file. It allows me to unzip into any directory. The rpm version is for sysadmins that need to install it on a central location for all users to use.
After the installation of java you may need to set JAVA_HOME and change your PATH so your java version is found and not the one you don't want.


