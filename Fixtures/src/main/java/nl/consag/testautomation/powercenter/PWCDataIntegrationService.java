/**
 * This purpose of this fixture is to start an Informatica Data Integration Process for DQ: Profile and Score card
 * @author Jac Beekers
 * @version 26 October 2014
 */
package nl.consag.testautomation.powercenter;

import com.informatica.wsh.*;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import nl.consag.testautomation.supporting.Logging;
import nl.consag.testautomation.supporting.GetParameters;

@WebServiceClient(wsdlLocation="http://localhost:7333/wsh/services/BatchServices/DataIntegration?WSDL",
  targetNamespace="http://www.informatica.com/wsh", name="DataIntegrationService")
public class PWCDataIntegrationService
  	extends com.informatica.wsh.DataIntegrationService {
    private static String version ="20160116.0";
    
	private static URL wsdlLocationURL;
	private static String m_application = "unknown";
	private static String m_WsdlUrl="unknown";
	private static String logText="none";
	private static String myName="constructor-static";
	private static String location="init";
  
	private static Logger logger;

	public PWCDataIntegrationService() {
		super(wsdlLocationURL,
				new QName("http://www.informatica.com/wsh", "DataIntegrationService"));
	}

	public PWCDataIntegrationService(String applicationName) {
		myName="PWCDataIntegrationServiceString";
		location="Get Parameters";
		URL baseUrl = DataIntegrationService.class.getResource(".");

		m_application = applicationName;
		m_WsdlUrl = GetParameters.getWshUrl(m_application);
		logText="Read from parameter file url=>" + m_WsdlUrl + "<.";
		Logging.LogEntry(myName, "debug", location, logText);

		location="create DataIntegrationService";

		try {
			if (!baseUrl.getPath().endsWith("/")) {
				baseUrl = new URL(baseUrl, baseUrl.getPath() + "/");
			}
			wsdlLocationURL =    new URL(baseUrl,m_WsdlUrl);
		}
		catch (MalformedURLException e) {
			location="URL Exception";
			logText="Failed to create wsdlLocationURL using =>" +m_WsdlUrl +"<.";
			Logging.LogEntry(myName, "error", location, logText);
			logger.log(Level.ALL,
					"Failed to create wsdlLocationURL using =>" +m_WsdlUrl +"<.",
					e);
		}

		location="create service call";
		logText="Determined URL =>" + wsdlLocationURL.toString() +"< from >" + m_WsdlUrl + "<.";
		Logging.LogEntry(myName, "debug", location, logText);

        PWCDataIntegrationService.create(wsdlLocationURL, new QName("http://www.informatica.com/wsh", "DataIntegrationService"));

        logText="After call";
        Logging.LogEntry(myName, "debug", location, logText);
  }


	public PWCDataIntegrationService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
  }

	public com.informatica.wsh.DataIntegrationInterface getDataIntegration() {
		//@WebEndpoint(name="DataIntegration")
		return (com.informatica.wsh.DataIntegrationInterface) super.getPort(new QName("http://www.informatica.com/wsh",
                                                                                  "DataIntegration"),
                                                                        com.informatica.wsh.DataIntegrationInterface.class);
  }

	public com.informatica.wsh.DataIntegrationInterface getDataIntegration(WebServiceFeature... features) {
		//@WebEndpoint(name="DataIntegration")
		return (com.informatica.wsh.DataIntegrationInterface) super.getPort(new QName("http://www.informatica.com/wsh",
                                                                                  "DataIntegration"),
                                                                        com.informatica.wsh.DataIntegrationInterface.class,
                                                                        features);
  }
    public static String getVersion() {
        return version;
    }
}