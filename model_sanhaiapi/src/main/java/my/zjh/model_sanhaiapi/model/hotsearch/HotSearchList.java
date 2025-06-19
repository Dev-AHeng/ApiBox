package my.zjh.model_sanhaiapi.model.hotsearch;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 通用响应基类，使用泛型处理不同类型的数据
 *
 * @param <T> 数据项类型
 *
 * @author AHeng
 * @date 2025/04/13 01:22
 */
public class HotSearchList<T> {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("msg")
    private String msg;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("subtitle")
    private String subtitle;
    
    @SerializedName("update_time")
    private String updateTime;
    
    @SerializedName("total")
    private int total;
    
    private List<T> data;
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"success\": " +
                       success
                       + "," + "\"msg\": " +
                       "\"" + msg + "\""
                       + "," + "\"title\": " +
                       "\"" + title + "\""
                       + "," + "\"subtitle\": " +
                       "\"" + subtitle + "\""
                       + "," + "\"updateTime\": " +
                       "\"" + updateTime + "\""
                       + "," + "\"total\": " +
                       total
                       + "," + "\"data\": " +
                       data
                       + "}";
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public List<T> getData() {
        return data;
    }
    
    public void setData(List<T> data) {
        this.data = data;
    }
}