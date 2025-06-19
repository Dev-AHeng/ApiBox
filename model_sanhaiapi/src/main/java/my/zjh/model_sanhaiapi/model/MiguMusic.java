package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 咪咕音乐数据模型
 */
public class MiguMusic {
    
    /**
     * 搜索结果列表
     */
    public static class SearchResult {
        private int code;
        private List<MusicItem> data;
        private String tips;
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public List<MusicItem> getData() {
            return data;
        }
        
        public void setData(List<MusicItem> data) {
            this.data = data;
        }
        
        public String getTips() {
            return tips;
        }
        
        public void setTips(String tips) {
            this.tips = tips;
        }
    }
    
    /**
     * 音乐项（搜索列表中的单项）
     */
    public static class MusicItem {
        private int n;
        private String title;
        private String singer;
        private String cover;
        private String quality;
        
        public int getN() {
            return n;
        }
        
        public void setN(int n) {
            this.n = n;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getSinger() {
            return singer;
        }
        
        public void setSinger(String singer) {
            this.singer = singer;
        }
        
        public String getCover() {
            return cover;
        }
        
        public void setCover(String cover) {
            this.cover = cover;
        }
        
        public String getQuality() {
            return quality;
        }
        
        public void setQuality(String quality) {
            this.quality = quality;
        }
    }
    
    /**
     * 音乐详情
     */
    public static class MusicDetail {
        private int code;
        private String title;
        private String singer;
        private String quality;
        private String cover;
        
        @SerializedName("lrc_url")
        private String lrcUrl;
        
        private String link;
        
        @SerializedName("music_url")
        private String musicUrl;
        
        private String tips;
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getSinger() {
            return singer;
        }
        
        public void setSinger(String singer) {
            this.singer = singer;
        }
        
        public String getQuality() {
            return quality;
        }
        
        public void setQuality(String quality) {
            this.quality = quality;
        }
        
        public String getCover() {
            return cover;
        }
        
        public void setCover(String cover) {
            this.cover = cover;
        }
        
        public String getLrcUrl() {
            return lrcUrl;
        }
        
        public void setLrcUrl(String lrcUrl) {
            this.lrcUrl = lrcUrl;
        }
        
        public String getLink() {
            return link;
        }
        
        public void setLink(String link) {
            this.link = link;
        }
        
        public String getMusicUrl() {
            return musicUrl;
        }
        
        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }
        
        public String getTips() {
            return tips;
        }
        
        public void setTips(String tips) {
            this.tips = tips;
        }
    }
} 