package my.zjh.model_sanhaiapi.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * API ViewModelFactory
 * 用于创建需要特殊初始化的ViewModel
 *
 * @author AHeng
 * @date 2025/03/25 08:57
 */
public class ApiViewModelFactory implements ViewModelProvider.Factory {

    // 可能需要的参数
    private final Object[] params;

    /**
     * 构造函数
     *
     * @param params ViewModel可能需要的参数
     */
    public ApiViewModelFactory(Object... params) {
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // 根据ViewModel类型，创建对应的ViewModel实例
        if (modelClass.isAssignableFrom(ApiListViewModel.class)) {
            return (T) new ApiListViewModel();
        } else if (modelClass.isAssignableFrom(YiyanViewModel.class)) {
            return (T) new YiyanViewModel();
        } else if (modelClass.isAssignableFrom(MiguMusicViewModel.class)) {
            return (T) new MiguMusicViewModel();
        } else if (modelClass.isAssignableFrom(IPAddressQueryViewModel.class)) {
            return (T) new IPAddressQueryViewModel();
        } else if (modelClass.isAssignableFrom(LocalIPViewModel.class)) {
            return (T) new LocalIPViewModel();
        } else if (modelClass.isAssignableFrom(ICPFilingViewModel.class)) {
            return (T) new ICPFilingViewModel();
        } else if (modelClass.isAssignableFrom(QQInfoViewModel.class)) {
            return (T) new QQInfoViewModel();
        }
        
        // 如果没有匹配的ViewModel类型，抛出异常
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
} 