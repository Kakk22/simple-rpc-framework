package com.cyf.annotation;

import com.cyf.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * rpc 扫描注解
 *
 * @author 陈一锋
 * @date 2020/12/21.
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
