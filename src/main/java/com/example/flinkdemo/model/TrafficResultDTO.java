package com.example.flinkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交通数据结果DTO，用于返回给前端的数据格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficResultDTO {
    
    /** 传感器ID */
    private Integer sensorId;
    
    /** 车辆数量 */
    private Integer carCount;
    
    /** 平均速度 */
    private Double avgSpeed;
} 