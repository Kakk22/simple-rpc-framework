package com.cyf.registry;

import com.cyf.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务发现
 *
 * @author 陈一锋
 * @date 2020/12/27 11:24
 **/
@SPI
public interface ServiceDiscovery {

    /**
     * 根据服务名获取服务地址
     *
     * @param rpcServiceName 服务名
     * @return 服务地址
     */
    InetSocketAddress discoveryService(String rpcServiceName);
}
