import com.cyf.factory.SingletonFactory;
import com.cyf.provider.ServiceProviderImpl;
import com.cyf.registry.zk.ZKServiceRegistry;

/**
 * @author 陈一锋
 * @date 2021/1/2 12:10
 **/
public class FactoryTest {

    public static void main(String[] args) {
        ServiceProviderImpl instance = SingletonFactory.getInstance(ServiceProviderImpl.class);
        System.out.println(instance);
        ServiceProviderImpl instance1 = SingletonFactory.getInstance(ServiceProviderImpl.class);

    }
}
