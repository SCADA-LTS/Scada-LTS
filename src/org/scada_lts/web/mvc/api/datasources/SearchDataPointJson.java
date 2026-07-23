package org.scada_lts.web.mvc.api.datasources;

import org.scada_lts.web.beans.validation.xss.XssProtect;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Set;

public class SearchDataPointJson {

    @XssProtect
    private String keywordSearch;
    private Set<Integer> excludeIds;
    private Set<Integer> dataTypes;
    @Min(10)
    private int limit;
    private boolean startsWith;
    @Positive
    private int page;
    private Boolean settable;

    public SearchDataPointJson() {
    }

    private SearchDataPointJson(String keywordSearch, Set<Integer> excludeIds, Set<Integer> dataTypes, int limit, boolean startsWith, int page, Boolean settable) {
        this.keywordSearch = keywordSearch;
        this.excludeIds = excludeIds;
        this.dataTypes = dataTypes;
        this.limit = limit;
        this.startsWith = startsWith;
        this.page = page;
        this.settable = settable;
    }

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

    public Boolean getSettable() {
        return settable;
    }

    public void setSettable(Boolean settable) {
        this.settable = settable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keywordSearch;
        private Set<Integer> excludeIds;
        private Set<Integer> dataTypes;
        private int limit;
        private boolean startsWith;
        private int page;
        private Boolean settable;

        public void keywordSearch(String keywordSearch) {
            this.keywordSearch = keywordSearch;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder excludeIds(Set<Integer> excludeIds) {
            this.excludeIds = excludeIds;
            return this;
        }


        public Builder dataTypes(Set<Integer> dataTypes) {
            this.dataTypes = dataTypes;
            return this;
        }


        public Builder startsWith(boolean startsWith) {
            this.startsWith = startsWith;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder settable(Boolean settable) {
            this.settable = settable;
            return this;
        }

        public SearchDataPointJson build() {
            return new SearchDataPointJson(keywordSearch, excludeIds, dataTypes, limit, startsWith, page, settable);
        }
    }

    @Override
    public String toString() {
        return "SearchDataPointJson{" +
                "keywordSearch='" + keywordSearch + '\'' +
                ", excludeIds=" + excludeIds +
                ", dataTypes=" + dataTypes +
                ", limit=" + limit +
                ", startsWith=" + startsWith +
                ", page=" + page +
                ", settable=" + settable +
                '}';
    }
}
