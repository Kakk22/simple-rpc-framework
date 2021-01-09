package com.cyf.registry.zk.util;

import com.cyf.util.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.cyf.enums.RpcConfigEnum.RPC_CONFIG_ENUM;
import static com.cyf.enums.RpcConfigEnum.ZK_ADDRESS;

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
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
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
        Properties properties = PropertiesFileUtil.readPropertiesFile(RPC_CONFIG_ENUM.getValue());
        String zookeeperAddress = properties != null
                && properties.getProperty(ZK_ADDRESS.getValue()) != null
                ? properties.getProperty(ZK_ADDRESS.getValue()) : DEFAULT_ZK_ADDRESS;
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

    /**
     * 清空注册服务
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTRY_PATH_SET.stream().parallel().forEach(path -> {
            if (path.endsWith(inetSocketAddress.toString())) {
                try {
                    zkClient.delete().forPath(path);
                } catch (Exception e) {
                    log.error("clear registry fail,path:{}", path);
                }
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTRY_PATH_SET.toString());
    }

    /**
     * 获取服务名的地址列表
     *
     * @param rpcServiceName 服务名 如:com.cyf.HelloService
     * @return 在指定节点下的子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        String path = ZK_REGISTRY_ROOT_PATH + "/" + rpcServiceName;
        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            //注册监听器
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("zkClient get children node for path [{}] error", path);
        }
        return result;
    }

    /**
     * Registers to listen for changes to the specified node
     *
     * @param rpcServiceName 服务名 如:com.cyf.HelloService
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTRY_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            //监听事件触发 修改节点后修改map缓存
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
