package my.zjh.model_guiguiapi.view.bingcoversby7days.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hjq.toast.Toaster;

import my.zjh.common.NullViewModel;
import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.databinding.GgActivityImageViewerBinding;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.bingcoversby7days.api.BingCoversBy7DaysApi;

/**
 * 图片查看器Activity
 * 支持图片缩放、移动、双击放大等功能
 *
 * @author AHeng
 * @date 2025/06/11 03:11
 */
public class ImageViewerActivity
        extends GuiBaseActivity<GgActivityImageViewerBinding, NullViewModel> {
    
    public static final String EXTRA_IMAGE_DATA = "extra_image_data";
    public static final String EXTRA_IMAGE_TITLE = "extra_image_title";
    
    private BingCoversBy7DaysApi.BingImageData imageData;
    private String imageTitle;
    private boolean isUiVisible = true;
    private float currentRotation = 0f;
    
    /**
     * 启动图片查看器
     *
     * @param context   上下文
     * @param imageData 图片数据
     * @param title     图片标题
     */
    public static void start(Context context, BingCoversBy7DaysApi.BingImageData imageData, String title) {
        if (context == null || imageData == null) {
            return;
        }
        
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_IMAGE_DATA, imageData);
        intent.putExtra(EXTRA_IMAGE_TITLE, title);
        context.startActivity(intent);
    }
    
    /**
     * 设置沉浸式显示
     */
    private void setupImmersiveDisplay() {
        // 设置沉浸式状态栏，保留 Toolbar 显示
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
    
    /**
     * 获取Intent传递的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            imageData = (BingCoversBy7DaysApi.BingImageData) intent.getSerializableExtra(EXTRA_IMAGE_DATA);
            imageTitle = intent.getStringExtra(EXTRA_IMAGE_TITLE);
        }
        
        if (imageData == null) {
            Toaster.showShort("图片数据为空");
            finish();
        }
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        // 设置 Toolbar
        setSupportActionBar(binding.ggToolbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // 设置描述信息
        if (!TextUtils.isEmpty(imageData.getTitle())) {
            binding.tvDescription.setText(imageData.getTitle());
            binding.tvDescription.setVisibility(View.VISIBLE);
        } else {
            binding.tvDescription.setVisibility(View.GONE);
        }
        
        // 设置PhotoView点击事件（切换UI显示/隐藏）
        binding.photoView.setOnPhotoTapListener((view, x, y) -> toggleUiVisibility());
        
        // 设置PhotoView缩放监听
        binding.photoView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            // 当缩放时，可以在这里添加额外的逻辑
        });
    }
    
    /**
     * 加载图片
     */
    private void loadImage() {
        String imageUrl = getBestQualityImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            Toaster.showShort("图片URL为空");
            finish();
            return;
        }
        
        // 显示加载进度条
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // 使用Glide加载图片
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // 加载失败
                        binding.progressBar.setVisibility(View.GONE);
                        Toaster.showShort("图片加载失败");
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 加载成功
                        binding.progressBar.setVisibility(View.GONE);
                        
                        // 更新图片信息
                        updateImageInfo(resource);
                        
                        // 恢复旋转角度
                        if (currentRotation != 0f) {
                            binding.photoView.setRotation(currentRotation);
                        }
                        
                        return false;
                    }
                })
                .into(binding.photoView);
    }
    
    /**
     * 获取最佳质量的图片URL
     */
    private String getBestQualityImageUrl() {
        if (imageData == null) {
            return null;
        }
        
        // 优先使用imageUrl
        if (!TextUtils.isEmpty(imageData.getImageUrl())) {
            return imageData.getImageUrl();
        }
        
        // 如果没有imageUrl，尝试从imageSize中获取最高质量的图片
        BingCoversBy7DaysApi.ImageSize imageSize = imageData.getImageSize();
        if (imageSize != null) {
            if (!TextUtils.isEmpty(imageSize.getUhd4K())) {
                return imageSize.getUhd4K();
            }
            if (!TextUtils.isEmpty(imageSize.getHd1920x1080())) {
                return imageSize.getHd1920x1080();
            }
            if (!TextUtils.isEmpty(imageSize.getHd1366x768())) {
                return imageSize.getHd1366x768();
            }
            if (!TextUtils.isEmpty(imageSize.getHd1024x768())) {
                return imageSize.getHd1024x768();
            }
        }
        
        return null;
    }
    
    /**
     * 更新图片信息显示
     */
    private void updateImageInfo(Drawable drawable) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            String info = width + " × " + height;
            binding.tvImageInfo.setText(info);
            binding.tvImageInfo.setVisibility(View.VISIBLE);
        } else {
            binding.tvImageInfo.setVisibility(View.GONE);
        }
    }
    
    /**
     * 旋转图片
     * 每次点击顺时针旋转90度
     */
    private void rotateImage() {
        currentRotation += 90f;
        if (currentRotation >= 360f) {
            currentRotation = 0f;
        }
        
        // 应用旋转动画
        binding.photoView.animate()
                .rotation(currentRotation)
                .setDuration(200)
                .start();
        
        // 显示旋转角度提示
        String rotationText;
        if (currentRotation == 0f) {
            rotationText = "原始角度";
        } else {
            rotationText = "旋转 " + (int) currentRotation + "°";
        }
        Toaster.showShort(rotationText);
    }
    
    /**
     * 切换UI显示/隐藏
     */
    private void toggleUiVisibility() {
        isUiVisible = !isUiVisible;
        
        if (isUiVisible) {
            // 显示UI
            binding.ggToolbarLayout.appbar.setVisibility(View.VISIBLE);
            binding.llBottomBar.setVisibility(View.VISIBLE);
        } else {
            // 隐藏UI
            binding.ggToolbarLayout.appbar.setVisibility(View.GONE);
            binding.llBottomBar.setVisibility(View.GONE);
        }
    }
    
    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gg_image_viewer_menu, menu);
        return true;
    }
    
    /**
     * 处理选项菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            // 处理返回按钮点击
            finish();
            return true;
        } else if (itemId == R.id.action_rotate) {
            // 处理旋转按钮点击
            rotateImage();
            return true;
        } else if (itemId == R.id.action_download) {
            // 处理下载按钮点击
            handleDownload();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 处理下载图片
     */
    private void handleDownload() {
        if (imageData == null) {
            Toaster.showShort("图片数据为空");
            return;
        }
        
        String downloadUrl = getDownloadImageUrl();
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
    
    /**
     * 获取下载图片的URL
     */
    private String getDownloadImageUrl() {
        if (imageData == null) {
            return null;
        }
        
        // 优先使用imageUrl
        if (!TextUtils.isEmpty(imageData.getImageUrl())) {
            return imageData.getImageUrl();
        }
        
        // 使用imageSize中的高质量图片
        BingCoversBy7DaysApi.ImageSize imageSize = imageData.getImageSize();
        if (imageSize != null) {
            if (!TextUtils.isEmpty(imageSize.getUhd4K())) {
                return imageSize.getUhd4K();
            }
            if (!TextUtils.isEmpty(imageSize.getHd1920x1080())) {
                return imageSize.getHd1920x1080();
            }
        }
        
        return getBestQualityImageUrl();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 清理Glide缓存
        if (!isDestroyed()) {
            Glide.with(this).clear(binding.photoView);
        }
    }
    
    @Override
    protected CharSequence setTitle() {
        // 获取传递的数据
        getIntentData();
        
        // 设置标题
        if (!TextUtils.isEmpty(imageTitle)) {
            return imageTitle;
        } else {
            return "图片查看";
        }
    }
    
    @Override
    protected void initView() {
        // 设置沉浸式显示
        setupImmersiveDisplay();
        
        // 初始化视图
        initViews();
        
        // 加载图片
        loadImage();
    }
} 