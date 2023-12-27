/**
 * GetDataHistoryOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.hda;

public class GetDataHistoryOptions  implements java.io.Serializable {
    private int maxReturn;

    private java.util.Calendar initialDate;

    private java.util.Calendar finalDate;

    public GetDataHistoryOptions() {
    }

    public GetDataHistoryOptions(
           int maxReturn,
           java.util.Calendar initialDate,
           java.util.Calendar finalDate) {
           this.maxReturn = maxReturn;
           this.initialDate = initialDate;
           this.finalDate = finalDate;
    }


    /**
     * Gets the maxReturn value for this GetDataHistoryOptions.
     * 
     * @return maxReturn
     */
    public int getMaxReturn() {
        return maxReturn;
    }


    /**
     * Sets the maxReturn value for this GetDataHistoryOptions.
     * 
     * @param maxReturn
     */
    public void setMaxReturn(int maxReturn) {
        this.maxReturn = maxReturn;
    }


    /**
     * Gets the initialDate value for this GetDataHistoryOptions.
     * 
     * @return initialDate
     */
    public java.util.Calendar getInitialDate() {
        return initialDate;
    }


    /**
     * Sets the initialDate value for this GetDataHistoryOptions.
     * 
     * @param initialDate
     */
    public void setInitialDate(java.util.Calendar initialDate) {
        this.initialDate = initialDate;
    }


    /**
     * Gets the finalDate value for this GetDataHistoryOptions.
     * 
     * @return finalDate
     */
    public java.util.Calendar getFinalDate() {
        return finalDate;
    }


    /**
     * Sets the finalDate value for this GetDataHistoryOptions.
     * 
     * @param finalDate
     */
    public void setFinalDate(java.util.Calendar finalDate) {
        this.finalDate = finalDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetDataHistoryOptions)) return false;
        GetDataHistoryOptions other = (GetDataHistoryOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.maxReturn == other.getMaxReturn() &&
            ((this.initialDate==null && other.getInitialDate()==null) || 
             (this.initialDate!=null &&
              this.initialDate.equals(other.getInitialDate()))) &&
            ((this.finalDate==null && other.getFinalDate()==null) || 
             (this.finalDate!=null &&
              this.finalDate.equals(other.getFinalDate())));
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
        _hashCode += getMaxReturn();
        if (getInitialDate() != null) {
            _hashCode += getInitialDate().hashCode();
        }
        if (getFinalDate() != null) {
            _hashCode += getFinalDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetDataHistoryOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "GetDataHistoryOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "maxReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initialDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "initialDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finalDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://hda.api.scadabr.org.br", "finalDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
