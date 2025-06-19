package my.zjh.model_sanhaiapi.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.IPDataAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityQueryLocalIpaddressBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.viewmodel.ApiViewModelFactory;
import my.zjh.model_sanhaiapi.viewmodel.LocalIPViewModel;

/**
 * 查询本地IP
 *
 * @author AHeng
 * @date 2025/03/26 04:29
 */
@Route(path = "/sanhai/QueryLocalIPAddressActivity")
public class QueryLocalIPAddressActivity extends BaseActivity {
    @Autowired
    ApiItem apiItem;
    private ShActivityQueryLocalIpaddressBinding binding;
    private LocalIPViewModel viewModel;
    private IPDataAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ShActivityQueryLocalIpaddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this, new ApiViewModelFactory()).get(LocalIPViewModel.class);
        
        // 初始化RecyclerView
        initRecyclerView();
        
        // 设置查询按钮点击事件
        binding.btnQuery.setOnClickListener(v -> queryLocalIP());
        
        // 观察数据变化
        observeData();
    }
    
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        adapter = new IPDataAdapter(this, new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
    
    /**
     * 观察数据变化
     */
    private void observeData() {
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnQuery.setEnabled(!isLoading);
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.tvError.setText(errorMessage);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.cardResults.setVisibility(View.GONE);
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });
        
        // 观察数据项列表
        viewModel.getDataItems().observe(this, dataItems -> {
            adapter.updateData(dataItems);
            binding.cardResults.setVisibility(dataItems.isEmpty() ? View.GONE : View.VISIBLE);
            binding.tvError.setVisibility(View.GONE);
        });
    }
    
    /**
     * 查询本地IP
     */
    private void queryLocalIP() {
        // 获取是否显示详细信息
        int detail = binding.switchDetail.isChecked() ? 1 : 0;
        
        // 查询本地IP
        viewModel.queryLocalIP(detail);
    }
    
    /**
     * 复制所有数据
     */
    private void copyAllData() {
        String allData = viewModel.getAllDataJson();
        if (allData != null && !allData.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("本地IP数据", allData);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "已复制所有数据到剪贴板", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "暂无数据可复制", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_local_ip, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_copy_all) {
            copyAllData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}