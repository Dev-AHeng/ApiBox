package my.zjh.model_sanhaiapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.model.ApiItem;

/**
 * API列表适配器
 *
 * @author AHeng
 * @date 2025/03/24 02:27
 */
public class ApiListAdapter extends ListAdapter<ApiItem, ApiListAdapter.ApiViewHolder> {
    
    private static final DiffUtil.ItemCallback<ApiItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ApiItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ApiItem oldItem, @NonNull ApiItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull ApiItem oldItem, @NonNull ApiItem newItem) {
            return oldItem.equals(newItem);
        }
    };
    
    private OnApiItemClickListener listener;
    
    public ApiListAdapter() {
        super(DIFF_CALLBACK);
        // 设置稳定ID，帮助RecyclerView减少闪烁
        setHasStableIds(true);
    }
    
    @NonNull
    @Override
    public ApiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用传统的布局膨胀方法
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sh_item_api_card, parent, false);
        return new ApiViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ApiViewHolder holder, int position) {
        ApiItem apiItem = getItem(position);
        holder.bind(apiItem);
    }
    
    @Override
    public long getItemId(int position) {
        // 提供稳定的ID
        ApiItem item = getItem(position);
        return item != null ? item.getId().hashCode() : super.getItemId(position);
    }
    
    public void setOnApiItemClickListener(OnApiItemClickListener listener) {
        this.listener = listener;
    }
    
    /**
     * 预加载布局（由于不再使用数据绑定，简化预加载方法）
     */
    public void preloadItems(ViewGroup parent, int count) {
        // 预加载简化为仅创建View
        new Thread(() -> {
            for (int i = 0; i < count && i < getItemCount(); i++) {
                final int position = i;
                parent.post(() -> {
                    if (position < getItemCount()) {
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.sh_item_api_card, parent, false);
                    }
                });
            }
        }).start();
    }
    
    public interface OnApiItemClickListener {
        void onApiItemClick(ApiItem apiItem);
    }
    
    public class ApiViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvIsFree;
        
        public ApiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_api_title);
            tvDescription = itemView.findViewById(R.id.tv_api_description);
            tvIsFree = itemView.findViewById(R.id.tv_is_free);
        }
        
        public void bind(final ApiItem apiItem) {
            // 设置文本
            tvTitle.setText(apiItem.getTitle());
            tvDescription.setText(apiItem.getDescription());

 
            // 设置可见性
            tvIsFree.setVisibility(apiItem.isFree() ? View.VISIBLE : View.GONE);
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApiItemClick(apiItem);
                }
            });
        }
    }
} 