package my.zjh.model_sanhaiapi.model.hotsearch;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 热搜列表
 *
 * @author AHeng
 * @date 2025/04/12 22:08
 */
public class HotSearchTypeList {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    /**
     * 注意：JSON Key 中的空格需要精确匹配
     */
    @SerializedName("百度热点 指数")
    private String baiduHotIndex;
    @SerializedName("少数派 热榜")
    private String sspaiHot;
    @SerializedName("CSDN 全站综合热榜")
    private String csdnHot;
    /**
     * 注意：注意这里有两个空格
     */
    @SerializedName("百度百科  历史上的今天")
    private String historyToday;
    @SerializedName("抖音 热搜榜")
    private String douyinHot;
    @SerializedName("哔哩哔哩 全站日榜")
    private String biliall;
    @SerializedName("哔哩哔哩 热搜榜")
    private String bilihot;
    /**
     * 注意：JSON Key 中可能存在连续空格
     */
    @SerializedName("知乎热榜  热度")
    private String zhihuHot;
    @SerializedName("知乎 热门问题")
    private String zhihuBillboard;
    @SerializedName("微博 热搜榜")
    private String weiboHot;
    @SerializedName("搜狗 热搜榜")
    private String sogouHot;
    @SerializedName("搜狐 热榜新闻")
    private String sohuNews;
    @SerializedName("今日头条 热榜")
    private String toutiaoHot;
    @SerializedName("ACfun弹幕网 热榜")
    private String acfunHot;
    @SerializedName("安全KER 热榜")
    private String kerHot;
    @SerializedName("懂球帝 热榜")
    private String dongqiudiHot;
    @SerializedName("爱范儿 快讯")
    private String ifanrNews;
    @SerializedName("稀土掘金 文章榜")
    private String juejinArticles;
    @SerializedName("网易新闻 热点榜")
    private String neteaseNews;
    @SerializedName("51CTO 推荐榜")
    private String cto51Recommended;
    @SerializedName("Github 热门榜")
    private String githubTrending;
    @SerializedName("copyright")
    private String copyright;
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"success\": " +
                       success
                       + "," + "\"message\": " +
                       "\"" + message + "\""
                       + "," + "\"baiduHotIndex\": " +
                       "\"" + baiduHotIndex + "\""
                       + "," + "\"sspaiHot\": " +
                       "\"" + sspaiHot + "\""
                       + "," + "\"csdnHot\": " +
                       "\"" + csdnHot + "\""
                       + "," + "\"historyToday\": " +
                       "\"" + historyToday + "\""
                       + "," + "\"douyinHot\": " +
                       "\"" + douyinHot + "\""
                       + "," + "\"biliall\": " +
                       "\"" + biliall + "\""
                       + "," + "\"bilihot\": " +
                       "\"" + bilihot + "\""
                       + "," + "\"zhihuHot\": " +
                       "\"" + zhihuHot + "\""
                       + "," + "\"zhihuBillboard\": " +
                       "\"" + zhihuBillboard + "\""
                       + "," + "\"weiboHot\": " +
                       "\"" + weiboHot + "\""
                       + "," + "\"sogouHot\": " +
                       "\"" + sogouHot + "\""
                       + "," + "\"sohuNews\": " +
                       "\"" + sohuNews + "\""
                       + "," + "\"toutiaoHot\": " +
                       "\"" + toutiaoHot + "\""
                       + "," + "\"acfunHot\": " +
                       "\"" + acfunHot + "\""
                       + "," + "\"kerHot\": " +
                       "\"" + kerHot + "\""
                       + "," + "\"dongqiudiHot\": " +
                       "\"" + dongqiudiHot + "\""
                       + "," + "\"ifanrNews\": " +
                       "\"" + ifanrNews + "\""
                       + "," + "\"juejinArticles\": " +
                       "\"" + juejinArticles + "\""
                       + "," + "\"neteaseNews\": " +
                       "\"" + neteaseNews + "\""
                       + "," + "\"cto51Recommended\": " +
                       "\"" + cto51Recommended + "\""
                       + "," + "\"githubTrending\": " +
                       "\"" + githubTrending + "\""
                       + "," + "\"copyright\": " +
                       "\"" + copyright + "\""
                       + "}";
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getBaiduHotIndex() {
        return baiduHotIndex;
    }
    
