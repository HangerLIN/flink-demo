package com.example.flinkdemo.service;

import com.example.flinkdemo.model.TrafficEvent;
import com.example.flinkdemo.model.TrafficAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

/**
 * 高级水印服务 - 演示Flink水印相关高级特性
 */
@Slf4j
@Service
public class AdvancedWatermarkService {

    // 用于收集延迟事件的侧输出标签
    private static final OutputTag<TrafficEvent> LATE_EVENTS = 
            new OutputTag<TrafficEvent>("late-events"){};

    /**
     * 演示基本的水印生成与事件时间窗口结合
     */
    public List<TrafficAnalysisResult> demonstrateBasicWatermark() throws Exception {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4); // 使用4个并行度
            
            // 设置事件时间特性
            env.getConfig().setAutoWatermarkInterval(200);
            
            // 模拟流量数据源
            // 通过 withTimestampAssigner 提取每个事件的时间戳，确保 Flink 能够正确识别事件的发生时间。
            // 使用 forBoundedOutOfOrderness(Duration.ofSeconds(5)) 策略，生成水印以跟踪事件时间的进度。
            DataStream<TrafficEvent> trafficStream = env.addSource(new TrafficDataSource(1000, 0))
                .assignTimestampsAndWatermarks(
                    WatermarkStrategy
                        .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                );
            
            // 使用5分钟滑动窗口，每1分钟滑动一次
            SingleOutputStreamOperator<TrafficAnalysisResult> results = trafficStream
                .keyBy(TrafficEvent::getRoadSegment)
                .window(SlidingEventTimeWindows.of(Time.minutes(5), Time.minutes(1)))
                .aggregate(new TrafficAggregateFunction(), new TrafficWindowFunction());
            
            // 由于本地演示环境的限制，这里使用模拟数据返回结果
            // 在实际生产环境中，应该使用execute执行作业并收集结果
            log.info("构建了基本水印演示的处理管道，在实际环境中会执行计算");
            
