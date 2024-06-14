package com.yoousi.gputemperature.entity;
import java.util.List;

// 定义接口返回的封装类
public class GpuResponse {
    private List<GpuInfo> gpuInfo;
    private String status;

    // 构造方法
    public GpuResponse(List<GpuInfo> gpuInfo, String status) {
        this.gpuInfo = gpuInfo;
        this.status = status;
    }
    public GpuResponse() {

    }

    // Getter和Setter方法
    public List<GpuInfo> getGpuInfo() {
        return gpuInfo;
    }

    public void setGpuInfo(List<GpuInfo> gpuInfo) {
        this.gpuInfo = gpuInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}