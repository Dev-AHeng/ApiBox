package my.zjh.model_guiguiapi.view.bottomsheetfragment.model;

import androidx.annotation.NonNull;

import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchItem;

/**
 * @author AHeng
 * @date 2025/04/21 2:31
 */
public class BottomGridMenuBean {
    private int itemDrawableIcon;
    private String itemName;
    private HotSearchItem hotSearchItem;
    private OnItemClickListener clickListener;
    
    /**
     * 菜单项点击监听器接口
     */
    public interface OnItemClickListener {
        void onClick(BottomGridMenuBean item);
    }
    
    public BottomGridMenuBean() {
    
    }
    
    public BottomGridMenuBean(int itemDrawableIcon, String itemName) {
        this.itemDrawableIcon = itemDrawableIcon;
        this.itemName = itemName;
    }
    
    public BottomGridMenuBean(int itemDrawableIcon, String itemName, OnItemClickListener clickListener) {
        this.itemDrawableIcon = itemDrawableIcon;
        this.itemName = itemName;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "{" + "\"itemDrawableIcon\": " +
                       itemDrawableIcon
                       + "," + "\"itemName\": " +
                       "\"" + itemName + "\""
                       + "}";
    }
    
    public OnItemClickListener getClickListener() {
        return clickListener;
    }
    
    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
    
    public HotSearchItem getHotSearchItem() {
        return hotSearchItem;
    }
    
    public void setHotSearchItem(HotSearchItem hotSearchItem) {
        this.hotSearchItem = hotSearchItem;
    }
    
    public int getItemDrawableIcon() {
        return itemDrawableIcon;
    }
    
    public void setItemDrawableIcon(int itemDrawableIcon) {
        this.itemDrawableIcon = itemDrawableIcon;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
