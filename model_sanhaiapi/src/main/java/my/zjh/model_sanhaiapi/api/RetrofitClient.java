package my.zjh.model_sanhaiapi.api;

import com.orhanobut.logger.Logger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit网络客户端
 *
 * @author AHeng
 * @date 2025/03/25 01:43
 */
public class RetrofitClient {
    
    private static final String BASE_URL = "https://api.yyy001.com/api/";
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
    
    /**
     * 获取咪咕音乐服务
     *
     * @return 咪咕音乐服务
     */
    public MiguMusicService getMiguMusicService() {
        return getService(MiguMusicService.class);
    }
    
    /**
     * 获取IP地址查询服务
     *
     * @return IP地址查询服务
     */
    public IPAddressService getIPAddressService() {
        return getService(IPAddressService.class);
    }
    
    /**
     * 获取本地IP查询服务
     *
     * @return 本地IP查询服务
     */
    public LocalIPService getLocalIPService() {
        return getService(LocalIPService.class);
    }
    
    /**
     * 获取一言服务
     *
     * @return 一言服务
     */
    public YiyanService getYiyanService() {
        return getService(YiyanService.class);
    }
    
    /**
     * 获取ICP备案查询服务
     *
     * @return ICP备案查询服务
     */
    public ICPFilingService getICPFilingService() {
        return getService(ICPFilingService.class);
    }
    
    /**
     * 获取QQ基础信息查询服务
     *
     * @return QQ基础信息查询服务
     */
    public QQInfoService getQQInfoService() {
        return getService(QQInfoService.class);
    }
    
    /**
     * 获取随机动漫图服务
     *
     * @return 随机动漫图服务
     */
    public RandomAnimeService getRandomAnimeService() {
        return getService(RandomAnimeService.class);
    }
    
    /**
     * 获取英雄战力排名查询服务
     *
     * @return 英雄战力排名查询服务
     */
    public HeroRankService getHeroRankService() {
        return getService(HeroRankService.class);
    }
    
    /**
     * 获取条形码信息
     *
     * @return 条形码信息
     */
    public BarcodeQueryService getBarcodeQueryService() {
        return getService(BarcodeQueryService.class);
    }
}