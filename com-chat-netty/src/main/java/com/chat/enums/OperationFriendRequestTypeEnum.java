package com.chat.enums;

public enum  OperationFriendRequestTypeEnum {

    IGNORE(0,"忽略"),
    PASS(1,"通过");

    public final Integer type;
    public final String msg;

    OperationFriendRequestTypeEnum(Integer type,String msg){
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgKey(Integer type){
        for(OperationFriendRequestTypeEnum typeEnum:OperationFriendRequestTypeEnum.values()){
            if(typeEnum.type == type){
                return typeEnum.msg;
            }
        }
        return null;
    }
}
