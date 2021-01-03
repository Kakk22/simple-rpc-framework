package com.cyf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 陈一锋
 * @date 2021/1/3 22:30
 **/
@AllArgsConstructor
@Getter
public enum SerializeTypeEnum {
    /**
     *kryo
     */
    KRYO((byte) 0x01, "kryo");

    private final byte code;
    private final String name;

    public static String getName(byte code){
        for (SerializeTypeEnum type : SerializeTypeEnum.values()) {
            if (type.getCode() == code){
                return type.getName();
            }
        }
        return null;
    }
}
