package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 业务规则模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRule {
    // 规则ID
    private String ruleId;
    
    // 规则名称
    private String ruleName;
    
    // 规则描述
    private String description;
    
    // 规则状态
    private RuleStatus status;
    
    // 规则优先级 (值越小优先级越高)
    private int priority;
    
    // 规则类型
    private RuleType type;
    
    // 规则条件 (CEP模式匹配的条件参数)
    private Map<String, Object> conditions;
    
    // 时间窗口大小(秒)
    private int timeWindowSeconds;
    
    // 要检测的事件序列
    private List<String> eventSequence;
    
    // 触发操作
    private List<ActionType> actions;
    
    /**
     * 规则状态
     */
    public enum RuleStatus {
        ACTIVE,    // 激活
        INACTIVE,  // 未激活
        TESTING,   // 测试中
        DEPRECATED // 已弃用
    }
    
    /**
     * 规则类型
     */
    public enum RuleType {
        FRAUD_DETECTION,   // 欺诈检测
        TRAFFIC_MONITORING, // 流量监控
        SECURITY_ALERT,    // 安全告警
        BUSINESS_ANALYTICS, // 业务分析
        COMPLIANCE        // 合规监控
    }
    
    /**
     * 触发动作类型
     */
    public enum ActionType {
        GENERATE_ALERT,    // 生成告警
        BLOCK_TRANSACTION, // 阻止交易
        NOTIFY_ADMIN,      // 通知管理员
        LOG_EVENT,         // 记录事件
        TRIGGER_WORKFLOW,  // 触发工作流
        UPDATE_RISK_SCORE  // 更新风险分数
    }
} 