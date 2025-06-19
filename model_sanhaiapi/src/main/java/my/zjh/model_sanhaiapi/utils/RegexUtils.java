package my.zjh.model_sanhaiapi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用正则表达式
 *
 * @author AHeng
 * @date 2025/04/04 4:58
 */
public class RegexUtils {
    /**
     * 条形码正则表达式
     */
    private static final String BARCODE = "^\\d{6,13}$";
    
    private RegexUtils() {
        throw new IllegalStateException("RegexUtils是一个工具类, 不能被实例化");
    }
    
    /**
     * 验证字符串是否匹配指定的正则表达式
     *
     * @param regex 正则表达式
     * @param input 要验证的字符串
     *
     * @return 如果字符串匹配正则表达式，则返回true，否则返回false
     */
    private static boolean isMatch(String regex, String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        // 使用Pattern.compile()方法将正则表达式编译成Pattern对象
        Pattern pattern = Pattern.compile(regex);
        // 使用Pattern.matcher()方法创建Matcher对象，用于匹配输入的字符串
        Matcher matcher = pattern.matcher(input);
        // 使用Matcher.matches()方法尝试将整个输入序列与该模式匹配
        return matcher.matches();
    }
    
    
    /**
     * 验证条形码格式是否正确
     *
     * @param barcode 条形码
     *
     * @return 是否有效
     */
    public static boolean isValidBarcode(String barcode) {
        return isMatch(BARCODE, barcode);
    }
}
