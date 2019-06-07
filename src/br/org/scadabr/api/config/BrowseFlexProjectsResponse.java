/**
 * BrowseFlexProjectsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.config;

public class BrowseFlexProjectsResponse  implements java.io.Serializable {
    private br.org.scadabr.api.vo.FlexProject[] projects;

    private br.org.scadabr.api.vo.ReplyBase replyBase;

    public BrowseFlexProjectsResponse() {
    }

    public BrowseFlexProjectsResponse(
           br.org.scadabr.api.vo.FlexProject[] projects,
           br.org.scadabr.api.vo.ReplyBase replyBase) {
           this.projects = projects;
           this.replyBase = replyBase;
    }


    /**
     * Gets the projects value for this BrowseFlexProjectsResponse.
     * 
     * @return projects
     */
    public br.org.scadabr.api.vo.FlexProject[] getProjects() {
        return projects;
    }


    /**
     * Sets the projects value for this BrowseFlexProjectsResponse.
     * 
     * @param projects
     */
    public void setProjects(br.org.scadabr.api.vo.FlexProject[] projects) {
        this.projects = projects;
    }

    public br.org.scadabr.api.vo.FlexProject getProjects(int i) {
        return this.projects[i];
    }

    public void setProjects(int i, br.org.scadabr.api.vo.FlexProject _value) {
        this.projects[i] = _value;
    }


    /**
     * Gets the replyBase value for this BrowseFlexProjectsResponse.
     * 
     * @return replyBase
     */
    public br.org.scadabr.api.vo.ReplyBase getReplyBase() {
        return replyBase;
    }


    /**
     * Sets the replyBase value for this BrowseFlexProjectsResponse.
     * 
     * @param replyBase
     */
    public void setReplyBase(br.org.scadabr.api.vo.ReplyBase replyBase) {
        this.replyBase = replyBase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BrowseFlexProjectsResponse)) return false;
        BrowseFlexProjectsResponse other = (BrowseFlexProjectsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.projects==null && other.getProjects()==null) || 
             (this.projects!=null &&
              java.util.Arrays.equals(this.projects, other.getProjects()))) &&
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
        if (getProjects() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getProjects());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProjects(), i);
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
        new org.apache.axis.description.TypeDesc(BrowseFlexProjectsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">BrowseFlexProjectsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("projects");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "projects"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "FlexProject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("replyBase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "replyBase"));
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
