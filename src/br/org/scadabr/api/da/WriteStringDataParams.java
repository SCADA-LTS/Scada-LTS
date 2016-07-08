/**
 * WriteStringDataParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.da;

public class WriteStringDataParams  implements java.io.Serializable {
    private br.org.scadabr.api.vo.ItemStringValue[] itemsList;

    private br.org.scadabr.api.da.WriteDataOptions options;

    public WriteStringDataParams() {
    }

    public WriteStringDataParams(
           br.org.scadabr.api.vo.ItemStringValue[] itemsList,
           br.org.scadabr.api.da.WriteDataOptions options) {
           this.itemsList = itemsList;
           this.options = options;
    }


    /**
     * Gets the itemsList value for this WriteStringDataParams.
     * 
     * @return itemsList
     */
    public br.org.scadabr.api.vo.ItemStringValue[] getItemsList() {
        return itemsList;
    }


    /**
     * Sets the itemsList value for this WriteStringDataParams.
     * 
     * @param itemsList
     */
    public void setItemsList(br.org.scadabr.api.vo.ItemStringValue[] itemsList) {
        this.itemsList = itemsList;
    }

    public br.org.scadabr.api.vo.ItemStringValue getItemsList(int i) {
        return this.itemsList[i];
    }

    public void setItemsList(int i, br.org.scadabr.api.vo.ItemStringValue _value) {
        this.itemsList[i] = _value;
    }


    /**
     * Gets the options value for this WriteStringDataParams.
     * 
     * @return options
     */
    public br.org.scadabr.api.da.WriteDataOptions getOptions() {
        return options;
    }


    /**
     * Sets the options value for this WriteStringDataParams.
     * 
     * @param options
     */
    public void setOptions(br.org.scadabr.api.da.WriteDataOptions options) {
        this.options = options;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WriteStringDataParams)) return false;
        WriteStringDataParams other = (WriteStringDataParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemsList==null && other.getItemsList()==null) || 
             (this.itemsList!=null &&
              java.util.Arrays.equals(this.itemsList, other.getItemsList()))) &&
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
        if (getItemsList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItemsList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItemsList(), i);
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
        new org.apache.axis.description.TypeDesc(WriteStringDataParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">WriteStringDataParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "itemsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ItemStringValue"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("options");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "WriteDataOptions"));
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
