package my.zjh.model_guiguiapi.view.bingcoversby7days.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.hjq.toast.Toaster;

import java.util.List;

import my.zjh.model_guiguiapi.databinding.GgActivityBingCoversBy7DaysBinding;
import my.zjh.model_guiguiapi.util.DialogFragmentUtils;
import my.zjh.model_guiguiapi.util.LoadingDialog;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.bingcoversby7days.adapter.BingImageAdapter;
import my.zjh.model_guiguiapi.view.bingcoversby7days.api.BingCoversBy7DaysApi;
import my.zjh.model_guiguiapi.view.bingcoversby7days.viewmodel.BingCoversBy7DaysViewModel;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;

/**
 * 必应近七天封面图片
 *
 * @author AHeng
 * @date 2025/06/09 05:17
 */
@Route(path = "/guigui/BingCoversBy7DaysActivity")
public class BingCoversBy7DaysActivity
        extends GuiBaseActivity<GgActivityBingCoversBy7DaysBinding, BingCoversBy7DaysViewModel> {
    
    private static final String LOADING_DIALOG_TAG = "bing_loading_dialog_tag";
    private BingCoversBy7DaysViewModel viewModel;
    private BingImageAdapter imageAdapter;
    
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
        setupRecyclerView();
        observeLiveData();
    }
    
    /**
     * 初始化ViewModel
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(BingCoversBy7DaysViewModel.class);
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        
        // 设置获取图片按钮点击事件
        binding.btnGetImages.setOnClickListener(v -> performGetImages());
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        imageAdapter = new BingImageAdapter(this);
        binding.recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewImages.setAdapter(imageAdapter);
        
        // 设置item点击事件
        imageAdapter.setOnItemClickListener(new BingImageAdapter.OnItemClickListener() {
            @Override
            public void onViewImageClick(BingCoversBy7DaysApi.BingImageData imageData) {
                handleViewImage(imageData);
            }
            
            @Override
            public void onDownloadClick(BingCoversBy7DaysApi.BingImageData imageData) {
                handleDownloadImage(imageData);
            }
        });
    }
    
    /**
     * 观察LiveData
     */
    private void observeLiveData() {
        
        // 观察图片列表数据
        viewModel.getImageListLiveData().observe(this, this::handleImageList);
        
        // 观察错误信息
        viewModel.getErrorLiveData().observe(this, this::handleError);
        
        // 观察加载状态
        viewModel.getLoadingLiveData().observe(this, this::handleLoadingState);
    }
    
    /**
     * 执行获取图片操作
     */
    private void performGetImages() {
        
        // 清除之前的错误
        viewModel.clearError();
        
        // 执行获取图片
        viewModel.getBingCoversBy7Days();
    }
    
    /**
     * 处理图片列表数据
     *
     * @param imageList 图片列表
     */
    private void handleImageList(List<BingCoversBy7DaysApi.BingImageData> imageList) {
        if (imageList != null && !imageList.isEmpty()) {
            
            // 显示图片列表卡片
            binding.cvImageList.setVisibility(View.VISIBLE);
            
            // 隐藏状态卡片
            binding.cvStatus.setVisibility(View.GONE);
            
            // 更新图片数量显示
            binding.tvImageCount.setText("共" + imageList.size() + "张图片");
            
            // 更新RecyclerView数据
            imageAdapter.updateData(imageList);
            
            Toaster.showShort("获取成功，共" + imageList.size() + "张图片");
        }
    }
    
    /**
     * 处理错误信息
     *
     * @param error 错误信息
     */
    private void handleError(String error) {
        if (!TextUtils.isEmpty(error)) {
            
            // 隐藏图片列表卡片
            binding.cvImageList.setVisibility(View.GONE);
            
            // 显示错误提示
            binding.cvStatus.setVisibility(View.VISIBLE);
            binding.tvErrorMessage.setVisibility(View.VISIBLE);
            binding.tvErrorMessage.setText(error);
            
            Toaster.showShort(error);
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
                
                // 禁用获取按钮
                binding.btnGetImages.setEnabled(false);
                binding.btnGetImages.setText("获取中...");
                
            } else {
                
                // 隐藏加载对话框
                DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
                
                // 启用获取按钮
                binding.btnGetImages.setEnabled(true);
                binding.btnGetImages.setText("获取近7天封面图片");
            }
        }
    }
    
    /**
     * 处理查看大图
     *
     * @param imageData 图片数据
     */
    private void handleViewImage(BingCoversBy7DaysApi.BingImageData imageData) {
        if (imageData == null) {
            Toaster.showShort("图片数据为空");
            return;
        }
        
        try {
            
            // 使用内置图片查看器
            ImageViewerActivity.start(this, imageData, "必应每日图片");
            
        } catch (Exception e) {
            Toaster.showShort("无法打开图片：" + e.getMessage());
        }
    }
    
    /**
     * 处理下载图片
     *
     * @param imageData 图片数据
     */
    private void handleDownloadImage(BingCoversBy7DaysApi.BingImageData imageData) {
        if (imageData == null) {
            Toaster.showShort("图片数据为空");
            return;
        }
        
        String downloadUrl = viewModel.getDownloadImageUrl(imageData);
        if (TextUtils.isEmpty(downloadUrl)) {
            Toaster.showShort("下载链接为空");
            return;
        }
        
        try {
            
            // 使用浏览器打开下载链接
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(downloadUrl));
            startActivity(intent);
            
            Toaster.showShort("正在打开下载链接...");
            
        } catch (Exception e) {
            Toaster.showShort("无法打开下载链接：" + e.getMessage());
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 确保对话框被关闭
        DialogFragmentUtils.dismissDialogFragment(this, LOADING_DIALOG_TAG);
    }
}
