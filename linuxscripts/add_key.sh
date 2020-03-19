. ./fitnesse_env.sh

openssl pkcs12 -inkey /data/security/certs/${thisHost}.key  -in /data/security/certs/${thisHost}.cer -export -out temp.pkcs12

keytool -importkeystore -srckeystore temp.pkcs12 -srcstoretype PKCS12 -destkeystore myfitnesse.jks

