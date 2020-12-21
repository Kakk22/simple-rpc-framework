package com.cyf.annotation;

import java.lang.annotation.*;

/**
 * rpc参考注解 自动注入服务实现类
 *
 * @author 陈一锋
 * @date 2020/12/21.
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * Service version,default value is empty string
     */
    String version() default "";

    /**
     * Service group ,default value is empty string
     */
    String group() default "";
}
