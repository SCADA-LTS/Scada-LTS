/**
 * GetStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.da;

public class GetStatusResponse  implements java.io.Serializable {
    private br.org.scadabr.api.vo.ReplyBase replyBase;

    private br.org.scadabr.api.vo.ServerStatus serverStatus;

    public GetStatusResponse() {
    }

    public GetStatusResponse(
           br.org.scadabr.api.vo.ReplyBase replyBase,
           br.org.scadabr.api.vo.ServerStatus serverStatus) {
           this.replyBase = replyBase;
           this.serverStatus = serverStatus;
    }


    /**
     * Gets the replyBase value for this GetStatusResponse.
     * 
     * @return replyBase
     */
    public br.org.scadabr.api.vo.ReplyBase getReplyBase() {
        return replyBase;
    }


    /**
     * Sets the replyBase value for this GetStatusResponse.
     * 
     * @param replyBase
     */
    public void setReplyBase(br.org.scadabr.api.vo.ReplyBase replyBase) {
        this.replyBase = replyBase;
    }


    /**
     * Gets the serverStatus value for this GetStatusResponse.
     * 
     * @return serverStatus
     */
    public br.org.scadabr.api.vo.ServerStatus getServerStatus() {
        return serverStatus;
    }


    /**
     * Sets the serverStatus value for this GetStatusResponse.
     * 
     * @param serverStatus
     */
    public void setServerStatus(br.org.scadabr.api.vo.ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetStatusResponse)) return false;
        GetStatusResponse other = (GetStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.replyBase==null && other.getReplyBase()==null) || 
             (this.replyBase!=null &&
              this.replyBase.equals(other.getReplyBase()))) &&
            ((this.serverStatus==null && other.getServerStatus()==null) || 
             (this.serverStatus!=null &&
              this.serverStatus.equals(other.getServerStatus())));
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
        if (getReplyBase() != null) {
            _hashCode += getReplyBase().hashCode();
        }
        if (getServerStatus() != null) {
            _hashCode += getServerStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", ">GetStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replyBase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "replyBase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ReplyBase"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serverStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://da.api.scadabr.org.br", "serverStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ServerStatus"));
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
