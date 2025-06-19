package my.zjh.model_guiguiapi.view.bingcoversby7days.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

/**
 * 必应近七天封面图片API接口
 *
 * @author AHeng
 * @date 2025/06/09 05:18
 */
public interface BingCoversBy7DaysApi {
    
    /**
     * 获取必应近七天封面图片
     *
     * @return Observable<BingCoversResponse>
     */
    @GET("bing?type=json&apiKey=8b9dc20e71922ac4c8c6392297c66f23&rand=sj-zh&type=week")
    Observable<BingCoversResponse> getBingCoversBy7Days();
    
    /**
     * 必应封面图片响应模型
     *
     * @author AHeng
     * @date 2025/06/09 05:20
     */
    class BingCoversResponse {
        
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
         * 图片数据列表
         */
        @SerializedName("data")
        private List<BingImageData> data;
        
        // Getter和Setter方法
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
        
        public List<BingImageData> getData() {
            return data;
        }
        
        public void setData(List<BingImageData> data) {
            this.data = data;
        }
    }
    
    /**
     * 必应图片数据模型
     *
     * @author AHeng
     * @date 2025/06/09 05:21
     */
    class BingImageData implements Serializable {
        
        /**
         * 时间ID
         */
        @SerializedName("time_id")
        private String timeId;
        
        /**
         * 搜索链接
         */
        @SerializedName("search")
        private String search;
        
        /**
         * 图片标题
         */
        @SerializedName("title")
        private String title;
        
        /**
         * 图片URL
         */
        @SerializedName("imageurl")
        private String imageUrl;
        
        /**
         * 不同尺寸的图片链接
         */
        @SerializedName("image_size")
        private ImageSize imageSize;
        
        /**
         * 消息
         */
        @SerializedName("msg")
        private String msg;
        
        // Getter和Setter方法
        public String getTimeId() {
            return timeId;
        }
        
        public void setTimeId(String timeId) {
            this.timeId = timeId;
        }
        
        public String getSearch() {
            return search;
        }
        
        public void setSearch(String search) {
            this.search = search;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        
        public ImageSize getImageSize() {
            return imageSize;
        }
        
        public void setImageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
        }
        
        public String getMsg() {
            return msg;
        }
        
        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
    
    /**
     * 图片尺寸模型
     *
     * @author AHeng
     * @date 2025/06/09 05:22
     */
    class ImageSize implements Serializable {
        
        @SerializedName("4K_UHD")
        private String uhd4K;
        
        @SerializedName("1920x1080")
        private String hd1920x1080;
        
        @SerializedName("1366x768")
        private String hd1366x768;
        
        @SerializedName("1280x768")
        private String hd1280x768;
        
        @SerializedName("1024x768")
        private String hd1024x768;
        
        @SerializedName("800x600")
        private String hd800x600;
        
        @SerializedName("800x480")
        private String hd800x480;
        
        @SerializedName("768x1280")
        private String hd768x1280;
        
        @SerializedName("720x1280")
        private String hd720x1280;
        
        @SerializedName("640x480")
        private String hd640x480;
        
        @SerializedName("400x240")
        private String hd400x240;
        
        @SerializedName("320x240")
        private String hd320x240;
        
        @SerializedName("240x320")
        private String hd240x320;
        
        // Getter和Setter方法
        public String getUhd4K() {
            return uhd4K;
        }
        
        public void setUhd4K(String uhd4K) {
            this.uhd4K = uhd4K;
        }
        
        public String getHd1920x1080() {
            return hd1920x1080;
        }
        
        public void setHd1920x1080(String hd1920x1080) {
            this.hd1920x1080 = hd1920x1080;
        }
        
        public String getHd1366x768() {
            return hd1366x768;
        }
        
        public void setHd1366x768(String hd1366x768) {
            this.hd1366x768 = hd1366x768;
        }
        
        public String getHd1280x768() {
            return hd1280x768;
        }
        
        public void setHd1280x768(String hd1280x768) {
            this.hd1280x768 = hd1280x768;
        }
        
        public String getHd1024x768() {
            return hd1024x768;
        }
        
        public void setHd1024x768(String hd1024x768) {
            this.hd1024x768 = hd1024x768;
        }
        
        public String getHd800x600() {
            return hd800x600;
        }
        
        public void setHd800x600(String hd800x600) {
            this.hd800x600 = hd800x600;
        }
        
        public String getHd800x480() {
            return hd800x480;
        }
        
        public void setHd800x480(String hd800x480) {
            this.hd800x480 = hd800x480;
        }
        
        public String getHd768x1280() {
            return hd768x1280;
        }
        
        public void setHd768x1280(String hd768x1280) {
            this.hd768x1280 = hd768x1280;
        }
        
        public String getHd720x1280() {
            return hd720x1280;
        }
        
        public void setHd720x1280(String hd720x1280) {
            this.hd720x1280 = hd720x1280;
        }
        
        public String getHd640x480() {
            return hd640x480;
        }
        
        public void setHd640x480(String hd640x480) {
            this.hd640x480 = hd640x480;
        }
        
        public String getHd400x240() {
            return hd400x240;
        }
        
        public void setHd400x240(String hd400x240) {
            this.hd400x240 = hd400x240;
        }
        
        public String getHd320x240() {
            return hd320x240;
        }
        
        public void setHd320x240(String hd320x240) {
            this.hd320x240 = hd320x240;
        }
        
        public String getHd240x320() {
            return hd240x320;
        }
        
        public void setHd240x320(String hd240x320) {
            this.hd240x320 = hd240x320;
        }
    }
}
