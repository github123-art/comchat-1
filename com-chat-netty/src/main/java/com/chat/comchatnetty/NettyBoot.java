package com.chat.comchatnetty;

import com.chat.netty.WSServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBoot implements ApplicationListener<ContextRefreshedEvent> {


    //  得到该事件的上下文对象  如果为空  就启动WS server
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
         if(contextRefreshedEvent.getApplicationContext().getParent() == null){
             try {

             WSServer.getInstance().start();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
    }
}
