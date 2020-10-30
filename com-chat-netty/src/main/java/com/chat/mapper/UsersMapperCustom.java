package com.chat.mapper;

import com.chat.pojo.Users;
import com.chat.pojo.VO.FriendRequestVo;
import com.chat.pojo.VO.MyFriendsVO;
import com.chat.utils.MyMapper;

import java.util.List;


public interface UsersMapperCustom extends MyMapper<Users> {

    public List<FriendRequestVo> queryFriendRequestList(String userId);

    public List<MyFriendsVO> queryMyFriendsList(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);
}