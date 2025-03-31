package com.yoousi.gputemperature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GpuTemperatureApplication {
    //TODO 实际部署时，配置文件中temperature.address需要更改
    public static void main(String[] args) {
        SpringApplication.run(GpuTemperatureApplication.class, args);
    }

}
