package br.org.scadabr.vo.dataSource.opcua;

import br.org.scadabr.rt.dataSource.opcua.OpcUaPointLocatorRT;
import com.serotonin.json.*;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class OpcUaPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	@Override
	public PointLocatorRT createRuntime() {
		return new OpcUaPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", new LocalizableMessage("dsEdit.opc.tag"), this.tag);
	}

	@Override
	public int getDataTypeId() {
		return dataType.getDataTypesId();
	}

	public void setDataType(OpcUaDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@JsonRemoteProperty
	private String tag = "";
	private OpcUaDataType dataType = OpcUaDataType.DEFAULT;
	@JsonRemoteProperty
	private boolean settable;
	private OpcUaIdentifierType identifierType = OpcUaIdentifierType.DEFAULT;
	@JsonRemoteProperty
	private String identifier;
	@JsonRemoteProperty
	private int namespaceIndex;
	@JsonRemoteProperty
	private String attributes;

	public OpcUaPointLocatorVO() {
	}

	private OpcUaPointLocatorVO(OpcUaPointLocatorVO pointLocator) {
		this.tag = pointLocator.getTag();
		this.dataType = pointLocator.getDataType();
		this.settable = pointLocator.isSettable();
		this.identifierType = pointLocator.getIdentifierType();
		this.identifier = pointLocator.getIdentifier();
		this.namespaceIndex = pointLocator.getNamespaceIndex();
		this.attributes = pointLocator.getAttributes();
	}

	@Override
	public void validate(DwrResponseI18n response) {

	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		AuditEventType.addDataTypeMessage(list, "dsEdit.opcua.dataType", dataType);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.tag", tag);
		AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.identifier", identifier);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.identifierType", identifierType);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.namespaceIndex", namespaceIndex);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.attributes", attributes);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		OpcUaPointLocatorVO from = (OpcUaPointLocatorVO) o;

		AuditEventType.maybeAddObjectChangeMessage(list,
				"dsEdit.opcua.dataType", from.dataType, dataType);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.tag",
				from.tag, tag);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable",
				from.settable, settable);

		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.identifier",
				from.identifier, identifier);

		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.identifierType",
				from.identifierType, identifierType);

		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.namespaceIndex",
				from.namespaceIndex, namespaceIndex);

		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.attributes",
				from.attributes, attributes);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, tag);
		out.writeObject(dataType);
		out.writeBoolean(settable);
		SerializationHelper.writeSafeUTF(out, identifier);
		out.writeObject(identifierType);
		out.writeInt(namespaceIndex);
		SerializationHelper.writeSafeUTF(out, attributes);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			tag = SerializationHelper.readSafeUTF(in);
			try {
				dataType = (OpcUaDataType) in.readObject();
			} catch (Exception e) {
				dataType = OpcUaDataType.DEFAULT;
			}
			settable = in.readBoolean();
			identifier = SerializationHelper.readSafeUTF(in);
			try {
				identifierType = (OpcUaIdentifierType) in.readObject();
			} catch (Exception e) {
				identifierType = OpcUaIdentifierType.DEFAULT;
			}
			namespaceIndex = in.readInt();
			attributes = SerializationHelper.readSafeUTF(in);
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {

		String dataTypeJson = json.getString("dataType");
		if(dataTypeJson != null) {
			try {
				dataType = OpcUaDataType.valueOf(dataTypeJson);
			} catch (Exception ex) {
				dataType = OpcUaDataType.DEFAULT;
			}
		}

		String identifierTypeJson = json.getString("identifierType");
		if(identifierTypeJson != null) {
			try {
				identifierType = OpcUaIdentifierType.valueOf(identifierTypeJson);
			} catch (Exception ex) {
				identifierType = OpcUaIdentifierType.DEFAULT;
			}
		}
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		serializeDataType(map);
		map.put("identifierType", identifierType.name());
		map.put("dataType", dataType.name());
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public OpcUaDataType getDataType() {
		return dataType;
	}

	public OpcUaIdentifierType getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(OpcUaIdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getNamespaceIndex() {
		return namespaceIndex;
	}

	public void setNamespaceIndex(int namespaceIndex) {
		this.namespaceIndex = namespaceIndex;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getNodeId() {
		if (!StringUtils.isEmpty(getAttributes())) {
			return MessageFormat.format("ns={0};{1}={2};{3};{4}", getNamespaceIndex(), getIdentifierType().getCode(), getIdentifier(), getAttributes(), getDataType());
		}
		return MessageFormat.format("ns={0};{1}={2};{3}", getNamespaceIndex(), getIdentifierType().getCode(), getIdentifier(), getDataType());
	}

	public OpcUaPointLocatorVO copy() {
		return new OpcUaPointLocatorVO(this);
	}
}
