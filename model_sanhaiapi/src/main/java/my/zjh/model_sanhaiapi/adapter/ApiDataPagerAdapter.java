package my.zjh.model_sanhaiapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_sanhaiapi.R;

/**
 * API数据ViewPager适配器
 * 
 * @author AHeng
 * @date 2025/03/26
 */
public class ApiDataPagerAdapter extends RecyclerView.Adapter<ApiDataPagerAdapter.ApiDataViewHolder> {
    
    // API名称列表
    private List<String> apiNames = new ArrayList<>();
    // API数据列表
    private List<JsonElement> apiDataList = new ArrayList<>();
    // Gson实例，用于美化JSON输出
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 设置API数据
     *
     * @param apiName API名称
     * @param jsonData API数据
     */
    public void addApiData(String apiName, JsonElement jsonData) {
        apiNames.add(apiName);
        apiDataList.add(jsonData);
        notifyItemInserted(apiNames.size() - 1);
    }
    
    /**
     * 清除所有数据
     */
    public void clearData() {
        int size = apiNames.size();
        apiNames.clear();
        apiDataList.clear();
        notifyItemRangeRemoved(0, size);
    }
    
    /**
     * 获取API名称列表
     *
     * @return API名称列表
     */
    public List<String> getApiNames() {
        return apiNames;
    }
    
    @NonNull
    @Override
    public ApiDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sh_item_api_data, parent, false);
        return new ApiDataViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ApiDataViewHolder holder, int position) {
        if (position < apiDataList.size()) {
            JsonElement jsonElement = apiDataList.get(position);
            String prettyJson = gson.toJson(jsonElement);
            holder.tvApiData.setText(prettyJson);
        }
    }
    
    @Override
    public int getItemCount() {
        return apiDataList.size();
    }
    
    /**
     * API数据ViewHolder
     */
    static class ApiDataViewHolder extends RecyclerView.ViewHolder {
        TextView tvApiData;
        
        ApiDataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApiData = itemView.findViewById(R.id.tv_api_data);
        }
    }
} 