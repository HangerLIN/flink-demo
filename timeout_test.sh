#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8090/api/fraud"
TIMEOUT=10  # 10秒超时限制

echo -e "${BLUE}=====================================================${NC}"
echo -e "${BLUE}     Flink CEP 欺诈检测系统 - 超时测试脚本         ${NC}"
echo -e "${BLUE}=====================================================${NC}"

# 检查服务健康状态
echo -e "\n${YELLOW}[1] 检查服务健康状态...${NC}"
if curl -s -m 5 -X GET "${BASE_URL}/health" | grep -q "UP"; then
    echo -e "${GREEN}服务运行正常!${NC}"
else
    echo -e "${RED}错误: 服务未运行或无法访问${NC}"
    exit 1
fi

# 生成少量测试数据
echo -e "\n${YELLOW}[2] 生成少量测试数据(5条)...${NC}"
curl -s -m 5 "${BASE_URL}/generate-test-data?count=5" > mini_test.json
echo -e "${GREEN}已生成测试数据${NC}"

# 测试欺诈检测接口
echo -e "\n${YELLOW}[3] 测试欺诈检测接口(带${TIMEOUT}秒超时)...${NC}"

# 使用curl自带的超时选项
curl -s -m $TIMEOUT -X POST "${BASE_URL}/detect" \
  -H "Content-Type: application/json" \
  -d @mini_test.json > detect_result.json
CURL_EXIT=$?

# 检查结果
if [ $CURL_EXIT -eq 28 ]; then
    echo -e "${RED}检测超时! 服务处理时间超过${TIMEOUT}秒${NC}"
    echo -e "${YELLOW}需要修改代码解决性能问题${NC}"
    exit 1
elif [ -s detect_result.json ]; then
    echo -e "${GREEN}检测成功! 结果:${NC}"
    cat detect_result.json | grep -o '"success":[^,]*' || echo -e "${RED}未找到success字段，可能出错${NC}"
    cat detect_result.json | head -20
else
    echo -e "${RED}检测失败! 未收到有效响应${NC}"
    exit 1
fi

echo -e "\n${BLUE}=====================================================${NC}"
echo -e "${BLUE}            测试完成                              ${NC}"
echo -e "${BLUE}=====================================================${NC}" 