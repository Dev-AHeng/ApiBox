package my.zjh.model_sanhaiapi.view;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.logger.Logger;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.box.BarcodeQueryResponseProto;
import my.zjh.common.BaseActivity;
import my.zjh.model_sanhaiapi.databinding.ShActivityBarcodeQueryBinding;
import my.zjh.model_sanhaiapi.dialog.LoadingDialogFragment;
import my.zjh.model_sanhaiapi.dialog.ProgressDialogFragment;
import my.zjh.model_sanhaiapi.model.ApiItem;
import my.zjh.model_sanhaiapi.model.BarcodeQueryResponse;
import my.zjh.model_sanhaiapi.proto.BarcodeDataStore;
import my.zjh.model_sanhaiapi.utils.RegexUtils;
import my.zjh.model_sanhaiapi.utils.ViewUtils;
import my.zjh.model_sanhaiapi.viewmodel.BarcodeQueryViewModel;

/**
 * 条形码查询 v1.0
 *
 * @author AHeng
 * @date 2025/03/31 17:29
 */
@Route(path = "/sanhai/BarcodeQueryActivity")
public class BarcodeQueryActivity extends BaseActivity {
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Autowired
    ApiItem apiItem;
    private ShActivityBarcodeQueryBinding binding;
    private BarcodeQueryViewModel barcodeQueryViewModel;
    private BarcodeDataStore dataStore;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ShActivityBarcodeQueryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        init();
        
        onClickCallback();
        
        viewModelCallback();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 确保对话框已关闭
        ProgressDialogFragment.hideLoading();
        LoadingDialogFragment.dismiss(getSupportFragmentManager());
        
        // 清理可能的内存泄漏
        if (binding != null) {
            binding = null;
        }
        
