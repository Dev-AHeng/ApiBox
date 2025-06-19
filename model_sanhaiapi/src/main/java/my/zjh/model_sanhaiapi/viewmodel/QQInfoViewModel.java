package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.QQInfoService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.QQInfoResponse;

/**
 * QQ基础信息ViewModel
 *
 * @author AHeng
 * @date 2025/03/26 18:50
 */
public class QQInfoViewModel extends ViewModel {
    
    // 网络服务
    private final QQInfoService qqInfoService;
    
    // RxJava订阅管理
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // 查询结果LiveData
    private final MutableLiveData<QQInfoResponse> qqInfoResult = new MutableLiveData<>();
    
    // 查询错误LiveData
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    // 加载状态LiveData
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    
    /**
     * 构造函数
     */
    public QQInfoViewModel() {
        qqInfoService = RetrofitClient.getInstance().getQQInfoService();
    }
    
    /**
     * 获取查询结果LiveData
     *
     * @return 查询结果LiveData
     */
    public LiveData<QQInfoResponse> getQqInfoResult() {
        return qqInfoResult;
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
     * 查询QQ号码基础信息
     *
     * @param qqNumber 要查询的QQ号码
     */
    public void queryQQInfo(String qqNumber) {
        if (qqNumber == null || qqNumber.trim().isEmpty()) {
            error.setValue("QQ号码不能为空");
            return;
        }
        
        // 设置加载状态
        loading.setValue(true);
        
        // 执行查询
        disposables.add(
                qqInfoService.queryQQInfo(qqNumber)
                        .subscribeOn(Schedulers.io())  // 在IO线程执行网络请求
                        .observeOn(AndroidSchedulers.mainThread())  // 在主线程处理结果
                        .subscribe(
                                // 成功回调
                                response -> {
                                    loading.setValue(false);
                                    
                                    // 成功处理
                                    if (response.isSuccess()) {
                                        qqInfoResult.setValue(response);
                                        error.setValue(null);
                                    } else {
                                        // API返回了错误
                                        error.setValue("查询失败，错误代码：" + response.getCode());
                                        qqInfoResult.setValue(null);
                                    }
                                },
                                // 错误回调
                                throwable -> {
                                    loading.setValue(false);
                                    error.setValue("查询失败：" + throwable.getMessage());
                                    qqInfoResult.setValue(null);
                                }
                        )
        );
    }
    
    /**
     * 清除查询结果
     */
    public void clearResult() {
        qqInfoResult.setValue(null);
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