package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.QQInfoResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * QQ基础信息查询服务接口
 *
 * @author AHeng
 * @date 2025/03/26 18:48
 */
public interface QQInfoService {
    
    /**
     * 查询QQ号码的基础信息
     *
     * @param qq 要查询的QQ号码，默认为1790643379
     * @return QQ基础信息响应
     */
    @GET("qqinfo")
    Observable<QQInfoResponse> queryQQInfo(@Query("qq") String qq);
} 