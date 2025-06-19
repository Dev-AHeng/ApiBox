package my.zjh.model_guiguiapi.view.main.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * API项的数据模型
 *
 * @author AHeng
 * @date 2025/05/02 07:59
 */
public class ApiItemBean implements Parcelable {
    public static final Creator<ApiItemBean> CREATOR = new Creator<ApiItemBean>() {
        @Override
        public ApiItemBean createFromParcel(Parcel in) {
            return new ApiItemBean(in);
        }
        
        @Override
        public ApiItemBean[] newArray(int size) {
            return new ApiItemBean[size];
        }
    };
    /**
     * API唯一标识符
     */
    private String id;
    /**
     * 接口名称
     */
    private String title;
    /**
     * 接口描述
     */
    private String description;
    /**
     * 路由
     */
    private String routePath;
    /**
     * 是否免费
     */
    private boolean isFree;
    
    public ApiItemBean() {
    }
    
    public ApiItemBean(String id, String title, String description, String routePath, boolean isFree) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.routePath = routePath;
        this.isFree = isFree;
    }
    
    protected ApiItemBean(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        routePath = in.readString();
        isFree = in.readByte() != 0;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"id\": " +
                       "\"" + id + "\""
                       + "," + "\"title\": " +
                       "\"" + title + "\""
                       + "," + "\"description\": " +
                       "\"" + description + "\""
                       + "," + "\"routePath\": " +
                       "\"" + routePath + "\""
                       + "," + "\"isFree\": " +
                       isFree
                       + "}";
    }
    
    /**
     * 描述内容，通常返回0即可
     *
     * @return 通常返回0
     */
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(routePath);
        parcel.writeByte((byte) (isFree ? 1 : 0));
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public boolean isFree() {
        return isFree;
    }
    
    public void setFree(boolean free) {
        isFree = free;
    }
    
    public String getRoutePath() {
        return routePath;
    }
    
    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }
}