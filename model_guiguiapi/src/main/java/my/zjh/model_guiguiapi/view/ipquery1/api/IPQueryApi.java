package my.zjh.model_guiguiapi.view.ipquery1.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_guiguiapi.view.ipquery1.model.IPQueryResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * IP查询API接口
 *
 * @author AHeng
 * @date 2025/06/08 16:35
 */
public interface IPQueryApi {
    
    /**
     * 查询IP地址信息
     *
     * @param ip 要查询的IP地址
     *
     * @return IP查询结果
     */
    @GET("mizhi-ipquery?type=json&apiKey=03ee57465c2c839041a2c573edf1aa3c")
    Observable<IPQueryResponse> queryIPInfo(@Query("ip") String ip);
} 