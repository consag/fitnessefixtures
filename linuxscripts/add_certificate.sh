. ./fitnesse_env.sh

keytool -import -v -trustcacerts -alias ${thisHost}_fitnesse -file ${certLocation}/${thisHost}.cer -keystore ${fitKeyStore} -noprompt

