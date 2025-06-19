package my.zjh.model_guiguiapi.view.hotsearch.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import me.liam.support.SupportActivity;
import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.databinding.GgActivityHotSearchBinding;
import my.zjh.model_guiguiapi.util.ViewPagerUtil;
import my.zjh.model_guiguiapi.view.hotsearch.adapter.HotSearchFragmentAdapter;
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchList;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;

/**
 * 鬼鬼api
 *
 * @author AHeng
 * @date 2025/04/13 02:34
 */
@Route(path = "/guigui/HotSearchActivity")
public class HotSearchActivity extends SupportActivity {
    @Autowired
    ApiItemBean apiItemBean;
    
    private final Map<String, String> platformMap = new HotSearchList().getPlatformMap();
    private GgActivityHotSearchBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    
    @Override
    public int getDefaultBackground() {
        return ContextCompat.getColor(this, R.color.md_theme_background);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        binding = GgActivityHotSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        initToolbar();
        
        // 创建并设置适配器，使用 FragmentManager 和 Lifecycle
        HotSearchFragmentAdapter hotSearchFragmentAdapter = new HotSearchFragmentAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                new ArrayList<>(platformMap.values())
        );
        
        // 使用弱引用持有 Activity，防止内存泄漏
        final WeakReference<HotSearchActivity> weakActivity = new WeakReference<>(this);
        
        binding.viewPager2.setAdapter(hotSearchFragmentAdapter);
        
        // 预加载全部
        binding.viewPager2.setOffscreenPageLimit(platformMap.size() - 1);
        
        // 降低ViewPager2的滑动灵敏度
        ViewPagerUtil.desensitization(binding.viewPager2);
        
        // 滑动切换动画
        // binding.viewPager2.setPageTransformer(new LessSensitivePageTransformer());
        
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                HotSearchActivity activity = weakActivity.get();
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                // 在这里处理页面选择事件
                String selectedPlatform = new ArrayList<>(platformMap.keySet()).get(position);
                // 更新标题或其他UI元素
                binding.ggToolbarLayout.toolbar.setTitle(selectedPlatform);
            }
        };
        
        // 添加页面变化监听器
        binding.viewPager2.registerOnPageChangeCallback(pageChangeCallback);
        
        // 将 TabLayout 与 ViewPager2 关联
        tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, i) -> {
            String tabTitle = platformMap.keySet().toArray()[i].toString();
            tab.setText(tabTitle);
        });
        tabLayoutMediator.attach();
        
    }
    
    private void initToolbar() {
        ARouter.getInstance().inject(this);
        // 使用基类方法设置Toolbar
        setupToolbar(binding.ggToolbarLayout.toolbar, apiItemBean.getTitle());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 先分离 TabLayoutMediator
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
        
        // 注销 ViewPager2 页面变化回调
        if (binding != null && pageChangeCallback != null) {
            binding.viewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
            pageChangeCallback = null;
            binding.viewPager2.setAdapter(null);
        }
        
        // 清空绑定引用
        binding = null;
    }
    
    protected void setupToolbar(Toolbar toolbar, String title) {
        // 设置标题
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }
    
    
}
