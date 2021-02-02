package com.teamnova.dateset.dto;

import java.io.Serializable;

public class TokenDto implements Serializable {
    private String user1Token;
    private String user2Token;

    public TokenDto(){}

    public String getUser1Token() {
        return user1Token;
    }

    public void setUser1Token(String user1Token) {
        this.user1Token = user1Token;
    }

    public String getUser2Token() {
        return user2Token;
    }

    public void setUser2Token(String user2Token) {
        this.user2Token = user2Token;
    }
}
