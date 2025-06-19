package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.VideoResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 短视频解析API接口
 *
 * @author AHeng
 * @date 2025/03/25 01:43
 */
public interface VideoParseService {

    /**
     * 解析短视频
     *
     * @param url 需要解析的视频URL地址
     * @return 视频解析响应
     */
    @GET("videoparse")
    Observable<VideoResponse> parseVideo(@Query("url") String url);
} 