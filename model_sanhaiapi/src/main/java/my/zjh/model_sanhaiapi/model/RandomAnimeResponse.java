package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * 随机动漫图接口响应
 */
public class RandomAnimeResponse {
    
    private int code;
    private String message;
    private Data data;
    
    public static class Data {
        private String url;
        private String category;
        private String filename;
        
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
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Data getData() {
        return data;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
} 