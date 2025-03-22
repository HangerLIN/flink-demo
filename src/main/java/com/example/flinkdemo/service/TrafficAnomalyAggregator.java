package com.example.flinkdemo.service;

import com.example.flinkdemo.model.TrafficAnomaly;
import com.example.flinkdemo.model.TrafficEvent;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 流量异常检测聚合器
 * 聚合事件流并检测流量异常
 */
public class TrafficAnomalyAggregator implements AggregateFunction<TrafficEvent, Tuple2<String, Double>, TrafficAnomaly> {

    // 基准请求率 (每分钟)
    private static final double DEFAULT_BASELINE_RATE = 100.0;
    
    @Override
    public Tuple2<String, Double> createAccumulator() {
        // 初始化累加器 (资源, 计数)
        return new Tuple2<>("", 0.0);
    }

    @Override
    public Tuple2<String, Double> add(TrafficEvent event, Tuple2<String, Double> accumulator) {
        // 更新资源
        if (accumulator.f0.isEmpty()) {
            accumulator.f0 = event.getTargetResource();
        }
        
        // 累加请求数
        accumulator.f1 += 1.0;
        
        return accumulator;
    }

    @Override
    public TrafficAnomaly getResult(Tuple2<String, Double> accumulator) {
        String targetResource = accumulator.f0;
        double detectedRate = accumulator.f1 * 6; // 转换为每分钟的请求率 (假设窗口是10秒)
        
        // 计算偏差百分比
        double deviationPercentage = ((detectedRate - DEFAULT_BASELINE_RATE) / DEFAULT_BASELINE_RATE) * 100;
        
        // 创建异常对象
        TrafficAnomaly anomaly = new TrafficAnomaly();
        anomaly.setAnomalyId(UUID.randomUUID().toString());
        anomaly.setType(TrafficAnomaly.AnomalyType.UNUSUAL_TRAFFIC_SPIKE);
        anomaly.setTargetResource(targetResource);
        anomaly.setDetectedRate(detectedRate);
        anomaly.setBaselineRate(DEFAULT_BASELINE_RATE);
        anomaly.setDeviationPercentage(deviationPercentage);
        anomaly.setStartTime(LocalDateTime.now().minusMinutes(1));
        anomaly.setOngoing(true);
        
        // 根据偏差设置严重程度
        if (deviationPercentage > 500) {
            anomaly.setSeverity(TrafficAnomaly.SeverityLevel.EMERGENCY);
            anomaly.setImpactDescription("流量异常飙升，可能是DDoS攻击或严重系统故障");
        } else if (deviationPercentage > 300) {
            anomaly.setSeverity(TrafficAnomaly.SeverityLevel.CRITICAL);
            anomaly.setImpactDescription("流量显著高于正常水平，系统可能面临压力");
        } else if (deviationPercentage > 200) {
            anomaly.setSeverity(TrafficAnomaly.SeverityLevel.WARNING);
            anomaly.setImpactDescription("流量高于正常水平，需要监控");
        } else {
            anomaly.setSeverity(TrafficAnomaly.SeverityLevel.NOTICE);
            anomaly.setImpactDescription("流量轻微波动，在可接受范围内");
        }
        
        return anomaly;
    }

    @Override
    public Tuple2<String, Double> merge(Tuple2<String, Double> a, Tuple2<String, Double> b) {
        // 合并两个累加器
        return new Tuple2<>(a.f0, a.f1 + b.f1);
    }
} 