package com.chat.service.impl;

import com.chat.comchatnetty.SpringUtil;
import com.chat.enums.MsgActionEnum;
import com.chat.enums.MsgTypeEnum;
import com.chat.enums.SearchFriendStatusEnum;
import com.chat.mapper.*;
import com.chat.netty.ChatCon;
import com.chat.netty.DataContent;
import com.chat.netty.UserChannelRel;
import com.chat.pojo.ChatMsg;
import com.chat.pojo.FriendsRequest;
import com.chat.pojo.MyFriends;
import com.chat.pojo.Users;
import com.chat.pojo.VO.FriendRequestVo;
import com.chat.pojo.VO.MyFriendsVO;
import com.chat.service.UserService;
import com.chat.utils.FastDFSClient;
import com.chat.utils.FileUtils;
import com.chat.utils.JsonUtils;
import com.chat.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    Sid sid;

    @Autowired
    QRCodeUtils qrCodeUtils;

    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    MyFriendsMapper myFriendsMapper;

    @Autowired
    ChatMsgMapper chatMsgMapper;

    @Autowired
    FriendsRequestMapper friendsRequestMapper;

    @Autowired
    UsersMapperCustom usersMapperCustom;

    @Autowired
    MyFriendsDeleteMapper myFriendsDeleteMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Boolean queryUsernameIsExist(String username) {

        Users users = new Users();

        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ? false : true;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",pwd);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users saveUser(Users users) {

        //生成用户id
        String userId = sid.next();

        //为每个生成用户唯一二维码
        String qrCodePath = "C:\\test\\"+userId+"qrcode.png";
        //muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath,"muxin_qrcode:"+users.getUsername());

        //转换文件
        MultipartFile qrcodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";

        try {
           qrCodeUrl =  fastDFSClient.uploadQRCode(qrcodeFile);
        }catch (Exception e){
           e.printStackTrace();
        }

        users.setQrcode(qrCodeUrl);
        users.setId(userId);
        usersMapper.insert(users);

        return users;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users users) {
        //更新表中是null的数据
        usersMapper.updateByPrimaryKeySelective(users);
        return queryUserById(users.getId());
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String FriendUsername) {
        Users users = queryUserInfoByUsername(FriendUsername);

        //0.用户不存在
        if(users == null){
            return SearchFriendStatusEnum.USER_NOT_EXIST.status;
        }
        //1.不能添加自己为好友
        if(users.getId().equals(myUserId)){
            return SearchFriendStatusEnum.NOT_YOURSELF.status;
        }
        //2.该用户已经是你好友了
        Example mfe = new Example(MyFriends.class);
        Example.Criteria criteria = mfe.createCriteria();

        criteria.andEqualTo("myUserId",myUserId);
        criteria.andEqualTo("myFriendUserId",users.getId());
        MyFriends myFriends = myFriendsMapper.selectOneByExample(mfe);
        if(myFriends != null){
            return  SearchFriendStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendStatusEnum.SUCCESS.status;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String FriendUsername){
        Example eu = new Example(Users.class);
        Example.Criteria ec = eu.createCriteria();

        ec.andEqualTo("username",FriendUsername);
        return usersMapper.selectOneByExample(eu);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        Users user = queryUserInfoByUsername(friendUsername);

        //1.查询发送好友请求记录表
        Example efr = new Example(FriendsRequest.class);
        Example.Criteria criteria = efr.createCriteria();

        criteria.andEqualTo("sendUserId",myUserId);
        System.out.println(myUserId+"------");
        criteria.andEqualTo("acceptUserId",user.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(efr);

        if(friendsRequest == null){
           //2.如果不是你的好友，并且好友记录没有添加，则新增好友记录
            String requestId = sid.nextShort();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(user.getId());
            request.setRequestDataTime(new Date());
            friendsRequestMapper.insert(request);

        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVo> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
      // myFriendsDeleteMapper.deleteFriendRequest("200925119491923017728","20092294160658890752");
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria ftc = fre.createCriteria();

        ftc.andEqualTo("sendUserId",sendUserId);
        ftc.andEqualTo("acceptUserId",acceptUserId);
        friendsRequestMapper.deleteByExample(fre);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String accepUserId) {
        //0.互相增加好友记录到MyFriends
        saveFriends(sendUserId,accepUserId);
        saveFriends(accepUserId,sendUserId);
        //1.删除好友请求记录
        deleteFriendRequest(sendUserId,accepUserId);

        //2.通过好友请求后，使用websocket主动推送消息到请求发送者，使它更新好友列表
        Channel sendChannel = UserChannelRel.get(sendUserId);
        if(sendChannel != null) {
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.Type);

            sendChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            JsonUtils.objectToJson(dataContent)));
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        List<MyFriendsVO> myFriendsVOS = usersMapperCustom.queryMyFriendsList(userId);
        return myFriendsVOS;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatCon chatCon) {

        ChatMsg chatMsg = new ChatMsg();
        String msgId = sid.nextShort();
        chatMsg.setId(msgId);
        chatMsg.setSendUserId(chatCon.getSenderId());
        chatMsg.setAcceptUserId(chatCon.getReceiverId());
        chatMsg.setCreateTime(new Date());
        chatMsg.setMsg(chatCon.getMsg());
        chatMsg.setSignFlag(MsgTypeEnum.unsig.type);

        chatMsgMapper.insert(chatMsg);
        return msgId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ChatMsg> unReadMsgList(String acceptId) {
        Example chat = new Example(ChatMsg.class);
        Example.Criteria chatCriteria = chat.createCriteria();

        chatCriteria.andEqualTo("signFlag",0);
        chatCriteria.andEqualTo("acceptUserId",acceptId);
        List<ChatMsg> chatMsgs = chatMsgMapper.selectByExample(chat);
        return chatMsgs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveFriends(String sendUserId, String accepUserId){

        MyFriends myFriends = new MyFriends();
        String nextShort = sid.nextShort();

        myFriends.setId(nextShort);
        myFriends.setMyUserId(sendUserId);
        myFriends.setMyFriendUserId(accepUserId);
        myFriendsMapper.insert(myFriends);
    }



}
