package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * 短视频解析响应数据模型
 *
 * @author AHeng
 * @date 2025/03/25 01:43
 */
public class VideoResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private VideoData data;

    @SerializedName("message")
    private String message;

    @SerializedName("tips")
    private String tips;

    @SerializedName("time")
    private String time;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public VideoData getData() {
        return data;
    }

    public void setData(VideoData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 视频数据
     */
    public static class VideoData {
        @SerializedName("title")
        private String title;

        @SerializedName("play_url")
        private String playUrl;

        @SerializedName("url")
        private String url;

        @SerializedName("cover")
        private String cover;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
} 