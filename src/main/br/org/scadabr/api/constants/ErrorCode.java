/**
 * ErrorCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class ErrorCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ErrorCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _OK = "OK";
    public static final java.lang.String _UNSPECIFIED_ERROR = "UNSPECIFIED_ERROR";
    public static final java.lang.String _INSUFFICIENT_PARAMETERS = "INSUFFICIENT_PARAMETERS";
    public static final java.lang.String _INVALID_PARAMETER = "INVALID_PARAMETER";
    public static final java.lang.String _ACCESS_DENIED = "ACCESS_DENIED";
    public static final java.lang.String _SERVER_BUSY = "SERVER_BUSY";
    public static final java.lang.String _INVALID_ID = "INVALID_ID";
    public static final java.lang.String _NOT_SUPPORTED = "NOT_SUPPORTED";
    public static final java.lang.String _READ_ONLY = "READ_ONLY";
    public static final java.lang.String _WRITE_ONLY = "WRITE_ONLY";
    public static final java.lang.String _TIMED_OUT = "TIMED_OUT";
    public static final ErrorCode OK = new ErrorCode(_OK);
    public static final ErrorCode UNSPECIFIED_ERROR = new ErrorCode(_UNSPECIFIED_ERROR);
    public static final ErrorCode INSUFFICIENT_PARAMETERS = new ErrorCode(_INSUFFICIENT_PARAMETERS);
    public static final ErrorCode INVALID_PARAMETER = new ErrorCode(_INVALID_PARAMETER);
    public static final ErrorCode ACCESS_DENIED = new ErrorCode(_ACCESS_DENIED);
    public static final ErrorCode SERVER_BUSY = new ErrorCode(_SERVER_BUSY);
    public static final ErrorCode INVALID_ID = new ErrorCode(_INVALID_ID);
    public static final ErrorCode NOT_SUPPORTED = new ErrorCode(_NOT_SUPPORTED);
    public static final ErrorCode READ_ONLY = new ErrorCode(_READ_ONLY);
    public static final ErrorCode WRITE_ONLY = new ErrorCode(_WRITE_ONLY);
    public static final ErrorCode TIMED_OUT = new ErrorCode(_TIMED_OUT);
    public java.lang.String getValue() { return _value_;}
    public static ErrorCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ErrorCode enumeration = (ErrorCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ErrorCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ErrorCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ErrorCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
