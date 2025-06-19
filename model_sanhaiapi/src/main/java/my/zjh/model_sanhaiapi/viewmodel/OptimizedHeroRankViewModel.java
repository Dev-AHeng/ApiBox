package my.zjh.model_sanhaiapi.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.HeroRankService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.HeroRank;

/**
 * 优化的王者荣耀英雄战力排名ViewModel
 */
public class OptimizedHeroRankViewModel extends AndroidViewModel {
    private static final String TAG = "OptimizedHeroRankViewModel";
    
    private final HeroRankService heroRankService;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // LiveData
    private final MutableLiveData<List<HeroRank.Hero>> heroes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    // 保存原始数据，用于筛选
    private List<HeroRank.Hero> originalHeroes;
    
    public OptimizedHeroRankViewModel(@NonNull Application application) {
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
                            originalHeroes = new ArrayList<>(heroRank.getHeroes());
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
     * 根据英雄类型过滤英雄
     */
    public void filterHeroesByType(int heroType) {
        if (originalHeroes == null || originalHeroes.isEmpty()) {
            return;
        }
        
        if (heroType == 0) {
            // 如果选择"全部"，显示原始列表
            heroes.setValue(new ArrayList<>(originalHeroes));
        } else {
            // 根据英雄类型筛选
            List<HeroRank.Hero> filteredHeroes = originalHeroes.stream()
                    .filter(hero -> hero.getHeroType() == heroType)
                    .collect(Collectors.toList());
            
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
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理资源，避免内存泄漏
        disposables.clear();
    }
} 