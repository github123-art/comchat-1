package com.chat.netty;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = -4451537425110454894L;

    private Integer action;  //动作类型
    private ChatCon chatCon; //用户的聊天内容
    private String extand;  //扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatCon getChatCon() {
        return chatCon;
    }

    public void setChatCon(ChatCon chatCon) {
        this.chatCon = chatCon;
    }

    public String getExtand() {
        return extand;
    }

    public void setExtand(String extand) {
        this.extand = extand;
    }
}
