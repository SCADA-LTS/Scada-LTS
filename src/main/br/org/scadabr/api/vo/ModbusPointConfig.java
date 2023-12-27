/**
 * ModbusPointConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class ModbusPointConfig  implements java.io.Serializable {
    private int id;

    private boolean enabled;

    private java.lang.String name;

    private java.lang.Integer slaveId;

    private br.org.scadabr.api.constants.ModbusRegisterRange registerRange;

    private br.org.scadabr.api.constants.ModbusDataType dataType;

    private boolean settable;

    private java.lang.Integer offset;

    private java.lang.Integer multiplier;

    private java.lang.Integer aditive;

    public ModbusPointConfig() {
    }

    public ModbusPointConfig(
           int id,
           boolean enabled,
           java.lang.String name,
           java.lang.Integer slaveId,
           br.org.scadabr.api.constants.ModbusRegisterRange registerRange,
           br.org.scadabr.api.constants.ModbusDataType dataType,
           boolean settable,
           java.lang.Integer offset,
           java.lang.Integer multiplier,
           java.lang.Integer aditive) {
           this.id = id;
           this.enabled = enabled;
           this.name = name;
           this.slaveId = slaveId;
           this.registerRange = registerRange;
           this.dataType = dataType;
           this.settable = settable;
           this.offset = offset;
           this.multiplier = multiplier;
           this.aditive = aditive;
    }


    /**
     * Gets the id value for this ModbusPointConfig.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this ModbusPointConfig.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the enabled value for this ModbusPointConfig.
     * 
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }


    /**
     * Sets the enabled value for this ModbusPointConfig.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * Gets the name value for this ModbusPointConfig.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ModbusPointConfig.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the slaveId value for this ModbusPointConfig.
     * 
     * @return slaveId
     */
    public java.lang.Integer getSlaveId() {
        return slaveId;
    }


    /**
     * Sets the slaveId value for this ModbusPointConfig.
     * 
     * @param slaveId
     */
    public void setSlaveId(java.lang.Integer slaveId) {
        this.slaveId = slaveId;
    }


    /**
     * Gets the registerRange value for this ModbusPointConfig.
     * 
     * @return registerRange
     */
    public br.org.scadabr.api.constants.ModbusRegisterRange getRegisterRange() {
        return registerRange;
    }


    /**
     * Sets the registerRange value for this ModbusPointConfig.
     * 
     * @param registerRange
     */
    public void setRegisterRange(br.org.scadabr.api.constants.ModbusRegisterRange registerRange) {
        this.registerRange = registerRange;
    }


    /**
     * Gets the dataType value for this ModbusPointConfig.
     * 
     * @return dataType
     */
    public br.org.scadabr.api.constants.ModbusDataType getDataType() {
        return dataType;
    }


    /**
     * Sets the dataType value for this ModbusPointConfig.
     * 
     * @param dataType
     */
    public void setDataType(br.org.scadabr.api.constants.ModbusDataType dataType) {
        this.dataType = dataType;
    }


    /**
     * Gets the settable value for this ModbusPointConfig.
     * 
     * @return settable
     */
    public boolean isSettable() {
        return settable;
    }


    /**
     * Sets the settable value for this ModbusPointConfig.
     * 
     * @param settable
     */
    public void setSettable(boolean settable) {
        this.settable = settable;
    }


    /**
     * Gets the offset value for this ModbusPointConfig.
     * 
     * @return offset
     */
    public java.lang.Integer getOffset() {
        return offset;
    }


    /**
     * Sets the offset value for this ModbusPointConfig.
     * 
     * @param offset
     */
    public void setOffset(java.lang.Integer offset) {
        this.offset = offset;
    }


    /**
     * Gets the multiplier value for this ModbusPointConfig.
     * 
     * @return multiplier
     */
    public java.lang.Integer getMultiplier() {
        return multiplier;
    }


    /**
     * Sets the multiplier value for this ModbusPointConfig.
     * 
     * @param multiplier
     */
    public void setMultiplier(java.lang.Integer multiplier) {
        this.multiplier = multiplier;
    }


    /**
     * Gets the aditive value for this ModbusPointConfig.
     * 
     * @return aditive
     */
    public java.lang.Integer getAditive() {
        return aditive;
    }


    /**
     * Sets the aditive value for this ModbusPointConfig.
     * 
     * @param aditive
     */
    public void setAditive(java.lang.Integer aditive) {
        this.aditive = aditive;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModbusPointConfig)) return false;
        ModbusPointConfig other = (ModbusPointConfig) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            this.enabled == other.isEnabled() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.slaveId==null && other.getSlaveId()==null) || 
             (this.slaveId!=null &&
              this.slaveId.equals(other.getSlaveId()))) &&
            ((this.registerRange==null && other.getRegisterRange()==null) || 
             (this.registerRange!=null &&
              this.registerRange.equals(other.getRegisterRange()))) &&
            ((this.dataType==null && other.getDataType()==null) || 
             (this.dataType!=null &&
              this.dataType.equals(other.getDataType()))) &&
            this.settable == other.isSettable() &&
            ((this.offset==null && other.getOffset()==null) || 
             (this.offset!=null &&
              this.offset.equals(other.getOffset()))) &&
            ((this.multiplier==null && other.getMultiplier()==null) || 
             (this.multiplier!=null &&
              this.multiplier.equals(other.getMultiplier()))) &&
            ((this.aditive==null && other.getAditive()==null) || 
             (this.aditive!=null &&
              this.aditive.equals(other.getAditive())));
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
        _hashCode += getId();
        _hashCode += (isEnabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getSlaveId() != null) {
            _hashCode += getSlaveId().hashCode();
        }
        if (getRegisterRange() != null) {
            _hashCode += getRegisterRange().hashCode();
        }
        if (getDataType() != null) {
            _hashCode += getDataType().hashCode();
        }
        _hashCode += (isSettable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOffset() != null) {
            _hashCode += getOffset().hashCode();
        }
        if (getMultiplier() != null) {
            _hashCode += getMultiplier().hashCode();
        }
        if (getAditive() != null) {
            _hashCode += getAditive().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ModbusPointConfig.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "ModbusPointConfig"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enabled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "enabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("slaveId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "slaveId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registerRange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "registerRange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ModbusRegisterRange"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "dataType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://constants.api.scadabr.org.br", "ModbusDataType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "settable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offset");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "offset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiplier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "multiplier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aditive");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vo.api.scadabr.org.br", "aditive"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
