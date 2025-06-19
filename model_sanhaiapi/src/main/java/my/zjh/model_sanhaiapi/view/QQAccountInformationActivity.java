package my.zjh.model_sanhaiapi.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.databinding.ShActivityQqaccountInformationBinding;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.QQInfoResponse;
import my.zjh.model_sanhaiapi.viewmodel.QQInfoViewModel;

/**
 * QQ基础信息 v1.0
 *
 * @author AHeng
 * @date 2025/03/26 18:33
 */
@Route(path = "/sanhai/QQAccountInformationActivity")
public class QQAccountInformationActivity extends BaseActivity {
    @Autowired
    ApiItem apiItem;
    
    // ViewBinding对象
    private ShActivityQqaccountInformationBinding binding;
    
    // ViewModel对象
    private QQInfoViewModel viewModel;
    
    // 剪贴板管理器
    private ClipboardManager clipboardManager;
    
    // 保存头像链接
    private String avatarUrl40;
    private String avatarUrl100;
    private String avatarUrl140;
    private String avatarUrl640;
    
    // 是否使用圆形头像
    private boolean useCircleAvatar = true;
    
    // QQ号码验证正则表达式（5-11位数字，首位不为0）
    private static final Pattern QQ_PATTERN = Pattern.compile("^[1-9][0-9]{4,10}$");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityQqaccountInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化剪贴板管理器
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        
        // ARouter注入
        ARouter.getInstance().inject(this);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(QQInfoViewModel.class);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        // 设置UI事件监听
        setupListeners();
        
