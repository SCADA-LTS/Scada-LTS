/**
 * AckEventsParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class AckEventsParams  implements java.io.Serializable {
    private java.lang.Integer[] eventsId;

    private br.org.scadabr.api.ae.AckEventsOptions options;

    public AckEventsParams() {
    }

    public AckEventsParams(
           java.lang.Integer[] eventsId,
           br.org.scadabr.api.ae.AckEventsOptions options) {
           this.eventsId = eventsId;
           this.options = options;
    }


    /**
     * Gets the eventsId value for this AckEventsParams.
     * 
     * @return eventsId
     */
    public java.lang.Integer[] getEventsId() {
        return eventsId;
    }


    /**
     * Sets the eventsId value for this AckEventsParams.
     * 
     * @param eventsId
     */
    public void setEventsId(java.lang.Integer[] eventsId) {
        this.eventsId = eventsId;
    }

    public java.lang.Integer getEventsId(int i) {
        return this.eventsId[i];
    }

    public void setEventsId(int i, java.lang.Integer _value) {
        this.eventsId[i] = _value;
    }


    /**
     * Gets the options value for this AckEventsParams.
     * 
     * @return options
     */
    public br.org.scadabr.api.ae.AckEventsOptions getOptions() {
        return options;
    }


    /**
     * Sets the options value for this AckEventsParams.
     * 
     * @param options
     */
    public void setOptions(br.org.scadabr.api.ae.AckEventsOptions options) {
        this.options = options;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AckEventsParams)) return false;
        AckEventsParams other = (AckEventsParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventsId==null && other.getEventsId()==null) || 
             (this.eventsId!=null &&
              java.util.Arrays.equals(this.eventsId, other.getEventsId()))) &&
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
        if (getEventsId() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventsId());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventsId(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOptions() != null) {
            _hashCode += getOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AckEventsParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AckEventsParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventsId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "eventsId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "AckEventsOptions"));
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
