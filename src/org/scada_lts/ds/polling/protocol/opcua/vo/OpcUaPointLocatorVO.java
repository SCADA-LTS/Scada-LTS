package org.scada_lts.ds.polling.protocol.opcua.vo;

import com.serotonin.json.*;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.ds.polling.PollingPointLocatorRT;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.scada_lts.ds.polling.protocol.opcua.vo.BaseDataTypeUtils.valueByNameOf;

@JsonRemoteEntity
public class OpcUaPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	@Override
	public PointLocatorRT createRuntime() {
		return new PollingPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", org.scada_lts.serorepl.utils.StringUtils.truncate(String.valueOf(getIdentifier()), "...", 128), "");
	}

	@Override
	public int getDataTypeId() {
		return opcDataType.getDataTypeId();
	}

	public void setOpcDataType(OpcUaDataType opcDataType) {
		this.opcDataType = opcDataType;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@JsonRemoteProperty
	private String nodeName = "";
	private OpcUaDataType opcDataType = OpcUaDataType.unknownType();
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
		this.nodeName = pointLocator.getNodeName();
		this.opcDataType = pointLocator.getOpcDataType();
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
		AuditEventType.addDataTypeMessage(list, "dsEdit.opcua.opcDataType", opcDataType);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.nodeName", nodeName);
		AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.identifier", identifier);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.identifierType", identifierType);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.namespaceIndex", namespaceIndex);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.attributes", attributes);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		OpcUaPointLocatorVO from = (OpcUaPointLocatorVO) o;

		AuditEventType.maybeAddObjectChangeMessage(list, "dsEdit.opcua.opcDataType",
				from.opcDataType, opcDataType);

		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.nodeName",
				from.nodeName, nodeName);
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
		SerializationHelper.writeSafeUTF(out, nodeName);
		out.writeObject(opcDataType);
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
			nodeName = SerializationHelper.readSafeUTF(in);
			try {
				this.opcDataType = (OpcUaDataType) in.readObject();
			} catch (Exception e) {
				this.opcDataType = OpcUaDataType.unknownType();
			}
			settable = in.readBoolean();
			identifier = SerializationHelper.readSafeUTF(in);
			try {
				this.identifierType = (OpcUaIdentifierType) in.readObject();
			} catch (Exception e) {
				this.identifierType = OpcUaIdentifierType.DEFAULT;
			}
			namespaceIndex = in.readInt();
			attributes = SerializationHelper.readSafeUTF(in);
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		deserializeDataType(json);
		String dataTypeJson = json.getString("opcDataType");
		if(dataTypeJson != null) {
			try {
				opcDataType = valueByNameOf(dataTypeJson);
			} catch (Exception ex) {
				opcDataType = OpcUaDataType.unknownType();
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
		map.put("opcDataType", opcDataType.getName());
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public OpcUaDataType getOpcDataType() {
		return opcDataType;
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
		return OpcUaUtils.getNodeId(getNamespaceIndex(), getIdentifier(), getIdentifierType(), getAttributes());
	}

	public OpcUaPointLocatorVO copy() {
		return new OpcUaPointLocatorVO(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		OpcUaPointLocatorVO that = (OpcUaPointLocatorVO) o;
		return namespaceIndex == that.namespaceIndex && identifierType == that.identifierType && Objects.equals(identifier, that.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifierType, identifier, namespaceIndex);
	}
}
