package com.teamnova.dateset.dto;

import java.io.Serializable;

public class CommentDto implements Serializable {
    String commentId;
    String id;
    String priorCommentId;
    String nickName;
    String date;
    String commentContents;

    public CommentDto(){}

    public CommentDto( String id,  String priorCommentId,  String nickName, String date, String commentContents) {
        this.id = id;
        this.priorCommentId = priorCommentId;
        this.nickName = nickName;
        this.date = date;
        this.commentContents = commentContents;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPriorCommentId() {
        return priorCommentId;
    }

    public void setPriorCommentId(String priorCommentId) {
        this.priorCommentId = priorCommentId;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentContents() {
        return commentContents;
    }

    public void setCommentContents(String commentContents) {
        this.commentContents = commentContents;
    }
}
