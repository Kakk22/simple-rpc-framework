import com.cyf.extension.ExtensionLoader;
import com.cyf.remote.dto.RpcMessage;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.serialize.Serializer;
import com.cyf.serialize.kryo.KryoSerializer;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author 陈一锋
 * @date 2021/1/3 21:05
 **/
public class KryoTest {

    @Test
    public void t1(){
        Serializer kryo = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("kryo");

        RpcMessage rpcMessage = RpcMessage.builder().requestId(1).codec((byte) 1).messageType((byte) 1).compress((byte) 22).build();
        RpcRequest hello = RpcRequest.builder().version("1").group("1").interfaceName("hello").build();
        rpcMessage.setData(hello);
        byte[] serialize = kryo.serialize(rpcMessage);
        System.out.println(Arrays.toString(serialize));
        RpcMessage deserialize = kryo.deserialize(serialize, RpcMessage.class);
        System.out.println(deserialize.getData());
    }
}
