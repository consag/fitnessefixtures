if [ -z "$1" ] ; then
  read -s -p "Enter what needs to be encrypted: " encryptThis
else
  encryptThis="$1"
fi
java -cp config/:lib/DataIntegrationFixtures.jar:lib/commons-codec-1.10.jar:lib/bcprov-jdk15on-154.jar nl.jacbeekers.testautomation.fitnesse.supporting.Encrypt "$encryptThis"
