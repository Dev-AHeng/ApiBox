package my.zjh.model_sanhaiapi.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.zjh.model_sanhaiapi.api.RandomAnimeService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.RandomAnimeResponse;

/**
 * 随机动漫图ViewModel
 */
public class RandomAnimeViewModel extends AndroidViewModel {
    private static final String TAG = "RandomAnimeViewModel";
    private static final String PREFS_NAME = "anime_favorites";
    private static final String FAVORITES_KEY = "favorites_list";
    
    private final RandomAnimeService animeService;
    private final io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();
    
    private final MutableLiveData<RandomAnimeResponse.Data> animeData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();
    
    public RandomAnimeViewModel(@NonNull Application application) {
        super(application);
        animeService = RetrofitClient.getInstance("https://api.yyy001.com/").create(RandomAnimeService.class);
    }
    
    /**
     * 获取随机动漫图
     * @param category 图片分类
     */
    public void fetchRandomAnime(String category) {
        loading.setValue(true);
        disposables.add(animeService.getRandomAnime(category, "json")
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.rxjava3.observers.DisposableObserver<RandomAnimeResponse>() {
                    @Override
                    public void onNext(RandomAnimeResponse response) {
                        if (response.getCode() == 200 && response.getData() != null) {
                            animeData.setValue(response.getData());
                            checkIfFavorite(response.getData());
                        } else {
                            error.setValue(response.getMessage());
                        }
                        loading.setValue(false);
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        loading.setValue(false);
                        error.setValue("请求失败: " + e.getMessage());
                        Log.e(TAG, "请求失败", e);
                    }
                    
                    @Override
                    public void onComplete() {
                        loading.setValue(false);
                    }
                }));
    }
    
    /**
     * 保存图片到本地
     */
    public void saveImageToStorage() {
        if (animeData.getValue() == null) {
            message.setValue("没有图片可保存");
            return;
        }
        
        Glide.with(getApplication())
                .asBitmap()
                .load(animeData.getValue().getUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String filename = animeData.getValue().getFilename();
                        saveImage(resource, filename);
                    }
                    
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                    
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        message.setValue("下载图片失败");
                    }
                });
    }
    
    /**
     * 保存图片
     */
    private void saveImage(Bitmap bitmap, String filename) {
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File file = new File(directory, filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            
            message.setValue("图片已保存至: " + file.getAbsolutePath());
        } catch (IOException e) {
            message.setValue("保存图片失败: " + e.getMessage());
            Log.e(TAG, "保存图片失败", e);
        }
    }
    
    /**
     * 切换收藏状态
     */
    public void toggleFavorite() {
        RandomAnimeResponse.Data data = animeData.getValue();
        if (data == null) {
            return;
        }
        
        boolean currentState = isFavorite.getValue() != null && isFavorite.getValue();
        if (currentState) {
            removeFavorite(data);
        } else {
            addFavorite(data);
        }
    }
    
    /**
     * 添加收藏
     */
    private void addFavorite(RandomAnimeResponse.Data data) {
        List<Map<String, String>> favorites = getFavorites();
        Map<String, String> favoriteMap = new HashMap<>();
        favoriteMap.put("url", data.getUrl());
        favoriteMap.put("category", data.getCategory());
        favoriteMap.put("filename", data.getFilename());
        favoriteMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        favorites.add(favoriteMap);
        saveFavorites(favorites);
        
        isFavorite.setValue(true);
        message.setValue("已添加到收藏");
    }
    
    /**
     * 移除收藏
     */
    private void removeFavorite(RandomAnimeResponse.Data data) {
        List<Map<String, String>> favorites = getFavorites();
        favorites.removeIf(item -> item.get("url").equals(data.getUrl()));
        saveFavorites(favorites);
        
        isFavorite.setValue(false);
        message.setValue("已从收藏中移除");
    }
    
    /**
     * 检查图片是否已收藏
     */
    private void checkIfFavorite(RandomAnimeResponse.Data data) {
        List<Map<String, String>> favorites = getFavorites();
        boolean found = favorites.stream()
                .anyMatch(item -> item.get("url").equals(data.getUrl()));
        isFavorite.setValue(found);
    }
    
    /**
     * 获取收藏列表
     */
    private List<Map<String, String>> getFavorites() {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String favoritesJson = prefs.getString(FAVORITES_KEY, "");
        
        if (favoritesJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        return gson.fromJson(favoritesJson, type);
    }
    
    /**
     * 保存收藏列表
     */
    private void saveFavorites(List<Map<String, String>> favorites) {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoritesJson = gson.toJson(favorites);
        prefs.edit().putString(FAVORITES_KEY, favoritesJson).apply();
    }
    
    /**
     * 获取图片数据
     */
    public LiveData<RandomAnimeResponse.Data> getAnimeData() {
        return animeData;
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
     * 获取收藏状态
     */
    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }
    
    /**
     * 获取提示消息
     */
    public LiveData<String> getMessage() {
        return message;
    }
    
    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
} 