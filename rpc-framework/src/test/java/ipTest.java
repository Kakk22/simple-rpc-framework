import lombok.SneakyThrows;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author 陈一锋
 * @date 2020/12/28 16:12
 **/
public class ipTest {

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(InetAddress.getLoopbackAddress());
        System.out.println(InetAddress.getLocalHost());
    }

    @Test
    public void t1() {
        t2(2, "aaaa");
    }

    public void t2(int i, String str) {
        System.out.println(i);
        System.out.println(str);
    }
    @Test
    @SneakyThrows
    public void t3() {
        System.out.println(System.currentTimeMillis());
        Thread.sleep(30);
        System.out.println(System.currentTimeMillis());
    }


}
