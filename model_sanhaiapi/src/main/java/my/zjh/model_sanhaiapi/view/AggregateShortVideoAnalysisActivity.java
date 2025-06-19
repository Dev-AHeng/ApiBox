package my.zjh.model_sanhaiapi.view;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.logger.Logger;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.api.VideoParseService;
import my.zjh.model_sanhaiapi.databinding.ShActivityAggregateShortVideoAnalysisBinding;
import my.zjh.model_sanhaiapi.dialog.LoadingDialogFragment;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.VideoResponse;

/**
 * 聚合短视频解析 v1.0
 *
 * @author AHeng
 * @date 2025/03/25 01:43
 */
@Route(path = "/sanhai/AggregateShortVideoAnalysisActivity")
public class AggregateShortVideoAnalysisActivity extends BaseActivity {
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Autowired
    ApiItem apiItem;
    private ShActivityAggregateShortVideoAnalysisBinding binding;
    private ExoPlayer player;
    private VideoParseService videoParseService;
    private String currentVideoUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityAggregateShortVideoAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化API服务
        videoParseService = RetrofitClient.getInstance().getVideoParseService();
        
        // 初始化播放器
        initPlayer();
        
        // 设置点击事件
        setupClickListeners();
    }
    
    @Override
    protected void onDestroy() {
        // 释放播放器资源
        if (player != null) {
            player.release();
            player = null;
        }
        
        // 取消所有网络请求
        disposables.clear();
        
        super.onDestroy();
    }
    
    /**
     * 初始化ExoPlayer
     */
    @OptIn(markerClass = UnstableApi.class)
    private void initPlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);
        binding.playerView.setControllerAutoShow(true);
        
        // 设置播放控制按钮
        binding.playerView.setShowNextButton(false);
        binding.playerView.setShowPreviousButton(false);
    }
    
    /**
     * 设置点击事件监听
     */
    private void setupClickListeners() {
        // 粘贴按钮
        binding.btnPaste.setOnClickListener(v -> {
            pasteFromClipboard(binding.etVideoUrl);
        });
        
        // 解析按钮
        binding.btnParse.setOnClickListener(v -> {
            String videoUrl = Objects.requireNonNull(binding.etVideoUrl.getText()).toString().trim();
            if (videoUrl.isEmpty()) {
                toast("请输入或粘贴视频链接");
                return;
            }
            parseVideo(videoUrl);
        });
        
        // 复制链接按钮
        binding.btnCopyUrl.setOnClickListener(v -> {
            if (currentVideoUrl != null && !currentVideoUrl.isEmpty()) {
                copyToClipboard(currentVideoUrl);
                toast("已复制视频链接");
            }
        });
        
        // 下载按钮
        binding.btnDownload.setOnClickListener(v -> {
            if (currentVideoUrl != null && !currentVideoUrl.isEmpty()) {
                // 这里可以实现下载逻辑，暂时只提示
                toast("视频下载功能即将上线");
            }
        });
    }
    
    /**
     * 解析视频
     *
     * @param videoUrl 视频链接
     */
    private void parseVideo(String videoUrl) {
        showLoading(true);
        
        disposables.add(videoParseService.parseVideo(videoUrl)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::handleSuccess, this::handleError));
    }
    
    /**
     * 处理解析成功
     *
     * @param response 解析响应
     */
    private void handleSuccess(VideoResponse response) {
        showLoading(false);
        
        if (response.isSuccess() && response.getData() != null) {
            VideoResponse.VideoData videoData = response.getData();
            
            // 显示结果卡片
            binding.cardResult.setVisibility(View.VISIBLE);
            
            // 设置视频标题
            binding.tvVideoTitle.setText(videoData.getTitle());
            
            // 保存视频URL
            currentVideoUrl = videoData.getPlayUrl();
            
            // 播放视频
            playVideo(videoData.getPlayUrl());
            
        } else {
            toast(response.getMessage() != null ? response.getMessage() : "解析失败");
            binding.cardResult.setVisibility(View.GONE);
        }
    }
    
    /**
     * 处理解析失败
     *
     * @param throwable 异常
     */
    private void handleError(Throwable throwable) {
        showLoading(false);
        Logger.e("视频解析失败: " + throwable.getMessage());
        toast("解析失败: " + throwable.getMessage());
        binding.cardResult.setVisibility(View.GONE);
    }
    
    /**
     * 使用ExoPlayer播放视频
     *
     * @param videoUrl 视频URL
     */
    private void playVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return;
        }
        
        // 创建媒体项
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        
        // 设置媒体项并准备播放
        player.setMediaItem(mediaItem);
        player.prepare();
        // 不自动播放
        // player.play();
    }
    
    /**
     * 显示或隐藏加载中
     *
     * @param isLoading 是否加载中
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            LoadingDialogFragment.showLoading(getSupportFragmentManager(), "正在解析视频...");
        } else {
            LoadingDialogFragment.dismiss(getSupportFragmentManager());
        }
    }
}