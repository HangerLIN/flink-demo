package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 表示一个交易事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    // 交易ID
    private String transactionId;
    
    // 用户ID
    private String userId;
    
    // 交易金额
    private BigDecimal amount;
    
    // 交易类型 (DEPOSIT, WITHDRAWAL, TRANSFER)
    private TransactionType type;
    
    // 交易渠道 (APP, WEB, ATM, POS)
    private String channel;
    
    // 交易IP地址
    private String ipAddress;
    
    // 交易地理位置
    private String location;
    
    // 设备ID
    private String deviceId;
    
    // 交易时间
    private LocalDateTime timestamp;
    
    // 风险评分 (系统预估的风险分数 0-100)
    private int riskScore;
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        PAYMENT
    }
} 