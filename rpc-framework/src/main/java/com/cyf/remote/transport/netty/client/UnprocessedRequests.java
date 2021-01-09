package com.cyf.remote.transport.netty.client;

import com.cyf.remote.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * unprocessed requests by the server.
 *
 * @author 陈一锋
 * @date 2021/1/9 14:59
 **/
public class UnprocessedRequests {
    private final Map<String, CompletableFuture<RpcResponse<Object>>> futureMap;

    public UnprocessedRequests() {
        this.futureMap = new ConcurrentHashMap<>();
    }

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        futureMap.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = futureMap.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
