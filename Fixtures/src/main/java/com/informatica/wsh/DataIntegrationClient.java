package com.informatica.wsh;

import javax.xml.ws.WebServiceRef;
// !THE CHANGES MADE TO THIS FILE WILL BE DESTROYED IF REGENERATED!
// This source file is generated by Oracle tools
// Contents may be subject to change
// For reporting problems, use the following
// Version = Oracle WebServices (11.1.1.0.0, build 101221.1153.15811)

public class DataIntegrationClient
{
  @WebServiceRef
  private static DataIntegrationService dataIntegrationService;

  public static void main(String [] args)
  {
    dataIntegrationService = new DataIntegrationService();
    DataIntegrationInterface dataIntegrationInterface = dataIntegrationService.getDataIntegration();
    // Add your code to call the desired methods.
  }
}
