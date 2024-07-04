package com.yoousi.gputemperature.entity;

public class GpuInfo {
    private String name;
    private String temperature;
    private int maxTemperature;

    // 构造方法
    public GpuInfo(String name, String temperature, int maxTemperature) {
        this.name = name;
        this.temperature = temperature;
        this.maxTemperature = maxTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
