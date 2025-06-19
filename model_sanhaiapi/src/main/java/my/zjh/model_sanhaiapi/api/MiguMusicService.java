package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.MiguMusic;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 咪咕音乐API服务接口
 */
public interface MiguMusicService {
    
    /**
     * 搜索音乐
     *
     * @param msg 搜索关键词，默认为'邓紫棋'
     * @param type 返回格式，可选：text（默认）、json
     * @param br 音质选择，1（优先最高音质）默认为1
     * @param num 搜索结果数量，默认20
     * @return 搜索结果
     */
    @GET("mgmuisc")
    Observable<MiguMusic.SearchResult> searchMusic(
            @Query("msg") String msg,
            @Query("type") String type,
            @Query("br") String br,
            @Query("num") Integer num
    );
    
    /**
     * 获取音乐详情
     *
     * @param msg 搜索关键词，默认为'邓紫棋'
     * @param n 获取第n个搜索结果的详情
     * @param type 返回格式，可选：text（默认）、json
     * @param br 音质选择，1（优先最高音质）默认为1
     * @return 音乐详情
     */
    @GET("mgmuisc")
    Observable<MiguMusic.MusicDetail> getMusicDetail(
            @Query("msg") String msg,
            @Query("n") Integer n,
            @Query("type") String type,
            @Query("br") String br
    );
} 