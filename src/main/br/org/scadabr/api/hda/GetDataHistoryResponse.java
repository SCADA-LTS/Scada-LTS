/**
 * GetDataHistoryResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.hda;

public class GetDataHistoryResponse  implements java.io.Serializable {
    private br.org.scadabr.api.vo.APIError[] errors;

    private boolean moreValues;

    private br.org.scadabr.api.vo.ItemValue[] itemsList;

    private br.org.scadabr.api.vo.ReplyBase replyBase;

    public GetDataHistoryResponse() {
    }

    public GetDataHistoryResponse(
           br.org.scadabr.api.vo.APIError[] errors,
           boolean moreValues,
           br.org.scadabr.api.vo.ItemValue[] itemsList,
           br.org.scadabr.api.vo.ReplyBase replyBase) {
           this.errors = errors;
           this.moreValues = moreValues;
           this.itemsList = itemsList;
           this.replyBase = replyBase;
    }


    /**
     * Gets the errors value for this GetDataHistoryResponse.
     * 
     * @return errors
     */
    public br.org.scadabr.api.vo.APIError[] getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this GetDataHistoryResponse.
     * 
     * @param errors
     */
    public void setErrors(br.org.scadabr.api.vo.APIError[] errors) {
        this.errors = errors;
    }

    public br.org.scadabr.api.vo.APIError getErrors(int i) {
        return this.errors[i];
    }

    public void setErrors(int i, br.org.scadabr.api.vo.APIError _value) {
        this.errors[i] = _value;
    }


    /**
     * Gets the moreValues value for this GetDataHistoryResponse.
     * 
     * @return moreValues
     */
    public boolean isMoreValues() {
        return moreValues;
    }


    /**
     * Sets the moreValues value for this GetDataHistoryResponse.
     * 
     * @param moreValues
     */
    public void setMoreValues(boolean moreValues) {
        this.moreValues = moreValues;
    }


    /**
     * Gets the itemsList value for this GetDataHistoryResponse.
     * 
     * @return itemsList
     */
    public br.org.scadabr.api.vo.ItemValue[] getItemsList() {
        return itemsList;
    }


    /**
     * Sets the itemsList value for this GetDataHistoryResponse.
     * 
     * @param itemsList
     */
    public void setItemsList(br.org.scadabr.api.vo.ItemValue[] itemsList) {
        this.itemsList = itemsList;
    }

    public br.org.scadabr.api.vo.ItemValue getItemsList(int i) {
        return this.itemsList[i];
    }

    public void setItemsList(int i, br.org.scadabr.api.vo.ItemValue _value) {
        this.itemsList[i] = _value;
    }


    /**
     * Gets the replyBase value for this GetDataHistoryResponse.
     * 
     * @return replyBase
     */
    public br.org.scadabr.api.vo.ReplyBase getReplyBase() {
        return replyBase;
    }


    /**
     * Sets the replyBase value for this GetDataHistoryResponse.
     * 
     * @param replyBase
     */
    public void setReplyBase(br.org.scadabr.api.vo.ReplyBase replyBase) {
        this.replyBase = replyBase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetDataHistoryResponse)) return false;
        GetDataHistoryResponse other = (GetDataHistoryResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errors==null && other.getErrors()==null) || 
             (this.errors!=null &&
              java.util.Arrays.equals(this.errors, other.getErrors()))) &&
            this.moreValues == other.isMoreValues() &&
            ((this.itemsList==null && other.getItemsList()==null) || 
             (this.itemsList!=null &&
              java.util.Arrays.equals(this.itemsList, other.getItemsList()))) &&
            ((this.replyBase==null && other.getReplyBase()==null) || 
             (this.replyBase!=null &&
              this.replyBase.equals(other.getReplyBase())));
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
        if (getErrors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErrors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErrors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isMoreValues() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
        if (getReplyBase() != null) {
            _hashCode += getReplyBase().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetDataHistoryResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", ">GetDataHistoryResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errors");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "errors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "APIError"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moreValues");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "moreValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "itemsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ItemValue"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replyBase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "replyBase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ReplyBase"));
        elemField.setNillable(false);
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
