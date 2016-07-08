/**
 * ReplyBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class ReplyBase  implements java.io.Serializable {
    private java.util.Calendar rcvTime;

    private java.util.Calendar replyTime;

    public ReplyBase() {
    }

    public ReplyBase(
           java.util.Calendar rcvTime,
           java.util.Calendar replyTime) {
           this.rcvTime = rcvTime;
           this.replyTime = replyTime;
    }


    /**
     * Gets the rcvTime value for this ReplyBase.
     * 
     * @return rcvTime
     */
    public java.util.Calendar getRcvTime() {
        return rcvTime;
    }


    /**
     * Sets the rcvTime value for this ReplyBase.
     * 
     * @param rcvTime
     */
    public void setRcvTime(java.util.Calendar rcvTime) {
        this.rcvTime = rcvTime;
    }


    /**
     * Gets the replyTime value for this ReplyBase.
     * 
     * @return replyTime
     */
    public java.util.Calendar getReplyTime() {
        return replyTime;
    }


    /**
     * Sets the replyTime value for this ReplyBase.
     * 
     * @param replyTime
     */
    public void setReplyTime(java.util.Calendar replyTime) {
        this.replyTime = replyTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReplyBase)) return false;
        ReplyBase other = (ReplyBase) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rcvTime==null && other.getRcvTime()==null) || 
             (this.rcvTime!=null &&
              this.rcvTime.equals(other.getRcvTime()))) &&
            ((this.replyTime==null && other.getReplyTime()==null) || 
             (this.replyTime!=null &&
              this.replyTime.equals(other.getReplyTime())));
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
        if (getRcvTime() != null) {
            _hashCode += getRcvTime().hashCode();
        }
        if (getReplyTime() != null) {
            _hashCode += getReplyTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReplyBase.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ReplyBase"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcvTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "rcvTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replyTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "replyTime"));
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
