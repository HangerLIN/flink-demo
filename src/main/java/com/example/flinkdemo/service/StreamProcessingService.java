package com.example.flinkdemo.service;

import com.example.flinkdemo.model.TrafficData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 流处理服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamProcessingService {

    private final StreamExecutionEnvironment streamEnv;

    /**
     * 演示基于处理时间的滚动窗口（Tumbling Window）
     * 每5秒统计一次各个传感器的车流量
     */
    public List<Tuple2<Integer, Integer>> processingTimeWindowDemo() throws Exception {
        // 创建新的流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        
        // 输出结果列表
        List<Tuple2<Integer, Integer>> results = new ArrayList<>();

        // 创建一个模拟的交通数据源，但限制为有界数据源
        DataStream<TrafficData> source = env.addSource(new BoundedTrafficDataSource(200));

        // 按传感器ID分组，使用5秒滚动窗口，计算窗口内的车流量总和
        DataStream<Tuple2<Integer, Integer>> windowedStream = source
                .map(new MapFunction<TrafficData, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> map(TrafficData data) {
                        return new Tuple2<>(data.getSensorId(), data.getCarCount());
                    }
                })
                .keyBy(0) // 按传感器ID分组
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5))) // 5秒滚动窗口
                .reduce(new ReduceFunction<Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> t1, Tuple2<Integer, Integer> t2) {
                        return new Tuple2<>(t1.f0, t1.f1 + t2.f1);
                    }
                });

        // 收集结果到列表中
        windowedStream.executeAndCollect().forEachRemaining(results::add);

        return results;
    }

    /**
     * 演示基于事件时间的滚动窗口（Tumbling Window）以及Watermark的使用
     * 每5秒统计一次各个传感器的车流量，允许2秒的延迟
     */
    public List<Tuple2<Integer, Integer>> eventTimeWindowWithWatermarkDemo() throws Exception {
        // 创建新的流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        
        // 输出结果列表
        List<Tuple2<Integer, Integer>> results = new ArrayList<>();

        // 创建一个有界的模拟交通数据源，生成200条数据
        DataStream<TrafficData> source = env.addSource(new BoundedTrafficDataSource(200));

        // 分配时间戳并设置Watermark，允许2秒的延迟
        DataStream<TrafficData> timestampedStream = source
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<TrafficData>(Time.seconds(2)) {
                            @Override
                            public long extractTimestamp(TrafficData data) {
                                return data.getEventTime().toEpochMilli();
                            }
                        });

        // 按传感器ID分组，使用5秒事件时间滚动窗口，计算窗口内的车流量总和
        DataStream<Tuple2<Integer, Integer>> windowedStream = timestampedStream
                .map(new MapFunction<TrafficData, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> map(TrafficData data) {
                        return new Tuple2<>(data.getSensorId(), data.getCarCount());
                    }
                })
                .keyBy(0) // 按传感器ID分组
                .window(TumblingEventTimeWindows.of(Time.seconds(5))) // 5秒滚动窗口，基于事件时间
                .reduce(new ReduceFunction<Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> t1, Tuple2<Integer, Integer> t2) {
                        return new Tuple2<>(t1.f0, t1.f1 + t2.f1);
                    }
                });

        // 收集结果到列表中
        windowedStream.executeAndCollect().forEachRemaining(results::add);

        return results;
    }

    /**
     * 模拟交通数据源
     */
    public static class TrafficDataSource implements SourceFunction<TrafficData> {
        private volatile boolean isRunning = true;
        private final Random random = new Random();

        @Override
        public void run(SourceContext<TrafficData> ctx) throws Exception {
            while (isRunning) {
                // 生成随机传感器ID (1-5)
                int sensorId = random.nextInt(5) + 1;
                // 生成随机车流量 (1-10)
                int carCount = random.nextInt(10) + 1;
                // 生成随机速度 (30-120)
                double speed = 30 + random.nextDouble() * 90;

                ctx.collect(new TrafficData(sensorId, carCount, speed));

                // 休眠一段时间
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    /**
     * 有界交通数据源
     */
    public static class BoundedTrafficDataSource implements SourceFunction<TrafficData> {
        private volatile boolean isRunning = true;
        private final Random random = new Random();
        private final int maxCount;

        public BoundedTrafficDataSource(int maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public void run(SourceContext<TrafficData> ctx) throws Exception {
            int count = 0;
            while (isRunning && count < maxCount) {
                // 生成随机传感器ID (1-5)
                int sensorId = random.nextInt(5) + 1;
                // 生成随机车流量 (1-10)
                int carCount = random.nextInt(10) + 1;
                // 生成随机速度 (30-120)
                double speed = 30 + random.nextDouble() * 90;

                ctx.collect(new TrafficData(sensorId, carCount, speed));

                // 休眠一段时间
                TimeUnit.MILLISECONDS.sleep(100);
                count++;
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }
} 