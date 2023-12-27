/**
 * BrowseEventsParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class BrowseEventsParams  implements java.io.Serializable {
    private java.lang.String eventsPath;

    private br.org.scadabr.api.ae.BrowseEventsOptions options;

    public BrowseEventsParams() {
    }

    public BrowseEventsParams(
           java.lang.String eventsPath,
           br.org.scadabr.api.ae.BrowseEventsOptions options) {
           this.eventsPath = eventsPath;
           this.options = options;
    }


    /**
     * Gets the eventsPath value for this BrowseEventsParams.
     * 
     * @return eventsPath
     */
    public java.lang.String getEventsPath() {
        return eventsPath;
    }


    /**
     * Sets the eventsPath value for this BrowseEventsParams.
     * 
     * @param eventsPath
     */
    public void setEventsPath(java.lang.String eventsPath) {
        this.eventsPath = eventsPath;
    }


    /**
     * Gets the options value for this BrowseEventsParams.
     * 
     * @return options
     */
    public br.org.scadabr.api.ae.BrowseEventsOptions getOptions() {
        return options;
    }


    /**
     * Sets the options value for this BrowseEventsParams.
     * 
     * @param options
     */
    public void setOptions(br.org.scadabr.api.ae.BrowseEventsOptions options) {
        this.options = options;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BrowseEventsParams)) return false;
        BrowseEventsParams other = (BrowseEventsParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventsPath==null && other.getEventsPath()==null) || 
             (this.eventsPath!=null &&
              this.eventsPath.equals(other.getEventsPath()))) &&
            ((this.options==null && other.getOptions()==null) || 
             (this.options!=null &&
              this.options.equals(other.getOptions())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getEventsPath() != null) {
            _hashCode += getEventsPath().hashCode();
        }
        if (getOptions() != null) {
            _hashCode += getOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BrowseEventsParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">BrowseEventsParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventsPath");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "eventsPath"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "BrowseEventsOptions"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
