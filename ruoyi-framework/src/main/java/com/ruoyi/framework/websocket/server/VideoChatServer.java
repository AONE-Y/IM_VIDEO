package com.ruoyi.framework.websocket.server;

import com.ruoyi.system.domain.ChatUser;
import com.ruoyi.system.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi.framework.websocket.server
 * @Date：2021/11/18 17:19
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@ServerEndpoint("/videochat/{userId}")
@Component
public class VideoChatServer {
    @Autowired
    private ChatUserService chatUserService;

    private ChatUser chatUser=new ChatUser();

    private Session session;
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) {

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {


    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session)throws Exception {

    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
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

    }


}
