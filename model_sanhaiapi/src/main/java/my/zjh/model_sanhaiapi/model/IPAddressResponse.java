package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * IP地址查询响应数据模型
 *
 * @author AHeng
 * @date 2025/03/26
 */
public class IPAddressResponse {
    
    // 查询的IP地址
    private String ip;
    
    // Vore API返回的数据
    @SerializedName("vore_data")
    private VoreData voreData;
    
    // IP-API返回的数据
    @SerializedName("ipapi_data")
    private IpapiData ipapiData;
    
    // 百度API返回的数据
    @SerializedName("baidu_data")
    private BaiduData baiduData;
    
    // IP Info返回的数据
    @SerializedName("ipinfo_data")
    private IPInfoData ipInfoData;
    
    // 聚合后的数据
    @SerializedName("aggregated_data")
    private AggregatedData aggregatedData;
    
    // API调用信息
    @SerializedName("api_info")
    private ApiInfo apiInfo;
    
    // getter和setter
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public VoreData getVoreData() {
        return voreData;
    }
    
    public void setVoreData(VoreData voreData) {
        this.voreData = voreData;
    }
    
    public IpapiData getIpapiData() {
        return ipapiData;
    }
    
    public void setIpapiData(IpapiData ipapiData) {
        this.ipapiData = ipapiData;
    }
    
    public BaiduData getBaiduData() {
        return baiduData;
    }
    
    public void setBaiduData(BaiduData baiduData) {
        this.baiduData = baiduData;
    }
    
    public IPInfoData getIpInfoData() {
        return ipInfoData;
    }
    
    public void setIpInfoData(IPInfoData ipInfoData) {
        this.ipInfoData = ipInfoData;
    }
    
    public AggregatedData getAggregatedData() {
        return aggregatedData;
    }
    
    public void setAggregatedData(AggregatedData aggregatedData) {
        this.aggregatedData = aggregatedData;
    }
    
    public ApiInfo getApiInfo() {
        return apiInfo;
    }
    
    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }
    
    // Vore API数据模型
    public static class VoreData {
        @SerializedName("ip_type")
        private String ipType;
        
        @SerializedName("is_cn_ip")
        private boolean isCnIp;
        
        private String province;
        private String city;
        private String district;
        private String isp;
        private Adcode adcode;
        
        @SerializedName("query_time")
        private long queryTime;
        
        // getter和setter
        public String getIpType() {
            return ipType;
        }
        
        public void setIpType(String ipType) {
            this.ipType = ipType;
        }
        
        public boolean isCnIp() {
            return isCnIp;
        }
        
        public void setCnIp(boolean cnIp) {
            isCnIp = cnIp;
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
        
        public String getDistrict() {
            return district;
        }
        
        public void setDistrict(String district) {
            this.district = district;
        }
        
        public String getIsp() {
            return isp;
        }
        
        public void setIsp(String isp) {
            this.isp = isp;
        }
        
        public Adcode getAdcode() {
            return adcode;
        }
        
        public void setAdcode(Adcode adcode) {
            this.adcode = adcode;
        }
        
        public long getQueryTime() {
            return queryTime;
        }
        
        public void setQueryTime(long queryTime) {
            this.queryTime = queryTime;
        }
        
        public static class Adcode {
            private String province;
            private String city;
            private String code;
            
            // getter和setter
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
            
            public String getCode() {
                return code;
            }
            
            public void setCode(String code) {
                this.code = code;
            }
        }
    }
    
    // IP-API数据模型
    public static class IpapiData {
        private String country;
        private String countryCode;
        private String region;
        private String regionName;
        private String city;
        private String zip;
        private double latitude;
        private double longitude;
        private String timezone;
        private String isp;
        private String org;
        private String as;
        
        // getter和setter
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public String getCountryCode() {
            return countryCode;
        }
        
        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public String getRegionName() {
            return regionName;
        }
        
        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getZip() {
            return zip;
        }
        
        public void setZip(String zip) {
            this.zip = zip;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
        
        public String getIsp() {
            return isp;
        }
        
        public void setIsp(String isp) {
            this.isp = isp;
        }
        
        public String getOrg() {
            return org;
        }
        
        public void setOrg(String org) {
            this.org = org;
        }
        
        public String getAs() {
            return as;
        }
        
        public void setAs(String as) {
            this.as = as;
        }
    }
    
    // 百度API数据模型
    public static class BaiduData {
        private String location;
        
        // getter和setter
        public String getLocation() {
            return location;
        }
        
        public void setLocation(String location) {
            this.location = location;
        }
    }
    
    // IP Info数据模型
    public static class IPInfoData {
        private String city;
        private String region;
        private String country;
        private String location;
        private String organization;
        private String timezone;
        private String hostname;
        
        // getter和setter
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
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public String getLocation() {
            return location;
        }
        
        public void setLocation(String location) {
            this.location = location;
        }
        
        public String getOrganization() {
            return organization;
        }
        
        public void setOrganization(String organization) {
            this.organization = organization;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
        
        public String getHostname() {
            return hostname;
        }
        
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }
    
    // 聚合数据模型
    public static class AggregatedData {
        private String country;
        private String province;
        private String city;
        private String district;
        private Location location;
        private String isp;
        private String timezone;
        
        @SerializedName("is_china_ip")
        private boolean isChinaIp;
        
        private String adcode;
        
        // getter和setter
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
        
        public String getDistrict() {
            return district;
        }
        
        public void setDistrict(String district) {
            this.district = district;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public void setLocation(Location location) {
            this.location = location;
        }
        
        public String getIsp() {
            return isp;
        }
        
        public void setIsp(String isp) {
            this.isp = isp;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
        
        public boolean isChinaIp() {
            return isChinaIp;
        }
        
        public void setChinaIp(boolean chinaIp) {
            isChinaIp = chinaIp;
        }
        
        public String getAdcode() {
            return adcode;
        }
        
        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }
        
        public static class Location {
            private double latitude;
            private double longitude;
            
            // getter和setter
            public double getLatitude() {
                return latitude;
            }
            
            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }
            
            public double getLongitude() {
                return longitude;
            }
            
            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }
        }
    }
    
    // API信息模型
    public static class ApiInfo {
        @SerializedName("requested_apis")
        private List<String> requestedApis;
        
        @SerializedName("successful_apis")
        private List<String> successfulApis;
        
        // getter和setter
        public List<String> getRequestedApis() {
            return requestedApis;
        }
        
        public void setRequestedApis(List<String> requestedApis) {
            this.requestedApis = requestedApis;
        }
        
        public List<String> getSuccessfulApis() {
            return successfulApis;
        }
        
        public void setSuccessfulApis(List<String> successfulApis) {
            this.successfulApis = successfulApis;
        }
    }
} 