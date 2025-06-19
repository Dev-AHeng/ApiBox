package my.zjh.model_guiguiapi.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.HashMap;
import java.util.Map;

/**
 * Drawable资源工具类
 *
 * @author AHeng
 * @date 2025/04/16 05:29
 */
public class DrawableUtils {
    private static final String TAG = "DrawableUtils";
    
    // 资源缓存，提高性能
    private static final Map<String, Integer> S_DRAWABLE_CACHE = new HashMap<>();
    
    /**
     * 通过资源名称获取Drawable
     *
     * @param context      上下文对象
     * @param drawableName 资源名称(不带扩展名)
     *
     * @return Drawable对象，如果找不到返回null
     */
    @Nullable
    public static Drawable getDrawableByName(@NonNull Context context, @NonNull String drawableName) {
        if (drawableName.isEmpty()) {
            Log.w(TAG, "可绘制名称不能为null或空");
            return null;
        }
        
        // 先从缓存中查找
        Integer resId = S_DRAWABLE_CACHE.get(drawableName);
        
        if (resId == null) {
            // 缓存中没有，通过Resources获取 // 资源名称, 资源类型, 包名
            resId = context.getResources().getIdentifier(
                    drawableName,
                    "drawable",
                    context.getPackageName()
            );
            
            // 检查资源是否存在
            if (resId != 0) {
                // 放入缓存
                S_DRAWABLE_CACHE.put(drawableName, resId);
            } else {
                Log.w(TAG, "未找到可绘制内容: " + drawableName);
                return null;
            }
        }
        
        try {
            // 使用AppCompatResources获取Drawable(支持矢量图)
            Drawable drawable = AppCompatResources.getDrawable(context, resId);
            if (drawable != null) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                // 设置Drawable的边界为固有尺寸
                drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            }
            return drawable;
        } catch (Exception e) {
            Log.e(TAG, "无法提取: " + drawableName, e);
            return null;
        }
    }
    
    /**
     * 清除资源缓存
     */
    public static void clearCache() {
        S_DRAWABLE_CACHE.clear();
    }
    
    /**
     * 预加载资源到缓存
     *
     * @param context       上下文对象
     * @param drawableNames 要预加载的资源名称数组
     */
    public static void preloadDrawables(@NonNull Context context, @NonNull String[] drawableNames) {
        for (String name : drawableNames) {
            getDrawableByName(context, name);
        }
    }
}
