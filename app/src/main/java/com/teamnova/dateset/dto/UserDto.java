package com.teamnova.dateset.dto;

import java.io.Serializable;

/**
 1. 클래스명: LoginDto
 2. 역할: 로그인 정보를 담고있는 클래스
 */
public class UserDto implements Serializable {
    String uId;
    String fbKey;
    String profileUrl;
    String id;
    String pw;
    String name;
    String nickName;
    String phoneNum;
    String sharedKey;

    public UserDto(){}

    public UserDto(String uId, String fbKey, String id, String pw) {
        this.uId = uId;
        this.id = id;
        this.pw = pw;
        this.fbKey = fbKey;
    }

    public UserDto(String id, String pw, String name, String nickName, String phoneNum) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.nickName = nickName;
        this.phoneNum = phoneNum;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getFbKey() {
        return fbKey;
    }

    public void setFbKey(String fbKey) {
        this.fbKey = fbKey;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
