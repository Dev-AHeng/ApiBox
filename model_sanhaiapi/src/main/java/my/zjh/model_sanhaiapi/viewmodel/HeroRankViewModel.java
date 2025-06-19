package my.zjh.model_sanhaiapi.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.HeroRankService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.HeroRank;
import my.zjh.model_sanhaiapi.model.HeroRankDetailResponse;

/**
 * 王者荣耀英雄战力排名ViewModel
 */
public class HeroRankViewModel extends AndroidViewModel {
    private static final String TAG = "HeroRankViewModel";
    
    private final HeroRankService heroRankService;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // LiveData
    private final MutableLiveData<List<HeroRank.Hero>> heroes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    // 英雄战力排名详情LiveData
    private final MutableLiveData<HeroRankDetailResponse> heroRankDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> detailLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> detailError = new MutableLiveData<>();
    
    // 保存原始英雄列表，用于筛选
    private List<HeroRank.Hero> originalHeroes;
    
    public HeroRankViewModel(@NonNull Application application) {
        super(application);
        heroRankService = RetrofitClient.getInstance("https://api.yyy001.com/").create(HeroRankService.class);
    }
    
    /**
     * 获取英雄战力排名数据
     */
    public void fetchHeroRank() {
        loading.setValue(true);
        
        disposables.add(heroRankService.getHeroRank()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<HeroRank>() {
                    @Override
                    public void onNext(HeroRank heroRank) {
                        if (heroRank != null && "200".equals(heroRank.getCode())) {
                            // 保存原始数据
                            originalHeroes = heroRank.getHeroes();
                            heroes.setValue(originalHeroes);
                            error.setValue(null);
                        } else {
                            error.setValue("获取数据失败: " + (heroRank != null ? heroRank.getDesc() : "未知错误"));
                        }
                        loading.setValue(false);
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "获取英雄战力排名失败", e);
                        error.setValue("获取数据失败: " + e.getMessage());
                        loading.setValue(false);
                    }
                    
                    @Override
                    public void onComplete() {
                        loading.setValue(false);
                    }
                }));
    }
    
    /**
     * 获取英雄战力排名详情
     * 
     * @param heroName 英雄名称，如：李白
     * @param zone 游戏区域代码，iwx（iOS微信区）、awx（安卓微信区）、iqq（iOS QQ区）、aqq（安卓QQ区）
     * @param type 查询类型，min（最低战力）、max（最高战力）、all（全部数据），默认为all
     */
    public void fetchHeroRankDetail(String heroName, String zone, String type) {
        detailLoading.setValue(true);
        
        disposables.add(heroRankService.getHeroRankDetail(heroName, zone, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<HeroRankDetailResponse>() {
                    @Override
                    public void onNext(HeroRankDetailResponse response) {
                        if (response != null && "200".equals(response.getCode())) {
                            heroRankDetail.setValue(response);
                            detailError.setValue(null);
                        } else {
                            detailError.setValue("获取英雄战力排名详情失败: " + (response != null ? response.getDesc() : "未知错误"));
                        }
                        detailLoading.setValue(false);
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "获取英雄战力排名详情失败", e);
                        detailError.setValue("获取英雄战力排名详情失败: " + e.getMessage());
                        detailLoading.setValue(false);
                    }
                    
                    @Override
                    public void onComplete() {
                        detailLoading.setValue(false);
                    }
                }));
    }
    
    /**
     * 根据英雄类型过滤英雄
     */
    public void filterHeroesByType(int heroType) {
        if (originalHeroes == null) {
            return;
        }
        
        if (heroType == 0) {
            // 全部英雄
            heroes.setValue(originalHeroes);
        } else {
            // 使用Java 8的Stream进行过滤
            List<HeroRank.Hero> filteredHeroes = originalHeroes.stream()
                    .filter(hero -> hero.getHeroType() == heroType)
                    .collect(java.util.stream.Collectors.toList());
            
            heroes.setValue(filteredHeroes);
        }
    }
    
    /**
     * 获取英雄数据
     */
    public LiveData<List<HeroRank.Hero>> getHeroes() {
        return heroes;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    /**
     * 获取错误信息
     */
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * 获取英雄战力排名详情
     */
    public LiveData<HeroRankDetailResponse> getHeroRankDetail() {
        return heroRankDetail;
    }
    
    /**
     * 获取详情加载状态
     */
    public LiveData<Boolean> getDetailLoading() {
        return detailLoading;
    }
    
    /**
     * 获取详情错误信息
     */
    public LiveData<String> getDetailError() {
        return detailError;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理资源，避免内存泄漏
        disposables.clear();
    }
} 