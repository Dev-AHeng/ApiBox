package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.MiguMusicService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.MiguMusic;

/**
 * 咪咕音乐ViewModel
 */
public class MiguMusicViewModel extends ViewModel {
    
    private final MiguMusicService miguMusicService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    // 音乐搜索结果LiveData
    private final MutableLiveData<MiguMusic.SearchResult> searchResultLiveData = new MutableLiveData<>();
    // 当前选中的音乐详情LiveData
    private final MutableLiveData<MiguMusic.MusicDetail> musicDetailLiveData = new MutableLiveData<>();
    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    // 错误信息LiveData
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    
    public MiguMusicViewModel() {
        miguMusicService = RetrofitClient.getInstance().getMiguMusicService();
    }
    
    /**
     * 搜索音乐
     *
     * @param keyword 搜索关键词
     * @param num 结果数量
     */
    public void searchMusic(String keyword, int num) {
        isLoadingLiveData.setValue(true);
        
        Disposable disposable = miguMusicService.searchMusic(keyword, "json", "1", num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        searchResult -> {
                            searchResultLiveData.setValue(searchResult);
                            isLoadingLiveData.setValue(false);
                        },
                        // onError
                        throwable -> {
                            errorMessageLiveData.setValue("搜索失败: " + throwable.getMessage());
                            isLoadingLiveData.setValue(false);
                        }
                );
        
        compositeDisposable.add(disposable);
    }
    
    /**
     * 获取音乐详情
     *
     * @param keyword 搜索关键词
     * @param position 结果位置
     */
    public void getMusicDetail(String keyword, int position) {
        isLoadingLiveData.setValue(true);
        
        Disposable disposable = miguMusicService.getMusicDetail(keyword, position, "json", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        musicDetail -> {
                            musicDetailLiveData.setValue(musicDetail);
                            isLoadingLiveData.setValue(false);
                        },
                        // onError
                        throwable -> {
                            errorMessageLiveData.setValue("获取详情失败: " + throwable.getMessage());
                            isLoadingLiveData.setValue(false);
                        }
                );
        
        compositeDisposable.add(disposable);
    }
    
    /**
     * 获取搜索结果LiveData
     */
    public LiveData<MiguMusic.SearchResult> getSearchResultLiveData() {
        return searchResultLiveData;
    }
    
    /**
     * 获取音乐详情LiveData
     */
    public LiveData<MiguMusic.MusicDetail> getMusicDetailLiveData() {
        return musicDetailLiveData;
    }
    
    /**
     * 获取加载状态LiveData
     */
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }
    
    /**
     * 获取错误信息LiveData
     */
    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理所有订阅，防止内存泄漏
        compositeDisposable.clear();
    }
} 