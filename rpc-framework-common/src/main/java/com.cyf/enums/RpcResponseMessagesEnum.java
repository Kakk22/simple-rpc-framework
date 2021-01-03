package com.cyf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 陈一锋
 * @date 2021/1/3 15:06
 **/
@AllArgsConstructor
@Getter
public enum RpcResponseMessagesEnum {

    /**
     * rpc成功返回
     */
    SUCCESS(200, "The remote call is successful"),

    /**
     * 失败返回
     */
    FAIL(500, "The remote call is fail");

    private final int code;
    private final String msg;
}
