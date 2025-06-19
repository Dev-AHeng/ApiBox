package my.zjh.model_sanhaiapi.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.ApiDataPagerAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityIpaddressQueryBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.IPAddressResponse;
import my.zjh.model_sanhaiapi.viewmodel.IPAddressQueryViewModel;

/**
 * IP地址查询-多结果 v1.0
 * 
 * @author AHeng
 * @date 2025/03/26 03:25
 */
@Route(path = "/sanhai/IPAddressQueryActivity")
public class IPAddressQueryActivity extends BaseActivity {
    @Autowired
    ApiItem apiItem;
    private ShActivityIpaddressQueryBinding binding;
    
    // API数据适配器
    private ApiDataPagerAdapter apiDataAdapter;
    
    // ViewModel实例
    private IPAddressQueryViewModel viewModel;
    
    // 当前选中的API优先级
    private Integer selectedApiPriority = null;
    
    // 当前查询的结果文本
    private String resultText = "";
    
    // TabLayoutMediator实例
    private TabLayoutMediator tabLayoutMediator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityIpaddressQueryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 获取ViewModel实例
        viewModel = new ViewModelProvider(this).get(IPAddressQueryViewModel.class);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化界面
        initViews();
        
        // 设置事件监听
        setupListeners();
        
        // 观察ViewModel数据变化
        observeViewModel();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_ip_address_query, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_copy) {
            copyResultToClipboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 将查询结果复制到剪贴板
     */
    private void copyResultToClipboard() {
        if (TextUtils.isEmpty(resultText)) {
            Toast.makeText(this, "暂无查询结果可复制", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取剪贴板管理器
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        
        // 创建ClipData对象
        ClipData clipData = ClipData.newPlainText("IP查询结果", resultText);
        
        // 将文本复制到剪贴板
        clipboardManager.setPrimaryClip(clipData);
        
        Toast.makeText(this, "已复制查询结果到剪贴板", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 初始化界面
     */
    private void initViews() {
        // 初始化ViewPager和TabLayout
        apiDataAdapter = new ApiDataPagerAdapter();
        binding.viewPager.setAdapter(apiDataAdapter);
        
        // 设置API优先级下拉列表
        String[] apiPriorityOptions = getResources().getStringArray(R.array.api_priority_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, apiPriorityOptions);
        ((AutoCompleteTextView) binding.tilApiPriority.getEditText()).setAdapter(adapter);
        
        // 默认选择"所有API"
        ((AutoCompleteTextView) binding.tilApiPriority.getEditText()).setText(apiPriorityOptions[0], false);
    }
    
    /**
     * 设置事件监听
     */
    private void setupListeners() {
        // 查询按钮点击事件
        binding.btnQuery.setOnClickListener(v -> queryIPAddress());
        
        // API优先级选择事件
        ((AutoCompleteTextView) binding.tilApiPriority.getEditText()).setOnItemClickListener((parent, view, position, id) -> {
            // 获取选中的API优先级值
            int[] priorityValues = getResources().getIntArray(R.array.api_priority_values);
            if (position < priorityValues.length) {
                int value = priorityValues[position];
                selectedApiPriority = value > 0 ? value : null; // 如果是0（所有API），则设为null
            }
        });
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            
            if (isLoading) {
                // 隐藏结果卡片和错误信息
                binding.cardAggregatedData.setVisibility(View.GONE);
                binding.cardOriginalData.setVisibility(View.GONE);
                binding.cardError.setVisibility(View.GONE);
                // 清空结果文本
                resultText = "";
            }
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (!TextUtils.isEmpty(errorMsg)) {
                // 隐藏结果卡片
                binding.cardAggregatedData.setVisibility(View.GONE);
                binding.cardOriginalData.setVisibility(View.GONE);
                
                // 显示错误信息
                binding.cardError.setVisibility(View.VISIBLE);
                binding.tvErrorMessage.setText("查询失败: " + errorMsg);
                
                // 设置错误结果文本
                resultText = "查询失败: " + errorMsg;
            } else {
                binding.cardError.setVisibility(View.GONE);
            }
        });
        
        // 观察IP地址查询响应
        viewModel.getIpAddressResponse().observe(this, this::updateUIWithResponse);
        
        // 观察原始API数据
        viewModel.getApiOriginalData().observe(this, this::updateOriginalDataUI);
    }
    
    /**
     * 更新UI显示IP地址查询响应
     * 
     * @param response IP地址查询响应
     */
    private void updateUIWithResponse(IPAddressResponse response) {
        if (response != null) {
            // 显示聚合数据卡片
            binding.cardAggregatedData.setVisibility(View.VISIBLE);
            
            // 构建结果文本
            StringBuilder resultBuilder = new StringBuilder();
            
            // 显示IP地址
            String ipText = "IP: " + response.getIp();
            binding.tvIp.setText(ipText);
            resultBuilder.append(ipText).append("\n");
            
            // 处理聚合数据
            IPAddressResponse.AggregatedData aggregatedData = response.getAggregatedData();
            if (aggregatedData != null) {
                String countryText = "国家: " + (aggregatedData.getCountry() != null ? aggregatedData.getCountry() : "未知");
                binding.tvCountry.setText(countryText);
                resultBuilder.append(countryText).append("\n");
                
                String provinceText = "省份: " + (aggregatedData.getProvince() != null ? aggregatedData.getProvince() : "未知");
                binding.tvProvince.setText(provinceText);
                resultBuilder.append(provinceText).append("\n");
                
                String cityText = "城市: " + (aggregatedData.getCity() != null ? aggregatedData.getCity() : "未知");
                binding.tvCity.setText(cityText);
                resultBuilder.append(cityText).append("\n");
                
                String districtText = "区县: " + (aggregatedData.getDistrict() != null ? aggregatedData.getDistrict() : "未知");
                binding.tvDistrict.setText(districtText);
                resultBuilder.append(districtText).append("\n");
                
                // 处理经纬度信息
                String locationText;
                if (aggregatedData.getLocation() != null) {
                    locationText = String.format("经纬度: %.4f, %.4f", 
                            aggregatedData.getLocation().getLatitude(), 
                            aggregatedData.getLocation().getLongitude());
                } else {
                    locationText = "经纬度: 未知";
                }
                binding.tvLocation.setText(locationText);
                resultBuilder.append(locationText).append("\n");
                
                String ispText = "ISP: " + (aggregatedData.getIsp() != null ? aggregatedData.getIsp() : "未知");
                binding.tvIsp.setText(ispText);
                resultBuilder.append(ispText).append("\n");
                
                String timezoneText = "时区: " + (aggregatedData.getTimezone() != null ? aggregatedData.getTimezone() : "未知");
                binding.tvTimezone.setText(timezoneText);
                resultBuilder.append(timezoneText).append("\n");
                
                String isChinaIpText = "是否中国IP: " + (aggregatedData.isChinaIp() ? "是" : "否");
                binding.tvIsChinaIp.setText(isChinaIpText);
                resultBuilder.append(isChinaIpText).append("\n");
                
                String adcodeText = "行政区划代码: " + (aggregatedData.getAdcode() != null ? aggregatedData.getAdcode() : "未知");
                binding.tvAdcode.setText(adcodeText);
                resultBuilder.append(adcodeText);
            }
            
            // 保存结果文本
            resultText = resultBuilder.toString();
        }
    }
    
    /**
     * 更新原始数据UI
     * 
     * @param apiDataMap API数据Map
     */
    private void updateOriginalDataUI(Map<String, JsonElement> apiDataMap) {
        // 清除之前的数据
        apiDataAdapter.clearData();
        
        // 先分离之前的TabLayoutMediator，避免内存泄漏
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
        
        if (apiDataMap != null && !apiDataMap.isEmpty()) {
            // 将Map转换为有序的列表
            List<String> apiNameList = new ArrayList<>(apiDataMap.keySet());
            
            // 添加各个API的数据
            for (String apiName : apiNameList) {
                apiDataAdapter.addApiData(apiName, apiDataMap.get(apiName));
            }
            
            // 显示原始数据卡片
            binding.cardOriginalData.setVisibility(View.VISIBLE);
            
            // 配置TabLayout
            tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                if (position < apiDataAdapter.getApiNames().size()) {
                    tab.setText(apiDataAdapter.getApiNames().get(position));
                }
            });
            tabLayoutMediator.attach();
        } else {
            binding.cardOriginalData.setVisibility(View.GONE);
        }
    }
    
    /**
     * 查询IP地址
     */
    private void queryIPAddress() {
        // 获取输入的IP地址
        String ipAddress = binding.etIpAddress.getText().toString().trim();
        
        // 验证输入
        if (TextUtils.isEmpty(ipAddress)) {
            Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 调用ViewModel查询IP地址
        viewModel.queryIPAddress(ipAddress, selectedApiPriority);
    }
    
    @Override
    protected void onDestroy() {
        // 分离TabLayoutMediator，避免内存泄漏
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
        super.onDestroy();
    }
}