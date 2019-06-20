/**
 * AlarmLevel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class AlarmLevel implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AlarmLevel(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _NONE = "NONE";
    public static final java.lang.String _INFORMATION = "INFORMATION";
    public static final java.lang.String _URGENT = "URGENT";
    public static final java.lang.String _CRITICAL = "CRITICAL";
    public static final java.lang.String _LIFE_SAFETY = "LIFE_SAFETY";
    public static final AlarmLevel NONE = new AlarmLevel(_NONE);
    public static final AlarmLevel INFORMATION = new AlarmLevel(_INFORMATION);
    public static final AlarmLevel URGENT = new AlarmLevel(_URGENT);
    public static final AlarmLevel CRITICAL = new AlarmLevel(_CRITICAL);
    public static final AlarmLevel LIFE_SAFETY = new AlarmLevel(_LIFE_SAFETY);
    public java.lang.String getValue() { return _value_;}
    public static AlarmLevel fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        AlarmLevel enumeration = (AlarmLevel)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static AlarmLevel fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(AlarmLevel.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "AlarmLevel"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
