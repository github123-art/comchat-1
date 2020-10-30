package com.chat.netty;

import com.chat.comchatnetty.SpringUtil;
import com.chat.enums.MsgActionEnum;
import com.chat.service.UserService;
import com.chat.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.timeout.IdleState.ALL_IDLE;
import static io.netty.handler.timeout.IdleState.READER_IDLE;
import static io.netty.handler.timeout.IdleState.WRITER_IDLE;

/**
 *   自定义空闲状态监测
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){

            IdleStateEvent idly = (IdleStateEvent)evt;

            if(idly.state() == IdleState.READER_IDLE){
                System.out.println("读空闲时间");
            }else if(idly.state() == IdleState.WRITER_IDLE){
                System.out.println("写空闲时间");
            }else if(idly.state() == IdleState.ALL_IDLE){

                System.out.println("channel关闭前, users中channel的数量:"+ ChatHandler.users.size());
                Channel channel = ctx.channel();
                //关闭无用的channel 以防浪费资源
                channel.close();

                System.out.println("channel关闭后, users中channel的数量:"+ ChatHandler.users.size());

            }
        }

    }
}
