package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * QQ基础信息响应实体类
 *
 * @author AHeng
 * @date 2025/03/26 18:45
 */
public class QQInfoResponse {
    
    @SerializedName("code")
    private int code;
    
    @SerializedName("qq")
    private String qq;
    
    @SerializedName("data")
    private QQInfoData data;
    
    @SerializedName("time")
    private String time;
    
    /**
     * QQ用户详细信息
     */
    public static class QQInfoData {
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("mail")
        private String mail;
        
        @SerializedName("avatar")
        private String avatar;
        
        @SerializedName("qzone")
        private String qzone;
        
        @SerializedName("imgurl")
        private String imgurl;
        
        @SerializedName("imgurl1")
        private String imgurl1;
        
        @SerializedName("imgurl2")
        private String imgurl2;
        
        @SerializedName("imgurl3")
        private String imgurl3;
        
        @SerializedName("by")
        private String by;
        
        @SerializedName("blog")
        private String blog;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getMail() {
            return mail;
        }
        
        public void setMail(String mail) {
            this.mail = mail;
        }
        
        public String getAvatar() {
            return avatar;
        }
        
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        
        public String getQzone() {
            return qzone;
        }
        
        public void setQzone(String qzone) {
            this.qzone = qzone;
        }
        
        public String getImgurl() {
            return imgurl;
        }
        
        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }
        
        public String getImgurl1() {
            return imgurl1;
        }
        
        public void setImgurl1(String imgurl1) {
            this.imgurl1 = imgurl1;
        }
        
        public String getImgurl2() {
            return imgurl2;
        }
        
        public void setImgurl2(String imgurl2) {
            this.imgurl2 = imgurl2;
        }
        
        public String getImgurl3() {
            return imgurl3;
        }
        
        public void setImgurl3(String imgurl3) {
            this.imgurl3 = imgurl3;
        }
        
        public String getBy() {
            return by;
        }
        
        public void setBy(String by) {
            this.by = by;
        }
        
        public String getBlog() {
            return blog;
        }
        
        public void setBlog(String blog) {
            this.blog = blog;
        }
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getQq() {
        return qq;
    }
    
    public void setQq(String qq) {
        this.qq = qq;
    }
    
    public QQInfoData getData() {
        return data;
    }
    
    public void setData(QQInfoData data) {
        this.data = data;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    /**
     * 判断响应是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code == 200;
    }
} 