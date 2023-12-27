/**
 * ModbusDataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api.constants;

public class ModbusDataType implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected ModbusDataType(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _BINARY = "BINARY";
	public static final java.lang.String _TWO_BYTE_UNSIGNED_INT = "TWO_BYTE_UNSIGNED_INT";
	public static final java.lang.String _TWO_BYTE_SIGNED_INT = "TWO_BYTE_SIGNED_INT";
	public static final java.lang.String _TWO_BYTE_BCD = "TWO_BYTE_BCD";
	public static final java.lang.String _FOUR_BYTE_UNSIGNED_INT = "FOUR_BYTE_UNSIGNED_INT";
	public static final java.lang.String _FOUR_BYTE_SIGNED_INT = "FOUR_BYTE_SIGNED_INT";
	public static final java.lang.String _FOUR_BYTE_UNSIGNED_INT_SWAPPED = "FOUR_BYTE_UNSIGNED_INT_SWAPPED";
	public static final java.lang.String _FOUR_BYTE_SIGNED_INT_SWAPPED = "FOUR_BYTE_SIGNED_INT_SWAPPED";
	public static final java.lang.String _FOUR_BYTE_FLOAT = "FOUR_BYTE_FLOAT";
	public static final java.lang.String _FOUR_BYTE_FLOAT_SWAPPED = "FOUR_BYTE_FLOAT_SWAPPED";
	public static final java.lang.String _FOUR_BYTE_FLOAT_SWAPPED_INVERTED = "FOUR_BYTE_FLOAT_SWAPPED_INVERTED";
	public static final java.lang.String _FOUR_BYTE_BCD = "FOUR_BYTE_BCD";
	public static final java.lang.String _EIGHT_BYTE_UNSIGNED_INT = "EIGHT_BYTE_UNSIGNED_INT";
	public static final java.lang.String _EIGHT_BYTE_SIGNED_INT = "EIGHT_BYTE_SIGNED_INT";
	public static final java.lang.String _EIGHT_BYTE_UNSIGNED_INT_SWAPPED = "EIGHT_BYTE_UNSIGNED_INT_SWAPPED";
	public static final java.lang.String _EIGHT_BYTE_SIGNED_INT_SWAPPED = "EIGHT_BYTE_SIGNED_INT_SWAPPED";
	public static final java.lang.String _EIGHT_BYTE_FLOAT = "EIGHT_BYTE_FLOAT";
	public static final java.lang.String _EIGHT_BYTE_FLOAT_SWAPPED = "EIGHT_BYTE_FLOAT_SWAPPED";
	public static final ModbusDataType BINARY = new ModbusDataType(_BINARY);
	public static final ModbusDataType TWO_BYTE_UNSIGNED_INT = new ModbusDataType(
			_TWO_BYTE_UNSIGNED_INT);
	public static final ModbusDataType TWO_BYTE_SIGNED_INT = new ModbusDataType(
			_TWO_BYTE_SIGNED_INT);
	public static final ModbusDataType TWO_BYTE_BCD = new ModbusDataType(
			_TWO_BYTE_BCD);
	public static final ModbusDataType FOUR_BYTE_UNSIGNED_INT = new ModbusDataType(
			_FOUR_BYTE_UNSIGNED_INT);
	public static final ModbusDataType FOUR_BYTE_SIGNED_INT = new ModbusDataType(
			_FOUR_BYTE_SIGNED_INT);
	public static final ModbusDataType FOUR_BYTE_UNSIGNED_INT_SWAPPED = new ModbusDataType(
			_FOUR_BYTE_UNSIGNED_INT_SWAPPED);
	public static final ModbusDataType FOUR_BYTE_SIGNED_INT_SWAPPED = new ModbusDataType(
			_FOUR_BYTE_SIGNED_INT_SWAPPED);
	public static final ModbusDataType FOUR_BYTE_FLOAT = new ModbusDataType(
			_FOUR_BYTE_FLOAT);
	public static final ModbusDataType FOUR_BYTE_FLOAT_SWAPPED = new ModbusDataType(
			_FOUR_BYTE_FLOAT_SWAPPED);
	public static final ModbusDataType FOUR_BYTE_FLOAT_SWAPPED_INVERTED = new ModbusDataType(
			_FOUR_BYTE_FLOAT_SWAPPED_INVERTED);
	public static final ModbusDataType FOUR_BYTE_BCD = new ModbusDataType(
			_FOUR_BYTE_BCD);
	public static final ModbusDataType EIGHT_BYTE_UNSIGNED_INT = new ModbusDataType(
			_EIGHT_BYTE_UNSIGNED_INT);
	public static final ModbusDataType EIGHT_BYTE_SIGNED_INT = new ModbusDataType(
			_EIGHT_BYTE_SIGNED_INT);
	public static final ModbusDataType EIGHT_BYTE_UNSIGNED_INT_SWAPPED = new ModbusDataType(
			_EIGHT_BYTE_UNSIGNED_INT_SWAPPED);
	public static final ModbusDataType EIGHT_BYTE_SIGNED_INT_SWAPPED = new ModbusDataType(
			_EIGHT_BYTE_SIGNED_INT_SWAPPED);
	public static final ModbusDataType EIGHT_BYTE_FLOAT = new ModbusDataType(
			_EIGHT_BYTE_FLOAT);
	public static final ModbusDataType EIGHT_BYTE_FLOAT_SWAPPED = new ModbusDataType(
			_EIGHT_BYTE_FLOAT_SWAPPED);

	public java.lang.String getValue() {
		return _value_;
	}

	public static ModbusDataType fromValue(java.lang.String value)
			throws java.lang.IllegalArgumentException {
		ModbusDataType enumeration = (ModbusDataType) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static ModbusDataType fromString(java.lang.String value)
			throws java.lang.IllegalArgumentException {
		return fromValue(value);
	}

	public boolean equals(java.lang.Object obj) {
		return (obj == this);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public java.lang.String toString() {
		return _value_;
	}

	public java.lang.Object readResolve() throws java.io.ObjectStreamException {
		return fromValue(_value_);
	}

	public static org.apache.axis.encoding.Serializer getSerializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.EnumSerializer(_javaType,
				_xmlType);
	}

	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.EnumDeserializer(_javaType,
				_xmlType);
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ModbusDataType.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://constants.api.scadabr.org.br", "ModbusDataType"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
