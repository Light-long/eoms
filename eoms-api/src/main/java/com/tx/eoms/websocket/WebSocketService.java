package com.tx.eoms.websocket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import javafx.scene.shape.VLineTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ServerEndpoint(value = "/socket")
public class WebSocketService {

    // 用于保存WebSocket连接对象
    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        Map<String, Object> map = session.getUserProperties();
        if (map.containsKey("userId")) {
            String userId = MapUtil.getStr(map, "userId");
            // 删除对应的websocket连接
            sessionMap.remove(userId);
        }
    }

    /**
     * 接收消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 把字符串转成json
        JSONObject json = JSONUtil.parseObj(message);
        String opt = json.getStr("opt");
        if ("ping".equals(opt)) {
            return;
        }
        // 从json中取出token
        String token = json.getStr("token");
        // 从token中取出userId
        String userId = StpUtil.stpLogic.getLoginIdByToken(token).toString();
        // 取出session绑定的属性
        Map<String, Object> map = session.getUserProperties();
        //如果没有userId属性，就给Session绑定userId属性，关闭连接的时候会用到
        if (!map.containsKey("userId")) {
            map.put("userId", userId);
        }
        // 把session缓存起来
        if (sessionMap.containsKey(userId)) {
            sessionMap.replace(userId, session);
        } else {
            sessionMap.put(userId, session);
        }
        // 发送消息
        sendInfo("ok", userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    /**
     * 发送消息给客户端
     */
    public static void sendInfo(String message, String userId) {
        if (StrUtil.isNotBlank(message) && sessionMap.containsKey(userId)) {
            // 从缓存中找到session对象
            Session session = sessionMap.get(userId);
            // 发送消息
            sendMessage(message, session);
        }
    }

    /**
     * 封装发送消息
     */
    private static void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("执行异常", e);
        }
    }
}
