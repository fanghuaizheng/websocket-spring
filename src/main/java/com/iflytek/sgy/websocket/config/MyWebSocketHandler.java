package com.iflytek.sgy.websocket.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by woni on 18/5/29.
 */
@Component
public class MyWebSocketHandler implements WebSocketHandler {

    /*生成logger*/
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    //存放会话
    private static ConcurrentHashMap<String,Set<WebSocketSession>> userSessionMap = new ConcurrentHashMap<>();

    //计数
    private static AtomicLong onlineCount = new AtomicLong(0);

    private String getUserId(WebSocketSession session){
        return (String) session.getAttributes().get("userId");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {


       String id = getUserId(webSocketSession);

        //如果包含在表里面，那么直接添加
        if (userSessionMap.contains(id)){
            userSessionMap.get(id).add(webSocketSession);
        }else {
            Set<WebSocketSession> sessions = new HashSet<>();
            sessions.add(webSocketSession);
            userSessionMap.put(id,sessions);
        }
        onlineCount.incrementAndGet();
        logger.info("新建立一个连接,建立的连接的主键是:\t"+id);

        sendMessageToUser(id,new TextMessage("连接建立成功"));



    }

    /**
     * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
     * @param webSocketSession
     * @param webSocketMessage
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        if (webSocketMessage.getPayloadLength()==0){
            return;
        }
        sendMessageToUser(getUserId(webSocketSession),new TextMessage(webSocketMessage.getPayload().toString()));

    }

    /**
     *消息传输错误处理
     * @param webSocketSession
     * @param throwable
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

        if (webSocketSession.isOpen()){
            webSocketSession.close();
        }
        String id = getUserId(webSocketSession);

        Set<WebSocketSession> sessions = userSessionMap.get(id);
        sessions.remove(webSocketSession);
        //如果客户端的连结数0，直接移除
        if (sessions.size()==0){
            userSessionMap.remove(id);
        }
        onlineCount.decrementAndGet();
        logger.info("有一个客户端发送错误，从服务器移除,移除的客户端id:\t"+id);
    }

    /**
     * 关闭连接后
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("Session {} disconnected. Because of {}", webSocketSession.getId(), closeStatus);
        String id = getUserId(webSocketSession);
        Set<WebSocketSession> sessions = userSessionMap.get(id);
        sessions.remove(webSocketSession);
        //如果客户端的连结数0，直接移除
        if (sessions.size()==0){
            userSessionMap.remove(id);
        }
        onlineCount.decrementAndGet();

        logger.info("当前在线用户数: {}"+onlineCount);


    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 广播信息，向全部客户端发送消息推送
     * @param message
     */
    public void broadcast(TextMessage message){
        try {
            Set<WebSocketSession> sessions = new HashSet<>();
            for (Set<WebSocketSession> itemSession:userSessionMap.values()
                    ) {
                sessions.addAll(itemSession);
            }

            sendMessage(sessions,message);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("广播发送消息发生错误,错误的原因：\t"+e.getMessage());
        }
    }

    /**
     * 向指定的客户端发送消息
     * @param id
     * @param message
     */
    public void sendMessageToUser(String id,TextMessage message){
       try {
           if (StringUtils.isNotBlank(id)){
               Set<WebSocketSession> sessions = userSessionMap.get(id);
               sendMessage(sessions,message);
           }else throw new NullPointerException();

       }catch (NullPointerException nullException){
           nullException.printStackTrace();
           logger.info("传入的id不允许为空");
       }catch (Exception e){
           e.printStackTrace();
           logger.info("发送消息失败,失败原因是：\t"+e.getMessage());
       }
    }

    /**
     * 发送消息
     * @param sessions
     * @param message
     */
    private void sendMessage(Set<WebSocketSession> sessions, TextMessage message){
        //如果id不为空，那么指定发送给某人，为空，那么群发


        try {

            for (WebSocketSession session: sessions
                 ) {
                session.sendMessage(message);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("消息发送失败");
        }
    }
}
