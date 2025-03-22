package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 欺诈检测警报
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {
    // 告警ID
    private String alertId;
    
    // 用户ID
    private String userId;
    
    // 检测到的欺诈模式类型
    private FraudPatternType patternType;
    
    // 风险等级
    private RiskLevel riskLevel;
    
    // 欺诈模式描述
    private String description;
    
    // 触发警报的交易事件序列
    private List<TransactionEvent> relatedTransactions;
    
    // 警报生成时间
    private LocalDateTime alertTime;
    
    // 状态
    private AlertStatus status;
    
    // 建议采取的行动
    private String recommendedAction;
    
    /**
     * 欺诈模式类型
     */
    public enum FraudPatternType {
        RAPID_SUCCESSION,        // 短时间内多次交易
        LARGE_AMOUNT,            // 大额交易
        UNUSUAL_LOCATION,        // 非常规地理位置
        VELOCITY_CHANGE,         // 交易地点快速变化
        MULTIPLE_FAILURES,       // 多次失败尝试
        ABNORMAL_BEHAVIOR,       // 与历史行为明显不符
        DEVICE_CHANGE,           // 设备异常变化
        HIGH_RISK_MERCHANT,      // 高风险商户交易
        ACCOUNT_TAKEOVER         // 账户接管行为
    }
    
    /**
     * 风险等级
     */
    public enum RiskLevel {
        LOW,      // 低风险
        MEDIUM,   // 中风险
        HIGH,     // 高风险
        CRITICAL  // 严重风险
    }
    
    /**
     * 警报状态
     */
    public enum AlertStatus {
        NEW,         // 新警报
        INVESTIGATING, // 调查中
        CONFIRMED,   // 确认为欺诈
        DISMISSED,   // 排除（确认为正常行为）
        MITIGATED    // 已采取措施
    }
} 