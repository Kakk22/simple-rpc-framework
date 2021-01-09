package com.cyf.remote.transport.netty.server;

import com.cyf.enums.CompressTypeEnum;
import com.cyf.enums.RpcResponseMessagesEnum;
import com.cyf.enums.SerializeTypeEnum;
import com.cyf.factory.SingletonFactory;
import com.cyf.remote.dto.RpcMessage;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.dto.RpcResponse;
import com.cyf.remote.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
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
            try {
                log.info("server receive msg:{}", msg);
                RpcMessage rpcMessage = (RpcMessage) msg;
                RpcMessage responseMessage = new RpcMessage();
                //设置序列化类型及压缩类型
                responseMessage.setCompressType(CompressTypeEnum.GZIP.getCode());
                responseMessage.setCodec(SerializeTypeEnum.KRYO.getCode());
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
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        responseMessage.setData(RpcResponse.success(data, rpcRequest.getRequestId()));
                    } else {
                        responseMessage.setData(RpcResponse.fail(RpcResponseMessagesEnum.FAIL));
                        log.error("No writable now,messages dropped");
                    }
                    //发送数据 添加监听器 当发送失败时关闭连接
                    ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            } finally {
                //释放msg 防止内存泄漏
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen,close the context");
                //触发空闲事件  关闭连接
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server error");
        cause.printStackTrace();
        ctx.close();
    }
}
