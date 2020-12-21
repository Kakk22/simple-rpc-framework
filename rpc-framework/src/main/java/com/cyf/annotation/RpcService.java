package com.cyf.annotation;

import java.lang.annotation.*;

/**
 * rpc服务注解 作用于服务实现类
 *
 * @author 陈一锋
 * @date 2020/12/21.
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcService {

    /**
     * Service version,default value is empty string
     */
    String version() default "";

    /**
     * Service group ,default value is empty string
     */
    String group() default "";
}
