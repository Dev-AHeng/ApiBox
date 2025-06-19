package my.zjh.gsyvideoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.hjq.toast.Toaster;
import com.shuyu.gsyvideoplayer.render.effect.AutoFixEffect;
import com.shuyu.gsyvideoplayer.render.effect.BarrelBlurEffect;
import com.shuyu.gsyvideoplayer.render.effect.BlackAndWhiteEffect;
import com.shuyu.gsyvideoplayer.render.effect.BrightnessEffect;
import com.shuyu.gsyvideoplayer.render.effect.ContrastEffect;
import com.shuyu.gsyvideoplayer.render.effect.CrossProcessEffect;
import com.shuyu.gsyvideoplayer.render.effect.DocumentaryEffect;
import com.shuyu.gsyvideoplayer.render.effect.DuotoneEffect;
import com.shuyu.gsyvideoplayer.render.effect.FillLightEffect;
import com.shuyu.gsyvideoplayer.render.effect.GammaEffect;
import com.shuyu.gsyvideoplayer.render.effect.GaussianBlurEffect;
import com.shuyu.gsyvideoplayer.render.effect.GrainEffect;
import com.shuyu.gsyvideoplayer.render.effect.HueEffect;
import com.shuyu.gsyvideoplayer.render.effect.InvertColorsEffect;
import com.shuyu.gsyvideoplayer.render.effect.LamoishEffect;
import com.shuyu.gsyvideoplayer.render.effect.NoEffect;
import com.shuyu.gsyvideoplayer.render.effect.OverlayEffect;
import com.shuyu.gsyvideoplayer.render.effect.PosterizeEffect;
import com.shuyu.gsyvideoplayer.render.effect.SampleBlurEffect;
import com.shuyu.gsyvideoplayer.render.effect.SaturationEffect;
import com.shuyu.gsyvideoplayer.render.effect.SepiaEffect;
import com.shuyu.gsyvideoplayer.render.effect.SharpnessEffect;
import com.shuyu.gsyvideoplayer.render.effect.TemperatureEffect;
import com.shuyu.gsyvideoplayer.render.effect.TintEffect;
import com.shuyu.gsyvideoplayer.render.effect.VignetteEffect;
import com.shuyu.gsyvideoplayer.render.view.GSYVideoGLView;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import my.zjh.gsyvideoplayer.util.BrightnessUtils;
import my.zjh.gsyvideoplayer.util.CommonUtil;
import my.zjh.gsyvideoplayer.util.PixelationEffect;

/**
 * 自定义播放器
 *
 * @author AHeng
 * @date 2025/05/25 16:07
 */
public class SampleControlVideo extends StandardGSYVideoPlayer {
    
    // 日志标签
    private static final String TAG = "SampleControlVideo";
    
    // 最小和最大缩放比例
    private static final float MIN_SCALE = 0.2f;
    private static final float MAX_SCALE = 4.0f;
    
    // 有效的倍速值数组（步长0.5，区间[0.25, 4]）
    private static final float[] SPEED_VALUES = {0.25f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f};
    
    // 滑动阈值，超过这个值才切换倍速
    private static final float SLIDE_THRESHOLD = 80f;
    
    // 快进/快退优化相关变量
    // 延迟执行seekTo的时间（毫秒）
    private static final long SEEK_DELAY_MS = 400;
    
    // 每次快进/快退的步长（毫秒）
    private static final int SEEK_STEP_MS = 5000;
    
    // 菜单数组定义 - 镜像模式
    private static final int[] TRANSFORM_MENU_IDS = {R.id.menu_transform_normal, R.id.menu_transform_horizontal, R.id.menu_transform_vertical};
    
    // 菜单数组定义 - 显示比例
    private static final int[] SCALE_MENU_IDS = {R.id.menu_scale_default, R.id.menu_scale_16_9, R.id.menu_scale_4_3, R.id.menu_scale_full, R.id.menu_scale_match_full};
    
    // 菜单数组定义 - 滤镜效果
    private static final int[] FILTER_MENU_IDS = {R.id.menu_filter_none, R.id.menu_filter_pixelation, R.id.menu_filter_black_white, R.id.menu_filter_contrast,
            R.id.menu_filter_cross_process, R.id.menu_filter_documentary, R.id.menu_filter_duotone, R.id.menu_filter_fill_light,
            R.id.menu_filter_gamma, R.id.menu_filter_grain, R.id.menu_filter_hue, R.id.menu_filter_invert,
            R.id.menu_filter_lamoish, R.id.menu_filter_posterize, R.id.menu_filter_barrel_blur, R.id.menu_filter_saturation,
            R.id.menu_filter_sepia, R.id.menu_filter_sharpness, R.id.menu_filter_temperature, R.id.menu_filter_tint,
            R.id.menu_filter_vignette, R.id.menu_filter_auto_fix, R.id.menu_filter_overlay, R.id.menu_filter_simple_blur,
            R.id.menu_filter_gaussian_blur, R.id.menu_filter_brightness};
    
    // 滤镜效果对应的数值
    private static final int[] FILTER_VALUES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    
    // 菜单数组定义 - 解码模式
    private static final int[] DECODE_MENU_IDS = {R.id.menu_hardware_decode, R.id.menu_software_decode};
    
    // 菜单数组定义 - 播放速度
    private static final int[] SPEED_MENU_IDS = {R.id.menu_speed_0_25, R.id.menu_speed_0_5, R.id.menu_speed_0_75, R.id.menu_speed_1_0, R.id.menu_speed_1_25,
            R.id.menu_speed_1_5, R.id.menu_speed_1_75, R.id.menu_speed_2_0, R.id.menu_speed_2_5, R.id.menu_speed_3_0, R.id.menu_speed_4_0};
    
    // 播放速度对应的数值
    private static final float[] SPEED_MENU_VALUES = {0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f, 4.0f};
    // 处理倍速提示显示和隐藏
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    // 记住切换数据源类型
    private int mType = 0;
    // 镜像变换大小
    private int mTransformSize = 0;
    // 数据源位置
    private int mSourcePosition = 0;
    // 缩放检测器
    private ScaleGestureDetector mScaleDetector;
    // 当前缩放比例
    private float mCurrentScale = 1.0f;
    // 当前平移位置
    private float mTranslateX = 0.0f;
    private float mTranslateY = 0.0f;
    // 上一次触摸位置，用于计算平移
    private float mLastTouchX = 0.0f;
    private float mLastTouchY = 0.0f;
    // 原始播放速度
    private float mOriginalSpeed = 1.0f;
    // 是否正在长按加速
    private boolean isSpeedUp = false;
    // 是否正在进行缩放操作
    private boolean isScaling = false;
    // 是否已经执行过多点触控操作
    private boolean mHasPerformedMultiTouch = false;
    // 触摸点数量
    private int pointerCount = 0;
    // 上一次的触摸ID，用于跟踪触摸点
    private int mLastTouchId = -1;
    // 倍速提示视图
    private TextView mSpeedTipView;
    
    // 播放器图标资源
    private Drawable videoResume;
    private Drawable videoResumeCenter;
    private Drawable videoPause;
    private Drawable videoPauseCenter;
    private Drawable doubleArrowLeft;
    private Drawable doubleArrowRight;
    private Drawable landscapeScreen;
    private Drawable verticalScreen;
    
    // 旋转按钮
    private AppCompatImageButton mChangeRotate;
    // 播放控制按钮
    private AppCompatImageButton playController;
    // 中央播放控制按钮
    private AppCompatImageButton playControllerCenter;
    // 全屏按钮
    private AppCompatImageButton fullBtn;
    // 菜单按钮
    private AppCompatImageButton mMenuButton;
    
    // 长按调节的当前速度
    private float mAdjustingSpeed = 2.0f;
    
    // 用于记录上次触摸的Y坐标
    private float mLastY = 0;
    
    // 是否正在调节速度
    private boolean mIsAdjustingSpeed = false;
    
    // 当前倍速值在数组中的索引，默认1.0倍速
    private int mCurrentSpeedIndex = 4;
    
    // 累计滑动距离
    private float mAccumulatedDelta = 0f;
    
    // 当前滤镜类型，默认为无效果
    private int mFilterType = 0;
    
    // 滤镜深度参数
    private float mFilterDeep = 0.8f;
    
    // 累加的快进/快退偏移量（毫秒）
    private long mAccumulatedSeekOffset = 0;
    
    // 延迟执行的seekTo任务
    private Runnable mSeekDelayedTask = null;
    
