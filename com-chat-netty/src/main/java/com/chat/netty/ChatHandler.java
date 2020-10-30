package com.chat.netty;

import com.chat.comchatnetty.SpringUtil;
import com.chat.enums.MsgActionEnum;
import com.chat.service.UserService;
import com.chat.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *    chathandler  处理读写消息
 *    因为是websocket传输  用ws的传输类型
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        Channel UserCurrentChannel = ctx.channel();
        //1.获取客户端发来的消息
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();
        //2.判断消息类型，根据不同类型处理不同业务
        if (action == MsgActionEnum.CONNECT.Type){
        //  2.1 当webscoket 第一次open的时候， 初始化的channel 和 数据库里的userid
        // 不能一一对应 需把用的channel和userid关联起来
            String senderId = dataContent.getChatCon().getSenderId();
            UserChannelRel.put(senderId,UserCurrentChannel);

            //测试
            for(Channel c:users){
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();

        }else if(action == MsgActionEnum.CHAT.Type){
            //  2.2 聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatCon chatCon = dataContent.getChatCon();
            String senderId = chatCon.getSenderId();
            String receiverId = chatCon.getReceiverId();

            //保存到数据库聊天信息，并改为未签收
            UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
            String MsgId = userService.saveMsg(chatCon);
            chatCon.setMsgId(MsgId);

            DataContent dataContentChat = new DataContent();
            dataContentChat.setChatCon(chatCon);

            //从全局用户channel关系中获取接受方channel
            Channel receivedChannel = UserChannelRel.get(receiverId);
            if(receivedChannel == null){
                // TODO  channel为空代表用户离线，推送消息(JPush,个推，小米推送)

            }else{
                //当reveivedchannel不为空时，在channelgroup users中查询有没有它
                Channel findchannel = users.find(receivedChannel.id());
                if(findchannel != null){
                    //用户在线
                    receivedChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContentChat)));

                }else {
                    // TODO  用户离线  推送消息
                }
            }

        }else if(action == MsgActionEnum.SIGNED.Type){
            //  TODO  需要两个手机来测试签收消息记录  hashmap在结束项目后就清零了
            //  2.3 签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            //扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String[] msgIds = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for(String mid: msgIds){
                msgIdList.add(mid);
            }
//            System.out.println(msgIdsStr.toString());

            if(msgIdList != null && !msgIdList.isEmpty() != msgIdList.size() > 0){
                 //批量签收
                userService.updateMsgSigned(msgIdList);
            }

        }else if(action == MsgActionEnum.KEEPALIVE.Type){
            //  2.4 心跳类型消息
            System.out.println("收到来自channel为["+UserCurrentChannel+"]的心跳包....");
        }

    }

    //客户端连接后 加入到channelgroup中
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().id().asLongText()+" 已加入");
        users.add(ctx.channel());
    }

    //移除客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        System.out.println("客户端被移除： channelId为："+channelId);

        //触发handlerremover  channelgroup会自定删除
        users.remove(ctx.channel());
    }

    // 发送异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常后关闭channel 随后在channelgroup删除
        System.out.println("异常信息"+cause);
        System.out.println("channel发生异常");
        ctx.channel().close();
        users.remove(ctx.channel());

    }
}
