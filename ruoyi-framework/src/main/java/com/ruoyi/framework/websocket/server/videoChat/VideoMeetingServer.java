package com.ruoyi.framework.websocket.server.videoChat;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.framework.websocket.server.videoChat.dto.ChatVideoMessageDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi.framework.websocket.server
 * @Date：2021/11/18 17:19
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@ServerEndpoint("/meeting/{room}/{userName}")
@Component
public class VideoMeetingServer {
    static Logger log = LoggerFactory.getLogger("websocket");
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, ConcurrentHashMap<String,VideoMeetingServer>> connectedUsers = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userName
     */
    private String userName;
    private String room;
    private static final String TYPE_INIT = "init";
    private static final String TYPE_LOGOUT = "logout";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("room") String room,@PathParam("userName") String userName) {
        this.session = session;
        this.userName = userName;
        this.room = room;
        final ChatVideoMessageDto userInit = new ChatVideoMessageDto();
        userInit.setType(TYPE_INIT);
        userInit.setSender(userName);
        ConcurrentHashMap<String, VideoMeetingServer> roomName = connectedUsers.get(room);
        if (roomName == null) {
            roomName = new ConcurrentHashMap<>();
            roomName.put(userName,this);
        }else {
            roomName.values().forEach(webSocketSession -> {
                try {
                    webSocketSession.sendMessage(JSONObject.toJSONString(userInit));
                } catch (Exception e) {
                    log.warn("Error while message sending.", e);
                }
            });
            roomName.put(userName,this);

        }
        connectedUsers.put(room, roomName);
        

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除

        ConcurrentHashMap<String, VideoMeetingServer> roomName = connectedUsers.get(this.room);

        final ChatVideoMessageDto userOut = new ChatVideoMessageDto();
        userOut.setType(TYPE_LOGOUT);
        userOut.setSender(userName);
        if (roomName != null) {
            roomName.remove(this.userName);
            roomName.values().forEach(webSocket -> {
                try {
                    webSocket.sendMessage(JSONObject.toJSONString(userOut));
                } catch (Exception e) {
                    log.warn("Error while message sending.", e);
                }
            });
        }
        if (roomName.isEmpty()||roomName==null) {
            connectedUsers.remove(this.room);
        }

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        ChatVideoMessageDto userMessage = JSONObject.parseObject(message, ChatVideoMessageDto.class);
        String destinationUser = userMessage.getReceiver();
        VideoMeetingServer destSocket = connectedUsers.get(this.room).get(destinationUser);
        if (destSocket != null && destSocket.session.isOpen()) {
            userMessage.setSender(userName);
            final String resendingMessage = JSONObject.toJSONString(userMessage);
            log.info("send message {} to {}", resendingMessage, destinationUser);
            destSocket.sendMessage(resendingMessage);
        }

    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        ConcurrentHashMap<String, VideoMeetingServer> roomName = connectedUsers.get(this.room);
        if (roomName != null) {
            roomName.remove(this.userName);
        }
        log.error("用户错误:" + this.userName + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        synchronized (this.session) {
            this.session.getBasicRemote().sendText(message);
        }

    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message,@PathParam("room") String room, @PathParam("userId") String userId) throws IOException {
        if (StringUtils.isNotBlank(userId) && connectedUsers.containsKey(userId)) {
            connectedUsers.get(room).get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

}
