package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.IPAddressService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.IPAddressResponse;

/**
 * IP地址查询ViewModel
 *
 * @author AHeng
 * @date 2025/03/26
 */
public class IPAddressQueryViewModel extends ViewModel {
    
    // 用于管理RxJava订阅
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // 错误信息LiveData
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // IP地址查询响应LiveData
    private final MutableLiveData<IPAddressResponse> ipAddressResponse = new MutableLiveData<>();
    
    // 原始API数据LiveData
    private final MutableLiveData<Map<String, JsonElement>> apiOriginalData = new MutableLiveData<>(new HashMap<>());
    
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
     * 获取IP地址查询响应LiveData
     *
     * @return IP地址查询响应LiveData
     */
    public LiveData<IPAddressResponse> getIpAddressResponse() {
        return ipAddressResponse;
    }
    
    /**
     * 获取原始API数据LiveData
     *
     * @return 原始API数据LiveData
     */
    public LiveData<Map<String, JsonElement>> getApiOriginalData() {
        return apiOriginalData;
    }
    
    /**
     * 查询IP地址
     *
     * @param ipAddress   IP地址
     * @param apiPriority API优先级
     */
    public void queryIPAddress(String ipAddress, Integer apiPriority) {
        // 设置加载状态
        isLoading.setValue(true);
        
        // 清除之前的错误信息
        errorMessage.setValue(null);
        
        // 调用API查询IP地址
        IPAddressService ipAddressService = RetrofitClient.getInstance().getIPAddressService();
        Disposable disposable = ipAddressService.queryIPAddress(ipAddress, apiPriority)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(this::handleSuccessResponse, this::handleError);
        
        // 添加到CompositeDisposable中统一管理
        compositeDisposable.add(disposable);
    }
    
    /**
     * 处理成功响应
     *
     * @param response IP地址查询响应
     */
    private void handleSuccessResponse(IPAddressResponse response) {
        // 停止加载状态
        isLoading.setValue(false);
        
        // 更新IP地址查询响应
        ipAddressResponse.setValue(response);
        
        // 处理原始数据
        Map<String, JsonElement> originalData = new HashMap<>();
        
        // 添加Vore API数据
        if (response.getVoreData() != null) {
            JsonElement voreData = JsonParser.parseString(new Gson().toJson(response.getVoreData()));
            originalData.put("Vore API", voreData);
        }
        
        // 添加IP-API数据
        if (response.getIpapiData() != null) {
            JsonElement ipapiData = JsonParser.parseString(new Gson().toJson(response.getIpapiData()));
            originalData.put("IP-API", ipapiData);
        }
        
        // 添加百度API数据
        if (response.getBaiduData() != null) {
            JsonElement baiduData = JsonParser.parseString(new Gson().toJson(response.getBaiduData()));
            originalData.put("百度API", baiduData);
        }
        
        // 添加IP Info数据
        if (response.getIpInfoData() != null) {
            JsonElement ipInfoData = JsonParser.parseString(new Gson().toJson(response.getIpInfoData()));
            originalData.put("IP Info", ipInfoData);
        }
        
        // 更新原始API数据
        apiOriginalData.setValue(originalData);
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
        Logger.e("IP查询错误: " + throwable.getMessage());
    }
} 