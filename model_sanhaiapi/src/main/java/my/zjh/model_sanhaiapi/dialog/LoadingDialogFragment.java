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
 * 加载对话框Fragment
 *
 * @author AHeng
 * @date 2025/03/25
 */
public class LoadingDialogFragment extends DialogFragment {
    
    // 标签常量
    private static final String TAG = "LoadingDialogFragment";
    private static final String ARG_MESSAGE = "message";
    
    private static LoadingDialogFragment instance;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.sh_AppTheme_Dialog);
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        
        // 配置窗口
        if (dialog.getWindow() != null) {
            // 去除默认标题
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            
            // 透明背景
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            // 设置窗口属性
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            // 背景暗度 50%
            params.dimAmount = 0.5f;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            
            // 设置状态栏和导航栏为半透明
            // 移除全屏标志
            params.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            // 半透明状态栏
            params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 半透明导航栏
            params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        
        // 使用对话框布局
        dialog.setCanceledOnTouchOutside(false);
        
        return dialog;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        if (getDialog() != null && getDialog().getWindow() != null) {
            // 修改对话框尺寸为包裹内容
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
            
            // 透明背景
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 直接使用已有的带圆角卡片的布局
        return inflater.inflate(R.layout.sh_dialog_loading, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置消息
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : null;
        if (message != null) {
            TextView messageView = view.findViewById(R.id.tv_message);
            messageView.setText(message);
        }
    }
    
    public static LoadingDialogFragment newInstance(String message) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }
    
    /**
     * 显示加载对话框
     *
     * @param fragmentManager Fragment管理器
     * @param message         显示消息
     */
    public static void showLoading(FragmentManager fragmentManager, String message) {
        // 如果已经有一个实例在显示，更新其消息
        if (instance != null && instance.isAdded()) {
            instance.updateMessage(message);
            return;
        }
        
        // 创建并显示新的对话框
        instance = newInstance(message);
        instance.setCancelable(false);
        instance.show(fragmentManager, TAG);
    }
    
    /**
     * 隐藏加载对话框
     */
    public static void dismiss(FragmentManager fragmentManager) {
        if (instance != null) {
            try {
                instance.dismissAllowingStateLoss();
                instance = null;
            } catch (Exception e) {
                // 忽略可能的异常
            }
        }
    }
    
    /**
     * 更新消息
     */
    public void updateMessage(String message) {
        if (getView() != null) {
            TextView messageView = getView().findViewById(R.id.tv_message);
            if (messageView != null) {
                messageView.setText(message);
            }
        }
    }
} 