    // 水波纹效果视图
    private View mLeftRippleView, mRightRippleView;
    
    // 后台线程执行器，用于快进快退操作
    private ExecutorService mSeekExecutor;
    
    // 当前正在执行的快进快退任务
    private Future<?> mCurrentSeekTask = null;
    private Drawable brightnessIcon;
    private AppCompatImageView ivBrightnessIcon;
    private ProgressBar brightnessDialogTv;
    private AppCompatTextView tvBrightnessValue;
    private Drawable volumeIcon;
    private AppCompatImageView ivVolumeIcon;
    private AppCompatTextView tvVolumeValue;
    
    // 锁屏
    // private boolean isSuoPing = false;
    private OrientationUtils orientationUtils;
    
    // 播放完毕布局
    private View mPlayCompleteLayout;
    // 重新播放按钮
    private View mReplayButton;
    
    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SampleControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }
    
    public SampleControlVideo(Context context) {
        super(context);
    }
    
    public SampleControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {
        super.onSurfaceSizeChanged(surface, width, height);
        resolveTransform();
    }
    
    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽 ********************/
    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == View.VISIBLE) {
            mThumbImageViewLayout.setVisibility(View.INVISIBLE);
        }
    }
    
    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mThumbImageViewLayout && visibility != View.VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }
    
    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        // 封面
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == View.VISIBLE) {
                mThumbImageViewLayout.setVisibility(View.INVISIBLE);
            }
        }
        
        resolveRotateUI();
        resolveTransform();
    }
    
    /****************************************************************************/
    
    @Override
    protected void init(Context context) {
        super.init(context);
        
        // 封面
        if (mThumbImageViewLayout != null &&
                    (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(View.VISIBLE);
        }
        
        // 初始化后台线程执行器
        mSeekExecutor = Executors.newSingleThreadExecutor();
        
        initView();
        initScaleDetector(context);
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.sample_video;
    }
    
    /**
     * 显示进度对话框
     *
     * @param deltaX            进度变化量
     * @param seekTime          当前播放时间
     * @param seekTimePosition  当前播放时间位置
     * @param totalTime         总时间
     * @param totalTimeDuration 总时间长度
     */
    @Override
    protected void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        AppCompatImageView durationImageTip = super.mProgressDialog.findViewById(R.id.duration_image_tip2);
        if (deltaX < 0 && deltaX > -100) {
            durationImageTip.setImageDrawable(doubleArrowLeft);
        } else {
            durationImageTip.setImageDrawable(doubleArrowRight);
        }
    }
    
    /**
     * 显示音量对话框
     *
     * @param deltaY        音量变化量
     * @param volumePercent 音量百分比
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        
        ivVolumeIcon = super.mVolumeDialog.findViewById(R.id.ivVolumeIcon);
        tvVolumeValue = super.mVolumeDialog.findViewById(R.id.tvVolumeValue);
        
        volumePercent = Math.max(0, Math.min(volumePercent, 100));
        if (volumePercent == 0) {
            volumeIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_volume_off_24);
        } else {
            volumeIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_volume_up_24);
        }
        
        // 控制音量范围是0-100
        ivVolumeIcon.setImageDrawable(volumeIcon);
        tvVolumeValue.setText(volumePercent + "%");
    }
    
    /**
     * 显示亮度对话框
     *
     * @param percent 亮度, 范围:[0, 255]
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void showBrightnessDialog(float percent) {
        super.showBrightnessDialog(0);
        
        // [0, 255]映射成[0, 100]
        percent = percent / 255 * 100;
        percent = percent <= 0 ? 1 : percent;
        
        ivBrightnessIcon = super.mBrightnessDialog.findViewById(R.id.ivBrightnessIcon);
        brightnessDialogTv = super.mBrightnessDialog.findViewById(R.id.progressBrightness);
        tvBrightnessValue = super.mBrightnessDialog.findViewById(R.id.tvBrightnessValue);
        
        if (percent <= 1) {
            brightnessIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_brightness_low_24);
        } else {
            brightnessIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_brightness_high_24);
        }
        
        ivBrightnessIcon.setImageDrawable(brightnessIcon);
        brightnessDialogTv.setProgress((int) percent);
        tvBrightnessValue.setText((int) percent + "%");
    }
    
    /**
     * 亮度滑动
     */
    @Override
    protected void onBrightnessSlide(float percent) {
        // 获取当前窗口的Window对象
        Window window = ((Activity) getContext()).getWindow();
        
        // 获取当前窗口亮度值（0-255）
        int currentBrightness = BrightnessUtils.getWindowBrightness(getContext(), window);
        
        // 计算新的亮度值，根据滑动百分比调整
        int newBrightness = currentBrightness + (int) (percent * (BrightnessUtils.getXiaoMiMaxBrightness() * 0.05));
        
        // 当拉到最低或最高时设置最小值或最大值
        // (-, 0)=0, [0, 255]=newBrightness, (255, +)=255
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 255) {
            newBrightness = 255;
        }
        
        // 设置窗口亮度
        BrightnessUtils.setWindowBrightness(window, newBrightness);
        
        this.showBrightnessDialog(newBrightness);
    }
    
    /**
     * 当底部控制栏隐藏时触发
     */
    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        logDebug("hideAllWidget: 设置播放控制中心为不可见" + "当前状态 = " + getCurrentState());
        // 打印当前状态信息
        if (getCurrentState() == CURRENT_STATE_PAUSE) {
            return;
        }
        playControllerCenter.setVisibility(View.INVISIBLE);
    }
    
    @Override
    protected void changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear();
        playControllerCenter.setVisibility(View.INVISIBLE);
    }
    
    @Override
    protected void changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear();
        playControllerCenter.setVisibility(View.INVISIBLE);
    }
    
    @Override
    protected void changeUiToClear() {
        super.changeUiToClear();
        // 暂停状态就跳过
        if (getCurrentState() == CURRENT_STATE_PAUSE) {
            return;
        }
        playControllerCenter.setVisibility(View.INVISIBLE);
    }
    
    @Override
    protected void changeUiToCompleteClear() {
        super.changeUiToCompleteClear();
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        // 打印当前状态信息
        logDebug("changeUiToNormal: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        // 打印当前状态信息
        logDebug("changeUiToPreparingShow: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 播放中点击触发显示底部控制栏, 顶部栏
     */
    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        // 打印当前状态信息
        logDebug("changeUiToPlayingShow: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 暂停时点击触发显示底部控制栏
     */
    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        // 打印当前状态信息
        logDebug("changeUiToPauseShow: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 进度条变化触发
     */
    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        // 打印当前状态信息
        logDebug("changeUiToPlayingBufferingShow: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        // 打印当前状态信息
        logDebug("changeUiToCompleteShow: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void changeUiToError() {
        super.changeUiToError();
        // 打印当前状态信息
        logDebug("changeUiToError: 当前状态 = " + getCurrentState());
        if (getCurrentState() != CURRENT_STATE_PLAYING) {
            playControllerCenter.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除所有回调，防止内存泄漏
        mHandler.removeCallbacksAndMessages(null);
        // 清理快进/快退相关状态
        clearSeekState();
        
        // 关闭后台线程执行器
        if (mSeekExecutor != null && !mSeekExecutor.isShutdown()) {
            mSeekExecutor.shutdown();
            mSeekExecutor = null;
        }
    }
    
    /**
     * 拖动条提示
     *
     * @return 拖动条提示布局
     */
    @Override
    protected int getProgressDialogLayoutId() {
        return R.layout.dialog_video_progress;
    }
    
    @Override
    protected int getVolumeLayoutId() {
        return R.layout.dialog_video_volume;
    }
    
    @Override
    protected int getVolumeProgressId() {
        return R.id.progressVolume;
    }
    
    @Override
    protected int getBrightnessLayoutId() {
        return R.layout.dialog_video_brightness;
    }
    
    @Override
    protected int getBrightnessTextId() {
        return R.id.progressBrightness;
    }
    
    /**
     * 触摸事件处理
     *
     * @param v     视图
     * @param event 触摸事件
     *
     * @return 是否处理事件
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 获取触摸点数量
        pointerCount = event.getPointerCount();
        
        // 记录操作类型
        int actionMasked = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        
        // 处理各种触摸事件
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(pointerId, event);
                break;
            
            case MotionEvent.ACTION_POINTER_DOWN:
                handlePointerDown();
                // 多点触控时记录初始中心点
                if (pointerCount >= 2) {
                    mLastTouchX = (event.getX(0) + event.getX(1)) / 2;
                    mLastTouchY = (event.getY(0) + event.getY(1)) / 2;
                }
                break;
            
            case MotionEvent.ACTION_MOVE:
                if (handleActionMove(event)) {
                    return true;
                }
                break;
            
            case MotionEvent.ACTION_POINTER_UP:
                handlePointerUp(pointerId, actionIndex, event);
                break;
            
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUp();
                break;
        }
        
        // 处理缩放手势
        if (pointerCount >= 2) {
            mScaleDetector.onTouchEvent(event);
        }
        
        // 如果是缩放手势或之前执行过多点触控，阻止父类处理
        if (isScaling || mHasPerformedMultiTouch) {
            logDebug("阻止其他手势: 正在缩放=" + isScaling + ", 多点触控历史=" + mHasPerformedMultiTouch);
            return true;
        }
        
        return super.onTouch(v, event);
    }
    
    /**
     * 触摸表面抬起事件处理
     */
    @Override
    protected void touchSurfaceUp() {
        if (shouldSkipTouchEvent("触摸表面抬起")) {
            return;
        }
        super.touchSurfaceUp();
        logDebug("触摸表面抬起");
        
        // 如果正在长按加速，恢复原速度
        if (isSpeedUp) {
            restoreOriginalSpeed();
        }
    }
    
    /**
     * 双击触摸事件处理
     * 左侧25%区域：快退5秒（可累加）
     * 右侧25%区域：快进5秒（可累加）
     * 中间50%区域：播放/暂停
     * <p>
     * 累加逻辑：
     * - 每次双击：基于当前实时播放位置+偏移量
     * - 连续双击（400ms内）：累加偏移量
     * - 400ms内无新双击则执行seekTo到：当前播放位置+累加偏移量
     * - 例如：当前10秒，连续快进3次 = 当前位置 + 5秒 + 5秒 + 5秒 = 当前位置 + 15秒
     *
     * @param e 触摸事件
     */
    @Override
    protected void touchDoubleUp(MotionEvent e) {
        // 跳过触摸事件
        if (shouldSkipTouchEvent("双击")) {
            return;
        }
        
        // 特别处理缓冲状态
        if (getCurrentState() == CURRENT_STATE_PLAYING_BUFFERING_START) {
            return;
        }
        
        // 获取屏幕宽度和触摸位置
        int width = getWidth();
        float touchX = e.getX();
        
        // 2. 处理左侧区域 - 快退（可累加）
        if (touchX < width * 0.25f) {
            logDebug("双击左侧区域 - 快退 " + SEEK_STEP_MS + "ms（当前累加：" + (mAccumulatedSeekOffset - SEEK_STEP_MS) + "ms）");
            showRippleEffect(true); // 显示左侧水波纹效果
            performSeekWithAccumulation(-SEEK_STEP_MS); // 在子线程执行
            return; // 不调用父类方法，避免触发播放/暂停
        }
        
        // 3. 处理右侧区域 - 快进（可累加）
        if (touchX > width * 0.75f) {
            logDebug("双击右侧区域 - 快进 " + SEEK_STEP_MS + "ms（当前累加：" + (mAccumulatedSeekOffset + SEEK_STEP_MS) + "ms）");
            showRippleEffect(false); // 显示右侧水波纹效果
            performSeekWithAccumulation(SEEK_STEP_MS); // 在子线程执行
            return; // 不调用父类方法，避免触发播放/暂停
        }
        
        // 4. 处理中间区域 - 暂停/播放
        // 默认双击暂停或开始
        super.touchDoubleUp(e);
    }
    
    /**
     * 长按触摸事件处理
     *
     * @param e 触摸事件
     */
    @Override
    protected void touchLongPress(MotionEvent e) {
        if (shouldSkipTouchEvent("长按") || isSpeedUp) {
            return;
        }
        
        // 3. 如果不是播放状态，显示提示并返回
        if (mCurrentState != CURRENT_STATE_PLAYING) {
            toast("暂停状态下不能倍速播放");
            logDebug("暂停状态下不能倍速播放");
            return;
        }
        
        // 4. 执行父类方法
        super.touchLongPress(e);
        
        startLongPressSpeedUp(e);
    }
    
    /**
     * 分发触摸事件
     * 拦截并取消长按事件
     *
     * @param ev 触摸事件
     *
     * @return 是否消费事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 获取触摸事件类型
        int action = ev.getAction();
        
        // 在按下时取消所有可能的长按回调
        if (action == MotionEvent.ACTION_DOWN) {
            // 取消长按相关的状态
            isSpeedUp = false;
            mIsAdjustingSpeed = false;
            
            // 清理长按加速相关的处理器回调
            mHandler.removeCallbacksAndMessages(null);
            
            // 重置累计滑动距离
            mAccumulatedDelta = 0f;
            
            logDebug("dispatchTouchEvent: 取消长按回调");
        }
        
        return super.dispatchTouchEvent(ev);
    }
    
    /**
     * 当视频准备完成可以播放时调用
     */
    @Override
    public void onPrepared() {
        super.onPrepared();
        logDebug("当视频准备完成可以播放时调用");
    }
    
    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context   上下文
     * @param actionBar 是否显示状态栏
     * @param statusBar 是否显示导航栏
     *
     * @return 全屏播放器实例
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SampleControlVideo sampleVideo = (SampleControlVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        sampleVideo.mCurrentScale = mCurrentScale;
        sampleVideo.mFilterType = mFilterType;
        sampleVideo.mFilterDeep = mFilterDeep;
        sampleVideo.mTranslateX = mTranslateX;
        sampleVideo.mTranslateY = mTranslateY;
        
        // 为全屏播放器创建后台线程执行器
        if (sampleVideo.mSeekExecutor == null || sampleVideo.mSeekExecutor.isShutdown()) {
            sampleVideo.mSeekExecutor = Executors.newSingleThreadExecutor();
        }
        
        // 清理当前播放器的快进/快退状态，防止状态混乱
        clearSeekState();
        
        sampleVideo.resolveTransform();
        sampleVideo.resolveTypeUI();
        sampleVideo.resolveRotateUI();
        // 这个播放器的demo配置切换到全屏播放器
        // 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        // 比如已旋转角度之类的等等
        // 可参考super中的实现
        
        // 设置播放速度
        sampleVideo.setSpeed(getSpeed());
        sampleVideo.applyFilter(mFilterType);
        
        return sampleVideo;
    }
    
    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF           旧的全屏视图
     * @param vp             父视图组
     * @param gsyVideoPlayer 当前的GSY视频播放器实例
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SampleControlVideo sampleVideo = (SampleControlVideo) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
            mTransformSize = sampleVideo.mTransformSize;
            mCurrentScale = sampleVideo.mCurrentScale;
            mFilterType = sampleVideo.mFilterType;
            mFilterDeep = sampleVideo.mFilterDeep;
            mTranslateX = sampleVideo.mTranslateX;
            mTranslateY = sampleVideo.mTranslateY;
            
            // 确保当前播放器有可用的后台线程执行器
            if (mSeekExecutor == null || mSeekExecutor.isShutdown()) {
                mSeekExecutor = Executors.newSingleThreadExecutor();
            }
            
            // 清理全屏播放器的快进/快退状态
            sampleVideo.clearSeekState();
            
            // 清理全屏播放器的后台线程执行器
            if (sampleVideo.mSeekExecutor != null && !sampleVideo.mSeekExecutor.isShutdown()) {
                sampleVideo.mSeekExecutor.shutdown();
                sampleVideo.mSeekExecutor = null;
            }
            
            resolveTypeUI();
            applyFilter(mFilterType);
        }
    }
    
    /**
     * 视频尺寸变化回调
     * 当视频尺寸发生变化时调用
     */
    @Override
    public void onVideoSizeChanged() {
        super.onVideoSizeChanged();
        logDebug("视频尺寸变化: 宽=" + (mTextureView != null ? mTextureView.getWidth() : "未知") +
                         ", 高=" + (mTextureView != null ? mTextureView.getHeight() : "未知"));
    }
    
    /**
     * 初始化缩放检测器
     */
    private void initScaleDetector(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                mCurrentScale *= scaleFactor;
                mCurrentScale = Math.max(MIN_SCALE, Math.min(mCurrentScale, MAX_SCALE));
                applyVideoScale();
                return true;
            }
        });
    }
    
    /**
     * 判断是否应该跳过触摸事件
     */
    private boolean shouldSkipTouchEvent(String eventName) {
        // 跳过缩放和多点触控事件
        if (isScaling || mHasPerformedMultiTouch) {
            logDebug("跳过" + eventName + ": 正在缩放=" + isScaling + ", 多点触控历史=" + mHasPerformedMultiTouch);
            return true;
        }
        return false;
    }
    
    /**
     * 清理快进/快退状态
     * 取消所有相关任务并重置状态变量
     */
    private void clearSeekState() {
        // 1. 取消后台任务
        synchronized (this) {
            if (mCurrentSeekTask != null && !mCurrentSeekTask.isDone()) {
                mCurrentSeekTask.cancel(true);
                mCurrentSeekTask = null;
                logDebug("已取消后台快进快退任务");
            }
        }
        
        // 2. 取消UI任务
        if (mSeekDelayedTask != null) {
            mHandler.removeCallbacks(mSeekDelayedTask);
            mSeekDelayedTask = null;
            logDebug("已取消UI快进快退任务");
        }
        
        // 3. 重置状态变量
        mAccumulatedSeekOffset = 0;
        
        // 4. 移除Handler中的所有回调
        mHandler.removeCallbacksAndMessages(null);
        
        logDebug("快进快退状态已清理");
    }
    
    /**
     * 恢复原始速度
     */
    private void restoreOriginalSpeed() {
        setSpeed(mOriginalSpeed);
        isSpeedUp = false;
        mIsAdjustingSpeed = false;
        mAccumulatedDelta = 0f;
        hideSpeedTip();
        logDebug("恢复原速度: " + mOriginalSpeed);
    }
    
    /**
     * 开始长按加速
     */
    private void startLongPressSpeedUp(MotionEvent e) {
        mOriginalSpeed = getSpeed();
        
        // 长按时默认设置为2.0倍速（索引4）
        mCurrentSpeedIndex = 4; // 2.0f 在 SPEED_VALUES 数组中的索引
        mAdjustingSpeed = SPEED_VALUES[mCurrentSpeedIndex]; // 2.0f
        
        setSpeed(mAdjustingSpeed);
        isSpeedUp = true;
        mLastY = e.getY(); // 记录长按时的初始Y坐标
        mAccumulatedDelta = 0f; // 重置累计距离
        showSpeedTip(mAdjustingSpeed);
    }
    
    /**
     * 处理ACTION_DOWN事件
     */
    private void handleActionDown(int pointerId, MotionEvent event) {
        if (!mHasPerformedMultiTouch) {
            mLastTouchId = pointerId;
            mLastY = event.getY();
            mLastTouchX = event.getX();
            mLastTouchY = event.getY();
            mIsAdjustingSpeed = false;
        }
        // 如果之前有过多点触控，则阻止本次所有手势
        // 不需要额外处理
    }
    
    /**
     * 处理POINTER_DOWN事件
     */
    private void handlePointerDown() {
        if (pointerCount >= 2) {
            isScaling = true;
            mHasPerformedMultiTouch = true;
            logDebug("多点触控开始: 点数=" + pointerCount);
        }
    }
    
    /**
     * 处理ACTION_MOVE事件
     *
     * @return 是否已处理事件
     */
    private boolean handleActionMove(MotionEvent event) {
        if (isScaling) {
            // 在缩放的同时处理平移
            if (pointerCount >= 2) {
                // 使用两指的中心点移动
                float x = (event.getX(0) + event.getX(1)) / 2;
                float y = (event.getY(0) + event.getY(1)) / 2;
                
                // 计算位移
                float dx = x - mLastTouchX;
                float dy = y - mLastTouchY;
                
                // 根据缩放比例调整移动灵敏度
                // 缩放比例越大，移动灵敏度越低
                float sensitivityFactor = 1.0f / mCurrentScale;
                
                // 确保灵敏度在合理范围内
                sensitivityFactor = Math.max(0.1f, Math.min(sensitivityFactor, 2.0f));
                
                // 应用灵敏度因子
                dx *= sensitivityFactor;
                dy *= sensitivityFactor;
                
                // 更新平移位置
                mTranslateX += dx;
                mTranslateY += dy;
                
                // 更新上次位置
                mLastTouchX = x;
                mLastTouchY = y;
                
                // 应用变换
                applyVideoScale();
            }
            
            mScaleDetector.onTouchEvent(event);
            return true;
        } else if (mHasPerformedMultiTouch) {
            return true;
        } else if (isSpeedUp) {
            return handleSpeedAdjustment(event);
        }
        return false;
    }
    
    /**
     * 处理倍速调节
     */
    private boolean handleSpeedAdjustment(MotionEvent event) {
        float currentY = event.getY();
        float deltaY = mLastY - currentY;
        
        if (Math.abs(deltaY) > 8 || mIsAdjustingSpeed) {
            mIsAdjustingSpeed = true;
            
            // 累计滑动距离
            mAccumulatedDelta += deltaY;
            
            // 检查是否达到切换阈值
            if (Math.abs(mAccumulatedDelta) >= SLIDE_THRESHOLD) {
                if (mAccumulatedDelta > 0) {
                    // 向上滑动，增加速度
                    if (mCurrentSpeedIndex < SPEED_VALUES.length - 1) {
                        mCurrentSpeedIndex++;
                    }
                } else {
                    // 向下滑动，减少速度
                    if (mCurrentSpeedIndex > 0) {
                        mCurrentSpeedIndex--;
                    }
                }
                
                // 应用新的倍速
                mAdjustingSpeed = SPEED_VALUES[mCurrentSpeedIndex];
                setSpeed(mAdjustingSpeed);
                showSpeedTip(mAdjustingSpeed); // 重新显示提示并开始2秒倒计时
                
                // 重置累计距离
                mAccumulatedDelta = 0f;
            }
            
            mLastY = currentY;
            return true;
        }
        return false;
    }
    
    /**
     * 处理POINTER_UP事件
     */
    private void handlePointerUp(int pointerId, int actionIndex, MotionEvent event) {
        if (pointerCount - 1 >= 1) {
            logDebug("一个手指抬起: 继续阻止其他手势");
            // 如果抬起的是当前跟踪的触摸点，更新为其他触摸点
            if (pointerId == mLastTouchId && pointerCount > 1) {
                for (int i = 0; i < pointerCount; i++) {
                    if (i != actionIndex) {
                        mLastTouchId = event.getPointerId(i);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * 处理ACTION_UP事件
     */
    private void handleActionUp() {
        // 处理长按抬起恢复速度
        if (isSpeedUp) {
            restoreOriginalSpeed();
        }
        
        // 重置缩放标志，但保留多点触控历史标记
        isScaling = false;
        
        // 如果是最后一个抬起的手指，那么下次触摸时可以重新开始手势
        if (pointerCount <= 1) {
            mHasPerformedMultiTouch = false;
            mLastTouchId = -1;
            logDebug("所有手指抬起: 重置手势状态");
        }
    }
    
    /**
     * 应用滤镜效果
     */
    public void applyFilter(int type) {
        mFilterType = type;
        GSYVideoGLView.ShaderInterface effect = new NoEffect();
        String effectName = "无效果";
        
        // 根据当前type值选择不同的滤镜效果
        switch (type) {
            case 0:  // 无效果
                effect = new NoEffect();
                effectName = "无效果";
                break;
            case 1:  // 像素化效果
                effect = new PixelationEffect();
                effectName = "像素化效果";
                break;
            case 2:  // 黑白效果
                effect = new BlackAndWhiteEffect();
                effectName = "黑白效果";
                break;
            case 3:  // 对比度效果
                effect = new ContrastEffect(mFilterDeep);
                effectName = "对比度效果";
                break;
            case 4:  // 交叉处理效果
                effect = new CrossProcessEffect();
                effectName = "交叉处理效果";
                break;
            case 5:  // 纪录片效果
                effect = new DocumentaryEffect();
                effectName = "纪录片效果";
                break;
            case 6:  // 双色调效果(蓝黄色)
                effect = new DuotoneEffect(Color.BLUE, Color.YELLOW);
                effectName = "双色调效果";
                break;
            case 7:  // 补光效果
                effect = new FillLightEffect(mFilterDeep);
                effectName = "补光效果";
                break;
            case 8:  // 伽马校正效果
                effect = new GammaEffect(mFilterDeep);
                effectName = "伽马校正效果";
                break;
            case 10: // 颗粒效果
                effect = new GrainEffect(mFilterDeep);
                effectName = "颗粒效果";
                break;
            case 11: // 色调效果
                effect = new HueEffect(mFilterDeep);
                effectName = "色调效果";
                break;
            case 12: // 反色效果
                effect = new InvertColorsEffect();
                effectName = "反色效果";
                break;
            case 13: // 老旧照片效果
                effect = new LamoishEffect();
                effectName = "老旧照片效果";
                break;
            case 14: // 海报化效果
                effect = new PosterizeEffect();
                effectName = "海报化效果";
                break;
            case 15: // 桶形模糊效果
                effect = new BarrelBlurEffect();
                effectName = "桶形模糊效果";
                break;
            case 16: // 饱和度效果
                effect = new SaturationEffect(mFilterDeep);
                effectName = "饱和度效果";
                break;
            case 17: // 怀旧(棕褐色)效果
                effect = new SepiaEffect();
                effectName = "怀旧效果";
                break;
            case 18: // 锐化效果
                effect = new SharpnessEffect(mFilterDeep);
                effectName = "锐化效果";
                break;
            case 19: // 色温效果
                effect = new TemperatureEffect(mFilterDeep);
                effectName = "色温效果";
                break;
            case 20: // 色调效果(绿色)
                effect = new TintEffect(Color.GREEN);
                effectName = "色调效果(绿色)";
                break;
            case 21: // 暗角效果
                effect = new VignetteEffect(mFilterDeep);
                effectName = "暗角效果";
                break;
            case 22: // 自动修复效果
                effect = new AutoFixEffect(mFilterDeep);
                effectName = "自动修复效果";
                break;
            case 23: // 叠加效果
                effect = new OverlayEffect();
                effectName = "叠加效果";
                break;
            case 24: // 简单模糊效果
                effect = new SampleBlurEffect(4.0f);
                effectName = "简单模糊效果";
                break;
            case 25: // 高斯模糊效果
                effect = new GaussianBlurEffect(6.0f, GaussianBlurEffect.TYPEXY);
                effectName = "高斯模糊效果";
                break;
            case 26: // 亮度效果
                effect = new BrightnessEffect(mFilterDeep);
                effectName = "亮度效果";
                break;
        }
        
        setEffectFilter(effect);
        
        // toast(effectName);
    }
    
    /**
     * 解码模式设置
     *
     * @param is true为硬解码，false为软解码
     */
    public void setDeCode(boolean is) {
        if (is) {
            // 硬解码
            GSYVideoType.enableMediaCodec();
            GSYVideoType.enableMediaCodecTexture();
            this.changeTextureViewShowType();
        } else {
            // 软解码
            GSYVideoType.disableMediaCodec();
            GSYVideoType.disableMediaCodecTexture();
            this.changeTextureViewShowType();
        }
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object... content) {
        Toaster.show(TextUtils.join(", ", content));
    }
    
    /**
     * 日志输出统一方法
     */
    private void logDebug(String message) {
        Log.i(TAG, message);
    }
    
    /**
     * 视频截图
     */
    public void shotImage() {
        // 获取截图
        taskShotPic(bitmap -> {
            if (bitmap != null) {
                try {
                    CommonUtil.saveBitmap(getContext(), bitmap);
                } catch (FileNotFoundException e) {
                    toast("保存失败");
                    e.printStackTrace();
                    return;
                }
                toast("保存成功");
            } else {
                toast("截图失败");
            }
        });
    }
    
    /**
     * 横竖屏(一定设置)
     *
     * @param orientationUtils orientationUtils
     */
    public void setLand(OrientationUtils orientationUtils) {
        this.orientationUtils = orientationUtils;
    }
    
    private void initView() {
        // 拖动进度条图标
        doubleArrowLeft = ContextCompat.getDrawable(getContext(), R.drawable.baseline_keyboard_double_arrow_left_24);
        doubleArrowRight = ContextCompat.getDrawable(getContext(), R.drawable.baseline_keyboard_double_arrow_right_24);
        
        // 播放器中间的播放/暂停icon
        videoResumeCenter = ContextCompat.getDrawable(getContext(), R.drawable.baseline_pause_64);
        videoPauseCenter = ContextCompat.getDrawable(getContext(), R.drawable.baseline_play_arrow_64);
        
        // 播放器底部的播放/暂停icon
        videoResume = ContextCompat.getDrawable(getContext(), R.drawable.baseline_pause_24);
        videoPause = ContextCompat.getDrawable(getContext(), R.drawable.baseline_play_arrow_24);
        
        // 播放器底部的横竖屏icon
        landscapeScreen = ContextCompat.getDrawable(getContext(), R.drawable.baseline_fit_screen_h_24);
        verticalScreen = ContextCompat.getDrawable(getContext(), R.drawable.baseline_fit_screen_v_24);
        
        // 横竖屏切换
        fullBtn = findViewById(R.id.fullbtn);
        fullBtn.setImageDrawable(isIfCurrentIsFullscreen() ? landscapeScreen : verticalScreen);
        fullBtn.setOnClickListener(view -> {
            if (isIfCurrentIsFullscreen()) {
                getBackButton().callOnClick();
            } else {
                // 直接横屏
                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                // 会横屏
                // orientationUtils.resolveByClick();
                // 第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                startWindowFullscreen(getContext(), true, true);
            }
        });
        
        // 底部控制器
        playController = findViewById(R.id.play_controller);
        playController.setOnClickListener(view -> clickStartIcon());
        
        // 倍数提示
        mSpeedTipView = findViewById(R.id.speed_tip_view);
        
        // 水波纹效果视图
        mLeftRippleView = findViewById(R.id.left_ripple_view);
        mRightRippleView = findViewById(R.id.right_ripple_view);
        
        // 设置水波纹背景
        mLeftRippleView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_effect_right));
        mRightRippleView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_effect));
        
        // 启动标题跑马灯效果
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setSelected(true);
        
        // 菜单按钮
        mMenuButton = findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(v -> {
            if (!mHadPlay) {
                return;
            }
            showPopupMenu(v);
        });
        
        // 顺时针旋转播放角度
        mChangeRotate = findViewById(R.id.rotate_90);
        mChangeRotate.setOnClickListener(v -> {
            if (!mHadPlay) {
                return;
            }
            if ((mTextureView.getRotation() - mRotate) == 270) {
                mTextureView.setRotation(mRotate);
                mTextureView.requestLayout();
            } else {
                mTextureView.setRotation(mTextureView.getRotation() + 90);
                mTextureView.requestLayout();
            }
        });
        
        // 播放器中间控制器
        playControllerCenter = findViewById(R.id.play_controller_center);
        playControllerCenter.setOnClickListener(view -> clickStartIcon());
        
        
        // 初始化播放完毕布局
        mPlayCompleteLayout = findViewById(R.id.layout_play_complete);
        // 初始化重新播放按钮
        mReplayButton = findViewById(R.id.btn_replay);
        
        // 设置重新播放按钮点击事件
        mReplayButton.setOnClickListener(v -> {
            // 隐藏播放完毕布局
            mPlayCompleteLayout.setVisibility(View.GONE);
            // 重新开始播放
            startPlayLogic();
        });
        
        // 播放状态回调
        setGSYStateUiListener(i -> {
            if (i == CURRENT_STATE_NORMAL) {
                // 隐藏播放完成布局
                mPlayCompleteLayout.setVisibility(View.GONE);
                // 隐藏菜单按钮
                mMenuButton.setVisibility(View.GONE);
                // 播放控制器中心可见
                playControllerCenter.setVisibility(View.VISIBLE);
                // 播放控制器中心图标设置为播放图标
                playControllerCenter.setImageDrawable(videoPauseCenter);
                // 播放控制器图标设置为播放图标
                playController.setImageDrawable(videoPause);
            }
            
            // 开始播放, 点击开始icon触发, 只触发第一次
            if (i == CURRENT_STATE_PREPAREING) {
                if (GSYVideoType.getRenderType() == GSYVideoType.GLSURFACE) {
                    mChangeRotate.setVisibility(View.GONE);
                }
            }
            
            // 播放
            if (i == CURRENT_STATE_PLAYING) {
                // 隐藏播放完成布局
                mPlayCompleteLayout.setVisibility(View.GONE);
                // 重新播放按钮可见
                mMenuButton.setVisibility(View.VISIBLE);
                // 播放控制器中心不可见
                playControllerCenter.setVisibility(View.INVISIBLE);
                // 播放控制器中心图标设置为视频恢复中心图标
                playControllerCenter.setImageDrawable(videoResumeCenter);
                // 播放控制器图标设置为视频恢复图标
                playController.setImageDrawable(videoResume);
            }
            
            // 缓冲
            if (i == CURRENT_STATE_PLAYING_BUFFERING_START) {
                // 缓冲中不可以双击快进快退
                playControllerCenter.setVisibility(View.INVISIBLE);
            }
            
            // 暂停
            if (i == CURRENT_STATE_PAUSE) {
                playControllerCenter.setImageDrawable(videoPauseCenter);
                playController.setImageDrawable(videoPause);
            }
            
            // 播放完毕
            if (i == CURRENT_STATE_AUTO_COMPLETE) {
                // 隐藏播放控制器
                playControllerCenter.setVisibility(View.GONE);
                // 状态底部栏开始播放icon设为暂停
                playController.setImageDrawable(videoPause);
                // 显示播放完毕布局
                mPlayCompleteLayout.setVisibility(View.VISIBLE);
                
                // mThumbImageViewLayout.setVisibility(View.VISIBLE);
            }
            
            // 播放失败
            if (i == CURRENT_STATE_ERROR) {
                toast("播放失败");
            }
        });
    }
    
    /**
     * 显示弹出菜单
     *
     * @param view 锚点视图
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.video_player_menu, popupMenu.getMenu());
        
        setMenuItemsChecked(popupMenu);
        popupMenu.setOnMenuItemClickListener(this::handleMenuItemClick);
        popupMenu.show();
    }
    
    /**
     * 设置所有菜单项的选中状态
     */
    private void setMenuItemsChecked(PopupMenu popupMenu) {
        // GL模式, 禁用旋转镜像, 禁用重置缩放
        if (GSYVideoType.getRenderType() == GSYVideoType.GLSURFACE) {
            disableMenuItem(popupMenu, R.id.menu_transform_mode);
            disableMenuItem(popupMenu, R.id.menu_reset_scale);
        }
        
        // 非GL模式, 禁用滤镜
        if (GSYVideoType.getRenderType() != GSYVideoType.GLSURFACE) {
            disableMenuItem(popupMenu, R.id.menu_filter_mode);
        }
        
        // 设置各种菜单项的选中状态
        setMenuGroupChecked(popupMenu, TRANSFORM_MENU_IDS, mTransformSize);
        setMenuGroupChecked(popupMenu, SCALE_MENU_IDS, mType);
        setMenuGroupCheckedWithValues(popupMenu, FILTER_MENU_IDS, FILTER_VALUES, mFilterType);
        setMenuGroupChecked(popupMenu, DECODE_MENU_IDS, GSYVideoType.isMediaCodec() ? 0 : 1);
        setMenuGroupCheckedWithFloatValues(popupMenu, SPEED_MENU_IDS, SPEED_MENU_VALUES, getSpeed());
    }
    
    /**
     * 禁用菜单项
     */
    private void disableMenuItem(PopupMenu popupMenu, int menuId) {
        MenuItem item = popupMenu.getMenu().findItem(menuId);
        if (item != null) {
            item.setEnabled(false);
        }
    }
    
    /**
     * 处理菜单项点击事件
     */
    private boolean handleMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        
        // 处理各种菜单项
        return handleTransformMenu(itemId) || handleScaleMenu(itemId) ||
                       handleFilterMenu(itemId) || handleDecodeMenu(itemId) ||
                       handleSpeedMenu(itemId) || handleSpecialMenu(itemId);
    }
    
    /**
     * 处理镜像模式菜单
     */
    private boolean handleTransformMenu(int itemId) {
        for (int i = 0; i < TRANSFORM_MENU_IDS.length; i++) {
            if (itemId == TRANSFORM_MENU_IDS[i]) {
                mTransformSize = i;
                resolveTransform();
                return true;
            }
        }
        return false;
    }
    
    /**
     * 处理画面比例菜单
     */
    private boolean handleScaleMenu(int itemId) {
        for (int i = 0; i < SCALE_MENU_IDS.length; i++) {
            if (itemId == SCALE_MENU_IDS[i]) {
                mType = i;
                resolveTypeUI();
                return true;
            }
        }
        return false;
    }
    
    /**
     * 处理滤镜效果菜单
     */
    private boolean handleFilterMenu(int itemId) {
        for (int i = 0; i < FILTER_MENU_IDS.length; i++) {
            if (itemId == FILTER_MENU_IDS[i]) {
                applyFilter(FILTER_VALUES[i]);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 处理解码模式菜单
     */
    private boolean handleDecodeMenu(int itemId) {
        if (itemId == R.id.menu_hardware_decode) {
            setDeCode(true);
            toast("已切换到硬解码");
            return true;
        } else if (itemId == R.id.menu_software_decode) {
            setDeCode(false);
            toast("已切换到软解码");
            return true;
        }
        return false;
    }
    
    /**
     * 处理播放速度菜单
     */
    private boolean handleSpeedMenu(int itemId) {
        for (int i = 0; i < SPEED_MENU_IDS.length; i++) {
            if (itemId == SPEED_MENU_IDS[i]) {
                setSpeed(SPEED_MENU_VALUES[i]);
                toast("播放速度: " + SPEED_MENU_VALUES[i] + "x");
                return true;
            }
        }
        return false;
    }
    
    /**
     * 处理特殊菜单项
     */
    private boolean handleSpecialMenu(int itemId) {
        if (itemId == R.id.menu_reset_scale) {
            resetScale();
            toast("已重置缩放");
            return true;
        } else if (itemId == R.id.menu_shot_image) {
            shotImage();
            return true;
        }
        return false;
    }
    
    /**
     * 通用方法：设置菜单组中的选中状态（按索引）
     */
    private void setMenuGroupChecked(PopupMenu popupMenu, int[] menuIds, int selectedIndex) {
        // 先取消所有选中状态
        for (int menuId : menuIds) {
            MenuItem item = popupMenu.getMenu().findItem(menuId);
            if (item != null) {
                item.setChecked(false);
            }
        }
        
        // 设置当前项为选中状态
        if (selectedIndex >= 0 && selectedIndex < menuIds.length) {
            MenuItem selectedItem = popupMenu.getMenu().findItem(menuIds[selectedIndex]);
            if (selectedItem != null) {
                selectedItem.setChecked(true);
            }
        }
    }
    
    /**
     * 通用方法：设置菜单组中的选中状态（按值匹配）
     */
    private void setMenuGroupCheckedWithValues(PopupMenu popupMenu, int[] menuIds, int[] values, int currentValue) {
        // 先取消所有选中状态
        for (int menuId : menuIds) {
            MenuItem item = popupMenu.getMenu().findItem(menuId);
            if (item != null) {
                item.setChecked(false);
            }
        }
        
        // 查找匹配的值并设置为选中
        for (int i = 0; i < values.length && i < menuIds.length; i++) {
            if (values[i] == currentValue) {
                MenuItem selectedItem = popupMenu.getMenu().findItem(menuIds[i]);
                if (selectedItem != null) {
                    selectedItem.setChecked(true);
                }
                break;
            }
        }
    }
    
    /**
     * 通用方法：设置菜单组中的选中状态（按浮点值匹配）
     */
    private void setMenuGroupCheckedWithFloatValues(PopupMenu popupMenu, int[] menuIds, float[] values, float currentValue) {
        // 先取消所有选中状态
        for (int menuId : menuIds) {
            MenuItem item = popupMenu.getMenu().findItem(menuId);
            if (item != null) {
                item.setChecked(false);
            }
        }
        
        // 查找匹配的值并设置为选中（考虑浮点数精度）
        for (int i = 0; i < values.length && i < menuIds.length; i++) {
            if (Math.abs(values[i] - currentValue) < 0.01f) {
                MenuItem selectedItem = popupMenu.getMenu().findItem(menuIds[i]);
                if (selectedItem != null) {
                    selectedItem.setChecked(true);
                }
                break;
            }
        }
    }
    
    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
        
    }
    
    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        if (mType == 1) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }
        
        changeTextureViewShowType();
        if (mTextureView != null) {
            mTextureView.requestLayout();
        }
    }
    
    /**
     * 应用视频缩放
     */
    private void applyVideoScale() {
        if (mTextureView != null) {
            Matrix matrix = new Matrix();
            
            // 获取视图尺寸
            float viewWidth = mTextureView.getWidth();
            float viewHeight = mTextureView.getHeight();
            
            // 计算缩放后的视频尺寸
            float scaledWidth = viewWidth * mCurrentScale;
            float scaledHeight = viewHeight * mCurrentScale;
            
            // 计算可移动的最大范围
            float maxTranslateX = Math.abs(scaledWidth - viewWidth) / 2;
            float maxTranslateY = Math.abs(scaledHeight - viewHeight) / 2;
            
            // 限制平移范围，防止视频移出视野
            // 如果缩放比例小于1，限制在中心点
            if (mCurrentScale <= 1.0f) {
                mTranslateX = 0;
                mTranslateY = 0;
            } else {
                // 如果缩放比例大于1，限制在有效范围内
                mTranslateX = Math.max(-maxTranslateX, Math.min(mTranslateX, maxTranslateX));
                mTranslateY = Math.max(-maxTranslateY, Math.min(mTranslateY, maxTranslateY));
            }
            
            // 保存当前的镜像状态
            int transformType = mTransformSize;
            
            // 先应用平移
            matrix.postTranslate(mTranslateX, mTranslateY);
            
            // 再应用缩放
            float pivotX = viewWidth / 2.0f;
            float pivotY = viewHeight / 2.0f;
            matrix.postScale(mCurrentScale, mCurrentScale, pivotX, pivotY);
            
            // 最后应用镜像效果（如果有）
            switch (transformType) {
                case 1: // 左右镜像
                    matrix.postScale(-1, 1, pivotX, 0);
                    break;
                case 2: // 上下镜像
                    matrix.postScale(1, -1, 0, pivotY);
                    break;
                default:
            }
            
            mTextureView.setTransform(matrix);
            mTextureView.invalidate();
        }
    }
    
    /**
     * 重置缩放
     */
    public void resetScale() {
        mCurrentScale = 1.0f;
        mTranslateX = 0.0f;
        mTranslateY = 0.0f;
        applyVideoScale();
    }
    
    /**
     * 显示倍速提示
     */
    @SuppressLint("SetTextI18n")
    private void showSpeedTip(float speed) {
        if (mSpeedTipView != null) {
            mSpeedTipView.setText(speed + "倍速");
            mSpeedTipView.setVisibility(VISIBLE);
            
            // 2秒后自动隐藏（即使在长按状态下也隐藏）
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(() -> {
                if (mSpeedTipView != null) {
                    mSpeedTipView.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }
    
    /**
     * 隐藏倍速提示
     */
    private void hideSpeedTip() {
        if (mSpeedTipView != null) {
            mSpeedTipView.setVisibility(View.GONE);
        }
    }
    
    /**
     * 执行带累加功能的快进/快退（后台线程版本）
     * <p>
     * 工作原理：
     * 1. 每次双击累加偏移量
     * 2. 等待400ms观察期，如果期间有新的双击则重新开始
     * 3. 400ms内无新双击则执行最终的seekTo操作
     * 4. 目标位置 = 当前播放位置 + 累加偏移量
     *
     * @param offsetMs 偏移量（毫秒），正数为快进，负数为快退
     */
    private void performSeekWithAccumulation(long offsetMs) {
        // 1. 状态检查
        if (getCurrentState() == CURRENT_STATE_PLAYING_BUFFERING_START) {
            toast("缓冲中，无法快进快退");
            return;
        }
        
        // 2. 获取当前播放位置和视频信息
        long currentPlayPosition = getSafeCurrentPosition();
        long duration = getDuration();
        
        // 3. 调试信息输出
        logDebug("=== 快进快退操作 ===");
        logDebug("偏移量: " + (offsetMs / 1000) + "秒, 当前位置: " + (currentPlayPosition / 1000) + "秒");
        logDebug("累加偏移: " + (mAccumulatedSeekOffset / 1000) + "秒, 视频时长: " + (duration / 1000) + "秒");
        
        // 4. 播放位置验证
        if (currentPlayPosition == 0 && duration > 0) {
            logDebug("警告：获取到的播放位置为0！");
        }
        
        // 5. 线程执行器检查
        if (mSeekExecutor == null || mSeekExecutor.isShutdown()) {
            logDebug("使用UI线程执行快进快退");
            performSeekWithAccumulationOnUI(offsetMs);
            return;
        }
        
        // 6. 提交后台任务
        mSeekExecutor.submit(() -> executeSeekTask(offsetMs, currentPlayPosition, duration));
    }
    
    /**
     * 在后台线程中执行快进快退任务
     */
    private void executeSeekTask(long offsetMs, long currentPlayPosition, long duration) {
        try {
            // 取消之前的任务
            cancelPreviousSeekTask();
            
            // 累加偏移量和状态检查
            updateAccumulatedOffset(offsetMs, currentPlayPosition);
            
            // 记录当前的累加偏移量，用于检测新的双击
            long currentAccumulatedOffset = mAccumulatedSeekOffset;
            
            // 等待观察期
            Thread.sleep(SEEK_DELAY_MS);
            
            // 检查是否有新的双击
            if (hasNewDoubleClick(currentAccumulatedOffset)) {
                logDebug("[后台线程] 检测到新的双击，当前累加: " + mAccumulatedSeekOffset + "ms，本次: " + currentAccumulatedOffset + "ms");
                return;
            }
            
            // 执行最终的seekTo操作
            performFinalSeek(duration);
            
        } catch (InterruptedException e) {
            logDebug("[后台线程] 任务被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logDebug("[后台线程] 任务执行异常: " + e.getMessage());
            resetSeekStateOnError();
        }
    }
    
    /**
     * 取消之前的快进快退任务
     */
    private void cancelPreviousSeekTask() {
        synchronized (this) {
            if (mCurrentSeekTask != null && !mCurrentSeekTask.isDone()) {
                mCurrentSeekTask.cancel(true);
                logDebug("[后台线程] 取消之前的快进快退任务");
            }
        }
    }
    
    /**
     * 更新累加偏移量并显示反馈
     */
    private void updateAccumulatedOffset(long offsetMs, long currentPlayPosition) {
        // 如果是新序列，记录开始信息
        if (mAccumulatedSeekOffset == 0) {
            logDebug("[后台线程] 开始新序列，基准位置: " + (currentPlayPosition / 1000) + "秒");
        }
        
        // 累加偏移量
        mAccumulatedSeekOffset += offsetMs;
        long targetPosition = currentPlayPosition + mAccumulatedSeekOffset;
        
        logDebug("[后台线程] 累加后偏移: " + (mAccumulatedSeekOffset / 1000) + "秒，目标: " + (targetPosition / 1000) + "秒");
        
        // 主线程显示即时反馈
        mHandler.post(() -> showSeekFeedback(mAccumulatedSeekOffset, currentPlayPosition));
    }
    
    /**
     * 显示快进快退的即时反馈
     */
    private void showSeekFeedback(long accumulatedOffset, long currentPosition) {
        try {
            int totalSeconds = Math.abs((int) (accumulatedOffset / 1000));
            String action = accumulatedOffset > 0 ? "快进" : "快退";
            long targetPosition = currentPosition + accumulatedOffset;
            
            logDebug("[反馈] " + action + totalSeconds + "秒（" + (currentPosition / 1000) + "秒→" + (targetPosition / 1000) + "秒）");
        } catch (Exception e) {
            logDebug("[反馈] 显示异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否有新的双击
     *
     * @param previousOffset 之前记录的累加偏移量
     *
     * @return true如果有新的双击，false如果没有
     */
    private boolean hasNewDoubleClick(long previousOffset) {
        // 如果累加偏移量发生了变化，说明有新的双击
        return mAccumulatedSeekOffset != previousOffset;
    }
    
    /**
     * 执行最终的seekTo操作
     */
    private void performFinalSeek(long duration) {
        mHandler.post(() -> {
            try {
                // 再次检查播放状态
                if (getCurrentState() == CURRENT_STATE_PLAYING_BUFFERING_START) {
                    toast("缓冲中，无法执行快进快退");
                    return;
                }
                
                // 获取最新播放位置并计算目标位置
                long finalCurrentPosition = getSafeCurrentPosition();
                long finalTargetPosition = finalCurrentPosition + mAccumulatedSeekOffset;
                
                // 位置范围检查
                finalTargetPosition = Math.max(0, Math.min(finalTargetPosition, duration));
                
                logDebug("[主线程] 执行seekTo: " + (finalCurrentPosition / 1000) + "秒 → " + (finalTargetPosition / 1000) + "秒");
                
                // 执行跳转
                seekTo(finalTargetPosition);
                
                // 显示完成提示
                showSeekCompletedToast(mAccumulatedSeekOffset, finalCurrentPosition, finalTargetPosition);
                
            } catch (Exception e) {
                logDebug("[主线程] seekTo异常: " + e.getMessage());
            } finally {
                resetSeekState();
            }
        });
    }
    
    /**
     * 显示快进快退完成的提示
     */
    private void showSeekCompletedToast(long accumulatedOffset, long fromPosition, long toPosition) {
        int totalSeconds = Math.abs((int) (accumulatedOffset / 1000));
        String action = accumulatedOffset > 0 ? "快进" : "快退";
        toast(action + totalSeconds + "秒（" + (fromPosition / 1000) + "秒→" + (toPosition / 1000) + "秒）");
    }
    
    /**
     * 在错误情况下重置快进快退状态
     */
    private void resetSeekStateOnError() {
        mHandler.post(() -> {
            try {
                toast("快进快退操作失败");
                resetSeekState();
            } catch (Exception ex) {
                logDebug("[主线程] 错误处理异常: " + ex.getMessage());
            }
        });
    }
    
    /**
     * 重置快进快退状态
     */
    private void resetSeekState() {
        mAccumulatedSeekOffset = 0;
        synchronized (this) {
            mCurrentSeekTask = null;
        }
        logDebug("重置快进快退状态");
    }
    
    /**
     * UI线程版本的快进快退（备用方案）
     * 当后台线程执行器不可用时使用
     *
     * @param offsetMs 偏移量（毫秒），正数为快进，负数为快退
     */
    private void performSeekWithAccumulationOnUI(long offsetMs) {
        // 1. 获取当前播放位置
        long currentPlayPosition = getSafeCurrentPosition();
        logDebug("UI线程安全获取当前播放位置: " + (currentPlayPosition / 1000) + "秒");
        
        // 2. 处理延迟任务
        if (mSeekDelayedTask != null) {
            mHandler.removeCallbacks(mSeekDelayedTask);
            logDebug("取消之前的UI任务，重新开始400ms观察期");
        } else {
            // 新序列开始
            mAccumulatedSeekOffset = 0;
            logDebug("开始新的UI快进快退序列");
        }
        
        // 3. 累加偏移量
        mAccumulatedSeekOffset += offsetMs;
        
        // 5. 创建延迟任务
        mSeekDelayedTask = this::executeUISeekTask;
        
        // 6. 启动400ms延迟执行
        mHandler.postDelayed(mSeekDelayedTask, SEEK_DELAY_MS);
    }
    
    /**
     * 执行UI线程的快进快退任务
     */
    private void executeUISeekTask() {
        try {
            // 特别处理缓冲状态
            if (getCurrentState() == CURRENT_STATE_PLAYING_BUFFERING_START) {
                toast("缓冲中，无法执行快进快退");
                return;
            }
            
            // 重新获取当前播放位置，确保是最新的
            long finalCurrentPosition = getSafeCurrentPosition();
            long finalTargetPosition = finalCurrentPosition + mAccumulatedSeekOffset;
            
            logDebug("UI最终执行: " + (finalCurrentPosition / 1000) + "秒 → " + (finalTargetPosition / 1000) + "秒");
            
            // 确保目标位置在有效范围内
            long duration = getDuration();
            finalTargetPosition = Math.max(0, Math.min(finalTargetPosition, duration));
            
            // 执行跳转
            seekTo(finalTargetPosition);
            
            // 显示完成提示
            showSeekCompletedToast(mAccumulatedSeekOffset, finalCurrentPosition, finalTargetPosition);
            
        } catch (Exception e) {
            logDebug("UI seekTo异常: " + e.getMessage());
            toast("快进快退操作失败");
        } finally {
            // 重置状态
            mAccumulatedSeekOffset = 0;
            mSeekDelayedTask = null;
            logDebug("UI重置快进快退状态");
        }
    }
    
    /**
     * 处理镜像旋转
     */
    protected void resolveTransform() {
        // 获取视图尺寸
        float viewWidth = mTextureView.getWidth();
        float viewHeight = mTextureView.getHeight();
        
        // 计算缩放后的视频尺寸
        float scaledWidth = viewWidth * mCurrentScale;
        float scaledHeight = viewHeight * mCurrentScale;
        
        // 计算可移动的最大范围
        float maxTranslateX = Math.abs(scaledWidth - viewWidth) / 2;
        float maxTranslateY = Math.abs(scaledHeight - viewHeight) / 2;
        
        // 限制平移范围，防止视频移出视野
        // 如果缩放比例小于1，限制在中心点
        if (mCurrentScale <= 1.0f) {
            mTranslateX = 0;
            mTranslateY = 0;
        } else {
            // 如果缩放比例大于1，限制在有效范围内
            mTranslateX = Math.max(-maxTranslateX, Math.min(mTranslateX, maxTranslateX));
            mTranslateY = Math.max(-maxTranslateY, Math.min(mTranslateY, maxTranslateY));
        }
        
        // 保存当前缩放比例
        float scale = mCurrentScale;
        
        Matrix transform = new Matrix();
        // 先应用平移
        transform.postTranslate(mTranslateX, mTranslateY);
        
        float pivotX = viewWidth / 2.0f;
        float pivotY = viewHeight / 2.0f;
        
        switch (mTransformSize) {
            case 1: {
                // 左右镜像
                transform.postScale(-1 * scale, 1 * scale, pivotX, 0);
            }
            break;
            case 2: {
                // 上下镜像
                transform.postScale(1 * scale, -1 * scale, 0, pivotY);
            }
            break;
            case 0: {
                // 正常显示
                transform.postScale(1 * scale, 1 * scale, pivotX, 0);
            }
            break;
        }
        mTextureView.setTransform(transform);
        mTextureView.invalidate();
    }
    
    /**
     * 显示水波纹效果
     *
     * @param isLeft true为左侧快退，false为右侧快进
     */
    private void showRippleEffect(boolean isLeft) {
        try {
            View rippleView = isLeft ? mLeftRippleView : mRightRippleView;
            if (rippleView == null) {
                logDebug("水波纹视图为空，跳过显示");
                return;
            }
            
            // 确保在主线程中执行UI操作
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mHandler.post(() -> showRippleEffectOnUI(isLeft));
                return;
            }
            
            showRippleEffectOnUI(isLeft);
        } catch (Exception e) {
            logDebug("显示水波纹效果异常: " + e.getMessage());
        }
    }
    
    /**
     * 在主线程显示水波纹效果
     */
    private void showRippleEffectOnUI(boolean isLeft) {
        View rippleView = isLeft ? mLeftRippleView : mRightRippleView;
        // 设置可见
        rippleView.setVisibility(VISIBLE);
        
        // 根据左右侧选择不同的动画
        int animationRes = isLeft ? R.anim.ripple_show_left_to_right : R.anim.ripple_show_right_to_left;
        
        // 加载并启动显示动画
        Animation showAnimation = AnimationUtils.loadAnimation(getContext(), animationRes);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 动画开始时的处理
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // 显示动画结束后隐藏
                hideRippleEffect(isLeft);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                // 重复动画的处理
            }
        });
        rippleView.startAnimation(showAnimation);
    }
    
    /**
     * 隐藏水波纹效果
     *
     * @param isLeft true为左侧快退，false为右侧快进
     */
    private void hideRippleEffect(boolean isLeft) {
        View rippleView = isLeft ? mLeftRippleView : mRightRippleView;
        if (rippleView != null && rippleView.getVisibility() == View.VISIBLE) {
            // 隐藏动画结束后设置不可见
            rippleView.setVisibility(View.GONE);
        }
    }
    
    /**
     * 安全地获取当前播放位置
     * 尝试多种方法确保获取到正确的播放位置
     *
     * @return 当前播放位置（毫秒），如果获取失败返回0
     */
    private long getSafeCurrentPosition() {
        try {
            // 方法1：使用getCurrentPositionWhenPlaying()
            long position1 = getCurrentPositionWhenPlaying();
            logDebug("方法1 getCurrentPositionWhenPlaying(): " + position1 + "ms");
            
            // 方法2：如果有播放器管理器，直接获取
            if (getGSYVideoManager() != null) {
                long position2 = getGSYVideoManager().getCurrentPosition();
                logDebug("方法2 GSYVideoManager.getCurrentPosition(): " + position2 + "ms");
                
                // 优先使用非0的值
                if (position2 > 0) {
                    return position2;
                }
            }
            
            // 使用方法1的结果
            return position1;
            
        } catch (Exception e) {
            logDebug("获取播放位置异常: " + e.getMessage());
            return 0;
        }
    }
}

