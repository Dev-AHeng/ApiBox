package my.zjh.model_guiguiapi.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;

///////////////////////////////////////////////自定义
// 创建自定义DialogFragment
// public class MyDialog extends DialogFragmentUtils.BaseDialogFragment {
//     @Override
//     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//         super.onCreateView(inflater, container, savedInstanceState);
//         // 返回你的自定义视图
//         return inflater.inflate(R.layout.your_dialog_layout, container, false);
//     }
// }
//
//    // 在Activity中显示
//    MyDialog dialog = new MyDialog();
//    DialogFragmentUtils.showDialogFragment(activity, dialog, "my_dialog_tag");
//
//    // 在Fragment中显示
//    DialogFragmentUtils.showDialogFragment(fragment, dialog, "my_dialog_tag");
//
//    // 关闭对话框
//    DialogFragmentUtils.dismissDialogFragment(activity, "my_dialog_tag");
//////////////////////////////////////////////

/**
 * DialogFragment工具类
 * 提供健壮的DialogFragment处理功能，包含显示、隐藏、销毁等管理
 * 防止内存泄漏，处理各种异常情况
 *
 * @author AHeng
 * @date 2025/05/07 10:29
 */
public class DialogFragmentUtils {
    
    /**
     * 安全地显示DialogFragment
     *
     * @param activity       宿主Activity
     * @param dialogFragment 要显示的DialogFragment
     * @param tag            标签，用于之后查找
     *
     * @return 是否成功显示
     * <p>
     * 使用示例：
     * <pre>
     * MyDialogFragment dialog = new MyDialogFragment();
     * DialogFragmentUtils.showDialogFragment(this, dialog, "dialog_tag");
     * </pre>
     */
    public static boolean showDialogFragment(@NonNull FragmentActivity activity, @NonNull DialogFragment dialogFragment, @NonNull String tag) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return false;
        }
        
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            // 检查是否已经添加过
            Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
            if (existingFragment != null) {
                if (existingFragment instanceof DialogFragment) {
                    ((DialogFragment) existingFragment).dismissAllowingStateLoss();
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(existingFragment);
                transaction.commitAllowingStateLoss();
                // 确保事务完成
                fragmentManager.executePendingTransactions();
            }
            
            // 防止对话框状态异常
            if (dialogFragment.isAdded()) {
                return false;
            }
            
            // 使用commitAllowingStateLoss替代show方法
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(dialogFragment, tag);
            ft.commitAllowingStateLoss();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 安全地显示DialogFragment在Fragment中
     *
     * @param fragment       宿主Fragment
     * @param dialogFragment 要显示的DialogFragment
     * @param tag            标签，用于之后查找
     *
     * @return 是否成功显示
     * <p>
     * 使用示例：
     * <pre>
     * MyDialogFragment dialog = new MyDialogFragment();
     * DialogFragmentUtils.showDialogFragment(this, dialog, "child_dialog_tag");
     * </pre>
     */
    public static boolean showDialogFragment(@NonNull Fragment fragment, @NonNull DialogFragment dialogFragment, @NonNull String tag) {
        if (fragment.getActivity() == null || fragment.isDetached() || fragment.getActivity().isFinishing() || fragment.getActivity().isDestroyed()) {
            return false;
        }
        
        try {
            FragmentManager fragmentManager = fragment.getChildFragmentManager();
            // 检查是否已经添加过
            Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
            if (existingFragment != null) {
                if (existingFragment instanceof DialogFragment) {
                    ((DialogFragment) existingFragment).dismissAllowingStateLoss();
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(existingFragment);
                transaction.commitAllowingStateLoss();
            }
            
            dialogFragment.show(fragmentManager, tag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 安全地关闭DialogFragment
     *
     * @param activity 宿主Activity
     * @param tag      DialogFragment的标签
     *
     * @return 是否成功关闭
     * <p>
     * 使用示例：
     * <pre>
     * DialogFragmentUtils.dismissDialogFragment(this, "dialog_tag");
     * </pre>
     */
    public static boolean dismissDialogFragment(@NonNull FragmentActivity activity, @NonNull String tag) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return false;
        }
        
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).dismissAllowingStateLoss();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 安全地关闭DialogFragment在Fragment中
     *
     * @param fragment 宿主Fragment
     * @param tag      DialogFragment的标签
     *
     * @return 是否成功关闭
     * <p>
     * 使用示例：
     * <pre>
     * DialogFragmentUtils.dismissDialogFragment(this, "child_dialog_tag");
     * </pre>
     */
    public static boolean dismissDialogFragment(@NonNull Fragment fragment, @NonNull String tag) {
        if (fragment.getActivity() == null || fragment.isDetached() || fragment.getActivity().isFinishing() || fragment.getActivity().isDestroyed()) {
            return false;
        }
        
        try {
            FragmentManager fragmentManager = fragment.getChildFragmentManager();
            Fragment dialogFragment = fragmentManager.findFragmentByTag(tag);
            if (dialogFragment instanceof DialogFragment) {
                ((DialogFragment) dialogFragment).dismissAllowingStateLoss();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 基础DialogFragment类，默认使用Material Design风格
     * 提供常用功能和内存泄漏防护
     * <p>
     * 使用示例：
     * <pre>
     * public class MyDialog extends DialogFragmentUtils.BaseDialogFragment {
     *     @Override
     *     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     *         super.onCreateView(inflater, container, savedInstanceState);
     *         View view = inflater.inflate(R.layout.my_dialog_layout, container, false);
     *         // 初始化view
     *         return view;
     *     }
     *
     *     @Override
     *     protected int getDialogWidth() {
     *         return (int)(ScreenUtils.getScreenWidth() * 0.8f); // 设置为屏幕宽度的80%
     *     }
     * }
     * </pre>
     *
     * @author AHeng
     * @date 2025/05/07 10:29
     */
    public static abstract class BaseDialogFragment extends DialogFragment {
        private WeakReference<Activity> activityWeakReference;
        private WeakReference<Context> contextWeakReference;
        private boolean canCancel = true;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            contextWeakReference = new WeakReference<>(context);
            if (context instanceof Activity) {
                activityWeakReference = new WeakReference<>((Activity) context);
            }
        }
        
        @Override
        public void onDetach() {
            super.onDetach();
            // 解除引用
            releaseResources();
        }
        
        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            super.onDismiss(dialog);
            // 释放资源
            releaseResources();
        }
        
        @Override
        public void onStart() {
            super.onStart();
            // 设置背景透明
            if (getDialog() != null && getDialog().getWindow() != null) {
                Window window = getDialog().getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                // 设置宽度为屏幕宽度的比例
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = getDialogWidth();
                layoutParams.height = getDialogHeight();
                layoutParams.dimAmount = getDimAmount();
                window.setAttributes(layoutParams);
            }
            // 设置可取消状态
            if (getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(canCancel);
                setCancelable(canCancel);
            }
        }
        
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // 移除标题栏
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        
        /**
         * 设置是否可取消
         * <p>
         * 使用示例：
         * <pre>
         * MyDialog dialog = new MyDialog();
         * dialog.setCanCancel(false); // 设置为不可取消
         * </pre>
         */
        public void setCanCancel(boolean canCancel) {
            this.canCancel = canCancel;
            if (getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(canCancel);
                setCancelable(canCancel);
            }
        }
        
        /**
         * 获取绑定的Activity，可能为null
         * <p>
         * 使用示例：
         * <pre>
         * Activity activity = getActivitySafely();
         * if (activity != null) {
         *     // 安全地使用activity
         * }
         * </pre>
         */
        @Nullable
        protected Activity getActivitySafely() {
            if (activityWeakReference != null) {
                Activity activity = activityWeakReference.get();
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    return activity;
                }
            }
            return null;
        }
        
        /**
         * 获取Context，可能为null
         * <p>
         * 使用示例：
         * <pre>
         * Context context = getContextSafely();
         * if (context != null) {
         *     // 安全地使用context
         * }
         * </pre>
         */
        @Nullable
        protected Context getContextSafely() {
            if (contextWeakReference != null) {
                return contextWeakReference.get();
            }
            return null;
        }
        
        /**
         * 获取对话框宽度，默认为MATCH_PARENT
         * <p>
         * 使用示例：
         * <pre>
         * @Override
         * protected int getDialogWidth() {
         *     return (int)(ScreenUtils.getScreenWidth() * 0.9f); // 设置为屏幕宽度的90%
         * }
         * </pre>
         */
        protected int getDialogWidth() {
            return WindowManager.LayoutParams.WRAP_CONTENT;
        }
        
        /**
         * 获取对话框高度，默认为WRAP_CONTENT
         * <p>
         * 使用示例：
         * <pre>
         * @Override
         * protected int getDialogHeight() {
         *     return (int)(ScreenUtils.getScreenHeight() * 0.5f); // 设置为屏幕高度的50%
         * }
         * </pre>
         */
        protected int getDialogHeight() {
            return WindowManager.LayoutParams.WRAP_CONTENT;
        }
        
        /**
         * 获取背景暗度，默认0.5f
         * <p>
         * 使用示例：
         * <pre>
         * @Override
         * protected float getDimAmount() {
         *     return 0.8f; // 设置背景更暗
         * }
         * </pre>
         */
        protected float getDimAmount() {
            return 0.5f;
        }
        
        /**
         * 释放资源
         */
        protected void releaseResources() {
            if (activityWeakReference != null) {
                activityWeakReference.clear();
                activityWeakReference = null;
            }
            if (contextWeakReference != null) {
                contextWeakReference.clear();
                contextWeakReference = null;
            }
        }
    }
} 