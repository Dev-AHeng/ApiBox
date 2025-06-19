package my.zjh.model_guiguiapi.view.main.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter4.BaseQuickAdapter;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.databinding.GgActivityGuiGuiMainBinding;
import my.zjh.model_guiguiapi.view.GuiBaseActivity;
import my.zjh.model_guiguiapi.view.main.adapter.ApiEntryBrvAdapter;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;
import my.zjh.model_guiguiapi.view.main.viewmodel.GuiGuiMainViewModel;

/**
 * 鬼鬼api入口页面
 *
 * @author AHeng
 * @date 2025/05/02 07:22
 */
@Route(path = "/guigui/GuiGuiMainActivity")
public class GuiGuiMainActivity extends GuiBaseActivity<GgActivityGuiGuiMainBinding, GuiGuiMainViewModel> {
    /**
     * 页面标题
     */
    private static final String PAGE_TITLE = "鬼鬼api";
    
    /**
     * API条目适配器
     */
    private ApiEntryBrvAdapter mAdapter;
    
    @Override
    protected CharSequence setTitle() {
        return getString(R.string.gg_titletest);
    }
    
    @Override
    protected void initView() {
        initRecyclerView();
        
        // 加载列表
        viewModel.getApiList().observe(this,
                apiItems -> mAdapter.submitList(apiItems));
    }
    
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new ApiEntryBrvAdapter();
        binding.rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::onItemClick);
    }
    
    /**
     * 处理列表项点击事件
     *
     * @param adapter  适配器
     * @param view     被点击的视图
     * @param position 项目位置
     */
    private void onItemClick(BaseQuickAdapter<ApiItemBean, ?> adapter, View view, int position) {
        ApiItemBean apiItemBean = adapter.getItem(position);
        if (apiItemBean != null) {
            ARouter.getInstance()
                    .build(apiItemBean.getRoutePath())
                    .withParcelable("apiItemBean", apiItemBean)
                    .navigation();
        }
    }
}
