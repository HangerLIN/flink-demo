package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 传感器读数模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {
    
    /** 传感器ID */
    private String id;
    
    /** 温度值 */
    private Double temperature;
    
    /** 时间戳 */
    private Long timestamp;
    
    public SensorReading(String id, Double temperature) {
        this.id = id;
        this.temperature = temperature;
        this.timestamp = Instant.now().toEpochMilli();
    }
} 