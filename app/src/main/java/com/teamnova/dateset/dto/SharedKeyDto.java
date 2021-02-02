package com.teamnova.dateset.dto;

import java.io.Serializable;

public class SharedKeyDto implements Serializable {
    private String sharedKeyId;
    private String user1Id;
    private String user2Id;
    private String user1ProfileUrl;
    private String user2ProfileUrl;
    private String user1NickName;
    private String user2NickName;
    private String backgroundUrl;
    private String dateOfFirstDay;

    public SharedKeyDto(){}

    public SharedKeyDto(String sharedKeyId, String user1Id, String user2Id) {
        this.sharedKeyId = sharedKeyId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public String getSharedKeyId() {
        return sharedKeyId;
    }

    public void setSharedKeyId(String sharedKeyId) {
        sharedKeyId = sharedKeyId;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getUser1ProfileUrl() {
        return user1ProfileUrl;
    }

    public void setUser1ProfileUrl(String user1ProfileUrl) {
        this.user1ProfileUrl = user1ProfileUrl;
    }

    public String getUser2ProfileUrl() {
        return user2ProfileUrl;
    }

    public void setUser2ProfileUrl(String user2ProfileUrl) {
        this.user2ProfileUrl = user2ProfileUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public String getDateOfFirstDay() {
        return dateOfFirstDay;
    }

    public void setDateOfFirstDay(String dateOfFirstDay) {
        this.dateOfFirstDay = dateOfFirstDay;
    }

    public String getUser1NickName() {
        return user1NickName;
    }

    public void setUser1NickName(String user1NickName) {
        this.user1NickName = user1NickName;
    }

    public String getUser2NickName() {
        return user2NickName;
    }

    public void setUser2NickName(String user2NickName) {
        this.user2NickName = user2NickName;
    }
}
