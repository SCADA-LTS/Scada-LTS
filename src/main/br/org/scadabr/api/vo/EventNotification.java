/**
 * EventNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class EventNotification  implements java.io.Serializable {
    private int id;

    private java.lang.String alias;

    private br.org.scadabr.api.constants.EventType eventType;

    private br.org.scadabr.api.constants.AlarmLevel alarmLevel;

    private br.org.scadabr.api.vo.EventMessage[] message;

    private java.util.Calendar timestamp;

    private java.util.Calendar ackTime;

    private java.util.Calendar rtnTime;

    public EventNotification() {
    }

    public EventNotification(
           int id,
           java.lang.String alias,
           br.org.scadabr.api.constants.EventType eventType,
           br.org.scadabr.api.constants.AlarmLevel alarmLevel,
           br.org.scadabr.api.vo.EventMessage[] message,
           java.util.Calendar timestamp,
           java.util.Calendar ackTime,
           java.util.Calendar rtnTime) {
           this.id = id;
           this.alias = alias;
           this.eventType = eventType;
           this.alarmLevel = alarmLevel;
           this.message = message;
           this.timestamp = timestamp;
           this.ackTime = ackTime;
           this.rtnTime = rtnTime;
    }


    /**
     * Gets the id value for this EventNotification.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this EventNotification.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the alias value for this EventNotification.
     * 
     * @return alias
     */
    public java.lang.String getAlias() {
        return alias;
    }


    /**
     * Sets the alias value for this EventNotification.
     * 
     * @param alias
     */
    public void setAlias(java.lang.String alias) {
        this.alias = alias;
    }


    /**
     * Gets the eventType value for this EventNotification.
     * 
     * @return eventType
     */
    public br.org.scadabr.api.constants.EventType getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this EventNotification.
     * 
     * @param eventType
     */
    public void setEventType(br.org.scadabr.api.constants.EventType eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the alarmLevel value for this EventNotification.
     * 
     * @return alarmLevel
     */
    public br.org.scadabr.api.constants.AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }


    /**
     * Sets the alarmLevel value for this EventNotification.
     * 
     * @param alarmLevel
     */
    public void setAlarmLevel(br.org.scadabr.api.constants.AlarmLevel alarmLevel) {
        this.alarmLevel = alarmLevel;
    }


    /**
     * Gets the message value for this EventNotification.
     * 
     * @return message
     */
    public br.org.scadabr.api.vo.EventMessage[] getMessage() {
        return message;
    }


    /**
     * Sets the message value for this EventNotification.
     * 
     * @param message
     */
    public void setMessage(br.org.scadabr.api.vo.EventMessage[] message) {
        this.message = message;
    }

    public br.org.scadabr.api.vo.EventMessage getMessage(int i) {
        return this.message[i];
    }

    public void setMessage(int i, br.org.scadabr.api.vo.EventMessage _value) {
        this.message[i] = _value;
    }


    /**
     * Gets the timestamp value for this EventNotification.
     * 
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this EventNotification.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the ackTime value for this EventNotification.
     * 
     * @return ackTime
     */
    public java.util.Calendar getAckTime() {
        return ackTime;
    }


    /**
     * Sets the ackTime value for this EventNotification.
     * 
     * @param ackTime
     */
    public void setAckTime(java.util.Calendar ackTime) {
        this.ackTime = ackTime;
    }


    /**
     * Gets the rtnTime value for this EventNotification.
     * 
     * @return rtnTime
     */
    public java.util.Calendar getRtnTime() {
        return rtnTime;
    }


    /**
     * Sets the rtnTime value for this EventNotification.
     * 
     * @param rtnTime
     */
    public void setRtnTime(java.util.Calendar rtnTime) {
        this.rtnTime = rtnTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventNotification)) return false;
        EventNotification other = (EventNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.alias==null && other.getAlias()==null) || 
             (this.alias!=null &&
              this.alias.equals(other.getAlias()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            ((this.alarmLevel==null && other.getAlarmLevel()==null) || 
             (this.alarmLevel!=null &&
              this.alarmLevel.equals(other.getAlarmLevel()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              java.util.Arrays.equals(this.message, other.getMessage()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            ((this.ackTime==null && other.getAckTime()==null) || 
             (this.ackTime!=null &&
              this.ackTime.equals(other.getAckTime()))) &&
            ((this.rtnTime==null && other.getRtnTime()==null) || 
             (this.rtnTime!=null &&
              this.rtnTime.equals(other.getRtnTime())));
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
        _hashCode += getId();
        if (getAlias() != null) {
            _hashCode += getAlias().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        if (getAlarmLevel() != null) {
            _hashCode += getAlarmLevel().hashCode();
        }
        if (getMessage() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMessage());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMessage(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getAckTime() != null) {
            _hashCode += getAckTime().hashCode();
        }
        if (getRtnTime() != null) {
            _hashCode += getRtnTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "EventNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alias");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "alias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
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
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "EventMessage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ackTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ackTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rtnTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "rtnTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
