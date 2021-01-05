package com.cyf.remote.netty.codec;

import com.cyf.compress.Compress;
import com.cyf.enums.CompressTypeEnum;
import com.cyf.enums.SerializeTypeEnum;
import com.cyf.extension.ExtensionLoader;
import com.cyf.remote.constants.RpcConstants;
import com.cyf.remote.dto.RpcMessage;
import com.cyf.remote.dto.RpcRequest;
import com.cyf.remote.dto.RpcResponse;
import com.cyf.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.Arrays;

import static com.cyf.remote.constants.RpcConstants.*;

/**
 * <p>
 * custom protocol decoder
 * <p>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 *
 *
 * @author 陈一锋
 * @date 2021/1/2 23:17
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 **/
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      Maximum frame length. It decide the maximum length of data that can be received.
     *                            If it exceeds, the data will be discarded.
     * @param lengthFieldOffset   Length field offset. The length field is the one that skips the specified length of byte.
     * @param lengthFieldLength   The number of bytes in the length field.
     * @param lengthAdjustment    The compensation value to add to the value of the length field
     * @param initialBytesToStrip Number of bytes skipped.
     *                            If you need to receive all of the header+body data, this value is 0
     *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
            if (frame.readableBytes() >= TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        //verify magic code
        int len = MAGIC_NUMBER.length;
        byte[] magicBytes = new byte[len];
        in.readBytes(magicBytes);
        for (int i = 0; i < magicBytes.length; i++) {
            if (magicBytes[i] != MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code:" + Arrays.toString(magicBytes));
            }
        }
        // verify version
        byte version = in.readByte();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unknown version:" + version);
        }
        int fullLen = in.readInt();
        //build RpcMessages obj
        byte msgType = in.readByte();
        byte codec = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(msgType)
                .compressType(compressType)
                .codec(codec)
                .requestId(requestId)
                .build();

        if (msgType == HEARTBEAT_REQUEST_TYPE) {
            //ping
            rpcMessage.setData(PING);
        } else if (msgType == HEARTBEAT_RESPONSE_TYPE) {
            //pong
            rpcMessage.setData(PONG);
        } else {
            int bodyLen = fullLen - HEAD_LENGTH;
            if (bodyLen > 0) {
                byte[] body = new byte[bodyLen];
                in.readBytes(body);
                //decompress
                String compressName = CompressTypeEnum.getName(compressType);
                log.info("compressName is:{}", compressName);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                body = compress.compress(body);
                //deserialize
                String serializerName = SerializeTypeEnum.getName(codec);
                log.info("serializerName is:{}", serializerName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerName);
                if (msgType == REQUEST_TYPE) {
                    RpcRequest data = serializer.deserialize(body, RpcRequest.class);
                    rpcMessage.setData(data);
                } else {
                    RpcResponse data = serializer.deserialize(body, RpcResponse.class);
                    rpcMessage.setData(data);
                }
            }
        }
        return rpcMessage;
    }
}
