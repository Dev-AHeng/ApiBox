package my.zjh.model_sanhaiapi.proto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.core.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import my.zjh.box.BarcodeQueryResponseProto;

/**
 * 条形码查询响应Proto序列化器
 * <p>
 * 实现DataStore的Serializer接口，用于序列化和反序列化BarcodeQueryResponseProto对象
 *
 * @author AHeng
 * @date 2025/04/05 03:20
 */
public class BarcodeQueryResponseSerializer implements Serializer<BarcodeQueryResponseProto> {
    
    @Override
    public BarcodeQueryResponseProto getDefaultValue() {
        return BarcodeQueryResponseProto.getDefaultInstance();
    }
    
    @Nullable
    @Override
    public BarcodeQueryResponseProto readFrom(@NonNull InputStream inputStream, @NonNull Continuation<? super BarcodeQueryResponseProto> continuation) {
        try {
            return BarcodeQueryResponseProto.parseFrom(inputStream);
        } catch (IOException e) {
            continuation.resumeWith(new kotlin.Result.Failure(e));
            throw new RuntimeException(e);
        }
    }
    
    @Nullable
    @Override
    public BarcodeQueryResponseProto writeTo(BarcodeQueryResponseProto barcodeQueryResponseProto, @NonNull OutputStream outputStream, @NonNull Continuation<? super Unit> continuation) {
        try {
            if (barcodeQueryResponseProto == null) {
                throw new IOException("Proto对象为空");
            }
            barcodeQueryResponseProto.writeTo(outputStream);
            return barcodeQueryResponseProto;
        } catch (IOException e) {
            continuation.resumeWith(new kotlin.Result.Failure(e));
            throw new RuntimeException(e);
        }
    }
    
}