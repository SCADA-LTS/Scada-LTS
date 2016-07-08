/**
 * SetFlexBuilderConfigParams.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.config;

public class SetFlexBuilderConfigParams  implements java.io.Serializable {
    private br.org.scadabr.api.vo.FlexProject project;

    public SetFlexBuilderConfigParams() {
    }

    public SetFlexBuilderConfigParams(
           br.org.scadabr.api.vo.FlexProject project) {
           this.project = project;
    }


    /**
     * Gets the project value for this SetFlexBuilderConfigParams.
     * 
     * @return project
     */
    public br.org.scadabr.api.vo.FlexProject getProject() {
        return project;
    }


    /**
     * Sets the project value for this SetFlexBuilderConfigParams.
     * 
     * @param project
     */
    public void setProject(br.org.scadabr.api.vo.FlexProject project) {
        this.project = project;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SetFlexBuilderConfigParams)) return false;
        SetFlexBuilderConfigParams other = (SetFlexBuilderConfigParams) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.project==null && other.getProject()==null) || 
             (this.project!=null &&
              this.project.equals(other.getProject())));
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
        if (getProject() != null) {
            _hashCode += getProject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SetFlexBuilderConfigParams.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", ">SetFlexBuilderConfigParams"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("project");
        elemField.setXmlName(new javax.xml.namespace.QName("http://config.api.scadabr.org.br", "project"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "FlexProject"));
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
