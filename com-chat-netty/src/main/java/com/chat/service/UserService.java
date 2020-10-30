package com.chat.service;

import com.chat.netty.ChatCon;
import com.chat.pojo.ChatMsg;
import com.chat.pojo.MyFriends;
import com.chat.pojo.Users;
import com.chat.pojo.VO.FriendRequestVo;
import com.chat.pojo.VO.MyFriendsVO;

import java.util.List;


public interface UserService {

    /**
     *    查询用户名是否存在
     * @param Username
     * @return
     */
    public Boolean queryUsernameIsExist(String Username);


    /**
     *     用户登录
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username,String password);

    /**
     *    保存用户
     * @param users
     */
    public Users  saveUser(Users users);

    /**
     *    更新用户信息
     * @return
     */
    public Users updateUserInfo(Users users);

    /**
     *    通过id查询用户
     * @return
     */
    public Users queryUserById(String userId);

    /**
     *  查询好友的前置条件
     * @param myUserId
     * @param FriendUsername
     * @return
     */
    public Integer preconditionSearchFriends(String myUserId,String FriendUsername);

    /**
     *     通过名字查找用户
     * @param Friendusername
     * @return
     */
    public Users queryUserInfoByUsername(String Friendusername);

    /**
     *     发送添加好友请求
     * @param myUserId
     * @param friendUsername
     */
    public void sendFriendRequest(String myUserId,String friendUsername);

    /**
     *
     * @param 通过自己的id和好友请求表相连得到请求好友的信息
     * @return
     */
    public List<FriendRequestVo> queryFriendRequestList(String userId);

    /**
     *      删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId,String acceptUserId);



    /**
     *     通过好友请求记录
     *           1.保存好友
     *           2.逆向保存好友
     *           3.删除好友请求
     * @param sendUserId
     * @param accepUserId
     */
    public void passFriendRequest(String sendUserId,String accepUserId);

    /**
     *         通过用户id查询用户好友
     * @param userId
     * @return
     */
    public List<MyFriendsVO> queryMyFriends(String userId);

    /**
     *    保存聊天消息
     * @param 聊天内容
     * @return
     */
    public String saveMsg(ChatCon chatCon);

    /**
     *      批量签收消息
     * @param id列表
     */
    public void updateMsgSigned(List<String> msgIdList);

    /**
     *     查看未签收消息
     * @param 接受者消息
     * @return
     */
    public List<ChatMsg> unReadMsgList(String acceptId);
}
