#!/bin/bash

# 欺诈检测API测试脚本
# 用于测试Flink CEP欺诈检测系统的各项功能

BASE_URL="http://localhost:8090/api/fraud"
TEMP_FILE="test_transactions.json"
CUSTOM_RULE_ID="RULE_DEVICE_CHANGE"

# 设置文本颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # 恢复默认颜色

echo -e "${BLUE}=====================================================${NC}"
echo -e "${BLUE}     Flink CEP 欺诈检测系统 - 自动测试脚本         ${NC}"
echo -e "${BLUE}=====================================================${NC}"

# 检查jq是否安装
if ! command -v jq &> /dev/null; then
    echo -e "${RED}错误: 请先安装jq工具 (brew install jq 或 apt-get install jq)${NC}"
    exit 1
fi

# 检查服务是否运行
echo -e "\n${YELLOW}[1] 检查服务状态...${NC}"
if curl -s -f -X GET "${BASE_URL}/health" > /dev/null; then
    echo -e "${GREEN}服务运行正常!${NC}"
else
    echo -e "${RED}错误: 服务未运行或无法访问，请确保服务已启动并运行在http://localhost:8090${NC}"
    exit 1
fi

# 获取当前规则
echo -e "\n${YELLOW}[2] 获取当前规则列表...${NC}"
curl -s -X GET "${BASE_URL}/rules" -H "Content-Type: application/json" | jq .

# 生成测试数据
echo -e "\n${YELLOW}[3] 生成测试交易数据...${NC}"
curl -s -X GET "${BASE_URL}/generate-test-data?count=20" -H "Content-Type: application/json" > ${TEMP_FILE}
echo -e "已生成20条测试交易数据并保存到${TEMP_FILE}:"
cat ${TEMP_FILE} | jq 'length'

# 添加一些高风险交易到测试数据
echo -e "\n${YELLOW}[4] 添加特定的高风险交易场景到测试数据...${NC}"
RISK_DATA=$(cat <<EOF
[
  {
    "transactionId": "tx-risk-1",
    "userId": "user-test-1",
    "amount": 500,
    "type": "TRANSFER",
    "channel": "APP",
    "ipAddress": "192.168.1.100",
    "location": "Beijing",
    "deviceId": "device-a",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 30
  },
  {
    "transactionId": "tx-risk-2",
    "userId": "user-test-1",
    "amount": 600,
    "type": "TRANSFER",
    "channel": "APP",
    "ipAddress": "192.168.1.101",
    "location": "Beijing",
    "deviceId": "device-a",
    "timestamp": "$(date -u -v+30S +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -u -d "+30 seconds" +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 35
  },
  {
    "transactionId": "tx-risk-3",
    "userId": "user-test-1",
    "amount": 700,
    "type": "WITHDRAWAL",
    "channel": "APP",
    "ipAddress": "192.168.1.102",
    "location": "Beijing",
    "deviceId": "device-a",
    "timestamp": "$(date -u -v+45S +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -u -d "+45 seconds" +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 40
  },
  {
    "transactionId": "tx-risk-4",
    "userId": "user-test-2",
    "amount": 12000,
    "type": "TRANSFER",
    "channel": "WEB",
    "ipAddress": "192.168.1.200",
    "location": "Shanghai",
    "deviceId": "device-b",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 75
  },
  {
    "transactionId": "tx-risk-5",
    "userId": "user-test-3",
    "amount": 800,
    "type": "PAYMENT",
    "channel": "APP",
    "ipAddress": "192.168.1.300",
    "location": "Guangzhou",
    "deviceId": "device-c",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 20
  },
  {
    "transactionId": "tx-risk-6",
    "userId": "user-test-3",
    "amount": 900,
    "type": "PAYMENT",
    "channel": "WEB",
    "ipAddress": "192.168.1.301",
    "location": "Shenzhen",
    "deviceId": "device-c",
    "timestamp": "$(date -u -v+60S +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -u -d "+60 seconds" +"%Y-%m-%dT%H:%M:%S")",
    "riskScore": 60
  }
]
EOF
)

echo $RISK_DATA > risk_transactions.json
echo -e "已创建6条高风险交易样本并保存到risk_transactions.json"

