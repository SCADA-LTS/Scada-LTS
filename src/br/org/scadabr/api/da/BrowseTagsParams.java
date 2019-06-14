/**
 * BrowseTagsParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.da;

public class BrowseTagsParams  implements java.io.Serializable {
    private java.lang.String itemsPath;

    private br.org.scadabr.api.da.BrowseTagsOptions options;

    public BrowseTagsParams() {
    }

    public BrowseTagsParams(
           java.lang.String itemsPath,
           br.org.scadabr.api.da.BrowseTagsOptions options) {
           this.itemsPath = itemsPath;
           this.options = options;
    }


    /**
     * Gets the itemsPath value for this BrowseTagsParams.
     * 
     * @return itemsPath
     */
    public java.lang.String getItemsPath() {
        return itemsPath;
    }


    /**
     * Sets the itemsPath value for this BrowseTagsParams.
     * 
     * @param itemsPath
     */
    public void setItemsPath(java.lang.String itemsPath) {
        this.itemsPath = itemsPath;
    }


    /**
     * Gets the options value for this BrowseTagsParams.
     * 
     * @return options
     */
    public br.org.scadabr.api.da.BrowseTagsOptions getOptions() {
        return options;
    }


    /**
     * Sets the options value for this BrowseTagsParams.
     * 
     * @param options
     */
    public void setOptions(br.org.scadabr.api.da.BrowseTagsOptions options) {
        this.options = options;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BrowseTagsParams)) return false;
        BrowseTagsParams other = (BrowseTagsParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemsPath==null && other.getItemsPath()==null) || 
             (this.itemsPath!=null &&
              this.itemsPath.equals(other.getItemsPath()))) &&
            ((this.options==null && other.getOptions()==null) || 
             (this.options!=null &&
              this.options.equals(other.getOptions())));
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
        if (getItemsPath() != null) {
            _hashCode += getItemsPath().hashCode();
        }
        if (getOptions() != null) {
            _hashCode += getOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BrowseTagsParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">BrowseTagsParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemsPath");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "itemsPath"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "BrowseTagsOptions"));
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
