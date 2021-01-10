package com.cyf.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 资源文件工具类
 *
 * @author 陈一锋
 * @date 2020/12/27 16:18
 **/
@Slf4j
public final class PropertiesFileUtil {

    private PropertiesFileUtil() {
    }

    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            try {
                rpcConfigPath = URLDecoder.decode(url.getPath() + fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        }
        Properties properties = null;
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)) {
            properties = new Properties();
            //加载流对象 properties实际为一个map 键值对形式
            properties.load(reader);
        } catch (Exception e) {
            log.error("error when read properties file {},error msg:{}", fileName, e.getMessage());
        }
        return properties;
    }

}
