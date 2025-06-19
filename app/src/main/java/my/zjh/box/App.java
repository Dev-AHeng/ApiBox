package my.zjh.box;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.ParseExceptionCallback;
import com.hjq.toast.ToastStrategy;
import com.hjq.toast.Toaster;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import dagger.hilt.android.HiltAndroidApp;

/**
 * 应用入口
 *
 * @author AHeng
 * @date 2022/02/27 00:42
 */
@HiltAndroidApp
public class App extends Application {
    
    private static App instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        instance = this;
        
        // DynamicColors.applyToActivitiesIfAvailable(this);
        
        
        // 应用动态主题（Material You）
        // applyDynamicColors();
        
        initAliBaBaRouter();
        
        initLogger();
        
        initToaster();
        
        initGsonFaultTolerant();
    }
    
    /**
     * 设置Gson解析容错监听
     */
    private static void initGsonFaultTolerant() {
        GsonFactory.setParseExceptionCallback(new ParseExceptionCallback() {
            @Override
            public void onParseObjectException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
                handlerGsonParseException("解析对象析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken);
            }
            
            @Override
            public void onParseListItemException(TypeToken<?> typeToken, String fieldName, JsonToken listItemJsonToken) {
                handlerGsonParseException("解析 List 异常：" + typeToken + "#" + fieldName + "，后台返回的条目类型为：" + listItemJsonToken);
            }
            
            @Override
            public void onParseMapItemException(TypeToken<?> typeToken, String fieldName, String mapItemKey, JsonToken mapItemJsonToken) {
                handlerGsonParseException("解析 Map 异常：" + typeToken + "#" + fieldName + "，mapItemKey = " + mapItemKey + "，后台返回的条目类型为：" + mapItemJsonToken);
            }
            
            private void handlerGsonParseException(String message) {
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException(message);
                } else {
                    Log.i("异常", "异常: " + message);
                }
            }
        });
    }
    
    private static void initLogger() {
        // 配置 Logger 的格式
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                                                // 是否显示线程信息，默认为 true
                                                .showThreadInfo(false)
                                                // 显示的方法行数，默认为 2
                                                .methodCount(3)
                                                // 方法偏移量，用于隐藏 Logger 内部堆栈信息，默认为 5
                                                .methodOffset(7)
                                                // 全局 tag，默认为 PRETTY_LOGGER
                                                .tag("日志")
                                                .build();
        // 添加 Android Log Adapter
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                // return BuildConfig.DEBUG; //只在debug模式下显示log
                return true;
            }
        });
    }
    
    /**
     * 获取Application实例
     */
    public static App getApp() {
        return instance;
    }
    
    // @Override
    // protected void attachBaseContext(Context base) {
    //     super.attachBaseContext(base);
    //     // 支持65K+方法数
    //     MultiDex.install(this);
    // }
    
    // private void initLeakCanary() {
    //     // 启用LeakCanary
    //     LeakCanary.install(this);
    //     // 配置LeakCanary的各种选项
    //     LeakCanary.Config config = LeakCanary.getConfig();
    //     // 当引用被清除时转储堆快照
    //     config.dumpHeapWhenReferenceCleared = true;
    //     // 设置分析超时时间，这里设置为5分钟
    //     config.analysisTimeoutMs = 5 * 60 * 1000;
    //     // 设置保留的堆字节数限制，这里设置为1MB
    //     config.retainedHeapByteLimit = 1024 * 1024;
    //     LeakCanary.setConfig(config);
    // }
    
    private void initToaster() {
        // Toaster.setView(R.layout.toaster_common);
        
        // 初始化 Toast 框架
        Toaster.init(this);
        // 设置 Toast 策略, 队列SHOW_STRATEGY_TYPE_QUEUE  立即显示
        Toaster.setStrategy(new ToastStrategy(ToastStrategy.SHOW_STRATEGY_TYPE_IMMEDIATELY));
        // 屏幕高度的四分之一位置
        Toaster.setGravity(Gravity.BOTTOM, 0, getResources().getDisplayMetrics().heightPixels / 9);
    }
    
    /**
     * 初始化ARouter
     */
    private void initAliBaBaRouter() {
        // 初始化ARouter
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog();
            // 开启调试模式
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
    
    /**
     * 应用动态主题
     */
    private void applyDynamicColors() {
        DynamicColorsOptions options = new DynamicColorsOptions.Builder()
                                               .setPrecondition((activity, theme) -> true)
                                               .build();
        DynamicColors.applyToActivitiesIfAvailable(this, options);
    }
    
}
