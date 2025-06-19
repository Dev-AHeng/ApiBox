package my.zjh.user.views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.logger.Logger;

import my.zjh.user.R;

/**
 * @author AHeng
 * @date 2025/03/22 01:13
 */
@Route(path = "/user/main", group = "user")
public class UserMainActivity extends AppCompatActivity {
    
    private static final String TAG = "UserMainActivity";
    
    @Autowired
    public String name;
    @Autowired
    public int age;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 必须在 onCreate 中调用 inject 方法，且在页面初始化之前调用
        ARouter.getInstance().inject(this);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        
        Logger.i("接收到的参数: name=" + name + ", age=" + age);
        toast("name: " + name + ", age: " + age);
        
   
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object content) {
        Toast.makeText(this, content.toString(), Toast.LENGTH_SHORT).show();
    }
    
}