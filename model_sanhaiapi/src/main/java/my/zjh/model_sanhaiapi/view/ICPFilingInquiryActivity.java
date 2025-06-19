package my.zjh.model_sanhaiapi.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.regex.Pattern;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.databinding.ShActivityIcpfilingInquiryBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.ICPFilingResponse;
import my.zjh.model_sanhaiapi.viewmodel.ApiViewModelFactory;
import my.zjh.model_sanhaiapi.viewmodel.ICPFilingViewModel;

/**
 * ICP备案查询
 *
 * @author AHeng
 * @date 2025/03/26 15:18
 */
@Route(path = "/sanhai/ICPFilingInquiryActivity")
public class ICPFilingInquiryActivity extends BaseActivity {
    // 通过ARouter注入的API项
    @Autowired
    ApiItem apiItem;
    
    // 视图绑定
    private ShActivityIcpfilingInquiryBinding binding;
    
    // ViewModel
    private ICPFilingViewModel viewModel;
    
    // 当前查询结果
    private ICPFilingResponse currentResponse;
    
    // 域名验证正则表达式
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // 初始化视图绑定
        binding = ShActivityIcpfilingInquiryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // ARouter注入
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this, new ApiViewModelFactory()).get(ICPFilingViewModel.class);
        
        // 设置UI监听器
        setupListeners();
        
        // 观察ViewModel中的数据变化
        observeViewModel();
    }
    
    /**
     * 设置UI监听器
     */
    private void setupListeners() {
        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener(v -> performSearch());
        
        // 设置粘贴按钮点击事件
        binding.pasteButton.setOnClickListener(v -> pasteDomainFromClipboard());
        
        // 设置输入框回车键事件
        binding.domainInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        
        // 设置文本点击复制功能
        setupTextClickListeners();
    }
    
    /**
     * 设置文本点击事件监听器
     */
    private void setupTextClickListeners() {
        // 设置各个文本的点击事件，点击直接复制内容
        binding.domainText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "域名"));
        binding.icpNumberText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "主体备案号"));
        binding.organizationText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "主办单位"));
        binding.orgTypeText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "单位性质"));
        binding.websiteNameText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "网站名称"));
        binding.websiteIcpText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "网站备案号"));
        binding.approvalDateText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "审核通过时间"));
        binding.updateTimeText.setOnClickListener(v -> copyText(((TextView) v).getText().toString(), "信息更新时间"));
    }
    
    /**
     * 从剪贴板粘贴域名
     */
    private void pasteDomainFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClip() != null) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String pasteText = item.getText().toString().trim();
            
            if (!pasteText.isEmpty()) {
                binding.domainInput.setText(pasteText);
                Toast.makeText(this, "已粘贴：" + pasteText, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "剪贴板内容为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "剪贴板中没有文本内容", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 复制文本到剪贴板
     * 
     * @param text 要复制的文本
     * @param label 复制内容的标签
     */
    private void copyText(String text, String label) {
        if (text == null || text.isEmpty() || "未提供".equals(text)) {
            Toast.makeText(this, label + "内容为空，无法复制", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(this, "已复制" + label + "到剪贴板", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 复制所有查询结果
     */
    private void copyAllResults() {
        if (currentResponse == null) {
            Toast.makeText(this, "当前没有查询结果", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("ICP备案查询结果\n\n");
        
        // 添加主体信息
        sb.append("查询域名：").append(currentResponse.getDomain()).append("\n");
        sb.append("主体备案号：").append(currentResponse.getIcpNumber()).append("\n");
        sb.append("主办单位：").append(currentResponse.getOrganization()).append("\n");
        sb.append("单位性质：").append(currentResponse.getOrgType()).append("\n\n");
        
        // 添加网站信息
        sb.append("网站信息\n");
        ICPFilingResponse.ICPData data = currentResponse.getData();
        if (data != null) {
            String websiteName = data.getWebsiteName();
            if (websiteName == null || websiteName.isEmpty() || "null".equals(websiteName)) {
                websiteName = "未提供";
            }
            
            sb.append("网站名称：").append(websiteName).append("\n");
            sb.append("网站备案号：").append(data.getWebsiteIcp()).append("\n");
            sb.append("审核通过时间：").append(data.getApprovalDate()).append("\n");
            sb.append("信息更新时间：").append(data.getUpdateTime()).append("\n\n");
        }
        
        // 添加查询时间
        sb.append("查询时间：").append(currentResponse.getTimestamp());
        
        // 复制到剪贴板
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ICP备案查询结果", sb.toString());
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(this, "已复制所有查询结果到剪贴板", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 验证域名格式
     *
     * @param domain 要验证的域名
     * @return 是否为有效的域名格式
     */
    private boolean isValidDomain(String domain) {
        if (TextUtils.isEmpty(domain)) {
            return false;
        }
        
        // 处理URL格式的域名输入（如 http://example.com）
        String processedDomain = domain;
        if (domain.startsWith("http://")) {
            processedDomain = domain.substring(7);
        } else if (domain.startsWith("https://")) {
            processedDomain = domain.substring(8);
        }
        
        // 去除可能的路径部分
        int pathIndex = processedDomain.indexOf('/');
        if (pathIndex > 0) {
            processedDomain = processedDomain.substring(0, pathIndex);
        }
        
        // 应用正则表达式验证
        return DOMAIN_PATTERN.matcher(processedDomain).matches();
    }
    
    /**
     * 执行搜索操作
     */
    private void performSearch() {
        // 获取输入内容
        Editable text = binding.domainInput.getText();
        String domain = text != null ? text.toString().trim() : "";
        
        // 验证输入
        if (TextUtils.isEmpty(domain)) {
            Toast.makeText(this, "请输入要查询的域名", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证域名格式
        if (!isValidDomain(domain)) {
            Toast.makeText(this, "请输入有效的域名格式，如：example.com", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 提取纯域名（去除http://、https://前缀）
        if (domain.startsWith("http://")) {
            domain = domain.substring(7);
        } else if (domain.startsWith("https://")) {
            domain = domain.substring(8);
        }
        
        // 去除可能的路径部分
        int pathIndex = domain.indexOf('/');
        if (pathIndex > 0) {
            domain = domain.substring(0, pathIndex);
        }
        
        // 清除之前的结果
        hideResultCard();
        hideErrorText();
        currentResponse = null;
        
        // 执行查询
        viewModel.queryICPFiling(domain);
    }
    
    /**
     * 观察ViewModel中的数据变化
     */
    private void observeViewModel() {
        // 观察查询结果
        viewModel.getIcpFilingResult().observe(this, this::updateResultUI);
        
        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (binding == null) {
                return;
            }
            
            if (error != null && !error.isEmpty()) {
                showErrorText(error);
            } else {
                hideErrorText();
            }
        });
        
        // 观察加载状态
        viewModel.getLoading().observe(this, isLoading -> {
            if (binding == null) {
                return;
            }
            
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.searchButton.setEnabled(!isLoading);
            binding.pasteButton.setEnabled(!isLoading);
            binding.domainInputLayout.setEnabled(!isLoading);
        });
    }
    
    /**
     * 更新结果UI
     *
     * @param response 查询响应
     */
    private void updateResultUI(ICPFilingResponse response) {
        if (binding == null) {
            return;
        }
        
        if (response == null) {
            hideResultCard();
            return;
        }
        
        // 保存当前响应
        currentResponse = response;
        
        // 检查状态
        if ("error".equals(response.getStatus())) {
            showErrorText(response.getMessage());
            hideResultCard();
            return;
        }
        
        // 显示结果卡片
        binding.resultCard.setVisibility(View.VISIBLE);
        
        // 设置主体信息（使用便捷方法，从data对象中获取）
        binding.domainText.setText(response.getDomain());
        binding.icpNumberText.setText(response.getIcpNumber());
        binding.organizationText.setText(response.getOrganization());
        binding.orgTypeText.setText(response.getOrgType());
        
        // 设置网站信息
        ICPFilingResponse.ICPData data = response.getData();
        if (data != null) {
            // 修复网站名称为空时显示"null"的问题
            String websiteName = data.getWebsiteName();
            if (websiteName == null || websiteName.isEmpty() || "null".equals(websiteName)) {
                binding.websiteNameText.setText("未提供");
            } else {
                binding.websiteNameText.setText(websiteName);
            }
            
            binding.websiteIcpText.setText(data.getWebsiteIcp());
            binding.approvalDateText.setText(data.getApprovalDate());
            binding.updateTimeText.setText(data.getUpdateTime());
        }
        
        // 设置时间戳
        binding.timestampText.setText(response.getTimestamp());
    }
    
    /**
     * 显示错误文本
     *
     * @param errorMessage 错误信息
     */
    private void showErrorText(String errorMessage) {
        if (binding == null) {
            return;
        }
        
        binding.errorText.setText(errorMessage);
        binding.errorText.setVisibility(View.VISIBLE);
    }
    
    /**
     * 隐藏错误文本
     */
    private void hideErrorText() {
        if (binding == null) {
            return;
        }
        
        binding.errorText.setVisibility(View.GONE);
    }
    
    /**
     * 隐藏结果卡片
     */
    private void hideResultCard() {
        if (binding == null) {
            return;
        }
        
        binding.resultCard.setVisibility(View.GONE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_icp_inquiry, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_copy_all) {
            copyAllResults();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放绑定，避免内存泄漏
        binding = null;
        
        // ViewModel会在Activity销毁时自动调用onCleared()方法清理资源
    }
}


