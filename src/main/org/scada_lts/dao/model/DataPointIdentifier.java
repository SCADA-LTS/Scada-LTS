package org.scada_lts.dao.model;

import com.serotonin.mango.vo.PointDataType;

public class DataPointIdentifier extends ScadaObjectIdentifier {

    private String extendName;
    private PointDataType dataType;
    private boolean enabled;
    private String description;
    private int type;
    private int typeId;
    private String datasourceName;

    private DataPointIdentifier(int id, String xid, String name, String extendName, PointDataType dataType,
                               boolean enabled, String description, String datasourceName) {
        super(id, xid, name);
        this.extendName = extendName;
        this.dataType = dataType;
        this.enabled = enabled;
        this.description = description;
        this.type = dataType.getCode();
        this.typeId = dataType.getCode();
        this.datasourceName = datasourceName;
    }

    public static Builder builder(PointDataType dataType) {
        return new Builder(dataType);
    }

    public static class Builder {

        private final PointDataType dataType;
        private int id;
        private String xid;
        private String name;
        private String extendName;
        private boolean enabled;
        private String description;
        private String datasourceName;

        public Builder(PointDataType dataType) {
            this.dataType = dataType;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder xid(String xid) {
            this.xid = xid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder extendName(String extendName) {
            this.extendName = extendName;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder dataSourceName(String datasourceName) {
            this.datasourceName = datasourceName;
            return this;
        }

        public DataPointIdentifier build() {
            return new DataPointIdentifier(id, xid, name, extendName, dataType, enabled, description, datasourceName);
        }
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    public PointDataType getDataType() {
        return dataType;
    }

    public void setDataType(PointDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }
}
