/**
 * QualityCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class QualityCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected QualityCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _BAD = "BAD";
    public static final java.lang.String _BAD_CONFIGURATION_ERROR = "BAD_CONFIGURATION_ERROR";
    public static final java.lang.String _BAD_NOT_CONNECTED = "BAD_NOT_CONNECTED";
    public static final java.lang.String _BAD_DEVICE_FAILURE = "BAD_DEVICE_FAILURE";
    public static final java.lang.String _BAD_SENSOR_FAILURE = "BAD_SENSOR_FAILURE";
    public static final java.lang.String _BAD_LAST_KNOWN_VALUE = "BAD_LAST_KNOWN_VALUE";
    public static final java.lang.String _BAD_COMM_FAILURE = "BAD_COMM_FAILURE";
    public static final java.lang.String _BAD_OUT_OF_SERVICE = "BAD_OUT_OF_SERVICE";
    public static final java.lang.String _BAD_WAITING_FOR_INITIAL_DATA = "BAD_WAITING_FOR_INITIAL_DATA";
    public static final java.lang.String _UNCERTAIN = "UNCERTAIN";
    public static final java.lang.String _UNCERTAIN_LAST_USABLE_VALUE = "UNCERTAIN_LAST_USABLE_VALUE";
    public static final java.lang.String _UNCERTAIN_SENSOR_NOT_ACCURATE = "UNCERTAIN_SENSOR_NOT_ACCURATE";
    public static final java.lang.String _UNCERTAIN_EU_EXCEEDED = "UNCERTAIN_EU_EXCEEDED";
    public static final java.lang.String _UNCERTAIN_SUB_NORMAL = "UNCERTAIN_SUB_NORMAL";
    public static final java.lang.String _GOOD = "GOOD";
    public static final java.lang.String _GOOD_LOCAL_OVERRIDE = "GOOD_LOCAL_OVERRIDE";
    public static final QualityCode BAD = new QualityCode(_BAD);
    public static final QualityCode BAD_CONFIGURATION_ERROR = new QualityCode(_BAD_CONFIGURATION_ERROR);
    public static final QualityCode BAD_NOT_CONNECTED = new QualityCode(_BAD_NOT_CONNECTED);
    public static final QualityCode BAD_DEVICE_FAILURE = new QualityCode(_BAD_DEVICE_FAILURE);
    public static final QualityCode BAD_SENSOR_FAILURE = new QualityCode(_BAD_SENSOR_FAILURE);
    public static final QualityCode BAD_LAST_KNOWN_VALUE = new QualityCode(_BAD_LAST_KNOWN_VALUE);
    public static final QualityCode BAD_COMM_FAILURE = new QualityCode(_BAD_COMM_FAILURE);
    public static final QualityCode BAD_OUT_OF_SERVICE = new QualityCode(_BAD_OUT_OF_SERVICE);
    public static final QualityCode BAD_WAITING_FOR_INITIAL_DATA = new QualityCode(_BAD_WAITING_FOR_INITIAL_DATA);
    public static final QualityCode UNCERTAIN = new QualityCode(_UNCERTAIN);
    public static final QualityCode UNCERTAIN_LAST_USABLE_VALUE = new QualityCode(_UNCERTAIN_LAST_USABLE_VALUE);
    public static final QualityCode UNCERTAIN_SENSOR_NOT_ACCURATE = new QualityCode(_UNCERTAIN_SENSOR_NOT_ACCURATE);
    public static final QualityCode UNCERTAIN_EU_EXCEEDED = new QualityCode(_UNCERTAIN_EU_EXCEEDED);
    public static final QualityCode UNCERTAIN_SUB_NORMAL = new QualityCode(_UNCERTAIN_SUB_NORMAL);
    public static final QualityCode GOOD = new QualityCode(_GOOD);
    public static final QualityCode GOOD_LOCAL_OVERRIDE = new QualityCode(_GOOD_LOCAL_OVERRIDE);
    public java.lang.String getValue() { return _value_;}
    public static QualityCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        QualityCode enumeration = (QualityCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static QualityCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(QualityCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "QualityCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
