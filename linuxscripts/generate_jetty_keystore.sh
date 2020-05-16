JETTY_HOME=/data/testautomation/jetty/current
keytool -importkeystore -srckeystore /data/security/certs/$(hostname -f).pkcs12 -srcstoretype PKCS12 -destkeystore $JETTY_HOME/dq-base/etc/keystore


