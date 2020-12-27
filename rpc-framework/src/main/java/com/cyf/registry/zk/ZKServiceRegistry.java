package com.cyf.registry.zk;

import com.cyf.registry.ServiceRegistry;
import com.cyf.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * zk 实现服务注册
 *
 * @author 陈一锋
 * @date 2020/12/27 11:29
 **/
@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    @Override
    public void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String path = CuratorUtils.ZK_REGISTRY_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, path);
    }
}
