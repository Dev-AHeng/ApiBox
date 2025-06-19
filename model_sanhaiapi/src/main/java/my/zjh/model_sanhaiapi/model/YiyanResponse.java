package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * 一言响应数据模型
 *
 * @author AHeng
 * @date 2025/03/27
 */
public class YiyanResponse {
    
    @SerializedName("code")
    private int code;
    
    @SerializedName("msg")
    private String message;
    
    @SerializedName("tips")
    private String tips;
    
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
    
    public String getTips() {
        return tips;
    }
    
    public void setTips(String tips) {
        this.tips = tips;
    }
} 