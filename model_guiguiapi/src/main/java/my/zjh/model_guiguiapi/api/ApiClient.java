package my.zjh.model_guiguiapi.api;

import android.util.Log;

import com.hjq.gson.factory.GsonFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit配置类
 *
 * @author AHeng
 * @date 2025/05/03 06:43
 */
public class ApiClient {
    
    /**
     * 基础URL
     */
    private static final String BASE_URL = "https://api.guiguiya.com/api/";
    
    /**
     * 连接超时时间, 秒
     */
    private static final long CONNECT_TIMEOUT = 10;
    
    /**
     * 读取超时时间, 秒
     */
    private static final long READ_TIMEOUT = 10;
    
    /**
     * 写入超时时间, 秒
     */
    private static final long WRITE_TIMEOUT = 10;
    
    private static volatile ApiClient instance;
    private final Retrofit retrofit;
    
    private ApiClient() {
        // 创建OkHttpClient
        OkHttpClient okHttpClient = createOkHttpClient();
        // 创建Retrofit实例
        retrofit = createRetrofit(okHttpClient);
    }
    
    /**
     * 获取RetrofitConfig单例
     */
    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * 创建OkHttpClient
     */
    private OkHttpClient createOkHttpClient() {
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.i("OkHttp拦截器", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // 自定义拦截器监控耗时 默认的日志就有请求时长
        Interceptor timingInterceptor = chain -> {
            // 记录请求开始时间
            long startTime = System.nanoTime();
            
            Request request = chain.request();
            // 记录请求URL
            Log.d("Network", "--> 发送请求到: " + request.url());
            
            // 执行请求
            Response response = chain.proceed(request);
            
            // 计算耗时(毫秒)
            long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;
            // 记录请求耗时
            Log.d("Network", "<-- 已收到响应" + elapsedTime + "ms");
            
            return response;
        };
        
        return new OkHttpClient.Builder()
                       .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                       .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                       .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                       // 添加日志拦截器(其实包含了请求耗时)
                       .addInterceptor(loggingInterceptor)
                       // 添加耗时监控拦截器
                       .addInterceptor(timingInterceptor)
                       .build();
    }
    
    /**
     * 创建Retrofit
     */
    private Retrofit createRetrofit(OkHttpClient okHttpClient) {
        // 首先配置GsonFactory
        // GsonFactory.setSingletonGson(new GsonBuilder()
        //                                      // 设置日期格式
        //                                      .setDateFormat("yyyy-MM-dd HH:mm:ss")
        //                                      // 禁止转义html标签
        //                                      .disableHtmlEscaping()
        //                                      // 设置字段命名策略(这里设置为小写带下划线)
        //                                      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        //                                      // 设置Long自动转String(避免JS精度丢失)
        //                                      .registerTypeAdapter(Long.class, new LongTypeAdapter())
        //                                      .create());
        
        
        return new Retrofit.Builder()
                       .baseUrl(BASE_URL)
                       .client(okHttpClient)
                       // .addConverterFactory(GsonConverterFactory.create())
                       // GsonFactory.getSingletonGson() 处理容错
                       .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
                       .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                       .build();
    }
    
    /**
     * 获取API服务接口
     *
     * @param serviceClass API服务接口类
     */
    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
    
    /**
     * 设置自定义的BASE_URL创建API服务接口
     *
     * @param serviceClass API服务接口类
     * @param baseUrl      自定义BASE_URL
     */
    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit customRetrofit = new Retrofit.Builder()
                                          .baseUrl(baseUrl)
                                          .client(createOkHttpClient())
                                          .addConverterFactory(GsonConverterFactory.create())
                                          .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                                          .build();
        return customRetrofit.create(serviceClass);
    }
} 