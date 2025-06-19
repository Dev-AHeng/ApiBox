package my.zjh.model_guiguiapi.view.qrcodegeneration.viewmodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_guiguiapi.api.ApiClient;
import my.zjh.model_guiguiapi.view.qrcodegeneration.api.QRCodeGenerationApi;

/**
 * 二维码生成ViewModel
 *
 * @author AHeng
 * @date 2025/06/08 03:33
 */
public class QRCodeGenerationViewModel extends ViewModel {
    
    private final QRCodeGenerationApi api;
    private final Gson gson;
    private final CompositeDisposable disposables;
    
    // 超时定时器
    private Disposable timeoutDisposable;
    
    // LiveData用于观察状态变化
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> qrCodeBitmap = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showQRCodeCard = new MutableLiveData<>(false);
    
    // 输入验证错误
    private final MutableLiveData<String> textInputError = new MutableLiveData<>();
    private final MutableLiveData<String> sizeInputError = new MutableLiveData<>();
    private final MutableLiveData<String> frameInputError = new MutableLiveData<>();
    
    // 权限和文件操作相关
    private final MutableLiveData<Boolean> needStoragePermission = new MutableLiveData<>();
    private final MutableLiveData<String> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Intent> shareIntent = new MutableLiveData<>();
    
    // 操作类型
    public static final int ACTION_SAVE = 1;
    public static final int ACTION_SHARE = 2;
    private final MutableLiveData<Integer> pendingAction = new MutableLiveData<>(0);
    
    public QRCodeGenerationViewModel() {
        api = ApiClient.getInstance().createService(QRCodeGenerationApi.class);
        gson = new Gson();
        disposables = new CompositeDisposable();
    }
    
    /**
     * 生成二维码
     */
    public void generateQRCode(String text, String sizeStr, String frameStr, String errorLevel) {
        // 清除之前的错误状态
        clearErrors();
        
        // 验证输入
        if (!validateInput(text, sizeStr, frameStr)) {
            return;
        }
        
        // 解析参数
        int size = TextUtils.isEmpty(sizeStr) ? 200 : Integer.parseInt(sizeStr);
        int frame = TextUtils.isEmpty(frameStr) ? 1 : Integer.parseInt(frameStr);
        String errorCode = getErrorLevelCode(errorLevel);
        
        // 开始加载
        startLoading("正在生成二维码...");
        
        // 调用API
        disposables.add(
                api.generateQRCode(text, size, frame, errorCode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleResponse,
                                this::handleNetworkError
                        )
        );
    }
    
    /**
     * 请求保存二维码
     */
    public void requestSaveQRCode(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            errorMessage.setValue("请先生成二维码");
            return;
        }
        
