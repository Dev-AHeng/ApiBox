package my.zjh.model_sanhaiapi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.databinding.ShItemHeroBinding;
import my.zjh.model_sanhaiapi.model.HeroRank;
import my.zjh.model_sanhaiapi.view.HeroRankDetailBottomSheet;

/**
 * 英雄排名列表适配器
 *
 * @author AHeng
 * @date 2023/05/30 21:31
 */
public class HeroAdapter extends ListAdapter<HeroRank.Hero, HeroAdapter.HeroViewHolder> {
    
    private OnItemClickListener listener;
    
    public HeroAdapter() {
        super(new HeroDiffCallback());
    }
    
    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShItemHeroBinding binding = ShItemHeroBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HeroViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        HeroRank.Hero hero = getItem(position);
        holder.bind(hero);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public interface OnItemClickListener {
        void onItemClick(HeroRank.Hero hero);
    }
    
    private static class HeroDiffCallback extends DiffUtil.ItemCallback<HeroRank.Hero> {
        @Override
        public boolean areItemsTheSame(@NonNull HeroRank.Hero oldItem, @NonNull HeroRank.Hero newItem) {
            return Objects.equals(oldItem.getEname(), newItem.getEname());
        }
        
        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull HeroRank.Hero oldItem, @NonNull HeroRank.Hero newItem) {
            return oldItem.equals(newItem);
        }
    }
    
    public class HeroViewHolder extends RecyclerView.ViewHolder {
        private final ShItemHeroBinding binding;
        
        public HeroViewHolder(@NonNull ShItemHeroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    HeroRank.Hero hero = getItem(position);
                    
                    // 调用外部监听器
                    if (listener != null) {
                        listener.onItemClick(hero);
                    }
                    
                    // 显示底部弹窗
                    showBottomSheetDialog(v.getContext(), hero);
                }
            });
        }
        
        public void bind(HeroRank.Hero hero) {
            // 设置英雄名称和称号
            binding.textHeroName.setText(hero.getCname());
            binding.textHeroTitle.setText(hero.getTitle());
            
            // 根据英雄类型设置不同的颜色
            int heroTypeColor;
            switch (hero.getHeroType()) {
                case 1: // 战士
                    heroTypeColor = R.color.hero_type_warrior;
                    break;
                case 2: // 法师
                    heroTypeColor = R.color.hero_type_mage;
                    break;
                case 3: // 坦克
                    heroTypeColor = R.color.hero_type_tank;
                    break;
                case 4: // 刺客
                    heroTypeColor = R.color.hero_type_assassin;
                    break;
                case 5: // 射手
                    heroTypeColor = R.color.hero_type_shooter;
                    break;
                case 6: // 辅助
                    heroTypeColor = R.color.hero_type_support;
                    break;
                default:
                    heroTypeColor = R.color.hero_type_default;
                    break;
            }
            
            // 设置英雄类型Chip
            String heroTypeText = getHeroTypeText(hero.getHeroType());
            binding.chipHeroType.setText(heroTypeText);
            binding.chipHeroType.setChipBackgroundColorResource(heroTypeColor);
            
            // 加载英雄头像，使用Glide加载圆角图片
            String imageUrl = "https://game.gtimg.cn/images/yxzj/img201606/heroimg/" + hero.getEname() + "/" + hero.getEname() + ".jpg";
            Glide.with(binding.getRoot().getContext())
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                    .placeholder(R.drawable.sh_error)
                    .error(R.drawable.sh_error)
                    .into(binding.imageHero);
        }
        
        /**
         * 显示底部弹窗
         */
        private void showBottomSheetDialog(Context context, HeroRank.Hero hero) {
            // 防止在Activity销毁后调用
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
            }
            
            // 创建并显示HeroRankDetailBottomSheet
            HeroRankDetailBottomSheet detailBottomSheet = HeroRankDetailBottomSheet.newInstance(
                    hero.getCname(), "iwx", "max");
            if (context instanceof androidx.fragment.app.FragmentActivity) {
                detailBottomSheet.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "HeroRankDetail");
            }
        }
        
        private String getHeroTypeText(int heroType) {
            switch (heroType) {
                case 1:
                    return "战士";
                case 2:
                    return "法师";
                case 3:
                    return "坦克";
                case 4:
                    return "刺客";
                case 5:
                    return "射手";
                case 6:
                    return "辅助";
                default:
                    return "未知";
            }
        }
    }
} 