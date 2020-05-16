if [ -z "$1" ] ; then
  read -s -p "Enter what needs to be encrypted: " encryptThis
else
  encryptThis="$1"
fi
java -cp config/:../Fixtures/target/DataIntegrationFixtures-202005.0.jar:lib/commons-codec-1.14.jar:lib/bcprov-jdk15on-1.65.jar nl.jacbeekers.testautomation.fitnesse.supporting.Encrypt "$encryptThis"
