package my.zjh.model_guiguiapi.view.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;

/**
 * GuiGui主页面ViewModel，负责管理API列表数据
 *
 * @author AHeng
 * @date 2025/05/02 9:23
 */
public class GuiGuiMainViewModel extends ViewModel {
    private final MutableLiveData<List<ApiItemBean>> apiList = new MutableLiveData<>();
    
    /**
     * 构造函数，初始化API列表数据
     */
    public GuiGuiMainViewModel() {
        initApiList();
    }
    
    /**
     * 初始化API列表数据
     */
    private void initApiList() {
        List<ApiItemBean> apiItemBeanList = new ArrayList<>();
        apiItemBeanList.add(new ApiItemBean("1", "聚合热搜", "聚合热搜", "/guigui/HotSearchActivity", true));
        apiItemBeanList.add(new ApiItemBean("2", "抖音视频解析", "聚合短视频去水印", "/guigui/DouYinVideoAnalysisActivity", true));
        apiItemBeanList.add(new ApiItemBean("3", "二维码生成", "快速将URL或者文字生成二维码", "/guigui/QRCodeGenerationActivity", true));
        apiItemBeanList.add(new ApiItemBean("4", "IP地址归属地详情查询1", "IP地址归属地详情查询1", "/guigui/IPQuery1Activity", true));
        apiItemBeanList.add(new ApiItemBean("5", "网站TDK描述查询", "查询网站标题关键词描述等等-本地", "/guigui/WebsiteTDKActivity", true));
        apiItemBeanList.add(new ApiItemBean("6", "获取必应每日壁纸", "必应近七天封面图片", "/guigui/BingCoversBy7DaysActivity", true));
       
        apiList.setValue(apiItemBeanList);
    }
    
    /**
     * 获取API列表LiveData
     *
     * @return API列表的LiveData对象
     */
    public LiveData<List<ApiItemBean>> getApiList() {
        return apiList;
    }
    
    /**
     * 刷新API列表数据
     */
    public void refreshApiList() {
        initApiList();
    }
    
    /**
     * 添加新的API项
     *
     * @param apiItem 要添加的API项
     */
    public void addApiItem(ApiItemBean apiItem) {
        List<ApiItemBean> currentList = apiList.getValue();
        if (currentList != null) {
            List<ApiItemBean> newList = new ArrayList<>(currentList);
            newList.add(apiItem);
            apiList.setValue(newList);
        }
    }
}
