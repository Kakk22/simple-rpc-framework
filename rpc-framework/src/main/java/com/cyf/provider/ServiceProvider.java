package com.cyf.provider;

import com.cyf.entity.RpcServiceProperties;

/**
 * 存储并提供服务对象
 *
 * @author 陈一锋
 * @date 2020/12/27 21:54
 **/
public interface ServiceProvider {


    /**
     * 添加服务
     *
     * @param service     服务对象
     * @param serviceName 服务名称
     */
    void addService(Object service, String serviceName);

    /**
     * 根据服务名获取服务
     *
     * @param serviceName 服务名
     * @return 服务对象
     */
    Object getService(String serviceName);

    /**
     * 服务发布
     *
     * @param service 服务对象
     */
    void publishService(Object service);

    /**
     * 带参数的服务发布
     *
     * @param service              服务对象
     * @param rpcServiceProperties 服务属性
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

}
