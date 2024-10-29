package org.scada_lts.web.mvc.api.json;

public class JsonUserComment {
    private int userId;
    private int commentType;
    private long typeKey;
    private long ts;
    private String comment;
    private String username;
    private String prettyTime;

    public JsonUserComment() {}

    public JsonUserComment(int userId, int commentType, long typeKey, long ts, String comment,
                           String username, String prettyTime) {
        this.userId = userId;
        this.commentType = commentType;
        this.typeKey = typeKey;
        this.ts = ts;
        this.comment = comment;
        this.username = username;
        this.prettyTime = prettyTime;
    }

    public int getUserId() {
        return userId;
    }

    public int getCommentType() {
        return commentType;
    }

    public long getTypeKey() {
        return typeKey;
    }

    public long getTs() {
        return ts;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public String getPrettyTime() {
        return prettyTime;
    }
}
