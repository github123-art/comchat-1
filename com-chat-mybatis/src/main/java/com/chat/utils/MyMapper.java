package com.chat.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 *     继承自己的MyMapper
 * @param <T>
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //注意 接口不能被扫描到，否则会出错
}