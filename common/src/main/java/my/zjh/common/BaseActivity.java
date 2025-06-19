package my.zjh.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.Objects;


/**
 * Activity基类
 *
 * @author Dev_Heng
 * @date 2024年8月4日22:04:44
 * @status ok
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "日志";
    
    /**
     * 判断字符串是否以人民币符号 "¥" 或 "￥" 开头，如果是，则删除这些符号，并在字符串前面拼接上指定的前缀，后面拼接上指定的后缀。
     *
     * @param str 要处理的字符串
     *
     * @return 处理后的字符串
     */
    public String formatRMB(String str) {
        // 检查字符串是否为空或 null
        if (str == null || str.isEmpty()) {
            // 返回默认值
            return "";
        }
        // 判断字符串是否以 "¥" 或 "￥" 开头
        if (str.startsWith("¥") || str.startsWith("￥")) {
            // 删除开头的 "¥" 或 "￥" 符号
            str = str.substring(1).trim();
        }
        // 在字符串前面拼接上指定的前缀，后面拼接上指定的后缀
        return "￥" + str + "元";
    }
    
    /**
     * 设置通用Toolbar
     *
     * @param toolbar        工具栏
     * @param title          标题
     * @param appBarLayout   AppBarLayout
     * @param showBackButton 是否显示返回按钮
     */
    protected void setupToolbar(@NonNull Toolbar toolbar, CharSequence title, AppBarLayout appBarLayout, boolean showBackButton) {
        // 设置标题
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        
        // 显示返回按钮
        // if (showBackButton && getSupportActionBar() != null) {
        //     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //     getSupportActionBar().setDisplayShowHomeEnabled(true);
        //     toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // }
        
        // 处理系统状态栏
        // ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
        //     Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //     // 为AppBarLayout设置顶部边距，避免被状态栏遮挡
        //     if (appBarLayout != null) {
        //         appBarLayout.setPadding(0, systemBars.top, 0, 0);
        //     }
        //     return insets;
        // });
    }
    
    /**
     * 启动新的Activity
     *
     * @param clazz 类名
     */
    public <T extends Activity> void goToActivity(Class<T> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    
    /**
     * 将不定参数方法的参数值拼接
     *
     * @param params 参数
     *
     * @return 结果
     */
    public String paramsToString(Object... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        
        // StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        // String invokeInfo = stackTraceElement.getFileName() + "-->" + stackTraceElement.getLineNumber();
        
        String invokeInfo = "";
        // 当前类名
        String currentClassName = "BaseActivity";
        StackTraceElement[] stackTraceElementArray = new Throwable().getStackTrace();
        for (int index = 0; index < stackTraceElementArray.length; index++) {
            String stackInfo = stackTraceElementArray[index].toString();
            if (stackInfo.contains(currentClassName)) {
                String info = stackTraceElementArray[index + 1].toString();
                if (!info.contains(currentClassName)) {
                    invokeInfo = info;
                    break;
                }
            }
        }
        
        int count = 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---------开始----------\n").append(invokeInfo);
        for (Object parma : params) {
            stringBuilder.append("\n----------").append(count++).append("-----------\n").append(parma).append("\n");
        }
        stringBuilder.append("\n---------结束----------");
        
        return stringBuilder.toString();
    }
    
    /**
     * 打印多个变量值，使用逗号分隔，并添加分割线。
     *
     * @param values 要打印的变量值
     */
    public void _log(Object... values) {
        if (values.length == 0) {
            // 如果没有参数，直接返回
            return;
        }
        
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String link = "-----------------------------------------\n" + ste.getFileName() + "/Line：" + ste.getLineNumber();
        
        int count = 1;
        StringBuilder logMessage = new StringBuilder(link);
        
        // 添加参数值
        for (Object value : values) {
            logMessage.append("\n----------").append("parameter").append(count++).append("------------------------\n").append(value).append("\n");
        }
        
        // 确保最后有一个分割线
        logMessage.append("\n------------------------------------------\n");
        
        // 使用info级别打印日志
        Log.i(TAG, logMessage.toString());
        
    }
    
    public void log(Object values) {
        Logger.i(values.toString());
    }
    
    public void log(Object... values) {
        Logger.i(Arrays.toString(values));
    }
    
    public void logi(Object values) {
        Logger.i(values.toString());
    }
    
    public void logi(Object... values) {
        Logger.i(Arrays.toString(values));
    }
    
    /**
     * 分享文本
     * <p>
     * 使用 fenXiang("哈哈哈", 123, true);
     */
    public void fenXiang(Object... contents) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, paramsToString(contents));
        shareIntent.setType("text/plain");
        Intent.createChooser(shareIntent, "分享到");
        startActivity(shareIntent);
    }
    
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
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), toastContent.toString(), Toast.LENGTH_SHORT).show();
        });
    }
    
    /**
     * 复制到剪贴板
     *
     * @param text 要复制的文本
     */
    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("视频链接", text);
        clipboard.setPrimaryClip(clip);
    }
    
    /**
     * 从剪贴板粘贴
     */
    public void pasteFromClipboard(EditText editTextObj) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
            String pasteData = item.getText().toString();
            editTextObj.setText(pasteData);
            toast("已粘贴");
        } else {
            toast("剪贴板为空");
        }
    }
}