
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetNextLogSegmentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetNextLogSegmentRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LogHandle" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TimeOut" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNextLogSegmentRequest", propOrder = {
    "logHandle",
    "timeOut"
})
public class GetNextLogSegmentRequest {

    @XmlElement(name = "LogHandle")
    protected int logHandle;
    @XmlElement(name = "TimeOut")
    protected int timeOut;

    /**
     * Gets the value of the logHandle property.
     * 
     */
    public int getLogHandle() {
        return logHandle;
    }

    /**
     * Sets the value of the logHandle property.
     * 
     */
    public void setLogHandle(int value) {
        this.logHandle = value;
    }

    /**
     * Gets the value of the timeOut property.
     * 
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the value of the timeOut property.
     * 
     */
    public void setTimeOut(int value) {
        this.timeOut = value;
    }

}
