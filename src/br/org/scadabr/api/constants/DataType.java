/**
 * DataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class DataType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected DataType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _INTEGER = "INTEGER";
    public static final java.lang.String _UNSIGNED_INTEGER = "UNSIGNED_INTEGER";
    public static final java.lang.String _LONG = "LONG";
    public static final java.lang.String _UNSIGNED_LONG = "UNSIGNED_LONG";
    public static final java.lang.String _STRING = "STRING";
    public static final java.lang.String _BOOLEAN = "BOOLEAN";
    public static final java.lang.String _FLOAT = "FLOAT";
    public static final java.lang.String _DOUBLE = "DOUBLE";
    public static final java.lang.String _BYTE = "BYTE";
    public static final java.lang.String _UNSIGNED_BYTE = "UNSIGNED_BYTE";
    public static final DataType INTEGER = new DataType(_INTEGER);
    public static final DataType UNSIGNED_INTEGER = new DataType(_UNSIGNED_INTEGER);
    public static final DataType LONG = new DataType(_LONG);
    public static final DataType UNSIGNED_LONG = new DataType(_UNSIGNED_LONG);
    public static final DataType STRING = new DataType(_STRING);
    public static final DataType BOOLEAN = new DataType(_BOOLEAN);
    public static final DataType FLOAT = new DataType(_FLOAT);
    public static final DataType DOUBLE = new DataType(_DOUBLE);
    public static final DataType BYTE = new DataType(_BYTE);
    public static final DataType UNSIGNED_BYTE = new DataType(_UNSIGNED_BYTE);
    public java.lang.String getValue() { return _value_;}
    public static DataType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        DataType enumeration = (DataType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static DataType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(DataType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "DataType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
