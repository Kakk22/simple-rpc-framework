package com.cyf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 读取rpc配置枚举
 *
 * @author 陈一锋
 * @date 2020/12/27 16:31
 **/
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * rpc 配置文件名称
     */
    RPC_CONFIG_ENUM("rpc.properties"),
    /**
     * zk 键值对 key
     */
    ZK_ADDRESS("rpc.zookeeper.address");

    String value;
}
