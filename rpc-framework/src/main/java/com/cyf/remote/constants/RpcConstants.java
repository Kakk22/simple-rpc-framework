package com.cyf.remote.constants;

/**
 * @author 陈一锋
 * @date 2021/1/3 6:02
 **/
public interface RpcConstants {

    /**
     * 魔法值
     */
    byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};

    /**
     * version information
     */
    byte VERSION = 1;
    byte TOTAL_LENGTH = 16;
    final byte REQUEST_TYPE = 1;
    byte RESPONSE_TYPE = 2;
    /**
     * ping
     */
    byte HEARTBEAT_REQUEST_TYPE = 3;
    /**
     * pong
     */
    byte HEARTBEAT_RESPONSE_TYPE = 4;
    int HEAD_LENGTH = 16;
    String PING = "ping";
    String PONG = "pong";
    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