    public void setBaiduHotIndex(String baiduHotIndex) {
        this.baiduHotIndex = baiduHotIndex;
    }
    
    public String getSspaiHot() {
        return sspaiHot;
    }
    
    public void setSspaiHot(String sspaiHot) {
        this.sspaiHot = sspaiHot;
    }
    
    public String getCsdnHot() {
        return csdnHot;
    }
    
    public void setCsdnHot(String csdnHot) {
        this.csdnHot = csdnHot;
    }
    
    public String getHistoryToday() {
        return historyToday;
    }
    
    public void setHistoryToday(String historyToday) {
        this.historyToday = historyToday;
    }
    
    public String getDouyinHot() {
        return douyinHot;
    }
    
    public void setDouyinHot(String douyinHot) {
        this.douyinHot = douyinHot;
    }
    
    public String getBiliall() {
        return biliall;
    }
    
    public void setBiliall(String biliall) {
        this.biliall = biliall;
    }
    
    public String getBilihot() {
        return bilihot;
    }
    
    public void setBilihot(String bilihot) {
        this.bilihot = bilihot;
    }
    
    public String getZhihuHot() {
        return zhihuHot;
    }
    
    public void setZhihuHot(String zhihuHot) {
        this.zhihuHot = zhihuHot;
    }
    
    public String getZhihuBillboard() {
        return zhihuBillboard;
    }
    
    public void setZhihuBillboard(String zhihuBillboard) {
        this.zhihuBillboard = zhihuBillboard;
    }
    
    public String getWeiboHot() {
        return weiboHot;
    }
    
    public void setWeiboHot(String weiboHot) {
        this.weiboHot = weiboHot;
    }
    
    public String getSogouHot() {
        return sogouHot;
    }
    
    public void setSogouHot(String sogouHot) {
        this.sogouHot = sogouHot;
    }
    
    public String getSohuNews() {
        return sohuNews;
    }
    
    public void setSohuNews(String sohuNews) {
        this.sohuNews = sohuNews;
    }
    
    public String getToutiaoHot() {
        return toutiaoHot;
    }
    
    public void setToutiaoHot(String toutiaoHot) {
        this.toutiaoHot = toutiaoHot;
    }
    
    public String getAcfunHot() {
        return acfunHot;
    }
    
    public void setAcfunHot(String acfunHot) {
        this.acfunHot = acfunHot;
    }
    
    public String getKerHot() {
        return kerHot;
    }
    
    public void setKerHot(String kerHot) {
        this.kerHot = kerHot;
    }
    
    public String getDongqiudiHot() {
        return dongqiudiHot;
    }
    
    public void setDongqiudiHot(String dongqiudiHot) {
        this.dongqiudiHot = dongqiudiHot;
    }
    
    public String getIfanrNews() {
        return ifanrNews;
    }
    
    public void setIfanrNews(String ifanrNews) {
        this.ifanrNews = ifanrNews;
    }
    
    public String getJuejinArticles() {
        return juejinArticles;
    }
    
    public void setJuejinArticles(String juejinArticles) {
        this.juejinArticles = juejinArticles;
    }
    
    public String getNeteaseNews() {
        return neteaseNews;
    }
    
    public void setNeteaseNews(String neteaseNews) {
        this.neteaseNews = neteaseNews;
    }
    
    public String getCto51Recommended() {
        return cto51Recommended;
    }
    
    public void setCto51Recommended(String cto51Recommended) {
        this.cto51Recommended = cto51Recommended;
    }
    
    public String getGithubTrending() {
        return githubTrending;
    }
    
    public void setGithubTrending(String githubTrending) {
        this.githubTrending = githubTrending;
    }
    
    public String getCopyright() {
        return copyright;
    }
    
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
