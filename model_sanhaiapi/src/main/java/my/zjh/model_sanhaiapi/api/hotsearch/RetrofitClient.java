package my.zjh.model_sanhaiapi.api.hotsearch;

import com.orhanobut.logger.Logger;

import my.zjh.model_sanhaiapi.api.VideoParseService;
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
        
        // 创建OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                                      .addInterceptor(loggingInterceptor)
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
     * 获取视频解析服务
     *
     * @return 视频解析服务
     */
    public VideoParseService getVideoParseService() {
        return getService(VideoParseService.class);
    }
    
}