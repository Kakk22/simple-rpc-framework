package com.cyf.registry.zk.util;

import com.cyf.enums.RpcConfigEnum;
import com.cyf.util.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zk 客户端 工具类
 *
 * @author 陈一锋
 * @date 2020/12/27 11:33
 **/
@Slf4j
public final class CuratorUtils {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTRY_ROOT_PATH = "my-rpc";
    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static final Set<String> REGISTRY_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;


    private CuratorUtils() {
    }

    public static CuratorFramework getZkClient() {
        // 如果zkClient 已经存在并且启动 则直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // 检查是否自定义zk地址
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_ENUM.getValue());
        String zookeeperAddress = properties != null
                && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getValue()) != null
                ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getValue()) : DEFAULT_ZK_ADDRESS;
        // 设置睡眠时间及最大重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        return zkClient;
    }

    /**
     * 创建一个持久化节点 客户端断开后不会删除改节点
     *
     * @param path node path
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTRY_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists ,The node is:{}", path);
            } else {
                // eg: /my-rpc/com.cyf.HelloService/127.0.0.1:9999
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            REGISTRY_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node error,node path {} ", path);
        }
    }
}