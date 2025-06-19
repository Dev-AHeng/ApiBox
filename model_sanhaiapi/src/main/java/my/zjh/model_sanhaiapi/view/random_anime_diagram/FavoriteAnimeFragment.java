package my.zjh.model_sanhaiapi.view.random_anime_diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import my.zjh.model_sanhaiapi.adapter.AnimeImageAdapter;
import my.zjh.model_sanhaiapi.databinding.ShFragmentImageListBinding;
import my.zjh.model_sanhaiapi.model.AnimeImage;

/**
 * 收藏动漫图Fragment
 */
public class FavoriteAnimeFragment extends Fragment {
    private static final String PREFS_NAME = "anime_favorites";
    private static final String FAVORITES_KEY = "favorites_list";
    
    // 排序方式常量
    private static final int SORT_TIME_DESC = 0; // 时间降序（新到旧）
    private static final int SORT_TIME_ASC = 1;  // 时间升序（旧到新）
    private static final int SORT_NAME_ASC = 2;  // 名称升序（A-Z）
    private static final int SORT_NAME_DESC = 3; // 名称降序（Z-A）
    
    private ShFragmentImageListBinding binding;
    private AnimeImageAdapter adapter;
    private List<AnimeImage> favoriteList = new ArrayList<>();
    private int currentSortMode = SORT_TIME_DESC; // 默认排序方式：时间降序
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShFragmentImageListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        setupSortChips();
        
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadFavorites);
    }
    
    /**
     * 设置排序下拉框
     */
    private void setupSortChips() {
        // 创建排序选项数组
        String[] sortOptions = new String[]{
            "时间降序（新到旧）", 
            "时间升序（旧到新）", 
            "名称升序（A-Z）", 
            "名称降序（Z-A）"
        };
        
        // 创建Spinner适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // 设置Spinner
        binding.sortSpinner.setAdapter(adapter);
        binding.sortSpinner.setSelection(currentSortMode);
        
        // 设置选择监听器
        binding.sortSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (currentSortMode != position) {
                    currentSortMode = position;
                    sortAndUpdateList();
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // 不做任何处理
            }
        });
    }
    
    /**
     * 根据当前排序模式排序并更新列表
     */
    private void sortAndUpdateList() {
        if (favoriteList.isEmpty()) {
            return;
        }
        
        // 保存当前滚动位置
        RecyclerView recyclerView = binding.recyclerView;
        int firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        View firstView = recyclerView.getChildAt(0);
        int topOffset = (firstView == null) ? 0 : firstView.getTop();
        
        List<AnimeImage> sortedList = new ArrayList<>(favoriteList);
        
        // 根据当前排序模式排序
        sortList(sortedList);
        
        // 更新列表
        adapter.submitList(sortedList, () -> {
            // 回调在列表更新完成后执行
            // 恢复之前的滚动位置
            if (firstVisibleItemPosition != RecyclerView.NO_POSITION && recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().scrollToPosition(0); // 先滚到顶部
                // 使用post延迟执行，确保布局计算完成
                recyclerView.post(() -> {
                    if (recyclerView.getLayoutManager() != null) {
                        ((GridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(firstVisibleItemPosition, topOffset);
                    }
                });
            }
        });
    }

    /**
     * 当Fragment可见并活跃时加载数据
     */
    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
    
    private void setupRecyclerView() {
        // 使用新的ListAdapter
        adapter = new AnimeImageAdapter();
        adapter.setOnItemClickListener(position -> {
            // 图片点击处理
            AnimeImage image = adapter.getCurrentList().get(position);
            // 显示大图或详情...
            toast("查看收藏图片: " + image.getFilename());
        });
        
        adapter.setOnItemLongClickListener(position -> {
            // 长按显示操作菜单
            showDeleteDialog(position);
            return true;
        });
        
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);
        
        // 设置滚动监听，控制FAB的显示和隐藏
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 获取第一个可见项的位置
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                
                // 如果不在顶部且列表有数据，则显示FAB
                if (firstVisibleItem > 0 && adapter.getItemCount() > 0) {
                    binding.fabTop.show();
                } else {
                    binding.fabTop.hide();
                }
            }
        });
        
        // 设置FAB点击事件，回到顶部
        binding.fabTop.setOnClickListener(v -> {
            binding.recyclerView.smoothScrollToPosition(0);
        });
    }
    
    private void showDeleteDialog(int position) {
        // 显示操作选项对话框
        String[] options = {"删除收藏", "下载到本地"};
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("图片操作")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // 删除收藏
                        confirmDelete(position);
                    } else if (which == 1) {
                        // 下载到本地
                        downloadToLocal(position);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 确认删除收藏
     */
    private void confirmDelete(int position) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("删除收藏")
                .setMessage("确定要删除这张收藏的图片吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    removeFromFavorites(position);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 下载收藏图片到本地
     */
    private void downloadToLocal(int position) {
        if (position >= 0 && position < favoriteList.size()) {
            AnimeImage image = favoriteList.get(position);
            
            // 检查存储权限
            if (checkStoragePermission()) {
                // 显示正在下载的提示
                binding.swipeRefreshLayout.setRefreshing(true);
                toast("开始下载图片...");
                
                new Thread(() -> {
                    boolean success = downloadImageToStorage(image);
                    
                    requireActivity().runOnUiThread(() -> {
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if (success) {
                            toast("图片已保存到本地");
                        } else {
                            toast("保存图片失败");
                        }
                    });
                }).start();
            } else {
                requestStoragePermission();
            }
        }
    }
    
    /**
     * 下载图片到存储空间
     */
    private boolean downloadImageToStorage(AnimeImage image) {
        try {
            // 创建Glide请求
            com.bumptech.glide.RequestBuilder<java.io.File> requestBuilder = 
                    com.bumptech.glide.Glide.with(requireContext())
                    .asFile()
                    .load(image.getUrl());
            
            // 执行请求并获取文件
            java.io.File imageFile = requestBuilder.submit().get();
            
            // 获取Pictures目录
            java.io.File picturesDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_PICTURES);
            
            // 创建目标文件
            java.io.File destFile = new java.io.File(picturesDir, image.getFilename());
            
            // 复制文件
            java.nio.file.Files.copy(
                    imageFile.toPath(),
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            // 通知系统媒体库更新
            requireActivity().sendBroadcast(new android.content.Intent(
                    android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    android.net.Uri.fromFile(destFile)));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查存储权限
     */
    private boolean checkStoragePermission() {
        return androidx.core.content.ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        androidx.core.app.ActivityCompat.requestPermissions(
                requireActivity(), 
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                1001);
        toast("需要存储权限才能下载图片");
    }
    
    private void removeFromFavorites(int position) {
        if (position >= 0 && position < favoriteList.size()) {
            AnimeImage imageToRemove = favoriteList.get(position);
            
            // 从列表和SharedPreferences中删除
            List<AnimeImage> newList = new ArrayList<>(favoriteList);
            newList.remove(position);
            
            // 更新列表
            favoriteList = newList;
            adapter.submitList(newList);
            
            // 更新SharedPreferences
            removeFromPrefs(imageToRemove);
            
            toast("已从收藏中移除");
            updateEmptyView();
        }
    }
    
    /**
     * 对列表进行排序
     */
    private void sortList(List<AnimeImage> list) {
        switch (currentSortMode) {
            case SORT_TIME_DESC: // 时间降序（新到旧）
                Collections.sort(list, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case SORT_TIME_ASC: // 时间升序（旧到新）
                Collections.sort(list, (a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
                break;
            case SORT_NAME_ASC: // 名称升序（A-Z）
                Collections.sort(list, (a, b) -> a.getFilename().compareToIgnoreCase(b.getFilename()));
                break;
            case SORT_NAME_DESC: // 名称降序（Z-A）
                Collections.sort(list, (a, b) -> b.getFilename().compareToIgnoreCase(a.getFilename()));
                break;
        }
    }
    
    /**
     * 加载收藏数据
     */
    public void loadFavorites() {
        binding.swipeRefreshLayout.setRefreshing(true);
        
        // 从SharedPreferences加载收藏
        List<Map<String, String>> favorites = getFavoritesFromPrefs();
        
        // 如果列表为空，直接处理
        if (favorites.isEmpty()) {
            favoriteList = new ArrayList<>();
            adapter.submitList(favoriteList);
            binding.swipeRefreshLayout.setRefreshing(false);
            updateEmptyView();
            return;
        }
        
        // 判断是否与现有数据相同
        if (!favoriteList.isEmpty()) {
            boolean isSameData = favorites.size() == favoriteList.size();
            if (isSameData) {
                // 数据量相同，判断是否内容相同
                for (int i = 0; i < favorites.size(); i++) {
                    Map<String, String> item = favorites.get(i);
                    AnimeImage image = favoriteList.get(i);
                    
                    if (!item.get("url").equals(image.getUrl())) {
                        isSameData = false;
                        break;
                    }
                }
                
                // 如果是相同数据，不重新加载
                if (isSameData) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    return;
                }
            }
        }
        
        // 转换为AnimeImage对象
        List<AnimeImage> newList = new ArrayList<>();
        for (Map<String, String> item : favorites) {
            AnimeImage image = new AnimeImage();
            image.setUrl(item.get("url"));
            image.setCategory(item.get("category"));
            image.setFilename(item.get("filename"));
            
            // 获取时间戳，如果存在则使用，否则使用当前时间
            String timestamp = item.get("timestamp");
            if (timestamp != null && !timestamp.isEmpty()) {
                try {
                    image.setTimestamp(Long.parseLong(timestamp));
                } catch (NumberFormatException e) {
                    image.setTimestamp(System.currentTimeMillis());
                }
            } else {
                image.setTimestamp(System.currentTimeMillis());
            }
            
            newList.add(image);
        }
        
        // 按照当前排序模式排序
        sortList(newList);
        
        // 使用submitList更新数据
        favoriteList = newList;
        adapter.submitList(new ArrayList<>(newList));
        
        binding.swipeRefreshLayout.setRefreshing(false);
        updateEmptyView();
    }
    
    private void updateEmptyView() {
        if (favoriteList.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyText.setText("暂无收藏的图片");
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }
    
    private List<Map<String, String>> getFavoritesFromPrefs() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String favoritesJson = prefs.getString(FAVORITES_KEY, "");
        
        if (favoritesJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        return gson.fromJson(favoritesJson, type);
    }
    
    private void saveFavoritesToPrefs(List<Map<String, String>> favorites) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoritesJson = gson.toJson(favorites);
        prefs.edit().putString(FAVORITES_KEY, favoritesJson).apply();
    }

    /**
     * 显示Toast消息
     */
    protected void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 
                && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            toast("权限已授予，现在可以下载图片");
        } else {
            toast("需要存储权限才能下载图片");
        }
    }

    /**
     * 从SharedPreferences中移除收藏
     */
    private void removeFromPrefs(AnimeImage image) {
        List<Map<String, String>> favorites = getFavoritesFromPrefs();
        favorites.removeIf(item -> item.get("url").equals(image.getUrl()));
        saveFavoritesToPrefs(favorites);
    }
} 