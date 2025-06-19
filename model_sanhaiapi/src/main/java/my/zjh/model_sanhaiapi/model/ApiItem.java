package my.zjh.model_sanhaiapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * API项的数据模型
 */
public class ApiItem implements Parcelable {
    // API唯一标识符
    private String id;
    // API标题
    private String title;
    // API描述
    private String description;
    // 是否免费
    private boolean isFree;
    private String routePath;
    
    public ApiItem(String id, String title, String description, boolean isFree, String routePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isFree = isFree;
        this.routePath = routePath;
    }
    
    // Parcelable实现
    protected ApiItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        isFree = in.readByte() != 0;
        routePath = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isFree ? 1 : 0));
        dest.writeString(routePath);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<ApiItem> CREATOR = new Creator<ApiItem>() {
        @Override
        public ApiItem createFromParcel(Parcel in) {
            return new ApiItem(in);
        }
        
        @Override
        public ApiItem[] newArray(int size) {
            return new ApiItem[size];
        }
    };
    
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, isFree);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiItem)) {
            return false;
        }
        ApiItem apiItem = (ApiItem) o;
        return isFree == apiItem.isFree &&
                       com.google.common.base.Objects.equal(id, apiItem.id) &&
                       com.google.common.base.Objects.equal(title, apiItem.title) &&
                       com.google.common.base.Objects.equal(description, apiItem.description);
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