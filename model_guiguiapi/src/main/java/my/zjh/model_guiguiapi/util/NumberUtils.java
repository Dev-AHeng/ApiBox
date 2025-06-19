package my.zjh.model_guiguiapi.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数字友好型工具类
 *
 * @author AHeng
 * @date 2025/05/07 09:44
 */
public final class NumberUtils {
    
    private NumberUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
    
    /**
     * 格式化数字为友好可读的字符串
     * 例如: 1234 -> 1234, 12345 -> 1.2万, 1234567 -> 123.5万, 12345678 -> 1234.6万, 123456789 -> 1.2亿
     * <p>
     * 输入输出示例:
     * 输入: 432174
     * 输出: 43.2万
     * <p>
     * 输入: 1234
     * 输出: 1234
     * <p>
     * 输入: 123456789
     * 输出: 1.2亿
     *
     * @param number 需要格式化的数字
     *
     * @return 格式化后的字符串
     */
    public static String formatFriendly(long number) {
        if (number < 0) {
            return "-" + formatFriendly(-number);
        }
        
        if (number < 10000) {
            // 小于1万，直接返回
            return String.valueOf(number);
        } else if (number < 100000000) {
            // 1万 - 1亿，以"万"为单位
            double value = number / 10000.0;
            return format(value, 1) + "万";
        } else {
            // 大于等于1亿，以"亿"为单位
            double value = number / 100000000.0;
            return format(value, 1) + "亿";
        }
    }
    
    /**
     * 格式化数字为友好可读的字符串，可自定义保留小数位数
     * <p>
     * 输入输出示例:
     * 输入: number=432174, scale=1
     * 输出: 43.2万
     * <p>
     * 输入: number=432174, scale=2
     * 输出: 43.22万
     * <p>
     * 输入: number=123456789, scale=2
     * 输出: 1.23亿
     *
     * @param number 需要格式化的数字
     * @param scale  保留的小数位数
     *
     * @return 格式化后的字符串
     */
    public static String formatFriendly(long number, int scale) {
        if (number < 0) {
            return "-" + formatFriendly(-number, scale);
        }
        
        if (number < 10000) {
            // 小于1万，直接返回
            return String.valueOf(number);
        } else if (number < 100000000) {
            // 1万 - 1亿，以"万"为单位
            double value = number / 10000.0;
            return format(value, scale) + "万";
        } else {
            // 大于等于1亿，以"亿"为单位
            double value = number / 100000000.0;
            return format(value, scale) + "亿";
        }
    }
    
    /**
     * 格式化浮点数，去除末尾多余的0
     * <p>
     * 输入输出示例:
     * 输入: value=43.20, scale=1
     * 输出: 43.2
     * <p>
     * 输入: value=43.00, scale=2
     * 输出: 43
     *
     * @param value 需要格式化的浮点数
     * @param scale 保留的小数位数
     *
     * @return 格式化后的字符串
     */
    private static String format(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        
        // 如果小数部分为0，则返回整数部分
        if (bd.stripTrailingZeros().scale() <= 0) {
            return bd.toBigInteger().toString();
        }
        
        return bd.stripTrailingZeros().toPlainString();
    }
    
    /**
     * 将字符串转换为整数，转换失败返回默认值
     * <p>
     * 输入输出示例:
     * 输入: str="123", defaultValue=0
     * 输出: 123
     * <p>
     * 输入: str="abc", defaultValue=0
     * 输出: 0
     *
     * @param str          字符串
     * @param defaultValue 默认值
     *
     * @return 转换后的整数或默认值
     */
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 将字符串转换为长整数，转换失败返回默认值
     * <p>
     * 输入输出示例:
     * 输入: str="123456789", defaultValue=0L
     * 输出: 123456789
     * <p>
     * 输入: str="abc", defaultValue=0L
     * 输出: 0
     *
     * @param str          字符串
     * @param defaultValue 默认值
     *
     * @return 转换后的长整数或默认值
     */
    public static long parseLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 将字符串转换为双精度浮点数，转换失败返回默认值
     * <p>
     * 输入输出示例:
     * 输入: str="123.45", defaultValue=0.0
     * 输出: 123.45
     * <p>
     * 输入: str="abc", defaultValue=0.0
     * 输出: 0.0
     *
     * @param str          字符串
     * @param defaultValue 默认值
     *
     * @return 转换后的双精度浮点数或默认值
     */
    public static double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 格式化数字，使用千位分隔符
     * 例如: 1234567 -> 1,234,567
     * <p>
     * 输入输出示例:
     * 输入: 1234567
     * 输出: 1,234,567
     * <p>
     * 输入: 1000000
     * 输出: 1,000,000
     *
     * @param number 需要格式化的数字
     *
     * @return 格式化后的字符串
     */
    public static String formatWithComma(long number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }
    
    /**
     * 格式化数字为指定小数位数的字符串
     * <p>
     * 输入输出示例:
     * 输入: number=123.456, scale=2
     * 输出: 123.46
     * <p>
     * 输入: number=123, scale=2
     * 输出: 123.00
     *
     * @param number 需要格式化的数字
     * @param scale  小数位数
     *
     * @return 格式化后的字符串
     */
    public static String formatDecimal(double number, int scale) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.toPlainString();
    }
} 