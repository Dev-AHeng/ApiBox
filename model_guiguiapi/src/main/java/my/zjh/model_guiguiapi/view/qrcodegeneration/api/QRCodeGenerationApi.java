package my.zjh.model_guiguiapi.view.qrcodegeneration.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 二维码生成API
 *
 * @author AHeng
 * @date 2025/06/08 15:30
 */
public interface QRCodeGenerationApi {
    
    /**
     * 生成二维码
     *
     * @param text      要生成二维码的文本
     * @param size      二维码尺寸
     * @param frame     边框大小（内边距）
     * @param errorLevel 错误纠正级别 (L/M/Q/H)
     * @return 响应体（成功返回图片，失败返回JSON）
     */
    @GET("qrcode?type=json&apiKey=9ae1fb9e9251731985e0da7774e3d4b4")
    Observable<Response<ResponseBody>> generateQRCode(
            @Query("text") String text,
            @Query("size") int size,
            @Query("frame") int frame,
            @Query("e") String errorLevel
    );
    
    /**
     * 二维码生成错误响应实体类
     */
    class ErrorResponse {
        
        @SerializedName("code")
        private int code;
        
        @SerializedName("msg")
        private String msg;
        
        @NonNull
        @Override
        public String toString() {
            return "{" + 
                   "\"code\": " + code + 
                   ", \"msg\": \"" + msg + "\"" + 
                   "}";
        }
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getMsg() {
            return msg;
        }
        
        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
} 