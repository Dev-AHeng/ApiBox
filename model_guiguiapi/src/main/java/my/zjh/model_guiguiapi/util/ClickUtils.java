package my.zjh.model_guiguiapi.util;

import android.os.SystemClock;
import android.view.View;

/**
 * 点击防抖工具类
 *
 * @author AHeng
 * @date 2025/04/10 08:10
 */
public class ClickUtils {
    // 上次点击时间
    private static long lastClickTime = 0;
    // 默认防抖时间间隔（毫秒）
    private static final long DEFAULT_DEBOUNCE_TIME = 800;

    /**
     * 私有构造函数，防止实例化
     */
    private ClickUtils() {
        throw new UnsupportedOperationException("无法实例化");
    }

    /**
     * 是否是快速点击
     *
     * @return true: 是快速点击, false: 不是快速点击
     */
    public static boolean isFastClick() {
        return isFastClick(DEFAULT_DEBOUNCE_TIME);
    }

    /**
     * 是否是快速点击
     *
     * @param debounceTime 防抖时间（毫秒）
     * @return true: 是快速点击, false: 不是快速点击
     */
    public static boolean isFastClick(long debounceTime) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime < debounceTime) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    /**
     * 设置防抖点击监听器
     *
     * @param onClickListener 点击监听器
     * @return 防抖后的点击监听器
     */
    public static View.OnClickListener getDebounceClickListener(View.OnClickListener onClickListener) {
        return getDebounceClickListener(onClickListener, DEFAULT_DEBOUNCE_TIME);
    }

    /**
     * 设置防抖点击监听器
     *
     * @param onClickListener 点击监听器
     * @param debounceTime 防抖时间（毫秒）
     * @return 防抖后的点击监听器
     */
    public static View.OnClickListener getDebounceClickListener(View.OnClickListener onClickListener, long debounceTime) {
        return v -> {
            if (!isFastClick(debounceTime)) {
                onClickListener.onClick(v);
            }
        };
    }
} 