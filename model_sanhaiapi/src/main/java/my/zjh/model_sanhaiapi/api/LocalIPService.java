package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.LocalIPResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 本地IP查询服务接口
 *
 * @author AHeng
 * @date 2025/03/26
 */
public interface LocalIPService {
    
    /**
     * 查询本地IP信息
     *
     * @param detail 是否返回完整信息，1表示返回完整信息，0或不传表示返回简略信息
     * @return 本地IP信息查询响应
     */
    @GET("myip")
    Observable<LocalIPResponse> queryLocalIP(@Query("detail") Integer detail);
} 