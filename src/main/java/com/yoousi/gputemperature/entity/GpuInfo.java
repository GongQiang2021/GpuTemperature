package com.yoousi.gputemperature.entity;

public class GpuInfo {
    private String name;
    private String temperature;

    // 构造方法
    public GpuInfo(String name, String temperature) {
        this.name = name;
        this.temperature = temperature;
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
