package my.zjh.model_guiguiapi.view.ipquery1.model;

/**
 * IP查询响应数据模型
 *
 * @author AHeng
 * @date 2025/06/08 16:30
 */
public class IPQueryResponse {
    
    private int status;
    private String ip;
    private IPInfo info;
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public IPInfo getInfo() {
        return info;
    }
    
    public void setInfo(IPInfo info) {
        this.info = info;
    }
    
    /**
     * IP详细信息
     */
    public static class IPInfo {
        private String continent;
        private String country;
        private String province;
        private String city;
        private String region;
        private String carrier;
        private String division;
        private String en_country;
        private String en_short_code;
        private String longitude;
        private String latitude;
        
        public String getContinent() {
            return continent;
        }
        
        public void setContinent(String continent) {
            this.continent = continent;
        }
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public String getProvince() {
            return province;
        }
        
        public void setProvince(String province) {
            this.province = province;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public String getCarrier() {
            return carrier;
        }
        
        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }
        
        public String getDivision() {
            return division;
        }
        
        public void setDivision(String division) {
            this.division = division;
        }
        
        public String getEn_country() {
            return en_country;
        }
        
        public void setEn_country(String en_country) {
            this.en_country = en_country;
        }
        
        public String getEn_short_code() {
            return en_short_code;
        }
        
        public void setEn_short_code(String en_short_code) {
            this.en_short_code = en_short_code;
        }
        
        public String getLongitude() {
            return longitude;
        }
        
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        
        public String getLatitude() {
            return latitude;
        }
        
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
    }
} 