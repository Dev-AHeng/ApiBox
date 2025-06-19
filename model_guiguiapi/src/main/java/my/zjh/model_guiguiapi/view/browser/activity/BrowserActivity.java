package my.zjh.model_guiguiapi.view.browser.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.just.agentweb.AgentWeb;

import my.zjh.common.BaseActivity;
import my.zjh.model_guiguiapi.databinding.GgBrowserBinding;

/**
 * AgentWeb浏览器
 *
 * @author AHeng
 * @date 2025/04/30 10:06
 */
@Route(path = "/guigui/BrowserActivity")
public class BrowserActivity extends BaseActivity {
    /**
     * 变量名必须与发送方的key一致
     */
    @Autowired
    String url;
    private AgentWeb mAgentWeb;
    private my.zjh.model_guiguiapi.databinding.GgBrowserBinding binding1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding1 = GgBrowserBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding1.ggToolbarLayout.toolbar, "内置浏览器", binding1.ggToolbarLayout.appbar, true);
        
        ARouter.getInstance().inject(this);
        
        toast(url);
        
        mAgentWeb = AgentWeb.with(this)
                            .setAgentWebParent((FrameLayout) binding1.agentwebParent, new FrameLayout.LayoutParams(-1, -1))
                            .useDefaultIndicator()
                            .createAgentWeb()
                            .ready()
                            .go(url);
    }
    
    @Override
    protected void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
        
    }
    
    @Override
    protected void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
}