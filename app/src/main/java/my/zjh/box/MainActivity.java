package my.zjh.box;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hjq.toast.Toaster;
import com.orhanobut.logger.Logger;

import my.zjh.box.databinding.AppActivityMainBinding;
import my.zjh.common.GlideGlobalConfig;

/**
 * @author AHeng
 * @date 2025/03/20 18:38
 */
@Route(path = "/app/main")
public class MainActivity extends MainBaseActivity<AppActivityMainBinding> {
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 登出
        // MobclickAgent.onProfileSignOff();
        
        // 程序退出时，用于保存统计数据的API
        // MobclickAgent.onKillProcess(this);
    }
    
    @Override
    protected CharSequence setTitle() {
        return "MainActivity";
    }
    
    @Override
    protected void initView() {
        
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //     Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //     return insets;
        // });
        //
        // binding.appBarLayout.setStatusBarForeground(MaterialShapeDrawable.createWithElevationOverlay(this));
        //
        // binding.appBarLayout.setStatusBarForegroundColor(
        //         MaterialColors.getColor(binding.appBarLayout, com.google.android.material.R.attr.colorSurface));
        //
        binding.topAppBar.setNavigationOnClickListener(view -> finish());
        
        
        // PushAgent.getInstance(this).onAppStart();
        //
        // MobclickAgent.onProfileSignIn("example_id", "AHeng");
        // MobclickAgent.userProfileMobile("13387538888");
        // MobclickAgent.userProfileEMail("developer@gmail.com");
        // MobclickAgent.userProfile("QQ", "val111");
        // MobclickAgent.userProfile("test", 123457878L);
        
        binding.btn.setOnClickListener(v -> {
            // 使用 ARouter 跳转到 User 模块的 UserActivity
            ARouter.getInstance()
                    .build("/user/main")
                    .withString("name", "张三")
                    .withInt("age", 20)
                    .navigation(this, new NavigationCallback() {
                        @Override
                        public void onFound(Postcard postcard) {
                            Logger.i("找到了目标页面: " + postcard.getPath());
                            toast("找到了目标页面: " + postcard.getPath());
                        }
                        
                        @Override
                        public void onLost(Postcard postcard) {
                            Logger.i("没有找到目标页面: " + postcard.getPath());
                        }
                        
                        @Override
                        public void onArrival(Postcard postcard) {
                            Logger.i("成功跳转到目标页面: %s", postcard.getPath());
                        }
                        
                        @Override
                        public void onInterrupt(Postcard postcard) {
                            Logger.i("跳转被中断: " + postcard.getPath());
                        }
                    });
        });
        
        binding.tosanhai.setOnClickListener(v -> {
            ARouter.getInstance().build("/sanhai/MainActivity").navigation();
            // ARouter.getInstance().build("/sanhai/exoplayer").navigation();
            // ARouter.getInstance().build("/view/AggregateShortVideoAnalysisActivity").navigation();
            
            // goToActivity(ICPFilingInquiryActivity.class);
            
            // ARouter.getInstance().build("/sanhai/ICPFilingInquiryActivity").navigation(this);
        });
        
        
        binding.toguigui.setOnClickListener(v -> {
            ARouter.getInstance().build("/guigui/GuiGuiMainActivity").navigation();
        });
        
        
        // 展示自定义MD3风格对话框
        binding.testBtn.setOnClickListener(view -> {
            // 展示MD3风格对话框
            MD3DialogHelper.createMD3Dialog(
                    this,
                    "自定义Material Design 3对话框",
                    "这是一个Material Design 3风格的对话框示例，拥有更现代的视觉效果和交互体验。",
                    "确定",
                    "取消",
                    v -> toast("您点击了确定按钮"),
                    v -> toast("您点击了取消按钮")
            ).show();
        });
        
        // 展示MD3 Alert对话框
        binding.testAlertDialog.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("MaterialAlertDialogBuilder")
                    .setMessage("原生Material Alert对话框")
                    .setCancelable(false)
                    .setPositiveButton("哈哈1", (dialog, which) -> {
                        Toaster.show("哈哈1");
                    })
                    .setNegativeButton("ha哈哈2", (dialog, which) -> {
                        Toaster.show("ha哈哈2");
                    })
                    .show();
        });
        
        
        binding.appButton3.setOnClickListener(view -> {
            // 显示内存缓存配置
            String cacheInfo = GlideGlobalConfig.getCacheInfo(this);
            Log.i("Glide", cacheInfo);
            // 输出：内存缓存配置: 64.0MB, 图片池配置: 32.0MB
            
            // 检查磁盘缓存大小
            GlideGlobalConfig.getDiskCacheSize(this, sizeInBytes -> {
                float sizeMB = sizeInBytes / 1024.0f / 1024.0f;
                Log.i("Glide", "磁盘缓存大小: " + String.format("%.1fMB", sizeMB));
                
                // 如果缓存过大，可以提醒用户清理
                if (sizeMB > 300) {
                    Toaster.show("如果缓存过大，可以提醒用户清理");
                }
            });
            
        });
        
        
        binding.appButton4.setOnClickListener(view -> {
        
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if (id == R.id.edit) {
        //     toast("点击了编辑");
        //     ARouter.getInstance().build("/app/MD3BottomSheetSample").navigation();
        //     return true;
        // } else if (id == R.id.favorite) {
        //     toast("点击了喜欢");
        //     return true;
        // } else
            if (id == R.id.more) {
            toast("点击了更多");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}