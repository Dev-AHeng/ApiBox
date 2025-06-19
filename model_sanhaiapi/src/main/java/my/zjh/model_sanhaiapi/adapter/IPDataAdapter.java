package my.zjh.model_sanhaiapi.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.zjh.model_sanhaiapi.databinding.ShItemIpDataBinding;
import my.zjh.model_sanhaiapi.viewmodel.LocalIPViewModel;

/**
 * IP数据适配器
 *
 * @author AHeng
 * @date 2025/03/26
 */
public class IPDataAdapter extends RecyclerView.Adapter<IPDataAdapter.ViewHolder> {
    
    private final List<LocalIPViewModel.DataItem> dataItems;
    private final Context context;
    
    public IPDataAdapter(Context context, List<LocalIPViewModel.DataItem> dataItems) {
        this.context = context;
        this.dataItems = dataItems;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShItemIpDataBinding binding = ShItemIpDataBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalIPViewModel.DataItem item = dataItems.get(position);
        holder.binding.tvKey.setText(item.getKey());
        holder.binding.tvValue.setText(item.getValue());
        
        // 设置点击事件
        holder.binding.getRoot().setOnClickListener(v -> showCopyDialog(item));
    }
    
    @Override
    public int getItemCount() {
        return dataItems.size();
    }
    
    /**
     * 显示复制对话框
     *
     * @param item 数据项
     */
    private void showCopyDialog(LocalIPViewModel.DataItem item) {
        String[] options = new String[]{"复制键", "复制值", "复制键值对"};
        
        new AlertDialog.Builder(context)
                .setTitle("选择复制内容")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // 复制键
                            copyToClipboard("键", item.getKey());
                            break;
                        case 1:
                            // 复制值
                            copyToClipboard("值", item.getValue());
                            break;
                        case 2:
                            // 复制键值对
                            copyToClipboard("键值对", item.getKey() + ": " + item.getValue());
                            break;
                        default:
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 复制内容到剪贴板
     *
     * @param label   标签
     * @param content 内容
     */
    private void copyToClipboard(String label, String content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "已复制" + label + "到剪贴板", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 更新数据
     *
     * @param newItems 新数据项
     */
    public void updateData(List<LocalIPViewModel.DataItem> newItems) {
        dataItems.clear();
        if (newItems != null) {
            dataItems.addAll(newItems);
        }
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder类
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShItemIpDataBinding binding;
        
        public ViewHolder(@NonNull ShItemIpDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
} 