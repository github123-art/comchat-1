package com.chat.controller;


import com.chat.enums.OperationFriendRequestTypeEnum;
import com.chat.enums.SearchFriendStatusEnum;
import com.chat.pojo.BO.UserBo;
import com.chat.pojo.ChatMsg;
import com.chat.pojo.Users;
import com.chat.pojo.VO.MyFriendsVO;
import com.chat.pojo.VO.UsersVO;
import com.chat.service.UserService;
import com.chat.service.impl.UserServiceImpl;
import com.chat.utils.FastDFSClient;
import com.chat.utils.FileUtils;
import com.chat.utils.IMoocJSONResult;
import com.chat.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("u")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registOrLogin")
    private IMoocJSONResult Login(@RequestBody Users users) throws Exception {
        if(StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())){
            IMoocJSONResult.build(500,"用户名密码错误",users);
        }
        //判断登录
        Boolean isExist = userService.queryUsernameIsExist(users.getUsername());
        Users usersResult = null;
        if(isExist){

            //登录
            usersResult = userService.queryUserForLogin(users.getUsername(),
                                                                MD5Utils.getMD5Str(users.getPassword()));
             if(usersResult == null){
                 return IMoocJSONResult.errorMsg("用户名密码错误");
             }
        }else {
            //注册
            users.setNickname(users.getUsername());
            users.setFaceImage("");
            users.setFaceImageBig("");
            users.setQrcode("");
            users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
            usersResult = userService.saveUser(users);
        }
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(usersResult,usersVO);

        return IMoocJSONResult.ok(usersVO);
    }


    /**
     *   上传用户头像
     * @return   face的数据
     */
    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UserBo userBo) throws Exception{

        //获取前端传来的base64字符串，然后转换成文件对象再上传
        String base64Data = userBo.getFaceData();
        String userFacePath = "C:\\test\\"+userBo.getUserId()+"userbase64.png";
        FileUtils.base64ToFile(userFacePath,base64Data);

        //上传文件到Fast
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        //获取缩略图url
        String thump = "_80x80.";
        String[] arr = url.split("\\.");
        String thumpUrl = arr[0]+thump+arr[1];

        //更细用户头像
        Users users = new Users();
        users.setId(userBo.getUserId());
        users.setFaceImage(thumpUrl);
        users.setFaceImageBig(url);
        Users result = userService.updateUserInfo(users);

        return IMoocJSONResult.ok(result);
    }

    /**
     *     更新昵称
     * @param userBo
     * @return 更新后的昵称信息
     * @throws Exception
     */
    @PostMapping("/setNickname")
    public IMoocJSONResult setNickname(@RequestBody UserBo userBo) throws Exception{
         if(userBo.getNickname().length() > 20 || userBo.getNickname() == null){
             return IMoocJSONResult.errorMsg("修改长度错误，或为空!");
         }
         Users users = new Users();
         users.setId(userBo.getUserId());
         users.setNickname(userBo.getNickname());

         Users result = userService.updateUserInfo(users);
         return IMoocJSONResult.ok(result);
    }


    /**
     *        搜索查询好友接口，根据账户做匹配查询不是模糊查询
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
    @PostMapping("/search")
    public IMoocJSONResult searchUser(String myUserId,String friendUsername) throws Exception{
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
                IMoocJSONResult.errorMsg("");
        }
        //前置条件0.用户不存在
        //1.不能添加自己为好友
        //2.该用户已经是你好友了
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);

        if(status == SearchFriendStatusEnum.SUCCESS.status){
            Users users = userService.queryUserInfoByUsername(friendUsername);
            UsersVO result = new UsersVO();
            BeanUtils.copyProperties(users,result);
            return IMoocJSONResult.ok(result);
        }else {
            String msg = SearchFriendStatusEnum.getMsgByKey(status);
            return  IMoocJSONResult.errorMsg(msg);
        }
    }

    /**
     *     添加好友发送请求
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId,String friendUsername) throws Exception{
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            IMoocJSONResult.errorMsg("");
        }
        //再做一遍搜索好友的前置条件
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);

        if(status == SearchFriendStatusEnum.SUCCESS.status){
            userService.sendFriendRequest(myUserId,friendUsername);
        }else {
            String msg = SearchFriendStatusEnum.getMsgByKey(status);
            return  IMoocJSONResult.errorMsg(msg);
        }
        return IMoocJSONResult.ok("");
    }

    /**
     *      查询用户接受的好友请求
     * @param myUserId
     * @return
     * @throws Exception
     */
    @PostMapping("/queryFriendRequest")
    public IMoocJSONResult queryFriendRequest(String myUserId) throws Exception{
        //0.判断用户名是否为空
        if(StringUtils.isBlank(myUserId)){
            IMoocJSONResult.errorMsg("");
        }
        //1.返回用户接受到的好友请求
        return IMoocJSONResult.ok(userService.queryFriendRequestList(myUserId));
    }


    /**
     *    接受方  接受和忽略添加好友请求
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     */
    @PostMapping("/operFriendRequest")
    public IMoocJSONResult operFriendRequest(String acceptUserId,String sendUserId,Integer operType){
        //0.判断送来的数据是否为空
        if(StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null){
            IMoocJSONResult.errorMsg("");
        }

        //1.如果operType没有对应的枚举值，则直接抛出空错误值
        if(StringUtils.isBlank(OperationFriendRequestTypeEnum.getMsgKey(operType))){
            return IMoocJSONResult.errorMsg("");
        }

        if(operType == OperationFriendRequestTypeEnum.IGNORE.type){
            //2.如果忽略好友申请 就删除好友申请表中的数据
            userService.deleteFriendRequest(sendUserId,acceptUserId);

        }else if(operType == OperationFriendRequestTypeEnum.PASS.type){
            //3.如果同意好友申请 则互相增加好友记录到对应的数据表
            //删除好友请求表中的记录
            userService.passFriendRequest(sendUserId,acceptUserId);
        }

        //4.数据库查询好友列表
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(acceptUserId);

        return IMoocJSONResult.ok(myFriendsVOS);
    }


    /**
     *     查询好友
     * @param userId
     * @return
     */
    @PostMapping("/myFriends")
    public IMoocJSONResult MyFriends(String userId){
        //0.判断送来的数据是否为空
        if(StringUtils.isBlank(userId)){
            IMoocJSONResult.errorMsg("");
        }

        //1.从数据库查找我的好友
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(userId);
        return IMoocJSONResult.ok(myFriendsVOS);
    }

    /**
     *    查询未签收消息
     * @param acceptId
     * @return
     */
    @PostMapping("/getUnReadMsgList")
    public IMoocJSONResult getUnReadMsgList(String acceptId){
        if(StringUtils.isBlank(acceptId)){
            IMoocJSONResult.errorMsg("");
        }

        //查询列表
        List<ChatMsg> unReadMsgList = userService.unReadMsgList(acceptId);
        return IMoocJSONResult.ok(unReadMsgList);
    }

}
