/**
 * EventDefinition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class EventDefinition  implements java.io.Serializable {
    private java.lang.String eventName;

    private java.lang.String message;

    private br.org.scadabr.api.constants.EventType eventType;

    private br.org.scadabr.api.constants.AlarmLevel alarmLevel;

    private java.lang.String configuration;

    public EventDefinition() {
    }

    public EventDefinition(
           java.lang.String eventName,
           java.lang.String message,
           br.org.scadabr.api.constants.EventType eventType,
           br.org.scadabr.api.constants.AlarmLevel alarmLevel,
           java.lang.String configuration) {
           this.eventName = eventName;
           this.message = message;
           this.eventType = eventType;
           this.alarmLevel = alarmLevel;
           this.configuration = configuration;
    }


    /**
     * Gets the eventName value for this EventDefinition.
     * 
     * @return eventName
     */
    public java.lang.String getEventName() {
        return eventName;
    }


    /**
     * Sets the eventName value for this EventDefinition.
     * 
     * @param eventName
     */
    public void setEventName(java.lang.String eventName) {
        this.eventName = eventName;
    }


    /**
     * Gets the message value for this EventDefinition.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this EventDefinition.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the eventType value for this EventDefinition.
     * 
     * @return eventType
     */
    public br.org.scadabr.api.constants.EventType getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this EventDefinition.
     * 
     * @param eventType
     */
    public void setEventType(br.org.scadabr.api.constants.EventType eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the alarmLevel value for this EventDefinition.
     * 
     * @return alarmLevel
     */
    public br.org.scadabr.api.constants.AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }


    /**
     * Sets the alarmLevel value for this EventDefinition.
     * 
     * @param alarmLevel
     */
    public void setAlarmLevel(br.org.scadabr.api.constants.AlarmLevel alarmLevel) {
        this.alarmLevel = alarmLevel;
    }


    /**
     * Gets the configuration value for this EventDefinition.
     * 
     * @return configuration
     */
    public java.lang.String getConfiguration() {
        return configuration;
    }


    /**
     * Sets the configuration value for this EventDefinition.
     * 
     * @param configuration
     */
    public void setConfiguration(java.lang.String configuration) {
        this.configuration = configuration;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventDefinition)) return false;
        EventDefinition other = (EventDefinition) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventName==null && other.getEventName()==null) || 
             (this.eventName!=null &&
              this.eventName.equals(other.getEventName()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            ((this.alarmLevel==null && other.getAlarmLevel()==null) || 
             (this.alarmLevel!=null &&
              this.alarmLevel.equals(other.getAlarmLevel()))) &&
            ((this.configuration==null && other.getConfiguration()==null) || 
             (this.configuration!=null &&
              this.configuration.equals(other.getConfiguration())));
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
        if (getEventName() != null) {
            _hashCode += getEventName().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        if (getAlarmLevel() != null) {
            _hashCode += getAlarmLevel().hashCode();
        }
        if (getConfiguration() != null) {
            _hashCode += getConfiguration().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventDefinition.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "EventDefinition"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "eventName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "EventType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alarmLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "alarmLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "AlarmLevel"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configuration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "configuration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
