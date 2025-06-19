package my.zjh.gsyvideoplayer.util;

import android.os.Build;

/**
 * 设备品牌工具类
 *
 * @author AHeng
 * @date 2025/05/30 02:17
 */
public final class DeviceBrandUtils {
    
    private DeviceBrandUtils() {
        throw new UnsupportedOperationException("请勿实例化");
    }
    
    /**
     * 是否是荣耀设备
     * 
     * @return 是否是荣耀设备
     */
    public static boolean isHonorDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("HONOR");
    }
    
    /**
     * 是否是小米设备
     * 
     * @return 是否是小米设备
     */
    public static boolean isXiaomiDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi");
    }
    
    /**
     * 是否是OPPO设备
     * 
     * @return 是否是OPPO设备
     */
    public static boolean isOppoDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("OPPO");
    }
    
    /**
     * 是否是一加手机
     * 
     * @return 是否是一加手机
     */
    public static boolean isOnePlusDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("OnePlus");
    }
    
    /**
     * 是否是realme手机
     * 
     * @return 是否是realme手机
     */
    public static boolean isRealmeDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("realme");
    }
    
    /**
     * 是否是vivo设备
     * 
     * @return 是否是vivo设备
     */
    public static boolean isVivoDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("vivo");
    }
    
    /**
     * 是否是华为设备
     * 
     * @return 是否是华为设备
     */
    public static boolean isHuaweiDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("HUAWEI");
    }
} 