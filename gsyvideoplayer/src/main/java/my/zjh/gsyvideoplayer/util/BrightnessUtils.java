package my.zjh.gsyvideoplayer.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 亮度
 *
 * @author AHeng
 * @date 2025/05/29 19:12
 */
public final class BrightnessUtils {
    
    private BrightnessUtils() {
        throw new UnsupportedOperationException("请勿实例化");
    }
    
    /**
     * 是否自动亮度模式
     *
     * @return 是否自动亮度模式
     */
    public static boolean isAutoBrightnessEnabled(Context appContext) {
        try {
            int mode = Settings.System.getInt(
                    appContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 启用或禁用自动亮度模式
     * <p>需要权限 {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     *
     * @param enabled 是否启用
     *
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
    public static boolean setAutoBrightnessEnabled(Context appContext, final boolean enabled) {
        try {
            return Settings.System.putInt(
                    appContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                            : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            );
        } catch (SecurityException e) {
            // 没有WRITE_SETTINGS权限
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取屏幕亮度
     *
     * @return 屏幕亮度 0-255
     */
    public static int getBrightness(Context appContext) {
        return Settings.System.getInt(
                appContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS
                , 255
        );
    }
    
    /**
     * 设置屏幕亮度
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     * 并得到授权
     *
     * @param brightness 亮度值
     */
    public static boolean setBrightness(Context appContext, @IntRange(from = 0, to = 255) final int brightness) {
        ContentResolver resolver = appContext.getContentResolver();
        boolean b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null);
        return b;
    }
    
    /**
     * 设置窗口亮度
     *
     * @param window     窗口
     * @param brightness 亮度值
     */
    public static void setWindowBrightness(@NonNull final Window window,
                                           @IntRange(from = 0, to = 255) final int brightness) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255f;
        window.setAttributes(lp);
    }
    
    /**
     * 获取窗口亮度
     *
     * @param window 窗口
     *
     * @return 屏幕亮度 0-255
     */
    public static int getWindowBrightness(Context appContext, @NonNull final Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
        if (brightness < 0) {
            if (DeviceBrandUtils.isXiaomiDevice()) {
                return getXiaoMiBrightness(appContext);
            }
        }
        return (int) (brightness * 255);
    }
    
    
    /**
     * 获取当前设备亮度的最大值
     *
     * @return 最大亮度值
     */
    @SuppressLint("DiscouragedPrivateApi")
    public static int getXiaoMiMaxBrightness() {
        try {
            return (int) PowerManager.class.getDeclaredField("BRIGHTNESS_ON").get(null);
        } catch (IllegalAccessException |
                 NoSuchFieldException e) {
            e.printStackTrace();
            // 返回默认最大亮度值
            return 255;
        }
    }
    
    /**
     * 获取当前设备亮度的最小值
     *
     * @return 最小亮度值
     */
    @SuppressLint("DiscouragedApi")
    public static int getXiaoMiMinBrightness(Context appContext) {
        int maxBrightness = getXiaoMiMaxBrightness();
        if (maxBrightness > 255) {
            return appContext.getResources().getInteger(appContext.getResources()
                                                                .getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android"));
        }
        return 1;
    }
    
    
    /**
     * 判断当前系统版本是否在Android P(9.0)到Android S(12.0)之间
     *
     * @return 是否在Android P到S之间
     */
    public static boolean isAndroidPToS() {
        int sdkInt = Build.VERSION.SDK_INT;
        return sdkInt >= Build.VERSION_CODES.P && sdkInt <= 31;
    }
    
    /**
     * 根据当前设备特性获取调整后的亮度值
     *
     * @return 根据设备特性调整后的亮度值
     */
    public static int getXiaoMiBrightness(Context appContext) {
        int maxBrightness = getXiaoMiMaxBrightness();
        int minBrightness = getXiaoMiMinBrightness(appContext);
        
        // 如果是小米设备且在Android P-S之间，需要进行亮度级别的转换
        if (DeviceBrandUtils.isXiaomiDevice() && isAndroidPToS() && maxBrightness > 255) {
            // 将[minBrightness, maxBrightness] -> [0, 255]
            return (getBrightness(appContext) - minBrightness) * 255 / (maxBrightness - minBrightness);
        } else {
            // 其他设备或版本，返回原始亮度值
            return getBrightness(appContext);
        }
    }
    
}