package my.zjh.model_guiguiapi.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.reflect.Field;

/**
 * 降低ViewPager2的滑动灵敏度
 *
 * @author AHeng
 * @date 2025/04/16 08:34
 */
public class ViewPagerUtil {
    
    /**
     * 默认的触摸阈值倍数
     */
    private static final int DEFAULT_TOUCH_SLOP_MULTIPLIER = 3;
    
    /**
     * 降低ViewPager2的滑动灵敏度，使用默认倍数3
     *
     * @param viewPager 需要调整灵敏度的ViewPager2实例
     */
    public static void desensitization(ViewPager2 viewPager) {
        desensitization(viewPager, DEFAULT_TOUCH_SLOP_MULTIPLIER);
    }
    
    /**
     * 降低ViewPager2的滑动灵敏度
     *
     * @param viewPager  需要调整灵敏度的ViewPager2实例
     * @param multiplier 触摸阈值的倍数
     */
    public static void desensitization(ViewPager2 viewPager, int multiplier) {
        if (viewPager == null || multiplier <= 0) {
            return;
        }
        
        try {
            // 通过反射获取ViewPager2内部的RecyclerView字段
            Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            
            // 获取ViewPager2内部的RecyclerView实例
            RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(viewPager);
            if (recyclerView == null) {
                return;
            }
            
            // 通过反射获取RecyclerView的触摸阈值字段
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            
            // 获取当前的触摸阈值
            int touchSlop = (int) touchSlopField.get(recyclerView);
            
            // 将触摸阈值增大为原来的multiplier倍
            touchSlopField.set(recyclerView, touchSlop * multiplier);
            
        } catch (Exception e) {
            // 忽略所有异常，防止反射失败导致应用崩溃
        }
    }
}