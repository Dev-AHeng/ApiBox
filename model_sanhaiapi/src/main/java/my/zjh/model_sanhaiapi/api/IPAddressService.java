package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.IPAddressResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * IP地址查询服务接口
 *
 * @author AHeng
 * @date 2025/03/26
 */
public interface IPAddressService {
    
    /**
     * 查询IP地址信息
     *
     * @param ip 要查询的IP地址
     * @param n  API优先级（1-4），不填则使用所有API
     * @return IP地址查询响应
     */
    @GET("ipdata")
    Observable<IPAddressResponse> queryIPAddress(
            @Query("ip") String ip,
            @Query("n") Integer n
    );
} 