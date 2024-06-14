package com.yoousi.gputemperature.service.impl;

import com.yoousi.gputemperature.entity.GpuInfo;
import com.yoousi.gputemperature.entity.GpuResponse;
import com.yoousi.gputemperature.service.GpuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Service
public class GpuServiceImpl implements GpuService {
    @Value("${temperature.alex:50}")
    private  Integer AlexTemperature;

    @Override
    public GpuResponse getTemperatureByIp( ) {
        GpuResponse gpuResponse =new GpuResponse();
        List<GpuInfo> temperatures = new ArrayList<>();
        try {
            String temperatureError = "所有GPU温度正常";
            gpuResponse.setStatus(temperatureError);
            // 构造命令，请求GPU个数
            Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=count --format=csv,noheader,nounits");
            // 用于读取命令执行后的输出
            BufferedReader countReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String countLine = countReader.readLine();
            // 得到GPU个数
            int gpuCount = Integer.parseInt(countLine.trim());
            // 循环获取每个GPU温度放入列表
            for (int i = 0; i < gpuCount; i++) {
                // 构建命令获取GPU温度
                Process tempProcess = Runtime.getRuntime().exec("nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader,nounits --id=" + i);
                // 用于读取命令执行后的输出
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(tempProcess.getInputStream()));
                String tempLine = tempReader.readLine();
                // 读取的温度不为空且多个自然数组成
                if (tempLine != null && tempLine.matches("\\d+")) {
                    // 将温度去除空格转为数字放入列表中
                    if (Integer.parseInt(tempLine.trim()) > AlexTemperature) {
                        temperatureError = "警告：至少有一个GPU的温度超过了"+AlexTemperature+"C！";
                        gpuResponse.setStatus(temperatureError);
                    }
                    temperatures.add(new GpuInfo("GPU " + (i + 1),tempLine.trim() ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.print("检测GPU温度时发生错误：" + e.getMessage());
        }
        gpuResponse.setGpuInfo(temperatures);
        return gpuResponse;
    }

}
