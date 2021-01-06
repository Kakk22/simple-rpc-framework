package com.cyf.remote.dto;

import com.cyf.enums.RpcResponseMessagesEnum;
import lombok.*;

import java.io.Serializable;

import static com.cyf.enums.RpcResponseMessagesEnum.SUCCESS;

/**
 * 封装消息返回对象
 *
 * @author 陈一锋
 * @date 2021/1/3 14:59
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -5654107943786412432L;
    /**
     * 请求id 用于确定具体请求
     */
    private String requestId;
    /**
     * 返回消息
     */
    private String messages;
    /**
     * code
     */
    private int code;
    /**
     * 数据对象
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setData(data);
        response.setRequestId(requestId);
        response.setCode(SUCCESS.getCode());
        response.setMessages(SUCCESS.getMsg());
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseMessagesEnum rpcResponseMessagesEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setMessages(rpcResponseMessagesEnum.getMsg());
        response.setCode(rpcResponseMessagesEnum.getCode());
        return response;
    }
}
