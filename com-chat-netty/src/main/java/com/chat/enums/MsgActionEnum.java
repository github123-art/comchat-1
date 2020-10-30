package com.chat.enums;

import java.io.Serializable;

/**
 *   发送消息的动作 :  枚举
 */
public enum  MsgActionEnum {

    CONNECT(1,"第一次(或重连)初始化连接"),
    CHAT(2,"聊天消息"),
    SIGNED(3,"消息签收"),
    KEEPALIVE(4,"客户端保持心跳"),
    PULL_FRIEND(5,"拉取朋友");

    public final Integer Type;
    public final String Content;

    MsgActionEnum(Integer Type,String Context){
         this.Type = Type;
         this.Content = Context;
    }

    public Integer getType() {
        return Type;
    }

    public String getContent() {
        return Content;
    }
}
