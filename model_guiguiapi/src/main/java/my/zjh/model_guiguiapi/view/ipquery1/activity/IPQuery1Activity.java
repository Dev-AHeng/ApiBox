package my.zjh.model_guiguiapi.view.ipquery1.activity;

import android.view.KeyEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.Objects;

import my.zjh.model_guiguiapi.util.DialogFragmentUtils;
import my.zjh.model_guiguiapi.util.LoadingDialog;

import my.zjh.model_guiguiapi.databinding.GgActivityIpquery1Binding;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.ipquery1.model.IPQueryResponse;
import my.zjh.model_guiguiapi.view.ipquery1.viewmodel.IPQuery1ViewModel;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;

/**
 * IP地址归属地详情查询1
 *
 * @author AHeng
 * @date 2025/06/08 16:18
 */
@Route(path = "/guigui/IPQuery1Activity")
public class IPQuery1Activity
        extends GuiBaseActivity<GgActivityIpquery1Binding, IPQuery1ViewModel> {
    
    private static final String LOADING_DIALOG_TAG = "loading_dialog";
    
    @Autowired
    ApiItemBean apiItemBean;
    
    @Override
    protected CharSequence setTitle() {
        return apiItemBean.getTitle();
    }
    
    @Override
    protected void initView() {
        // 设置按钮点击事件
        binding.btnQuery.setOnClickListener(v -> performQuery());
        
        // 设置输入框回车事件
        binding.etIpAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                performQuery();
                return true;
            }
            return false;
        });
        
        // 观察ViewModel数据变化
        observeViewModel();
    }
    
    /**
     * 执行IP查询
     */
    private void performQuery() {
        // 隐藏软键盘
        hideSoftKeyboard();
        
        // 清除输入框错误状态
        binding.tilIpAddress.setError(null);
        
        // 获取输入的IP地址
        String ipAddress = Objects.requireNonNull(binding.etIpAddress.getText()).toString().trim();
        
        // 基本验证
        if (ipAddress.isEmpty()) {
            binding.tilIpAddress.setError("请输入IP地址");
            binding.etIpAddress.requestFocus();
            return;
        }
        
        // 调用ViewModel执行查询
        viewModel.queryIPInfo(ipAddress);
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察加载状态
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showLoadingDialog();
                    hideErrorMessage();
                    hideResultCard();
                } else {
                    dismissLoadingDialog();
                }
            }
        });
        
        // 观察查询结果
        viewModel.queryResult.observe(this, result -> {
            if (result != null) {
                hideStatusCard();
                showQueryResult(result);
            }
        });
        
        // 观察错误信息
        viewModel.errorMessage.observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                hideResultCard();
                showError(errorMsg);
                
                // 如果是IP格式错误，设置输入框错误状态
                if (errorMsg.contains("有效的IP地址格式")) {
                    binding.tilIpAddress.setError(errorMsg);
                    binding.etIpAddress.requestFocus();
                }
            }
        });
    }
    
    /**
     * 显示加载对话框
     */
    private void showLoadingDialog() {
        LoadingDialog dialog = new LoadingDialog();
        dialog.setCanCancel(false);
        DialogFragmentUtils.showDialogFragment(this, dialog, LOADING_DIALOG_TAG);
        
        binding.btnQuery.setEnabled(false);
        binding.btnQuery.setText("查询中...");
    }
    
    /**
     * 隐藏加载对话框
     */
    private void dismissLoadingDialog() {
        DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
        
        binding.btnQuery.setEnabled(true);
        binding.btnQuery.setText("查询IP地址信息");
    }
    
    /**
     * 显示查询结果
     *
     * @param result 查询结果
     */
    private void showQueryResult(IPQueryResponse result) {
        if (result.getInfo() != null) {
            IPQueryResponse.IPInfo info = result.getInfo();
            
            // 设置IP地址
            binding.tvIp.setText(result.getIp());
            
            // 设置位置信息
            binding.tvContinent.setText(viewModel.safeGetString(info.getContinent()));
            binding.tvCountry.setText(viewModel.safeGetString(info.getCountry()));
            binding.tvProvince.setText(viewModel.safeGetString(info.getProvince()));
            binding.tvCity.setText(viewModel.safeGetString(info.getCity()));
            binding.tvRegion.setText(viewModel.safeGetString(info.getRegion()));
            binding.tvCarrier.setText(viewModel.safeGetString(info.getCarrier()));
            
            // 设置坐标和行政区划信息
            binding.tvCoordinates.setText(viewModel.formatCoordinates(info.getLongitude(), info.getLatitude()));
            binding.tvDivision.setText(viewModel.safeGetString(info.getDivision()));
            
            // 显示结果卡片
            binding.cardResult.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 显示错误信息
     *
     * @param errorMsg 错误消息
     */
    private void showError(String errorMsg) {
        binding.cardStatus.setVisibility(View.VISIBLE);
        
        binding.tvErrorMessage.setText(errorMsg);
        binding.tvErrorMessage.setVisibility(View.VISIBLE);
    }
    
    /**
     * 隐藏错误消息
     */
    private void hideErrorMessage() {
        binding.tvErrorMessage.setVisibility(View.GONE);
    }
    
    /**
     * 隐藏状态卡片
     */
    private void hideStatusCard() {
        binding.cardStatus.setVisibility(View.GONE);
    }
    
    /**
     * 隐藏结果卡片
     */
    private void hideResultCard() {
        binding.cardResult.setVisibility(View.GONE);
    }
    
    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            android.view.inputmethod.InputMethodManager imm = 
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}