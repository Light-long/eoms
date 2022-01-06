package com.tx.eoms.util;

import org.apache.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

public class CommonResult extends HashMap<String, Object> {

    public CommonResult() {
        put("code", HttpStatus.SC_OK);
        put("message", "success");
    }

    // 方便链式调用
    public CommonResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static CommonResult ok(){
        return new CommonResult();
    }

    public static CommonResult ok(String message){
        CommonResult result = new CommonResult();
        result.put("message", message);
        return result;
    }

    public static CommonResult ok(Map<String, Object> map){
        CommonResult result = new CommonResult();
        result.putAll(map);
        return result;
    }

    public static CommonResult error(int code, String message){
        CommonResult result = new CommonResult();
        result.put("code", code);
        result.put("message", message);
        return result;
    }

    public static CommonResult error(String message) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
    }

    public static CommonResult error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员！");
    }
}
