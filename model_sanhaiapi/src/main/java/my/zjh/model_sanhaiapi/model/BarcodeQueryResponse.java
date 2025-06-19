package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * 条形码返回实体
 *
 * @author AHeng
 * @date 2025/03/31 1:10
 */
public class BarcodeQueryResponse {
    /**
     * 请求的状态码，0 表示失败，1 表示成功。
     */
    @SerializedName("code")
    private Integer code;
    
    /**
     * 请求结果的消息，成功时为 success，失败时为错误信息。
     */
    @SerializedName("msg")
    private String msg;
    
    /**
     * 商品详细信息，具体内容根据条形码查询的商品返回。
     */
    @SerializedName("data")
    private GoodsData data;
    
    
    public static class GoodsData {
        /**
         * 商品名称
         */
        @SerializedName("goodsName")
        private String goodsName;
        
        /**
         * 商品条形码
         */
        @SerializedName("barcode")
        private String barcode;
        
        /**
         * 商品价格
         */
        @SerializedName("price")
        private String price;
        
        /**
         * 商品品牌
         */
        @SerializedName("brand")
        private String brand;
        
        /**
         * 商品供应商
         */
        @SerializedName("supplier")
        private String supplier;
        
        /**
         * 商品规格，例如 600ml
         */
        @SerializedName("standard")
        private String standard;
        
        public String getGoodsName() {
            return goodsName;
        }
        
        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }
        
        public String getBarcode() {
            return barcode;
        }
        
        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }
        
        public String getPrice() {
            return price;
        }
        
        public void setPrice(String price) {
            this.price = price;
        }
        
        public String getBrand() {
            return brand;
        }
        
        public void setBrand(String brand) {
            this.brand = brand;
        }
        
        public String getSupplier() {
            return supplier;
        }
        
        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
        
        public String getStandard() {
            return standard;
        }
        
        public void setStandard(String standard) {
            this.standard = standard;
        }
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public GoodsData getData() {
        return data;
    }
    
    public void setData(GoodsData data) {
        this.data = data;
    }
}
