#!/bin/bash

# 测试脚本: 使用curl测试Flink与Spring Boot集成示例的所有API端点

echo "============================================================"
echo "                 Flink API 测试脚本"
echo "============================================================"

BASE_URL="http://localhost:8090/api/flink"

# 定义颜色常量
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m' # 恢复默认颜色

# 测试函数
test_api() {
    local endpoint=$1
    local name=$2
    
    echo -e "\n${BLUE}测试 $name${NC}"
    echo "请求: $endpoint"
    echo "响应:"
    
    response=$(curl -s "$endpoint")
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}成功!${NC}"
        # 美化JSON输出
        echo "$response" | python -m json.tool
    else
        echo -e "${RED}失败: 无法连接到 $endpoint${NC}"
    fi
    echo "---------------------------------------------------------"
}

# 批处理API测试
echo -e "\n${YELLOW}[1] 批处理 API 测试${NC}"

test_api "$BASE_URL/batch/transform" "批处理 - 基本转换操作"
test_api "$BASE_URL/batch/wordcount" "批处理 - 单词计数"
test_api "$BASE_URL/batch/aggregate" "批处理 - 分组聚合"

# 流处理API测试
echo -e "\n${YELLOW}[2] 流处理 API 测试${NC}"

test_api "$BASE_URL/stream/processing-time" "流处理 - 处理时间窗口"
test_api "$BASE_URL/stream/event-time" "流处理 - 事件时间窗口与Watermark"

# Table API与SQL测试
echo -e "\n${YELLOW}[3] Table API与SQL 测试${NC}"

test_api "$BASE_URL/table/api" "Table API"
test_api "$BASE_URL/table/sql" "SQL"
test_api "$BASE_URL/table/combined" "Table API与SQL结合"

# 参数化API测试
echo -e "\n${YELLOW}[4] 参数化 API 测试${NC}"

for threshold in 10 20 30 50 80; do
    test_api "$BASE_URL/table/speed-filter?threshold=$threshold" "速度阈值过滤 (阈值=$threshold)"
done

echo -e "\n${GREEN}全部API测试完成!${NC}" 