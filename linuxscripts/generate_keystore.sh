keytool -genkey -noprompt \
 -alias selfsigned \
 -dname "CN=demo, OU=demo, O=demo, L=demo, S=Demo, C=NL" \
 -keystore myfitnesse.jks \
 -storepass SomethingVerySecret \
 -keypass SomethingVerySecret


