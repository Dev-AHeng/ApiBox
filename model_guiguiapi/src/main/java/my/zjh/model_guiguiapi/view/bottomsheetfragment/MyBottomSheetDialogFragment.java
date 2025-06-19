package my.zjh.model_guiguiapi.view.bottomsheetfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.view.bottomsheetfragment.adapter.BottomGridMenuAdapter;
import my.zjh.model_guiguiapi.view.bottomsheetfragment.model.BottomGridMenuBean;

/**
 * @author AHeng
 * @date 2025/04/26 4:24
 */
public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    
    /**
     * 标签用于Fragment管理
     */
    public static final String TAG = "MyBottomSheetDialogFragment";
    /**
     * UI元素
     */
    private BottomSheetBehavior<View> behavior;
    private View contentSection;
    /**
     * 拖动条部分
     */
    private BottomSheetDragHandleView dragHandle;
    private RecyclerView rvMenu;
    
    private BottomSheetListener mListener;
    private BottomGridMenuAdapter adapter;
    private List<BottomGridMenuBean> customMenuItems;
    
    private MyBottomSheetDialogFragment() {
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
        initViews(view);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }
    
    @Override
    public int getTheme() {
        return R.style.gg_BottomSheetDialog;
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        // 确保解除对Activity的引用
        mListener = null;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public void onDestroyView() {
        // 清理视图引用，避免内存泄漏
        super.onDestroyView();
        
        if (adapter != null) {
            adapter.release();
            adapter = null;
        }
        
        // 清理点击监听器
        if (dragHandle != null) {
            dragHandle.setOnClickListener(null);
            dragHandle = null;
        }
        
        // 清理RecyclerView
        if (rvMenu != null) {
            rvMenu.setAdapter(null);
            rvMenu = null;
        }
        
        contentSection = null;
        behavior = null;
        customMenuItems = null;
    }
    
    /**
     * 创建实例的静态方法
     *
     * @return this
     */
    public static MyBottomSheetDialogFragment newInstance() {
        return new MyBottomSheetDialogFragment();
    }
    
    /**
     * 创建实例的静态方法，带有自定义菜单项
     *
     * @param menuItems 自定义菜单项列表
     * @return this
     */
    public static MyBottomSheetDialogFragment newInstance(List<BottomGridMenuBean> menuItems) {
        MyBottomSheetDialogFragment fragment = new MyBottomSheetDialogFragment();
        fragment.customMenuItems = menuItems;
        return fragment;
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
     * 初始化视图引用
     *
     * @param rootView 根视图
     */
    private void initViews(View rootView) {
        contentSection = rootView.findViewById(R.id.content_section);
        dragHandle = rootView.findViewById(R.id.drag_handle);
        
        // ConstraintLayout bottomSheet = rootView.findViewById(R.id.drag_handle);
        
        initMenuRecyclerView(rootView);
        
        // 设置拖动条点击事件
        if (dragHandle != null) {
            dragHandle.setOnClickListener(v -> {
                if (behavior != null && behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                }
            });
        }
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
        adapter = new BottomGridMenuAdapter(getContext());
        adapter.submitList(menuItems);
        
        // 设置点击监听
        adapter.setOnItemClickListener((itemAdapter, view, position) -> {
            BottomGridMenuBean item = adapter.getItem(position);
            if (item != null) {
                // 如果有自定义点击监听器，则调用它
                if (item.getClickListener() != null) {
                    item.getClickListener().onClick(item);
                } else {
                    // 若没有自定义点击监听，则执行默认行为
                    if (mListener != null) {
                        mListener.onOptionSelected(item.getItemName());
                    } else {
                        toast("点击了：" + item.getItemName());
                        
                        ARouter.getInstance()
                                .build("/guigui/BrowserActivity")
                                .withString("url", "https://zjh.my")
                                .navigation();
                    }
                }
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
        
        // 默认菜单项
        List<BottomGridMenuBean> gridMenuBeans = new ArrayList<>();
        gridMenuBeans.add(new BottomGridMenuBean(my.zjh.common.R.drawable.twotone_content_copy_24, "复制信息"));
        gridMenuBeans.add(new BottomGridMenuBean(my.zjh.common.R.drawable.main_with_overlay, "内部浏览器"));
        gridMenuBeans.add(new BottomGridMenuBean(my.zjh.common.R.drawable.browser, "外部浏览器"));
        gridMenuBeans.add(new BottomGridMenuBean(my.zjh.common.R.drawable.baseline_share_24, "分享"));
        gridMenuBeans.add(new BottomGridMenuBean(my.zjh.common.R.drawable.baseline_more_horiz_24, "更多"));
        return gridMenuBeans;
    }
    
    /**
     * 设置监听器
     *
     * @param listener 底部表单监听器
     */
    public void setBottomSheetListener(BottomSheetListener listener) {
        this.mListener = listener;
    }
    
    /**
     * 设置自定义菜单项
     *
     * @param menuItems 菜单项列表
     */
    public void setMenuItems(List<BottomGridMenuBean> menuItems) {
        this.customMenuItems = menuItems;
        if (adapter != null && menuItems != null) {
            adapter.submitList(menuItems);
        }
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object content) {
        if (isAdded() && getActivity() != null) {
            Toast.makeText(getContext(), content.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // 用于与Activity通信的接口
    public interface BottomSheetListener {
        void onOptionSelected(String option);
    }
    
    /**
     * Builder类，用于链式调用构建底部表单
     */
    public static class Builder {
        private final List<BottomGridMenuBean> menuItems = new ArrayList<>();
        private BottomSheetListener listener;
        
        /**
         * 添加菜单项
         *
         * @param icon     图标资源ID
         * @param name     菜单项名称
         * @param listener 点击监听器
         * @return Builder实例
         */
        public Builder addMenuItem(int icon, String name, BottomGridMenuBean.OnItemClickListener listener) {
            menuItems.add(new BottomGridMenuBean(icon, name, listener));
            return this;
        }
        
        /**
         * 设置底部表单监听器
         *
         * @param listener 底部表单监听器
         * @return Builder实例
         */
        public Builder setBottomSheetListener(BottomSheetListener listener) {
            this.listener = listener;
            return this;
        }
        
        /**
         * 构建底部表单实例
         *
         * @return MyBottomSheetDialogFragment实例
         */
        public MyBottomSheetDialogFragment build() {
            MyBottomSheetDialogFragment fragment = newInstance(menuItems);
            if (listener != null) {
                fragment.setBottomSheetListener(listener);
            }
            return fragment;
        }
    }
}