# 合并两个数据文件
jq -s '.[0] + .[1]' ${TEMP_FILE} risk_transactions.json > all_transactions.json
echo -e "已将随机交易和高风险交易合并到all_transactions.json"

# 执行欺诈检测
echo -e "\n${YELLOW}[5] 执行欺诈检测...${NC}"
curl -s -X POST "${BASE_URL}/detect" \
  -H "Content-Type: application/json" \
  -d @all_transactions.json | jq .

# 获取检测结果
echo -e "\n${YELLOW}[6] 获取检测到的欺诈警报...${NC}"
curl -s -X GET "${BASE_URL}/alerts" -H "Content-Type: application/json" | jq .

echo -e "\n${YELLOW}[7] 获取检测到的流量异常...${NC}"
curl -s -X GET "${BASE_URL}/anomalies" -H "Content-Type: application/json" | jq .

# 添加自定义规则
echo -e "\n${YELLOW}[8] 添加新规则 - 设备变更检测...${NC}"
curl -s -X POST "${BASE_URL}/rules" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleId": "'${CUSTOM_RULE_ID}'",
    "ruleName": "设备变更检测",
    "description": "检测同一用户在短时间内使用多个设备",
    "status": "ACTIVE",
    "priority": 4,
    "type": "FRAUD_DETECTION",
    "timeWindowSeconds": 300,
    "conditions": {
      "timeWindowSeconds": 300
    },
    "eventSequence": ["TRANSACTION", "TRANSACTION"],
    "actions": ["GENERATE_ALERT", "UPDATE_RISK_SCORE"]
  }' | jq .

echo -e "\n${YELLOW}[9] 验证规则是否添加成功...${NC}"
curl -s -X GET "${BASE_URL}/rules" -H "Content-Type: application/json" | jq .

# 更新规则
echo -e "\n${YELLOW}[10] 更新规则 - 调整大额交易规则阈值...${NC}"
curl -s -X PUT "${BASE_URL}/rules/RULE_LARGE_AMOUNT" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleId": "RULE_LARGE_AMOUNT",
    "ruleName": "大额交易检测-更新版",
    "description": "检测超过阈值的大额交易(已更新阈值)",
    "status": "ACTIVE",
    "priority": 3,
    "type": "FRAUD_DETECTION",
    "timeWindowSeconds": 3600,
    "conditions": {
      "thresholdAmount": 8000,
      "timeWindowSeconds": 3600
    },
    "eventSequence": ["TRANSACTION"],
    "actions": ["GENERATE_ALERT", "NOTIFY_ADMIN", "BLOCK_TRANSACTION"]
  }' | jq .

# 重新检测欺诈
echo -e "\n${YELLOW}[11] 使用更新后的规则重新检测欺诈...${NC}"
curl -s -X POST "${BASE_URL}/detect" \
  -H "Content-Type: application/json" \
  -d @all_transactions.json | jq .

# 再次获取结果
echo -e "\n${YELLOW}[12] 再次获取警报结果...${NC}"
curl -s -X GET "${BASE_URL}/alerts" -H "Content-Type: application/json" | jq .

# 删除自定义规则
echo -e "\n${YELLOW}[13] 删除自定义规则...${NC}"
curl -s -X DELETE "${BASE_URL}/rules/${CUSTOM_RULE_ID}" -H "Content-Type: application/json" | jq .

# 检查规则列表
echo -e "\n${YELLOW}[14] 验证规则删除结果...${NC}"
curl -s -X GET "${BASE_URL}/rules" -H "Content-Type: application/json" | jq .

# 服务健康状态
echo -e "\n${YELLOW}[15] 检查服务健康状态...${NC}"
curl -s -X GET "${BASE_URL}/health" -H "Content-Type: application/json" | jq .

# 清理临时文件
echo -e "\n${YELLOW}[16] 清理临时文件...${NC}"
rm -f ${TEMP_FILE} risk_transactions.json all_transactions.json
echo -e "已删除临时文件"

echo -e "\n${GREEN}=====================================================${NC}"
echo -e "${GREEN}      测试完成! 欺诈检测系统功能验证已完成        ${NC}"
echo -e "${GREEN}=====================================================${NC}" 