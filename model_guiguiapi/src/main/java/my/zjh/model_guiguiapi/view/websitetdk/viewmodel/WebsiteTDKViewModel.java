package my.zjh.model_guiguiapi.view.websitetdk.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_guiguiapi.api.ApiClient;
import my.zjh.model_guiguiapi.view.websitetdk.api.WebsiteTDKApi;


/**
 * 网站TDK描述查询-本地-API接口ViewModel
 */
public class WebsiteTDKViewModel extends ViewModel {
    
    private final WebsiteTDKApi websiteTDKApi;
    private final CompositeDisposable compositeDisposable;
    
    /**
     * 查询结果LiveData
     */
    private final MutableLiveData<WebsiteTDKApi.WebsiteTDKModel> queryResultLiveData = new MutableLiveData<>();
    
    /**
     * 错误信息LiveData
     */
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    /**
     * 加载状态LiveData
     */
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    
    public WebsiteTDKViewModel() {
        websiteTDKApi = ApiClient.getInstance().createService(WebsiteTDKApi.class);
        compositeDisposable = new CompositeDisposable();
    }
    
    /**
     * 查询网站TDK信息
     *
     * @param url 网站URL
     */
    public void queryWebsiteTDK(String url) {
        
        // 输入验证并获取处理后的URL
        String processedUrl = getValidatedUrl(url);
        if (processedUrl == null) {
            return;
        }
        
        // 设置加载状态
        loadingLiveData.setValue(true);
        
        // 执行查询
        compositeDisposable.add(
                websiteTDKApi.getWebsiteTDK(processedUrl)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .timeout(30, TimeUnit.SECONDS)
                        .subscribe(
                                this::handleSuccess,
                                this::handleError
                        )
        );
    }
    
    /**
     * 验证输入并返回处理后的URL
     *
     * @param url 要验证的URL
     *
     * @return 处理后的有效URL，如果无效则返回null
     */
    private String getValidatedUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            errorLiveData.setValue("请输入网站URL");
            return null;
        }
        
        String trimmedUrl = url.trim();
        
        // 如果没有协议，自动添加http://
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            trimmedUrl = "http://" + trimmedUrl;
        }
        
        // 验证URL格式
        if (!Patterns.WEB_URL.matcher(trimmedUrl).matches()) {
            errorLiveData.setValue("请输入有效的URL格式");
            return null;
        }
        
        return trimmedUrl;
    }
    
    /**
     * 处理查询成功
     *
     * @param result 查询结果
     */
    private void handleSuccess(WebsiteTDKApi.WebsiteTDKModel result) {
        loadingLiveData.setValue(false);
        
        if (result != null && result.getCode() == 200) {
            queryResultLiveData.setValue(result);
        } else {
            String errorMsg = result != null && result.getMsg() != null ?
                                      result.getMsg() : "查询失败，请稍后重试";
            errorLiveData.setValue(errorMsg);
        }
    }
    
    /**
     * 处理查询错误
     *
     * @param throwable 错误信息
     */
    private void handleError(Throwable throwable) {
        loadingLiveData.setValue(false);
        
        String errorMessage = "网络请求失败，请检查网络连接";
        if (throwable != null) {
            String message = throwable.getMessage();
            if (message != null) {
                if (message.contains("timeout")) {
                    errorMessage = "请求超时，请稍后重试";
                } else if (message.contains("ConnectException") || message.contains("UnknownHostException")) {
                    errorMessage = "网络连接失败，请检查网络";
                } else if (message.contains("SocketTimeoutException")) {
                    errorMessage = "网络超时，请稍后重试";
                }
            }
        }
        
        errorLiveData.setValue(errorMessage);
    }
    
    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }
    
    // Getter方法
    public MutableLiveData<WebsiteTDKApi.WebsiteTDKModel> getQueryResultLiveData() {
        return queryResultLiveData;
    }
    
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    
    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
} 