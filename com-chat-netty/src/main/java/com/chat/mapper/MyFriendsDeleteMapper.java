package com.chat.mapper;

import com.chat.pojo.FriendsRequest;
import com.chat.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface MyFriendsDeleteMapper extends MyMapper<FriendsRequest> {

    public void deleteFriendRequest(@Param("sendUserId") String sendUserId,@Param("acceptUserId") String acceptUserId);
}