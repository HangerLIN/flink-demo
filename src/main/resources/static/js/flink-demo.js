document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM加载完成，开始初始化事件监听");

    // 速度阈值过滤演示
    const speedThresholdForm = document.getElementById('speedThresholdForm');
    const speedThresholdInput = document.getElementById('speedThreshold');
    const speedThresholdResult = document.getElementById('speedThresholdResult');
    const speedThresholdLoading = document.getElementById('speedThresholdLoading');

    if (speedThresholdForm) {
        console.log("找到速度阈值表单，添加提交事件监听器");
        speedThresholdForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const threshold = speedThresholdInput.value;
            
            if (!threshold || isNaN(threshold)) {
                alert('请输入有效的速度阈值');
                return;
            }
            
            // 显示加载状态
            speedThresholdLoading.style.display = 'block';
            speedThresholdResult.innerHTML = '';
            
            // 调用API
            fetch(`/api/flink/table/speed-filter?threshold=${threshold}`)
                .then(response => response.json())
                .then(data => {
                    // 隐藏加载状态
                    speedThresholdLoading.style.display = 'none';
                    
                    // 显示结果
                    let resultHtml = '<h4>查询结果:</h4>';
                    resultHtml += '<table>';
                    resultHtml += '<tr><th>传感器ID</th><th>数据量</th><th>平均速度</th></tr>';
                    
                    // 直接处理返回的DTO列表
                    if (data && data.length > 0) {
                        data.forEach(item => {
                            resultHtml += `<tr><td>${item.sensorId}</td><td>${item.carCount}</td><td>${item.avgSpeed.toFixed(2)}</td></tr>`;
                        });
                    } else {
                        resultHtml += '<tr><td colspan="3">没有符合条件的数据</td></tr>';
                    }
                    
                    resultHtml += '</table>';
                    speedThresholdResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    // 隐藏加载状态
                    speedThresholdLoading.style.display = 'none';
                    speedThresholdResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到速度阈值表单");
    }
    
    // 批处理 - 基本转换
    const batchTransformBtn = document.getElementById('loadBatchTransformBtn');
    const batchTransformLoading = document.getElementById('batchTransformLoading');
    const batchTransformResult = document.getElementById('batchTransformResult');
    
    if (batchTransformBtn) {
        console.log("找到批处理基本转换按钮，添加点击事件监听器");
        batchTransformBtn.addEventListener('click', function() {
            console.log("点击了批处理基本转换按钮");
            batchTransformLoading.style.display = 'block';
            batchTransformResult.innerHTML = '';
            
            fetch('/api/flink/batch/transform')
                .then(response => response.json())
                .then(data => {
                    console.log("收到批处理基本转换数据:", data);
                    batchTransformLoading.style.display = 'none';
                    
                    let resultHtml = '<h4>转换结果:</h4>';
                    if (data && data.result && data.result.length > 0) {
                        resultHtml += '<div class="result-words">';
                        data.result.forEach(word => {
                            resultHtml += `<span class="word-chip">${word}</span>`;
                        });
                        resultHtml += '</div>';
                    } else {
                        resultHtml += '<div class="no-data">没有数据</div>';
                    }
                    
                    batchTransformResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    console.error("批处理基本转换请求错误:", error);
                    batchTransformLoading.style.display = 'none';
                    batchTransformResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到批处理基本转换按钮");
    }
    
    // 批处理 - 分组聚合
    const batchAggregateBtn = document.getElementById('loadBatchAggregateBtn');
    const batchAggregateLoading = document.getElementById('batchAggregateLoading');
    const batchAggregateResult = document.getElementById('batchAggregateResult');
    
    if (batchAggregateBtn) {
        console.log("找到批处理分组聚合按钮，添加点击事件监听器");
        batchAggregateBtn.addEventListener('click', function() {
            console.log("点击了批处理分组聚合按钮");
            batchAggregateLoading.style.display = 'block';
            batchAggregateResult.innerHTML = '';
            
            fetch('/api/flink/batch/aggregate')
                .then(response => response.json())
                .then(data => {
                    console.log("收到批处理分组聚合数据:", data);
                    batchAggregateLoading.style.display = 'none';
                    
                    let resultHtml = '<h4>分组聚合结果:</h4>';
                    if (data && data.result && data.result.length > 0) {
                        resultHtml += '<table>';
                        resultHtml += '<tr><th>产品类别</th><th>销售总额</th></tr>';
                        
                        data.result.forEach(item => {
                            resultHtml += `<tr><td>${item.f0}</td><td>${item.f1}</td></tr>`;
                        });
                        
                        resultHtml += '</table>';
                    } else {
                        resultHtml += '<div class="no-data">没有数据</div>';
                    }
                    
                    batchAggregateResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    console.error("批处理分组聚合请求错误:", error);
                    batchAggregateLoading.style.display = 'none';
                    batchAggregateResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到批处理分组聚合按钮");
    }
    
    // 流处理 - 处理时间窗口
    const processingTimeBtn = document.getElementById('loadProcessingTimeBtn');
    const processingTimeLoading = document.getElementById('processingTimeLoading');
    const processingTimeResult = document.getElementById('processingTimeResult');
    
    if (processingTimeBtn) {
        console.log("找到处理时间窗口按钮，添加点击事件监听器");
        processingTimeBtn.addEventListener('click', function() {
            console.log("点击了处理时间窗口按钮");
            processingTimeLoading.style.display = 'block';
            processingTimeResult.innerHTML = '';
            
            fetch('/api/flink/stream/processing-time')
                .then(response => response.json())
                .then(data => {
                    console.log("收到处理时间窗口数据:", data);
                    processingTimeLoading.style.display = 'none';
                    
                    let resultHtml = '<h4>处理时间窗口结果:</h4>';
                    if (data && data.result && data.result.length > 0) {
                        resultHtml += '<table>';
                        resultHtml += '<tr><th>传感器ID</th><th>车流量</th></tr>';
                        
                        data.result.forEach(item => {
                            resultHtml += `<tr><td>${item.f0}</td><td>${item.f1}</td></tr>`;
                        });
                        
                        resultHtml += '</table>';
                    } else {
                        resultHtml += '<div class="no-data">没有数据</div>';
                    }
                    
                    processingTimeResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    console.error("处理时间窗口请求错误:", error);
                    processingTimeLoading.style.display = 'none';
                    processingTimeResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到处理时间窗口按钮");
    }
    
    // Table API演示
    const tableApiBtn = document.getElementById('loadTableApiBtn');
    const tableApiLoading = document.getElementById('tableApiLoading');
    const tableApiResult = document.getElementById('tableApiResult');
    
    if (tableApiBtn) {
        console.log("找到Table API按钮，添加点击事件监听器");
        tableApiBtn.addEventListener('click', function() {
            console.log("点击了Table API按钮");
            tableApiLoading.style.display = 'block';
            tableApiResult.innerHTML = '';
            
            fetch('/api/flink/table/api')
                .then(response => response.json())
                .then(data => {
                    console.log("收到Table API数据:", data);
                    tableApiLoading.style.display = 'none';
                    
                    let resultHtml = '<h4>Table API结果:</h4>';
                    if (data && data.result && data.result.length > 0) {
                        resultHtml += '<table>';
                        resultHtml += '<tr><th>传感器ID</th><th>平均速度</th></tr>';
                        
                        data.result.forEach(item => {
                            resultHtml += `<tr><td>${item.f0}</td><td>${item.f1.toFixed(2)}</td></tr>`;
                        });
                        
                        resultHtml += '</table>';
                    } else {
                        resultHtml += '<div class="no-data">没有数据</div>';
                    }
                    
                    tableApiResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    console.error("Table API请求错误:", error);
                    tableApiLoading.style.display = 'none';
                    tableApiResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到Table API按钮");
    }
    
    // Table API和SQL结合演示
    const combinedBtn = document.getElementById('loadCombinedBtn');
    const combinedLoading = document.getElementById('combinedLoading');
    const combinedResult = document.getElementById('combinedResult');
    
    if (combinedBtn) {
        console.log("找到Table API和SQL结合按钮，添加点击事件监听器");
        combinedBtn.addEventListener('click', function() {
            console.log("点击了Table API和SQL结合按钮");
            combinedLoading.style.display = 'block';
            combinedResult.innerHTML = '';
            
            fetch('/api/flink/table/combined')
                .then(response => response.json())
                .then(data => {
                    console.log("收到Table API和SQL结合数据:", data);
                    combinedLoading.style.display = 'none';
                    
                    let resultHtml = '<h4>Table API与SQL结合结果:</h4>';
                    if (data && data.result) {
                        resultHtml += '<p>过滤条件: 速度 >= 60 km/h</p>';
                        resultHtml += '<table>';
                        resultHtml += '<tr><th>传感器ID</th><th>数据量</th><th>平均速度</th></tr>';
                        
                        try {
                            // 尝试提取Row对象的值
                            const resultStr = data.result;
                            // 解析类似 "[+I[1, 15, 75.0], +I[2, 8, 61.25], ...]" 的字符串
                            const matches = resultStr.match(/\+I\[([^\]]+)\]/g);
                            
                            if (matches && matches.length > 0) {
                                matches.forEach(match => {
                                    // 去掉 "+I[" 和 "]"
                                    const content = match.substring(3, match.length - 1);
                                    // 分割字段值
                                    const values = content.split(', ');
                                    if (values.length >= 3) {
                                        resultHtml += `<tr><td>${values[0]}</td><td>${values[1]}</td><td>${parseFloat(values[2]).toFixed(2)}</td></tr>`;
                                    }
                                });
                            } else {
                                resultHtml += '<tr><td colspan="3">解析结果出错</td></tr>';
                            }
                        } catch (e) {
                            console.error("解析Table API和SQL结合数据出错:", e);
                            resultHtml += `<tr><td colspan="3">解析结果出错: ${e.message}</td></tr>`;
                        }
                        
                        resultHtml += '</table>';
                    } else {
                        resultHtml += '<div class="no-data">没有数据</div>';
                    }
                    
                    combinedResult.innerHTML = resultHtml;
                })
                .catch(error => {
                    console.error("Table API和SQL结合请求错误:", error);
                    combinedLoading.style.display = 'none';
                    combinedResult.innerHTML = `<div class="error">请求出错: ${error.message}</div>`;
                });
        });
    } else {
        console.log("未找到Table API和SQL结合按钮");
    }
}); 