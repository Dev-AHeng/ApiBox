package my.zjh.model_sanhaiapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.model.HeroRankDetailResponse;

/**
 * 英雄战力排名列表适配器
 */
public class HeroRankAdapter extends RecyclerView.Adapter<HeroRankAdapter.RankViewHolder> {
    
    private List<HeroRankDetailResponse.RankInfo> rankInfoList;
    
    public HeroRankAdapter(List<HeroRankDetailResponse.RankInfo> rankInfoList) {
        this.rankInfoList = rankInfoList;
    }
    
    /**
     * 更新数据
     */
    public void updateData(List<HeroRankDetailResponse.RankInfo> rankInfoList) {
        this.rankInfoList = rankInfoList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sh_item_hero_rank, parent, false);
        return new RankViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        HeroRankDetailResponse.RankInfo rankInfo = rankInfoList.get(position);
        holder.bind(rankInfo);
    }
    
    @Override
    public int getItemCount() {
        return rankInfoList == null ? 0 : rankInfoList.size();
    }
    
    /**
     * 排名信息ViewHolder
     */
    static class RankViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageLocation;
        private final TextView textLocation;
        private final TextView textRankValue;
        
        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLocation = itemView.findViewById(R.id.image_location);
            textLocation = itemView.findViewById(R.id.text_location);
            textRankValue = itemView.findViewById(R.id.text_rank_value);
        }
        
        /**
         * 绑定数据
         */
        public void bind(HeroRankDetailResponse.RankInfo rankInfo) {
            // 设置地区名称
            textLocation.setText(rankInfo.getAddress());
            
            // 设置战力值
            textRankValue.setText(String.valueOf(rankInfo.getRank()));
            
            // 根据级别设置不同的图标
            if ("province".equals(rankInfo.getLevel())) {
                imageLocation.setImageResource(android.R.drawable.ic_menu_compass);
            } else if ("city".equals(rankInfo.getLevel())) {
                imageLocation.setImageResource(android.R.drawable.ic_menu_mapmode);
            } else if ("district".equals(rankInfo.getLevel())) {
                imageLocation.setImageResource(android.R.drawable.ic_menu_mylocation);
            } else {
                imageLocation.setImageResource(android.R.drawable.ic_dialog_map);
            }
        }
    }
} 