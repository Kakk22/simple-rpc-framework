package com.cyf.remote.transport.netty.client;

import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.transport.RpcRequestTransport;
import com.cyf.remote.transport.netty.codec.RpcMessageDecoder;
import com.cyf.remote.transport.netty.codec.RpcMessageEncoder;
import com.cyf.remote.transport.netty.server.NettyRpcServiceHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * netty 实现客户端
 *
 * @author 陈一锋
 * @date 2021/1/7 22:57
 **/
public class NettyClient implements RpcRequestTransport {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventExecutors;

    public NettyClient() {
        eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //这里设置连接超时时间
                //if this time is exceeded or the connection cannot be established ,the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        //接受到消息后的业务处理
                        p.addLast(new NettyRpcServiceHandler());

                    }
                });
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }
}
