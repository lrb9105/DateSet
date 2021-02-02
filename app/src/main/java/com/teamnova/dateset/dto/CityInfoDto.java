package com.teamnova.dateset.dto;

public class CityInfoDto {
    private String kr_city_name;
    private String en_city_name;
    private double lat;
    private double lng;

    public CityInfoDto(String kr_city_name, String en_city_name, double lat, double lng) {
        this.kr_city_name = kr_city_name;
        this.en_city_name = en_city_name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getKr_city_name() {
        return kr_city_name;
    }

    public void setKr_city_name(String kr_city_name) {
        this.kr_city_name = kr_city_name;
    }

    public String getEn_city_name() {
        return en_city_name;
    }

    public void setEn_city_name(String en_city_name) {
        this.en_city_name = en_city_name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
