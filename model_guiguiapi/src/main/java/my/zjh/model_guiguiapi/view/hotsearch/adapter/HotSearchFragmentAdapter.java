package my.zjh.model_guiguiapi.view.hotsearch.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import my.zjh.model_guiguiapi.view.hotsearch.fragment.HotSearchListFragment;

/**
 * 热搜页面的Fragment适配器，用于管理ViewPager2中的多个热搜列表Fragment
 * 
 * @author AHeng
 * @date 2025/04/14 6:02
 * @status ok
 */
public class HotSearchFragmentAdapter extends FragmentStateAdapter {
    private final List<String> hotSearchItems;
    
    public HotSearchFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<String> hotSearchItems) {
        super(fragmentManager, lifecycle);
        this.hotSearchItems = new ArrayList<>(hotSearchItems);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 使用更优化的方式创建Fragment
        return HotSearchListFragment.newInstance(hotSearchItems.get(position));
    }
    
    @Override
    public int getItemCount() {
        return hotSearchItems.size();
    }
    
    @Override
    public long getItemId(int position) {
        // 使用稳定的ID策略，基于内容的哈希值而不是位置
        // 这样可以确保Fragment在数据集变化时保持其状态
        return hotSearchItems.get(position).hashCode();
    }
    
    @Override
    public boolean containsItem(long itemId) {
        // 检查给定的itemId是否存在于当前数据集中
        for (String item : hotSearchItems) {
            if (item.hashCode() == itemId) {
                return true;
            }
        }
        return false;
    }
    
}
