package com.example.flinkdemo.config;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class FlinkConfig {

    /**
     * 创建批处理执行环境
     */
    @Bean
    public ExecutionEnvironment batchEnvironment() {
        return ExecutionEnvironment.getExecutionEnvironment();
    }

    /**
     * 创建流处理执行环境
     */
    @Bean
    @org.springframework.context.annotation.Primary
    public StreamExecutionEnvironment streamEnvironment() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置为流处理模式，以支持无界数据源
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        return env;
    }

    /**
     * 创建Table API执行环境
     */
    @Bean
    public TableEnvironment tableEnvironment() {
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        return TableEnvironment.create(settings);
    }

    /**
     * 创建Stream Table API执行环境
     */
    @Bean
    public StreamTableEnvironment streamTableEnvironment(StreamExecutionEnvironment streamEnv) {
        return StreamTableEnvironment.create(streamEnv);
    }

    /**
     * 创建本地Web UI的Flink环境，方便调试
     */
    @Bean
    public StreamExecutionEnvironment localWebEnvironment() {
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8081);
        return StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
    }
} 