package my.zjh.model_guiguiapi.view.bottomsheetfragment.adapter;

import android.content.Context;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * 底部菜单适配器工厂，负责创建BottomGridMenuAdapter实例
 *
 * @author AHeng
 * @date 2025/04/23 21:50
 */
public class BottomGridMenuAdapterFactory {

    private final Context context;

    @Inject
    public BottomGridMenuAdapterFactory(@ApplicationContext Context context) {
        this.context = context;
    }

    /**
     * 创建新的BottomGridMenuAdapter实例
     */
    public BottomGridMenuAdapter create() {
        return new BottomGridMenuAdapter(context);
    }
} 