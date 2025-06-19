package my.zjh.model_guiguiapi.view.qrcodegeneration.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Objects;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.databinding.GgActivityQrcodeGenerationBinding;
import my.zjh.model_guiguiapi.util.DialogFragmentUtils;
import my.zjh.model_guiguiapi.util.LoadingDialog;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;
import my.zjh.model_guiguiapi.view.qrcodegeneration.viewmodel.QRCodeGenerationViewModel;

/**
 * 二维码生成
 *
 * @author AHeng
 * @date 2025/06/08 03:34
 */
@Route(path = "/guigui/QRCodeGenerationActivity")
public class QRCodeGenerationActivity
        extends GuiBaseActivity<GgActivityQrcodeGenerationBinding, QRCodeGenerationViewModel> {
    
    @Autowired
    ApiItemBean apiItemBean;
    
    private static final String LOADING_DIALOG_TAG = "loading_dialog_tag";
    private static final int REQUEST_STORAGE_PERMISSION = 1001;
    
    @Override
    protected CharSequence setTitle() {
        return apiItemBean.getTitle();
    }
    
    @Override
    protected void initView() {
        setupDropdownMenus();
        setupClickListeners();
        observeViewModel();
    }
    
    /**
     * 设置下拉菜单
     */
    private void setupDropdownMenus() {
        // 设置错误纠正级别下拉菜单
        String[] errorLevels = getResources().getStringArray(R.array.error_correction_levels);
        ArrayAdapter<String> errorAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_dropdown_item_1line, errorLevels);
        binding.actErrorCorrection.setAdapter(errorAdapter);
    }
    
    /**
     * 设置点击监听器
     */
    private void setupClickListeners() {
        // 生成二维码按钮点击事件
        binding.btnGenerate.setOnClickListener(v -> generateQRCode());
        
        // 保存按钮点击事件
        binding.btnSave.setOnClickListener(v -> saveQRCode());
        
        // 分享按钮点击事件
        binding.btnShare.setOnClickListener(v -> shareQRCode());
    }
    
    /**
     * 观察ViewModel的状态变化
     */
    private void observeViewModel() {
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoadingDialog();
            } else {
                dismissLoadingDialog();
            }
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 观察状态信息
        viewModel.getStatusMessage().observe(this, statusMessage -> {
            if (statusMessage != null) {
                binding.cardStatus.setVisibility(View.VISIBLE);
                binding.tvStatus.setText(statusMessage);
                binding.tvStatus.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察二维码卡片显示状态
        viewModel.getShowQRCodeCard().observe(this, showCard -> {
            if (showCard) {
                // 生成成功，加载图片
                loadQRCodeImage();
                binding.cardQrcode.setVisibility(View.VISIBLE);
            } else {
                // 失败或初始状态，隐藏二维码卡片
                binding.cardQrcode.setVisibility(View.GONE);
            }
        });
        
        // 观察输入验证错误
        viewModel.getTextInputError().observe(this, error -> 
                binding.tilTextInput.setError(error));
        
        viewModel.getSizeInputError().observe(this, error -> 
                binding.tilSize.setError(error));
        
        viewModel.getFrameInputError().observe(this, error -> 
                binding.tilFrame.setError(error));
        
        // 观察权限请求
        viewModel.getNeedStoragePermission().observe(this, needPermission -> {
            if (needPermission) {
                requestStoragePermission();
            }
        });
        
        // 观察保存结果
        viewModel.getSaveResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 观察分享Intent
        viewModel.getShareIntent().observe(this, intent -> {
            if (intent != null) {
                startActivity(intent);
            }
        });
    }
    
    /**
     * 生成二维码
     */
    private void generateQRCode() {
        String text = Objects.requireNonNull(binding.etTextInput.getText()).toString().trim();
        String sizeStr = Objects.requireNonNull(binding.etSize.getText()).toString().trim();
        String frameStr = Objects.requireNonNull(binding.etFrame.getText()).toString().trim();
        String errorLevel = binding.actErrorCorrection.getText().toString();
        
        // 调用ViewModel处理业务逻辑
        viewModel.generateQRCode(text, sizeStr, frameStr, errorLevel);
    }
    
    /**
     * 保存二维码到相册
     */
    private void saveQRCode() {
        Bitmap bitmap = getBitmapFromImageView();
        viewModel.requestSaveQRCode(this, bitmap);
    }
    
    /**
     * 分享二维码到其他应用
     */
    private void shareQRCode() {
        Bitmap bitmap = getBitmapFromImageView();
        viewModel.requestShareQRCode(this, bitmap);
    }
    
    /**
     * 从ImageView获取Bitmap
     */
    private Bitmap getBitmapFromImageView() {
        Drawable drawable = binding.ivQrcode.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }
    
    /**
     * 加载二维码图片
     */
    private void loadQRCodeImage() {
        String text = Objects.requireNonNull(binding.etTextInput.getText()).toString().trim();
        String sizeStr = Objects.requireNonNull(binding.etSize.getText()).toString().trim();
        String frameStr = Objects.requireNonNull(binding.etFrame.getText()).toString().trim();
        String errorLevel = binding.actErrorCorrection.getText().toString();
        
        String imageUrl = viewModel.buildImageUrl(text, sizeStr, frameStr, errorLevel);
        
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.ivQrcode.setImageBitmap(resource);
                    }
                    
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        binding.cardQrcode.setVisibility(View.GONE);
                        binding.cardStatus.setVisibility(View.VISIBLE);
                        binding.tvStatus.setText("图片加载失败");
                        binding.tvStatus.setVisibility(View.VISIBLE);
                        Toast.makeText(QRCodeGenerationActivity.this, 
                                "图片加载失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }
    
    /**
     * 权限请求结果处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // 权限获取成功，通知ViewModel
                Bitmap bitmap = getBitmapFromImageView();
                viewModel.onPermissionGranted(this, bitmap);
            } else {
                // 权限被拒绝，通知ViewModel
                viewModel.onPermissionDenied();
            }
        }
    }
    
    /**
     * 显示加载对话框
     */
    private void showLoadingDialog() {
        LoadingDialog dialog = new LoadingDialog();
        dialog.setCanCancel(false);
        DialogFragmentUtils.showDialogFragment(this, dialog, LOADING_DIALOG_TAG);
    }
    
    /**
     * 隐藏加载对话框
     */
    private void dismissLoadingDialog() {
        DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
    }
}