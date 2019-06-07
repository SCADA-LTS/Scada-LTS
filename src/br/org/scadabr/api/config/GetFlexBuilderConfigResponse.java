/**
 * GetFlexBuilderConfigResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.config;

public class GetFlexBuilderConfigResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private br.org.scadabr.api.vo.FlexProject project;

	private br.org.scadabr.api.vo.ReplyBase replyBase;

	public GetFlexBuilderConfigResponse() {
	}

	public GetFlexBuilderConfigResponse(
			br.org.scadabr.api.vo.FlexProject project,
			br.org.scadabr.api.vo.ReplyBase replyBase) {
		this.project = project;
		this.replyBase = replyBase;
	}

	/**
	 * Gets the project value for this GetFlexBuilderConfigResponse.
	 * 
	 * @return project
	 */
	public br.org.scadabr.api.vo.FlexProject getProject() {
		return project;
	}

	/**
	 * Sets the project value for this GetFlexBuilderConfigResponse.
	 * 
	 * @param project
	 */
	public void setProject(br.org.scadabr.api.vo.FlexProject project) {
		this.project = project;
	}

	/**
	 * Gets the replyBase value for this GetFlexBuilderConfigResponse.
	 * 
	 * @return replyBase
	 */
	public br.org.scadabr.api.vo.ReplyBase getReplyBase() {
		return replyBase;
	}

	/**
	 * Sets the replyBase value for this GetFlexBuilderConfigResponse.
	 * 
	 * @param replyBase
	 */
	public void setReplyBase(br.org.scadabr.api.vo.ReplyBase replyBase) {
		this.replyBase = replyBase;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetFlexBuilderConfigResponse))
			return false;
		GetFlexBuilderConfigResponse other = (GetFlexBuilderConfigResponse) obj;
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
				&& ((this.project == null && other.getProject() == null) || (this.project != null && this.project
						.equals(other.getProject())))
				&& ((this.replyBase == null && other.getReplyBase() == null) || (this.replyBase != null && this.replyBase
						.equals(other.getReplyBase())));
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
		if (getReplyBase() != null) {
			_hashCode += getReplyBase().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetFlexBuilderConfigResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://config.api.scadabr.org.br",
				">GetFlexBuilderConfigResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("project");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://config.api.scadabr.org.br", "project"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "FlexProject"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("replyBase");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://config.api.scadabr.org.br", "replyBase"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://vo.api.scadabr.org.br", "ReplyBase"));
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
