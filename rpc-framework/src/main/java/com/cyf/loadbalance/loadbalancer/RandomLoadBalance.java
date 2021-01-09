package com.cyf.loadbalance.loadbalancer;

import com.cyf.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * 简单的随机负载均衡算法
 *
 * @author 陈一锋
 * @date 2021/1/9 11:14
 **/
public class RandomLoadBalance extends AbstractLoadBalance {


    @Override
    protected String doSelect(List<String> serviceAddress, String rpcServiceName) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
