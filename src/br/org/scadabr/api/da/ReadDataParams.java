/**
 * ReadDataParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.da;

public class ReadDataParams  implements java.io.Serializable {
    private java.lang.String[] itemPathList;

    private br.org.scadabr.api.da.ReadDataOptions options;

    public ReadDataParams() {
    }

    public ReadDataParams(
           java.lang.String[] itemPathList,
           br.org.scadabr.api.da.ReadDataOptions options) {
           this.itemPathList = itemPathList;
           this.options = options;
    }


    /**
     * Gets the itemPathList value for this ReadDataParams.
     * 
     * @return itemPathList
     */
    public java.lang.String[] getItemPathList() {
        return itemPathList;
    }


    /**
     * Sets the itemPathList value for this ReadDataParams.
     * 
     * @param itemPathList
     */
    public void setItemPathList(java.lang.String[] itemPathList) {
        this.itemPathList = itemPathList;
    }

    public java.lang.String getItemPathList(int i) {
        return this.itemPathList[i];
    }

    public void setItemPathList(int i, java.lang.String _value) {
        this.itemPathList[i] = _value;
    }


    /**
     * Gets the options value for this ReadDataParams.
     * 
     * @return options
     */
    public br.org.scadabr.api.da.ReadDataOptions getOptions() {
        return options;
    }


    /**
     * Sets the options value for this ReadDataParams.
     * 
     * @param options
     */
    public void setOptions(br.org.scadabr.api.da.ReadDataOptions options) {
        this.options = options;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadDataParams)) return false;
        ReadDataParams other = (ReadDataParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemPathList==null && other.getItemPathList()==null) || 
             (this.itemPathList!=null &&
              java.util.Arrays.equals(this.itemPathList, other.getItemPathList()))) &&
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
        if (getItemPathList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItemPathList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItemPathList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOptions() != null) {
            _hashCode += getOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadDataParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">ReadDataParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemPathList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "itemPathList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "ReadDataOptions"));
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
