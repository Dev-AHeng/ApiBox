package my.zjh.model_guiguiapi.view.browser.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettingsImpl;
import com.just.agentweb.AgentWebUIControllerImplBase;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.IWebLayout;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;
import com.just.agentweb.PermissionInterceptor;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

/**
 * Created by cenxiaozhong on 2017/7/22.
 * <p>
 * source code  <a href="https://github.com/Justson/AgentWeb">AgentWeb</a>
 */
public abstract class BaseAgentWebActivity extends AppCompatActivity {
    
    protected AgentWeb mAgentWeb;
    private AgentWebUIControllerImplBase mAgentWebUIController;
    private ErrorLayoutEntity mErrorLayoutEntity;
    private MiddlewareWebChromeBase mMiddleWareWebChrome;
    private MiddlewareWebClientBase mMiddleWareWebClient;
    
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        buildAgentWeb();
    }
    
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        buildAgentWeb();
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
    
    protected void buildAgentWeb() {
        ErrorLayoutEntity mErrorLayoutEntity = getErrorLayoutEntity();
        mAgentWeb = AgentWeb.with(this)
                            .setAgentWebParent(getAgentWebParent(), new ViewGroup.LayoutParams(-1, -1))
                            .useDefaultIndicator(getIndicatorColor(), getIndicatorHeight())
                            .setWebChromeClient(getWebChromeClient())
                            .setWebViewClient(getWebViewClient())
                            .setWebView(getWebView())
                            .setPermissionInterceptor(getPermissionInterceptor())
                            .setWebLayout(getWebLayout())
                            .setAgentWebUIController(getAgentWebUIController())
                            .interceptUnkownUrl()
                            .setOpenOtherPageWays(getOpenOtherAppWay())
                            .useMiddlewareWebChrome(getMiddleWareWebChrome())
                            .useMiddlewareWebClient(getMiddleWareWebClient())
                            .setAgentWebWebSettings(getAgentWebSettings())
                            .setMainFrameErrorView(mErrorLayoutEntity.layoutRes, mErrorLayoutEntity.reloadId)
                            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                            .createAgentWeb()
                            .ready()
                            .go(getUrl());
    }
    
    protected ErrorLayoutEntity getErrorLayoutEntity() {
        if (this.mErrorLayoutEntity == null) {
            this.mErrorLayoutEntity = new ErrorLayoutEntity();
        }
        return mErrorLayoutEntity;
    }
    
    protected AgentWeb getAgentWeb() {
        return this.mAgentWeb;
    }
    
    protected String getUrl() {
        return null;
    }
    
    
    public IAgentWebSettings getAgentWebSettings() {
        return AgentWebSettingsImpl.getInstance();
    }
    
    protected abstract ViewGroup getAgentWebParent();
    
    protected WebChromeClient getWebChromeClient() {
        return null;
    }
    
    protected int getIndicatorColor() {
        return -1;
    }
    
    protected int getIndicatorHeight() {
        return -1;
    }
    
    protected WebViewClient getWebViewClient() {
        return null;
    }
    
    protected WebView getWebView() {
        return null;
    }
    
    protected IWebLayout getWebLayout() {
        return null;
    }
    
    protected PermissionInterceptor getPermissionInterceptor() {
        return null;
    }
    
    public AgentWebUIControllerImplBase getAgentWebUIController() {
        return null;
    }
    
    public DefaultWebClient.OpenOtherPageWays getOpenOtherAppWay() {
        return null;
    }
    
    protected MiddlewareWebChromeBase getMiddleWareWebChrome() {
        return this.mMiddleWareWebChrome = new MiddlewareWebChromeBase() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(view, title);
            }
        };
    }
    
    protected void setTitle(WebView view, String title) {
    
    }
    
    protected MiddlewareWebClientBase getMiddleWareWebClient() {
        return this.mMiddleWareWebClient = new MiddlewareWebClientBase() {
        };
    }
    
    protected static class ErrorLayoutEntity {
        private int layoutRes = com.just.agentweb.R.layout.agentweb_error_page;
        private int reloadId;
        
        public void setLayoutRes(int layoutRes) {
            this.layoutRes = layoutRes;
            if (layoutRes <= 0) {
                layoutRes = -1;
            }
        }
        
        public void setReloadId(int reloadId) {
            this.reloadId = reloadId;
            if (reloadId <= 0) {
                reloadId = -1;
            }
        }
    }
}