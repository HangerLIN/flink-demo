#!/bin/bash

# 彻底简化的测试
echo "测试欺诈检测接口(极简模式)"
echo '{"userId":"user1","transactionId":"t1","amount":1000,"type":"TRANSFER","channel":"WEB","ipAddress":"1.1.1.1","location":"Test","deviceId":"d1","timestamp":"2025-03-22T15:00:00","riskScore":50}' > one_tx.json
echo '[' > single_tx.json
cat one_tx.json >> single_tx.json
echo ']' >> single_tx.json

echo "发送请求..."
curl -s -m 3 -X POST "http://localhost:8090/api/fraud/detect" -H "Content-Type: application/json" -d @single_tx.json

if [ $? -eq 0 ]; then
    echo "请求成功"
else
    echo "请求超时或失败，退出码: $?"
fi 