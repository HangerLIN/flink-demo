#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 应用JAR文件路径
JAR_FILE="target/flink-springboot-demo-0.0.1-SNAPSHOT.jar"
APP_PORT=8090

# 打印分隔线
print_separator() {
  echo -e "${BLUE}===================================================${NC}"
}

# 检查应用是否正在运行
check_app_running() {
  # 先尝试使用lsof命令
  pid=$(lsof -i :$APP_PORT -t 2>/dev/null)
  if [ -n "$pid" ]; then
    return 0  # 应用正在运行
  fi
  
  # 如果lsof命令没有返回结果，尝试使用netstat
  netstat_result=$(netstat -anp 2>/dev/null | grep ":$APP_PORT " | grep "LISTEN")
  if [ -n "$netstat_result" ]; then
    return 0  # 应用正在运行
  fi
  
  # 最后尝试使用curl命令
  if curl -s http://localhost:$APP_PORT >/dev/null 2>&1; then
    return 0  # 应用可以访问
  fi
  
  return 1  # 应用未运行
}

# 启动应用
start_app() {
  print_separator
  echo -e "${YELLOW}准备启动Flink SpringBoot应用...${NC}"
  
  # 先检查应用是否已经在运行
  if check_app_running; then
    echo -e "${RED}应用已经在运行，PID: $(lsof -i :$APP_PORT -t)${NC}"
    echo -e "${YELLOW}如需重启应用，请先停止当前运行的实例${NC}"
    return 1
  fi
  
  # 检查JAR文件是否存在
  if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}错误: 未找到JAR文件: $JAR_FILE${NC}"
    echo -e "${YELLOW}请先构建应用程序${NC}"
    return 1
  fi
  
  # 启动应用
  echo -e "${YELLOW}启动中...${NC}"
  nohup java -jar $JAR_FILE > app.log 2>&1 &
  
  # 等待应用启动
  echo -e "${YELLOW}等待应用启动...${NC}"
  
  # 修改: 增加等待时间，并使用循环检查多次
  max_attempts=12
  attempts=0
  success=false
  
  while [ $attempts -lt $max_attempts ]; do
    sleep 5
    attempts=$((attempts+1))
    
    if curl -s http://localhost:$APP_PORT >/dev/null; then
      success=true
      break
    fi
    
    echo -e "${YELLOW}等待应用启动中... ($attempts/$max_attempts)${NC}"
  done
  
  # 检查应用是否成功启动
  if [ "$success" = true ] || check_app_running; then
    echo -e "${GREEN}应用成功启动，PID: $(lsof -i :$APP_PORT -t)${NC}"
    echo -e "${GREEN}日志文件: $(pwd)/app.log${NC}"
    return 0
  else
    echo -e "${RED}应用启动失败，请检查日志文件: $(pwd)/app.log${NC}"
    return 1
  fi
}

# 停止应用
stop_app() {
  print_separator
  echo -e "${YELLOW}准备停止Flink SpringBoot应用...${NC}"
  
  # 检查应用是否在运行
  if ! check_app_running; then
    echo -e "${YELLOW}应用未运行${NC}"
    return 0
  fi
  
  # 获取PID并停止应用
  pid=$(lsof -i :$APP_PORT -t)
  echo -e "${YELLOW}正在停止应用，PID: $pid${NC}"
  
  kill -15 $pid
  sleep 2
  
  # 检查应用是否已停止
  if check_app_running; then
    echo -e "${RED}应用未能正常停止，尝试强制终止${NC}"
    kill -9 $pid
    sleep 1
  fi
  
  # 再次检查应用是否已停止
  if check_app_running; then
    echo -e "${RED}无法停止应用，请手动检查${NC}"
    return 1
  else
    echo -e "${GREEN}应用已成功停止${NC}"
    return 0
  fi
}

# 显示应用状态
show_status() {
  print_separator
  echo -e "${YELLOW}Flink SpringBoot应用状态:${NC}"
  
  if check_app_running; then
    pid=$(lsof -i :$APP_PORT -t)
    echo -e "${GREEN}应用正在运行${NC}"
    echo -e "${YELLOW}PID: ${NC}$pid"
    echo -e "${YELLOW}端口: ${NC}$APP_PORT"
    echo -e "${YELLOW}进程信息: ${NC}"
    ps -p $pid -o pid,ppid,user,%cpu,%mem,vsz,rss,tt,stat,start,time,command
  else
    echo -e "${RED}应用未运行${NC}"
  fi
}

# 显示帮助信息
show_help() {
  print_separator
  echo -e "${GREEN}Flink SpringBoot应用管理脚本${NC}"
  echo ""
  echo -e "${YELLOW}用法:${NC}"
  echo -e "  $0 ${GREEN}start${NC}    启动应用"
  echo -e "  $0 ${GREEN}stop${NC}     停止应用"
  echo -e "  $0 ${GREEN}restart${NC}  重启应用"
  echo -e "  $0 ${GREEN}status${NC}   显示应用状态"
  echo -e "  $0 ${GREEN}help${NC}     显示此帮助信息"
}

# 主逻辑
case "$1" in
  start)
    start_app
    ;;
  stop)
    stop_app
    ;;
  restart)
    stop_app && start_app
    ;;
  status)
    show_status
    ;;
  help)
    show_help
    ;;
  *)
    show_help
    exit 1
    ;;
esac 