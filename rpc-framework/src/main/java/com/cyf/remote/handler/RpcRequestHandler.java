package com.cyf.remote.handler;

import com.cyf.exception.RpcException;
import com.cyf.factory.SingletonFactory;
import com.cyf.provider.ServiceProvider;
import com.cyf.provider.ServiceProviderImpl;
import com.cyf.remote.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RpcRequest processor
 *
 * @author 陈一锋
 * @date 2021/1/3 7:01
 **/
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 处理rpc请求
     *
     * @param rpcRequest 请求数据
     * @return 方法返回值
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.toRpcServiceProperties().toRpcServiceName());
        return this.invokeTargetMethod(service, rpcRequest);
    }

    /**
     * 根据服务及方法参数反射执行方法
     *
     * @param service    指定方法的服务类
     * @param rpcRequest 客户端发送的rpc请求参数
     * @return 方法返回值
     */
    private Object invokeTargetMethod(Object service, RpcRequest rpcRequest) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method :[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
