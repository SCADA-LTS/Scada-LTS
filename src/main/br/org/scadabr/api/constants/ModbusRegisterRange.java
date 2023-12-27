/**
 * ModbusRegisterRange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class ModbusRegisterRange implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ModbusRegisterRange(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _COIL_STATUS = "COIL_STATUS";
    public static final java.lang.String _INPUT_STATUS = "INPUT_STATUS";
    public static final java.lang.String _HOLDING_REGISTER = "HOLDING_REGISTER";
    public static final java.lang.String _INPUT_REGISTER = "INPUT_REGISTER";
    public static final ModbusRegisterRange COIL_STATUS = new ModbusRegisterRange(_COIL_STATUS);
    public static final ModbusRegisterRange INPUT_STATUS = new ModbusRegisterRange(_INPUT_STATUS);
    public static final ModbusRegisterRange HOLDING_REGISTER = new ModbusRegisterRange(_HOLDING_REGISTER);
    public static final ModbusRegisterRange INPUT_REGISTER = new ModbusRegisterRange(_INPUT_REGISTER);
    public java.lang.String getValue() { return _value_;}
    public static ModbusRegisterRange fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ModbusRegisterRange enumeration = (ModbusRegisterRange)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ModbusRegisterRange fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ModbusRegisterRange.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ModbusRegisterRange"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
