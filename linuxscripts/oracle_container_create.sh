# Clone oracle/docker-images
# Go to directory docker-images/OracleDatabase/SingleInstance/dockerfiles
# Download the version you want into the correct directory (read more in the https://github.com/oracle/docker-images/blob/master/OracleDatabase/SingleInstance/README.md
#
oracle@localhost dockerfiles]$ ./buildDockerImage.sh -h

Usage: buildDockerImage.sh -v [version] [-e | -s | -x] [-i] [-o] [Docker build option]
Builds a Docker Image for Oracle Database.

Parameters:
   -v: version to build
       Choose one of: 11.2.0.2  12.1.0.2  12.2.0.1  18.3.0  18.4.0  19.3.0
   -e: creates image based on 'Enterprise Edition'
   -s: creates image based on 'Standard Edition 2'
   -x: creates image based on 'Express Edition'
   -i: ignores the MD5 checksums
   -o: passes on Docker build option

* select one edition only: -e, -s, or -x

LICENSE UPL 1.0

Copyright (c) 2014-2019 Oracle and/or its affiliates. All rights reserved.
IMPORTANT: The resulting images will be an image with the Oracle binaries installed. On first startup of the container a new database will be created, the following lines highlight when the database is ready to be used:

#########################
DATABASE IS READY TO USE!
#########################

