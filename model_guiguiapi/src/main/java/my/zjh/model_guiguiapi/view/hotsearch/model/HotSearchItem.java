package my.zjh.model_guiguiapi.view.hotsearch.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * List<T> data;
 *
 * @author AHeng
 * @date 2025/04/13 1:21
 */
public class HotSearchItem implements Serializable {
    @SerializedName("index")
    private Integer index;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    
    @Override
    public int hashCode() {
        return Objects.hash(index, title, url);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HotSearchItem)) {
            return false;
        }
        HotSearchItem that = (HotSearchItem) o;
        return Objects.equals(index, that.index) && Objects.equals(title, that.title) && Objects.equals(url, that.url);
    }
    
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
