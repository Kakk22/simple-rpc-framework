package com.cyf.serialize;

import com.cyf.extension.SPI;

/**
 * 序列化接口
 *
 * @author 陈一锋
 * @date 2021/1/3 20:29
 **/
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);


    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @param clazz 序列化成的对象
     * @param <T>   类的类型·
     * @return 反序列的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
