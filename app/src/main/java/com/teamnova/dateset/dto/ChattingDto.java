package com.teamnova.dateset.dto;

import java.util.HashMap;
import java.util.Map;

public class ChattingDto {
    private String fromId;
    private String toId;
    private String fromNickName;
    private String contents;
    private String date;
    private String time;
    private Map<String,Object> readUsers;


    public ChattingDto(){}

    public ChattingDto(String fromId, String toId, String fromNickName, String contents, String date, String time) {
        this.fromId = fromId;
        this.toId = toId;
        this.fromNickName = fromNickName;
        this.contents = contents;
        this.date = date;
        this.time = time;
        this.readUsers = new HashMap<String,Object>();
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFromNickName() {
        return fromNickName;
    }

    public void setFromNickName(String fromNickName) {
        this.fromNickName = fromNickName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
