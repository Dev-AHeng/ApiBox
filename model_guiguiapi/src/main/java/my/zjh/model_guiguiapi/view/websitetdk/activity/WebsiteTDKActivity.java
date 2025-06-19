package my.zjh.model_guiguiapi.view.websitetdk.activity;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.hjq.toast.Toaster;

import java.util.Objects;

import my.zjh.model_guiguiapi.databinding.GgActivityWebsitetdkBinding;
import my.zjh.model_guiguiapi.util.DialogFragmentUtils;
import my.zjh.model_guiguiapi.util.LoadingDialog;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;
import my.zjh.model_guiguiapi.view.websitetdk.api.WebsiteTDKApi;
import my.zjh.model_guiguiapi.view.websitetdk.viewmodel.WebsiteTDKViewModel;

/**
 * 网站TDK描述查询-本地-API接口
 *
 * @author AHeng
 * @date 2025/06/09 02:49
 */
@Route(path = "/guigui/WebsiteTDKActivity")
public class WebsiteTDKActivity
        extends GuiBaseActivity<GgActivityWebsitetdkBinding, WebsiteTDKViewModel> {
    private static final String LOADING_DIALOG_TAG = "tdk_loading_dialog_tag";
    private WebsiteTDKViewModel viewModel;
    
    @Autowired
    ApiItemBean apiItemBean;
    
    @Override
    protected CharSequence setTitle() {
        return apiItemBean.getTitle();
    }
    
    @Override
    protected void initView() {
        initViewModel();
        initViews();
        observeLiveData();
    }
    
    /**
     * 初始化ViewModel
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WebsiteTDKViewModel.class);
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        // 设置点击事件
        binding.btnQuery.setOnClickListener(v -> performQuery());
    }
    
    /**
     * 观察LiveData
     */
    private void observeLiveData() {
        
        // 观察查询结果
        viewModel.getQueryResultLiveData().observe(this, this::handleQueryResult);
        
        // 观察错误信息
        viewModel.getErrorLiveData().observe(this, this::handleError);
        
        // 观察加载状态
        viewModel.getLoadingLiveData().observe(this, this::handleLoadingState);
    }
    
    /**
     * 执行查询
     */
    private void performQuery() {
        String url = Objects.requireNonNull(binding.etUrl.getText()).toString().trim();
        
        // 清除之前的错误
        binding.tilUrl.setError(null);
        viewModel.clearError();
        
        // 执行查询
        viewModel.queryWebsiteTDK(url);
    }
    
    /**
     * 处理查询结果
     *
     * @param result 查询结果
     */
    private void handleQueryResult(WebsiteTDKApi.WebsiteTDKModel result) {
        if (result != null) {
            
            // 显示结果卡片
            binding.cvResult.setVisibility(View.VISIBLE);
            
            // 填充数据
            binding.tvFullUrl.setText(result.getFullUrl());
            binding.tvTitle.setText(TextUtils.isEmpty(result.getTitle()) ? "无" : result.getTitle());
            binding.tvDescription.setText(TextUtils.isEmpty(result.getDescription()) ? "无" : result.getDescription());
            binding.tvKeywords.setText(TextUtils.isEmpty(result.getKeywords()) ? "无" : result.getKeywords());
            binding.tvQueryTime.setText(TextUtils.isEmpty(result.getQueryTime()) ? "未知" : result.getQueryTime());
            // binding.tvApiBy.setText(TextUtils.isEmpty(result.getBy()) ? "" : "API by " + result.getBy());
            
            Toaster.showShort("查询成功");
        }
    }
    
    /**
     * 处理错误信息
     *
     * @param error 错误信息
     */
    private void handleError(String error) {
        if (!TextUtils.isEmpty(error)) {
            
            // 隐藏结果卡片
            binding.cvResult.setVisibility(View.GONE);
            
            // 显示错误提示
            if (error.contains("请输入") || error.contains("格式")) {
                binding.tilUrl.setError(error);
            } else {
                Toaster.showShort(error);
            }
        }
    }
    
    /**
     * 处理加载状态
     *
     * @param isLoading 是否正在加载
     */
    private void handleLoadingState(Boolean isLoading) {
        if (isLoading != null) {
            if (isLoading) {
                // 显示加载对话框
                LoadingDialog dialog = new LoadingDialog();
                dialog.setCanCancel(false);
                DialogFragmentUtils.showDialogFragment(this, dialog, LOADING_DIALOG_TAG);
                
                // 禁用查询按钮
                binding.btnQuery.setEnabled(false);
                
            } else {
                // 隐藏加载对话框
                DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
                
                // 启用查询按钮
                binding.btnQuery.setEnabled(true);
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保对话框被关闭
        DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
    }
} 