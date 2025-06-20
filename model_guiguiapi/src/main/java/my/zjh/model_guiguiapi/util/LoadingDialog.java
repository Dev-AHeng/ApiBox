package my.zjh.model_guiguiapi.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import my.zjh.model_guiguiapi.R;

/**
 * 加载中弹窗
 *
 * @author AHeng
 * @date 2025/05/07 10:36
 */
public class LoadingDialog extends DialogFragmentUtils.BaseDialogFragment {
    /**
     * 在创建视图时调用
     *
     * @param inflater           布局填充器
     * @param container          父容器
     * @param savedInstanceState 保存的实例状态
     *
     * @return 返回加载中弹窗的视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.gg_dialog_loading, container, false);
    }
    
    @Override
    public void onDetach() {
        // 确保彻底清理资源
        releaseResources();
        super.onDetach();
    }
}
