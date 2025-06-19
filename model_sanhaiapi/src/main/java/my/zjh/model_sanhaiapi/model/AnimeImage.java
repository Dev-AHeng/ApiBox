package my.zjh.model_sanhaiapi.model;

import java.util.Objects;

/**
 * 动漫图像实体类
 */
public class AnimeImage {
    
    private String url;
    private String category;
    private String filename;
    // 时间戳，用于排序
    private long timestamp;
    
    public AnimeImage() {
        // 默认为当前时间
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimeImage that = (AnimeImage) o;
        return timestamp == that.timestamp &&
                Objects.equals(url, that.url) &&
                Objects.equals(category, that.category) &&
                Objects.equals(filename, that.filename);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(url, category, filename, timestamp);
    }
} 