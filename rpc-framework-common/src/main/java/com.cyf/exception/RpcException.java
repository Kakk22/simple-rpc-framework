package com.cyf.exception;

import com.cyf.enums.RpcErrorMessagesEnum;

/**
 * 自定义rpc异常
 *
 * @author 陈一锋
 * @date 2020/12/27 22:43
 **/
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessagesEnum rpcErrorMessagesEnum) {
        super(rpcErrorMessagesEnum.getMessages());
    }
}
