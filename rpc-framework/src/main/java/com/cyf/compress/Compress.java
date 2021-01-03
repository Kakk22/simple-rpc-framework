package com.cyf.compress;

/**
 * 压缩接口
 *
 * @author 陈一锋
 * @date 2021/1/3 22:23
 **/
public interface Compress {

    /**
     * 压缩
     *
     * @param bytes 字节数据
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压
     *
     * @param bytes 字节数据
     * @return 解压后字节数组
     */
    byte[] decompress(byte[] bytes);
}
