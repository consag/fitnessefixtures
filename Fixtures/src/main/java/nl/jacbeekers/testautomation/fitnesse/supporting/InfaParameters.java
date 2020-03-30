package nl.jacbeekers.testautomation.fitnesse.supporting;

public class InfaParameters {

    public final String PARAM_INFA_HOME ="INFA_HOME";
    public final String PARAM_INFA_CMD ="INFA_CMD";
    public final String PARAM_INFA_DOMAINS_FILE ="INFA_DOMAINS_FILE";
    public final String PARAM_INFA_DOMAIN ="INFA_DOMAIN";
    public final String PARAM_INFA_MRS ="INFA_MRS";
    public final String PARAM_INFA_DIS ="INFA_DIS";
    public final String PARAM_INFA_OSPROFILE ="INFA_OSPROFILE";

    public final String PARAM_INFA_DEFAULT_DOMAIN ="INFA_DEFAULT_DOMAIN";
    public final String PARAM_INFA_SECURITY_DOMAIN ="INFA_DEFAULT_SECURITY_DOMAIN";
    public final String PARAM_INFA_DOMAIN_USER ="INFA_DEFAULT_DOMAIN_USER";
    public final String PARAM_INFA_DOMAIN_PASSWORD ="INFA_DEFAULT_DOMAIN_PASSWORD";

    public final String PARAM_INFA_TRUSTSTORE ="INFA_TRUSTSTORE";
    public final String PARAM_INFA_TRUSTSTORE_PASSWORD ="INFA_TRUSTSTORE_PASSWORD";
    public final String PARAM_INFA_KRB5_CONFIG ="KRB5_CONFIG";

    private String infaEnvironment;

    public static String logFilename = Constants.NOT_PROVIDED;
    public static String logLevel = Constants.VERBOSE;

    public static String getLogFilename() { return logFilename; }
    public static void setLogFilename(String logFilename) { InfaParameters.logFilename = logFilename; }

    public static String getLogLevel() { return logLevel; }
    public static void setLogLevel(String logLevel) { InfaParameters.logLevel = logLevel; }

    public String getInfaHome() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
                , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_HOME);
    }
    public String getInfacmd() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_CMD);
    }
    public String getInfaDomainsFile() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DOMAINS_FILE);
    }
    public String getInfaDomain() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN);
    }
    public String getInfaMRS() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_MRS);
    }
    public String getInfaDIS() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DIS);
    }
    public String getInfaDefaultDomain() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DEFAULT_DOMAIN);
    }
    public String getInfaDefaultSecurityDomain() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_SECURITY_DOMAIN);
    }
    public String getInfaDefaultDomainUser() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN_USER);
    }
    public String getInfaDefaultDomainPassword() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN_PASSWORD);
    }
    public String getInfaTruststore() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_TRUSTSTORE);
    }
    public String getInfaTruststorePassword() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_TRUSTSTORE_PASSWORD);
    }
    public String getInfaKerberosConfig() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_KRB5_CONFIG);
    }
    public String getInfaOSProfile() {
        GetParameters.setLogFilename(getLogFilename());
        GetParameters.setLogLevel(getLogLevel());
        return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
                , getInfaEnvironment(),getInfaEnvironment() +"." + PARAM_INFA_OSPROFILE);
    }

    public void setInfaEnvironment(String infaEnvironment) { this.infaEnvironment = infaEnvironment; }
    public String getInfaEnvironment() { return this.infaEnvironment; }
}
