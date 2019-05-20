defaultOutputFile=output.csv
fitnesseInstall=/appl/testautomation/fitnesse

jsonFile=$1
outputFile=$2
nrRecords=$3

if [ -z "$jsonFile" ] ; then
   echo "$(date) - $0 - No json input file provided. Please do so."
   exit 1
fi

if [ -z "${outputFile}" ] ; then
   echo "$(date) - $0 - No output file provided. Output will be written to >$defaultOutputFile<."
   outputFile=$defaultOutputFile
fi

if [ -z "$nrRecords" ] ; then
   echo "$(date) - $0 - number of records to generate must be specified as 3rd argument."
   exit 2
fi

java -jar ${fitnesseInstall}/lib/generator.jar generate --quiet --max-rows=10000 --replace $jsonFile $outputFile

