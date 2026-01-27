package org.scada_lts.web.mvc.api.datasources;

import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.Set;

public class SearchDataPointJson {

    @XssProtect
    private String keywordSearch;
    private Set<Integer> includeIds;
    private Set<Integer> excludeIds;
    private Set<Integer> dataTypes;
    private int limit;
    private boolean startsWith;
    private int page;
    private boolean setPermissionRequired;
    private Boolean settable;

    public String getKeywordSearch() {
        return keywordSearch;
    }

    public void setKeywordSearch(String keywordSearch) {
        this.keywordSearch = keywordSearch;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Set<Integer> getIncludeIds() {
        return includeIds;
    }

    public void setIncludeIds(Set<Integer> includeIds) {
        this.includeIds = includeIds;
    }

    public Set<Integer> getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(Set<Integer> excludeIds) {
        this.excludeIds = excludeIds;
    }

    public Set<Integer> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Set<Integer> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public boolean isStartsWith() {
        return startsWith;
    }

    public void setStartsWith(boolean startsWith) {
        this.startsWith = startsWith;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isSetPermissionRequired() {
        return setPermissionRequired;
    }

    public void setSetPermissionRequired(boolean setPermissionRequired) {
        this.setPermissionRequired = setPermissionRequired;
    }

    public Boolean getSettable() {
        return settable;
    }

    public void setSettable(Boolean settable) {
        this.settable = settable;
    }
}
