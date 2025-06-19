package my.zjh.model_sanhaiapi.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.MusicAdapter;
import my.zjh.model_sanhaiapi.databinding.ShActivityMiguMusicVipBinding;
import my.zjh.model_sanhaiapi.model.MiguMusic;
import my.zjh.model_sanhaiapi.service.MusicPlayerService;
import my.zjh.model_sanhaiapi.viewmodel.MiguMusicViewModel;

/**
 * 咪咕音乐VIP活动
 */
@Route(path = "/sanhai/MiguMusicVipActivity")
public class MiguMusicVipActivity extends BaseActivity {
    
    private static final String TAG = "MiguMusicVipActivity";
    // 默认搜索结果数量
    private static final int DEFAULT_SEARCH_NUM = 20;
    // 当前搜索关键词
    private String currentKeyword = "邓紫棋";
    // ViewBinding
    private ShActivityMiguMusicVipBinding binding;
    // ViewModel
    private MiguMusicViewModel viewModel;
    // 音乐列表适配器
    private MusicAdapter musicAdapter;
    
    // 音乐播放服务
    private MusicPlayerService musicPlayerService;
    // 服务绑定状态
    private boolean isBound = false;
    // 待播放的音乐详情
    private MiguMusic.MusicDetail pendingMusic = null;
    // 服务连接
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "服务已连接");
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicPlayerService = binder.getService();
            isBound = true;
            
            // 检查是否有待播放的音乐
            checkPendingPlay();
            
            // 更新播放按钮状态
            updatePlayPauseButton();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "服务已断开");
            musicPlayerService = null;
            isBound = false;
        }
    };
    
    // 播放状态监听器引用
    private Player.Listener playerStateListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityMiguMusicVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        Log.d(TAG, "Activity创建");
        
        // 设置工具栏
        setupToolbar(binding.shToolbarLayout.toolbar, "咪咕音乐VIP", binding.shToolbarLayout.appbar, true);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(MiguMusicViewModel.class);
        
        // 初始化RecyclerView
        setupRecyclerView();
        
        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener(v -> performSearch());
        
        // 设置搜索框回车搜索
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        
        // 设置播放/暂停按钮点击事件
        binding.playPauseButton.setOnClickListener(v -> {
            if (isBound && musicPlayerService != null) {
                musicPlayerService.togglePlayPause();
                updatePlayPauseButton();
            } else {
                Toast.makeText(this, "播放服务未连接", Toast.LENGTH_SHORT).show();
                // 尝试重新绑定服务
                startAndBindMusicService();
            }
        });
        
        // 观察ViewModel中的数据变化
        observeViewModel();
        
        // 启动并绑定音乐播放服务
        startAndBindMusicService();
        
        // 默认搜索
        viewModel.searchMusic(currentKeyword, DEFAULT_SEARCH_NUM);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity恢复");
        
        // 检查服务绑定状态
        if (!isBound) {
            startAndBindMusicService();
        }
        
        // 更新UI
        if (isBound && musicPlayerService != null && musicPlayerService.getCurrentMusic() != null) {
            updatePlayerUI(musicPlayerService.getCurrentMusic());
            binding.playerContainer.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Activity销毁");
        
        // 移除所有监听器，避免内存泄漏
        if (isBound && musicPlayerService != null) {
            // 移除进度监听器
            Log.d(TAG, "移除进度监听器");
            musicPlayerService.setProgressListener(null);
            
            // 移除播放状态监听器
            if (playerStateListener != null && musicPlayerService.getPlayer() != null) {
                Log.d(TAG, "移除播放状态监听器");
                musicPlayerService.getPlayer().removeListener(playerStateListener);
                playerStateListener = null;
            }
        }
        
        // 解绑服务
        if (isBound) {
            Log.d(TAG, "解绑服务");
            unbindService(serviceConnection);
            isBound = false;
        }
        
        super.onDestroy();
    }
    
    /**
     * 启动并绑定音乐播放服务
     */
    private void startAndBindMusicService() {
        Log.d(TAG, "启动并绑定音乐播放服务");
        try {
            // 创建意图
            Intent serviceIntent = new Intent(this, MusicPlayerService.class);
            // 启动服务（确保服务在后台运行）
            startService(serviceIntent);
            // 绑定服务（便于直接调用服务方法）
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.e(TAG, "启动或绑定服务失败: " + e.getMessage());
            Toast.makeText(this, "启动服务失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 检查是否有待播放的音乐
     */
    private void checkPendingPlay() {
        if (pendingMusic != null && isBound && musicPlayerService != null) {
            Log.d(TAG, "播放待播放的音乐: " + pendingMusic.getTitle());
            musicPlayerService.playMusic(pendingMusic);
            updatePlayerUI(pendingMusic);
            binding.playerContainer.setVisibility(View.VISIBLE);
            pendingMusic = null;
            
            // 添加播放状态监听器
            addPlayerStateListener();
        }
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        musicAdapter = new MusicAdapter();
        binding.musicRecyclerView.setAdapter(musicAdapter);
        
        // 设置音乐项点击事件
        musicAdapter.setOnMusicItemClickListener((musicItem, position) -> {
            Log.d(TAG, "音乐项点击: " + musicItem.getTitle() + ", 位置: " + position);
            // 获取音乐详情
            viewModel.getMusicDetail(currentKeyword, musicItem.getN());
        });
    }
    
    /**
     * 观察ViewModel中的数据变化
     */
    private void observeViewModel() {
        // 观察搜索结果
        viewModel.getSearchResultLiveData().observe(this, searchResult -> {
            if (searchResult != null && searchResult.getData() != null) {
                Log.d(TAG, "搜索结果更新: " + searchResult.getData().size() + "项");
                // 更新列表
                musicAdapter.updateList(searchResult.getData());
                
                // 设置空视图可见性
                if (searchResult.getData().isEmpty()) {
                    binding.emptyView.setText("未找到相关音乐");
                    binding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyView.setVisibility(View.GONE);
                }
            }
        });
        
        // 观察音乐详情
        viewModel.getMusicDetailLiveData().observe(this, musicDetail -> {
            if (musicDetail != null) {
                Log.d(TAG, "音乐详情更新: " + musicDetail.getTitle());
                Log.d(TAG, "音乐URL: " + musicDetail.getMusicUrl());
                
                if (musicDetail.getMusicUrl() == null || musicDetail.getMusicUrl().isEmpty()) {
                    // 处理音乐URL为空的情况
                    Toast.makeText(this, "无法获取音乐播放地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 更新列表项中的封面和品质信息
                updateMusicItemInfo(musicDetail);
                
                if (isBound && musicPlayerService != null) {
                    // 服务已绑定，直接播放
                    Log.d(TAG, "服务已绑定，直接播放");
                    musicPlayerService.playMusic(musicDetail);
                    updatePlayerUI(musicDetail);
                    binding.playerContainer.setVisibility(View.VISIBLE);
                    
                    // 添加播放状态监听器
                    addPlayerStateListener();
                } else {
                    // 服务未绑定，先保存待播放的音乐，然后再绑定服务
                    Log.d(TAG, "服务未绑定，保存待播放音乐");
                    pendingMusic = musicDetail;
                    // 尝试重新绑定服务
                    startAndBindMusicService();
                }
            }
        });
        
        // 观察加载状态
        viewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            Log.d(TAG, "加载状态更新: " + isLoading);
            binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // 观察错误信息
        viewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "错误信息: " + errorMessage);
                toast(errorMessage);
                // Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * 执行搜索
     */
    private void performSearch() {
        // 获取搜索关键词
        String keyword = binding.searchEditText.getText().toString().trim();
        
        if (!keyword.isEmpty()) {
            Log.d(TAG, "执行搜索: " + keyword);
            // 保存当前关键词
            currentKeyword = keyword;
            // 执行搜索
            viewModel.searchMusic(currentKeyword, DEFAULT_SEARCH_NUM);
        } else {
            // 提示输入关键词
            toast("请输入搜索关键词");
            // Snackbar.make(binding.getRoot(), "请输入搜索关键词", Snackbar.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 更新播放器UI
     *
     * @param musicDetail 音乐详情
     */
    private void updatePlayerUI(MiguMusic.MusicDetail musicDetail) {
        Log.d(TAG, "更新播放器UI: " + musicDetail.getTitle());
        
        // 设置歌曲标题
        binding.songTitleText.setText(musicDetail.getTitle());
        // 设置歌手
        binding.songArtistText.setText(musicDetail.getSinger());
        // 设置音质
        binding.songQualityText.setText(musicDetail.getQuality());
        
        // 重置进度条和时间
        binding.songProgressBar.setProgress(0);
        binding.songProgressText.setText("00:00");
        binding.songDurationText.setText("00:00");
        
        // 加载专辑封面
        if (musicDetail.getCover() != null && !musicDetail.getCover().isEmpty()) {
            Log.d(TAG, "加载专辑封面: " + musicDetail.getCover());
            Glide.with(this)
                    .load(musicDetail.getCover())
                    .apply(RequestOptions.placeholderOf(R.drawable.sh_default_album_art))
                    .into(binding.albumImage);
        } else {
            // 设置默认封面
            binding.albumImage.setImageResource(R.drawable.sh_default_album_art);
        }
        
        // 更新播放按钮状态
        updatePlayPauseButton();
    }
    
    /**
     * 更新播放/暂停按钮状态
     */
    private void updatePlayPauseButton() {
        if (isBound && musicPlayerService != null) {
            try {
                // 根据播放状态设置图标
                if (musicPlayerService.getPlayer().isPlaying()) {
                    Log.d(TAG, "更新按钮状态: 正在播放");
                    binding.playPauseButton.setIconResource(R.drawable.sh_ic_pause);
                } else {
                    Log.d(TAG, "更新按钮状态: 已暂停");
                    binding.playPauseButton.setIconResource(R.drawable.sh_ic_play);
                }
            } catch (Exception e) {
                Log.e(TAG, "更新播放按钮状态失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 添加播放器状态监听
     */
    private void addPlayerStateListener() {
        if (isBound && musicPlayerService != null && musicPlayerService.getPlayer() != null) {
            Log.d(TAG, "添加播放器状态监听");
            
            // 先移除之前的监听器
            if (playerStateListener != null) {
                musicPlayerService.getPlayer().removeListener(playerStateListener);
            }
            
            // 创建新的监听器
            playerStateListener = new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Log.d(TAG, "播放状态变化: " + (isPlaying ? "播放中" : "已暂停"));
                    // 在UI线程中更新按钮状态
                    runOnUiThread(() -> updatePlayPauseButton());
                }
                
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Log.d(TAG, "播放状态变化: " + playbackState);
                    if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "播放结束，更新按钮状态");
                        // 在UI线程中更新按钮状态为播放
                        runOnUiThread(() -> updatePlayPauseButton());
                    }
                }
            };
            
            // 添加监听器
            musicPlayerService.getPlayer().addListener(playerStateListener);
            
            // 添加进度监听
            musicPlayerService.setProgressListener((position, duration, progress) -> {
                runOnUiThread(() -> {
                    // 更新进度条
                    binding.songProgressBar.setMax(100);
                    binding.songProgressBar.setProgress(progress);
                    
                    // 更新时间文本
                    binding.songProgressText.setText(MusicPlayerService.formatTime(position));
                    binding.songDurationText.setText(MusicPlayerService.formatTime(duration));
                });
            });
        }
    }
    
    /**
     * 更新音乐列表项信息
     *
     * @param musicDetail 音乐详情
     */
    private void updateMusicItemInfo(MiguMusic.MusicDetail musicDetail) {
        // 获取当前列表
        List<MiguMusic.MusicItem> currentList = musicAdapter.getMusicList();
        
        // 查找对应的列表项
        for (MiguMusic.MusicItem item : currentList) {
            if (item.getTitle().equals(musicDetail.getTitle()) && 
                item.getSinger().equals(musicDetail.getSinger())) {
                // 找到匹配的项，更新封面和品质
                item.setCover(musicDetail.getCover());
                item.setQuality(musicDetail.getQuality());
                
                Log.d(TAG, "更新列表项: " + item.getTitle() + " 的封面和品质信息");
                
                // 刷新适配器
                musicAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}