package my.zjh.model_guiguiapi.view.douyinvideoanalysis.activity;

import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.hjq.toast.Toaster;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.Objects;

import my.zjh.common.ImageLoader;
import my.zjh.gsyvideoplayer.SampleControlVideo;
import my.zjh.model_guiguiapi.view.douyinvideoanalysis.api.DouYinVideoAnalysisApi;
import my.zjh.model_guiguiapi.databinding.GgActivityDouYinVideoAnalysisBinding;
import my.zjh.model_guiguiapi.util.DialogFragmentUtils;
import my.zjh.model_guiguiapi.util.LoadingDialog;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.douyinvideoanalysis.viewmodel.DouYinVideoAnalysisViewModel;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

/**
 * 抖音短视频解析(去水印等)
 *
 * @author AHeng
 * @date 2025/05/02 07:06
 */
@Route(path = "/guigui/DouYinVideoAnalysisActivity")
public class DouYinVideoAnalysisActivity
        extends GuiBaseActivity<GgActivityDouYinVideoAnalysisBinding, DouYinVideoAnalysisViewModel> {
    private static final String TAG = "DouYinVideoAnalysisActivity";
    @Autowired
    ApiItemBean apiItemBean;
    private SampleControlVideo detailPlayer;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;
    private ImageView imageView;
    
    @Override
    protected CharSequence setTitle() {
        return apiItemBean.getTitle();
    }
    
    @Override
    protected void initView() {
        detailPlayer = binding.SampleControlVideo;
        
        // 设置点击事件
        binding.analyzeButton.setOnClickListener(view -> {
            view.setEnabled(false);
            
            viewModel.analysis(Objects.requireNonNull(binding.videoUrlInput.getText()).toString().trim());
        });
        
        // 观察解析数据
        viewModel.getData().observe(this, this::result);
        
        // 观察错误信息
        viewModel.getError().observe(this, this::error);
        
        // 观察请求开始
        viewModel.getStartReq().observe(this, aBoolean -> {
            // 每次需要显示时创建新的对话框实例
            LoadingDialog dialog = new LoadingDialog();
            dialog.setCanCancel(false);
            DialogFragmentUtils.showDialogFragment(this, dialog, "loading_dialog_tag");
        });
        
        // 观察请求完成
        viewModel.getCompleteReq().observe(this, aBoolean -> {
            DialogFragmentUtils.dismissDialogFragment(this, "loading_dialog_tag");
            binding.analyzeButton.setEnabled(true);
        });
        
        // EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        
        // exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        
        // 增加封面
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        // 隐藏返回按钮
        detailPlayer.getBackButton().setVisibility(View.GONE);
        
        // 外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        // 初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        // 设置OrientationUtils
        detailPlayer.setLand(orientationUtils);
        
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                // 防止错位设置
                .setPlayTag(TAG)
                // 是否支持触摸调节播放进度、音量等
                .setIsTouchWiget(true)
                // 是否开启自动旋转
                .setRotateViewAuto(false)
                // 是否锁定为横屏
                .setLockLand(false)
                // 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
                .setAutoFullWithSize(false)
                // 是否开启全屏动画
                .setShowFullAnimation(false)
                // 是否开启全屏动画
                .setNeedLockFull(true)
                // 设置播放视频的URL
                // .setUrl(url)
                // 设置触摸显示控制ui的消失时间, 毫秒，默认2500
                // .setDismissControlTime(5000)
                // 是否边播边缓存
                .setCacheWithPlay(true)
                // 设置视频标题
                // .setVideoTitle(apiItemBean.getTitle())
                // 全屏时不需要旋转
                .setNeedOrientationUtils(false)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        // 开始播放了才能旋转和全屏
                        orientationUtils.setEnable(detailPlayer.isRotateWithSystem());
                        isPlay = true;
                    }
                    
                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener((view, lock) -> {
                    if (orientationUtils != null) {
                        // 配合下方的onConfigurationChanged
                        orientationUtils.setEnable(!lock);
                    }
                }).build(detailPlayer);
        
    }
    
    private void error(Throwable throwable) {
        Toaster.debugShow(throwable);
        binding.analyzeButton.setEnabled(true);
    }
    
    @Override
    public void onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }
    
    @Override
    protected void onPause() {
        detailPlayer.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }
    
    @Override
    protected void onResume() {
        detailPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }
    
    @Override
    protected void onDestroy() {
        // 确保在Activity销毁时关闭对话框
        DialogFragmentUtils.dismissDialogFragment(this, "loading_dialog_tag");
        super.onDestroy();
        if (isPlay) {
            detailPlayer.getCurrentPlayer().release();
        }
        
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        
    }
    
    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
    
    /**
     * 处理抖音视频解析结果
     *
     * @param response 抖音视频解析接口返回的数据
     */
    private void result(DouYinVideoAnalysisApi.Response response) {
        // 判空处理，防止空指针异常
        DouYinVideoAnalysisApi.Response.VideoData data = response.getData();
        if (data == null) {
            // 显示错误提示
            Toaster.debugShow("解析失败，未获取到视频数据");
            return;
        }
        
        DouYinVideoAnalysisApi.Response.MusicInfo music = data.getMusic();
        
        // 显示卡片
        binding.mianCard.setVisibility(View.VISIBLE);
        
        // 设置头像
        ImageLoader.load(binding.profileImage, data.getAvatar());
        
        // 设置昵称
        binding.username.setText(data.getAuthor());
        
        // uid
        binding.userId.setText(data.getUid());
        
        // 设置视频标题
        binding.postContent.setText(data.getTitle());
        
        // 重置detailPlayer，释放当前播放器资源并初始化为初始状态
        if (detailPlayer != null) {
            // 释放播放器资源
            detailPlayer.release();
            // 设置视频封面
            ImageLoader.load(imageView, data.getCover());
            // 设置标题
            detailPlayer.setUp(data.getUrl(), true, data.getTitle());
        }
        
        // 设置视频点赞量
        binding.likeCount.setText(data.getLike());
        
        // 发布时间
        binding.postTime.setText(data.getTime2());
        
        // 判空处理，防止music为null
        if (music != null) {
            // 音乐名称
            binding.musicTitle.setText(music.getAuthor());
            // 音乐封面头像
            ImageLoader.load(binding.musicCover, data.getAvatar());
        } else {
            // music为null时清空相关UI
            binding.musicTitle.setText("");
            binding.musicCover.setImageDrawable(null);
        }
    }
    
}