package com.cyf.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取单例的简单工厂
 *
 * @author 陈一锋
 * @date 2020/12/29 20:45
 **/
public class SingletonFactory {

    private static volatile Map<String, Object> FACTORY = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }


    public static <T> T getInstance(Class<T> c) {
        String key = c.getName();
        Object instance = FACTORY.get(key);
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                instance = FACTORY.get(key);
                if (instance == null) {
                    try {
                        instance = c.getDeclaredConstructor().newInstance();
                        FACTORY.put(key, instance);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e.getMessage());
                    } catch (NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return c.cast(instance);
    }
}
