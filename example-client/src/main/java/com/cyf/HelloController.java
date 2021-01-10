package com.cyf;

import com.cyf.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author 陈一锋
 * @date 2021/1/10 20:36
 **/
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = helloService.hello(new Hello("hello rpc", "222"));
        System.out.println("Hello description is 222".equals(hello));
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.hello(new Hello("111","222")));
        }
    }
}
