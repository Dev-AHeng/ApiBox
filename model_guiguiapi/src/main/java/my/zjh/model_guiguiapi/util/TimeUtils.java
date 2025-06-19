package my.zjh.model_guiguiapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 *
 * @author AHeng
 * @date 2025/04/15 18:08
 */
public class TimeUtils {
    
    
    /**
     * 将日期字符串转换为友好的时间间隔显示
     *
     * @param dateStr 日期字符串，格式为"yyyy-MM-dd HH:mm:ss"
     *
     * @return 友好的时间字符串
     */
    public static String getFriendlyTimeSpanByNow(String dateStr) {
        // 定义日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        try {
            // 将字符串解析为Date对象
            Date date = sdf.parse(dateStr);
            // 转换为时间戳
            long timeMillis = 0;
            if (date != null) {
                timeMillis = date.getTime();
            }
            
            // 计算时间差（毫秒）
            long span = Math.max(System.currentTimeMillis() - timeMillis, 0);
            
            // 根据时间差返回不同的友好字符串
            if (span < 30 * 1000) {
                return "刚刚";
            } else if (span < 60 * 1000) {
                return (span / 1000) + "秒前";
            } else if (span < 60 * 60 * 1000) {
                return (span / (60 * 1000)) + "分钟前";
            } else if (span < 24 * 60 * 60 * 1000) {
                return (span / (60 * 60 * 1000)) + "小时前";
            } else if (span < 30L * 24 * 60 * 60 * 1000) {
                return (span / (24 * 60 * 60 * 1000)) + "天前";
            } else if (span < 12L * 30 * 24 * 60 * 60 * 1000) {
                return (span / (30L * 24 * 60 * 60 * 1000)) + "个月前";
            } else {
                return (span / (365L * 24 * 60 * 60 * 1000)) + "年前";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "时间格式错误";
        }
    }
    
    
    /**
     * 将时间戳转换为友好的时间间隔显示
     *
     * @param timeMillis 目标时间的时间戳（毫秒）
     *
     * @return 友好的时间字符串
     */
    public static String getFriendlyTimeSpanByNow(long timeMillis) {
        // 获取当前时间戳并计算时间差（毫秒）
        long span = Math.max(System.currentTimeMillis() - timeMillis, 0);
        
        // 根据时间差返回不同的友好字符串
        if (span < 30 * 1000) {
            return "刚刚";
        } else if (span < 60 * 1000) {
            return (span / 1000) + "秒前";
        } else if (span < 60 * 60 * 1000) {
            return (span / (60 * 1000)) + "分钟前";
        } else if (span < 24 * 60 * 60 * 1000) {
            return (span / (60 * 60 * 1000)) + "小时前";
        } else if (span < 30L * 24 * 60 * 60 * 1000) {
            return (span / (24 * 60 * 60 * 1000)) + "天前";
        } else if (span < 12L * 30 * 24 * 60 * 60 * 1000) {
            return (span / (30L * 24 * 60 * 60 * 1000)) + "个月前";
        } else {
            return (span / (365L * 24 * 60 * 60 * 1000)) + "年前";
        }
    }
    
}
