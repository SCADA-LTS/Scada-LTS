/**
 * ConfigureDataPointParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.config;

public class ConfigureDataPointParams  implements java.io.Serializable {
    private java.lang.Integer dataSourceId;

    private br.org.scadabr.api.constants.DataSourceType type;

    private java.lang.Object dataPoint;

    public ConfigureDataPointParams() {
    }

    public ConfigureDataPointParams(
           java.lang.Integer dataSourceId,
           br.org.scadabr.api.constants.DataSourceType type,
           java.lang.Object dataPoint) {
           this.dataSourceId = dataSourceId;
           this.type = type;
           this.dataPoint = dataPoint;
    }


    /**
     * Gets the dataSourceId value for this ConfigureDataPointParams.
     * 
     * @return dataSourceId
     */
    public java.lang.Integer getDataSourceId() {
        return dataSourceId;
    }


    /**
     * Sets the dataSourceId value for this ConfigureDataPointParams.
     * 
     * @param dataSourceId
     */
    public void setDataSourceId(java.lang.Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }


    /**
     * Gets the type value for this ConfigureDataPointParams.
     * 
     * @return type
     */
    public br.org.scadabr.api.constants.DataSourceType getType() {
        return type;
    }


    /**
     * Sets the type value for this ConfigureDataPointParams.
     * 
     * @param type
     */
    public void setType(br.org.scadabr.api.constants.DataSourceType type) {
        this.type = type;
    }


    /**
     * Gets the dataPoint value for this ConfigureDataPointParams.
     * 
     * @return dataPoint
     */
    public java.lang.Object getDataPoint() {
        return dataPoint;
    }


    /**
     * Sets the dataPoint value for this ConfigureDataPointParams.
     * 
     * @param dataPoint
     */
    public void setDataPoint(java.lang.Object dataPoint) {
        this.dataPoint = dataPoint;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigureDataPointParams)) return false;
        ConfigureDataPointParams other = (ConfigureDataPointParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataSourceId==null && other.getDataSourceId()==null) || 
             (this.dataSourceId!=null &&
              this.dataSourceId.equals(other.getDataSourceId()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.dataPoint==null && other.getDataPoint()==null) || 
             (this.dataPoint!=null &&
              this.dataPoint.equals(other.getDataPoint())));
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
        if (getDataSourceId() != null) {
            _hashCode += getDataSourceId().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getDataPoint() != null) {
            _hashCode += getDataPoint().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConfigureDataPointParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">ConfigureDataPointParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataSourceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "dataSourceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "DataSourceType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataPoint");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "dataPoint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
