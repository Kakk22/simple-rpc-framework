package com.cyf.impl;

import com.cyf.Hello;
import com.cyf.HelloService;
import com.cyf.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 陈一锋
 * @date 2021/1/10 18:35
 **/
@Slf4j
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建了");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到：{}",hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
