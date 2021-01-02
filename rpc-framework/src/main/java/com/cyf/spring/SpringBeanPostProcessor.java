package com.cyf.spring;

import com.cyf.annotation.RpcService;
import com.cyf.entity.RpcServiceProperties;
import com.cyf.factory.SingletonFactory;
import com.cyf.provider.ServiceProvider;
import com.cyf.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

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

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
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
        return null;
    }
}