            // 返回模拟数据
            return generateDemoResults("基本水印", 10);
        } catch (Exception e) {
            log.error("执行基本水印演示时出错", e);
            throw e;
        }
    }
    
    /**
     * 演示水印对齐功能 - 用于处理来自不同传感器的事件流
     */
    public List<TrafficAnalysisResult> demonstrateWatermarkAlignment() throws Exception {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4);
            
            // 设置事件时间特性
            env.getConfig().setAutoWatermarkInterval(200);
            
            // 创建多个传感器源，每个源有不同的延迟特性
            DataStream<TrafficEvent> fastSensorStream = env.addSource(new TrafficDataSource(1000, 0, "FAST"))
                .assignTimestampsAndWatermarks(
                    WatermarkStrategy
                        .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                        .withIdleness(Duration.ofSeconds(30)) // 空闲检测
                );
            
            DataStream<TrafficEvent> mediumSensorStream = env.addSource(new TrafficDataSource(1000, 2000, "MEDIUM"))
                .assignTimestampsAndWatermarks(
                    WatermarkStrategy
                        .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                        .withIdleness(Duration.ofSeconds(30)) // 空闲检测
                );
            
            DataStream<TrafficEvent> slowSensorStream = env.addSource(new TrafficDataSource(1000, 5000, "SLOW"))
                .assignTimestampsAndWatermarks(
                    WatermarkStrategy
                        .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                        .withIdleness(Duration.ofSeconds(30)) // 空闲检测
                );
            
            // 合并多个传感器流
            DataStream<TrafficEvent> unifiedStream = fastSensorStream
                .union(mediumSensorStream, slowSensorStream);
            
            // 启用水印对齐 - 确保所有分区的水印都接近一致
            WatermarkStrategy<TrafficEvent> alignedStrategy = WatermarkStrategy
                .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                .withIdleness(Duration.ofSeconds(30)) // 空闲源检测
                .withWatermarkAlignment("traffic-group", Duration.ofSeconds(10), Duration.ofSeconds(20));
                
            SingleOutputStreamOperator<TrafficEvent> alignedStream = unifiedStream
                .assignTimestampsAndWatermarks(alignedStrategy);
            
            // 使用带侧输出的窗口处理延迟事件
            SingleOutputStreamOperator<TrafficAnalysisResult> mainResults = alignedStream
                .keyBy(TrafficEvent::getRoadSegment)
                .window(TumblingEventTimeWindows.of(Time.minutes(3)))
                // 允许延迟5分钟的事件仍可计入结果
                .allowedLateness(Time.minutes(5))
                // 将最终仍然延迟的事件发送到侧输出
                .sideOutputLateData(LATE_EVENTS)
                .aggregate(new TrafficAggregateFunction(), new TrafficWindowFunction());
                
            // 处理延迟事件
            DataStream<TrafficEvent> lateEvents = mainResults.getSideOutput(LATE_EVENTS);
            
            // 实际生产环境下，应该使用execute执行作业并收集结果
            log.info("构建了水印对齐示例的处理管道，在实际环境中会执行计算");
            
            // 返回模拟数据
            return generateDemoResults("水印对齐", 15, true);
        } catch (Exception e) {
            log.error("执行水印对齐示例时出错", e);
            throw e;
        }
    }
    
    /**
     * 演示处理积压期间使用更大的检查点间隔
     */
    public List<TrafficAnalysisResult> demonstrateBacklogProcessing() throws Exception {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4);
            
            // 设置基本检查点配置 - 每5秒一次
            env.enableCheckpointing(5000);
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            
            // 设置积压处理期间的检查点间隔 - 每30秒一次
            // 注意：在实际运行中，这需要Flink 1.18+并且源实现了IsProcessingBacklog接口
            env.getCheckpointConfig().setCheckpointInterval(5000); // 普通检查点间隔
            // 实际环境中应该设置：env.getCheckpointConfig().set("execution.checkpointing.interval-during-backlog", "30s");
            
            // 模拟高负载数据源，带有积压报告功能
            DataStream<TrafficEvent> highLoadStream = env.addSource(new BacklogTrafficSource(20000, 0))
                .assignTimestampsAndWatermarks(
                    WatermarkStrategy
                        .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(20))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                );
            
            // 使用会话窗口进行处理 - 捕获连续的交通流量
            // 如果车辆之间的间隔超过2分钟，则认为是不同的交通会话
            SingleOutputStreamOperator<TrafficAnalysisResult> sessionResults = highLoadStream
                .keyBy(TrafficEvent::getRoadSegment)
                .window(EventTimeSessionWindows.withGap(Time.minutes(2)))
                .aggregate(new TrafficAggregateFunction(), new TrafficWindowFunction());
            
            // 实际生产环境下，应该使用execute执行作业并收集结果
            log.info("构建了积压处理示例的处理管道，在实际环境中会执行计算");
            
            // 返回模拟数据 - 积压处理情况下会有更多数据
            return generateDemoResults("会话窗口", 20, false, true);
        } catch (Exception e) {
            log.error("执行积压处理示例时出错", e);
            throw e;
        }
    }
    
    /**
     * 综合演示 - 将所有高级水印特性结合起来
     */
    public List<TrafficAnalysisResult> comprehensiveWatermarkDemo() throws Exception {
        try {
            // 创建执行环境
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(4);
            
            // 设置事件时间特性
            env.getConfig().setAutoWatermarkInterval(100);
            env.enableCheckpointing(5000);
            
            // 创建多个传感器源，模拟不同的延迟和吞吐量特性
            DataStream<TrafficEvent> cityStream = env.addSource(new TrafficDataSource(5000, 500, "CITY"))
                .assignTimestampsAndWatermarks(createWatermarkStrategy("city-group"));
                
            DataStream<TrafficEvent> highwayStream = env.addSource(new TrafficDataSource(2000, 100, "HIGHWAY"))
                .assignTimestampsAndWatermarks(createWatermarkStrategy("highway-group"));
                
            DataStream<TrafficEvent> ruralStream = env.addSource(new BacklogTrafficSource(1000, 3000, "RURAL"))
                .assignTimestampsAndWatermarks(createWatermarkStrategy("rural-group"));
            
            // 合并所有流并应用全局水印对齐
            DataStream<TrafficEvent> allStreams = cityStream
                .union(highwayStream, ruralStream);
                
            // 使用具有多种窗口类型的复杂处理管道
            // 1. 滑动窗口用于实时交通状况
            SingleOutputStreamOperator<TrafficAnalysisResult> slidingResults = allStreams
                .keyBy(TrafficEvent::getRoadSegment)
                .window(SlidingEventTimeWindows.of(Time.minutes(5), Time.minutes(1)))
                .allowedLateness(Time.minutes(1))
                .sideOutputLateData(LATE_EVENTS)
                .aggregate(new TrafficAggregateFunction(), new TrafficWindowFunction());
                
            // 实际生产环境下，应该使用execute执行作业并收集结果
            log.info("构建了综合水印演示的处理管道，在实际环境中会执行计算");
            
            // 返回模拟数据 - 所有特性的综合
            return generateDemoResults("综合示例", 25, true, true);
        } catch (Exception e) {
            log.error("执行综合水印演示时出错", e);
            throw e;
        }
    }
    
    /**
     * 生成模拟的交通分析结果数据
     */
    private List<TrafficAnalysisResult> generateDemoResults(String prefix, int count) {
        return generateDemoResults(prefix, count, false, false);
    }
    
    /**
     * 生成模拟的交通分析结果数据
     * @param prefix 结果前缀
     * @param count 结果数量
     * @param includeLateness 是否包含延迟事件
     * @param highTraffic 是否为高流量场景
     */
    private List<TrafficAnalysisResult> generateDemoResults(String prefix, int count, boolean includeLateness, boolean highTraffic) {
        List<TrafficAnalysisResult> results = new ArrayList<>();
        Random random = new Random();
        long currentTime = System.currentTimeMillis();
        
        String[] roadSegments = {"A1", "B2", "C3", "D4", "E5", "F6", "G7", "H8"};
        
        for (int i = 0; i < count; i++) {
            String roadSegment = roadSegments[random.nextInt(roadSegments.length)];
            long windowStart = currentTime - (6 - i) * 60 * 1000; // 模拟窗口开始时间(每分钟一个窗口)
            long windowEnd = windowStart + 5 * 60 * 1000; // 5分钟窗口
            
            // 生成车速和车流量 - 高流量场景有更多车辆和更低的速度
            double avgSpeed = highTraffic ? 
                              20 + random.nextDouble() * 40 : // 高流量: 20-60 km/h
                              40 + random.nextDouble() * 80;  // 正常: 40-120 km/h
            
            int vehicleCount = highTraffic ?
                              50 + random.nextInt(150) : // 高流量: 50-200辆
                              5 + random.nextInt(45);    // 正常: 5-50辆
            
            // 创建结果对象
            TrafficAnalysisResult result = new TrafficAnalysisResult(
                prefix + "-" + roadSegment,
                windowStart,
                windowEnd,
                avgSpeed,
                vehicleCount
            );
            
            // 设置延迟事件计数(如果需要)
            if (includeLateness && random.nextDouble() < 0.3) { // 30%的窗口有延迟事件
                result.setLateEventCount(1 + random.nextInt(5)); // 1-5个延迟事件
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 生成模拟的交通分析结果数据，包含延迟事件
     */
    private List<TrafficAnalysisResult> generateDemoResults(String prefix, int count, boolean includeLateness) {
        return generateDemoResults(prefix, count, includeLateness, false);
    }
    
    // 创建包含水印对齐和空闲检测的水印策略
    private WatermarkStrategy<TrafficEvent> createWatermarkStrategy(String group) {
        return WatermarkStrategy
            .<TrafficEvent>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
            .withIdleness(Duration.ofSeconds(30))
            .withWatermarkAlignment(group, Duration.ofSeconds(5), Duration.ofSeconds(15));
    }
    
    /**
     * 交通数据聚合函数 - 计算平均速度和车辆数量
     */
    private static class TrafficAggregateFunction implements 
            AggregateFunction<TrafficEvent, Tuple4<String, Double, Integer, Long>, Tuple5<String, Long, Long, Double, Integer>> {
        
        @Override
        public Tuple4<String, Double, Integer, Long> createAccumulator() {
            return new Tuple4<>("", 0.0, 0, 0L);
        }
        
        @Override
        public Tuple4<String, Double, Integer, Long> add(TrafficEvent value, Tuple4<String, Double, Integer, Long> accumulator) {
            return new Tuple4<>(
                value.getRoadSegment(),
                accumulator.f1 + value.getSpeed(),
                accumulator.f2 + 1,
                Math.max(accumulator.f3, value.getTimestamp())
            );
        }
        
        @Override
        public Tuple5<String, Long, Long, Double, Integer> getResult(Tuple4<String, Double, Integer, Long> accumulator) {
            double avgSpeed = accumulator.f2 > 0 ? accumulator.f1 / accumulator.f2 : 0;
            // 返回：道路段ID、窗口开始(临时0)、窗口结束(临时0)、平均速度、车辆数量
            return new Tuple5<>(accumulator.f0, 0L, 0L, avgSpeed, accumulator.f2);
        }
        
        @Override
        public Tuple4<String, Double, Integer, Long> merge(
                Tuple4<String, Double, Integer, Long> a, Tuple4<String, Double, Integer, Long> b) {
            return new Tuple4<>(
                a.f0,
                a.f1 + b.f1,
                a.f2 + b.f2,
                Math.max(a.f3, b.f3)
            );
        }
    }
    
    /**
     * 交通数据窗口函数 - 添加窗口元数据和额外计算
     */
    private static class TrafficWindowFunction extends
            ProcessWindowFunction<Tuple5<String, Long, Long, Double, Integer>, TrafficAnalysisResult, String, TimeWindow> {
        
        @Override
        public void process(String key, Context context, 
                          Iterable<Tuple5<String, Long, Long, Double, Integer>> elements, 
                          Collector<TrafficAnalysisResult> out) {
            // 应该只有一个元素，我们从聚合函数得到一个结果
            Tuple5<String, Long, Long, Double, Integer> element = elements.iterator().next();
            
            // 添加窗口信息
            TrafficAnalysisResult result = new TrafficAnalysisResult(
                element.f0,
                context.window().getStart(),
                context.window().getEnd(),
                element.f3,
                element.f4
            );
            
            // 注册当前的水印
            long watermark = context.currentWatermark();
            long lateness = Math.max(0, context.window().getEnd() - watermark);
            
            // 如果是延迟事件，增加延迟计数
            if (lateness > 0) {
                result.setLateEventCount(1);
            }
            
            out.collect(result);
        }
    }
    
    /**
     * 交通数据源 - 生成模拟交通数据
     */
    private static class TrafficDataSource implements SourceFunction<TrafficEvent> {
        private volatile boolean isRunning = true;
        private final int eventsPerSecond;
        private final int delayMillis;
        private final String sensorPrefix;
        private final Random random = new Random();
        private final String[] roadSegments = {"A1", "B2", "C3", "D4", "E5", "F6", "G7", "H8"};
        
        public TrafficDataSource(int eventsPerSecond, int delayMillis) {
            this(eventsPerSecond, delayMillis, "SENSOR");
        }
        
        public TrafficDataSource(int eventsPerSecond, int delayMillis, String sensorPrefix) {
            this.eventsPerSecond = eventsPerSecond;
            this.delayMillis = delayMillis;
            this.sensorPrefix = sensorPrefix;
        }
        
        @Override
        public void run(SourceContext<TrafficEvent> ctx) throws Exception {
            long sleepTimeMillis = 1000 / (eventsPerSecond > 0 ? eventsPerSecond : 1);
            
            while (isRunning) {
                // 生成当前时间戳
                long currentTime = System.currentTimeMillis();
                
                // 根据配置的延迟调整时间戳
                long eventTime = currentTime - delayMillis - random.nextInt(1000);
                
                // 为了演示目的，添加一些随机性
                // 有10%的情况下生成一个严重延迟的事件
                if (random.nextDouble() < 0.1) {
                    eventTime = currentTime - delayMillis - 10000 - random.nextInt(50000);
                }
                
                // 随机选择道路段
                String roadSegment = roadSegments[random.nextInt(roadSegments.length)];
                
                // 随机生成车速 (20-120 km/h)
                double speed = 20 + random.nextDouble() * 100;
                
                // 生成传感器ID
                String sensorId = sensorPrefix + "-" + (random.nextInt(5) + 1);
                
                // 创建并发出事件
                TrafficEvent event = new TrafficEvent(sensorId, eventTime, speed, roadSegment);
                ctx.collectWithTimestamp(event, eventTime);
                
                // 模拟处理时间
                Thread.sleep(sleepTimeMillis);
            }
        }
        
        @Override
        public void cancel() {
            isRunning = false;
        }
    }
    
    /**
     * 带有积压报告功能的交通数据源
     */
    private static class BacklogTrafficSource implements SourceFunction<TrafficEvent> {
        private volatile boolean isRunning = true;
        private final int eventsPerSecond;
        private final int delayMillis;
        private final String sensorPrefix;
        private final Random random = new Random();
        private final String[] roadSegments = {"A1", "B2", "C3", "D4", "E5", "F6", "G7", "H8"};
        
        public BacklogTrafficSource(int eventsPerSecond, int delayMillis) {
            this(eventsPerSecond, delayMillis, "BACKLOG");
        }
        
        public BacklogTrafficSource(int eventsPerSecond, int delayMillis, String sensorPrefix) {
            this.eventsPerSecond = eventsPerSecond;
            this.delayMillis = delayMillis;
            this.sensorPrefix = sensorPrefix;
        }
        
        @Override
        public void run(SourceContext<TrafficEvent> ctx) throws Exception {
            long sleepTimeMillis = 1000 / (eventsPerSecond > 0 ? eventsPerSecond : 1);
            boolean isProcessingBacklog = false;
            
            // 创建一个积压模拟阶段
            long backlogStartTime = System.currentTimeMillis() + 5000; // 5秒后开始积压
            long backlogEndTime = backlogStartTime + 30000; // 积压持续30秒
            
            while (isRunning) {
                long currentTime = System.currentTimeMillis();
                
                // 检查是否处于积压阶段
                if (currentTime >= backlogStartTime && currentTime <= backlogEndTime) {
                    isProcessingBacklog = true;
                    
                    // 在积压期间产生大量事件
                    for (int i = 0; i < 100; i++) {
                        generateAndEmitEvent(ctx, currentTime);
                    }
                } else {
                    isProcessingBacklog = false;
                    generateAndEmitEvent(ctx, currentTime);
                }
                
                // 在实际Flink 1.18+应用中，应调用：
                // 如果源实现了SplitEnumeratorContext，则可以设置积压状态
                // context.setIsProcessingBacklog(isProcessingBacklog);
                
                Thread.sleep(sleepTimeMillis);
            }
        }
        
        private void generateAndEmitEvent(SourceContext<TrafficEvent> ctx, long currentTime) {
            long eventTime = currentTime - delayMillis - random.nextInt(1000);
            String roadSegment = roadSegments[random.nextInt(roadSegments.length)];
            double speed = 20 + random.nextDouble() * 100;
            String sensorId = sensorPrefix + "-" + (random.nextInt(5) + 1);
            
            TrafficEvent event = new TrafficEvent(sensorId, eventTime, speed, roadSegment);
            ctx.collectWithTimestamp(event, eventTime);
        }
        
        @Override
        public void cancel() {
            isRunning = false;
        }
    }
} 