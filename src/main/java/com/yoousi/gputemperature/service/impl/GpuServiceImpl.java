package com.yoousi.gputemperature.service.impl;

import com.yoousi.gputemperature.entity.GpuInfo;
import com.yoousi.gputemperature.entity.GpuResponse;
import com.yoousi.gputemperature.entity.MaxTemperatureFromLog;
import com.yoousi.gputemperature.service.GpuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Service
public class GpuServiceImpl implements GpuService {
    @Value("${temperature.alex:72}")
    private  Integer AlexTemperature;
    @Value("${temperature.log.address:D:\\GPUTemperatureLog.txt}")
    private  String address;
    @Override
    public GpuResponse getTemperatureByIp( ) {
        GpuResponse gpuResponse =new GpuResponse();
        List<GpuInfo> temperatures = new ArrayList<>();
        MaxTemperatureFromLog maxFromLog ;
        try {
            maxFromLog = readTodaysMaxTemperatureFromLog();    // 从日志文件读取最高温度
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int currentMaxTemperature = maxFromLog.getMaxTemperature();          // 初始化当前最高温度为日志中的最高温度
        try {
            String temperatureError = "所有GPU温度正常";
            gpuResponse.setStatus(temperatureError);
            // 构造命令，请求GPU个数
            Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=count " +
                    "--format=csv,noheader,nounits");
            // 用于读取命令执行后的输出
            BufferedReader countReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String countLine = countReader.readLine();
            // 得到GPU个数
            int gpuCount = Integer.parseInt(countLine.trim());
            // 循环获取每个GPU温度放入列表
            for (int i = 0; i < gpuCount; i++) {
                // 构建命令获取GPU温度
                Process tempProcess = Runtime.getRuntime().exec("nvidia-smi --query-gpu=temperature.gpu " +
                        "--format=csv,noheader,nounits --id=" + i);
                // 用于读取命令执行后的输出
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(tempProcess.getInputStream()));
                String tempLine = tempReader.readLine();
                // 读取的温度不为空且多个自然数组成
                if (tempLine != null && tempLine.matches("\\d+")) {
                    // 将温度去除空格转为数字放入列表中

                    // 检查当前温度是否高于已知的最高温度
                    if (Integer.parseInt(tempLine.trim()) > currentMaxTemperature) {
                        currentMaxTemperature = Integer.parseInt(tempLine.trim()); // 更新当前最高温度
                    }
                    if (Integer.parseInt(tempLine.trim()) > AlexTemperature) {
                        temperatureError = "警告：至少有一个GPU的温度超过了"+AlexTemperature+"C！";
                        gpuResponse.setStatus(temperatureError);
                    }
                    temperatures.add(new GpuInfo("GPU " + (i + 1),tempLine.trim(),maxFromLog.getMaxTemperature(),maxFromLog.getMaxTemperatureTime()));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.print("检测GPU温度时发生错误：" + e.getMessage());
        }
        gpuResponse.setGpuInfo(temperatures);

        return gpuResponse;
    }
    @Scheduled(fixedRate = 3000)
    public void realTimeTemperature(){
        MaxTemperatureFromLog maxFromLog ;
        try {
            maxFromLog = readTodaysMaxTemperatureFromLog();    // 从日志文件读取最高温度
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int currentMaxTemperature = maxFromLog.getMaxTemperature();          // 初始化当前最高温度为日志中的最高温度
        try {
            // 构造命令，请求GPU个数
            Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=count " +
                    "--format=csv,noheader,nounits");
            // 用于读取命令执行后的输出
            BufferedReader countReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String countLine = countReader.readLine();
            // 得到GPU个数
            int gpuCount = Integer.parseInt(countLine.trim());
            // 循环获取每个GPU温度放入列表
            for (int i = 0; i < gpuCount; i++) {
                // 构建命令获取GPU温度
                Process tempProcess = Runtime.getRuntime().exec("nvidia-smi --query-gpu=temperature.gpu " +
                        "--format=csv,noheader,nounits --id=" + i);
                // 用于读取命令执行后的输出
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(tempProcess.getInputStream()));
                String tempLine = tempReader.readLine();
                // 读取的温度不为空且多个自然数组成
                if (tempLine != null && tempLine.matches("\\d+")) {
                    // 将温度去除空格转为数字放入列表中

                    // 检查当前温度是否高于已知的最高温度
                    if (Integer.parseInt(tempLine.trim()) > currentMaxTemperature) {
                        currentMaxTemperature = Integer.parseInt(tempLine.trim()); // 更新当前最高温度
                        updateMaxTemperatureInLog(currentMaxTemperature); // 更新日志文件中的最高温度
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.print("检测GPU温度时发生错误：" + e.getMessage());
        }
    }
    // 从日志文件读取最高温度
    public MaxTemperatureFromLog readTodaysMaxTemperatureFromLog() throws IOException {
        MaxTemperatureFromLog maxTemperatureFromLog = new MaxTemperatureFromLog();
        File logFile = new File(address);
        int todaysMaxTemperature = 0;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Pattern pattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}): 最高GPU温度为 (\\d+)℃");
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    LocalDate logDate = LocalDate.parse(matcher.group(1), formatter);
                    String logTime = matcher.group(2);
                    maxTemperatureFromLog.setMaxTemperatureTime(logTime);
                    if (logDate.equals(today)) {
                        int temperature = Integer.parseInt(matcher.group(3));
                        todaysMaxTemperature = Math.max(todaysMaxTemperature, temperature);
                        maxTemperatureFromLog.setMaxTemperature(todaysMaxTemperature);

                    }
                }
            }
        }
        return maxTemperatureFromLog;
    }

    // 更新日志文件中的最高温度
    private void updateMaxTemperatureInLog(int maxTemperature) throws IOException {
        File logFile = new File(address);
        List<String> updatedLines = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern pattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}) \\d{2}:\\d{2}:\\d{2}:" +
                        " 最高GPU温度为 (\\d+)℃");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    LocalDate logDate = LocalDate.parse(matcher.group(1), formatter);
                    if (!logDate.equals(today)) {
                        // 保存非今天的记录
                        updatedLines.add(line);
                    }
                }
            }
        }

        // 添加新的今天最高温度记录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String logMessage = sdf.format(date) + ": 最高GPU温度为 " + maxTemperature + "℃";
        updatedLines.add(logMessage);

        // 写入更新后的行到文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

}
