/**
 * ServerStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class ServerStatus  implements java.io.Serializable {
    private java.util.Calendar startTime;

    private br.org.scadabr.api.constants.ServerStateCode serverState;

    private java.lang.String productVersion;

    private java.lang.String[] supportedLocaleIDs;

    public ServerStatus() {
    }

    public ServerStatus(
           java.util.Calendar startTime,
           br.org.scadabr.api.constants.ServerStateCode serverState,
           java.lang.String productVersion,
           java.lang.String[] supportedLocaleIDs) {
           this.startTime = startTime;
           this.serverState = serverState;
           this.productVersion = productVersion;
           this.supportedLocaleIDs = supportedLocaleIDs;
    }


    /**
     * Gets the startTime value for this ServerStatus.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this ServerStatus.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }


    /**
     * Gets the serverState value for this ServerStatus.
     * 
     * @return serverState
     */
    public br.org.scadabr.api.constants.ServerStateCode getServerState() {
        return serverState;
    }


    /**
     * Sets the serverState value for this ServerStatus.
     * 
     * @param serverState
     */
    public void setServerState(br.org.scadabr.api.constants.ServerStateCode serverState) {
        this.serverState = serverState;
    }


    /**
     * Gets the productVersion value for this ServerStatus.
     * 
     * @return productVersion
     */
    public java.lang.String getProductVersion() {
        return productVersion;
    }


    /**
     * Sets the productVersion value for this ServerStatus.
     * 
     * @param productVersion
     */
    public void setProductVersion(java.lang.String productVersion) {
        this.productVersion = productVersion;
    }


    /**
     * Gets the supportedLocaleIDs value for this ServerStatus.
     * 
     * @return supportedLocaleIDs
     */
    public java.lang.String[] getSupportedLocaleIDs() {
        return supportedLocaleIDs;
    }


    /**
     * Sets the supportedLocaleIDs value for this ServerStatus.
     * 
     * @param supportedLocaleIDs
     */
    public void setSupportedLocaleIDs(java.lang.String[] supportedLocaleIDs) {
        this.supportedLocaleIDs = supportedLocaleIDs;
    }

    public java.lang.String getSupportedLocaleIDs(int i) {
        return this.supportedLocaleIDs[i];
    }

    public void setSupportedLocaleIDs(int i, java.lang.String _value) {
        this.supportedLocaleIDs[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServerStatus)) return false;
        ServerStatus other = (ServerStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            ((this.serverState==null && other.getServerState()==null) || 
             (this.serverState!=null &&
              this.serverState.equals(other.getServerState()))) &&
            ((this.productVersion==null && other.getProductVersion()==null) || 
             (this.productVersion!=null &&
              this.productVersion.equals(other.getProductVersion()))) &&
            ((this.supportedLocaleIDs==null && other.getSupportedLocaleIDs()==null) || 
             (this.supportedLocaleIDs!=null &&
              java.util.Arrays.equals(this.supportedLocaleIDs, other.getSupportedLocaleIDs())));
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
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        if (getServerState() != null) {
            _hashCode += getServerState().hashCode();
        }
        if (getProductVersion() != null) {
            _hashCode += getProductVersion().hashCode();
        }
        if (getSupportedLocaleIDs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSupportedLocaleIDs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSupportedLocaleIDs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServerStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ServerStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serverState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "serverState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ServerStateCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "productVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supportedLocaleIDs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "supportedLocaleIDs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
