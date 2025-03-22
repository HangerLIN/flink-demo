package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficEvent {
    private String vehicleId;       // 车辆ID
    private String sensorId;        // 传感器ID
    private long timestamp;         // 事件时间戳（毫秒）
    private double speed;           // 车速（km/h）
    private String location;        // 位置信息
    private String roadSegment;     // 道路段ID
    private int laneNumber;         // 车道号
    private String vehicleType;     // 车辆类型（轿车、卡车等）
    
    // 添加目标资源字段
    private String targetResource;
    
    // 添加资源获取方法
    public String getTargetResource() {
        return targetResource;
    }
    
    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
    }
    
    // 用于演示的构造函数
    public TrafficEvent(String sensorId, long timestamp, double speed, String roadSegment) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.speed = speed;
        this.roadSegment = roadSegment;
        this.vehicleId = "V-" + Math.round(Math.random() * 10000);
        this.location = "LOC-" + roadSegment;
        this.laneNumber = (int)(Math.random() * 4) + 1;
        this.vehicleType = Math.random() > 0.2 ? "轿车" : "卡车";
    }
} 