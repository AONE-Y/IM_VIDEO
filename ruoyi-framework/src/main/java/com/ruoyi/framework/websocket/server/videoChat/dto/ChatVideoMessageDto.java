package com.ruoyi.framework.websocket.server.videoChat.dto;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi.framework.websocket.server.videoChat.dto
 * @Date：2021/11/19 16:45
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
public class ChatVideoMessageDto {
    private String type;
    private String sender;
    private String receiver;
    private Object data;

    public ChatVideoMessageDto(String type, String sender, String receiver, Object data) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }

    public ChatVideoMessageDto() {
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
