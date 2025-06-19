package my.zjh.model_sanhaiapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.model.AnimeImage;

/**
 * 动漫图片适配器 - 使用ListAdapter和DiffUtil实现高效更新
 *
 * @author AHeng
 * @date 2025/03/27 02:38
 */
public class AnimeImageAdapter extends ListAdapter<AnimeImage, AnimeImageAdapter.ViewHolder> {
    
    /**
     * DiffUtil回调，用于计算两个列表的差异
     */
    private static final DiffUtil.ItemCallback<AnimeImage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AnimeImage>() {
                @Override
                public boolean areItemsTheSame(@NonNull AnimeImage oldItem, @NonNull AnimeImage newItem) {
                    // 判断是否是同一个项目（通常比较ID或唯一标识）
                    return oldItem.getUrl().equals(newItem.getUrl());
                }
                
                @Override
                public boolean areContentsTheSame(@NonNull AnimeImage oldItem, @NonNull AnimeImage newItem) {
                    // 判断内容是否相同
                    return oldItem.equals(newItem);
                }
            };
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    
    public AnimeImageAdapter() {
        super(DIFF_CALLBACK);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sh_item_anime_image, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnimeImage image = getItem(position);
        
        // 使用Glide加载图片，添加缓存策略
        Glide.with(holder.itemView.getContext())
                .load(image.getUrl())
                .placeholder(R.drawable.sh_placeholder)
                .error(R.drawable.sh_error)
                .transition(DrawableTransitionOptions.withCrossFade(100)) // 加快过渡动画速度
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // 缓存所有版本的图片
                .dontAnimate() // 禁用动画，避免闪烁
                .centerCrop()
                .into(holder.imageView);
        
        // 设置文件名
        holder.textFilename.setText(image.getFilename());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getBindingAdapterPosition());
            }
        });
        
        // 设置长按事件
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                return onItemLongClickListener.onItemLongClick(holder.getBindingAdapterPosition());
            }
            return false;
        });
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
    
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    
    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position);
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        
        ImageView imageView;
        TextView textFilename;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_anime);
            textFilename = itemView.findViewById(R.id.text_filename);
        }
    }
} 