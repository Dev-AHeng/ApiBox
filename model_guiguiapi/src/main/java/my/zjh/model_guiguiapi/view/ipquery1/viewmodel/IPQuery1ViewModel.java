package my.zjh.model_guiguiapi.view.ipquery1.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_guiguiapi.api.ApiClient;
import my.zjh.model_guiguiapi.view.ipquery1.api.IPQueryApi;
import my.zjh.model_guiguiapi.view.ipquery1.model.IPQueryResponse;

/**
 * IP地址归属地详情查询1 ViewModel
 *
 * @author AHeng
 * @date 2025/06/08 16:24
 */
public class IPQuery1ViewModel extends ViewModel {
    
    // IP地址验证正则表达式
    private static final String IP_PATTERN = 
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    
    private final Pattern ipPattern = Pattern.compile(IP_PATTERN);
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // LiveData用于观察数据变化
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<IPQueryResponse> queryResult = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private IPQueryApi apiService;
    
    public IPQuery1ViewModel() {
        initApiService();
    }
    
    /**
     * 初始化API服务
     */
    private void initApiService() {
        apiService = ApiClient.getInstance().createService(IPQueryApi.class);
    }
    
    /**
     * 验证IP地址格式
     *
     * @param ip IP地址字符串
     * @return 是否为有效的IP地址
     */
    public boolean isValidIP(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return ipPattern.matcher(ip.trim()).matches();
    }
    
    /**
     * 查询IP地址信息
     *
     * @param ip 要查询的IP地址
     */
    public void queryIPInfo(String ip) {
        // 清除之前的错误信息和结果
        errorMessage.setValue(null);
        queryResult.setValue(null);
        
        // 验证IP地址格式
        if (!isValidIP(ip)) {
            errorMessage.setValue("请输入有效的IP地址格式，例如：192.168.1.1");
            return;
        }
        
        // 开始加载
        isLoading.setValue(true);
        
        // 使用RxJava3发起网络请求
        disposables.add(
                apiService.queryIPInfo(ip.trim())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleSuccess,
                                this::handleError
                        )
        );
    }
    
    /**
     * 处理成功响应
     *
     * @param response API响应结果
     */
    private void handleSuccess(IPQueryResponse response) {
        isLoading.setValue(false);
        
        if (response != null && response.getStatus() == 200) {
            queryResult.setValue(response);
        } else {
            String statusCode = response != null ? String.valueOf(response.getStatus()) : "未知";
            errorMessage.setValue("查询失败，API返回状态码：" + statusCode);
        }
    }
    
    /**
     * 处理错误响应
     *
     * @param throwable 异常信息
     */
    private void handleError(Throwable throwable) {
        isLoading.setValue(false);
        
        String errorMsg = throwable.getMessage();
        if (errorMsg != null) {
            if (errorMsg.contains("timeout")) {
                errorMessage.setValue("请求超时，请检查网络连接后重试");
            } else if (errorMsg.contains("Unable to resolve host") || errorMsg.contains("No address associated")) {
                errorMessage.setValue("无法连接到服务器，请检查网络连接");
            } else if (errorMsg.contains("ConnectException")) {
                errorMessage.setValue("网络连接失败，请检查网络设置");
            } else {
                errorMessage.setValue("查询失败：" + errorMsg);
            }
        } else {
            errorMessage.setValue("查询失败：未知错误");
        }
    }
    
    /**
     * 格式化坐标信息
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return 格式化的坐标字符串
     */
    public String formatCoordinates(String longitude, String latitude) {
        if (longitude != null && latitude != null && 
            !longitude.trim().isEmpty() && !latitude.trim().isEmpty()) {
            return longitude + ", " + latitude;
        }
        return "暂无坐标信息";
    }
    
    /**
     * 安全获取字符串值
     *
     * @param value 原始值
     * @return 处理后的字符串
     */
    public String safeGetString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "暂无信息";
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理RxJava订阅，防止内存泄漏
        disposables.clear();
    }
}
