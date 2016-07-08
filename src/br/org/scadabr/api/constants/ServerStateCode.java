/**
 * ServerStateCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class ServerStateCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ServerStateCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _RUNNING = "RUNNING";
    public static final java.lang.String _FAILED = "FAILED";
    public static final java.lang.String _NO_CONFIG = "NO_CONFIG";
    public static final java.lang.String _SUSPENDED = "SUSPENDED";
    public static final java.lang.String _TEST = "TEST";
    public static final java.lang.String _COMM_FAULT = "COMM_FAULT";
    public static final ServerStateCode RUNNING = new ServerStateCode(_RUNNING);
    public static final ServerStateCode FAILED = new ServerStateCode(_FAILED);
    public static final ServerStateCode NO_CONFIG = new ServerStateCode(_NO_CONFIG);
    public static final ServerStateCode SUSPENDED = new ServerStateCode(_SUSPENDED);
    public static final ServerStateCode TEST = new ServerStateCode(_TEST);
    public static final ServerStateCode COMM_FAULT = new ServerStateCode(_COMM_FAULT);
    public java.lang.String getValue() { return _value_;}
    public static ServerStateCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ServerStateCode enumeration = (ServerStateCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ServerStateCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ServerStateCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ServerStateCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
