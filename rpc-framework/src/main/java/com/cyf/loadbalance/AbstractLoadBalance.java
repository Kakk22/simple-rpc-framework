package com.cyf.loadbalance;

import java.util.List;

/**
 * 抽象负债均衡类 采用模板方法
 *
 * @author 陈一锋
 * @date 2021/1/9 11:11
 **/
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddress, String rpcServiceName) {
        if (serviceAddress == null || serviceAddress.size() == 0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }

        return doSelect(serviceAddress, rpcServiceName);
    }

    /**
     * 子类实现具体的负载均衡算法
     *
     * @param serviceAddress /
     * @param rpcServiceName /
     * @return 服务地址
     */
    protected abstract String doSelect(List<String> serviceAddress, String rpcServiceName);
}
