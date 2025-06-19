package my.zjh.model_guiguiapi.view.hotsearch.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchList;
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchTypeList;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 全网聚合热搜榜单
 *
 * @author AHeng
 * @date 2025/04/12 22:20
 */
public interface HotSearchService {
    
    @GET("hotlist?apiKey=cc1fbbc029908f2f42f27720f45f6b7f")
    Observable<HotSearchTypeList> getHotlist();
    
    @GET("hotlist?apiKey=cc1fbbc029908f2f42f27720f45f6b7f")
    Observable<HotSearchList> getHotlist(
            @Query("type") String type
    );
    
}
