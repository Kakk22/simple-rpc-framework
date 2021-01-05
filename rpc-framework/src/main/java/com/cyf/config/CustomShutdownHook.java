package com.cyf.config;

import com.cyf.registry.zk.util.CuratorUtils;
import com.cyf.remote.netty.server.NettyService;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 当服务器关闭时,将服务进行注销
 *
 * @author 陈一锋
 * @date 2021/1/2 21:43
 **/
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("clear all server");
        //添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyService.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
            }
            //todo 关闭线程池
        }));
    }
}
