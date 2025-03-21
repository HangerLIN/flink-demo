#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 设置API基础URL
BASE_URL="http://localhost:8090/api/datastream"

# 打印分隔线
print_separator() {
  echo -e "${BLUE}===================================================${NC}"
}

# 测试并打印API调用结果
test_api() {
  local api_path=$1
  local description=$2
  
  print_separator
  echo -e "${YELLOW}测试: $description${NC}"
  echo -e "${YELLOW}API: $api_path${NC}"
  echo ""
  
  # 发起HTTP请求
  response=$(curl -s "$BASE_URL$api_path")
  if [ $? -eq 0 ]; then
    # 如果curl成功
    echo -e "${GREEN}响应:${NC}"
    echo "$response" | head -n 20
    echo ""
    echo -e "${GREEN}✓ 测试完成${NC}"
  else
    # 如果curl失败
    echo -e "${RED}✗ 请求失败${NC}"
  fi
}

# 打印测试标题
echo -e "${GREEN}=== Flink DataStream 流处理算子 API 测试 ===${NC}"
echo ""

# 测试基础转换算子
test_api "/basic-transform" "基础转换算子(Map/Filter/FlatMap)"

# 测试KeyBy和Reduce算子
test_api "/keyby-reduce" "KeyBy和Reduce算子"

# 测试Window窗口算子
test_api "/window" "Window窗口算子"

# 测试多流转换算子
test_api "/multi-stream" "多流转换算子(Union/Connect)"

# 显示总结
print_separator
echo -e "${GREEN}所有DataStream流处理算子API测试完成!${NC}" 