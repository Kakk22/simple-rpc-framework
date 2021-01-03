package com.cyf.spring;

import com.cyf.annotation.RpcService;

/**
 * @author 陈一锋
 * @date 2020/12/22.
 **/
@RpcService
public class HelloService {

    public String hello() {
        return "hello";
    }
}
