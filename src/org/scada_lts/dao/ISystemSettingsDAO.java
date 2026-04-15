package org.scada_lts.dao;

public interface ISystemSettingsDAO {
    public abstract void setValue(java.lang.String key, java.lang.String value);
    public abstract void setIntValue(java.lang.String key, int value);
    public abstract void setBooleanValue(java.lang.String key, boolean value);
    public abstract void removeValue(java.lang.String key);
    public abstract java.lang.String getDatabaseSchemaVersion(java.lang.String key, java.lang.String defaultValue);
    public abstract void resetDataBase();
    public abstract double getDatabaseSize();
}

