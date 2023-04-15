package com.xshiwu.component;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/websocket/{userId}")
@Component
public class WebSocket {
    private static final Logger logger = LogManager.getLogger(WebSocket.class);

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象
     * userId为key，value为MyWebSocket对象
     */
    private static Map<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */

    private Session session;
    private String userId;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        //加入map
        webSocketMap.put(userId, this);
        addOnlineCount();           //在线数加1
        logger.info("用户{}连接成功,当前在线人数为{}", userId, getOnlineCount());
        try {
            sendMessage(String.format("用户%s连接成功", userId));
            // sendMessage(String.valueOf(this.session.getQueryString()));
        } catch (IOException e) {
            logger.error("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从map中删除
        webSocketMap.remove(userId);
        subOnlineCount();           //在线数减1
        logger.info("用户{}关闭连接！当前在线人数为{}", userId, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("来自客户端用户：{} 消息:{}", userId, message);
        Map map = JSON.parseObject(message, Map.class);
        String otherUserId = (String) map.get("userId");
        if (CharSequenceUtil.isNotBlank(otherUserId) && webSocketMap.containsKey(otherUserId)) {
            WebSocket webSocket = webSocketMap.get(otherUserId);
            Session otherSession = webSocket.session;
            webSocket.sendMessage(message, otherSession);
        } else {
            logger.error("用户{}不在线", otherUserId);
        }
        //群发消息
//        for (String item : webSocketMap.keySet()) {
//            try {
//                webSocketMap.get(item).sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("用户错误: {} ,原因: {}", this.userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 向客户端发送消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        // this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 向客户端发送消息
     */
    public void sendMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
        // this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 通过userId向客户端发送消息
     */
    public void sendMessageByUserId(String userId, String message) throws IOException {
        logger.info("服务端发送消息到{},消息：{}", userId, message);
        if (CharSequenceUtil.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            logger.error("用户{}不在线", userId);
        }

    }

    /**
     * 群发自定义消息
     */
    public void sendMessageForAll(String message) {
        logger.info("服务端发送消息到所有用户，消息：{}", message);
        webSocketMap.forEach((key, value) -> {
            try {
                value.sendMessage(message);
            } catch (IOException e) {
                logger.error("群发消息异常");
            }
        });
    }

    /**
     * 获取所有在线用户
     */
    public static int getOnlineCount() {
        return WebSocket.webSocketMap.size();
    }

    /**
     * 递增在线用户
     */
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount.incrementAndGet();
    }

    /**
     * 修改总在线用户，下线减一
     */
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount.decrementAndGet();
    }

}
