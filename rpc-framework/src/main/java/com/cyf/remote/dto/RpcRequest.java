package com.cyf.remote.dto;

import com.cyf.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

/**
 * @author 陈一锋
 * @date 2021/1/3 6:52
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 3556274219728345326L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public RpcServiceProperties toRpcServiceProperties() {
        return RpcServiceProperties.builder()
                .serviceName(this.interfaceName)
                .group(this.group)
                .version(this.version)
                .build();
    }

}
