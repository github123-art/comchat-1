package com.chat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //ws 基于http  设置http的编码器
        pipeline.addLast(new HttpServerCodec());

        //对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());

        //对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024*64));


        //针对客户端，如果在1分钟内没有想服务器发送读写心跳(ALL),则主动断开
        //如果读空闲或写空闲，不处理
        pipeline.addLast(new IdleStateHandler(8,10,12));

        //自定义读写空闲状态handler
        pipeline.addLast(new HeartBeatHandler());


        // 配置websocket的协议  让他完成一些繁琐复杂的事
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
