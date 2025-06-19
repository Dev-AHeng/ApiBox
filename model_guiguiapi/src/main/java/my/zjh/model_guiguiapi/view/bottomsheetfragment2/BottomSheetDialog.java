package my.zjh.model_guiguiapi.view.bottomsheetfragment2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.view.bottomsheetfragment.adapter.BottomGridMenuAdapter;
import my.zjh.model_guiguiapi.view.bottomsheetfragment.model.BottomGridMenuBean;

/**
 * 底部弹窗
 *
 * @author AHeng
 * @date 2025/04/26 4:24
 */
public class BottomSheetDialog extends BottomSheetDialogFragment {
    /**
     * 标签用于Fragment管理
     */
    public static final String TAG = "BottomSheetDialog";
    
    /**
     * 拖动条部分
     */
    private RecyclerView rvMenu;
    
    private BottomGridMenuAdapter adapter;
    private List<BottomGridMenuBean> customMenuItems;
    
    private BottomSheetDialog() {
        // 必须提供公共无参构造函数
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用布局文件创建视图
        return inflater.inflate(R.layout.gg_dialog_bottom_sheet_content, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 在这里初始化视图组件和设置监听器
        initMenuRecyclerView(view);
    }
    
    @Override
    public int getTheme() {
        return R.style.gg_BottomSheetDialog;
    }
    
    @Override
    public void onDestroyView() {
        // 清理视图引用，避免内存泄漏
        super.onDestroyView();
        
        if (adapter != null) {
            adapter.release();
            adapter = null;
        }
        
        // 清理RecyclerView
        if (rvMenu != null) {
            rvMenu.setAdapter(null);
            rvMenu = null;
        }
        
        customMenuItems = null;
    }
    
    /**
     * 获取Builder实例，用于链式调用创建底部表单
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 初始化菜单RecyclerView
     *
     * @param rootView 根视图
     */
    private void initMenuRecyclerView(View rootView) {
        rvMenu = rootView.findViewById(R.id.rv_menu);
        
        // 创建数据源
        List<BottomGridMenuBean> menuItems = createMenuItems();
        
        // 使用工厂创建适配器
        adapter = new BottomGridMenuAdapter(requireContext());
        adapter.submitList(menuItems);
        
        // 设置点击监听
        adapter.setOnItemClickListener((itemAdapter, view, position) -> {
            BottomGridMenuBean item = adapter.getItem(position);
            if (item != null && item.getClickListener() != null) {
                // 如果有自定义点击监听器，则调用它
                item.getClickListener().onClick(item);
            }
        });
        
        // 设置适配器到RecyclerView
        rvMenu.setAdapter(adapter);
    }
    
    /**
     * 创建菜单项列表
     *
     * @return 菜单项列表
     */
    private List<BottomGridMenuBean> createMenuItems() {
        // 如果有自定义菜单项，则使用自定义菜单项
        if (customMenuItems != null && !customMenuItems.isEmpty()) {
            return customMenuItems;
        }
        
        // 没有自定义菜单项时返回空列表
        return new ArrayList<>();
    }
    
    /**
     * Builder类，用于链式调用构建底部表单
     */
    public static class Builder {
        private final List<BottomGridMenuBean> menuItems = new ArrayList<>();
        
        /**
         * 添加菜单项
         *
         * @param icon     图标资源ID
         * @param name     菜单项名称
         * @param listener 点击监听器
         *
         * @return Builder实例
         */
        public Builder addMenuItem(int icon, String name, BottomGridMenuBean.OnItemClickListener listener) {
            menuItems.add(new BottomGridMenuBean(icon, name, listener));
            return this;
        }
        
        /**
         * 构建底部表单实例
         *
         * @return MyBottomSheetDialogFragment实例
         */
        public BottomSheetDialog build() {
            BottomSheetDialog fragment = new BottomSheetDialog();
            if (!menuItems.isEmpty()) {
                fragment.customMenuItems = new ArrayList<>(menuItems);
            }
            return fragment;
        }
    }
}
