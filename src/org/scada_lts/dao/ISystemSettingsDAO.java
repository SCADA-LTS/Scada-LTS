package org.scada_lts.dao;

public interface ISystemSettingsDAO {
    public abstract void setValue(java.lang.String arg0, java.lang.String arg1);
    public abstract void setIntValue(java.lang.String arg0, int arg1);
    public abstract void setBooleanValue(java.lang.String arg0, boolean arg1);
    public abstract void removeValue(java.lang.String arg0);
    public abstract java.lang.String getDatabaseSchemaVersion(java.lang.String arg0, java.lang.String arg1);
    public abstract void resetDataBase();
    public abstract double getDatabaseSize();
}

