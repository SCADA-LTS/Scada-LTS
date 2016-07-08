/**
 * AckEventsOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class AckEventsOptions  implements java.io.Serializable {
    private boolean returnEventDetails;

    public AckEventsOptions() {
    }

    public AckEventsOptions(
           boolean returnEventDetails) {
           this.returnEventDetails = returnEventDetails;
    }


    /**
     * Gets the returnEventDetails value for this AckEventsOptions.
     * 
     * @return returnEventDetails
     */
    public boolean isReturnEventDetails() {
        return returnEventDetails;
    }


    /**
     * Sets the returnEventDetails value for this AckEventsOptions.
     * 
     * @param returnEventDetails
     */
    public void setReturnEventDetails(boolean returnEventDetails) {
        this.returnEventDetails = returnEventDetails;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AckEventsOptions)) return false;
        AckEventsOptions other = (AckEventsOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.returnEventDetails == other.isReturnEventDetails();
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
        _hashCode += (isReturnEventDetails() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AckEventsOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AckEventsOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnEventDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "returnEventDetails"));
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
