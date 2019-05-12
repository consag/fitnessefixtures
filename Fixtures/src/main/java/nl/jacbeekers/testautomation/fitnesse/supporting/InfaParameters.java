package nl.jacbeekers.testautomation.fitnesse.supporting;

public class InfaParameters {

    public final String PARAM_INFA_HOME ="INFA_HOME";
    public final String PARAM_INFA_CMD ="INFA_CMD";
    public final String PARAM_INFA_DOMAINS_FILE ="INFA_DOMAINS_FILE";
    public final String PARAM_INFA_DOMAIN ="INFA_DOMAIN";
    public final String PARAM_INFA_MRS ="INFA_MRS";
    public final String PARAM_INFA_DIS ="INFA_DIS";

    public final String PARAM_INFA_DEFAULT_DOMAIN ="INFA_DEFAULT_DOMAIN";
    public final String PARAM_INFA_SECURITY_DOMAIN ="INFA_DEFAULT_SECURITY_DOMAIN";
    public final String PARAM_INFA_DOMAIN_USER ="INFA_DEFAULT_DOMAIN_USER";
    public final String PARAM_INFA_DOMAIN_PASSWORD ="INFA_DEFAULT_DOMAIN_PASSWORD";

    public final String PARAM_INFA_TRUSTSTORE ="INFA_TRUSTSTORE";
    public final String PARAM_INFA_TRUSTSTORE_PASSWORD ="INFA_TRUSTSTORE_PASSWORD";
    public final String PARAM_INFA_KRB5_CONFIG ="KRB5_CONFIG";

    private String infaEnvironment ="DEMO";
    private String featureName;

    public String getInfaHome() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
                ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_HOME); }
    public String getInfacmd() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_CMD); }
    public String getInfaDomainsFile() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DOMAINS_FILE); }
    public String getInfaDomain() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN); }
    public String getInfaMRS() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_MRS); }
    public String getInfaDIS() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DIS); }
    public String getInfaDefaultDomain() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DEFAULT_DOMAIN); }
    public String getInfaDefaultSecurityDomain() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_SECURITY_DOMAIN); }
    public String getInfaDefaultDomainUser() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN_USER); }
    public String getInfaDefaultDomainPassword() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_DOMAIN_PASSWORD); }
    public String getInfaTruststore() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_TRUSTSTORE); }
    public String getInfaTruststorePassword() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_TRUSTSTORE_PASSWORD); }
    public String getInfaKerberosConfig() { return GetParameters.getPropertyVal(Constants.INFA_PLATFORM_PROPERTIES
            ,getFeatureName(),getInfaEnvironment() +"." + PARAM_INFA_KRB5_CONFIG); }


    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public String getFeatureName() { return this.featureName; }

    public void setInfaEnvironment(String infaEnvironment) { this.infaEnvironment = infaEnvironment; }
    public String getInfaEnvironment() { return this.infaEnvironment; }
}
