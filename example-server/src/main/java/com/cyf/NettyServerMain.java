package com.cyf;

import com.cyf.annotation.RpcScan;
import com.cyf.entity.RpcServiceProperties;
import com.cyf.impl.HelloServiceImpl;
import com.cyf.remote.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author 陈一锋
 * @date 2021/1/10 18:31
 **/
@RpcScan(basePackage = {"com.cyf"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyRpcServer = (NettyServer) applicationContext.getBean("nettyServer");
        nettyRpcServer.start();
    }
}
