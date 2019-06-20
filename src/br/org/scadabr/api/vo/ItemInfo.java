/**
 * ItemInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class ItemInfo  implements java.io.Serializable {
    private java.lang.String itemName;

    private br.org.scadabr.api.constants.DataType dataType;

    private java.lang.Boolean writable;

    public ItemInfo() {
    }

    public ItemInfo(
           java.lang.String itemName,
           br.org.scadabr.api.constants.DataType dataType,
           java.lang.Boolean writable) {
           this.itemName = itemName;
           this.dataType = dataType;
           this.writable = writable;
    }


    /**
     * Gets the itemName value for this ItemInfo.
     * 
     * @return itemName
     */
    public java.lang.String getItemName() {
        return itemName;
    }


    /**
     * Sets the itemName value for this ItemInfo.
     * 
     * @param itemName
     */
    public void setItemName(java.lang.String itemName) {
        this.itemName = itemName;
    }


    /**
     * Gets the dataType value for this ItemInfo.
     * 
     * @return dataType
     */
    public br.org.scadabr.api.constants.DataType getDataType() {
        return dataType;
    }


    /**
     * Sets the dataType value for this ItemInfo.
     * 
     * @param dataType
     */
    public void setDataType(br.org.scadabr.api.constants.DataType dataType) {
        this.dataType = dataType;
    }


    /**
     * Gets the writable value for this ItemInfo.
     * 
     * @return writable
     */
    public java.lang.Boolean getWritable() {
        return writable;
    }


    /**
     * Sets the writable value for this ItemInfo.
     * 
     * @param writable
     */
    public void setWritable(java.lang.Boolean writable) {
        this.writable = writable;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ItemInfo)) return false;
        ItemInfo other = (ItemInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemName==null && other.getItemName()==null) || 
             (this.itemName!=null &&
              this.itemName.equals(other.getItemName()))) &&
            ((this.dataType==null && other.getDataType()==null) || 
             (this.dataType!=null &&
              this.dataType.equals(other.getDataType()))) &&
            ((this.writable==null && other.getWritable()==null) || 
             (this.writable!=null &&
              this.writable.equals(other.getWritable())));
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
        if (getItemName() != null) {
            _hashCode += getItemName().hashCode();
        }
        if (getDataType() != null) {
            _hashCode += getDataType().hashCode();
        }
        if (getWritable() != null) {
            _hashCode += getWritable().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ItemInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ItemInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "itemName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "dataType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "DataType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("writable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "writable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
