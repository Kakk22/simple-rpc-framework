package com.cyf.remote.dto;

import lombok.*;

/**
 * rpc消息
 *
 * @author 陈一锋
 * @date 2021/1/2 23:19
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RpcMessage {
    /**
     * 消息类型
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩类型
     */
    private byte compress;
    /**
     * 请求id
     */
    private int requestId;
    /**
     * rpcResponse or rpcRequest
     */
    private Object data;
}
