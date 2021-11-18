package com.ruoyi.framework.websocket;



/**
 * Created by IntelliJ IDEA.
 * Project : springboot-webrtc
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 28/12/20
 * Time: 17.40
 */

public class SignalMessage {
    private String type;
    private String sender;
    private String receiver;
    private Object data;

    public SignalMessage(String type, String sender, String receiver, Object data) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }

    public SignalMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
