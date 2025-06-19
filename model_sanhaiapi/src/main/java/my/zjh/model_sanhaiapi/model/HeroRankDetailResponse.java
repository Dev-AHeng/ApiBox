package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 英雄战力排名详情响应实体类
 */
public class HeroRankDetailResponse {
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("desc")
    private String desc;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("data")
    private Data data;
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Data getData() {
        return data;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
    
    /**
     * 数据实体类
     */
    public static class Data {
        @SerializedName("hero")
        private Hero hero;
        
        @SerializedName("zone")
        private Zone zone;
        
        @SerializedName("rank_data")
        private RankData rankData;
        
        @SerializedName("type")
        private String type;
        
        @SerializedName("syn_date")
        private String synDate;
        
        public Hero getHero() {
            return hero;
        }
        
        public void setHero(Hero hero) {
            this.hero = hero;
        }
        
        public Zone getZone() {
            return zone;
        }
        
        public void setZone(Zone zone) {
            this.zone = zone;
        }
        
        public RankData getRankData() {
            return rankData;
        }
        
        public void setRankData(RankData rankData) {
            this.rankData = rankData;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getSynDate() {
            return synDate;
        }
        
        public void setSynDate(String synDate) {
            this.synDate = synDate;
        }
    }
    
    /**
     * 英雄基本信息
     */
    public static class Hero {
        @SerializedName("id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("ename")
        private String ename;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getEname() {
            return ename;
        }
        
        public void setEname(String ename) {
            this.ename = ename;
        }
    }
    
    /**
     * 游戏区域信息
     */
    public static class Zone {
        @SerializedName("code")
        private String code;
        
        @SerializedName("system")
        private String system;
        
        @SerializedName("platform")
        private String platform;
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getSystem() {
            return system;
        }
        
        public void setSystem(String system) {
            this.system = system;
        }
        
        public String getPlatform() {
            return platform;
        }
        
        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
    
    /**
     * 战力排名数据
     */
    public static class RankData {
        @SerializedName("extreme")
        private Extreme extreme;
        
        @SerializedName("similar")
        private Similar similar;
        
        public Extreme getExtreme() {
            return extreme;
        }
        
        public void setExtreme(Extreme extreme) {
            this.extreme = extreme;
        }
        
        public Similar getSimilar() {
            return similar;
        }
        
        public void setSimilar(Similar similar) {
            this.similar = similar;
        }
    }
    
    /**
     * 最值数据
     */
    public static class Extreme {
        @SerializedName("province")
        private RankInfo province;
        
        @SerializedName("city")
        private RankInfo city;
        
        @SerializedName("district")
        private RankInfo district;
        
        public RankInfo getProvince() {
            return province;
        }
        
        public void setProvince(RankInfo province) {
            this.province = province;
        }
        
        public RankInfo getCity() {
            return city;
        }
        
        public void setCity(RankInfo city) {
            this.city = city;
        }
        
        public RankInfo getDistrict() {
            return district;
        }
        
        public void setDistrict(RankInfo district) {
            this.district = district;
        }
    }
    
    /**
     * 相近战力数据
     */
    public static class Similar {
        @SerializedName("province")
        private List<RankInfo> province;
        
        @SerializedName("city")
        private List<RankInfo> city;
        
        @SerializedName("district")
        private List<RankInfo> district;
        
        public List<RankInfo> getProvince() {
            return province;
        }
        
        public void setProvince(List<RankInfo> province) {
            this.province = province;
        }
        
        public List<RankInfo> getCity() {
            return city;
        }
        
        public void setCity(List<RankInfo> city) {
            this.city = city;
        }
        
        public List<RankInfo> getDistrict() {
            return district;
        }
        
        public void setDistrict(List<RankInfo> district) {
            this.district = district;
        }
    }
    
    /**
     * 排名信息
     */
    public static class RankInfo {
        @SerializedName("address")
        private String address;
        
        @SerializedName("level")
        private String level;
        
        @SerializedName("adcode")
        private String adcode;
        
        @SerializedName("rank")
        private int rank;
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getLevel() {
            return level;
        }
        
        public void setLevel(String level) {
            this.level = level;
        }
        
        public String getAdcode() {
            return adcode;
        }
        
        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }
        
        public int getRank() {
            return rank;
        }
        
        public void setRank(int rank) {
            this.rank = rank;
        }
    }
} 