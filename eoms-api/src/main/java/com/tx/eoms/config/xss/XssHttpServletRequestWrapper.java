package com.tx.eoms.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 过滤xss脚本
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (!StrUtil.isEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null && values.length != 0) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (!StrUtil.isEmpty(value)) {
                    value = HtmlUtil.filter(value);
                }
                values[i] = value;
            }
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        Map<String, String[]> map = new LinkedHashMap<>();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                String[] values = parameters.get(key);
                if (values != null && values.length != 0) {
                    for (int i = 0; i < values.length; i++) {
                        String value = values[i];
                        if (!StrUtil.isEmpty(value)) {
                            value = HtmlUtil.filter(value);
                        }
                        values[i] = value;
                    }
                }
                map.put(key, values);
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!StrUtil.isEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 获取原来的数据流
        ServletInputStream inputStream = super.getInputStream();
        // 转换
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        // 包装
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        // 存储数据
        StringBuffer body = new StringBuffer();
        // 循环读取数据
        String line = bufferedReader.readLine();
        while (line != null) {
            body.append(line);
            line = bufferedReader.readLine();
        }
        // 关闭流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        // 处理数据
        Map<String, Object> map = JSONUtil.parseObj(body.toString());
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof String) {
                if (!StrUtil.isEmpty(value.toString())) {
                    result.put(key, HtmlUtil.filter(value.toString()));
                }
            } else {
                result.put(key, value);
            }
        }
        // map --> json
        String json = JSONUtil.toJsonStr(result);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(json.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}
