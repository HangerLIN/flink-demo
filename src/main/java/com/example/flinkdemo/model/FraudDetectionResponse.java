package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 欺诈检测API响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudDetectionResponse {
    // 操作是否成功
    private boolean success;
    
    // 操作消息
    private String message;
    
    // 检测到的欺诈警报
    private List<FraudAlert> alerts;
    
    // 检测到的流量异常
    private List<TrafficAnomaly> anomalies;
    
    // 执行的任务ID
    private String jobId;
    
    // 规则执行统计
    private RuleExecutionStats executionStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleExecutionStats {
        // 执行的规则总数
        private int totalRulesExecuted;
        
        // 触发的规则数量
        private int rulesTriggered;
        
        // 处理的事件数量
        private long eventsProcessed;
        
        // 处理时间 (毫秒)
        private long processingTimeMs;
    }
} 