package com.yoousi.gputemperature.controller;


import com.yoousi.gputemperature.entity.GpuResponse;
import com.yoousi.gputemperature.service.GpuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;


@RestController()
public class GpuController {
    @Resource
    private GpuService gpuService;



    @GetMapping("/gpu")
    @CrossOrigin(origins = "*")
    public GpuResponse getTemperature() {
        return gpuService.getTemperatureByIp();
    }
    @GetMapping("/gpuMax")
    @CrossOrigin(origins = "*")
    public int readTodaysMaxTemperatureFromLog() {
        try {
            return gpuService.readTodaysMaxTemperatureFromLog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
