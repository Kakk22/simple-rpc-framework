package com.cyf.remote.transport;

import com.cyf.extension.SPI;
import com.cyf.remote.dto.RpcRequest;

/**
 * @author 陈一锋
 * @date 2021/1/7 22:59
 **/
@SPI
public interface RpcRequestTransport {

    /**
     * send rpc request to server
     *
     * @param rpcRequest rpcRequest object
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
