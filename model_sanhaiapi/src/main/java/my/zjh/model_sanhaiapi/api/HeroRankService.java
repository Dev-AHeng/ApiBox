package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.HeroRank;
import my.zjh.model_sanhaiapi.model.HeroRankDetailResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 王者荣耀英雄战力排名API接口
 *
 * @author Dev_Heng
 */
public interface HeroRankService {
    
    /**
     * 获取英雄战力排名
     */
    @GET("api/herorank")
    Observable<HeroRank> getHeroRank();
    
    /**
     * 获取指定英雄的战力排名详情
     * 
     * @param hero 英雄名称，如：李白
     * @param zone 游戏区域代码，iwx（iOS微信区）、awx（安卓微信区）、iqq（iOS QQ区）、aqq（安卓QQ区）
     * @param type 查询类型，min（最低战力）、max（最高战力）、all（全部数据），默认为all
     * @return 英雄战力排名详情
     */
    @GET("api/herorank")
    Observable<HeroRankDetailResponse> getHeroRankDetail(
            @Query("hero") String hero,
            @Query("zone") String zone,
            @Query("type") String type
    );
} 