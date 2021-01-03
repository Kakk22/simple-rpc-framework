package com.cyf.remote.netty.server;

import com.cyf.factory.SingletonFactory;
import com.cyf.remote.dto.RpcMessage;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.handler.RpcRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.cyf.remote.constants.RpcConstants.*;

/**
 * 服务端收到客户端消息后处理器
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。
 *
 * @author 陈一锋
 * @date 2021/1/2 23:15
 **/
@Slf4j
public class NettyRpcServiceHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServiceHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcMessage) {
            log.info("server receive msg:{}", msg);
            RpcMessage rpcMessage = (RpcMessage) msg;
            RpcMessage responseMessage = new RpcMessage();
            //todo 设置序列化类型及压缩类型
            byte messageType = rpcMessage.getMessageType();
            if (messageType == HEARTBEAT_REQUEST_TYPE) {
                // ping pong 心跳连接
                responseMessage.setMessageType(HEARTBEAT_RESPONSE_TYPE);
                responseMessage.setData(PONG);
            } else {
                // 正常rpc请求
                RpcRequest rpcRequest = (RpcRequest) rpcMessage.getData();
                Object data = rpcRequestHandler.handle(rpcRequest);
                log.info(String.format("server get request: %s", data.toString()));
                responseMessage.setMessageType(RESPONSE_TYPE);

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
