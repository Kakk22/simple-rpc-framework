package com.cyf.spring;

import com.cyf.annotation.RpcReference;
import com.cyf.annotation.RpcService;
import com.cyf.entity.RpcServiceProperties;
import com.cyf.extension.ExtensionLoader;
import com.cyf.factory.SingletonFactory;
import com.cyf.provider.ServiceProvider;
import com.cyf.provider.ServiceProviderImpl;
import com.cyf.proxy.RpcClientProxy;
import com.cyf.remote.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 在bean注册前判断是否有@rpcService 注解 如果有则注册服务
 *
 * @author 陈一锋
 * @date 2021/1/2 14:40
 **/
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcRequestTransport;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcRequestTransport = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("{} is annotation with {}", bean.getClass().getName(), RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .build();
            serviceProvider.publishService(bean, rpcServiceProperties);
        }
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        //get bean declaredFields
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version())
                        .build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
                //获取代理对象
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    //设置该字段为代理对象
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    log.error("set bean proxy error:", e);
                }
            }
        }
        return bean;
    }
}
