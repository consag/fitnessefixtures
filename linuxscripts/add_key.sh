. ./fitnesse_env.sh

openssl pkcs12 -inkey /data/security/certs/vm400022270.nl.eu.abnamro.com.key  -in /data/security/certs/vm400022270.nl.eu.abnamro.com.cer -export -out temp.pkcs12

keytool -importkeystore -srckeystore temp.pkcs12 -srcstoretype PKCS12 -destkeystore myfitnesse.jks