        // 观察ViewModel数据变化
        observeViewModel();
    }
    
    @Override
    protected void onDestroy() {
        binding = null;
        
        super.onDestroy();
    }
    
    /**
     * 设置UI事件监听
     */
    private void setupListeners() {
        // 查询按钮点击事件
        binding.queryButton.setOnClickListener(v -> performQuery());
        
        // 粘贴按钮点击事件
        binding.pasteButton.setOnClickListener(v -> pasteFromClipboard());
        
        // 输入框回车键事件
        binding.qqEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performQuery();
                return true;
            }
            return false;
        });
        
        // QQ空间链接点击事件
        binding.qzoneText.setOnClickListener(v -> {
            copyTextToClipboard(binding.qzoneText.getText().toString());
        });
        
        // 头像形状切换事件
        binding.avatarShapeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            useCircleAvatar = isChecked;
            
            // 如果已有头像数据，立即重新加载头像应用新形状
            if (avatarUrl40 != null) {
                loadAvatar(binding.avatarImage, avatarUrl640);
                loadAvatar(binding.avatarImage40, avatarUrl40);
                loadAvatar(binding.avatarImage100, avatarUrl100);
                loadAvatar(binding.avatarImage140, avatarUrl140);
                loadAvatar(binding.avatarImage640, avatarUrl640);
            } else {
                // 如果没有数据，只更新形状设置
                updateAvatarShape();
            }
        });
        
        // 设置文本点击事件 - 复制文本
        setupTextViewCopyListeners();
        
        // 设置头像点击事件 - 复制头像链接
        setupAvatarCopyListeners();
        
        // 设置文本输入框的尾部图标点击事件
        setupTextInputEndIconListeners();
    }
    
    /**
     * 设置文本输入框的尾部图标点击事件
     */
    private void setupTextInputEndIconListeners() {
        // 为每个TextInputLayout设置尾部图标点击监听
        TextInputLayout layout40 = (TextInputLayout) binding.avatar40Text.getParent().getParent();
        TextInputLayout layout100 = (TextInputLayout) binding.avatar100Text.getParent().getParent();
        TextInputLayout layout140 = (TextInputLayout) binding.avatar140Text.getParent().getParent();
        TextInputLayout layout640 = (TextInputLayout) binding.avatar640Text.getParent().getParent();
        
        layout40.setEndIconOnClickListener(v -> copyTextToClipboard(Objects.requireNonNull(binding.avatar40Text.getText()).toString()));
        layout100.setEndIconOnClickListener(v -> copyTextToClipboard(Objects.requireNonNull(binding.avatar100Text.getText()).toString()));
        layout140.setEndIconOnClickListener(v -> copyTextToClipboard(Objects.requireNonNull(binding.avatar140Text.getText()).toString()));
        layout640.setEndIconOnClickListener(v -> copyTextToClipboard(Objects.requireNonNull(binding.avatar640Text.getText()).toString()));
    }
    
    /**
     * 设置文本复制点击事件
     */
    private void setupTextViewCopyListeners() {
        // QQ号
        binding.qqNumberText.setOnClickListener(v -> 
                copyTextToClipboard(binding.qqNumberText.getText().toString()));
        
        // 昵称
        binding.nameText.setOnClickListener(v -> 
                copyTextToClipboard(binding.nameText.getText().toString()));
        
        // 邮箱
        binding.emailText.setOnClickListener(v -> 
                copyTextToClipboard(binding.emailText.getText().toString()));
    }
    
    /**
     * 设置头像复制链接点击事件
     */
    private void setupAvatarCopyListeners() {
        // 主头像 (640x640)
        binding.avatarImage.setOnClickListener(v -> {
            if (avatarUrl640 != null) {
                copyTextToClipboard(avatarUrl640);
            }
        });
        
        // 40x40头像
        binding.avatarImage40.setOnClickListener(v -> {
            if (avatarUrl40 != null) {
                copyTextToClipboard(avatarUrl40);
            }
        });
        
        // 100x100头像
        binding.avatarImage100.setOnClickListener(v -> {
            if (avatarUrl100 != null) {
                copyTextToClipboard(avatarUrl100);
            }
        });
        
        // 140x140头像
        binding.avatarImage140.setOnClickListener(v -> {
            if (avatarUrl140 != null) {
                copyTextToClipboard(avatarUrl140);
            }
        });
        
        // 640x640头像
        binding.avatarImage640.setOnClickListener(v -> {
            if (avatarUrl640 != null) {
                copyTextToClipboard(avatarUrl640);
            }
        });
    }
    
    /**
     * 更新所有头像的形状（圆形或方形）
     */
    private void updateAvatarShape() {
        // 更新所有头像的形状
        updateAvatarShapeAppearance(binding.avatarImage);
        updateAvatarShapeAppearance(binding.avatarImage40);
        updateAvatarShapeAppearance(binding.avatarImage100);
        updateAvatarShapeAppearance(binding.avatarImage140);
        updateAvatarShapeAppearance(binding.avatarImage640);
    }
    
    /**
     * 更新单个头像的形状
     * 
     * @param imageView 头像视图
     */
    private void updateAvatarShapeAppearance(ShapeableImageView imageView) {
        ShapeAppearanceModel shapeAppearanceModel;
        
        if (useCircleAvatar) {
            // 完全圆形 - 使用PILL常量（表示50%）
            shapeAppearanceModel = new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(ShapeAppearanceModel.PILL)
                    .build();
        } else {
            // 方形效果
            shapeAppearanceModel = new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(0f)
                    .build();
        }
        
        // 设置形状模型
        imageView.setShapeAppearanceModel(shapeAppearanceModel);
    }
    
    /**
     * 从剪贴板粘贴内容到输入框
     */
    private void pasteFromClipboard() {
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence pasteText = clipData.getItemAt(0).getText();
                if (pasteText != null) {
                    binding.qqEditText.setText(pasteText);
                    Toast.makeText(this, "已粘贴", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        Toast.makeText(this, "剪贴板中没有文本内容", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 复制文本到剪贴板
     *
     * @param text 要复制的文本
     */
    private void copyTextToClipboard(String text) {
        if (!TextUtils.isEmpty(text)) {
            ClipData clipData = ClipData.newPlainText("QQ信息", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 验证QQ号码格式是否合法
     * 
     * @param qqNumber QQ号码
     * @return 是否合法
     */
    private boolean isValidQQNumber(String qqNumber) {
        return QQ_PATTERN.matcher(qqNumber).matches();
    }
    
    /**
     * 执行查询操作
     */
    private void performQuery() {
        String qqNumber = Objects.requireNonNull(binding.qqEditText.getText()).toString().trim();
        
        // 检查QQ号码是否为空
        if (TextUtils.isEmpty(qqNumber)) {
            Toast.makeText(this, "请输入QQ号码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查QQ号码格式是否合法
        if (!isValidQQNumber(qqNumber)) {
            Toast.makeText(this, "QQ号码格式不正确，应为5-11位数字且首位不为0", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 隐藏错误信息和结果
        binding.errorText.setVisibility(View.GONE);
        binding.resultScroll.setVisibility(View.GONE);
        
        // 执行查询
        viewModel.queryQQInfo(qqNumber);
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察加载状态
        viewModel.getLoading().observe(this, isLoading -> {
            binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // 观察错误信息
        viewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                binding.errorText.setText(errorMsg);
                binding.errorText.setVisibility(View.VISIBLE);
                binding.resultScroll.setVisibility(View.GONE);
            } else {
                binding.errorText.setVisibility(View.GONE);
            }
        });
        
        // 观察查询结果
        viewModel.getQqInfoResult().observe(this, response -> {
            if (response != null) {
                updateUI(response);
                binding.resultScroll.setVisibility(View.VISIBLE);
            } else {
                binding.resultScroll.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * 更新UI显示查询结果
     *
     * @param response QQ信息响应对象
     */
    private void updateUI(QQInfoResponse response) {
        QQInfoResponse.QQInfoData data = response.getData();
        if (data == null) {
            return;
        }
        
        // 设置QQ号
        binding.qqNumberText.setText(response.getQq());
        
        // 设置昵称
        binding.nameText.setText(data.getName());
        
        // 设置邮箱
        binding.emailText.setText(data.getMail());
        
        // 设置QQ空间链接
        binding.qzoneText.setText(data.getQzone());
        
        // 保存头像链接
        avatarUrl40 = data.getImgurl();
        avatarUrl100 = data.getImgurl1();
        avatarUrl140 = data.getImgurl2();
        avatarUrl640 = data.getImgurl3();
        
        // 设置头像链接文本
        binding.avatar40Text.setText(avatarUrl40);
        binding.avatar100Text.setText(avatarUrl100);
        binding.avatar140Text.setText(avatarUrl140);
        binding.avatar640Text.setText(avatarUrl640);
        
        // 加载所有尺寸的头像
        loadAvatar(binding.avatarImage, avatarUrl640);
        loadAvatar(binding.avatarImage40, avatarUrl40);
        loadAvatar(binding.avatarImage100, avatarUrl100);
        loadAvatar(binding.avatarImage140, avatarUrl140);
        loadAvatar(binding.avatarImage640, avatarUrl640);
        
        // 更新头像形状
        updateAvatarShape();
    }
    
    /**
     * 加载头像
     *
     * @param imageView 图片视图
     * @param url 图片URL
     */
    private void loadAvatar(ShapeableImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView);
                
        // 设置ShapeableImageView的形状模式
        if (useCircleAvatar) {
            // 完全圆形 - 使用PILL常量（表示50%）
            imageView.setShapeAppearanceModel(new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(ShapeAppearanceModel.PILL)
                    .build());
        } else {
            // 方形效果
            imageView.setShapeAppearanceModel(new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(0f)
                    .build());
        }
    }
}