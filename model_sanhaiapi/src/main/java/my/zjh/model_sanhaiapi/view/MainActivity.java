package my.zjh.model_sanhaiapi.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.ApiListAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityMainBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.viewmodel.ApiListViewModel;
import my.zjh.model_sanhaiapi.viewmodel.ApiViewModelFactory;

/**
 * 山海api
 *
 * @author AHeng
 * @date 2025/03/23 00:22
 */
@Route(path = "/sanhai/MainActivity")
public class MainActivity extends BaseActivity {
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    private ShActivityMainBinding binding;
    private ApiListViewModel viewModel;
    private ApiListAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ShActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, "山海", binding.shToolbarLayout.appbar, true);
        
        // 使用Factory初始化ViewModel
        viewModel = new ViewModelProvider(this, new ApiViewModelFactory()).get(ApiListViewModel.class);
        
        // 初始化适配器
        setupRecyclerView();
        
        // 设置下拉刷新
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refreshData());
        
        // 设置重试按钮
        binding.retryButton.setOnClickListener(v -> viewModel.refreshData());
        binding.refreshButton.setOnClickListener(v -> viewModel.refreshData());
        
        // 设置FAB点击事件
        binding.fab.setOnClickListener(v -> showHelpDialog());
        
        // 观察数据变化
        observeViewModel();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            viewModel.refreshData();
            return true;
        } else if (id == R.id.action_settings) {
            toast("设置功能尚未实现");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        // 清理所有订阅，避免内存泄漏
        compositeDisposable.clear();
        super.onDestroy();
    }
    
    /**
     * 显示帮助对话框
     */
    private void showHelpDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_help_title)
                .setMessage(R.string.dialog_help_message)
                .setPositiveButton(R.string.dialog_btn_ok, null)
                .show();
    }
    
    /**
     * 初始化RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new ApiListAdapter();
        
        // 设置RecyclerView的预取和缓存
        // 增加缓存大小
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setDrawingCacheEnabled(true);
        binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        // 如果项目高度固定，可以提高性能
        binding.recyclerView.setHasFixedSize(true);
        
        // 关闭默认动画，减少闪烁
        binding.recyclerView.setItemAnimator(null);
        
        // 固定使用双列瀑布流布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        
        // 设置点击监听器
        adapter.setOnApiItemClickListener(this::navigateToApiDetail);
    }
    
    /**
     * 计算列数
     */
    private int calculateSpanCount() {
        // 根据屏幕宽度确定合适的列数
        float density = getResources().getDisplayMetrics().density;
        int screenWidthDp = (int) (getResources().getDisplayMetrics().widthPixels / density);
        
        // 假设我们希望每个卡片最小宽度为300dp
        int minCardWidthDp = 300;
        
        return Math.max(1, screenWidthDp / minCardWidthDp);
    }
    
    /**
     * 处理API项点击
     */
    private void navigateToApiDetail(ApiItem apiItem) {
        ARouter.getInstance()
                .build(apiItem.getRoutePath())
                .withParcelable("apiItem", apiItem)
                .navigation();
        toast("点击了API: " + apiItem.getTitle());
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察API列表数据
        viewModel.getApiList().observe(this, apiItems -> {
            // 使用提交列表而不是在UI线程立即生效，避免闪烁
            adapter.submitList(apiItems);
            
            // 更新UI状态
            binding.swipeRefresh.setRefreshing(false);
            if (apiItems != null) {
                // 避免频繁的可见性切换，先检查当前状态
                boolean isEmpty = apiItems.isEmpty();
                if (binding.emptyLayout.getVisibility() == View.VISIBLE != isEmpty) {
                    binding.emptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                }
                if (binding.recyclerView.getVisibility() == View.VISIBLE == isEmpty) {
                    binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                }
                
                // 当列表有数据时，才执行预加载
                if (!isEmpty) {
                    adapter.preloadItems(binding.recyclerView, 5);
                }
            }
        });
        
        // 观察加载状态
        viewModel.isLoading().observe(this, isLoading -> {
            // 如果已经在刷新，则不再设置刷新状态
            if (!binding.swipeRefresh.isRefreshing()) {
                binding.swipeRefresh.setRefreshing(isLoading);
            }
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            // 显示错误信息
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // 显示错误信息，例如使用Snackbar
                toast(errorMessage);
            }
        });
    }
}