package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 流量异常事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficAnomaly {
    // 异常ID
    private String anomalyId;
    
    // 异常类型
    private AnomalyType type;
    
    // 源IP
    private String sourceIp;
    
    // 目标资源
    private String targetResource;
    
    // 检测到的请求率 (requests per minute)
    private double detectedRate;
    
    // 正常基线请求率
    private double baselineRate;
    
    // 偏差百分比
    private double deviationPercentage;
    
    // 异常开始时间
    private LocalDateTime startTime;
    
    // 异常结束时间 (如果仍在进行中为null)
    private LocalDateTime endTime;
    
    // 严重程度
    private SeverityLevel severity;
    
    // 是否仍在进行中
    private boolean ongoing;
    
    // 影响描述
    private String impactDescription;
    
    /**
     * 异常类型
     */
    public enum AnomalyType {
        DDoS_ATTACK,          // 分布式拒绝服务攻击
        BRUTE_FORCE,          // 暴力破解尝试
        UNUSUAL_TRAFFIC_SPIKE, // 异常流量峰值
        SCAN_ACTIVITY,        // 扫描活动
        API_ABUSE,            // API滥用
        BOT_TRAFFIC,          // 机器人流量
        DATA_EXFILTRATION     // 数据泄露
    }
    
    /**
     * 严重程度级别
     */
    public enum SeverityLevel {
        NOTICE,   // 提示
        WARNING,  // 警告
        CRITICAL, // 严重
        EMERGENCY // 紧急
    }
} 