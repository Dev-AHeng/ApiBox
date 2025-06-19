package my.zjh.model_guiguiapi.view.hotsearch.api;

import com.orhanobut.logger.Logger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 热榜client
 *
 * @author AHeng
 * @date 2025/04/12 22:15
 */
public class RetrofitClient {
    
    private static final String BASE_URL = "https://api.guiguiya.com/api/";
    private static RetrofitClient instance;
    private static RetrofitClient customInstance;
    private final Retrofit retrofit;
    
    /**
     * 私有构造方法
     */
    private RetrofitClient() {
        this(BASE_URL);
    }
    
    /**
     * 带baseUrl的私有构造方法
     */
    private RetrofitClient(String baseUrl) {
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Logger.d("OkHttp: " + message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // 自定义拦截器监控耗时 默认的日志就有请求时长
        // Interceptor timingInterceptor = chain -> {
        //     long startTime = System.nanoTime();  // 记录请求开始时间
        //
        //     Request request = chain.request();
        //     Log.d("Network", "--> 发送请求到: " + request.url());  // 记录请求URL
        //
        //     Response response = chain.proceed(request);  // 执行请求
        //
        //     long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;  // 计算耗时(毫秒)
        //     Log.d("Network", "<-- 已收到响应" + elapsedTime + "ms");  // 记录请求耗时
        //
        //     return response;
        // };
        
        // 创建OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                                      // 添加日志拦截器
                                      .addInterceptor(loggingInterceptor)
                                      // 添加耗时监控拦截器
                                      // .addInterceptor(timingInterceptor)
                                      .build();
        
        // 创建Retrofit实例
        retrofit = new Retrofit.Builder()
                           .baseUrl(baseUrl)
                           .client(client)
                           .addConverterFactory(GsonConverterFactory.create())
                           .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                           .build();
    }
    
    /**
     * 获取单例实例
     *
     * @return RetrofitClient实例
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    /**
     * 获取指定baseUrl的实例
     *
     * @param baseUrl 基础URL
     *
     * @return RetrofitClient实例
     */
    public static synchronized RetrofitClient getInstance(String baseUrl) {
        if (customInstance == null) {
            customInstance = new RetrofitClient(baseUrl);
        }
        return customInstance;
    }
    
    /**
     * 获取API服务
     *
     * @param service 服务接口类
     * @param <T>     服务接口类型
     *
     * @return 服务接口实现
     */
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
    
    /**
     * 获取API服务
     *
     * @param service 服务接口类
     * @param <T>     服务接口类型
     *
     * @return 服务接口实现
     */
    public <T> T getService(Class<T> service) {
        return retrofit.create(service);
    }
    
    /**
     * 获取热榜搜索服务
     *
     * @return 热榜搜索服务
     */
    public HotSearchService getHotSearchService() {
        return getService(HotSearchService.class);
    }
    
}