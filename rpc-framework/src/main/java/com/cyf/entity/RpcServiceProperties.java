package com.cyf.entity;

import lombok.*;

/**
 * rpc 服务的属性
 *
 * @author 陈一锋
 * @date 2020/12/27 21:58
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {
    /**
     * 服务版本
     */
    private String version;
    /**
     * 但接口有多个实现类,通过分组区分
     */
    private String group;
    /**
     * 服务名称
     */
    private String serviceName;

    public String toRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
