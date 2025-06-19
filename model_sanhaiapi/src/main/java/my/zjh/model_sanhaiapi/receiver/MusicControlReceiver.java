package my.zjh.model_sanhaiapi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import my.zjh.model_sanhaiapi.service.MusicPlayerService;

/**
 * 音乐控制广播接收器
 */
public class MusicControlReceiver extends BroadcastReceiver {
    
    private static final String TAG = "MusicControlReceiver";
    
    // 广播动作
    public static final String ACTION_MUSIC_CONTROL = "my.zjh.model_sanhaiapi.ACTION_MUSIC_CONTROL";
    
    // 动作代码
    public static final String EXTRA_CONTROL_CODE = "control_code";
    
    // 控制代码常量
    public static final int CONTROL_PLAY_PAUSE = 0;
    public static final int CONTROL_NEXT = 1;
    public static final int CONTROL_STOP = 2;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到广播: " + intent.getAction());
        
        if (ACTION_MUSIC_CONTROL.equals(intent.getAction())) {
            // 获取控制代码
            int controlCode = intent.getIntExtra(EXTRA_CONTROL_CODE, -1);
            Log.d(TAG, "控制代码: " + controlCode);
            
            // 转发到服务
            Intent serviceIntent = new Intent(context, MusicPlayerService.class);
            serviceIntent.setAction(ACTION_MUSIC_CONTROL);
            serviceIntent.putExtra(EXTRA_CONTROL_CODE, controlCode);
            
            // 启动服务处理操作
            context.startService(serviceIntent);
            Log.d(TAG, "转发到服务完成");
        }
    }
} 