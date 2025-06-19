package my.zjh.model_sanhaiapi.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * DialogFragment 显示对话框
 *
 * @author AHeng
 * @date 2025/04/04 05:14
 */
public class PurchaseConfirmationDialogFragment extends DialogFragment {
    public static String TAG = "PurchaseConfirmationDialog";
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(null);
        progressDialog.setMessage(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        
        // 配置对话框窗口参数（参考LoadingDialogFragment的窗口配置）
        if (progressDialog.getWindow() != null) {
            // progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 设置宽高自适应
            progressDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            // 设置内边距（单位：像素）
            progressDialog.getWindow().getDecorView().setPadding(80, 80, 80, 80);
            progressDialog.getWindow().setGravity(Gravity.CENTER);
        }
        
        return progressDialog;
    }
}