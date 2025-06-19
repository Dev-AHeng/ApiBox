package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.ICPFilingService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.ICPFilingResponse;

/**
 * ICP备案查询ViewModel
 *
 * @author AHeng
 * @date 2025/03/26 15:45
 */
public class ICPFilingViewModel extends ViewModel {
    
    // 网络服务
    private final ICPFilingService icpFilingService;
    
    // RxJava订阅管理
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // 查询结果LiveData
    private final MutableLiveData<ICPFilingResponse> icpFilingResult = new MutableLiveData<>();
    
    // 查询错误LiveData
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    
    /**
     * 构造函数
     */
    public ICPFilingViewModel() {
        icpFilingService = RetrofitClient.getInstance().getICPFilingService();
    }
    
    /**
     * 获取查询结果LiveData
     *
     * @return 查询结果LiveData
     */
    public LiveData<ICPFilingResponse> getIcpFilingResult() {
        return icpFilingResult;
    }
    
    /**
     * 获取错误LiveData
     *
     * @return 错误LiveData
     */
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * 获取加载状态LiveData
     *
     * @return 加载状态LiveData
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    /**
     * 查询域名ICP备案信息
     *
     * @param domain 要查询的域名
     */
    public void queryICPFiling(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            error.setValue("域名不能为空");
            return;
        }
        
        // 设置加载状态
        loading.setValue(true);
        
        // 执行查询
        disposables.add(
                icpFilingService.queryICPFiling(domain)
                        .subscribeOn(Schedulers.io())  // 在IO线程执行网络请求
                        .observeOn(AndroidSchedulers.mainThread())  // 在主线程处理结果
                        .subscribe(
                                // 成功回调
                                response -> {
                                    loading.setValue(false);
                                    
                                    // 成功处理
                                    if ("success".equals(response.getStatus())) {
                                        icpFilingResult.setValue(response);
                                        error.setValue(null);
                                    } else {
                                        // API返回了错误
                                        String errorMsg = response.getMessage();
                                        if (errorMsg == null || errorMsg.isEmpty()) {
                                            errorMsg = response.getMsg();
                                        }
                                        if (errorMsg == null || errorMsg.isEmpty()) {
                                            errorMsg = "查询失败，请稍后重试";
                                        }
                                        error.setValue(errorMsg);
                                        icpFilingResult.setValue(null);
                                    }
                                },
                                // 错误回调
                                throwable -> {
                                    loading.setValue(false);
                                    error.setValue("查询失败：" + throwable.getMessage());
                                    icpFilingResult.setValue(null);
                                }
                        )
        );
    }
    
    /**
     * 清除查询结果
     */
    public void clearResult() {
        icpFilingResult.setValue(null);
        error.setValue(null);
    }
    
    /**
     * 移除所有RxJava订阅，避免内存泄漏
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
} 