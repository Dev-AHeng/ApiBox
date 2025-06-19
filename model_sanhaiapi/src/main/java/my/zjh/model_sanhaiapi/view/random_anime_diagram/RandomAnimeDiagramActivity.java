package my.zjh.model_sanhaiapi.view.random_anime_diagram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.ViewPagerAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityRandomAnimeDiagramBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.RandomAnimeResponse;
import my.zjh.model_sanhaiapi.viewmodel.RandomAnimeViewModel;

/**
 * 随机动漫图
 *
 * @author AHeng
 * @date 2025/03/26 22:31
 */
@Route(path = "/sanhai/RandomAnimeDiagramActivity")
public class RandomAnimeDiagramActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    @Autowired
    ApiItem apiItem;
    
    private ShActivityRandomAnimeDiagramBinding binding;
    private RandomAnimeViewModel viewModel;
    private TabLayoutMediator mediator;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    
    private final Fragment[] fragments = new Fragment[3];
    private final String[] titles = new String[]{"随机图片", "我的收藏", "本地下载"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityRandomAnimeDiagramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(RandomAnimeViewModel.class);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化Fragment
        initFragments();
        
        // 设置ViewPager2
        setupViewPager();
    }
    
    /**
     * 初始化Fragment
     */
    private void initFragments() {
        fragments[0] = new RandomAnimeFragment();
        fragments[1] = new FavoriteAnimeFragment();
        fragments[2] = new LocalAnimeFragment();
    }
    
    /**
     * 设置ViewPager2
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments, titles);
        binding.viewPager.setAdapter(adapter);
        // 默认懒加载，只保留相邻页面
        binding.viewPager.setOffscreenPageLimit(1);
        
        // 关联TabLayout和ViewPager2
        mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(titles[position]);
        });
        mediator.attach();
        
        // 监听页面切换，只需简单记录当前位置
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 不需要额外代码，ViewPager2会自动管理Fragment生命周期
            }
        };
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_anime, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_copy) {
            copyInfoToClipboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 复制信息到剪贴板
     */
    private void copyInfoToClipboard() {
        RandomAnimeResponse.Data data = viewModel.getAnimeData().getValue();
        if (data == null) {
            toast("没有图片信息可复制");
            return;
        }
        
        String info = "文件名: " + data.getFilename() + "\n" +
                "URL: " + data.getUrl();
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("随机动漫图信息", info);
        clipboard.setPrimaryClip(clip);
        
        toast("信息已复制到剪贴板");
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        // 将权限结果传递给当前可见的Fragment
        Fragment currentFragment = fragments[binding.viewPager.getCurrentItem()];
        if (currentFragment != null) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    /**
     * 清理资源，防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        
        // 解除TabLayoutMediator关联
        if (mediator != null) {
            mediator.detach();
            mediator = null;
        }
        
        // 注销ViewPager2回调
        if (binding != null && pageChangeCallback != null) {
            binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
            pageChangeCallback = null;
        }
        
        // 清除Fragment引用
        Arrays.fill(fragments, null);
        
        // 清除视图绑定
        binding = null;
        
        
        super.onDestroy();
    }
}