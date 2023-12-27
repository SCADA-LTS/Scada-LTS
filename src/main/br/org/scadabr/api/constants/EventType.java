/**
 * EventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class EventType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EventType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _SYSTEM_EVENT = "SYSTEM_EVENT";
    public static final java.lang.String _AUDIT_EVENT = "AUDIT_EVENT";
    public static final java.lang.String _SCHEDULED_EVENT = "SCHEDULED_EVENT";
    public static final java.lang.String _POINT_CONDITION_EVENT = "POINT_CONDITION_EVENT";
    public static final java.lang.String _ASYNCHRONOUS_DATA = "ASYNCHRONOUS_DATA";
    public static final EventType SYSTEM_EVENT = new EventType(_SYSTEM_EVENT);
    public static final EventType AUDIT_EVENT = new EventType(_AUDIT_EVENT);
    public static final EventType SCHEDULED_EVENT = new EventType(_SCHEDULED_EVENT);
    public static final EventType POINT_CONDITION_EVENT = new EventType(_POINT_CONDITION_EVENT);
    public static final EventType ASYNCHRONOUS_DATA = new EventType(_ASYNCHRONOUS_DATA);
    public java.lang.String getValue() { return _value_;}
    public static EventType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EventType enumeration = (EventType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EventType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "EventType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
