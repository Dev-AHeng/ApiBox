package my.zjh.model_guiguiapi.view.bingcoversby7days.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.zjh.common.ImageLoader;
import my.zjh.model_guiguiapi.databinding.GgItemBingImageBinding;
import my.zjh.model_guiguiapi.view.bingcoversby7days.api.BingCoversBy7DaysApi;
import my.zjh.model_guiguiapi.view.bingcoversby7days.viewmodel.BingCoversBy7DaysViewModel;

/**
 * 必应图片列表适配器
 *
 * @author AHeng
 * @date 2025/06/09 05:30
 */
public class BingImageAdapter extends RecyclerView.Adapter<BingImageAdapter.BingImageViewHolder> {
    
    private final Context context;
    private final BingCoversBy7DaysViewModel viewModel;
    private final List<BingCoversBy7DaysApi.BingImageData> imageList;
    private OnItemClickListener onItemClickListener;
    
    public BingImageAdapter(Context context) {
        this.context = context;
        this.viewModel = new BingCoversBy7DaysViewModel();
        this.imageList = new ArrayList<>();
    }
    
    /**
     * 设置点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    /**
     * 更新数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<BingCoversBy7DaysApi.BingImageData> newImageList) {
        if (newImageList != null) {
            this.imageList.clear();
            this.imageList.addAll(newImageList);
            notifyDataSetChanged();
        }
    }
    
    @NonNull
    @Override
    public BingImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GgItemBingImageBinding binding = GgItemBingImageBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new BingImageViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BingImageViewHolder holder, int position) {
        BingCoversBy7DaysApi.BingImageData imageData = imageList.get(position);
        holder.bind(imageData);
    }
    
    @Override
    public int getItemCount() {
        return imageList.size();
    }
    
    /**
     * ViewHolder类
     */
    public class BingImageViewHolder extends RecyclerView.ViewHolder {
        private final GgItemBingImageBinding binding;
        
        public BingImageViewHolder(@NonNull GgItemBingImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(BingCoversBy7DaysApi.BingImageData imageData) {
            if (imageData == null) {
                return;
            }
            
            // 设置标题
            String title = imageData.getTitle();
            if (!TextUtils.isEmpty(title)) {
                
                // 移除可能的版权信息
                if (title.contains("(©")) {
                    title = title.substring(0, title.indexOf("(©")).trim();
                }
                binding.tvTitle.setText(title);
            } else {
                binding.tvTitle.setText("无标题");
            }
            
            // 设置日期
            String formattedDate = viewModel.formatDate(imageData.getTimeId());
            binding.tvDate.setText(formattedDate);
            
            // 加载图片
            String imageUrl = viewModel.getBestQualityImageUrl(imageData);
            if (!TextUtils.isEmpty(imageUrl)) {
                ImageLoader.load(binding.ivBingImage, imageUrl);
            } else {
                binding.ivBingImage.setImageResource(my.zjh.common.R.drawable.image_24px);
            }
            
            // 设置按钮点击事件
            binding.btnViewImage.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onViewImageClick(imageData);
                }
            });
            
            binding.btnDownload.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onDownloadClick(imageData);
                }
            });
        }
    }
    
    /**
     * 点击事件监听器接口
     */
    public interface OnItemClickListener {
        
        /**
         * 查看大图按钮点击
         *
         * @param imageData 图片数据
         */
        void onViewImageClick(BingCoversBy7DaysApi.BingImageData imageData);
        
        /**
         * 下载按钮点击
         *
         * @param imageData 图片数据
         */
        void onDownloadClick(BingCoversBy7DaysApi.BingImageData imageData);
    }
} 