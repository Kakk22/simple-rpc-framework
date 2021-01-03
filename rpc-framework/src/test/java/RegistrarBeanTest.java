import com.cyf.annotation.RpcScan;
import com.cyf.spring.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author 陈一锋
 * @date 2020/12/22.
 **/
@RpcScan(basePackage = {"com.cyf"})
public class RegistrarBeanTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RegistrarBeanTest.class);
        HelloService helloService = (HelloService)applicationContext.getBean("helloService");
        System.out.println(helloService.hello());
    }
}
