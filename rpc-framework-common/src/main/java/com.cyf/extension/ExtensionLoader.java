package com.cyf.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * SPI注解 延迟加载实现
 *
 * @author 陈一锋
 * @date 2020/12/27 23:01
 **/
@Slf4j
public final class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE = new ConcurrentHashMap<>();

    private final Map<String, Holder<T>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final Class<?> type;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotation by @SPI");
        }
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }


    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should be not");
        }
        // 检查缓存是否存在
        Holder<T> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        T instance = holder.get();
        // 双重检查
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return instance;
    }

    /**
     * 创建扩张类实例
     *
     * @param name 名称
     * @return 实例对象
     */
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClass().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name" + name);
        }
        T instance = (T) EXTENSION_INSTANCE.get(clazz);
        if (instance == null) {
            try {
                // 这里创建一个读取到的实例对象
                EXTENSION_INSTANCE.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCE.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    /**
     * 获取扩张类列表
     *
     * @return 全部扩张类
     */
    private Map<String, Class<?>> getExtensionClass() {
        // 从缓存中获取扩展类列表
        Map<String, Class<?>> classes = cachedClasses.get();
        //双重检查
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>(16);
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        //文件名为全限定类名
        String fileName = SERVICE_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(resourceUrl, classes, classLoader);
                }
            }
        } catch (IOException e) {
            log.error("spi load extension instances error:", e.getMessage());
        }

    }

    /**
     * 根据类全限定类名加载类
     *
     * @param resourceUrl 全限定类名
     * @param classes     缓存类对象
     * @param classLoader 类加载器
     */
    private void loadResource(URL resourceUrl, Map<String, Class<?>> classes, ClassLoader classLoader) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            //读每一行
            // zk = com.cyf.registry.zk.ZKServiceRegistry
            while ((line = reader.readLine()) != null) {
                final int c = line.indexOf("#");
                if (c >= 0) {
                    // # 开始为注释 截取
                    line = line.substring(0, c);
                }
                line = line.trim();
                if (line.length() > 0) {
                    final int eq = line.indexOf("=");
                    String name = line.substring(0, eq).trim();
                    String clazzName = line.substring(eq + 1).trim();
                    try {
                        // 初始化类
                        Class<?> clazz = classLoader.loadClass(clazzName);
                        classes.put(name, clazz);
                    } catch (ClassNotFoundException e) {
                        log.error("cloud not load class,class name:{}", clazzName);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
