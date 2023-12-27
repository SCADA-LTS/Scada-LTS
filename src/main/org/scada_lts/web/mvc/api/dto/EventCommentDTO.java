package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.vo.UserComment;

import java.util.List;

public class EventCommentDTO {
    private int userId;
    private String username;
    private int commentType;
    private int typeKey;
    private String ts;
    private String commentText;

    public EventCommentDTO() {
    }

    public EventCommentDTO(int userId, String username, int commentType, int typeKey, String ts, String commentText) {
        this.userId = userId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
