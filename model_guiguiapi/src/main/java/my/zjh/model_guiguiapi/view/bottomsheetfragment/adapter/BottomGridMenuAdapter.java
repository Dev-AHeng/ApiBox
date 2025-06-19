package my.zjh.model_guiguiapi.view.bottomsheetfragment.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;

import my.zjh.model_guiguiapi.R;
import my.zjh.model_guiguiapi.util.SizeUtils;
import my.zjh.model_guiguiapi.view.bottomsheetfragment.model.BottomGridMenuBean;

/**
 * 设置数据集合
 * <p>
 * adapter.submitList(list)
 * 修改某一位置的数据
 * <p>
 * //修改index为1处的数据
 * adapter[1] = data
 * 新增数据
 * <p>
 * // 尾部新增数据
 * adapter.add(data)
 * <p>
 * // 在指定位置添加一条新数据
 * adapter.add(1, data)
 * <p>
 * // 添加数据集
 * adapter.addAll(list)
 * <p>
 * // 指定位置添加数据集
 * adapter.addAll(1, list)
 * 删除数据
 * <p>
 * // 删除数据
 * adapter.remove(data)
 * <p>
 * // 删除指定位置数据
 * adapter.removeAt(1)
 * 交换数据位置（仅仅是这两个数据的位置交换）
 * <p>
 * // 交换两个位置的数据
 * adapter.swap(1, 3)
 * 移动数据位置（注意和 swap 的区别）
 * <p>
 * // 交换两个位置的数据
 * adapter.move(1, 3)
 * 获取Item数据的索引
 * <p>
 * // 如果返回 -1，表示不存在
 * adapter.itemIndexOfFirst(data)
 * 根据索引，获取Item数据
 * <p>
 * // 如果返回 null，表示没有数据
 * adapter.getItem(1)
 *
 * @author AHeng
 * @date 2025/04/21 2:28
 */
public class BottomGridMenuAdapter extends BaseQuickAdapter<BottomGridMenuBean, QuickViewHolder> {
    private final int iconSize;
    private Context context;
    // 缓存Drawable对象，减少重复创建
    private SparseArray<InsetDrawable> drawableCache = new SparseArray<>();
    
    public BottomGridMenuAdapter(Context context) {
        this.context = context;
        iconSize = SizeUtils.dp2px(42);
    }
    
    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable BottomGridMenuBean bottomGridMenuBean) {
        if (bottomGridMenuBean != null) {
            int drawableResId = bottomGridMenuBean.getItemDrawableIcon();
            
            // 从缓存获取或创建新的InsetDrawable
            InsetDrawable insetDrawable = getInsetDrawableFromCache(drawableResId);
            
            // 应用到 ImageView
            AppCompatImageView icon = quickViewHolder.getView(R.id.icon);
            icon.setImageDrawable(insetDrawable);
            
            AppCompatTextView textView = quickViewHolder.getView(R.id.text);
            textView.setText(bottomGridMenuBean.getItemName());
            // 在RecyclerView/ListView中TextView跑马灯要设置这个
            textView.setSelected(true);
        }
    }
    
    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        // 如果context为null，则初始化
        if (this.context == null) {
            this.context = context;
        }
        return new QuickViewHolder(R.layout.gg_item_bottom, viewGroup);
    }
    
    /**
     * 从缓存中获取InsetDrawable，如果不存在则创建并缓存
     */
    private InsetDrawable getInsetDrawableFromCache(int drawableResId) {
        InsetDrawable insetDrawable = drawableCache.get(drawableResId);
        if (insetDrawable == null) {
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            // drawable.setTint(Color.parseColor("#bababa"));
            insetDrawable = new InsetDrawable(drawable, 0, 0, 0, 0) {
                @Override
                public int getIntrinsicWidth() {
                    return iconSize;
                }
                
                @Override
                public int getIntrinsicHeight() {
                    return iconSize;
                }
            };
            drawableCache.put(drawableResId, insetDrawable);
        }
        return insetDrawable;
    }
    
    /**
     * 释放资源
     */
    public void release() {
        if (drawableCache != null) {
            drawableCache.clear();
            drawableCache = null;
        }
        context = null;
    }
}
