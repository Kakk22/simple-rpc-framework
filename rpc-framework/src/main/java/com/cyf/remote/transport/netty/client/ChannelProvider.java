package com.cyf.remote.transport.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管道提供者
 *
 * @author 陈一锋
 * @date 2021/1/9 14:33
 **/
public class ChannelProvider {

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress address) {
        String key = address.toString();
        if (channelMap.containsKey(key)) {
            //根据key获取管道
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) {
                // 如果不为空且可使用则直接返回
                return channel;
            } else {
                // 否作移除
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }
}
