package my.zjh.model_guiguiapi.view.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.view.main.model.ApiItemBean;

/**
 * api列表适配器
 *
 * @author AHeng
 * @date 2025/05/02 7:42
 */
public class ApiEntryBrvAdapter extends BaseQuickAdapter<ApiItemBean, QuickViewHolder> {
    
    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable ApiItemBean apiItemBean) {
        if (apiItemBean != null) {
            quickViewHolder.setText(R.id.tv_api_title, apiItemBean.getTitle())
                    .setText(R.id.tv_is_free, String.valueOf(apiItemBean.isFree()))
                    .setText(R.id.tv_api_description, apiItemBean.getDescription());
        }
    }
    
    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.gg_item_api_card, viewGroup);
    }
}