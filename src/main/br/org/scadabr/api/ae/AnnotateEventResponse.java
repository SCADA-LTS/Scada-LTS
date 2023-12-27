/**
 * AnnotateEventResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.ae;

public class AnnotateEventResponse  implements java.io.Serializable {
    private br.org.scadabr.api.vo.APIError[] errors;

    private br.org.scadabr.api.vo.EventMessage[] eventMessagesList;

    private br.org.scadabr.api.vo.ReplyBase replyBase;

    public AnnotateEventResponse() {
    }

    public AnnotateEventResponse(
           br.org.scadabr.api.vo.APIError[] errors,
           br.org.scadabr.api.vo.EventMessage[] eventMessagesList,
           br.org.scadabr.api.vo.ReplyBase replyBase) {
           this.errors = errors;
           this.eventMessagesList = eventMessagesList;
           this.replyBase = replyBase;
    }


    /**
     * Gets the errors value for this AnnotateEventResponse.
     * 
     * @return errors
     */
    public br.org.scadabr.api.vo.APIError[] getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this AnnotateEventResponse.
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
     * Gets the eventMessagesList value for this AnnotateEventResponse.
     * 
     * @return eventMessagesList
     */
    public br.org.scadabr.api.vo.EventMessage[] getEventMessagesList() {
        return eventMessagesList;
    }


    /**
     * Sets the eventMessagesList value for this AnnotateEventResponse.
     * 
     * @param eventMessagesList
     */
    public void setEventMessagesList(br.org.scadabr.api.vo.EventMessage[] eventMessagesList) {
        this.eventMessagesList = eventMessagesList;
    }

    public br.org.scadabr.api.vo.EventMessage getEventMessagesList(int i) {
        return this.eventMessagesList[i];
    }

    public void setEventMessagesList(int i, br.org.scadabr.api.vo.EventMessage _value) {
        this.eventMessagesList[i] = _value;
    }


    /**
     * Gets the replyBase value for this AnnotateEventResponse.
     * 
     * @return replyBase
     */
    public br.org.scadabr.api.vo.ReplyBase getReplyBase() {
        return replyBase;
    }


    /**
     * Sets the replyBase value for this AnnotateEventResponse.
     * 
     * @param replyBase
     */
    public void setReplyBase(br.org.scadabr.api.vo.ReplyBase replyBase) {
        this.replyBase = replyBase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnnotateEventResponse)) return false;
        AnnotateEventResponse other = (AnnotateEventResponse) obj;
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
            ((this.eventMessagesList==null && other.getEventMessagesList()==null) || 
             (this.eventMessagesList!=null &&
              java.util.Arrays.equals(this.eventMessagesList, other.getEventMessagesList()))) &&
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
        if (getEventMessagesList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventMessagesList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventMessagesList(), i);
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
        new org.apache.axis.description.TypeDesc(AnnotateEventResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", ">AnnotateEventResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errors");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "errors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "APIError"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventMessagesList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "eventMessagesList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "EventMessage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replyBase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ae.api.scadabr.org.br", "replyBase"));
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
