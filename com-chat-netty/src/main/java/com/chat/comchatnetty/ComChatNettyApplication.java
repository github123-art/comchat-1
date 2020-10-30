package com.chat.comchatnetty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//扫描mybatis mapper包路径
@MapperScan(basePackages = {"com.chat.mapper","org.n3r","com.chat.configuration"})
//扫描所有需要的包，包含一些自用的工具类包所在的路径
@ComponentScan(basePackages = {"com.chat","org.n3r"})
public class ComChatNettyApplication {

    @Bean
    public SpringUtil getSpringUtile(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(ComChatNettyApplication.class, args);
    }

}
