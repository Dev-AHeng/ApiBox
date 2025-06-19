package my.zjh.model_sanhaiapi.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.ExecutionException;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.model.MiguMusic;
import my.zjh.model_sanhaiapi.receiver.MusicControlReceiver;
import my.zjh.model_sanhaiapi.view.MiguMusicVipActivity;

/**
 * 音乐播放服务
 *
 * @author Dev_Heng
 */
public class MusicPlayerService extends Service {
    
    private static final String TAG = "MusicPlayerService";
    
    // 通知渠道ID
    private static final String CHANNEL_ID = "music_player_channel";
    // 通知ID
    private static final int NOTIFICATION_ID = 1;
    // 绑定服务的Binder
    private final IBinder binder = new MusicBinder();
    // 进度更新Handler和Runnable
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    // ExoPlayer实例
    private ExoPlayer player;
    // MediaSession实例
    private MediaSession mediaSession;
    // 当前播放的音乐
    private MiguMusic.MusicDetail currentMusic;
    // 进度更新监听器
    private ProgressListener progressListener;
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            // 更新通知中的进度条
            updateNotification();
            
            // 通知进度更新监听器
            if (progressListener != null) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                int progress = (int) ((position * 100) / (duration == 0 ? 1 : duration));
                progressListener.onProgressChanged(position, duration, progress);
            }
            
            // 如果播放器正在播放，继续定期更新
            if (player != null && player.isPlaying()) {
                progressHandler.postDelayed(this, 1000);
            }
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "服务创建");
        
        // 创建通知渠道
        createNotificationChannel();
        
        // 创建ExoPlayer实例
        player = new ExoPlayer.Builder(this)
                         .setHandleAudioBecomingNoisy(true) // 处理音频设备变化（如拔出耳机）
                         .build();
        
        // 设置播放状态监听
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.d(TAG, "播放状态变化: " + playbackState);
                // 更新通知
                updateNotification();
                
                // 输出当前播放状态的详细信息
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        Log.d(TAG, "播放器状态: 空闲");
                        Toast.makeText(MusicPlayerService.this, "播放器已就绪", Toast.LENGTH_SHORT).show();
                        // 停止进度更新
                        stopProgressUpdates();
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "播放器状态: 缓冲中");
                        Toast.makeText(MusicPlayerService.this, "缓冲中...", Toast.LENGTH_SHORT).show();
                        break;
                    case Player.STATE_READY:
                        Log.d(TAG, "播放器状态: 就绪" + (player.isPlaying() ? "，并且正在播放" : "，但没有播放"));
                        if (player.isPlaying()) {
                            Toast.makeText(MusicPlayerService.this, "开始播放", Toast.LENGTH_SHORT).show();
                            // 开始进度更新
                            startProgressUpdates();
                        }
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "播放器状态: 播放结束");
                        Log.d(TAG, "播放完成");
                        Toast.makeText(MusicPlayerService.this, "播放完成", Toast.LENGTH_SHORT).show();
                        // 停止进度更新
                        stopProgressUpdates();
                        break;
                    default:
                }
            }
            
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d(TAG, "正在播放状态变化: " + isPlaying);
                // 更新通知
                updateNotification();
                // 提示当前状态
                if (isPlaying) {
                    Toast.makeText(MusicPlayerService.this, "正在播放: " + (currentMusic != null ? currentMusic.getTitle() : ""), Toast.LENGTH_SHORT).show();
                    // 开始进度更新
                    startProgressUpdates();
                } else {
                    Toast.makeText(MusicPlayerService.this, "已暂停", Toast.LENGTH_SHORT).show();
                    // 停止进度更新
                    stopProgressUpdates();
                }
            }
            
            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "播放器错误: " + error.getMessage());
                Log.e(TAG, "错误详情: " + error.getErrorCodeName());
                Toast.makeText(MusicPlayerService.this, "播放失败: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // 停止进度更新
                stopProgressUpdates();
            }
        });
        
        // 创建MediaSession
        mediaSession = new MediaSession.Builder(this, player)
                               .setId("MiguMusicSession")
                               .build();
        
        // 设置播放器为前台服务，确保不会被系统杀死
        Notification notification = createEmptyNotification();
        startForeground(NOTIFICATION_ID, notification);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "服务启动: " + (intent != null ? intent.getAction() : "null"));
        
        if (intent != null && MusicControlReceiver.ACTION_MUSIC_CONTROL.equals(intent.getAction())) {
            // 处理控制操作
            int controlCode = intent.getIntExtra(MusicControlReceiver.EXTRA_CONTROL_CODE, -1);
            Log.d(TAG, "收到控制操作: " + controlCode);
            handleControlAction(controlCode);
        }
        
        // 使服务在被杀死后可以重启
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "服务销毁");
        
        // 释放播放器资源
        if (player != null) {
            player.release();
            player = null;
        }
        
        // 释放MediaSession资源
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        
        super.onDestroy();
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "服务绑定");
        return binder;
    }
    
    /**
     * 格式化时间（毫秒转 mm:ss 格式）
     *
     * @param timeMs 时间（毫秒）
     *
     * @return 格式化后的时间字符串
     */
    @SuppressLint("DefaultLocale")
    public static String formatTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * 处理控制操作
     */
    private void handleControlAction(int controlCode) {
        Log.d(TAG, "处理控制操作: " + controlCode);
        
        switch (controlCode) {
            case MusicControlReceiver.CONTROL_PLAY_PAUSE:
                Log.d(TAG, "执行播放/暂停操作");
                togglePlayPause();
                break;
            case MusicControlReceiver.CONTROL_NEXT:
                // 目前不处理下一曲
                Log.d(TAG, "暂不支持下一曲操作");
                break;
            case MusicControlReceiver.CONTROL_STOP:
                Log.d(TAG, "执行停止操作");
                stop();
                break;
            default:
                Log.d(TAG, "未知控制操作: " + controlCode);
                break;
        }
    }
    
    /**
     * 创建空通知
     */
    private Notification createEmptyNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                       .setSmallIcon(R.drawable.sh_ic_music_note)
                       .setContentTitle("咪咕音乐")
                       .setContentText("准备播放...")
                       .setPriority(NotificationCompat.PRIORITY_LOW)
                       .build();
    }
    
    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "音乐播放",
                    NotificationManager.IMPORTANCE_LOW); // 设置为低优先级，避免打断用户
            
            channel.setDescription("咪咕音乐播放通知");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "通知渠道创建完成");
        }
    }
    
    /**
     * 更新通知
     */
    private void updateNotification() {
        if (currentMusic == null) {
            Log.d(TAG, "当前没有音乐，不更新通知");
            return;
        }
        
        Log.d(TAG, "更新通知: " + currentMusic.getTitle());
        
        try {
            // 创建打开应用的PendingIntent
            Intent openAppIntent = new Intent(this, MiguMusicVipActivity.class);
            openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            
            // 加载专辑封面
            Bitmap albumArt = null;
            try {
                if (currentMusic.getCover() != null && !currentMusic.getCover().isEmpty()) {
                    albumArt = Glide.with(this)
                                       .asBitmap()
                                       .load(currentMusic.getCover())
                                       .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                       .submit()
                                       .get();
                    Log.d(TAG, "专辑封面加载成功");
                }
            } catch (ExecutionException |
                     InterruptedException e) {
                Log.e(TAG, "专辑封面加载失败: " + e.getMessage());
            }
            
            // 构建通知
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                                         .setSmallIcon(R.drawable.sh_ic_music_note)
                                                         .setContentTitle(currentMusic.getTitle())
                                                         .setContentText(currentMusic.getSinger() + " · " + currentMusic.getQuality()) // 显示歌手和音质
                                                         .setContentIntent(contentIntent)
                                                         .setOnlyAlertOnce(true)
                                                         .setPriority(NotificationCompat.PRIORITY_LOW)
                                                         .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // 在锁屏上显示
            
            // 设置专辑封面
            if (albumArt != null) {
                builder.setLargeIcon(albumArt);
            }
            
            // 添加进度条（只有在播放时和有有效时长时才显示）
            if (player != null && player.getDuration() > 0) {
                // 获取当前进度
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                
                // 设置进度条
                builder.setProgress((int) duration, (int) position, false);
                
                // 设置副标题显示时间信息
                String progressText = formatTime(position) + " / " + formatTime(duration);
                builder.setSubText(progressText);
            }
            
            // 创建播放/暂停意图
            Intent playPauseIntent = new Intent(this, MusicControlReceiver.class);
            playPauseIntent.setAction(MusicControlReceiver.ACTION_MUSIC_CONTROL);
            playPauseIntent.putExtra(MusicControlReceiver.EXTRA_CONTROL_CODE, MusicControlReceiver.CONTROL_PLAY_PAUSE);
            PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(
                    this, MusicControlReceiver.CONTROL_PLAY_PAUSE, playPauseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            
            // 创建停止播放意图
            Intent stopIntent = new Intent(this, MusicControlReceiver.class);
            stopIntent.setAction(MusicControlReceiver.ACTION_MUSIC_CONTROL);
            stopIntent.putExtra(MusicControlReceiver.EXTRA_CONTROL_CODE, MusicControlReceiver.CONTROL_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                    this, MusicControlReceiver.CONTROL_STOP, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            
            // 根据播放状态设置播放/暂停按钮
            if (player.isPlaying()) {
                builder.addAction(R.drawable.sh_ic_pause, "暂停", playPausePendingIntent);
            } else {
                builder.addAction(R.drawable.sh_ic_play, "播放", playPausePendingIntent);
            }
            
            // 添加停止按钮
            builder.addAction(R.drawable.sh_ic_close, "停止", stopPendingIntent);
            
            // 显示通知
            Notification notification = builder.build();
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
            
            Log.d(TAG, "通知更新成功");
        } catch (Exception e) {
            Log.e(TAG, "通知更新失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 播放音乐
     *
     * @param music 音乐详情
     */
    public void playMusic(MiguMusic.MusicDetail music) {
        Log.d(TAG, "播放音乐: " + music.getTitle());
        Log.d(TAG, "音乐URL: " + music.getMusicUrl());
        
        try {
            // 检查URL是否为空
            if (music.getMusicUrl() == null || music.getMusicUrl().isEmpty()) {
                Log.e(TAG, "音乐URL为空，无法播放");
                Toast.makeText(this, "无法播放：音乐链接无效", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 尝试解析URL，确保格式正确
            Uri uri = Uri.parse(music.getMusicUrl());
            Log.d(TAG, "解析后的URI: " + uri.toString());
            
            currentMusic = music;
            
            // 创建媒体元数据
            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                                                  .setTitle(music.getTitle())
                                                  .setArtist(music.getSinger())
                                                  .setArtworkUri(music.getCover() != null ? Uri.parse(music.getCover()) : null)
                                                  .build();
            
            // 创建媒体项
            MediaItem mediaItem = new MediaItem.Builder()
                                          .setUri(uri)
                                          .setMediaId(String.valueOf(System.currentTimeMillis()))
                                          .setMediaMetadata(mediaMetadata)
                                          .build();
            
            Log.d(TAG, "媒体项创建完成，准备播放");
            
            // 设置媒体项并播放
            player.setMediaItem(mediaItem);
            player.prepare();
            
            // 检查播放器状态
            Log.d(TAG, "播放器准备完成，当前状态: " + player.getPlaybackState());
            
            // 开始播放
            player.play();
            Log.d(TAG, "已调用播放方法，当前播放状态: " + player.isPlaying());
            
            // 更新通知
            updateNotification();
            
            Log.d(TAG, "音乐播放设置完成");
        } catch (Exception e) {
            Log.e(TAG, "音乐播放失败: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 切换播放/暂停状态
     */
    public void togglePlayPause() {
        if (player == null) {
            Log.e(TAG, "播放器为null，无法切换播放状态");
            return;
        }
        
        Log.d(TAG, "切换播放/暂停状态，当前状态: " + (player.isPlaying() ? "播放中" : "已暂停"));
        
        if (player.isPlaying()) {
            Log.d(TAG, "暂停播放");
            player.pause();
        } else {
            Log.d(TAG, "恢复播放");
            player.play();
        }
        
        // 再次检查状态并记录
        Log.d(TAG, "切换后状态: " + (player.isPlaying() ? "播放中" : "已暂停"));
        
        // 更新通知
        updateNotification();
    }
    
    /**
     * 停止播放
     */
    public void stop() {
        Log.d(TAG, "停止播放");
        
        if (player != null) {
            player.stop();
            Log.d(TAG, "播放器已停止");
        }
        
        // 停止前台服务
        stopForeground(true);
        stopSelf();
    }
    
    /**
     * 获取播放器实例
     *
     * @return ExoPlayer实例
     */
    public ExoPlayer getPlayer() {
        return player;
    }
    
    /**
     * 获取当前播放的音乐
     *
     * @return 当前音乐
     */
    public MiguMusic.MusicDetail getCurrentMusic() {
        return currentMusic;
    }
    
    /**
     * 设置进度监听器
     *
     * @param listener 进度监听器
     */
    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }
    
    /**
     * 开始更新进度
     */
    private void startProgressUpdates() {
        // 先停止之前的更新
        stopProgressUpdates();
        
        // 开始新的更新
        progressHandler.post(progressRunnable);
    }
    
    /**
     * 停止更新进度
     */
    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
    }
    
    /**
     * 进度监听器接口
     */
    public interface ProgressListener {
        /**
         * 当进度变化时回调
         *
         * @param position 当前位置（毫秒）
         * @param duration 总时长（毫秒）
         * @param progress 进度（0-100）
         */
        void onProgressChanged(long position, long duration, int progress);
    }
    
    /**
     * 绑定服务的Binder
     */
    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
} 