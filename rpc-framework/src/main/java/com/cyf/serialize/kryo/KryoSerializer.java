package com.cyf.serialize.kryo;

import com.cyf.exception.SerializeException;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.dto.RpcResponse;
import com.cyf.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * kyro是高性能序列化
 *
 * @author 陈一锋
 * @date 2021/1/3 20:33
 **/
public class KryoSerializer implements Serializer {
    /**
     * kryo 非线程安全 使用ThreadLocal
     */
    private final static ThreadLocal<Kryo> THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = THREAD_LOCAL.get();
            //obj->byte[] 对象序列化成字节数组
            kryo.writeObject(output, obj);
            THREAD_LOCAL.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("serialize fail:" + e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayOutputStream)) {
            Kryo kryo = THREAD_LOCAL.get();
            // 反序列化 bytes ->object
            Object object = kryo.readObject(input, clazz);
            THREAD_LOCAL.remove();
            return clazz.cast(object);
        } catch (Exception e) {
            throw new SerializeException("deserialize fail:" + e.getMessage());
        }
    }
}