        // 检查权限
        if (checkStoragePermission(context)) {
            performSave(context, bitmap);
        } else {
            pendingAction.setValue(ACTION_SAVE);
            needStoragePermission.setValue(true);
        }
    }
    
    /**
     * 请求分享二维码
     */
    public void requestShareQRCode(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            errorMessage.setValue("请先生成二维码");
            return;
        }
        
        // 检查权限
        if (checkStoragePermission(context)) {
            performShare(context, bitmap);
        } else {
            pendingAction.setValue(ACTION_SHARE);
            needStoragePermission.setValue(true);
        }
    }
    
    /**
     * 权限获取成功后执行待定操作
     */
    public void onPermissionGranted(Context context, Bitmap bitmap) {
        Integer action = pendingAction.getValue();
        if (action != null) {
            if (action == ACTION_SAVE) {
                performSave(context, bitmap);
            } else if (action == ACTION_SHARE) {
                performShare(context, bitmap);
            }
        }
        pendingAction.setValue(0);
    }
    
    /**
     * 权限被拒绝
     */
    public void onPermissionDenied() {
        errorMessage.setValue("需要存储权限才能保存或分享二维码");
        pendingAction.setValue(0);
    }
    
    /**
     * 检查存储权限
     */
    private boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) 及以上使用Scoped Storage，不需要申请权限
            return true;
        } else {
            // Android 9 (API 28) 及以下需要申请存储权限
            return ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * 执行保存操作
     */
    private void performSave(Context context, Bitmap bitmap) {
        try {
            boolean success = saveBitmapToGallery(context, bitmap);
            if (success) {
                saveResult.setValue("二维码已保存到相册");
            } else {
                saveResult.setValue("保存失败，请重试");
            }
        } catch (Exception e) {
            saveResult.setValue("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行分享操作
     */
    private void performShare(Context context, Bitmap bitmap) {
        try {
            File tempFile = saveBitmapToTempFile(context, bitmap);
            if (tempFile != null) {
                Intent intent = createShareIntent(context, tempFile);
                shareIntent.setValue(intent);
            } else {
                errorMessage.setValue("创建临时文件失败");
            }
        } catch (Exception e) {
            errorMessage.setValue("分享失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存Bitmap到相册
     */
    private boolean saveBitmapToGallery(Context context, Bitmap bitmap) {
        try {
            String fileName = "QRCode_" + new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date()) + ".png";
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 (API 29) 及以上使用MediaStore API
                ContentResolver resolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        if (out != null) {
                            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }
                    }
                }
            } else {
                // Android 9 (API 28) 及以下使用传统文件API
                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(picturesDir, fileName);
                
                try (FileOutputStream out = new FileOutputStream(file)) {
                    boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    if (success) {
                        // 通知媒体扫描器
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(Uri.fromFile(file));
                        context.sendBroadcast(mediaScanIntent);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 保存Bitmap到临时文件
     */
    private File saveBitmapToTempFile(Context context, Bitmap bitmap) {
        try {
            File cacheDir = new File(context.getCacheDir(), "shared_images");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            
            String fileName = "qrcode_temp_" + System.currentTimeMillis() + ".png";
            File tempFile = new File(cacheDir, fileName);
            
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                return tempFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 创建分享Intent
     */
    private Intent createShareIntent(Context context, File imageFile) {
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 (API 24) 及以上使用FileProvider
            imageUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".fileprovider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "分享二维码");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        return Intent.createChooser(shareIntent, "分享二维码");
    }
    
    /**
     * 验证输入参数
     */
    private boolean validateInput(String text, String sizeStr, String frameStr) {
        boolean isValid = true;
        
        // 验证文本输入
        if (TextUtils.isEmpty(text)) {
            textInputError.setValue("请输入要生成二维码的文本");
            isValid = false;
        }
        
        // 验证尺寸
        try {
            int size = TextUtils.isEmpty(sizeStr) ? 200 : Integer.parseInt(sizeStr);
            if (size < 100 || size > 800) {
                sizeInputError.setValue("尺寸范围应在100-800之间");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            sizeInputError.setValue("请输入有效的数字");
            isValid = false;
        }
        
        // 验证边框大小
        try {
            int frame = TextUtils.isEmpty(frameStr) ? 1 : Integer.parseInt(frameStr);
            if (frame < 1 || frame > 10) {
                frameInputError.setValue("边框大小范围应在1-10之间");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            frameInputError.setValue("请输入有效的数字");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * 处理API响应
     */
    private void handleResponse(retrofit2.Response<okhttp3.ResponseBody> response) {
        stopLoading();
        
        if (response.isSuccessful() && response.body() != null) {
            String contentType = response.headers().get("Content-Type");
            
            if (contentType != null && contentType.contains("image/png")) {
                // 成功响应，返回图片
                handleSuccessResponse(response.body());
            } else {
                // 错误响应，返回JSON
                handleErrorResponse(response.body());
            }
        } else {
            showQRCodeCard.setValue(false);
            statusMessage.setValue("请求失败，状态码: " + response.code());
            errorMessage.setValue("请求失败，请重试");
        }
    }
    
    /**
     * 处理成功响应（图片）
     */
    private void handleSuccessResponse(okhttp3.ResponseBody responseBody) {
        try {
            // 这里需要将ResponseBody转换为Bitmap
            // 由于我们需要在Activity中使用Glide来处理图片加载
            // 这里我们发送成功信号，让Activity处理图片加载
            showQRCodeCard.setValue(true);
            statusMessage.setValue("二维码生成成功！");
        } catch (Exception e) {
            showQRCodeCard.setValue(false);
            statusMessage.setValue("图片处理失败");
            errorMessage.setValue("图片处理失败，请重试");
        }
    }
    
    /**
     * 处理错误响应（JSON）
     */
    private void handleErrorResponse(okhttp3.ResponseBody responseBody) {
        try {
            String responseStr = responseBody.string();
            QRCodeGenerationApi.ErrorResponse errorResponse = gson.fromJson(responseStr, QRCodeGenerationApi.ErrorResponse.class);
            String message = "错误: " + errorResponse.getMsg();
            
            showQRCodeCard.setValue(false);
            statusMessage.setValue(message);
            errorMessage.setValue(message);
        } catch (IOException |
                 JsonSyntaxException e) {
            showQRCodeCard.setValue(false);
            statusMessage.setValue("响应解析失败");
            errorMessage.setValue("响应解析失败，请重试");
        }
    }
    
    /**
     * 处理网络错误
     */
    private void handleNetworkError(Throwable throwable) {
        stopLoading();
        showQRCodeCard.setValue(false);
        statusMessage.setValue("网络请求失败: " + throwable.getMessage());
        errorMessage.setValue("网络请求失败，请检查网络连接");
    }
    
    /**
     * 获取错误纠正级别代码
     */
    private String getErrorLevelCode(String errorLevel) {
        if (errorLevel.contains("L")) {
            return "L";
        } else if (errorLevel.contains("M")) {
            return "M";
        } else if (errorLevel.contains("Q")) {
            return "Q";
        } else if (errorLevel.contains("H")) {
            return "H";
        }
        return "L"; // 默认值
    }
    
    /**
     * 构建API URL（用于Glide加载图片）
     */
    public String buildImageUrl(String text, String sizeStr, String frameStr, String errorLevel) {
        int size = TextUtils.isEmpty(sizeStr) ? 200 : Integer.parseInt(sizeStr);
        int frame = TextUtils.isEmpty(frameStr) ? 1 : Integer.parseInt(frameStr);
        String errorCode = getErrorLevelCode(errorLevel);
        
        return "https://api.guiguiya.com/api/qrcode?text=" + text +
                       "&size=" + size +
                       "&frame=" + frame +
                       "&e=" + errorCode;
    }
    
    /**
     * 开始加载（带10秒超时）
     */
    private void startLoading(String message) {
        isLoading.setValue(true);
        statusMessage.setValue(message);
        
        // 设置10秒超时定时器
        timeoutDisposable = Single.timer(10, TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            ignore -> {
                                                // 超时处理
                                                if (Boolean.TRUE.equals(isLoading.getValue())) {
                                                    stopLoading();
                                                    showQRCodeCard.setValue(false);
                                                    statusMessage.setValue("请求超时，请重试");
                                                    errorMessage.setValue("请求超时，请检查网络连接");
                                                }
                                            },
                                            throwable -> {
                                                // 定时器异常（通常不会发生）
                                            }
                                    );
        
        // 将超时定时器添加到disposables中管理
        disposables.add(timeoutDisposable);
    }
    
    /**
     * 停止加载
     */
    private void stopLoading() {
        isLoading.setValue(false);
        
        // 取消超时定时器
        if (timeoutDisposable != null && !timeoutDisposable.isDisposed()) {
            timeoutDisposable.dispose();
            timeoutDisposable = null;
        }
    }
    
    /**
     * 清除所有错误状态
     */
    private void clearErrors() {
        textInputError.setValue(null);
        sizeInputError.setValue(null);
        frameInputError.setValue(null);
        errorMessage.setValue(null);
    }
    
    // Getter方法用于观察LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Bitmap> getQrCodeBitmap() {
        return qrCodeBitmap;
    }
    
    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }
    
    public LiveData<Boolean> getShowQRCodeCard() {
        return showQRCodeCard;
    }
    
    public LiveData<String> getTextInputError() {
        return textInputError;
    }
    
    public LiveData<String> getSizeInputError() {
        return sizeInputError;
    }
    
    public LiveData<String> getFrameInputError() {
        return frameInputError;
    }
    
    public LiveData<Boolean> getNeedStoragePermission() {
        return needStoragePermission;
    }
    
    public LiveData<String> getSaveResult() {
        return saveResult;
    }
    
    public LiveData<Intent> getShareIntent() {
        return shareIntent;
    }
    
    public LiveData<Integer> getPendingAction() {
        return pendingAction;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        
        // 清理超时定时器
        if (timeoutDisposable != null && !timeoutDisposable.isDisposed()) {
            timeoutDisposable.dispose();
            timeoutDisposable = null;
        }
        
        // 清理所有订阅
        disposables.clear();
    }
}
