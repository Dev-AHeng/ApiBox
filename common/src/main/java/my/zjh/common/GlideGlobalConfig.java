package my.zjh.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 深度优化的Glide全局配置实现类
 * 包含内存管理、磁盘缓存、请求优先级、图片格式等全方位优化
 *
 * @author AHeng
 * @date 2025/06/8 01:02
 */
@GlideModule
public final class GlideGlobalConfig extends AppGlideModule {
    private static final String TAG = "GlideGlobalConfig";
    
    /**
     * 禁用清单解析以提升初始化速度
     *
     * @return 始终返回false表示禁用清单解析
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
    
    /**
     * 配置Glide全局参数
     *
     * @param context 应用上下文
     * @param builder Glide构建器
     */
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        configureDefaultRequestOptions(builder);
        configureMemoryCache(context, builder);
        configureDiskCache(context, builder);
        configureExecutor(builder);
        configureLogLevel(builder);
    }
    
    /**
     * 配置默认请求参数
     *
     * @param builder Glide构建器
     */
    private void configureDefaultRequestOptions(GlideBuilder builder) {
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        // 内存占用减少50%
                        .format(DecodeFormat.PREFER_RGB_565)
                        // 兼容性处理
                        .disallowHardwareConfig()
                        // 智能缓存策略
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        // 默认优先级
                        .priority(Priority.NORMAL)
                        // 错误占位图
                        .error(R.drawable.ic_red_error)
                        // 加载中占位图
                        // .placeholder(R.drawable.image_24px)
                        // 18秒超时
                        .timeout(18000)
                        // 压缩质量85%
                        .encodeQuality(85)
        );
    }
    
    /**
     * 配置内存缓存
     *
     * @param context 应用上下文
     * @param builder Glide构建器
     */
    private void configureMemoryCache(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                                                  // 内存缓存占用系统分配给应用内存的1/6（默认1/4）
                                                  .setMemoryCacheScreens(1.5f)
                                                  // 图片池占用系统分配给应用内存的1/8（默认1/4）
                                                  .setBitmapPoolScreens(3.0f)
                                                  .build();
        
        // 设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
    }
    
    /**
     * 配置磁盘缓存
     *
     * @param context 应用上下文
     * @param builder Glide构建器
     */
    private void configureDiskCache(Context context, GlideBuilder builder) {
        // 设置磁盘缓存大小为500MB，存储在内部缓存目录
        int diskCacheSizeBytes = 1024 * 1024 * 500; // 500MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", diskCacheSizeBytes));
    }
    
    /**
     * 配置线程池
     *
     * @param builder Glide构建器
     */
    private void configureExecutor(GlideBuilder builder) {
        // 源线程池：用于加载原图，线程数为CPU核心数
        int sourceExecutorThreadCount = Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()));
        builder.setSourceExecutor(GlideExecutor.newSourceBuilder()
                                          .setThreadCount(sourceExecutorThreadCount)
                                          .setName("glide-source-thread")
                                          .build());
        
        // 磁盘缓存线程池：用于磁盘缓存操作，单线程避免竞争
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheBuilder()
                                             .setThreadCount(1)
                                             .setName("glide-disk-cache-thread")
                                             .build());
    }
    
    /**
     * 配置日志级别
     *
     * @param builder Glide构建器
     */
    private void configureLogLevel(GlideBuilder builder) {
        // 生产环境关闭详细日志，调试时可开启
        builder.setLogLevel(Log.ERROR);
    }
    
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
        // 配置高性能OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                                      // 连接超时
                                      .connectTimeout(18, TimeUnit.SECONDS)
                                      // 读取超时
                                      .readTimeout(20, TimeUnit.SECONDS)
                                      // 写入超时
                                      .writeTimeout(20, TimeUnit.SECONDS)
                                      // 调用超时
                                      .callTimeout(60, TimeUnit.SECONDS)
                                      // 自动重试失败的连接
                                      .retryOnConnectionFailure(true)
                                      // 连接池配置：最多保持5个连接，空闲5分钟后清理
                                      .connectionPool(new okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES))
                                      // HTTP/2支持
                                      .protocols(java.util.Arrays.asList(okhttp3.Protocol.HTTP_2, okhttp3.Protocol.HTTP_1_1))
                                      .build();
        
        // 使用优化的OkHttp客户端替换默认网络组件
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory((Call.Factory) client));
    }
    
    /**
     * 清理所有缓存（内存+磁盘）
     * 建议在应用设置中提供此功能
     *
     * @param context 应用上下文
     */
    public static void clearAllCache(@NonNull Context context) {
        new Thread(() -> {
            // 清理磁盘缓存（必须在子线程）
            Glide.get(context).clearDiskCache();
        }).start();
        
        // 清理内存缓存（主线程）
        Glide.get(context).clearMemory();
    }
    
    /**
     * 获取缓存大小信息
     *
     * @param context 应用上下文
     *
     * @return 缓存大小信息字符串
     */
    @SuppressLint("DefaultLocale")
    public static String getCacheInfo(@NonNull Context context) {
        // 由于Glide没有直接提供获取缓存大小的API，返回配置信息
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        long memoryCacheSize = calculator.getMemoryCacheSize();
        long bitmapPoolSize = calculator.getBitmapPoolSize();
        
        return String.format("内存缓存配置: %.1fMB, 图片池配置: %.1fMB",
                memoryCacheSize / 1024.0f / 1024.0f,
                bitmapPoolSize / 1024.0f / 1024.0f);
    }
    
    /**
     * 获取磁盘缓存目录大小（异步）
     *
     * @param context  应用上下文
     * @param callback 回调接口
     */
    public static void getDiskCacheSize(@NonNull Context context, @NonNull CacheSizeCallback callback) {
        new Thread(() -> {
            try {
                java.io.File cacheDir = new java.io.File(context.getCacheDir(), "glide_cache");
                long size = getDirSize(cacheDir);
                // 回到主线程返回结果
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onResult(size));
            } catch (Exception e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onResult(0));
            }
        }).start();
    }
    
    /**
     * 计算目录大小
     *
     * @param dir 目录
     *
     * @return 目录大小（字节）
     */
    private static long getDirSize(java.io.File dir) {
        if (!dir.exists()) {
            return 0;
        }
        long size = 0;
        java.io.File[] files = dir.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                if (file.isDirectory()) {
                    size += getDirSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }
    
    /**
     * 缓存大小回调接口
     */
    public interface CacheSizeCallback {
        /**
         * 返回缓存大小结果
         *
         * @param sizeInBytes 缓存大小（字节）
         */
        void onResult(long sizeInBytes);
    }
    
    /**
     * 预加载图片到缓存
     *
     * @param context  应用上下文
     * @param imageUrl 图片URL
     */
    public static void preloadImage(@NonNull Context context, @NonNull String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .preload();
    }
    
}
