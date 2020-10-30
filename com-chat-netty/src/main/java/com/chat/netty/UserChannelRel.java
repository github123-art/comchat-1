package com.chat.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 *    对用户channel 和  userid  相关联
 */
public class UserChannelRel {

    private  static  HashMap<String, Channel> userRelation = new HashMap<>();

    public static void put(String userId,Channel channel){
        userRelation.put(userId,channel);
    }

    public static Channel get(String userId){
      return   userRelation.get(userId);
    }

    public static void output(){
        for(HashMap.Entry<String,Channel> entry:userRelation.entrySet()){
            System.out.println("userId:"+entry.getKey()
                               +"ChannelId:"+entry.getValue());
        }
    }
}
