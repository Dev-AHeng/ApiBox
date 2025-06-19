package my.zjh.model_sanhaiapi.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.chip.Chip;

import java.util.HashMap;
import java.util.Map;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.HeroAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityWangZheHeroStrengthRankingQueryBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.viewmodel.OptimizedHeroRankViewModel;

/**
 * 王者荣耀英雄战力排名查询
 *
 * @author AHeng
 * @date 2025/03/27 02:25
 */
@Route(path = "/sanhai/WangZheHeroStrengthRankingQueryActivity")
public class WangZheHeroStrengthRankingQueryActivity extends BaseActivity {
    @Autowired
    ApiItem apiItem;
    
    private ShActivityWangZheHeroStrengthRankingQueryBinding binding;
    private OptimizedHeroRankViewModel viewModel;
    private HeroAdapter adapter;
    
    // 英雄类型映射
    private final Map<Integer, Integer> chipToHeroType = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // 使用ViewBinding
        binding = ShActivityWangZheHeroStrengthRankingQueryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(OptimizedHeroRankViewModel.class);
        
        // 初始化UI
        setupChipGroup();
        setupRecyclerView();
        setupSwipeRefresh();
        setupErrorView();
        
        // 观察数据变化
        observeViewModel();
        
        // 加载数据
        loadData();
    }
    
    /**
     * 设置英雄类型筛选Chip组
     */
    private void setupChipGroup() {
        // 初始化英雄类型映射
        chipToHeroType.put(R.id.chip_all, 0);
        chipToHeroType.put(R.id.chip_warrior, 1);
        chipToHeroType.put(R.id.chip_mage, 2);
        chipToHeroType.put(R.id.chip_tank, 3);
        chipToHeroType.put(R.id.chip_assassin, 4);
        chipToHeroType.put(R.id.chip_shooter, 5);
        chipToHeroType.put(R.id.chip_support, 6);
        
        // 设置Chip点击事件
        binding.chipGroupHeroType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            
            // 获取选中的英雄类型ID
            int chipId = checkedIds.get(0);
            Integer heroType = chipToHeroType.get(chipId);
            
            // 更新数据过滤
            if (heroType != null) {
                viewModel.filterHeroesByType(heroType);
            }
        });
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new HeroAdapter();
        binding.recyclerViewHeroes.setAdapter(adapter);
        
        // 设置点击事件
        adapter.setOnItemClickListener(hero -> {
            Toast.makeText(this, "点击了英雄: " + hero.getCname(), Toast.LENGTH_SHORT).show();
            // 可以在这里打开英雄详情页面
        });
        
        // 监听滚动事件，控制FAB的显示和隐藏
        binding.recyclerViewHeroes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 防止在Activity销毁后调用
                if (binding == null) {
                    return;
                }
                
                // 向下滚动，隐藏FAB
                if (dy > 0 && binding.fabTop.getVisibility() == View.VISIBLE) {
                    binding.fabTop.hide();
                } 
                // 向上滚动，显示FAB
                else if (dy < 0 && binding.fabTop.getVisibility() != View.VISIBLE) {
                    binding.fabTop.show();
                }
                
                // 检查是否滚动到顶部
                if (!recyclerView.canScrollVertically(-1)) {
                    binding.fabTop.hide();
                }
            }
        });
        
        // 设置FAB点击事件，回到顶部
        binding.fabTop.setOnClickListener(v -> {
            binding.recyclerViewHeroes.smoothScrollToPosition(0);
        });
    }
    
    /**
     * 设置下拉刷新
     */
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }
    
    /**
     * 设置错误视图
     */
    private void setupErrorView() {
        binding.buttonRetry.setOnClickListener(v -> loadData());
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察英雄数据
        viewModel.getHeroes().observe(this, heroes -> {
            adapter.submitList(heroes);
            binding.layoutError.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            
            if (heroes == null || heroes.isEmpty()) {
                binding.layoutError.setVisibility(View.VISIBLE);
                binding.textError.setText("暂无数据");
                binding.buttonRetry.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察加载状态
        viewModel.getLoading().observe(this, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            
            if (isLoading) {
                binding.layoutError.setVisibility(View.GONE);
            }
        });
        
        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                binding.layoutError.setVisibility(View.VISIBLE);
                binding.textError.setText(error);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                binding.layoutError.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * 加载数据
     */
    private void loadData() {
        // 重置筛选器为全部
        Chip chipAll = binding.chipAll;
        if (!chipAll.isChecked()) {
            chipAll.setChecked(true);
        }
        
        // 请求数据
        viewModel.fetchHeroRank();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        // 移除RecyclerView的滚动监听器，防止内存泄漏
        if (binding != null && binding.recyclerViewHeroes != null) {
            binding.recyclerViewHeroes.clearOnScrollListeners();
        }
        
        // 清除adapter的监听器
        if (adapter != null) {
            adapter.setOnItemClickListener(null);
        }
        
        // 取消任何正在进行的异步操作
        if (viewModel != null) {
            // 如果ViewModel有任何需要清理的资源，这里处理
        }
        
        // 最后清空binding引用
        binding = null;
        
        super.onDestroy();
    }
}