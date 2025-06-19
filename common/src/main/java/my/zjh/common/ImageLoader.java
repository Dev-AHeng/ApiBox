package my.zjh.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

/**
 * Glide加载工具类
 * 提供统一的图片加载入口和常用方法封装
 *
 * @author AHeng
 * @date 2025/06/8 01:04
 */
public class ImageLoader {
    
    /**
     * 标准图片加载方法
     * <p>
     * 使用:
     * ImageLoader.load(binding.musicCover, data.getAvatar());
     *
     * @param view 目标ImageView
     * @param url  图片URL
     */
    public static void load(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(view);
    }
    
    /**
     * 带回调的图片加载
     *
     * @param view     目标ImageView
     * @param url      图片URL
     * @param listener 加载监听器
     */
    public static void load(ImageView view, String url, RequestListener<Drawable> listener) {
        Glide.with(view.getContext())
                .load(url)
                .addListener(listener)
                .into(view);
    }
    
    /**
     * 圆形图片加载
     *
     * @param view 目标ImageView
     * @param url  图片URL
     */
    public static void loadCircle(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }
    
    /**
     * 获取特定缓存策略的RequestOptions
     *
     * @param cacheType 缓存类型枚举
     *
     * @return 配置好的RequestOptions实例
     */
    public static RequestOptions getCacheOptions(CacheType cacheType) {
        switch (cacheType) {
            case NONE:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE);
            case ALL:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            case RESOURCE:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            case DATA:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA);
            default:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        }
    }
    
    /**
     * 清除磁盘缓存（需在子线程执行）
     *
     * @param context 上下文
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
    
    /**
     * 清除内存缓存（需在主线程执行）
     *
     * @param context 上下文
     */
    public static void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }
    
    public enum CacheType {
        NONE,       // 不使用缓存
        ALL,        // 缓存原始数据和转换后数据
        RESOURCE,   // 只缓存转换后的数据
        DATA,       // 只缓存原始数据
        AUTO        // 自动选择
    }
}
