package com.cyf.loadbalance.loadbalancer;

import com.cyf.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * refer to dubbo consistent hash load balance: http://dubbo.apache.org/zh-cn/blog/dubbo-consistent-hash-implementation.html
 *
 * @author 陈一锋
 * @date 2021/1/9 11:18
 **/
public class ConsistentHashLoadBalance extends AbstractLoadBalance {


    @Override
    protected String doSelect(List<String> serviceAddress, String rpcServiceName) {
        // TODO: 2021/1/9 一致hash算法
        return null;
    }
}
