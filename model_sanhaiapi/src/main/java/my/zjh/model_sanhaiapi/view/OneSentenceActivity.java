package my.zjh.model_sanhaiapi.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.databinding.ShActivityOneSentenceBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.viewmodel.ApiViewModelFactory;
import my.zjh.model_sanhaiapi.viewmodel.YiyanViewModel;

/**
 * 一言查询
 *
 * @author AHeng
 * @date 2025/03/27
 */
@Route(path = "/sanhai/OneSentenceActivity")
public class OneSentenceActivity extends BaseActivity {
    @Autowired
    ApiItem apiItem;
    private ShActivityOneSentenceBinding binding;
    private YiyanViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ShActivityOneSentenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this, new ApiViewModelFactory()).get(YiyanViewModel.class);
        
        // 设置日期和星期
        setDateAndWeekday();
        
        // 设置获取一言按钮点击事件
        binding.btnGetYiyan.setOnClickListener(v -> getYiyan());
        
        // 观察数据变化
        observeData();
        
        // 首次加载一言
        getYiyan();
    }
    
    /**
     * 设置日期和星期
     */
    private void setDateAndWeekday() {
        // 获取当前日期
        Date date = Calendar.getInstance().getTime();
        
        // 设置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        binding.tvDate.setText(dateFormat.format(date));
        
        // 获取星期
        String[] weekdays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        binding.tvWeekday.setText(weekdays[dayOfWeek]);
    }
    
    /**
     * 观察数据变化
     */
    private void observeData() {
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (binding != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.btnGetYiyan.setEnabled(!isLoading);
            }
        });
        
        // 观察错误信息
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (binding != null) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    binding.tvError.setText(errorMessage);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.cardYiyan.setVisibility(View.GONE);
                } else {
                    binding.tvError.setVisibility(View.GONE);
                    binding.cardYiyan.setVisibility(View.VISIBLE);
                }
            }
        });
        
        // 观察一言内容
        viewModel.getYiyanContent().observe(this, content -> {
            if (binding != null && content != null && !content.isEmpty()) {
                binding.tvYiyanContent.setText(content);
            }
        });
        
        // 观察一言提示
        viewModel.getYiyanTips().observe(this, tips -> {
            if (binding != null && tips != null && !tips.isEmpty()) {
                binding.tvTips.setText(tips);
            }
        });
    }
    
    /**
     * 获取一言
     */
    private void getYiyan() {
        viewModel.getYiyan();
    }
    
    /**
     * 复制一言
     */
    private void copyYiyan() {
        String content = viewModel.getCurrentYiyan();
        if (content != null && !content.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("一言", content);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "已复制一言到剪贴板", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "暂无内容可复制", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sh_menu_yiyan, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_copy) {
            copyYiyan();
            return true;
        } else if (itemId == R.id.action_refresh) {
            getYiyan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        // 释放binding引用，避免内存泄漏
        binding = null;
        
        // ViewModel会在Activity销毁时自动调用onCleared()方法清理资源
        // 所以这里不需要额外清理ViewModel
        
        
        super.onDestroy();
    }
}