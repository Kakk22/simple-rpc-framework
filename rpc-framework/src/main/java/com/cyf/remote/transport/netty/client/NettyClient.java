package com.cyf.remote.transport.netty.client;

import com.cyf.enums.CompressTypeEnum;
import com.cyf.enums.SerializeTypeEnum;
import com.cyf.extension.ExtensionLoader;
import com.cyf.factory.SingletonFactory;
import com.cyf.registry.ServiceDiscovery;
import com.cyf.remote.constants.RpcConstants;
import com.cyf.remote.dto.RpcMessage;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.dto.RpcResponse;
import com.cyf.remote.transport.RpcRequestTransport;
import com.cyf.remote.transport.netty.codec.RpcMessageDecoder;
import com.cyf.remote.transport.netty.codec.RpcMessageEncoder;
import com.cyf.remote.transport.netty.server.NettyRpcServiceHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * netty 实现客户端
 *
 * @author 陈一锋
 * @date 2021/1/7 22:57
 **/
@Slf4j
public class NettyClient implements RpcRequestTransport {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventExecutors;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;

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
                        p.addLast(new NettyClientHandler());

                    }
                });
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //1.获取服务名
        String rpcServiceName = rpcRequest.toRpcServiceProperties().toRpcServiceName();
        //2.根据服务名获取服务地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.discoveryService(rpcServiceName);
        //3.根据服务地址获取管道连接
        Channel channel = getChannel(inetSocketAddress);
        //4.构造请求数据

        // 异步返回值接受对象
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        if (channel.isActive()) {
            // 客户端存储调用请求id
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .codec(SerializeTypeEnum.KRYO.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE)
                    .compressType(CompressTypeEnum.GZIP.getCode())
                    .data(rpcRequest)
                    .build();
            //发送数据
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message [{}] successful", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        }
        return null;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            //如果为空 则创建连接
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 根据指定地址创建连接管道
     *
     * @return 连接管道
     */
    @SneakyThrows
    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        //创建一个返回对象
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
