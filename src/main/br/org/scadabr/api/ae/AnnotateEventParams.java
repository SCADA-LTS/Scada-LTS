/**
 * AnnotateEventParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class AnnotateEventParams  implements java.io.Serializable {
    private java.lang.Integer eventId;

    private br.org.scadabr.api.vo.EventMessage message;

    public AnnotateEventParams() {
    }

    public AnnotateEventParams(
           java.lang.Integer eventId,
           br.org.scadabr.api.vo.EventMessage message) {
           this.eventId = eventId;
           this.message = message;
    }


    /**
     * Gets the eventId value for this AnnotateEventParams.
     * 
     * @return eventId
     */
    public java.lang.Integer getEventId() {
        return eventId;
    }


    /**
     * Sets the eventId value for this AnnotateEventParams.
     * 
     * @param eventId
     */
    public void setEventId(java.lang.Integer eventId) {
        this.eventId = eventId;
    }


    /**
     * Gets the message value for this AnnotateEventParams.
     * 
     * @return message
     */
    public br.org.scadabr.api.vo.EventMessage getMessage() {
        return message;
    }


    /**
     * Sets the message value for this AnnotateEventParams.
     * 
     * @param message
     */
    public void setMessage(br.org.scadabr.api.vo.EventMessage message) {
        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnnotateEventParams)) return false;
        AnnotateEventParams other = (AnnotateEventParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventId==null && other.getEventId()==null) || 
             (this.eventId!=null &&
              this.eventId.equals(other.getEventId()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage())));
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
        if (getEventId() != null) {
            _hashCode += getEventId().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AnnotateEventParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AnnotateEventParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "eventId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "EventMessage"));
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
