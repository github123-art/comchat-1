package com.chat.netty;

import java.io.Serializable;

public class ChatCon implements Serializable {

    private static final long serialVersionUID = 2877081302616808135L;

    private String senderId;   //发送者用户的id
    private String receiverId;    //接受者用户的id
    private String msg;        //聊天内容
    private String msgId;      //用户消息的签收

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
