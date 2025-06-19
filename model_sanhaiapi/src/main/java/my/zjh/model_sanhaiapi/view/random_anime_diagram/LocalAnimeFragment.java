package my.zjh.model_sanhaiapi.view.random_anime_diagram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import my.zjh.model_sanhaiapi.adapter.AnimeImageAdapter;
import my.zjh.model_sanhaiapi.databinding.ShFragmentImageListBinding;
import my.zjh.model_sanhaiapi.model.AnimeImage;

/**
 * 本地下载的动漫图Fragment
 */
public class LocalAnimeFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 1002;
    
    // 排序方式常量
    private static final int SORT_TIME_DESC = 0; // 时间降序（新到旧）
    private static final int SORT_TIME_ASC = 1;  // 时间升序（旧到新）
    private static final int SORT_NAME_ASC = 2;  // 名称升序（A-Z）
    private static final int SORT_NAME_DESC = 3; // 名称降序（Z-A）
    
    private ShFragmentImageListBinding binding;
    private AnimeImageAdapter adapter;
    private List<AnimeImage> localImageList = new ArrayList<>();
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
        
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadLocalImages);
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
        if (localImageList.isEmpty()) return;
        
        // 保存当前滚动位置
        RecyclerView recyclerView = binding.recyclerView;
        int firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        View firstView = recyclerView.getChildAt(0);
        int topOffset = (firstView == null) ? 0 : firstView.getTop();
        
        List<AnimeImage> sortedList = new ArrayList<>(localImageList);
        
        // 根据当前排序模式排序
        switch (currentSortMode) {
            case SORT_TIME_DESC: // 时间降序（新到旧）
                Collections.sort(sortedList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case SORT_TIME_ASC: // 时间升序（旧到新）
                Collections.sort(sortedList, (a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
                break;
            case SORT_NAME_ASC: // 名称升序（A-Z）
                Collections.sort(sortedList, (a, b) -> a.getFilename().compareToIgnoreCase(b.getFilename()));
                break;
            case SORT_NAME_DESC: // 名称降序（Z-A）
                Collections.sort(sortedList, (a, b) -> b.getFilename().compareToIgnoreCase(a.getFilename()));
                break;
        }
        
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
        checkPermissionAndLoadImages();
    }
    
    private void setupRecyclerView() {
        // 使用新的ListAdapter
        adapter = new AnimeImageAdapter();
        adapter.setOnItemClickListener(position -> {
            // 图片点击处理
            AnimeImage image = adapter.getCurrentList().get(position);
            // 显示大图或详情...
            toast("查看本地图片: " + image.getFilename());
        });
        
        adapter.setOnItemLongClickListener(position -> {
            // 长按删除本地图片
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
        // 显示删除确认对话框
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("删除图片")
                .setMessage("确定要删除这张本地图片吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    deleteLocalImage(position);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void deleteLocalImage(int position) {
        List<AnimeImage> currentList = new ArrayList<>(adapter.getCurrentList());
        if (position >= 0 && position < currentList.size()) {
            AnimeImage image = currentList.get(position);
            File imageFile = new File(image.getUrl());
            
            if (imageFile.exists() && imageFile.delete()) {
                // 创建新列表并提交
                currentList.remove(position);
                localImageList = currentList;
                adapter.submitList(currentList);
                
                toast("已删除本地图片");
            } else {
                toast("删除失败");
            }
            
            updateEmptyView();
        }
    }
    
    /**
     * 检查权限并加载本地图片
     */
    public void checkPermissionAndLoadImages() {
        if (checkStoragePermission()) {
            loadLocalImages();
        } else {
            requestStoragePermission();
        }
    }
    
    private void loadLocalImages() {
        binding.swipeRefreshLayout.setRefreshing(true);
        
        new Thread(() -> {
            // 创建新列表
            List<AnimeImage> newList = new ArrayList<>();
            
            // 获取Pictures目录中的图片
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (picturesDir.exists() && picturesDir.isDirectory()) {
                File[] files = picturesDir.listFiles(file -> {
                    String name = file.getName().toLowerCase();
                    return file.isFile() && (name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                            name.endsWith(".png") || name.endsWith(".webp"));
                });
                
                if (files != null) {
                    for (File file : files) {
                        AnimeImage image = new AnimeImage();
                        image.setUrl(file.getAbsolutePath());
                        image.setFilename(file.getName());
                        image.setCategory("本地");
                        // 使用文件的最后修改时间作为时间戳
                        image.setTimestamp(file.lastModified());
                        newList.add(image);
                    }
                    
                    // 根据当前排序模式排序
                    sortList(newList);
                }
            }
            
            // 更新主线程UI
            localImageList = newList;
            requireActivity().runOnUiThread(() -> {
                adapter.submitList(newList);
                binding.swipeRefreshLayout.setRefreshing(false);
                updateEmptyView();
            });
        }).start();
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
    
    private void updateEmptyView() {
        if (localImageList.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyText.setText("暂无本地图片");
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }
    
    /**
     * 检查存储权限
     */
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                PERMISSION_REQUEST_CODE);
    }
    
    /**
     * 显示Toast消息
     */
    protected void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadLocalImages();
        } else {
            toast("需要存储权限才能查看本地图片");
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyText.setText("无法访问本地图片，请授予存储权限");
        }
    }
    
    @Override
    public void onDestroyView() {
        binding = null;
        
        super.onDestroyView();
    }
} 