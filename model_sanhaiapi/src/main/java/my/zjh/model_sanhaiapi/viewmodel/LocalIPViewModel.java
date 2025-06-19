package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.LocalIPService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.LocalIPResponse;

/**
 * 本地IP查询ViewModel
 *
 * @author AHeng
 * @date 2025/03/26
 */
public class LocalIPViewModel extends ViewModel {
    
    // 用于管理RxJava订阅
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // 错误信息LiveData
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // 本地IP查询响应LiveData
    private final MutableLiveData<LocalIPResponse> localIPResponse = new MutableLiveData<>();
    
    // 展平的数据项列表，用于RecyclerView展示
    private final MutableLiveData<List<DataItem>> dataItems = new MutableLiveData<>(new ArrayList<>());
    
    @Override
    protected void onCleared() {
        super.onCleared();
        
        // 清理RxJava订阅，避免内存泄漏
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
    
    /**
     * 获取加载状态LiveData
     *
     * @return 加载状态LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * 获取错误信息LiveData
     *
     * @return 错误信息LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 获取本地IP查询响应LiveData
     *
     * @return 本地IP查询响应LiveData
     */
    public LiveData<LocalIPResponse> getLocalIPResponse() {
        return localIPResponse;
    }
    
    /**
     * 获取数据项列表LiveData
     *
     * @return 数据项列表LiveData
     */
    public LiveData<List<DataItem>> getDataItems() {
        return dataItems;
    }
    
    /**
     * 查询本地IP
     *
     * @param detail 是否返回完整信息，1表示返回完整信息，0或不传表示返回简略信息
     */
    public void queryLocalIP(Integer detail) {
        // 设置加载状态
        isLoading.setValue(true);
        
        // 清除之前的错误信息
        errorMessage.setValue(null);
        
        // 调用API查询本地IP
        LocalIPService localIPService = RetrofitClient.getInstance().getLocalIPService();
        Disposable disposable = localIPService.queryLocalIP(detail)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(this::handleSuccessResponse, this::handleError);
        
        // 添加到CompositeDisposable中统一管理
        compositeDisposable.add(disposable);
    }
    
    /**
     * 处理成功响应
     *
     * @param response 本地IP查询响应
     */
    private void handleSuccessResponse(LocalIPResponse response) {
        // 停止加载状态
        isLoading.setValue(false);
        
        // 更新本地IP查询响应
        localIPResponse.setValue(response);
        
        // 将嵌套对象转换为展平的键值对列表
        List<DataItem> items = new ArrayList<>();
        
        // 只添加核心数据项
        
        // 处理网络信息
        if (response.getNetwork() != null) {
            LocalIPResponse.Network network = response.getNetwork();
            items.add(new DataItem("IP地址", network.getIp()));
            items.add(new DataItem("IP版本", network.getVersion()));
            
            // 处理位置信息
            if (network.getLocation() != null) {
                LocalIPResponse.Network.Location location = network.getLocation();
                items.add(new DataItem("国家/地区", location.getCountry()));
                items.add(new DataItem("城市", location.getCity()));
                items.add(new DataItem("区县", location.getDistrict()));
                items.add(new DataItem("网络运营商", location.getIsp()));
            }
        }
        
        // 处理客户端信息
        if (response.getClient() != null && response.getClient().getDevice() != null) {
            LocalIPResponse.Client.Device device = response.getClient().getDevice();
            items.add(new DataItem("设备类型", device.getType()));
            items.add(new DataItem("操作系统", device.getOs()));
            items.add(new DataItem("浏览器", device.getBrowser()));
        }
        
        // 处理安全信息
        if (response.getSecurity() != null) {
            items.add(new DataItem("HTTPS状态", response.getSecurity().getHttps()));
        }
        
        // 添加提示信息
        items.add(new DataItem("提示", response.getTips()));
        
        // 更新数据项列表
        dataItems.setValue(items);
    }
    
    /**
     * 处理错误
     *
     * @param throwable 错误信息
     */
    private void handleError(Throwable throwable) {
        // 停止加载状态
        isLoading.setValue(false);
        
        // 设置错误信息
        errorMessage.setValue(throwable.getMessage());
        
        // 记录错误日志
        Logger.e("本地IP查询错误: " + throwable.getMessage());
    }
    
    /**
     * 获取所有数据的JSON字符串
     *
     * @return 所有数据的JSON字符串
     */
    public String getAllDataJson() {
        LocalIPResponse response = localIPResponse.getValue();
        if (response != null) {
            return new Gson().toJson(response);
        }
        return "{}";
    }
    
    /**
     * 数据项类，用于RecyclerView展示
     */
    public static class DataItem {
        private String key;
        private String value;
        
        public DataItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public void setKey(String key) {
            this.key = key;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }
} 