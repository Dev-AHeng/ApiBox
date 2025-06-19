package my.zjh.model_sanhaiapi.utils;

import android.view.View;

/**
 * ViewUtils
 *
 * @author AHeng
 * @date 2025/04/03 06:19
 */
public class ViewUtils {
    /**
     * 私有构造函数，防止实例化
     */
    private ViewUtils() {
        throw new UnsupportedOperationException("无法实例化");
    }
    
    /**
     * 显示视图
     *
     * @param view 要显示的视图
     */
    public static void show(View view) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 隐藏视图
     *
     * @param view 要隐藏的视图
     */
    public static void hide(View view) {
        if (view != null && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }
    
    /**
     * 隐藏视图并保留布局空间
     *
     * @param view 要隐藏的视图
     */
    public static void invisible(View view) {
        if (view != null && view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 切换视图的显示状态
     *
     * @param view 要切换的视图
     */
    public static void toggleVisibility(View view) {
        if (view != null) {
            int currentVisibility = view.getVisibility();
            if (currentVisibility == View.VISIBLE) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
}
