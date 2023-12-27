/**
 * ModbusIPConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.vo;

public class ModbusIPConfig implements java.io.Serializable {
	private int id;

	private boolean enabled;

	private java.lang.String name;

	private long pollingPeriod;

	private boolean contiguousBatches;

	private boolean createSlaveMonitorPoints;

	private int timeout;

	private int retries;

	private java.lang.String host;

	private int port;

	public ModbusIPConfig() {
	}

	public ModbusIPConfig(int id, boolean enabled, java.lang.String name,
			long pollingPeriod, boolean contiguousBatches,
			boolean createSlaveMonitorPoints, int timeout, int retries,
			java.lang.String host, int port) {
		this.id = id;
		this.enabled = enabled;
		this.name = name;
		this.pollingPeriod = pollingPeriod;
		this.contiguousBatches = contiguousBatches;
		this.createSlaveMonitorPoints = createSlaveMonitorPoints;
		this.timeout = timeout;
		this.retries = retries;
		this.host = host;
		this.port = port;
	}

	/**
	 * Gets the id value for this ModbusIPConfig.
	 * 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id value for this ModbusIPConfig.
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the enabled value for this ModbusIPConfig.
	 * 
	 * @return enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled value for this ModbusIPConfig.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the name value for this ModbusIPConfig.
	 * 
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name value for this ModbusIPConfig.
	 * 
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Gets the pollingPeriod value for this ModbusIPConfig.
	 * 
	 * @return pollingPeriod
	 */
	public long getPollingPeriod() {
		return pollingPeriod;
	}

	/**
	 * Sets the pollingPeriod value for this ModbusIPConfig.
	 * 
	 * @param pollingPeriod
	 */
	public void setPollingPeriod(long pollingPeriod) {
		this.pollingPeriod = pollingPeriod;
	}

	/**
	 * Gets the contiguousBatches value for this ModbusIPConfig.
	 * 
	 * @return contiguousBatches
	 */
	public boolean isContiguousBatches() {
		return contiguousBatches;
	}

	/**
	 * Sets the contiguousBatches value for this ModbusIPConfig.
	 * 
	 * @param contiguousBatches
	 */
	public void setContiguousBatches(boolean contiguousBatches) {
		this.contiguousBatches = contiguousBatches;
	}

	/**
	 * Gets the createSlaveMonitorPoints value for this ModbusIPConfig.
	 * 
	 * @return createSlaveMonitorPoints
	 */
	public boolean isCreateSlaveMonitorPoints() {
		return createSlaveMonitorPoints;
	}

	/**
	 * Sets the createSlaveMonitorPoints value for this ModbusIPConfig.
	 * 
	 * @param createSlaveMonitorPoints
	 */
	public void setCreateSlaveMonitorPoints(boolean createSlaveMonitorPoints) {
		this.createSlaveMonitorPoints = createSlaveMonitorPoints;
	}

	/**
	 * Gets the timeout value for this ModbusIPConfig.
	 * 
	 * @return timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout value for this ModbusIPConfig.
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets the retries value for this ModbusIPConfig.
	 * 
	 * @return retries
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * Sets the retries value for this ModbusIPConfig.
	 * 
	 * @param retries
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * Gets the host value for this ModbusIPConfig.
	 * 
	 * @return host
	 */
	public java.lang.String getHost() {
		return host;
	}

	/**
	 * Sets the host value for this ModbusIPConfig.
	 * 
	 * @param host
	 */
	public void setHost(java.lang.String host) {
		this.host = host;
	}

	/**
	 * Gets the port value for this ModbusIPConfig.
	 * 
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port value for this ModbusIPConfig.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ModbusIPConfig))
			return false;
		ModbusIPConfig other = (ModbusIPConfig) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& this.id == other.getId()
				&& this.enabled == other.isEnabled()
				&& ((this.name == null && other.getName() == null) || (this.name != null && this.name
						.equals(other.getName())))
				&& this.pollingPeriod == other.getPollingPeriod()
				&& this.contiguousBatches == other.isContiguousBatches()
				&& this.createSlaveMonitorPoints == other
						.isCreateSlaveMonitorPoints()
				&& this.timeout == other.getTimeout()
				&& this.retries == other.getRetries()
				&& ((this.host == null && other.getHost() == null) || (this.host != null && this.host
						.equals(other.getHost())))
				&& this.port == other.getPort();
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
		_hashCode += new Long(getPollingPeriod()).hashCode();
		_hashCode += (isContiguousBatches() ? Boolean.TRUE : Boolean.FALSE)
				.hashCode();
		_hashCode += getTimeout();
		_hashCode += getRetries();
		if (getHost() != null) {
			_hashCode += getHost().hashCode();
		}
		_hashCode += getPort();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ModbusIPConfig.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "ModbusIPConfig"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("enabled");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "enabled"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("pollingPeriod");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "pollingPeriod"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("contiguousBatches");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "contiguousBatches"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createSlaveMonitorPoints");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "createSlaveMonitorPoints"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("timeout");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "timeout"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("retries");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "retries"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("host");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "host"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("port");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "port"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
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
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType,
				_xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType,
				_xmlType, typeDesc);
	}

}
