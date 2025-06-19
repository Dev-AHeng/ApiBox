package my.zjh.model_sanhaiapi.proto;

import android.content.Context;

import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import my.zjh.box.BarcodeQueryResponseProto;

/**
 * 条形码数据存储类，使用Android DataStore进行本地数据持久化
 *
 * @author AHeng
 * @date 2025/04/05 03:41
 */
public class BarcodeDataStore {
    /**
     * DataStore文件名
     */
    private static final String DATA_STORE_NAME = "barcode_query_response";
    
    /**
     * 静态DataStore实例，用于存储条形码查询响应数据
     */
    private static RxDataStore<BarcodeQueryResponseProto> dataStore;
    
    /**
     * 构造函数，初始化DataStore
     *
     * @param context Android上下文对象
     */
    public BarcodeDataStore(Context context) {
        dataStore = new RxDataStoreBuilder<>(context, DATA_STORE_NAME, new BarcodeQueryResponseSerializer()).build();
    }
    
    /**
     * 检查是否存在存储的条形码数据
     *
     * @return 返回包含检查结果的Single对象
     */
    public Single<Boolean> hasData() {
        return dataStore.data().firstOrError()
                       .map(data -> !data.equals(BarcodeQueryResponseProto.getDefaultInstance()));
    }
    
    /**
     * 获取条形码数据流
     *
     * @return 返回包含条形码查询响应数据的Flowable流
     */
    public Flowable<BarcodeQueryResponseProto> getBarcodeData() {
        return dataStore.data();
    }
    
    
    /**
     * 保存条形码数据
     *
     * @param response 要保存的条形码查询响应数据
     *
     * @return 返回保存操作结果的Single对象
     */
    public Single<BarcodeQueryResponseProto> saveBarcodeData(final BarcodeQueryResponseProto response) {
        return dataStore.updateDataAsync(currentData -> Single.just(response));
    }
    
    
    /**
     * 更新条形码数据
     *
     * @param updater 更新函数，接收当前数据并返回更新后的数据
     *
     * @return 返回更新操作结果的Single对象
     */
    public Single<BarcodeQueryResponseProto> updateBarcodeData(
            final java.util.function.Function<BarcodeQueryResponseProto, BarcodeQueryResponseProto> updater) {
        return dataStore.updateDataAsync(currentData -> Single.just(updater.apply(currentData)));
    }
    
    
    /**
     * 清空存储的条形码数据
     *
     * @return 返回操作结果的Single对象
     */
    public Single<BarcodeQueryResponseProto> clearBarcodeData() {
        return dataStore.updateDataAsync(currentData -> Single.just(BarcodeQueryResponseProto.getDefaultInstance()));
    }
    
}