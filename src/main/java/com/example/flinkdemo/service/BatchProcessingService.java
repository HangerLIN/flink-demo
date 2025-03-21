package com.example.flinkdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 批处理服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessingService {

    private final ExecutionEnvironment batchEnv;

    /**
     * 演示基本的数据转换操作（Map, FlatMap, Filter）并统计词频
     */
    public List<Tuple2<String, Integer>> basicTransformationDemo() throws Exception {
        // 创建一个简化的数据集，减少类别但保持大量重复以便统计
        DataSet<String> textDataSet = batchEnv.fromElements(
                "Flink Java Flink Python Flink Scala Flink JavaScript Flink TypeScript Flink Ruby Flink Golang Flink Swift",
                "Flink Hadoop Flink Spark Flink MapReduce Flink HDFS Flink Storm Flink Kafka Flink ZooKeeper Flink Beam",
                "Java Spring Java Hibernate Java MyBatis Java JDBC Java JPA Java Spring Java Boot Java Cloud Java Microservice",
                "Python Django Python Flask Python FastAPI Python Tornado Python Celery Python Pandas Python NumPy Python TensorFlow",
                "Cloud AWS Cloud Azure Cloud Google Cloud Alibaba Cloud Tencent Cloud DigitalOcean Cloud Heroku Cloud Vercel",
                "Database MySQL Database Redis Database MongoDB Database PostgreSQL Database Oracle Database SQLite Database MariaDB",
                "React Angular React Vue React Svelte React NextJS React Gatsby React Redux React MobX React GraphQL React Apollo",
                "Docker Kubernetes Docker Swarm Docker Compose Docker Podman Docker Containerd Docker CRI-O Docker LXC Docker OCI",
                "Linux Ubuntu Linux Debian Linux CentOS Linux Fedora Linux Arch Linux RHEL Linux Alpine Linux Gentoo Linux Mint",
                "Security Firewall Security VPN Security Encryption Security Authentication Security Authorization Security OAuth"
        );

        // 分割单词、转换为小写、过滤长度大于4的单词，并进行词频统计
        DataSet<Tuple2<String, Integer>> wordCounts = textDataSet
                .flatMap(new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String line, Collector<String> out) {
                        Arrays.stream(line.split("\\s+"))
                                .forEach(out::collect);
                    }
                })
                .map(new MapFunction<String, String>() {
                    @Override
                    public String map(String word) {
                        return word.toLowerCase();
                    }
                })
                .filter(new FilterFunction<String>() {
                    @Override
                    public boolean filter(String word) {
                        return word.length() > 4;
                    }
                })
                .map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String word) {
                        return new Tuple2<>(word, 1);
                    }
                })
                .groupBy(0)
                .sum(1);

        // 获取结果并按词频从高到低排序
        List<Tuple2<String, Integer>> result = wordCounts.collect();
        result.sort((a, b) -> Integer.compare(b.f1, a.f1)); // 从高到低排序
        
        return result;
    }

    /**
     * 演示单词计数（WordCount）批处理
     */
    public List<Tuple2<String, Integer>> wordCountDemo() throws Exception {
        // 创建一个简单的字符串数据集
        DataSet<String> textDataSet = batchEnv.fromElements(
                "Flink Apache Spark Hadoop",
                "Flink Spark Storm Samza",
                "Hadoop HDFS Hive Pig Flink Flink"
        );

        // 实现经典的WordCount
        DataSet<Tuple2<String, Integer>> wordCounts = textDataSet
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
                        Arrays.stream(line.split("\\s+"))
                                .map(String::toLowerCase)
                                .forEach(word -> out.collect(new Tuple2<>(word, 1)));
                    }
                })
                .groupBy(0)
                .sum(1);

        return wordCounts.collect();
    }

    /**
     * 演示分组聚合操作（Group and Reduce）
     */
    public List<Tuple2<String, Integer>> groupAndReduceDemo() throws Exception {
        // 创建一个产品销售数据集
        DataSet<Tuple3<String, String, Integer>> salesData = batchEnv.fromElements(
                new Tuple3<>("电子", "手机", 1000),
                new Tuple3<>("电子", "笔记本", 2000),
                new Tuple3<>("电子", "耳机", 300),
                new Tuple3<>("服装", "T恤", 200),
                new Tuple3<>("服装", "牛仔裤", 800),
                new Tuple3<>("服装", "外套", 1200),
                new Tuple3<>("食品", "饮料", 100),
                new Tuple3<>("食品", "零食", 150),
                new Tuple3<>("食品", "主食", 400)
        );

        // 按类别分组，计算每个类别的总销售额
        DataSet<Tuple2<String, Integer>> categorySales = salesData
                .map(new MapFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(Tuple3<String, String, Integer> sale) {
                        return new Tuple2<>(sale.f0, sale.f2);
                    }
                })
                .groupBy(0)
                .sum(1);

        return categorySales.collect();
    }
} 