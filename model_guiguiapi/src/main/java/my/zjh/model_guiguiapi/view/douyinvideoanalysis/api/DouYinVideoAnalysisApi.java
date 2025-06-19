package my.zjh.model_guiguiapi.view.douyinvideoanalysis.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.reactivex.rxjava3.core.Observable;
import my.zjh.model_guiguiapi.util.NumberUtils;
import my.zjh.model_guiguiapi.util.TimeUtils;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 抖音视频解析
 *
 * @author AHeng
 * @date 2025/05/03 6:51
 */
public interface DouYinVideoAnalysisApi {
    
    /**
     * 抖音视频解析
     *
     * @param url 视频分享的链接
     *
     * @return 解析数据
     */
    @GET("video_qsy/juhe?type=json&apiKey=cea11c0f1c47fc417f2a7c70ecb48895")
    Observable<Response> analysis(@Query("url") String url);
    
    
    /**
     * 抖音视频解析响应实体类
     */
    class Response {
        
        @SerializedName("code")
        private int code;
        
        @SerializedName("msg")
        private String msg;
        @SerializedName("data")
        private VideoData data;
        
        @NonNull
        @Override
        public String toString() {
            return "{" + "\"code\": " +
                           code
                           + "," + "\"msg\": " +
                           "\"" + msg + "\""
                           + "," + "\"data\": " +
                           data
                           + "}";
        }
        
        public int getCode() {
            return code;
        }
        
        public String getMsg() {
            return msg;
        }
        
        public VideoData getData() {
            return data;
        }
        
        public static class VideoData {
            @SerializedName("author")
            private String author;
            @SerializedName("uid")
            private String uid;
            @SerializedName("avatar")
            private String avatar;
            @SerializedName("like")
            private Integer like;
            @SerializedName("time")
            private long time;
            @SerializedName("time2")
            private String time2;
            @SerializedName("title")
            private String title;
            @SerializedName("cover")
            private String cover;
            @SerializedName("url")
            private String url;
            @SerializedName("music")
            private MusicInfo music;
            
            @NonNull
            @Override
            public String toString() {
                return "{" + "\"author\": " +
                               "\"" + author + "\""
                               + "," + "\"uid\": " +
                               "\"" + uid + "\""
                               + "," + "\"avatar\": " +
                               "\"" + avatar + "\""
                               + "," + "\"like\": " +
                               like
                               + "," + "\"time\": " +
                               time
                               + "," + "\"time2\": " +
                               "\"" + time2 + "\""
                               + "," + "\"title\": " +
                               "\"" + title + "\""
                               + "," + "\"cover\": " +
                               "\"" + cover + "\""
                               + "," + "\"url\": " +
                               "\"" + url + "\""
                               + "," + "\"music\": " +
                               music
                               + "}";
            }
            
            public String getAuthor() {
                return author;
            }
            
            public String getUid() {
                return "UID: " + uid;
            }
            
            public String getAvatar() {
                return avatar;
            }
            
            public String getLike() {
                return NumberUtils.formatFriendly(like, 2);
            }
            
            public long getTime() {
                // 转换成友好型时间
                return time;
            }
            
            public String getTime2() {
                return TimeUtils.getFriendlyTimeSpanByNow(time2);
            }
            
            public String getTitle() {
                return title;
            }
            
            public String getCover() {
                return cover;
            }
            
            public String getUrl() {
                return url;
            }
            
            public MusicInfo getMusic() {
                return music;
            }
        }
        
        public static class MusicInfo {
            @SerializedName("author")
            private String author;
            @SerializedName("avatar")
            private String avatar;
            
            @Override
            public String toString() {
                return "{" + "\"author\": " +
                               "\"" + author + "\""
                               + "," + "\"avatar\": " +
                               "\"" + avatar + "\""
                               + "}";
            }
            
            public String getAuthor() {
                return "音乐: " + author;
            }
            
            public void setAuthor(String author) {
                this.author = author;
            }
            
        }
    }
    
}
