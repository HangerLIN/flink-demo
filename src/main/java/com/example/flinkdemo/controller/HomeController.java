package com.example.flinkdemo.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    /**
     * 首页 - 重定向到静态HTML
     */
    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("/index.html");
    }
    
    /**
     * 修复按钮ID页面
     */
    @GetMapping(value = "/fix-buttons", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String fixButtons() {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>修复按钮 ID</title>\n" +
               "    <script>\n" +
               "        // 这个脚本用于替换按钮ID，使其与JavaScript中的选择器匹配\n" +
               "        document.addEventListener('DOMContentLoaded', function() {\n" +
               "            // 先检查是否有需要返回的URL\n" +
               "            const returnUrl = localStorage.getItem('returnUrl');\n" +
               "            \n" +
               "            // 如果按钮已修复过，直接返回首页\n" +
               "            if (localStorage.getItem('buttonsFixed')) {\n" +
               "                if (returnUrl) {\n" +
               "                    window.location.href = returnUrl;\n" +
               "                } else {\n" +
               "                    window.location.href = '/';\n" +
               "                }\n" +
               "                return;\n" +
               "            }\n" +
               "            \n" +
               "            // 加载主页内容\n" +
               "            fetch('/')\n" +
               "                .then(response => response.text())\n" +
               "                .then(html => {\n" +
               "                    // 创建临时DOM以处理HTML\n" +
               "                    const tempDiv = document.createElement('div');\n" +
               "                    tempDiv.innerHTML = html;\n" +
               "                    \n" +
               "                    // 获取页面中所有的按钮\n" +
               "                    updateButton(tempDiv, '批处理 - 基本转换', 'loadBatchTransformBtn');\n" +
               "                    updateButton(tempDiv, '批处理 - 分组聚合', 'loadBatchAggregateBtn');\n" +
               "                    updateButton(tempDiv, '流处理 - 处理时间窗口', 'loadProcessingTimeBtn');\n" +
               "                    updateButton(tempDiv, 'Table API 演示', 'loadTableApiBtn');\n" +
               "                    updateButton(tempDiv, 'Table API与SQL结合', 'loadCombinedBtn');\n" +
               "                    \n" +
               "                    // 设置已修复标记\n" +
               "                    localStorage.setItem('buttonsFixed', 'true');\n" +
               "                    \n" +
               "                    // 显示成功消息\n" +
               "                    document.getElementById('status').textContent = '按钮ID修复成功！';\n" +
               "                    document.getElementById('status').className = 'success';\n" +
               "                    \n" +
               "                    // 延迟后返回首页\n" +
               "                    setTimeout(function() {\n" +
               "                        if (returnUrl) {\n" +
               "                            window.location.href = returnUrl;\n" +
               "                        } else {\n" +
               "                            window.location.href = '/';\n" +
               "                        }\n" +
               "                    }, 1500);\n" +
               "                })\n" +
               "                .catch(error => {\n" +
               "                    console.error('加载首页失败:', error);\n" +
               "                    document.getElementById('status').textContent = '修复失败: ' + error.message;\n" +
               "                    document.getElementById('status').className = 'error';\n" +
               "                });\n" +
               "        });\n" +
               "        \n" +
               "        function updateButton(container, sectionTitle, btnId) {\n" +
               "            // 找到含有特定标题的section\n" +
               "            const sections = container.querySelectorAll('.api-section h3');\n" +
               "            for (let i = 0; i < sections.length; i++) {\n" +
               "                if (sections[i].textContent.trim() === sectionTitle) {\n" +
               "                    const section = sections[i].closest('.api-section');\n" +
               "                    const btn = section.querySelector('.btn');\n" +
               "                    if (btn) {\n" +
               "                        console.log(`正在更新按钮: \"${sectionTitle}\" 的ID为 \"${btnId}\"`);\n" +
               "                        btn.id = btnId;\n" +
               "                    }\n" +
               "                }\n" +
               "            }\n" +
               "        }\n" +
               "    </script>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: Arial, sans-serif;\n" +
               "            max-width: 600px;\n" +
               "            margin: 0 auto;\n" +
               "            padding: 20px;\n" +
               "            text-align: center;\n" +
               "        }\n" +
               "        h1 {\n" +
               "            color: #0070E0;\n" +
               "        }\n" +
               "        .success {\n" +
               "            color: green;\n" +
               "            font-weight: bold;\n" +
               "            padding: 10px;\n" +
               "            margin-top: 20px;\n" +
               "        }\n" +
               "        .error {\n" +
               "            color: red;\n" +
               "            font-weight: bold;\n" +
               "            padding: 10px;\n" +
               "            margin-top: 20px;\n" +
               "        }\n" +
               "        button {\n" +
               "            padding: 10px 16px;\n" +
               "            background-color: #0070E0;\n" +
               "            color: white;\n" +
               "            border: none;\n" +
               "            border-radius: 4px;\n" +
               "            cursor: pointer;\n" +
               "            transition: background-color 0.3s;\n" +
               "            font-weight: bold;\n" +
               "            margin-top: 20px;\n" +
               "        }\n" +
               "        button:hover {\n" +
               "            background-color: #0060C0;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <h1>修复按钮 ID</h1>\n" +
               "    <p>此页面用于修复Flink演示应用中的按钮ID。</p>\n" +
               "    <p>系统正在自动修复页面中的按钮ID，完成后将自动返回首页...</p>\n" +
               "    \n" +
               "    <div id=\"status\">正在修复按钮ID...</div>\n" +
               "    \n" +
               "    <div>\n" +
               "        <button onclick=\"window.location.href='/'\">手动返回首页</button>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
} 