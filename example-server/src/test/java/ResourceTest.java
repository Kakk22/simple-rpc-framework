import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author 陈一锋
 * @date 2021/1/10 22:47
 **/
public class ResourceTest {


    public static void main(String[] args) throws UnsupportedEncodingException {
        String fileName = "rpc.properties";
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        System.out.println(url.getPath());
        String path = URLDecoder.decode(url.getPath(), "UTF-8");
        Properties properties = null;
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(path))) {
            properties = new Properties();
            //加载流对象 properties实际为一个map 键值对形式
            properties.load(reader);
            System.out.println(properties.get("rpc.zookeeper.address"));
        } catch (Exception e) {
            System.out.println(String.format("error when read properties file %s,error msg:%s", fileName, e.getMessage()));
        }
    }
}
