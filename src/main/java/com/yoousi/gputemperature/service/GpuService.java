package com.yoousi.gputemperature.service;

import com.yoousi.gputemperature.entity.GpuResponse;

import java.util.List;


public interface  GpuService {
    // TODO 这里需要改成根据ip获取对应服务器的温度
    // 获取GPU温度
    GpuResponse getTemperatureByIp();

}
