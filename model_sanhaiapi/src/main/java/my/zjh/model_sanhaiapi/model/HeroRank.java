package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 王者荣耀英雄战力排名响应实体类
 */
public class HeroRank {
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("desc")
    private String desc;
    
    @SerializedName("data")
    private List<Hero> heroes;
    
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
    
    public List<Hero> getHeroes() {
        return heroes;
    }
    
    public void setHeroes(List<Hero> heroes) {
        this.heroes = heroes;
    }
    
    /**
     * 英雄信息实体类
     */
    public static class Hero {
        @SerializedName("id")
        private int id;
        
        @SerializedName("warId")
        private int warId;
        
        @SerializedName("ename")
        private String ename;
        
        @SerializedName("cname")
        private String cname;
        
        @SerializedName("pname")
        private String pname;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("heroType")
        private int heroType;
        
        @SerializedName("skinName")
        private String skinName;
        
        @SerializedName("status")
        private int status;
        
        @SerializedName("createTime")
        private String createTime;
        
        @SerializedName("updateTime")
        private String updateTime;
        
        @SerializedName("avatar")
        private String avatar;
        
        // Getter和Setter方法
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public int getWarId() {
            return warId;
        }
        
        public void setWarId(int warId) {
            this.warId = warId;
        }
        
        public String getEname() {
            return ename;
        }
        
        public void setEname(String ename) {
            this.ename = ename;
        }
        
        public String getCname() {
            return cname;
        }
        
        public void setCname(String cname) {
            this.cname = cname;
        }
        
        public String getPname() {
            return pname;
        }
        
        public void setPname(String pname) {
            this.pname = pname;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public int getHeroType() {
            return heroType;
        }
        
        public void setHeroType(int heroType) {
            this.heroType = heroType;
        }
        
        public String getSkinName() {
            return skinName;
        }
        
        public void setSkinName(String skinName) {
            this.skinName = skinName;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public String getCreateTime() {
            return createTime;
        }
        
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        
        public String getUpdateTime() {
            return updateTime;
        }
        
        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        
        public String getAvatar() {
            return avatar;
        }
        
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        
        /**
         * 获取英雄类型名称
         */
        public String getHeroTypeName() {
            switch (heroType) {
                case 1:
                    return "战士";
                case 2:
                    return "法师";
                case 3:
                    return "坦克";
                case 4:
                    return "刺客";
                case 5:
                    return "射手";
                case 6:
                    return "辅助";
                default:
                    return "未知";
            }
        }
    }
} 