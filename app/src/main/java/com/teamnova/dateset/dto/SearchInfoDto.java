package com.teamnova.dateset.dto;

public class SearchInfoDto {
    private String title;
    private String link;
    private String address;
    private String mapX;
    private String mapY;

    public SearchInfoDto(String title, String link, String address, String mapX, String mapY) {
        this.title = title;
        this.link = link;
        this.address = address;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getAddress() {
        return address;
    }

    public String getMapX() {
        return mapX;
    }

    public String getMapY() {
        return mapY;
    }
}
