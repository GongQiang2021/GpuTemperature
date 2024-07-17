package com.yoousi.gputemperature.entity;

public class MaxTemperatureFromLog {
    private int maxTemperature;
    private String maxTemperatureTime;

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getMaxTemperatureTime() {
        return maxTemperatureTime;
    }

    public void setMaxTemperatureTime(String maxTemperatureTime) {
        this.maxTemperatureTime = maxTemperatureTime;
    }

    public MaxTemperatureFromLog() {
    }

    public MaxTemperatureFromLog(int maxTemperature, String maxTemperatureTime) {
        this.maxTemperature = maxTemperature;
        this.maxTemperatureTime = maxTemperatureTime;
    }
}
