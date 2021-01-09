package com.cyf.registry.zk;

import com.cyf.enums.RpcErrorMessagesEnum;
import com.cyf.exception.RpcException;
import com.cyf.extension.ExtensionLoader;
import com.cyf.loadbalance.LoadBalance;
import com.cyf.registry.ServiceDiscovery;
import com.cyf.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * zk 服务发现
 *
 * @author 陈一锋
 * @date 2021/1/9 11:28
 **/
@Slf4j
public class ZKServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("random");
    }

    @Override
    public InetSocketAddress discoveryService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceAddress = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceAddress == null || serviceAddress.size() == 0) {
            throw new RpcException(RpcErrorMessagesEnum.SERVICE_CAN_NOT_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceAddress, rpcServiceName);
        log.info("Successful found the service address:{}", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
