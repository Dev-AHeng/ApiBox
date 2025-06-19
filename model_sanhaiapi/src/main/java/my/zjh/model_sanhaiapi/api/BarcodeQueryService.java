package my.zjh.model_sanhaiapi.api;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_sanhaiapi.model.BarcodeQueryResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 条形码查询
 *
 * @author AHeng
 * @date 2025/03/31 1:06
 */
public interface BarcodeQueryService {
    
    /**
     * 条形码查询
     *
     * @param barcode 条形码
     *
     * @return BarcodeQueryResponse
     */
    @GET("barcode")
    Observable<BarcodeQueryResponse> query(@Query("barcode") String barcode);
}
