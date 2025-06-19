package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.model.ApiItem;

/**
 * API列表的ViewModel，负责管理数据和业务逻辑
 *
 * @author AHeng
 * @date 2025/03/26 04:54
 */
public class ApiListViewModel extends ViewModel {
    
    private final MutableLiveData<List<ApiItem>> apiListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    
    // 管理RxJava订阅
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    /**
     * 构造函数
     */
    public ApiListViewModel() {
        // 初始化时加载数据
        loadApiList();
    }
    
    @Override
    protected void onCleared() {
        // 清理RxJava订阅，避免内存泄漏
        compositeDisposable.clear();
        super.onCleared();
    }
    
    /**
     * 获取API列表数据
     */
    public LiveData<List<ApiItem>> getApiList() {
        return apiListLiveData;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }
    
    /**
     * 获取错误信息
     */
    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }
    
    /**
     * 加载API列表数据
     */
    private void loadApiList() {
        // 如果已经在加载，则不重复请求
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            return;
        }
        
        isLoadingLiveData.setValue(true);
        // 清空错误信息
        errorMessageLiveData.setValue(null);
        
        // 使用RxJava处理异步操作
        compositeDisposable.add(
                // 创建模拟数据
                createMockData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(apiItems -> {
                            isLoadingLiveData.setValue(false);
                            apiListLiveData.setValue(apiItems);
                        }, throwable -> {
                            isLoadingLiveData.setValue(false);
                            errorMessageLiveData.setValue("加载数据失败: " + throwable.getMessage());
                        })
        );
    }
    
    /**
     * 刷新数据
     */
    public void refreshData() {
        loadApiList();
    }
    
    /**
     * 创建模拟数据
     */
    private Single<List<ApiItem>> createMockData() {
        return Single.fromCallable(() -> {
            // 模拟网络延迟
            // Thread.sleep(800);
            
            List<ApiItem> apiItems = new ArrayList<>();
            
            // 添加新的模拟数据
            apiItems.add(new ApiItem("1", "聚合短视频解析", "目前支持的平台有:抖音快手火山视频西瓜视频皮皮虾秒拍头条视频腾讯微视美图秀秀美拍微博小红书网易云目前只支持视频类解析", true, "/sanhai/AggregateShortVideoAnalysisActivity"));
            apiItems.add(new ApiItem("2", "咪咕音乐支持VIP", "咪咕音乐API提供音乐搜索和音乐详情获取服务，支持高品质音乐、歌词和封面获取服务。可以返回文本格式或JSON格式的数据，适用于音乐播放器和音乐网站开发。", true, "/sanhai/MiguMusicVipActivity"));
            apiItems.add(new ApiItem("3", "IP地址查询-多结果", "多源IP地理位置查询服务，能够通过多个上游API获取IP地址的地理位置信息。该服务会自动整合多个数据源的结果，提供更全面可靠的IP地理位置信息。", true, "/sanhai/IPAddressQueryActivity"));
            apiItems.add(new ApiItem("4", "本地IP查询", "此接口用于查询客户端的网络、设备、浏览器、服务器和安全信息。支持简略信息和完整信息两种返回格式。", true, "/sanhai/QueryLocalIPAddressActivity"));
            // apiItems.add(new ApiItem("5", "聚合短视频解析2", "目前支持的平台有:抖音快手火山视频西瓜视频皮皮虾秒拍头条视频腾讯微视美图秀秀美拍微博小红书网易云目前只支持视频类解析", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("6", "ICP备案查询", "ICP备案信息查询API提供域名ICP备案信息的实时查询服务，支持查询主办单位、备案号、网站名称等信息。", true, "/sanhai/ICPFilingInquiryActivity"));
            apiItems.add(new ApiItem("7", "一言", "动漫也好、小说也好、网络也好，不论在哪里，我们总会看到有那么一两个句子能穿透你的心。", true, "/sanhai/OneSentenceActivity"));
            apiItems.add(new ApiItem("8", "QQ基础信息", "此接口用于查询指定QQ号码的用户信息，包括昵称、头像、QQ空间链接等。", true, "/sanhai/QQAccountInformationActivity"));
            // apiItems.add(new ApiItem("9", "随机一言-英文版", "此接口用于获取随机的名言句子，包含英文原文、中文翻译以及出处信息。", true, "/sanhai/xxx"));
            // apiItems.add(new ApiItem("10", "访问量统计-动漫图片版", "访问量统计服务是一个生成SVG格式访问计数器的服务，支持多种主题风格，并提供每日访问量统计功能。计数器以图片形式展示，支持自定义名称和主题。", true, "/sanhai/xxx"));
            // apiItems.add(new ApiItem("11", "聚合天气预报", "多个天气数据源的综合天气查询API，支持墨迹天气、百度天气、中央天气和彩云天气等多个数据源，提供全面的天气信息查询服务。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("12", "随机动漫图", "一个简单的随机动漫图片API接口，支持多种图片分类，可用于网站背景、头像、壁纸等场景。", true, "/sanhai/RandomAnimeDiagramActivity"));
            // apiItems.add(new ApiItem("13", "60秒看世界图片生成", "生成每日60秒看世界资讯图片，包含当日新闻热点、农历日期、阳历日期以及每日一言等信息。自动获取百度热搜新闻，集成图文并茂的信息展示，适用于日报、资讯速览等场景。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("14", "英雄战力排名查询", "此接口用于查询指定英雄在特定游戏区域（如iOS微信区、安卓QQ区等）的战力排名信息，支持查询最低战力、最高战力或全部排名数据。", true, "/sanhai/WangZheHeroStrengthRankingQueryActivity"));
            apiItems.add(new ApiItem("15", "条形码查询", "该API提供了通过条形码查询商品详情的功能。用户只需提供有效的条形码，API会返回与该条形码对应的商品信息。", true, "/sanhai/BarcodeQueryActivity"));
            
            apiItems.add(new ApiItem("16", "60秒看世界图片生成", "生成每日60秒看世界资讯图片，包含当日新闻热点、农历日期、阳历日期以及每日一言等信息。自动获取百度热搜新闻，集成图文并茂的信息展示，适用于日报、资讯速览等场景。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("17", "英雄战力排名查询", "此接口用于查询指定英雄在特定游戏区域（如iOS微信区、安卓QQ区等）的战力排名信息，支持查询最低战力、最高战力或全部排名数据。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("18", "微信运动", "此接口用于将用户的微信运动步数同步到指定的API服务，支持自定义步数或生成随机步数。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("19", "条形码查询", "该API提供了通过条形码查询商品详情的功能。用户只需提供有效的条形码，API会返回与该条形码对应的商品信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("20", "咪咕音乐热歌榜单", "此接口用于爬取咪咕音乐尖叫热歌榜的歌曲信息，包括歌曲排名、名称、艺术家、时长、专辑信息及封面图片。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("21", "聚合搜题", "该API提供了一种通过查询问题在多个数据源中查找答案的方式。通过传入问题，API会从题海网和山海云端API获取相关数据，并返回整理后的答案与相关信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("22", "随机写真图", "免费随机写真图API，基于写真图API构建，提供高速快捷免费的写真图。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("23", "聚合热搜", "聚合了知乎、微博、百度三大平台热搜榜的API服务。通过该API，您可以方便地获取这三个平台的实时热搜数据。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("24", "随机美女视频", "随机美女视频API提供随机的美女视频内容，支持直接跳转到视频播放页面或返回JSON格式的视频信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("25", "手机号码归属地查询", "提供手机号码归属地信息查询服务，支持查询运营商、归属地区、邮编、区号等详细信息。数据来源可靠，实时更新，适用于用户信息核验、商业数据分析等场景。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("26", "QQ详细资料查询", "此接口用于查询QQ用户的信息资料卡，本地不存储数据", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("27", "IP签名档", "IP签名档服务是一个生成个性化SVG格式签名图片的服务。集成了IP信息、天气信息、访问统计等功能，支持多种主题切换，可自定义尺寸。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("28", "违禁词检测", "此接口用于检测文本中是否包含违禁词，支持GET和POST请求。违禁词库从指定文件加载，并进行了缓存优化以提高性能。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("29", "占位图生成", "图片占位服务是一个用于生成临时占位图片的服务。它提供了缓存机制和错误处理，支持自定义图片尺寸、背景颜色、文字等特性。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("30", "多维文案评测", "提供专业的文案质量评估与优化建议服务。通过简单的GET请求即可获取AI对文案的多维度分析。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("31", "全球时间获取", "此接口用于查询指定时区的当前时间信息，支持多种时区，并返回详细的时间数据。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("32", "快递查询", "这是一个快递轨迹查询服务，支持查询各大快递公司的物流轨迹信息。通过提供快递单号，即可获取完整的物流追踪信息", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("33", "网页截图", "将任意网页链接转换为图片截图，支持自定义宽度和高度，适用于网页预览、缩略图生成等场景。支持 HTTP 和 HTTPS 网址，输出统一为 JPEG 格式图片。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("34", "Bilibili视频搜索", "基于哔哩哔哩官方接口的视频搜索服务，支持视频搜索和详情查看功能。可以根据关键词搜索视频列表，也可以获取单个视频的详细信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("35", "聚合随机图片(28种)", "此接口用于调用随机图片。注：1：淘宝秀图, 2：清纯美女, 3：超清动漫, 4：美腿制服, 5：微博网红, 6：桜井宁宁, 7：瞄糖映画, 8：兔玩映画, 9：自然风景, 10：高清壁纸, 11：女生头像, 12：男生头像, 13：动漫头像, 14：动漫女头, 15：动漫男头, 16：游戏壁纸, 17：视觉创意, 18：明星影视, 19：炫酷汽车, 20：萌宠动物, 21：体育运动, 22：军事枪械, 23：动漫卡通, 24：情感文案, 25：文案配图, 26：你的负卿, 27：过期米线, 28：蜜汁猫裘", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("36", "动态SVG图表生成", "此接口用于生成动态的SVG图表，支持多种图表类型（如折线图、柱状图、面积图），并允许自定义样式、数据和交互功能。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("37", "腾讯动漫搜索", "这是一个基于腾讯动漫的漫画搜索服务，支持漫画搜索和详情查看功能。可以根据关键词搜索漫画列表，也可以获取单个漫画的详细信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("38", "春节灯笼", "此接口用于在网页上添加春节灯笼效果，支持自定义显示的文字内容。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("39", "聚合翻译", "这是一个支持多种语言互译的RESTfulAPI接口，支持自动语言检测，可以快速实现文本的跨语言转换。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("40", "身份证归属地查询", "根据身份证号码查询对应的省、市、区（县）信息，支持六位及以上数字的查询。为保护隐私，系统会自动对超过6位的号码部分进行脱敏处理。数据实时更新，覆盖全国各省市区县行政区划信息。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("41", "WHOIS查询", "此接口用于查询域名的WHOIS信息，支持多种顶级域名的查询，包括com、net、org、cn等常见域名。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("42", "二维码生成", "功能强大的二维码生成API，支持自定义二维码大小、容错级别和边框宽度，可以生成高质量的PNG格式二维码图片。", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("43", "聚合随机视频(72种)", "此接口用于调用随机类型视频", true, "/sanhai/xxx"));
            apiItems.add(new ApiItem("44", "聚合加密/解密", "多功能的加密解密API，支持多种加密方式，包括Base64、AES、MD5、SHA1、SHA256等，可以满足各种加密解密需求。", true, "/sanhai/xxx"));
            
            
            return apiItems;
        });
    }
} 