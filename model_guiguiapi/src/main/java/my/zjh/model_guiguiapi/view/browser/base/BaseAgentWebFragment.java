package my.zjh.model_guiguiapi.view.browser.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
 * AgentWeb封装基础Fragment
 * 来源: <a href="https://github.com/Justson/AgentWeb">AgentWeb</a>
 */
public abstract class BaseAgentWebFragment extends Fragment {
    
    protected AgentWeb mAgentWeb;
    private MiddlewareWebChromeBase mMiddleWareWebChrome;
    private MiddlewareWebClientBase mMiddleWareWebClient;
    private ErrorLayoutEntity mErrorLayoutEntity;
    private AgentWebUIControllerImplBase mAgentWebUIController;
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ErrorLayoutEntity errorLayoutEntity = getErrorLayoutEntity();
        mAgentWeb = AgentWeb.with(this)
                            .setAgentWebParent(getAgentWebParent(), new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT))
                            .useDefaultIndicator(getIndicatorColor(), getIndicatorHeight())
                            .setWebView(getWebView())
                            .setWebLayout(getWebLayout())
                            .setAgentWebWebSettings(getAgentWebSettings())
                            .setWebViewClient(getWebViewClient())
                            .setPermissionInterceptor(getPermissionInterceptor())
                            .setWebChromeClient(getWebChromeClient())
                            .interceptUnkownUrl()
                            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                            .setAgentWebUIController(getAgentWebUIController())
                            .setMainFrameErrorView(errorLayoutEntity.layoutRes, errorLayoutEntity.reloadId)
                            .useMiddlewareWebChrome(getMiddleWareWebChrome())
                            .useMiddlewareWebClient(getMiddleWareWebClient())
                            .createAgentWeb()
                            .ready()
                            .go(getUrl());
    }
    
    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }
    
    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }
    
    /**
     * 设置标题
     *
     * @param view  WebView实例
     * @param title 页面标题
     */
    protected void setTitle(WebView view, String title) {
        // 子类实现标题设置
    }
    
    /**
     * 获取错误布局实体
     *
     * @return 错误布局实体
     */
    @NonNull
    protected ErrorLayoutEntity getErrorLayoutEntity() {
        if (mErrorLayoutEntity == null) {
            mErrorLayoutEntity = new ErrorLayoutEntity();
        }
        return mErrorLayoutEntity;
    }
    
    /**
     * 获取AgentWeb UI控制器
     *
     * @return AgentWeb UI控制器
     */
    @Nullable
    protected AgentWebUIControllerImplBase getAgentWebUIController() {
        return mAgentWebUIController;
    }
    
    /**
     * 获取加载的URL
     *
     * @return 默认返回空字符串
     */
    @Nullable
    protected String getUrl() {
        return "";
    }
    
    /**
     * 获取AgentWeb设置
     *
     * @return AgentWeb设置
     */
    @Nullable
    protected IAgentWebSettings getAgentWebSettings() {
        return AgentWebSettingsImpl.getInstance();
    }
    
    /**
     * 获取WebChromeClient
     *
     * @return WebChromeClient实例
     */
    @Nullable
    protected WebChromeClient getWebChromeClient() {
        return null;
    }
    
    /**
     * 获取AgentWeb的父容器
     *
     * @return ViewGroup容器
     */
    @NonNull
    protected abstract ViewGroup getAgentWebParent();
    
    /**
     * 获取进度条颜色
     *
     * @return 进度条颜色值
     */
    @ColorInt
    protected int getIndicatorColor() {
        return -1;
    }
    
    /**
     * 获取进度条高度
     *
     * @return 进度条高度
     */
    protected int getIndicatorHeight() {
        return -1;
    }
    
    /**
     * 获取WebViewClient
     *
     * @return WebViewClient实例
     */
    @Nullable
    protected WebViewClient getWebViewClient() {
        return null;
    }
    
    /**
     * 获取WebView
     *
     * @return WebView实例
     */
    @Nullable
    protected WebView getWebView() {
        return null;
    }
    
    /**
     * 获取Web布局
     *
     * @return IWebLayout实例
     */
    @Nullable
    protected IWebLayout getWebLayout() {
        return null;
    }
    
    /**
     * 获取权限拦截器
     *
     * @return 权限拦截器
     */
    @Nullable
    protected PermissionInterceptor getPermissionInterceptor() {
        return null;
    }
    
    /**
     * 获取中间件WebChrome
     *
     * @return 中间件WebChrome
     */
    @NonNull
    protected MiddlewareWebChromeBase getMiddleWareWebChrome() {
        return this.mMiddleWareWebChrome = new MiddlewareWebChromeBase() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(view, title);
            }
        };
    }
    
    /**
     * 获取中间件WebClient
     *
     * @return 中间件WebClient
     */
    @NonNull
    protected MiddlewareWebClientBase getMiddleWareWebClient() {
        return this.mMiddleWareWebClient = new MiddlewareWebClientBase() {
        };
    }
    
    /**
     * 错误布局实体类
     */
    protected static class ErrorLayoutEntity {
        private int layoutRes = com.just.agentweb.R.layout.agentweb_error_page;
        private int reloadId;
        
        public void setLayoutRes(int layoutRes) {
            if (layoutRes > 0) {
                this.layoutRes = layoutRes;
            } else {
                this.layoutRes = -1;
            }
        }
        
        public void setReloadId(int reloadId) {
            if (reloadId > 0) {
                this.reloadId = reloadId;
            } else {
                this.reloadId = -1;
            }
        }
    }
}