package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficAnalysisResult {
    private String roadSegment;     // 道路段ID
    private long windowStart;       // 窗口开始时间
    private long windowEnd;         // 窗口结束时间
    private double avgSpeed;        // 平均车速
    private int vehicleCount;       // 车辆数量
    private double congestionIndex; // 拥堵指数(0-1范围，值越大越拥堵)
    private String congestionLevel; // 拥堵级别（畅通、轻度拥堵、中度拥堵、严重拥堵）
    private int lateEventCount;     // 延迟事件计数（用于显示水印效果）
    
    public TrafficAnalysisResult(String roadSegment, long windowStart, long windowEnd,
                                double avgSpeed, int vehicleCount) {
        this.roadSegment = roadSegment;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.avgSpeed = avgSpeed;
        this.vehicleCount = vehicleCount;
        
        // 简单拥堵指数计算：速度与车流量的关系
        this.congestionIndex = calculateCongestionIndex(avgSpeed, vehicleCount);
        this.congestionLevel = getCongestionLevel(this.congestionIndex);
        this.lateEventCount = 0; // 默认值
    }
    
    private double calculateCongestionIndex(double avgSpeed, int vehicleCount) {
        // 简单算法：速度越低、车流量越大，拥堵指数越高
        double speedFactor = Math.max(0, Math.min(1, (120 - avgSpeed) / 120));
        double volumeFactor = Math.max(0, Math.min(1, vehicleCount / 100.0));
        return (speedFactor * 0.7 + volumeFactor * 0.3);
    }
    
    private String getCongestionLevel(double index) {
        if (index < 0.3) return "畅通";
        if (index < 0.5) return "轻度拥堵";
        if (index < 0.7) return "中度拥堵";
        return "严重拥堵";
    }
} 