package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ScadaErrorMessage implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String type;
    private String title;
    private Map<String, String> detail;
    private String instance;
    private int status;

    private ScadaErrorMessage(String type, String title, HttpStatus status, Map<String, String> detail,
                              String instance) {
        this.type = type;
        this.title = title;
        this.status = status.value();
        this.detail = detail;
        this.instance = instance;
    }

    private ScadaErrorMessage(String type, String title, int status, Map<String, String> detail,
                              String instance) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, String> detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public static ScadaErrorMessage.Builder builder(HttpStatus httpStatus) {
        return new ScadaErrorMessage.Builder(httpStatus);
    }

    public static class Builder {
        private final int status;
        private final Map<String, String> detail = new HashMap<>();
        private String type;
        private String title;
        private String instance;

        public Builder(HttpStatus status) {
            this.status = status.value();
            this.title = status.getReasonPhrase();
            this.type = "/api/exceptions/" + ScadaApiException.class.getSimpleName();
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Merge the provided string detail entries into the builder's detail map, overwriting any existing entries with the same keys.
         *
         * @param detail map of detail entries to merge into the error message
         * @return this Builder instance
         */
        public Builder detail(Map<String, String> detail) {
            this.detail.putAll(detail);
            return this;
        }

        /**
         * Merges the given detail map into the builder's detail entries, converting each value to a string.
         *
         * @param detail map of detail keys to arbitrary objects; each value is converted to a string and merged into the builder's detail map
         * @return this builder instance
         */
        public Builder detailObj(Map<String, Object> detail) {
            this.detail.putAll(detail.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, b -> String.valueOf(b.getValue()))));
            return this;
        }

        /**
         * Adds or replaces the "message" entry in the builder's detail map.
         *
         * @param detail the message to store under the "message" key
         * @return this Builder instance for method chaining
         */
        public Builder detail(String detail) {
            this.detail.put("message", detail);
            return this;
        }

        public Builder detail(String key, String message) {
            this.detail.put(key, message);
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public ScadaErrorMessage build() {
            return new ScadaErrorMessage(type, title, status, detail, instance);
        }
    }
}