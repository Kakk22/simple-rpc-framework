package com.cyf.compress.gzip;

import com.cyf.compress.Compress;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author 陈一锋
 * @date 2021/1/3 22:25
 **/
@Slf4j
public class GzipCompress implements Compress {

    private static final int READ_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] bytes) {
        checkBytes(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("compress error:{}", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        checkBytes(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));) {
            byte[] readBuffer = new byte[READ_SIZE];
            int len;
            while ((len = gzip.read(readBuffer)) > -1) {
                out.write(readBuffer, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("decompress error:{}", e);
        }
    }

    private void checkBytes(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
    }
}