        // 清理所有订阅
        disposables.clear();
    }
    
    private void init() {
        ARouter.getInstance().inject(this);
        
        // 使用基类方法设置Toolbar
        setupToolbar(binding.shToolbarLayout.toolbar, apiItem.getTitle(), binding.shToolbarLayout.appbar, true);
        
        barcodeQueryViewModel = new ViewModelProvider(this).get(BarcodeQueryViewModel.class);
        
        // 初始化DataStore
        dataStore = new BarcodeDataStore(this);
    }
    
    private void onClickCallback() {
        // 扫描按钮
        binding.btnScan.setOnClickListener(this::scan);
        
        // 粘贴按钮
        binding.btnPaste.setOnClickListener(this::paste);
        
        // 查询按钮
        binding.btnSearch.setOnClickListener(this::query);
        
        // 收藏
        binding.btnFavorite.setOnClickListener(this::favorite);
        
        // 分享按钮
        binding.btnShare.setOnClickListener(this::share);
        
        // 购买按钮
        binding.btnBuy.setOnClickListener(this::buy);
        
        // 复制按钮
        binding.btnCopy.setOnClickListener(this::copy);
    }
    
    private void viewModelCallback() {
        barcodeQueryViewModel.getResponseData().observe(this, this::responseData);
        barcodeQueryViewModel.isRequestSuccessful().observe(this, this::isRequestSuccessful);
    }
    
    private void scan(View view) {
    
    }
    
    private void paste(View view) {
        pasteFromClipboard(binding.etBarcode);
    }
    
    private void query(View view) {
        String barcode = Objects.requireNonNull(binding.etBarcode.getText()).toString().trim();
        
        if (barcode.isEmpty()) {
            toast("请输入条形码");
            return;
        }
        
        // 验证条形码格式
        if (!RegexUtils.isValidBarcode(barcode)) {
            toast("条形码格式不正确，请重新输入");
            return;
        }
        
        ViewUtils.hide(binding.resultContainer);
        ViewUtils.hide(binding.errorContainer);
        showLoading(true);
        
        // 使用静态方法显示加载对话框
        ProgressDialogFragment.showLoading(getSupportFragmentManager(), "加载中...");
        
        barcodeQueryViewModel.query(barcode);
    }
    
    private void favorite(View view) {
        // 获取商品信息
        BarcodeQueryResponse.GoodsData data = Objects.requireNonNull(barcodeQueryViewModel.getResponseData().getValue()).getData();
        if (data == null) {
            toast("没有可收藏的信息");
            return;
        }
        
        BarcodeQueryResponseProto.GoodsData goodsData = BarcodeQueryResponseProto.GoodsData.newBuilder()
                                                                .setGoodsName(data.getGoodsName())
                                                                .setBarcode(data.getBarcode())
                                                                .setPrice(data.getPrice())
                                                                .setBrand(data.getBrand())
                                                                .setSupplier(data.getSupplier())
                                                                .setStandard(data.getStandard())
                                                                .build();
        
        BarcodeQueryResponseProto barcodeQueryResponseProto = BarcodeQueryResponseProto.newBuilder()
                                                                      .setData(goodsData)
                                                                      .build();
        
        // 收藏
        disposables.add(
                dataStore.saveBarcodeData(barcodeQueryResponseProto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                savedData -> toast("保存成功"),
                                error -> {
                                    toast("保存失败", error);
                                    Logger.i("保存失败: %s", error);
                                }
                        )
        );
    }
    
    /**
     * 分享
     */
    private void share(View view) {
        // 获取商品信息
        BarcodeQueryResponse.GoodsData data = barcodeQueryViewModel.getResponseData().getValue().getData();
        if (data == null) {
            toast("没有可分享的信息");
            return;
        }
        
        // 拼接商品信息 商品名称：- 条形码：- 价格：-
        StringBuilder sb = new StringBuilder();
        sb.append("商品名称：").append(data.getGoodsName() != null && !data.getGoodsName().isEmpty() ? data.getGoodsName() : "-").append("\n");
        sb.append("条形码：").append(data.getBarcode() != null && !data.getBarcode().isEmpty() ? data.getBarcode() : "-").append("\n");
        sb.append("价格：").append(data.getPrice() != null ? formatRMB(data.getPrice()) : "-").append("\n");
        
        // 分享
        fenXiang(sb.toString());
    }
    
    private void buy(View view) {
    }
    
    /**
     * 复制商品信息
     */
    private void copy(View view) {
        // 获取商品信息
        BarcodeQueryResponse.GoodsData data = barcodeQueryViewModel.getResponseData().getValue().getData();
        if (data == null) {
            toast("没有可复制的信息");
            return;
        }
        
        // 拼接商品信息
        StringBuilder sb = new StringBuilder();
        sb.append("商品名称：").append(data.getGoodsName() != null && !data.getGoodsName().isEmpty() ? data.getGoodsName() : "-").append("\n");
        sb.append("条形码：").append(data.getBarcode() != null && !data.getBarcode().isEmpty() ? data.getBarcode() : "-").append("\n");
        sb.append("价格：").append(data.getPrice() != null ? formatRMB(data.getPrice()) : "-").append("\n");
        sb.append("品牌：").append(data.getBrand() != null && !data.getBrand().isEmpty() ? data.getBrand() : "-").append("\n");
        sb.append("规格：").append(data.getStandard() != null && !data.getStandard().isEmpty() ? data.getStandard() : "-").append("\n");
        sb.append("供应商：").append(data.getSupplier() != null && !data.getSupplier().isEmpty() ? data.getSupplier() : "-");
        
        // 复制到剪贴板
        copyToClipboard(sb.toString());
        toast("商品信息已复制到剪贴板");
    }
    
    /**
     * 请求回调
     *
     * @param barcodeQueryResponse 请求回调数据
     */
    private void responseData(BarcodeQueryResponse barcodeQueryResponse) {
        log(barcodeQueryResponse);
        binding.tvGoodsName.setText(barcodeQueryResponse.getData().getGoodsName());
        binding.tvBarcode.setText(barcodeQueryResponse.getData().getBarcode());
        binding.tvPrice.setText(formatRMB(barcodeQueryResponse.getData().getPrice()));
        binding.tvBrand.setText(barcodeQueryResponse.getData().getBrand());
        binding.tvStandard.setText(barcodeQueryResponse.getData().getStandard());
        binding.tvSupplier.setText(barcodeQueryResponse.getData().getSupplier());
        
        toast(barcodeQueryResponse.getMsg());
    }
    
    private void isRequestSuccessful(Boolean isRequestSuccessful) {
        showLoading(false);
        
        // 使用静态方法隐藏加载对话框
        ProgressDialogFragment.hideLoading();
        
        if (isRequestSuccessful) {
            ViewUtils.show(binding.resultContainer);
        } else {
            ViewUtils.show(binding.errorContainer);
        }
    }
    
    /**
     * 显示或隐藏加载中
     *
     * @param isLoading 是否加载中
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            LoadingDialogFragment.showLoading(getSupportFragmentManager(), "正在解析视频...");
        } else {
            LoadingDialogFragment.dismiss(getSupportFragmentManager());
        }
    }
}