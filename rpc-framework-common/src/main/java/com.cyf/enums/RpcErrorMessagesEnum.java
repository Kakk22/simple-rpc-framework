package com.cyf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 陈一锋
 * @date 2020/12/27 22:45
 **/
@Getter
@AllArgsConstructor
public enum RpcErrorMessagesEnum {

    /**
     * 找不到指定服务
     */
    SERVICE_CAN_NOT_FOUND("未找到指定服务");

    private final String messages;
}
