package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.RandomAnimeResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 随机动漫图API接口
 * @author Dev_Heng
 */
public interface RandomAnimeService {
    
    /**
     * 获取随机动漫图
     * @param category 图片分类（PC/adaptive/bd/moban）
     * @param type 返回类型（json/img）
     * @return 随机动漫图响应
     */
    @GET("api/dmimg")
    Observable<RandomAnimeResponse> getRandomAnime(
            @Query("category") String category,
            @Query("type") String type
    );
} 