/**
 * BrowseEventsOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class BrowseEventsOptions  implements java.io.Serializable {
    private br.org.scadabr.api.constants.EventType eventType;

    private boolean returnEventsConfig;

    public BrowseEventsOptions() {
    }

    public BrowseEventsOptions(
           br.org.scadabr.api.constants.EventType eventType,
           boolean returnEventsConfig) {
           this.eventType = eventType;
           this.returnEventsConfig = returnEventsConfig;
    }


    /**
     * Gets the eventType value for this BrowseEventsOptions.
     * 
     * @return eventType
     */
    public br.org.scadabr.api.constants.EventType getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this BrowseEventsOptions.
     * 
     * @param eventType
     */
    public void setEventType(br.org.scadabr.api.constants.EventType eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the returnEventsConfig value for this BrowseEventsOptions.
     * 
     * @return returnEventsConfig
     */
    public boolean isReturnEventsConfig() {
        return returnEventsConfig;
    }


    /**
     * Sets the returnEventsConfig value for this BrowseEventsOptions.
     * 
     * @param returnEventsConfig
     */
    public void setReturnEventsConfig(boolean returnEventsConfig) {
        this.returnEventsConfig = returnEventsConfig;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BrowseEventsOptions)) return false;
        BrowseEventsOptions other = (BrowseEventsOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            this.returnEventsConfig == other.isReturnEventsConfig();
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
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        _hashCode += (isReturnEventsConfig() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BrowseEventsOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "BrowseEventsOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "EventType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnEventsConfig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "returnEventsConfig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
