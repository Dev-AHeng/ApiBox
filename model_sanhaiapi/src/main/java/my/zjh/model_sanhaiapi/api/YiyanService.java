package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.YiyanResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 一言API服务接口
 *
 * @author AHeng
 * @date 2025/03/27
 */
public interface YiyanService {
    
    /**
     * 获取一言
     *
     * @param charset 字符集编码，可选值为：GBK、gbk、gb2312、UTF-8（默认）
     * @param code 响应类型，可选值为 js、javascript、json、xml、array，默认为纯文本
     * @return 一言响应
     */
    @GET("yiyan")
    Observable<YiyanResponse> getYiyan(
            @Query("charset") String charset,
            @Query("code") String code
    );
} 