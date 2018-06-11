
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Log complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Log">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FileSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Buffer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CodePage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Log", propOrder = {
    "fileSize",
    "buffer",
    "codePage"
})
public class Log {

    @XmlElement(name = "FileSize")
    protected int fileSize;
    @XmlElement(name = "Buffer", required = true, nillable = true)
    protected String buffer;
    @XmlElement(name = "CodePage")
    protected int codePage;

    /**
     * Gets the value of the fileSize property.
     * 
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     */
    public void setFileSize(int value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the buffer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuffer() {
        return buffer;
    }

    /**
     * Sets the value of the buffer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuffer(String value) {
        this.buffer = value;
    }

    /**
     * Gets the value of the codePage property.
     * 
     */
    public int getCodePage() {
        return codePage;
    }

    /**
     * Sets the value of the codePage property.
     * 
     */
    public void setCodePage(int value) {
        this.codePage = value;
    }

}
