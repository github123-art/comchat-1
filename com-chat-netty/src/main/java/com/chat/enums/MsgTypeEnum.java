package com.chat.enums;

/**
 *    消息签收类型
 */
public enum  MsgTypeEnum {

    unsig(0,"未签收"),
    signed(1,"已签收");


     public final Integer type;
     public final String  Msg;

     MsgTypeEnum(Integer type,String Msg){
         this.type = type;
         this.Msg = Msg;
     }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return Msg;
    }
}
