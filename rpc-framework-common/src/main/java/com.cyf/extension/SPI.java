package com.cyf.extension;

import java.lang.annotation.*;

/**
 * 自定义扩展SPI机制
 *
 * @author 陈一锋
 * @date 2020/12/27.
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
}
