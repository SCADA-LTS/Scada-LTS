package com.serotonin.mango;


/**
 * Responsibility:
 *
 * Here we have dwrScriptSessionId and businessObject (BU). Under BU variable there is instance eeg. View, Point, WatchList etc.
 *
 *
 */
public class BusinessObjectVSDwrScriptSessionId {

    /**
     * unique dwrScriptSessionId
     */
    private String dwrScriptSessionId;

    /**
     * instance of View or Point or WatchList or DataSource
     */
    private Object businessObject;

    public BusinessObjectVSDwrScriptSessionId(String dwrScriptSessionId, Object businessObject) {
        this.dwrScriptSessionId = dwrScriptSessionId;
        this.businessObject = businessObject;
    }

    public String getDwrScriptSessionId() {
        return dwrScriptSessionId;
    }

    public Object getBusinessObject() {
        return businessObject;
    }

}
