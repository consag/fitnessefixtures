
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InitializeDIServerConnectionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InitializeDIServerConnectionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LoginHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DIServerName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DIServerDomain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InitializeDIServerConnectionRequest", propOrder = {
    "loginHandle",
    "diServerName",
    "diServerDomain"
})
public class InitializeDIServerConnectionRequest {

    @XmlElement(name = "LoginHandle", required = true, nillable = true)
    protected String loginHandle;
    @XmlElement(name = "DIServerName", required = true)
    protected String diServerName;
    @XmlElement(name = "DIServerDomain", required = true, nillable = true)
    protected String diServerDomain;

    /**
     * Gets the value of the loginHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginHandle() {
        return loginHandle;
    }

    /**
     * Sets the value of the loginHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginHandle(String value) {
        this.loginHandle = value;
    }

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
     * Gets the value of the diServerDomain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIServerDomain() {
        return diServerDomain;
    }

    /**
     * Sets the value of the diServerDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIServerDomain(String value) {
        this.diServerDomain = value;
    }

}
