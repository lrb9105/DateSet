package com.teamnova.dateset.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class PostDto implements Serializable {
    private String id;
    private String key;
    private String writer;
    private String textContent;
    private String place;
    private String weather;
    private String date;
    private int commentCount;
    private String imgUrl;

    public PostDto(){}

    public PostDto(String id, String writer, String textContent, String place, String weather, String date) {
        this.id = id;
        this.writer = writer;
        this.textContent = textContent;
        this.place = place;
        this.weather = weather;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
