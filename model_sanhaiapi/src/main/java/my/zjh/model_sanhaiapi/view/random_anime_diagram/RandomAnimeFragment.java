package my.zjh.model_sanhaiapi.view.random_anime_diagram;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.databinding.ShFragmentRandomAnimeBinding;
import my.zjh.model_sanhaiapi.dialog.LoadingDialogFragment;
import my.zjh.model_sanhaiapi.model.RandomAnimeResponse;
import my.zjh.model_sanhaiapi.viewmodel.RandomAnimeViewModel;

/**
 * 随机动漫图Fragment - 使用标准Fragment生命周期实现懒加载
 */
public class RandomAnimeFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private ShFragmentRandomAnimeBinding binding;
    private RandomAnimeViewModel viewModel;
    private String currentCategory = "adaptive";
    private boolean isFirstLoad = true;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShFragmentRandomAnimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(RandomAnimeViewModel.class);
        
        // 初始化时隐藏图片卡片
        binding.cardImage.setVisibility(View.GONE);
        
        // 监听RadioGroup变化
        binding.radioGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_adaptive) {
                currentCategory = "adaptive";
            } else if (checkedId == R.id.radio_bd) {
                currentCategory = "bd";
            } else if (checkedId == R.id.radio_moban) {
                currentCategory = "moban";
            }
        });
        
        // 刷新按钮点击事件
        binding.btnRefresh.setOnClickListener(v -> {
            viewModel.fetchRandomAnime(currentCategory);
            // 显示图片卡片
            binding.cardImage.setVisibility(View.VISIBLE);
        });
        
        // 下载按钮点击事件
        binding.btnDownload.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                viewModel.saveImageToStorage();
            } else {
                requestStoragePermission();
            }
        });
        
        // 收藏按钮点击事件
        binding.btnFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite();
        });
        
        // 文件名点击复制
        binding.textFilename.setOnClickListener(v -> {
            String text = binding.textFilename.getText().toString();
            if (text.startsWith("文件名: ")) {
                String filename = text.substring("文件名: ".length());
                copyToClipboard("文件名", filename);
                toast("文件名已复制到剪贴板");
            }
        });
        
        // URL点击复制
        binding.textUrl.setOnClickListener(v -> {
            String text = binding.textUrl.getText().toString();
            if (text.startsWith("URL: ")) {
                String url = text.substring("URL: ".length());
                copyToClipboard("URL", url);
                toast("URL已复制到剪贴板");
            }
        });
        
        // 观察ViewModel数据变化
        observeViewModelData();
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModelData() {
        // 观察图片数据
        viewModel.getAnimeData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                updateUI(data);
            }
        });
        
        // 观察加载状态
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                LoadingDialogFragment.showLoading(requireActivity().getSupportFragmentManager(), "加载中...");
            }
            // 加载结束时不关闭，等待图片加载完成再关闭
        });
        
        // 观察错误信息
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.textError.setText(error);
                binding.textError.setVisibility(View.VISIBLE);
            } else {
                binding.textError.setVisibility(View.GONE);
            }
        });
        
        // 观察收藏状态
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            binding.btnFavorite.setText(isFavorite ? "取消收藏" : "收藏");
            binding.btnFavorite.setIcon(ContextCompat.getDrawable(requireContext(), 
                    isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off));
        });
        
        // 观察消息提示
        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                toast(message);
            }
        });
    }
    
    /**
     * 更新UI显示
     */
    private void updateUI(RandomAnimeResponse.Data data) {
        // 加载图片
        Glide.with(this)
                .load(data.getUrl())
                .placeholder(R.drawable.sh_placeholder)
                .error(R.drawable.sh_error)
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        LoadingDialogFragment.dismiss(requireActivity().getSupportFragmentManager());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        LoadingDialogFragment.dismiss(requireActivity().getSupportFragmentManager());
                        return false;
                    }
                })
                .into(binding.imageAnime);
        
        // 更新文本信息
        binding.textFilename.setText("文件名: " + data.getFilename());
        binding.textUrl.setText("URL: " + data.getUrl());
        
        // 隐藏错误信息
        binding.textError.setVisibility(View.GONE);
    }
    
    /**
     * 复制文本到剪贴板
     */
    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
    
    /**
     * 显示Toast消息
     */
    protected void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 检查存储权限
     */
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onDestroyView() {
        binding = null;
        
        super.onDestroyView();
        
    }
} 