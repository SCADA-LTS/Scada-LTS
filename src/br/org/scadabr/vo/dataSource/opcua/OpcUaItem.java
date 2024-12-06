package br.org.scadabr.vo.dataSource.opcua;

public class OpcUaItem {
    private String identifier;
    private OpcUaIdentifierType identifierType;
    private String tag;
    private OpcUaDataType dataType;
    private int dataTypeId;
    private boolean settable;
    private boolean validate;
    private int namespaceIndex;

    public OpcUaItem(boolean validate, OpcUaPointLocatorVO pointLocator) {
        this.validate = validate;
        this.tag = pointLocator.getTag();
        this.settable = pointLocator.isSettable();
        this.identifier = pointLocator.getIdentifier();
        this.identifierType = pointLocator.getIdentifierType();
        this.dataType = pointLocator.getDataType();
        this.dataTypeId = pointLocator.getDataType().getDataTypesId();
        this.namespaceIndex = pointLocator.getNamespaceIndex();
    }

    public boolean isValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public OpcUaDataType getDataType() {
        return this.dataType;
    }

    public void setDataType(OpcUaDataType dataType) {
        this.dataType = dataType;
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
}
