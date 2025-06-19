package my.zjh.model_guiguiapi.view.hotsearch.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.hjq.toast.Toaster;

import org.jetbrains.annotations.Nullable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.liam.support.SupportFragment;
import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.databinding.GgFragmentHotsearchlsitBinding;
import my.zjh.model_guiguiapi.util.ClickUtils;
import my.zjh.model_guiguiapi.util.ClipboardUtils;
import my.zjh.model_guiguiapi.util.DrawableUtils;
import my.zjh.model_guiguiapi.util.TimeUtils;
import my.zjh.model_guiguiapi.view.bottomsheetfragment2.BottomSheetDialog;
import my.zjh.model_guiguiapi.view.hotsearch.adapter.DifferAdapter;
import my.zjh.model_guiguiapi.view.hotsearch.api.HotSearchService;
import my.zjh.model_guiguiapi.view.hotsearch.api.RetrofitClient;
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchItem;
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchList;

/**
 * 创建一个包含RecyclerView的Fragment类 (懒加载)
 *
 * @author AHeng
 * @date 2025/04/14 05:49
 * @status ok
 */
public class HotSearchListFragment extends SupportFragment {
    private static final String TAG = HotSearchListFragment.class.getSimpleName();
    private static final String ARG_TYPE = "arg_type";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final DifferAdapter adapter = new DifferAdapter();
    private HotSearchService hotSearchService;
    private String type;
    private GgFragmentHotsearchlsitBinding binding;
    private LottieAnimationView lottieListLoading, lottieListError;
    private boolean isFirstLoad = true;
    private HotSearchItem hotSearchItem;
    
    /**
     * 空的构造方法，必须提供
     */
    private HotSearchListFragment() {
    }
    
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GgFragmentHotsearchlsitBinding.inflate(inflater, container, false);
        
        // 先拿到type
        initData();
        
        initView();
        
