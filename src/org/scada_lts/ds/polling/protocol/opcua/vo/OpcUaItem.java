package org.scada_lts.ds.polling.protocol.opcua.vo;

import java.util.Objects;

public class OpcUaItem {
    private String identifier;
    private OpcUaIdentifierType identifierType;
    private String nodeName;
    private OpcUaDataType opcDataType;
    private int dataTypeId;
    private boolean settable;
    private boolean validate;
    private int namespaceIndex;

    public OpcUaItem(boolean validate, OpcUaPointLocatorVO pointLocator) {
        this.validate = validate;
        this.nodeName = pointLocator.getNodeName();
        this.settable = pointLocator.isSettable();
        this.identifier = pointLocator.getIdentifier();
        this.identifierType = pointLocator.getIdentifierType();
        this.opcDataType = pointLocator.getOpcDataType();
        this.dataTypeId = pointLocator.getOpcDataType().getDataTypeId();
        this.namespaceIndex = pointLocator.getNamespaceIndex();
    }

    public boolean isValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public OpcUaDataType getOpcDataType() {
        return this.opcDataType;
    }

    public void setOpcDataType(OpcUaDataType opcDataType) {
        this.opcDataType = opcDataType;
    }

    public boolean isSettable() {
        return this.settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getIdentifierDisplay() {
        return org.scada_lts.serorepl.utils.StringUtils.truncate(String.valueOf(getIdentifier()), "...", 128);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public OpcUaIdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(OpcUaIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public int getNamespaceIndex() {
        return namespaceIndex;
    }

    public void setNamespaceIndex(int namespaceIndex) {
        this.namespaceIndex = namespaceIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OpcUaItem opcUaItem = (OpcUaItem) o;
        return namespaceIndex == opcUaItem.namespaceIndex && Objects.equals(identifier, opcUaItem.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, namespaceIndex);
    }
}
