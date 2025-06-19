package my.zjh.box;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import my.zjh.box.databinding.Md3DialogLayoutBinding;

/**
 * Material Design 3 对话框辅助类
 */
public class MD3DialogHelper {

    /**
     * 创建一个Material Design 3风格的对话框
     *
     * @param context 上下文
     * @param title 对话框标题
     * @param content 对话框内容
     * @param confirmText 确认按钮文本
     * @param cancelText 取消按钮文本
     * @param onConfirmListener 确认按钮点击监听器
     * @param onCancelListener 取消按钮点击监听器
     * @return 创建的Dialog对象
     */
    public static Dialog createMD3Dialog(
            Context context,
            String title,
            String content,
            String confirmText,
            String cancelText,
            View.OnClickListener onConfirmListener,
            View.OnClickListener onCancelListener) {

        // 创建自定义样式的Dialog
        Dialog dialog = new Dialog(context);
        
        // 去除默认标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // 使用ViewBinding加载布局
        Md3DialogLayoutBinding binding = Md3DialogLayoutBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        
        // 设置标题和内容
        TextView titleTextView = binding.dialogTitle;
        TextView contentTextView = binding.dialogContent;
        MaterialButton confirmButton = binding.dialogConfirmButton;
        MaterialButton cancelButton = binding.dialogCancelButton;
        
        // 设置文本内容
        titleTextView.setText(title);
        contentTextView.setText(content);
        
        // 设置按钮文本
        if (confirmText != null && !confirmText.isEmpty()) {
            confirmButton.setText(confirmText);
        }
        
        if (cancelText != null && !cancelText.isEmpty()) {
            cancelButton.setText(cancelText);
        }
        
        // 设置按钮点击事件
        confirmButton.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onClick(v);
            }
            dialog.dismiss();
        });
        
        cancelButton.setOnClickListener(v -> {
            if (onCancelListener != null) {
                onCancelListener.onClick(v);
            }
            dialog.dismiss();
        });
        
        // 设置对话框宽度为屏幕宽度的90%
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        return dialog;
    }

    /**
     * 创建一个简化版的MD3对话框（只有确认按钮）
     */
    public static Dialog createSimpleMD3Dialog(
            Context context,
            String title,
            String content,
            String confirmText,
            View.OnClickListener onConfirmListener) {
        
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Md3DialogLayoutBinding binding = Md3DialogLayoutBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        
        binding.dialogTitle.setText(title);
        binding.dialogContent.setText(content);
        
        if (confirmText != null && !confirmText.isEmpty()) {
            binding.dialogConfirmButton.setText(confirmText);
        }
        
        // 隐藏取消按钮
        binding.dialogCancelButton.setVisibility(View.GONE);
        
        // 设置确认按钮点击事件
        binding.dialogConfirmButton.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onClick(v);
            }
            dialog.dismiss();
        });
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        return dialog;
    }
    
} 