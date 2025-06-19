package my.zjh.model_guiguiapi.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hjq.toast.Toaster;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import my.zjh.model_guiguiapi.R;

/**
 * 鬼鬼接口的BaseActivity
 *
 * @author AHeng
 * @date 2025/05/03 0:41
 */
public abstract class GuiBaseActivity<Binding extends ViewBinding, VM extends ViewModel> extends AppCompatActivity {
    
    /**
     * 定义绑定视图对象
     */
    protected Binding binding;
    
    /**
     * 定义ViewModel对象
     */
    protected VM viewModel;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = inflateBinding();
        
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 状态栏, 底部导航栏沉浸式
        EdgeToEdge.enable(this);
        
        setStatusBar();
        
        initToolbar();
        
        viewModel = getViewModel();
        
        initView();
        
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源，防止内存泄漏
        binding = null;
        viewModel = null;
    }
    
    /**
     * 内部log
     *
     * @param message 内容
     */
    private static void log(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 需要根据你的调用层级调整这个值
        // 通常为4: 0-getStackTrace, 1-printLog, 2-d/e, 3-调用处
        StackTraceElement element = stackTrace[4];
        String location = element.getFileName() + ":" + element.getLineNumber();
        
        // Android Studio可识别的格式
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n" + location + message);
    }
    
    /**
     * 打印, 供外部使用
     *
     * @param values 内容
     */
    public static void log(Object... values) {
        if (values.length == 0) {
            return;
        }
        
        StringBuilder logMessage = new StringBuilder();
        
        // 添加参数值
        for (int count = 0; count < values.length; count++) {
            logMessage.append("\n┌──────────parameter").append(count + 1).append("────────────────────────\n│ ").append(values[count]);
            // 确保最后有一个分割线
            logMessage.append("\n└──────────────────────────────────────────");
        }
        
        logMessage.append("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        
        log(logMessage.toString());
    }
    
    /**
     * 状态栏, 底部导航栏沉浸式
     */
    private void setStatusBar() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, 0, 0, systemBars.bottom);
                return insets;
            });
        }
    }
    
    /**
     * 初始化Toolbar
     */
    protected void initToolbar() {
        View toolbarLayout = binding.getRoot().findViewById(R.id.gg_toolbar_layout);
        if (toolbarLayout != null) {
            Toolbar toolbar = toolbarLayout.findViewById(my.zjh.common.R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(setTitle() != null ? setTitle() : "空null");
                setSupportActionBar(toolbar);
            }
        }
    }
    
    /**
     * 通过反射自动获取ViewBinding类并调用inflate方法
     *
     * @return 返回具体的ViewBinding对象
     */
    @SuppressWarnings("unchecked")
    private Binding inflateBinding() {
        try {
            // 获取泛型参数类型
            Type superClass = getClass().getGenericSuperclass();
            Type type = null;
            if (superClass != null) {
                type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            }
            Class<Binding> clazz = (Class<Binding>) type;
            
            // 获取inflate方法并调用
            Method inflateMethod = clazz.getMethod("inflate", LayoutInflater.class);
            return (Binding) inflateMethod.invoke(null, getLayoutInflater());
        } catch (Exception e) {
            throw new RuntimeException("ViewBinding初始化失败", e);
        }
    }
    
    /**
     * 通过反射自动获取ViewModel类并实例化
     *
     * @return 返回具体的ViewModel对象
     */
    @SuppressWarnings("unchecked")
    private VM getViewModel() {
        try {
            // 获取泛型参数类型
            Type superClass = getClass().getGenericSuperclass();
            Type type = null;
            if (superClass != null) {
                type = ((ParameterizedType) superClass).getActualTypeArguments()[1]; // VM是第二个泛型参数
            }
            Class<VM> viewModelClass = (Class<VM>) type;
            
            // 使用ViewModelProvider创建ViewModel实例
            return new ViewModelProvider(this).get(viewModelClass);
        } catch (Exception e) {
            throw new RuntimeException("ViewModel初始化失败", e);
        }
    }
    
    /**
     * 设置Activity的标题
     *
     * @return Activity的标题
     */
    protected abstract CharSequence setTitle();
    
    /**
     * 初始化控件
     */
    protected abstract void initView();
    
    /**
     * Toast
     *
     * @param showContents 提示内容
     */
    public void toast(Object... showContents) {
        if (showContents == null || showContents.length == 0) {
            return;
        }
        
        StringBuilder toastContent = new StringBuilder();
        for (int i = 0; i < showContents.length; i++) {
            if (showContents[i] != null) {
                toastContent.append(showContents[i].toString());
            }
            // 添加分隔符，除了最后一个元素
            if (i < showContents.length - 1) {
                toastContent.append("\n");
            }
        }
        
        // 使用 runOnUiThread 确保在主线程中更新 UI
        // runOnUiThread(() -> Toast.makeText(getApplicationContext(), toastContent.toString(), Toast.LENGTH_SHORT).show());
        
        Toaster.debugShow(toastContent.toString());
    }
    
    
}
