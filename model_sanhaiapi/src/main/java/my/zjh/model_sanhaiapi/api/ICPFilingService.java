package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.ICPFilingResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ICP备案查询服务接口
 *
 * @author AHeng
 * @date 2025/03/26 15:40
 */
public interface ICPFilingService {
    
    /**
     * 查询域名的ICP备案信息
     *
     * @param domain 要查询的域名
     * @return ICP备案信息响应
     */
    @GET("icp")
    Observable<ICPFilingResponse> queryICPFiling(@Query("domain") String domain);
} 