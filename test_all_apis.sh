#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 设置API基础URL
BASE_URL="http://localhost:8090/api"

# 打印分隔线
print_separator() {
  echo -e "${BLUE}===================================================${NC}"
}

# 显示章节标题
print_section() {
  print_separator
  echo -e "${GREEN}$1${NC}"
  print_separator
}

# 测试并打印API调用结果
test_api() {
  local api_path=$1
  local description=$2
  
  echo -e "${YELLOW}测试: $description${NC}"
  echo -e "${YELLOW}API: $api_path${NC}"
  echo ""
  
  # 发起HTTP请求
  response=$(curl -s "$BASE_URL$api_path")
  if [ $? -eq 0 ]; then
    # 如果curl成功
    echo -e "${GREEN}响应:${NC}"
    echo "$response" | head -n 20
    if [[ $(echo "$response" | wc -l) -gt 20 ]]; then
      echo -e "${YELLOW}...(输出被截断)${NC}"
    fi
    echo ""
    echo -e "${GREEN}✓ 测试完成${NC}"
  else
    # 如果curl失败
    echo -e "${RED}✗ 请求失败${NC}"
  fi
  
  # 添加简单间隔
  echo ""
}

# 打印测试标题
echo -e "${GREEN}=== Flink SpringBoot 应用 API 测试 ===${NC}"
echo ""

# 测试DataStream流处理算子
print_section "DataStream 流处理算子"

test_api "/datastream/basic-transform" "基础转换算子(Map/Filter/FlatMap)"
test_api "/datastream/keyby-reduce" "KeyBy和Reduce算子"
test_api "/datastream/window" "Window窗口算子"
test_api "/datastream/multi-stream" "多流转换算子(Union/Connect)"

# 测试批处理API（如果有）
print_section "批处理 API"

test_api "/flink/batch/transform" "批处理基本转换"
test_api "/flink/batch/aggregate" "批处理分组聚合"

# 测试流处理API（如果有）
print_section "流处理 API"

test_api "/flink/stream/processing-time" "处理时间窗口"
test_api "/flink/stream/event-time" "事件时间窗口与Watermark"

# 测试Table API和SQL（如果有）
print_section "Table API 和 SQL"

test_api "/flink/table/api" "Table API查询"
test_api "/flink/table/sql" "SQL查询"
test_api "/flink/table/combined" "Table API与SQL结合"
test_api "/flink/table/speed-filter?threshold=60" "速度阈值过滤 (阈值=60)"

# 显示总结
print_separator
echo -e "${GREEN}所有API测试完成!${NC}"
echo -e "${YELLOW}如果某些API返回404错误，表示该API端点不存在或尚未实现${NC}" 