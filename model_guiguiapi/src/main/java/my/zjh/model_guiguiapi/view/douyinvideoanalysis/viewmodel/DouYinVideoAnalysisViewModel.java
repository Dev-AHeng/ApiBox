package my.zjh.model_guiguiapi.view.douyinvideoanalysis.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_guiguiapi.api.ApiClient;
import my.zjh.model_guiguiapi.view.douyinvideoanalysis.api.DouYinVideoAnalysisApi;

/**
 * DouYinVideoAnalysisViewModel
 *
 * @author AHeng
 * @date 2025/05/03 6:59
 */
public class DouYinVideoAnalysisViewModel extends ViewModel {
    private static final String URL_REGEX = "((?:https?://)?[\\w/\\-?=%.]+\\.(?:douyin|tiktok)\\.com\\S+)";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
    
    private final DouYinVideoAnalysisApi service;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<DouYinVideoAnalysisApi.Response> data = new MediatorLiveData<>();
    private final MutableLiveData<Throwable> error = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> startReq = new MutableLiveData<>();
    private final MutableLiveData<Boolean> completeReq = new MutableLiveData<>();
    
    public DouYinVideoAnalysisViewModel() {
        service = ApiClient.getInstance().createService(DouYinVideoAnalysisApi.class);
    }
    
    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
    
    public LiveData<Boolean> getCompleteReq() {
        return completeReq;
    }
    
    /**
     * 获取解析数据
     *
     * @return 解析数据
     */
    public LiveData<DouYinVideoAnalysisApi.Response> getData() {
        return data;
    }
    
    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public LiveData<Throwable> getError() {
        return error;
    }
    
    /**
     * 获取回调事件
     *
     * @return 回调事件LiveData
     */
    public LiveData<Boolean> getStartReq() {
        return startReq;
    }
    
    /**
     * 解析抖音视频分享链接
     *
     * @param url 抖音视频分享链接
     */
    public void analysis(String url) {
        // 判空
        if (TextUtils.isEmpty(url)) {
            error.setValue(new IllegalArgumentException("请输入抖音视频分享链接"));
            return;
        }
        
        // 正则表达式匹配抖音或TikTok的URL
        Matcher matcher = URL_PATTERN.matcher(url);
        
        // 解析
        if (matcher.find()) {
            String validUrl = matcher.group(1);
            compositeDisposable.add(
                    service.analysis(validUrl)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            // 请求开始回调
                            .doOnSubscribe(disposable -> {
                                // 可以在这里显示加载进度条
                                Log.i("RxJava", "请求开始");
                                startReq.setValue(true);
                            })
                            .doOnNext(data -> {
                                Log.d("RxJava", "请求成功回调(每次收到数据): " + data);
                            })
                            .doOnError(error -> {
                                Log.e("RxJava", "请求错误回调", error);
                            })
                            .doOnComplete(() -> {
                                Log.i("RxJava", "请求完成回调");
                            })
                            .doFinally(() -> {
                                // 可以在这里隐藏加载进度条
                                Log.i("RxJava", "请求终止回调(无论成功或失败)");
                                completeReq.setValue(true);
                            })
                            .subscribe(data::setValue, error::setValue)
            );
        } else {
            error.setValue(new IllegalArgumentException("无效的抖音视频分享链接"));
        }
    }
}
