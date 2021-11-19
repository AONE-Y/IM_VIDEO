package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi.system.domain
 * @Date：2021/11/19 16:50
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@TableName(value = "IMCHAT.chat_user")
public class ChatUser {
    public static final String COL_USER_ID = "user_id";
    /**
     * 聊天id
     */
    @TableId(value = "chat_id", type = IdType.AUTO)
    private Long chatId;

    /**
     * 用户账号
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 音视频的相关参数
     */
    @TableField(value = "user_sdp")
    private String userSdp;

    /**
     * Client 端的 IP 地址信息
     */
    @TableField(value = "user_candidate")
    private String userCandidate;

    /**
     * client端连接时间
     */
    @TableField(value = "connect_time")
    private LocalDateTime connectTime;

    public static final String COL_CHAT_ID = "chat_id";

    public static final String COL_USER_NAME = "user_name";

    public static final String COL_USER_SDP = "user_sdp";

    public static final String COL_USER_CANDIDATE = "user_candidate";

    public static final String COL_CONNECT_TIME = "connect_time";

    /**
     * 获取聊天id
     *
     * @return chat_id - 聊天id
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * 设置聊天id
     *
     * @param chatId 聊天id
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /**
     * 获取用户账号
     *
     * @return user_name - 用户账号
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户账号
     *
     * @param userName 用户账号
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取音视频的相关参数
     *
     * @return user_sdp - 音视频的相关参数
     */
    public String getUserSdp() {
        return userSdp;
    }

    /**
     * 设置音视频的相关参数
     *
     * @param userSdp 音视频的相关参数
     */
    public void setUserSdp(String userSdp) {
        this.userSdp = userSdp == null ? null : userSdp.trim();
    }

    /**
     * 获取 Client 端的 IP 地址信息
     *
     * @return user_candidate -  Client 端的 IP 地址信息
     */
    public String getUserCandidate() {
        return userCandidate;
    }

    /**
     * 设置 Client 端的 IP 地址信息
     *
     * @param userCandidate Client 端的 IP 地址信息
     */
    public void setUserCandidate(String userCandidate) {
        this.userCandidate = userCandidate == null ? null : userCandidate.trim();
    }

    /**
     * 获取client端连接时间
     *
     * @return connect_time - client端连接时间
     */
    public LocalDateTime getConnectTime() {
        return connectTime;
    }

    /**
     * 设置client端连接时间
     *
     * @param connectTime client端连接时间
     */
    public void setConnectTime(LocalDateTime connectTime) {
        this.connectTime = connectTime;
    }
}