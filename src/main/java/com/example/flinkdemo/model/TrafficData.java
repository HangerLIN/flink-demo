package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 交通数据模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficData {
    
    /** 传感器ID */
    private Integer sensorId;
    
    /** 通过车辆数 */
    private Integer carCount;
    
    /** 事件时间 */
    private Instant eventTime;
    
    /** 速度 */
    private Double speed;
    
    public TrafficData(Integer sensorId, Integer carCount) {
        this.sensorId = sensorId;
        this.carCount = carCount;
        this.eventTime = Instant.now();
        this.speed = 0.0;
    }
    
    public TrafficData(Integer sensorId, Integer carCount, Double speed) {
        this.sensorId = sensorId;
        this.carCount = carCount;
        this.eventTime = Instant.now();
        this.speed = speed;
    }
} 