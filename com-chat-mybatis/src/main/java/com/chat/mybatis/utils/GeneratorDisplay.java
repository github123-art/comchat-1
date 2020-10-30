package com.chat.mybatis.utils;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneratorDisplay {

    public void generator() throws Exception{
        List<String> warning = new ArrayList<>();
        boolean overwrite = true;

        //指定逆向工程配置文件
        File configFile = new File("C:\\Users\\24847\\IdeaProjects\\comchat\\com-chat-mybatis\\generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warning);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,callback,warning);
        myBatisGenerator.generate(null);
    }

    public static void main(String[] args) {
        try {
             GeneratorDisplay generatorSqlmap = new GeneratorDisplay();
             generatorSqlmap.generator();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
