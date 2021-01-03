package com.cyf.remote.netty.server;

import com.cyf.config.CustomShutdownHook;
import com.cyf.entity.RpcServiceProperties;
import com.cyf.factory.SingletonFactory;
import com.cyf.provider.ServiceProvider;
import com.cyf.provider.ServiceProviderImpl;
import com.cyf.remote.netty.codec.RpcMessageDecoder;
import com.cyf.remote.netty.codec.RpcMessageEncoder;
import com.cyf.util.concurrent.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 服务器。 接收客户端消息，根据客户端消息调用相应的方法，然后将结果返回给客户端
 *
 * @author 陈一锋
 * @date 2020/12/27 22:27
 **/
@Slf4j
@Component
public class NettyService {

    public static final int PORT = 9998;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public void registryService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    @SneakyThrows
    public void start() {
        //清空注册表
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup defaultChannelGroup = new DefaultEventExecutorGroup(
                // 获取cpu核心数
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 设置io模型
                    .channel(NioServerSocketChannel.class)
                    //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //是否开启TCP底层心跳协议
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //当第一次有客户端连接时,这里才会初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //30 秒之内没有收到客户端请求的话就关闭连接
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, SECONDS));
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(new RpcMessageDecoder());
                            // 这里进行业务处理
                            pipeline.addLast(defaultChannelGroup, new NettyRpcServiceHandler());
                        }
                    });
            // 同步绑定端口
            ChannelFuture future = serverBootstrap.bind(host, PORT).sync();
            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            defaultChannelGroup.shutdownGracefully();
        }
    }

}
