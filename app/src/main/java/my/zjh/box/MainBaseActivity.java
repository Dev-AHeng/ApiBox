package my.zjh.box;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import my.zjh.common.BaseActivity;

/**
 * 鬼鬼接口的BaseActivity
 *
 * @author AHeng
 * @date 2025/05/03 0:41
 */
public abstract class MainBaseActivity<Binding extends ViewBinding> extends BaseActivity {
    
    /**
     * 定义绑定视图对象
     */
    protected Binding binding;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ARouter.getInstance().inject(this);
        
        EdgeToEdge.enable(this);
        
        binding = inflateBinding();
        
        setContentView(binding.getRoot());
        
        initToolbar();
        
        initView();
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源，防止内存泄漏
        binding = null;
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
     * 初始化Toolbar，子类可以覆盖此方法自定义Toolbar行为
     */
    protected void initToolbar() {
        Toolbar toolbar = binding.getRoot().findViewById(R.id.topAppBar);
        AppBarLayout appBarLayout = binding.getRoot().findViewById(R.id.appBarLayout);
        if (toolbar != null && appBarLayout != null) {
            setupToolbar(toolbar, setTitle() != null ? setTitle() : "空null", appBarLayout, true);
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
    
}
