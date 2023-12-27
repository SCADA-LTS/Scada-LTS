/**
 * ActiveEventsOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class ActiveEventsOptions  implements java.io.Serializable {
    private int maxReturn;

    private br.org.scadabr.api.constants.AlarmLevel alarmLevel;

    public ActiveEventsOptions() {
    }

    public ActiveEventsOptions(
           int maxReturn,
           br.org.scadabr.api.constants.AlarmLevel alarmLevel) {
           this.maxReturn = maxReturn;
           this.alarmLevel = alarmLevel;
    }


    /**
     * Gets the maxReturn value for this ActiveEventsOptions.
     * 
     * @return maxReturn
     */
    public int getMaxReturn() {
        return maxReturn;
    }


    /**
     * Sets the maxReturn value for this ActiveEventsOptions.
     * 
     * @param maxReturn
     */
    public void setMaxReturn(int maxReturn) {
        this.maxReturn = maxReturn;
    }


    /**
     * Gets the alarmLevel value for this ActiveEventsOptions.
     * 
     * @return alarmLevel
     */
    public br.org.scadabr.api.constants.AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }


    /**
     * Sets the alarmLevel value for this ActiveEventsOptions.
     * 
     * @param alarmLevel
     */
    public void setAlarmLevel(br.org.scadabr.api.constants.AlarmLevel alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ActiveEventsOptions)) return false;
        ActiveEventsOptions other = (ActiveEventsOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.maxReturn == other.getMaxReturn() &&
            ((this.alarmLevel==null && other.getAlarmLevel()==null) || 
             (this.alarmLevel!=null &&
              this.alarmLevel.equals(other.getAlarmLevel())));
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
        _hashCode += getMaxReturn();
        if (getAlarmLevel() != null) {
            _hashCode += getAlarmLevel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ActiveEventsOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "ActiveEventsOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "maxReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alarmLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "alarmLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "AlarmLevel"));
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
