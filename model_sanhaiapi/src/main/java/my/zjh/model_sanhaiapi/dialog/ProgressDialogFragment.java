package my.zjh.model_sanhaiapi.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import my.zjh.model_sanhaiapi.R;

/**
 * ProgressDialogFragment
 *
 * @author AHeng
 * @date 2025/04/04 5:42
 */
public class ProgressDialogFragment extends DialogFragment {
    // 标签常量
    public static final String TAG = "ProgressDialogFragment";
    private static final String ARG_MESSAGE = "message";
    
    // 视图绑定
    private View rootView;
    private TextView tvMessage;
    
    // 单例实例
    private static ProgressDialogFragment instance;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用自定义布局
        rootView = inflater.inflate(R.layout.sh_dialog_loading_lottie, container, false);
        tvMessage = rootView.findViewById(R.id.tv_message);
        
        // 设置消息
        if (getArguments() != null) {
            String message = getArguments().getString(ARG_MESSAGE);
            if (message != null && !message.isEmpty()) {
                tvMessage.setText(message);
            }
        }
        
        return rootView;
    }
    
    /**
     * 关闭对话框
     */
    public void dismiss() {
        try {
            if (isAdded() && !requireActivity().isFinishing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置对话框样式
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.sh_AppTheme_Dialog);
        // 设置不可取消
        setCancelable(false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            // 设置背景透明
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            // 设置窗口透明度
            WindowManager.LayoutParams windowParams = window.getAttributes();
            // 设置背景透明度为50%
            windowParams.dimAmount = 0f;
            window.setAttributes(windowParams);
            // 设置动画
            window.setWindowAnimations(R.style.sh_DialogAnimation);
            
            // 设置宽高
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置位置
            window.setGravity(Gravity.CENTER);
            // 设置内边距
            // window.getDecorView().setPadding(50, 50, 50, 50);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 释放资源
        rootView = null;
        tvMessage = null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 清除单例实例引用
        if (instance == this) {
            instance = null;
        }
    }
    
    /**
     * 创建实例
     *
     * @param message 显示的消息
     *
     * @return ProgressDialogFragment实例
     */
    public static ProgressDialogFragment newInstance(String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }
    
    /**
     * 显示加载对话框
     *
     * @param fragmentManager FragmentManager
     * @param message 显示的消息
     */
    public static void showLoading(FragmentManager fragmentManager, String message) {
        if (fragmentManager == null || fragmentManager.isDestroyed() || fragmentManager.isStateSaved()) {
            return;
        }
        
        try {
            // 如果已存在实例，先尝试关闭它
            if (instance != null) {
                instance.dismiss();
                instance = null;
            }
            
            // 创建新实例
            instance = newInstance(message);
            instance.show(fragmentManager, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 隐藏加载对话框
     */
    public static void hideLoading() {
        if (instance != null) {
            instance.dismiss();
            instance = null;
        }
    }
    
    /**
     * 显示对话框
     *
     * @param fragmentManager FragmentManager
     */
    public void show(FragmentManager fragmentManager) {
        // 检查FragmentManager状态
        if (fragmentManager == null || fragmentManager.isDestroyed() || fragmentManager.isStateSaved()) {
            return;
        }
        
        try {
            // 防止重复添加
            if (isAdded() || isVisible()) {
                return;
            }
            
            // 显示对话框
            show(fragmentManager, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
