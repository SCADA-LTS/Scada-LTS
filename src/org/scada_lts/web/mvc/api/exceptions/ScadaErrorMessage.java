package org.scada_lts.web.mvc.api.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ScadaErrorMessage implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String type;
    private String title;
    private Map<String, String> detail;
    private String instance;
    private HttpStatus status;

    private ScadaErrorMessage(String type, String title, HttpStatus status, Map<String, String> detail,
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

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
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
        private final HttpStatus status;
        private final Map<String, String> detail = new HashMap<>();
        private String type;
        private String title;
        private String instance;

        public Builder(HttpStatus status) {
            this.status = status;
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

        public Builder detail(Map<String, String> detail) {
            this.detail.putAll(detail);
            return this;
        }

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
