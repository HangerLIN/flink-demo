document.addEventListener('DOMContentLoaded', function() {
    // 公共图表主题设置
    const chartTheme = {
        color: ['#0070E0', '#91CC75', '#FAC858', '#EE6666', '#73C0DE'],
        backgroundColor: '#ffffff',
        textStyle: {
            fontFamily: 'Arial, sans-serif'
        },
        title: {
            textStyle: {
                fontSize: 16,
                fontWeight: 'normal'
            },
            left: 'center'
        },
        tooltip: {
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            borderColor: '#ccc',
            borderWidth: 1,
            textStyle: {
                color: '#333'
            },
            axisPointer: {
                type: 'shadow',
                shadowStyle: {
                    color: 'rgba(0, 112, 224, 0.1)'
                }
            }
        }
    };
    
    // 初始化ECharts图表
    const wordCountChart = echarts.init(document.getElementById('wordCountChart'));
    const eventTimeChart = echarts.init(document.getElementById('eventTimeChart'));
    const sqlChart = echarts.init(document.getElementById('sqlChart'));
    
    // 窗口大小调整时重置图表大小
    window.addEventListener('resize', function() {
        wordCountChart.resize();
        eventTimeChart.resize();
        sqlChart.resize();
    });
    
    // 批处理 - 单词计数
    const wordCountBtn = document.getElementById('loadWordCountBtn');
    const wordCountLoading = document.getElementById('wordCountLoading');
    
    if (wordCountBtn) {
        wordCountBtn.addEventListener('click', function() {
            wordCountLoading.style.display = 'block';
            
            fetch('/api/flink/batch/wordcount')
                .then(response => response.json())
                .then(data => {
                    wordCountLoading.style.display = 'none';
                    
                    if (data && data.result && data.result.length > 0) {
                        // 数据处理和排序
                        const sortedData = [...data.result].sort((a, b) => b.f1 - a.f1).slice(0, 20);
                        const words = sortedData.map(item => item.f0);
                        const counts = sortedData.map(item => item.f1);
                        
                        // 设置图表配置
                        const option = {
                            ...chartTheme,
                            title: {
                                ...chartTheme.title,
                                text: '单词出现频率 (Top 20)'
                            },
                            tooltip: {
                                ...chartTheme.tooltip,
                                trigger: 'axis'
                            },
                            grid: {
                                left: '3%',
                                right: '4%',
                                bottom: '15%',
                                top: '15%',
                                containLabel: true
                            },
                            xAxis: {
                                type: 'category',
                                data: words,
                                axisLabel: {
                                    rotate: 45,
                                    interval: 0,
                                    fontSize: 10
                                }
                            },
                            yAxis: {
                                type: 'value',
                                name: '出现次数'
                            },
                            series: [{
                                name: '次数',
                                type: 'bar',
                                data: counts,
                                barWidth: '60%',
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        {offset: 0, color: '#83bff6'},
                                        {offset: 0.5, color: '#188df0'},
                                        {offset: 1, color: '#188df0'}
                                    ])
                                },
                                emphasis: {
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                            {offset: 0, color: '#71a5dc'},
                                            {offset: 0.7, color: '#1082e1'},
                                            {offset: 1, color: '#0275d8'}
                                        ])
                                    }
                                }
                            }]
                        };
                        
                        // 渲染图表
                        wordCountChart.setOption(option);
                    } else {
                        wordCountChart.innerHTML = '无数据';
                    }
                })
                .catch(error => {
                    wordCountLoading.style.display = 'none';
                    console.error('加载单词计数数据出错:', error);
                });
        });
    }
    
    // 流处理 - 事件时间窗口
    const eventTimeBtn = document.getElementById('loadEventTimeBtn');
    const eventTimeLoading = document.getElementById('eventTimeLoading');
    
    if (eventTimeBtn) {
        eventTimeBtn.addEventListener('click', function() {
            eventTimeLoading.style.display = 'block';
            
            fetch('/api/flink/stream/event-time')
                .then(response => response.json())
                .then(data => {
                    eventTimeLoading.style.display = 'none';
                    
                    if (data && data.result && data.result.length > 0) {
                        // 数据处理
                        const sensors = [];
                        const carCounts = [];
                        
                        data.result.forEach(item => {
                            sensors.push('传感器 ' + item.f0);
                            carCounts.push(item.f1);
                        });
                        
                        // 设置图表配置
                        const option = {
                            ...chartTheme,
                            title: {
                                ...chartTheme.title,
                                text: '事件时间窗口下的传感器车流量'
                            },
                            tooltip: {
                                ...chartTheme.tooltip,
                                trigger: 'axis'
                            },
                            grid: {
                                left: '3%',
                                right: '4%',
                                bottom: '10%',
                                top: '15%',
                                containLabel: true
                            },
                            xAxis: {
                                type: 'category',
                                data: sensors,
                                axisLabel: {
                                    interval: 0,
                                    fontSize: 12
                                }
                            },
                            yAxis: {
                                type: 'value',
                                name: '车流量'
                            },
                            series: [{
                                name: '车流量',
                                type: 'bar',
                                data: carCounts,
                                barWidth: '60%',
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        {offset: 0, color: '#91cc75'},
                                        {offset: 0.5, color: '#7db964'},
                                        {offset: 1, color: '#5ca743'}
                                    ])
                                },
                                label: {
                                    show: true,
                                    position: 'top',
                                    fontSize: 14,
                                    fontWeight: 'bold'
                                },
                                emphasis: {
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                            {offset: 0, color: '#7db964'},
                                            {offset: 0.7, color: '#5ca743'},
                                            {offset: 1, color: '#4a9734'}
                                        ])
                                    }
                                }
                            }]
                        };
                        
                        // 渲染图表
                        eventTimeChart.setOption(option);
                    } else {
                        console.log('事件时间窗口数据为空');
                    }
                })
                .catch(error => {
                    eventTimeLoading.style.display = 'none';
                    console.error('加载事件时间窗口数据出错:', error);
                });
        });
    }
    
    // SQL查询结果
    const sqlBtn = document.getElementById('loadSqlBtn');
    const sqlLoading = document.getElementById('sqlLoading');
    
    if (sqlBtn) {
        sqlBtn.addEventListener('click', function() {
            sqlLoading.style.display = 'block';
            
            fetch('/api/flink/table/sql')
                .then(response => response.json())
                .then(data => {
                    sqlLoading.style.display = 'none';
                    
                    if (data && data.result) {
                        // SQL结果是一个字符串，我们需要解析它
                        let resultData = [];
                        try {
                            // 尝试提取Row对象的值
                            const resultStr = data.result;
                            // 解析类似 "[+I[1, 15, 75.0], +I[2, 8, 61.25], ...]" 的字符串
                            const matches = resultStr.match(/\+I\[([^\]]+)\]/g);
                            
                            if (matches) {
                                resultData = matches.map(match => {
                                    // 去掉 "+I[" 和 "]"
                                    const content = match.substring(3, match.length - 1);
                                    // 分割字段值
                                    const values = content.split(', ');
                                    if (values.length >= 3) {
                                        return {
                                            sensorId: parseInt(values[0]),
                                            count: parseInt(values[1]),
                                            avgSpeed: parseFloat(values[2])
                                        };
                                    }
                                    return null;
                                }).filter(item => item !== null);
                            }
                        } catch (e) {
                            console.error('解析SQL结果出错:', e);
                        }
                        
                        if (resultData.length > 0) {
                            // 准备图表数据
                            const sensors = resultData.map(item => '传感器 ' + item.sensorId);
                            const counts = resultData.map(item => item.count);
                            const avgSpeeds = resultData.map(item => item.avgSpeed);
                            
                            // 设置图表配置 - 双Y轴
                            const option = {
                                ...chartTheme,
                                title: {
                                    ...chartTheme.title,
                                    text: 'SQL查询结果 - 车流量与平均速度'
                                },
                                tooltip: {
                                    ...chartTheme.tooltip,
                                    trigger: 'axis',
                                    axisPointer: {
                                        type: 'cross'
                                    }
                                },
                                grid: {
                                    left: '3%',
                                    right: '8%',
                                    bottom: '10%',
                                    top: '25%',
                                    containLabel: true
                                },
                                legend: {
                                    data: ['车流量', '平均速度'],
                                    top: 30
                                },
                                xAxis: {
                                    type: 'category',
                                    data: sensors,
                                    axisLabel: {
                                        interval: 0,
                                        fontSize: 12
                                    }
                                },
                                yAxis: [
                                    {
                                        type: 'value',
                                        name: '车流量',
                                        position: 'left',
                                        axisLine: {
                                            show: true,
                                            lineStyle: {
                                                color: '#0070E0'
                                            }
                                        }
                                    },
                                    {
                                        type: 'value',
                                        name: '平均速度 (km/h)',
                                        position: 'right',
                                        axisLine: {
                                            show: true,
                                            lineStyle: {
                                                color: '#FAC858'
                                            }
                                        }
                                    }
                                ],
                                series: [
                                    {
                                        name: '车流量',
                                        type: 'bar',
                                        data: counts,
                                        yAxisIndex: 0,
                                        barWidth: '40%',
                                        itemStyle: {
                                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                                {offset: 0, color: '#83bff6'},
                                                {offset: 0.5, color: '#188df0'},
                                                {offset: 1, color: '#188df0'}
                                            ])
                                        }
                                    },
                                    {
                                        name: '平均速度',
                                        type: 'line',
                                        symbol: 'circle',
                                        symbolSize: 8,
                                        yAxisIndex: 1,
                                        data: avgSpeeds,
                                        itemStyle: {
                                            color: '#FAC858'
                                        },
                                        lineStyle: {
                                            width: 3,
                                            shadowColor: 'rgba(0, 0, 0, 0.3)',
                                            shadowBlur: 10,
                                            shadowOffsetY: 8
                                        },
                                        emphasis: {
                                            itemStyle: {
                                                borderWidth: 3,
                                                borderColor: '#fff',
                                                shadowBlur: 10,
                                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                                            }
                                        },
                                        smooth: true
                                    }
                                ]
                            };
                            
                            // 渲染图表
                            sqlChart.setOption(option);
                        } else {
                            console.log('SQL查询结果解析后为空');
                        }
                    } else {
                        console.log('SQL查询结果为空');
                    }
                })
                .catch(error => {
                    sqlLoading.style.display = 'none';
                    console.error('加载SQL查询数据出错:', error);
                });
        });
    }
}); 