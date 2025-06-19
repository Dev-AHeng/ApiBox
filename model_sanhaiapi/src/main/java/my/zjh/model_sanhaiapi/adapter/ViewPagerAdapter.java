package my.zjh.model_sanhaiapi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * ViewPager2适配器
 *
 * @author Dev_Heng
 */
public class ViewPagerAdapter extends FragmentStateAdapter {
    
    private final Fragment[] fragments;
    private final String[] titles;
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Fragment[] fragments, String[] titles) {
        super(fragmentActivity);
        this.fragments = fragments;
        this.titles = titles;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }
    
    @Override
    public int getItemCount() {
        return fragments.length;
    }
    
    public String getPageTitle(int position) {
        return titles[position];
    }
} 