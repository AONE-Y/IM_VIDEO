package com.ruoyi.framework.websocket;



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
 * @Project：websocket-try
 * @Package：com.example.websockettry.server
 * @Date：2021/9/27 15:40
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@ServerEndpoint("/imserver/{userId}")
@Component
public class WebSocketServer {
    static Logger log=  LoggerFactory.getLogger("websocket");;
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketServer> connectedUsers = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";
    private static final String TYPE_INIT = "init";
    private static final String TYPE_LOGOUT = "logout";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) {
        this.session = session;
        this.userId=userId;
        log.info("[" + session.getId() + "] Connection established " + session.getId());

        // send the message to all other peers, that new men its being registered
        final SignalMessage newMenOnBoard = new SignalMessage();
        newMenOnBoard.setType(TYPE_INIT);
        newMenOnBoard.setSender(userId);

        connectedUsers.values().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(Utils.getString(newMenOnBoard));
            } catch (Exception e) {
                log.warn("Error while message sending.", e);
            }
        });

        connectedUsers.put(userId, this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(connectedUsers.containsKey(userId)){
            connectedUsers.remove(userId);
            //从set中删除

        }
        final SignalMessage menOut = new SignalMessage();
        menOut.setType(TYPE_LOGOUT);
        menOut.setSender(userId);

        connectedUsers.values().forEach(webSocket -> {
            try {
                webSocket.sendMessage(Utils.getString(menOut));
            } catch (Exception e) {
                log.warn("Error while message sending.", e);
            }
        });

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session)throws Exception {
        log.info("用户消息:"+userId+",报文:"+message);

        SignalMessage signalMessage = Utils.getObject(message);
        // with the destinationUser find the targeted socket, if any
        String destinationUser = signalMessage.getReceiver();
        WebSocketServer destSocket = connectedUsers.get(destinationUser);
        // if the socket exists and is open, we go on
        if (destSocket != null && destSocket.session.isOpen()) {
            // set the sender as current sessionId.
            signalMessage.setSender(userId);
            final String resendingMessage = Utils.getString(signalMessage);
            log.info("send message {} to {}", resendingMessage, destinationUser);
            destSocket.sendMessage(resendingMessage);
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(StringUtils.isNotBlank(userId)&&connectedUsers.containsKey(userId)){
            connectedUsers.get(userId).sendMessage(message);
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
