/**
 * BrowseDataSourcesParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.config;

public class BrowseDataSourcesParams  implements java.io.Serializable {
    private br.org.scadabr.api.constants.DataSourceType type;

    public BrowseDataSourcesParams() {
    }

    public BrowseDataSourcesParams(
           br.org.scadabr.api.constants.DataSourceType type) {
           this.type = type;
    }


    /**
     * Gets the type value for this BrowseDataSourcesParams.
     * 
     * @return type
     */
    public br.org.scadabr.api.constants.DataSourceType getType() {
        return type;
    }


    /**
     * Sets the type value for this BrowseDataSourcesParams.
     * 
     * @param type
     */
    public void setType(br.org.scadabr.api.constants.DataSourceType type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BrowseDataSourcesParams)) return false;
        BrowseDataSourcesParams other = (BrowseDataSourcesParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BrowseDataSourcesParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseDataSourcesParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "DataSourceType"));
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
