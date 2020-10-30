package com.chat.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 *   自定义响应结构，转换类
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     *     将对象转化为json
     * @param data
     * @return
     */
    public static String objectToJson(Object data){
        try {
            return MAPPER.writeValueAsString(data);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *      json结果集转换成对象
     * @param jsonData   json数据
     * @param beanType   object类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToPojo(String jsonData,Class<T> beanType){
        T t = null;
        try {
            t = MAPPER.readValue(jsonData, beanType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }

    /**
     *    将json数据转换成pojo对象的list
     * @param jsonData
     * @param beanType
     * @param <T>
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData,Class<T> beanType){
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