        return binding.getRoot();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }
    
    /**
     * 懒加载
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onLazyInit(Bundle savedInstanceState) {
        super.onLazyInit(savedInstanceState);
        
        if (isFirstLoad) {
            loadData();
            isFirstLoad = false;
            
            Toaster.debugShow("onLazyInit");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        
        // 在Activity销毁时释放资源
        if (lottieListLoading != null) {
            lottieListLoading.cancelAnimation();
            lottieListLoading.setImageDrawable(null);
        }
        
        // 处置 CompositeDisposable
        // 先清除所有订阅
        compositeDisposable.clear();
        compositeDisposable.dispose();
        
        binding = null;
    }
    
    /**
     * 创建HotSearchListFragment的新实例
     *
     * @param type 热搜类型
     *
     * @return 新的HotSearchListFragment实例
     */
    public static HotSearchListFragment newInstance(String type) {
        HotSearchListFragment fragment = new HotSearchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
    
    private void loadData() {
        // 使用RxJava链式调用加载热搜数据
        compositeDisposable.add(
                hotSearchService.getHotlist(type)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::requestStart)
                        .doFinally(this::requestEnd)
                        .subscribe(this::requestSuccessful, this::requestFailure)
        );
    }
    
    /**
     * 请求开始
     *
     * @param disposable 订阅
     */
    private void requestStart(Disposable disposable) {
        Log.i(TAG, "lazyLoad-开始加载网络数据-开始");
        
        if (lottieListLoading != null) {
            // 动态设置动画
            lottieListLoading.setAnimation(my.zjh.common.R.raw.list_loading);
            // 开始播放动画
            lottieListLoading.playAnimation();
        }
    }
    
    /**
     * 请求结束 无论成功失败
     */
    private void requestEnd() {
        binding.swipeRefreshLayout.setRefreshing(false);
        
        if (lottieListLoading != null) {
            // 停止动画
            lottieListLoading.cancelAnimation();
            // 释放动画资源
            lottieListLoading.setImageDrawable(null);
        }
        
        Log.i(TAG, "lazyLoad-开始加载网络数据-结束");
    }
    
    /**
     * 请求成功
     *
     * @param hotSearchList 热搜列表
     */
    @SuppressLint("SetTextI18n")
    private void requestSuccessful(HotSearchList hotSearchList) {
        Log.i(TAG, hotSearchList.getTitle() + ": lazyLoad-开始加载网络数据-成功");
        
        if (lottieListError != null) {
            // 停止动画
            lottieListError.cancelAnimation();
            // 释放动画资源
            lottieListError.setImageDrawable(null);
        }
        
        // 设置图标
        type = TextUtils.equals(type, "51cto") ? "wycto" : type;
        Drawable vectorDrawable = DrawableUtils.getDrawableByName(requireActivity(), type);
        binding.company.setCompoundDrawablesRelative(vectorDrawable, null, null, null);
        // 设置标题
        binding.company.setText(hotSearchList.getTitle());
        // 设置副标题
        binding.hotListNaem.setText(hotSearchList.getSubtitle());
        // 友好的时间间隔显示
        binding.time.setText(TimeUtils.getFriendlyTimeSpanByNow(hotSearchList.getUpdateTime()));
        // 设置总条数
        binding.total.setText(hotSearchList.getTotal() + "条数据");
        
        // 设置数据
        adapter.submitList(hotSearchList.getData());
    }
    
    /**
     * 请求失败
     *
     * @param throwable 异常
     */
    private void requestFailure(Throwable throwable) {
        Toaster.debugShow("加载失败：" + throwable.getMessage());
        
        // RecyclerView加载失败lottie动画
        adapter.setStateViewLayout(requireContext(), R.layout.gg_error_view);
        if (adapter.getStateView() != null) {
            lottieListError = adapter.getStateView().findViewById(R.id.lottie_list_error);
            // 动态设置动画
            lottieListError.setAnimation(my.zjh.common.R.raw.list_error);
            // 开始播放动画
            lottieListError.playAnimation();
        }
    }
    
    private void initView() {
        // 设置RecyclerView的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.hotSearchRecyclerView.setLayoutManager(layoutManager);
        
        // 设置RecyclerView的适配器
        binding.hotSearchRecyclerView.setAdapter(adapter);
        // 打开空布局功能, 当RecyclerView没有数据时显示
        adapter.setStateViewEnable(true);
        // 设置空布局, 当RecyclerView没有数据时显示
        adapter.setStateViewLayout(requireContext(), R.layout.gg_loading_view);
        if (adapter.getStateView() != null) {
            lottieListLoading = adapter.getStateView().findViewById(R.id.lottie_list_loading);
        }
        // 显示动画, 渐显AlphaIn, 从左往右SlideInLeft
        adapter.setItemAnimation(BaseQuickAdapter.AnimationType.AlphaIn);
        // 是否是首次显示时候加载动画 默认true
        // adapter.setAnimationFirstOnly(false);
        
        
        // 设置RecyclerView的点击事件
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            // 使用防抖功能，防止快速点击
            if (!ClickUtils.isFastClick()) {
                // toast(baseQuickAdapter.getItems().get(i));
                
                hotSearchItem = baseQuickAdapter.getItems().get(i);
                
                // 使用Builder模式创建底部表单
                BottomSheetDialog bottomSheet
                        = BottomSheetDialog.builder()
                                  .addMenuItem(my.zjh.common.R.drawable.twotone_content_copy_24, "复制信息", item -> {
                                      ClipboardUtils.copyText(hotSearchItem.toString());
                                      Toaster.debugShow("已复制到剪贴板");
                                  })
                                  .addMenuItem(my.zjh.common.R.drawable.main_with_overlay, "内部浏览器", item -> {
                                      // 打开内部浏览器
                                      ARouter.getInstance()
                                              .build("/guigui/BrowserActivity")
                                              .withString("url", hotSearchItem.getUrl())
                                              .navigation();
                                  })
                                  .addMenuItem(my.zjh.common.R.drawable.browser, "外部浏览器", item -> {
                                      // 打开外部浏览器
                                      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hotSearchItem.getUrl()));
                                      startActivity(intent);
                                  })
                                  .addMenuItem(my.zjh.common.R.drawable.baseline_share_24, "分享", item -> {
                                      // 分享功能
                                      Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                      shareIntent.setType("text/plain");
                                      shareIntent.putExtra(Intent.EXTRA_TEXT, hotSearchItem.toString());
                                      startActivity(Intent.createChooser(shareIntent, "分享到"));
                                  })
                                  .addMenuItem(my.zjh.common.R.drawable.baseline_more_horiz_24, "更多", item -> {
                                  
                                  })
                                  .build();
                // 显示底部表单
                bottomSheet.show(getChildFragmentManager(), BottomSheetDialog.TAG);
                
                
            }
        });
        
        // 调整灵敏度参数
        // 设置触发刷新的距离阈值(单位:dp)
        binding.swipeRefreshLayout.setDistanceToTriggerSync(400);
        // 设置刷新指示器大小
        // binding.swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        // 设置进度条偏移量
        // binding.swipeRefreshLayout.setProgressViewOffset(false, 0, 200);
        // 设置下拉刷新监听
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
        
    }
    
    private void initData() {
        hotSearchService = RetrofitClient.getInstance().getHotSearchService();
        
        Bundle args = getArguments();
        if (args != null) {
            type = args.getString(ARG_TYPE);
        }
        
        Log.i(TAG, type + ": onCreateView");
    }
    
    
}
