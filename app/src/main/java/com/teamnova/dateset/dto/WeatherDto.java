package com.teamnova.dateset.dto;

public class WeatherDto {
    private Long dateTime;
    private String dateWithDay;
    private String temp;
    private String feelsLikeTemp;
    private String humidity;
    private String windSpeed;
    private String weatherMain;
    private String pop;
    private String minTemp;
    private String maxTemp;
    private String feelsLikeTempDay;
    private String weatherDescription;

    public WeatherDto(Long dateTime, String dateWithDay, String temp, String feelsLikeTemp, String humidity, String windSpeed, String weatherMain, String pop, String minTemp, String maxTemp, String feelsLikeTempDay, String weatherDescription) {
        this.dateTime = dateTime;
        this.dateWithDay = dateWithDay;
        this.temp = temp;
        this.feelsLikeTemp = feelsLikeTemp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherMain = weatherMain;
        this.pop = pop;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.feelsLikeTempDay = feelsLikeTempDay;
        this.weatherDescription = weatherDescription;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateWithDay() {
        return dateWithDay;
    }

    public void setDateWithDay(String dateWithDay) {
        this.dateWithDay = dateWithDay;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public void setFeelsLikeTemp(String feelsLikeTemp) {
        this.feelsLikeTemp = feelsLikeTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getFeelsLikeTempDay() {
        return feelsLikeTempDay;
    }

    public void setFeelsLikeTempDay(String feelsLikeTempDay) {
        this.feelsLikeTempDay = feelsLikeTempDay;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}
