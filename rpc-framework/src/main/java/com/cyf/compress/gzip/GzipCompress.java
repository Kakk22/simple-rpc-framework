package com.cyf.compress.gzip;

import com.cyf.compress.Compress;

/**
 * @author 陈一锋
 * @date 2021/1/3 22:25
 **/
public class GzipCompress implements Compress {
    @Override
    public byte[] compress(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return new byte[0];
    }
}
