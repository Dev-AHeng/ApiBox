package my.zjh.model_guiguiapi.view.hotsearch.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用响应基类，使用泛型处理不同类型的数据
 *
 * @author AHeng
 * @date 2025/04/13 01:22
 */
public class HotSearchList {
    private final Map<String, String> platformMap = new LinkedHashMap<String, String>() {{
        put("百度热点指数", "baidu");
        put("百度百科历史上的今天", "history");
        put("抖音热搜榜", "douyin");
        put("哔哩哔哩全站日榜", "biliall");
        put("哔哩哔哩热搜榜", "bilihot");
        put("ACfun弹幕网热榜", "acfun");
        put("知乎热榜热度", "zhihu");
        put("知乎热门问题", "billboard");
        put("微博热搜榜", "weibo");
        put("搜狗热搜榜", "sogou");
        put("搜狐热榜新闻", "sohu");
        put("今日头条热榜", "toutiao");
        put("爱范儿快讯", "ifanr");
        put("网易新闻热点榜", "netease_news");
        
        put("安全KER热榜", "ker");
        put("稀土掘金文章榜", "juejin");
        put("51CTO推荐榜", "51cto");
        put("CSDN全站综合热榜", "csdn");
        put("Github热门榜", "github");
        
        // 失效
        put("少数派热榜", "sspai");
        put("懂球帝热榜", "dongqiudi");
    }};
    @SerializedName("success")
    private boolean success;
    @SerializedName("msg")
    private String msg;
    @SerializedName("title")
    private String title;
    @SerializedName("subtitle")
    private String subtitle;
    @SerializedName("update_time")
    private String updateTime;
    @SerializedName("total")
    private int total;
    @SerializedName("data")
    private List<HotSearchItem> data;
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"success\": " +
                       success
                       + "," + "\"msg\": " +
                       "\"" + msg + "\""
                       + "," + "\"title\": " +
                       "\"" + title + "\""
                       + "," + "\"subtitle\": " +
                       "\"" + subtitle + "\""
                       + "," + "\"updateTime\": " +
                       "\"" + updateTime + "\""
                       + "," + "\"total\": " +
                       total
                       + "," + "\"data\": " +
                       data
                       + "}";
    }
    
    public Map<String, String> getPlatformMap() {
        return platformMap;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public String getTitle() {
        if (title.contains("15秒了解全球新鲜事")) {
            return "爱范儿";
        }
        
        if (title.contains("文章榜")) {
            return "稀土掘金";
        }
        
        return title;
    }
    
    public String getSubtitle() {
        if (subtitle.contains("历史上的今天")) {
            return "历史上的今天";
        }
        
        if (subtitle.contains("AcFunRank排行榜")) {
            return "排行榜";
        }
        
        return subtitle;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public int getTotal() {
        return total;
    }
    
    public List<HotSearchItem> getData() {
        return data;
    }
    
}