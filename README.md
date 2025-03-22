# Flink SpringBoot 示例项目

这是一个演示如何在Spring Boot应用程序中集成Apache Flink的示例项目。该项目展示了Flink的各种功能和API的使用，包括DataStream API、Table API和SQL、CEP复杂事件处理和高级水印特性等。

## 项目结构

```
flink-springboot-demo
├── src/main/java/com/example/flinkdemo
│   ├── controller        # REST API控制器
│   ├── service           # 业务逻辑和Flink处理服务
│   ├── model             # 数据模型类
│   ├── config            # 配置类
│   └── FlinkDemoApplication.java  # 应用程序入口
├── test_datastream_apis.sh  # 测试DataStream API的脚本
├── test_fraud_detection.sh  # 测试欺诈检测功能的脚本
├── final_test.sh            # 综合测试脚本
├── manage_app.sh            # 应用程序管理脚本
└── pom.xml                  # Maven配置文件
```

## 技术栈

- Spring Boot 2.7.5
- Apache Flink 1.16.1
- Java 11
- Lombok
- Maven

## 功能演示

本项目演示了以下Flink功能：

### DataStream API

1. **基础转换算子**：
   - Map: 将传感器温度从摄氏度转为华氏度
   - Filter: 过滤高温值
   - FlatMap: 为每个传感器生成多条记录

2. **KeyBy和Reduce算子**：
   - 按传感器ID分组
   - 计算每个传感器的最高温度

3. **Window窗口算子**：
   - 按传感器ID分组
   - 应用5秒滚动窗口计算窗口内的平均温度

4. **多流转换算子**：
   - Union: 合并同类型的数据流
   - Connect: 连接不同类型的数据流
   - CoMap: 对不同类型的流进行分别处理

### 高级水印特性

1. **基本水印与事件时间窗口**：
   - 使用有界乱序水印策略
   - 结合事件时间滑动窗口

2. **水印对齐**：
   - 处理不同数据源的水印传播
   - 防止快速数据源饿死慢速数据源

3. **积压处理**：
   - 在数据积压期间调整检查点行为
   - 适应高负载场景

4. **综合示例**：
   - 多种水印策略结合使用
   - 不同窗口类型的应用

### CEP复杂事件处理

1. **欺诈检测**：
   - 使用Flink CEP识别可疑交易模式
   - 实现实时欺诈警报生成

2. **模式匹配**：
   - 定义连续事件匹配规则
   - 处理带时间约束的复杂事件序列

3. **风险评分**：
   - 基于多维度特征分析
   - 实时更新风险评分

### Table API & SQL

1. **基础表操作**：
   - 创建表并注册视图
   - 执行简单的SQL查询

2. **表聚合**：
   - 使用Table API执行分组聚合
   - 将DataStream转换为表并返回

3. **SQL查询**：
   - 执行复杂的SQL查询
   - 使用窗口函数和聚合函数

### API端点

| 端点 | 描述 |
|------|------|
| `/api/datastream/basic-transform` | 基础转换算子示例(Map/Filter/FlatMap) |
| `/api/datastream/keyby-reduce` | KeyBy和Reduce算子示例 |
| `/api/datastream/window` | Window窗口算子示例 |
| `/api/datastream/multi-stream` | 多流转换算子示例(Union/Connect) |
| `/api/watermark/basic` | 基本水印和事件时间窗口示例 |
| `/api/watermark/alignment` | 水印对齐功能示例 |
| `/api/watermark/backlog` | 积压处理期间的检查点调整示例 |
| `/api/watermark/comprehensive` | 综合演示所有高级水印特性 |
| `/api/fraud-detection/analyze` | 欺诈检测分析 |
| `/api/table-sql/basic-table` | 基础表操作示例 |
| `/api/table-sql/table-aggregate` | 表聚合示例 |
| `/api/table-sql/sql-query` | SQL查询示例 |

## 运行项目

### 前置条件

- Java 11+
- Maven 3.6+

### 编译和运行

```bash
# 编译项目
mvn clean package

# 运行Spring Boot应用
java -jar target/flink-springboot-demo-0.0.1-SNAPSHOT.jar
```

### 使用管理脚本

项目提供了一个管理脚本用于启动、停止和查看应用状态：

```bash
# 赋予脚本执行权限
chmod +x manage_app.sh

# 查看帮助
./manage_app.sh help

# 启动应用
./manage_app.sh start

# 查看应用状态
./manage_app.sh status

# 停止应用
./manage_app.sh stop

# 重启应用
./manage_app.sh restart
```

### 测试API

项目提供了多个测试脚本用于测试不同功能：

```bash
# 赋予脚本执行权限
chmod +x test_datastream_apis.sh
chmod +x test_fraud_detection.sh
chmod +x final_test.sh

# 测试DataStream API功能
./test_datastream_apis.sh

# 测试欺诈检测功能
./test_fraud_detection.sh

# 运行综合测试
./final_test.sh
```

## 架构说明

- **控制器层**：提供REST API端点，接收请求并返回处理结果
- **服务层**：包含Flink处理逻辑，使用不同的Flink算子处理数据
- **模型层**：定义数据模型，如传感器读数、交易事件、交通数据等

## 高级特性

- **复杂事件处理(CEP)**：使用Flink CEP库实现对复杂事件序列的检测
- **水印和事件时间**：处理乱序事件并基于事件时间进行窗口计算
- **状态管理**：演示有状态计算，使用不同类型的状态(ValueState等)
- **检查点配置**：配置检查点行为，支持在不同负载下的性能调整
- **侧输出流**：处理延迟事件和异常情况，确保不丢失数据

## 注意事项

- 本项目中的Flink操作均为基于内存的示例，未使用外部数据源或sink
- 为了简化演示，使用了有界数据流和本地执行环境
- 在实际生产环境中，应考虑使用外部数据源、持久化存储、状态后端等

## 延伸阅读

- [Apache Flink官方文档](https://flink.apache.org/docs/stable/)
- [Spring Boot官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Flink DataStream API](https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/datastream/overview/)
- [Flink CEP文档](https://nightlies.apache.org/flink/flink-docs-stable/docs/libs/cep/)
- [Flink水印与事件时间](https://nightlies.apache.org/flink/flink-docs-stable/docs/concepts/time/) 