package com.chat.enums;

/**
 *    添加好友前置状态  枚举
 */
public enum  SearchFriendStatusEnum {

    SUCCESS(0,"OK"),
    USER_NOT_EXIST(1,"用户不存在..."),
    NOT_YOURSELF(2,"不能添加你自己"),
    ALREADY_FRIENDS(3,"该用户已经是你朋友了")
    ;

   public final Integer status;
   public final String  msg;

   SearchFriendStatusEnum(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByKey(Integer status){

       for(SearchFriendStatusEnum type:SearchFriendStatusEnum.values()){
             if(type.status == status){
                 return type.getMsg();
             }
       }
       return null;
    }
}
