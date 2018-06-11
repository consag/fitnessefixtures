
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DIServerProperties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DIServerProperties">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DIServerName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Repositoryname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CanInfaServerDebugMapping" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DIServerMode" type="{http://www.informatica.com/wsh}EDIServerMode"/>
 *         &lt;element name="DIServerVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrentTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="StartupTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="ProductName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DIServerProperties", propOrder = {
    "diServerName",
    "repositoryname",
    "canInfaServerDebugMapping",
    "diServerMode",
    "diServerVersion",
    "currentTime",
    "startupTime",
    "productName"
})
public class DIServerProperties {

    @XmlElement(name = "DIServerName", required = true, nillable = true)
    protected String diServerName;
    @XmlElement(name = "Repositoryname", required = true, nillable = true)
    protected String repositoryname;
    @XmlElement(name = "CanInfaServerDebugMapping")
    protected boolean canInfaServerDebugMapping;
    @XmlElement(name = "DIServerMode", required = true, nillable = true)
    protected EDIServerMode diServerMode;
    @XmlElement(name = "DIServerVersion", required = true, nillable = true)
    protected String diServerVersion;
    @XmlElement(name = "CurrentTime", required = true, nillable = true)
    protected DIServerDate currentTime;
    @XmlElement(name = "StartupTime", required = true, nillable = true)
    protected DIServerDate startupTime;
    @XmlElement(name = "ProductName", required = true, nillable = true)
    protected String productName;

    /**
     * Gets the value of the diServerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIServerName() {
        return diServerName;
    }

    /**
     * Sets the value of the diServerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIServerName(String value) {
        this.diServerName = value;
    }

    /**
     * Gets the value of the repositoryname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryname() {
        return repositoryname;
    }

    /**
     * Sets the value of the repositoryname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryname(String value) {
        this.repositoryname = value;
    }

    /**
     * Gets the value of the canInfaServerDebugMapping property.
     * 
     */
    public boolean isCanInfaServerDebugMapping() {
        return canInfaServerDebugMapping;
    }

    /**
     * Sets the value of the canInfaServerDebugMapping property.
     * 
     */
    public void setCanInfaServerDebugMapping(boolean value) {
        this.canInfaServerDebugMapping = value;
    }

    /**
     * Gets the value of the diServerMode property.
     * 
     * @return
     *     possible object is
     *     {@link EDIServerMode }
     *     
     */
    public EDIServerMode getDIServerMode() {
        return diServerMode;
    }

    /**
     * Sets the value of the diServerMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDIServerMode }
     *     
     */
    public void setDIServerMode(EDIServerMode value) {
        this.diServerMode = value;
    }

    /**
     * Gets the value of the diServerVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIServerVersion() {
        return diServerVersion;
    }

    /**
     * Sets the value of the diServerVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIServerVersion(String value) {
        this.diServerVersion = value;
    }

    /**
     * Gets the value of the currentTime property.
     * 
     * @return
     *     possible object is
     *     {@link DIServerDate }
     *     
     */
    public DIServerDate getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the value of the currentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServerDate }
     *     
     */
    public void setCurrentTime(DIServerDate value) {
        this.currentTime = value;
    }

    /**
     * Gets the value of the startupTime property.
     * 
     * @return
     *     possible object is
     *     {@link DIServerDate }
     *     
     */
    public DIServerDate getStartupTime() {
        return startupTime;
    }

    /**
     * Sets the value of the startupTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServerDate }
     *     
     */
    public void setStartupTime(DIServerDate value) {
        this.startupTime = value;
    }

    /**
     * Gets the value of the productName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductName(String value) {
        this.productName = value;
    }

}
