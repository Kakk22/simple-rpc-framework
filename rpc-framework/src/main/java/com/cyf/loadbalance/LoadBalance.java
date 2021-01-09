package com.cyf.loadbalance;

import com.cyf.extension.SPI;

import java.util.List;

/**
 * 负载均衡接口
 *
 * @author 陈一锋
 * @date 2021/1/9 11:07
 **/
@SPI
public interface LoadBalance {

    /**
     * 从列表里面选择一个服务地址
     *
     * @param serviceAddress 服务地址列表
     * @param rpcServiceName 服务名称
     * @return
     */
    String selectServiceAddress(List<String> serviceAddress, String rpcServiceName);
}
