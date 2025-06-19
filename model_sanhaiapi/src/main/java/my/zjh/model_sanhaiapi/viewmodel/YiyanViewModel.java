package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.api.YiyanService;
import my.zjh.model_sanhaiapi.model.YiyanResponse;

/**
 * 一言ViewModel
 *
 * @author AHeng
 * @date 2025/03/27
 */
public class YiyanViewModel extends ViewModel {
    
    // 用于管理RxJava订阅
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // 错误信息LiveData
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // 一言响应LiveData
    private final MutableLiveData<YiyanResponse> yiyanResponse = new MutableLiveData<>();
    
    // 显示的一言内容
    private final MutableLiveData<String> yiyanContent = new MutableLiveData<>();
    
    // 一言来源提示
    private final MutableLiveData<String> yiyanTips = new MutableLiveData<>();
    
    // 当前获取的一言内容
    private String currentYiyan = "";
    
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
     * 获取一言响应LiveData
     *
     * @return 一言响应LiveData
     */
    public LiveData<YiyanResponse> getYiyanResponse() {
        return yiyanResponse;
    }
    
    /**
     * 获取一言内容LiveData
     *
     * @return 一言内容LiveData
     */
    public LiveData<String> getYiyanContent() {
        return yiyanContent;
    }
    
    /**
     * 获取一言提示LiveData
     *
     * @return 一言提示LiveData
     */
    public LiveData<String> getYiyanTips() {
        return yiyanTips;
    }
    
    /**
     * 获取当前一言内容
     *
     * @return 当前一言内容
     */
    public String getCurrentYiyan() {
        return currentYiyan;
    }
    
    /**
     * 获取一言
     */
    public void getYiyan() {
        // 设置加载状态
        isLoading.setValue(true);
        
        // 清除之前的错误信息
        errorMessage.setValue(null);
        
        // 调用API获取一言
        YiyanService yiyanService = RetrofitClient.getInstance().getYiyanService();
        Disposable disposable = yiyanService.getYiyan("UTF-8", "json")
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(this::handleSuccessResponse, this::handleError);
        
        // 添加到CompositeDisposable中统一管理
        compositeDisposable.add(disposable);
    }
    
    /**
     * 处理成功响应
     *
     * @param response 一言响应
     */
    private void handleSuccessResponse(YiyanResponse response) {
        // 停止加载状态
        isLoading.setValue(false);
        
        // 更新一言响应
        yiyanResponse.setValue(response);
        
        // 检查状态码
        if (response.getCode() == 200) {
            // 更新一言内容
            yiyanContent.setValue(response.getMessage());
            yiyanTips.setValue(response.getTips());
            currentYiyan = response.getMessage();
        } else {
            // 设置错误信息
            errorMessage.setValue("获取一言失败，状态码：" + response.getCode());
        }
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
        Logger.e("获取一言错误: " + throwable.getMessage());
    }
} 