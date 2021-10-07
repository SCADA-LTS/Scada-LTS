package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.Common;

public class JsonEventComment {
    private int userId;
    private int commentType;
    private long typeKey;
    private long ts;
    private String commentText;
    public JsonEventComment() {}

    public JsonEventComment(int userId, int commentType, long typeKey, long ts, String commentText) {
        this.userId = userId;
        this.commentType = commentType;
        this.typeKey = typeKey;
        this.ts = ts;
        this.commentText = commentText;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public long getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(long typeKey) {
        this.typeKey = typeKey;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
