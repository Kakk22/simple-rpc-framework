package com.cyf.provider;

import com.cyf.entity.RpcServiceProperties;
import com.cyf.enums.RpcErrorMessagesEnum;
import com.cyf.exception.RpcException;
import com.cyf.registry.ServiceRegistry;
import com.cyf.transport.netty.server.NettyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈一锋
 * @date 2020/12/27 22:04
 **/
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.registeredService = ConcurrentHashMap.newKeySet();
        // todo spi服务
        this.serviceRegistry = null;
    }

    @Override
    public void addService(Object service, String serviceName) {
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("add service:{} and interfaces is: {}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorMessagesEnum.SERVICE_CAN_NOT_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, RpcServiceProperties.builder().group("").version("").build());
    }

    @Override
    @SneakyThrows(UnknownHostException.class)
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        String host = InetAddress.getLocalHost().getHostAddress();
        Class<?> serviceInterfaces = service.getClass().getInterfaces()[0];
        String serviceName = serviceInterfaces.getCanonicalName();
        rpcServiceProperties.setServiceName(serviceName);
        // rpc 服务名称
        String rpcServiceName = rpcServiceProperties.toRpcServiceName();
        this.addService(service, rpcServiceName);
        // 服务注册
        serviceRegistry.registryService(rpcServiceName, new InetSocketAddress(host, NettyService.PORT));
    }
}
