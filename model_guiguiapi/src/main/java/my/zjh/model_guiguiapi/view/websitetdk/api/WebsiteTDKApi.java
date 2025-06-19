package my.zjh.model_guiguiapi.view.websitetdk.api;

import com.google.gson.annotations.SerializedName;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 网站TDK查询API接口
 *
 * @author AHeng
 * @date 2025/06/09 02:49
 */
public interface WebsiteTDKApi {
    
    /**
     * 查询网站TDK信息
     *
     * @param url 要查询的网站URL
     *
     * @return Observable<WebsiteTDKModel>
     */
    @GET("tdk?type=json&apiKey=13d287864f4b43a921ea5645e0e31f3b")
    Observable<WebsiteTDKModel> getWebsiteTDK(@Query("url") String url);
    
    /**
     * 网站TDK查询响应模型
     *
     * @author AHeng
     * @date 2025/06/09 02:50
     */
    class WebsiteTDKModel {
        
        /**
         * 响应代码
         */
        @SerializedName("code")
        private int code;
        
        /**
         * 响应消息
         */
        @SerializedName("msg")
        private String msg;
        
        /**
         * 查询的URL
         */
        @SerializedName("url")
        private String url;
        
        /**
         * 完整URL
         */
        @SerializedName("full_url")
        private String fullUrl;
        
        /**
         * 网站标题
         */
        @SerializedName("title")
        private String title;
        
        /**
         * 网站描述
         */
        @SerializedName("description")
        private String description;
        
        /**
         * 关键词
         */
        @SerializedName("keywords")
        private String keywords;
        
        /**
         * API提供者
         */
        @SerializedName("by")
        private String by;
        
        /**
         * 查询耗时
         */
        @SerializedName("查询耗时")
        private String queryTime;
        
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
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getFullUrl() {
            return fullUrl;
        }
        
        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getKeywords() {
            return keywords;
        }
        
        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }
        
        public String getQueryTime() {
            return queryTime;
        }
        
        public void setQueryTime(String queryTime) {
            this.queryTime = queryTime;
        }
    }
    
}