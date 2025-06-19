package my.zjh.model_guiguiapi.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dev_Heng
 */
public final class SizeUtils {
    
    private SizeUtils() {
        throw new UnsupportedOperationException("不能实例化");
    }
    
    /**
     * dp值转换为px值
     *
     * @param dpValue dp值
     *
     * @return px值
     */
    public static int dp2px(final float dpValue) {
        if (dpValue == 0) {
            return 0;
        }
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + (dpValue >= 0 ? 0.5f : -0.5f));
    }
    
    /**
     * px值转换为dp值
     *
     * @param pxValue px值
     *
     * @return dp值
     */
    public static int px2dp(final float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + (pxValue >= 0 ? 0.5f : -0.5f));
    }
    
    /**
     * sp值转换为px值
     *
     * @param spValue sp值
     *
     * @return px值
     */
    public static int sp2px(final float spValue) {
        if (spValue == 0) {
            return 0;
        }
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + (spValue >= 0 ? 0.5f : -0.5f));
    }
    
    /**
     * px值转换为sp值
     *
     * @param pxValue px值
     *
     * @return sp值
     */
    public static int px2sp(final float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + (pxValue >= 0 ? 0.5f : -0.5f));
    }
    
    /**
     * 将包含维度值的数据转换为最终的浮点值
     * 参数 <var>unit</var> 和 <var>value</var>
     * 与 {@link TypedValue#TYPE_DIMENSION} 中的含义相同
     *
     * @param value 要应用单位的值
     * @param unit  要转换的单位
     *
     * @return 根据其单位乘以适当度量值后的复杂浮点值
     */
    public static float applyDimension(final float value, final int unit) {
        if (value == 0) {
            return 0;
        }
        
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
            default:
                throw new IllegalArgumentException("不支持的单位类型: " + unit);
        }
    }
    
    /**
     * 强制获取视图大小
     * <p>示例：</p>
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
     *     @Override
     *     public void onGetSize(final View view) {
     *         view.getWidth();
     *     }
     * });
     * </pre>
     *
     * @param view     视图
     * @param listener 获取大小的监听器
     */
    public static void forceGetViewSize(final View view, final OnGetSizeListener listener) {
        if (view == null) {
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onGetSize(view);
                }
            }
        });
    }
    
    
    /**
     * 获取导航栏高度
     *
     * @param context 上下文
     *
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    
    /**
     * 检查设备是否有导航栏
     *
     * @param context 上下文
     *
     * @return 如果有导航栏返回true，否则返回false
     */
    public static boolean hasNavigationBar(Context context) {
        if (context == null) {
            return false;
        }
        
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId > 0) {
            return resources.getBoolean(resourceId);
        }
        return false;
    }
    
    /**
     * 获取手势导航栏高度(如果是全面屏手势导航模式)
     *
     * @param context 上下文
     *
     * @return 手势导航区域高度
     */
    public static int getGestureNavigationBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        
        if (!hasNavigationBar(context)) {
            return 0;
        }
        
        // 全面屏手势区域通常为系统导航栏高度
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_gesture_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        
        // 如果没有找到特定的手势高度资源，使用默认导航栏高度
        resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 手势导航通常比传统导航栏稍矮
            return resources.getDimensionPixelSize(resourceId);
        }
        
        // 如果都找不到，返回一个估计值 (通常在30dp左右)
        return dp2px(30);
    }
    
    /**
     * 返回视图的宽度
     *
     * @param view 视图
     *
     * @return 视图的宽度
     */
    public static int getMeasuredWidth(final View view) {
        if (view == null) {
            return 0;
        }
        return measureView(view)[0];
    }
    
    /**
     * 返回视图的高度
     *
     * @param view 视图
     *
     * @return 视图的高度
     */
    public static int getMeasuredHeight(final View view) {
        if (view == null) {
            return 0;
        }
        return measureView(view)[1];
    }
    
    /**
     * 测量视图的尺寸
     *
     * @param view 视图
     *
     * @return arr[0]: 视图的宽度, arr[1]: 视图的高度
     */
    public static int[] measureView(final View view) {
        if (view == null) {
            return new int[] {0, 0};
        }
        
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        
        int parentWidthMeasureSpec;
        int parentHeightMeasureSpec;
        
        if (view.getParent() instanceof ViewGroup) {
            parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(((ViewGroup) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
            parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(((ViewGroup) view.getParent()).getHeight(), View.MeasureSpec.EXACTLY);
        } else {
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY);
            parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY);
        }
        
        int widthSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec, 0, lp.width);
        int lpHeight = lp.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec);
        return new int[] {view.getMeasuredWidth(), view.getMeasuredHeight()};
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // 接口
    ///////////////////////////////////////////////////////////////////////////
    
    public interface OnGetSizeListener {
        /**
         * 在获取视图大小时回调
         *
         * @param view 视图
         */
        void onGetSize(View view);
    }
}