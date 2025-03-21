# Flink SpringBoot 示例项目

这是一个演示如何在Spring Boot应用程序中集成Apache Flink的示例项目。该项目展示了Flink的各种功能和API的使用，包括DataStream API、Table API和SQL等。

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
├── manage_app.sh           # 应用程序管理脚本
└── pom.xml                 # Maven配置文件
```

## 技术栈

- Spring Boot 2.7.5
- Apache Flink 1.16.0
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

### API端点

| 端点 | 描述 |
|------|------|
| `/api/datastream/basic-transform` | 基础转换算子示例(Map/Filter/FlatMap) |
| `/api/datastream/keyby-reduce` | KeyBy和Reduce算子示例 |
| `/api/datastream/window` | Window窗口算子示例 |
| `/api/datastream/multi-stream` | 多流转换算子示例(Union/Connect) |

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

项目提供了一个测试脚本用于测试所有DataStream API端点：

```bash
# 赋予脚本执行权限
chmod +x test_datastream_apis.sh

# 运行测试脚本
./test_datastream_apis.sh
```

## 架构说明

- **控制器层**：提供REST API端点，接收请求并返回处理结果
- **服务层**：包含Flink处理逻辑，使用不同的Flink算子处理数据
- **模型层**：定义数据模型，如传感器读数、交通数据等

## 注意事项

- 本项目中的Flink操作均为基于内存的示例，未使用外部数据源或sink
- 为了简化演示，使用了有界数据流和本地执行环境
- 在实际生产环境中，应考虑使用外部数据源、持久化存储、状态后端等

## 延伸阅读

- [Apache Flink官方文档](https://flink.apache.org/docs/stable/)
- [Spring Boot官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Flink DataStream API](https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/datastream/overview/) 