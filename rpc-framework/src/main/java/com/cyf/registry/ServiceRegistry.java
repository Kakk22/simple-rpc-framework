package com.cyf.registry;

import com.cyf.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 *
 * @author 陈一锋
 * @date 2020/12/27.
 **/
@SPI
public interface ServiceRegistry {

    /**
     * 服务注册
     *
     * @param rpcServiceName    服务名称
     * @param inetSocketAddress 服务地址
     */
    void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
