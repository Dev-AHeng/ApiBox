package my.zjh.model_guiguiapi.view.bingcoversby7days.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_guiguiapi.api.ApiClient;
import my.zjh.model_guiguiapi.view.bingcoversby7days.api.BingCoversBy7DaysApi;

/**
 * 必应近七天封面图片ViewModel
 *
 * @author AHeng
 * @date 2025/06/09 05:25
 */
public class BingCoversBy7DaysViewModel extends ViewModel {
    
    private final BingCoversBy7DaysApi bingCoversApi;
    private final CompositeDisposable compositeDisposable;
    
    /**
     * 图片列表LiveData
     */
    private final MutableLiveData<List<BingCoversBy7DaysApi.BingImageData>> imageListLiveData = new MutableLiveData<>();
    
    /**
     * 错误信息LiveData
     */
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    /**
     * 加载状态LiveData
     */
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    
    public BingCoversBy7DaysViewModel() {
        bingCoversApi = ApiClient.getInstance().createService(BingCoversBy7DaysApi.class);
        compositeDisposable = new CompositeDisposable();
    }
    
    /**
     * 获取必应近七天封面图片
     */
    public void getBingCoversBy7Days() {
        
        // 清除之前的错误信息
        clearError();
        
        // 设置加载状态
        loadingLiveData.setValue(true);
        
        // 执行网络请求
        compositeDisposable.add(
                bingCoversApi.getBingCoversBy7Days()
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
     * 处理请求成功
     *
     * @param response 响应数据
     */
    private void handleSuccess(BingCoversBy7DaysApi.BingCoversResponse response) {
        loadingLiveData.setValue(false);
        
        if (response != null && response.getCode() == 200) {
            List<BingCoversBy7DaysApi.BingImageData> imageData = response.getData();
            if (imageData != null && !imageData.isEmpty()) {
                imageListLiveData.setValue(imageData);
            } else {
                errorLiveData.setValue("暂无图片数据");
            }
        } else {
            String errorMsg = response != null && response.getMsg() != null ?
                                      response.getMsg() : "获取图片失败，请稍后重试";
            errorLiveData.setValue(errorMsg);
        }
    }
    
    /**
     * 处理请求错误
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
    
    /**
     * 格式化日期显示
     *
     * @param timeId 时间ID，格式如20250609
     *
     * @return 格式化后的日期字符串
     */
    public String formatDate(String timeId) {
        if (timeId != null && timeId.length() == 8) {
            try {
                String month = timeId.substring(4, 6);
                String day = timeId.substring(6, 8);
                return month + "-" + day;
            } catch (Exception e) {
                return timeId;
            }
        }
        return timeId != null ? timeId : "";
    }
    
    /**
     * 安全获取字符串值
     *
     * @param value 原始值
     *
     * @return 处理后的字符串
     */
    public String safeGetString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "暂无信息";
    }
    
    /**
     * 获取最佳质量的图片URL
     *
     * @param imageData 图片数据
     *
     * @return 图片URL
     */
    public String getBestQualityImageUrl(BingCoversBy7DaysApi.BingImageData imageData) {
        if (imageData == null) {
            return null;
        }
        
        // 优先使用imageUrl
        if (imageData.getImageUrl() != null && !imageData.getImageUrl().trim().isEmpty()) {
            return imageData.getImageUrl();
        }
        
        // 如果没有imageUrl，尝试从imageSize中获取最高质量的图片
        BingCoversBy7DaysApi.ImageSize imageSize = imageData.getImageSize();
        if (imageSize != null) {
            if (imageSize.getUhd4K() != null && !imageSize.getUhd4K().trim().isEmpty()) {
                return imageSize.getUhd4K();
            }
            if (imageSize.getHd1920x1080() != null && !imageSize.getHd1920x1080().trim().isEmpty()) {
                return imageSize.getHd1920x1080();
            }
            if (imageSize.getHd1366x768() != null && !imageSize.getHd1366x768().trim().isEmpty()) {
                return imageSize.getHd1366x768();
            }
        }
        
        return null;
    }
    
    /**
     * 获取下载用的高质量图片URL
     *
     * @param imageData 图片数据
     *
     * @return 高质量图片URL
     */
    public String getDownloadImageUrl(BingCoversBy7DaysApi.BingImageData imageData) {
        if (imageData == null || imageData.getImageSize() == null) {
            return getBestQualityImageUrl(imageData);
        }
        
        BingCoversBy7DaysApi.ImageSize imageSize = imageData.getImageSize();
        
        // 下载时优先选择4K或1920x1080
        if (imageSize.getUhd4K() != null && !imageSize.getUhd4K().trim().isEmpty()) {
            return imageSize.getUhd4K();
        }
        if (imageSize.getHd1920x1080() != null && !imageSize.getHd1920x1080().trim().isEmpty()) {
            return imageSize.getHd1920x1080();
        }
        
        return getBestQualityImageUrl(imageData);
    }
    
    // Getter方法
    public MutableLiveData<List<BingCoversBy7DaysApi.BingImageData>> getImageListLiveData() {
        return imageListLiveData;
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
