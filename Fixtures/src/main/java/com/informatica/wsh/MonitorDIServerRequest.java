
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MonitorDIServerRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MonitorDIServerRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DIServiceInfo" type="{http://www.informatica.com/wsh}DIServiceInfo"/>
 *         &lt;element name="MonitorMode" type="{http://www.informatica.com/wsh}EDIServerMonitorMode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonitorDIServerRequest", propOrder = {
    "diServiceInfo",
    "monitorMode"
})
public class MonitorDIServerRequest {

    @XmlElement(name = "DIServiceInfo", required = true, nillable = true)
    protected DIServiceInfo diServiceInfo;
    @XmlElement(name = "MonitorMode", required = true)
    protected EDIServerMonitorMode monitorMode;

    /**
     * Gets the value of the diServiceInfo property.
     * 
     * @return
     *     possible object is
     *     {@link DIServiceInfo }
     *     
     */
    public DIServiceInfo getDIServiceInfo() {
        return diServiceInfo;
    }

    /**
     * Sets the value of the diServiceInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServiceInfo }
     *     
     */
    public void setDIServiceInfo(DIServiceInfo value) {
        this.diServiceInfo = value;
    }

    /**
     * Gets the value of the monitorMode property.
     * 
     * @return
     *     possible object is
     *     {@link EDIServerMonitorMode }
     *     
     */
    public EDIServerMonitorMode getMonitorMode() {
        return monitorMode;
    }

    /**
     * Sets the value of the monitorMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDIServerMonitorMode }
     *     
     */
    public void setMonitorMode(EDIServerMonitorMode value) {
        this.monitorMode = value;
    }

}
