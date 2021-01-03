package com.cyf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩类型枚举
 *
 * @author 陈一锋
 * @date 2021/1/3 22:05
 **/
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    /**
     * gzip类型枚举
     */
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum type : CompressTypeEnum.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }
        return null;
    }
}
