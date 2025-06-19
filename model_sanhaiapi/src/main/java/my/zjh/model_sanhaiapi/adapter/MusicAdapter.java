package my.zjh.model_sanhaiapi.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.databinding.ShItemMusicBinding;
import my.zjh.model_sanhaiapi.model.MiguMusic;

/**
 * 音乐列表适配器
 *
 * @author AHeng
 * @date 2025/03/27 02:38
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    
    private List<MiguMusic.MusicItem> musicList = new ArrayList<>();
    private OnMusicItemClickListener listener;
    
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShItemMusicBinding binding = ShItemMusicBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MiguMusic.MusicItem musicItem = musicList.get(position);
        holder.bind(musicItem, position);
    }
    
    @Override
    public int getItemCount() {
        return musicList.size();
    }
    
    /**
     * 更新音乐列表
     *
     * @param newList 新的音乐列表
     */
    public void updateList(List<MiguMusic.MusicItem> newList) {
        this.musicList = newList;
        notifyDataSetChanged();
    }
    
    /**
     * 获取音乐列表
     *
     * @return 当前音乐列表
     */
    public List<MiguMusic.MusicItem> getMusicList() {
        return musicList;
    }
    
    /**
     * 设置音乐项点击监听器
     *
     * @param listener 监听器
     */
    public void setOnMusicItemClickListener(OnMusicItemClickListener listener) {
        this.listener = listener;
    }
    
    /**
     * 音乐项点击监听器接口
     */
    public interface OnMusicItemClickListener {
        void onMusicItemClick(MiguMusic.MusicItem musicItem, int position);
    }
    
    /**
     * 音乐列表ViewHolder
     */
    class MusicViewHolder extends RecyclerView.ViewHolder {
        
        private final ShItemMusicBinding binding;
        
        public MusicViewHolder(ShItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        /**
         * 绑定数据到视图
         *
         * @param musicItem 音乐项
         * @param position  位置
         */
        public void bind(MiguMusic.MusicItem musicItem, int position) {
            // 设置序号
            binding.musicNumber.setText(String.valueOf(musicItem.getN()));
            // 设置标题
            binding.musicTitle.setText(musicItem.getTitle());
            // 设置歌手
            binding.musicSinger.setText(musicItem.getSinger());
            
            // 加载专辑封面
            if (musicItem.getCover() != null && !musicItem.getCover().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(musicItem.getCover())
                        .apply(RequestOptions.placeholderOf(R.drawable.sh_default_album_art))
                        .into(binding.musicCover);
            } else {
                // 设置默认封面
                binding.musicCover.setImageResource(R.drawable.sh_default_album_art);
            }
            
            // 设置音质
            if (musicItem.getQuality() != null && !musicItem.getQuality().isEmpty()) {
                binding.musicQuality.setText(musicItem.getQuality());
                binding.musicQuality.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.musicQuality.setVisibility(android.view.View.GONE);
            }
            
            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMusicItemClick(musicItem, position);
                }
            });
        }
    }
} 