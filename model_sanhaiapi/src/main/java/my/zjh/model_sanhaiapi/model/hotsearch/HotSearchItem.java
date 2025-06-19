package my.zjh.model_sanhaiapi.model.hotsearch;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * List<T> data;
 *
 * @author AHeng
 * @date 2025/04/13 1:21
 */
public class HotSearchItem {
    @SerializedName("index")
    private Integer index;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"index\": " +
                       index
                       + "," + "\"title\": " +
                       "\"" + title + "\""
                       + "," + "\"url\": " +
                       "\"" + url + "\""
                       + "}";
    }
    
    public Integer getIndex() {
        return index;
    }
    
    public void setIndex(Integer index) {
        this.index = index;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
