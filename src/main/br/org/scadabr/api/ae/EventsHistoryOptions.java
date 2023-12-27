/**
 * EventsHistoryOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class EventsHistoryOptions  implements java.io.Serializable {
    private br.org.scadabr.api.constants.AlarmLevel alarmLevel;

    private java.util.Calendar initialDate;

    private java.util.Calendar finalDate;

    private int maxReturn;

    public EventsHistoryOptions() {
    }

    public EventsHistoryOptions(
           br.org.scadabr.api.constants.AlarmLevel alarmLevel,
           java.util.Calendar initialDate,
           java.util.Calendar finalDate,
           int maxReturn) {
           this.alarmLevel = alarmLevel;
           this.initialDate = initialDate;
           this.finalDate = finalDate;
           this.maxReturn = maxReturn;
    }


    /**
     * Gets the alarmLevel value for this EventsHistoryOptions.
     * 
     * @return alarmLevel
     */
    public br.org.scadabr.api.constants.AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }


    /**
     * Sets the alarmLevel value for this EventsHistoryOptions.
     * 
     * @param alarmLevel
     */
    public void setAlarmLevel(br.org.scadabr.api.constants.AlarmLevel alarmLevel) {
        this.alarmLevel = alarmLevel;
    }


    /**
     * Gets the initialDate value for this EventsHistoryOptions.
     * 
     * @return initialDate
     */
    public java.util.Calendar getInitialDate() {
        return initialDate;
    }


    /**
     * Sets the initialDate value for this EventsHistoryOptions.
     * 
     * @param initialDate
     */
    public void setInitialDate(java.util.Calendar initialDate) {
        this.initialDate = initialDate;
    }


    /**
     * Gets the finalDate value for this EventsHistoryOptions.
     * 
     * @return finalDate
     */
    public java.util.Calendar getFinalDate() {
        return finalDate;
    }


    /**
     * Sets the finalDate value for this EventsHistoryOptions.
     * 
     * @param finalDate
     */
    public void setFinalDate(java.util.Calendar finalDate) {
        this.finalDate = finalDate;
    }


    /**
     * Gets the maxReturn value for this EventsHistoryOptions.
     * 
     * @return maxReturn
     */
    public int getMaxReturn() {
        return maxReturn;
    }


    /**
     * Sets the maxReturn value for this EventsHistoryOptions.
     * 
     * @param maxReturn
     */
    public void setMaxReturn(int maxReturn) {
        this.maxReturn = maxReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventsHistoryOptions)) return false;
        EventsHistoryOptions other = (EventsHistoryOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.alarmLevel==null && other.getAlarmLevel()==null) || 
             (this.alarmLevel!=null &&
              this.alarmLevel.equals(other.getAlarmLevel()))) &&
            ((this.initialDate==null && other.getInitialDate()==null) || 
             (this.initialDate!=null &&
              this.initialDate.equals(other.getInitialDate()))) &&
            ((this.finalDate==null && other.getFinalDate()==null) || 
             (this.finalDate!=null &&
              this.finalDate.equals(other.getFinalDate()))) &&
            this.maxReturn == other.getMaxReturn();
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
        if (getAlarmLevel() != null) {
            _hashCode += getAlarmLevel().hashCode();
        }
        if (getInitialDate() != null) {
            _hashCode += getInitialDate().hashCode();
        }
        if (getFinalDate() != null) {
            _hashCode += getFinalDate().hashCode();
        }
        _hashCode += getMaxReturn();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventsHistoryOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "EventsHistoryOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alarmLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "alarmLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "AlarmLevel"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initialDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "initialDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finalDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "finalDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "maxReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
