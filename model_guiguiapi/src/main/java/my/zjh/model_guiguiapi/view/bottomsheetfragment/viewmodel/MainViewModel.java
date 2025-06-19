package my.zjh.model_guiguiapi.view.bottomsheetfragment.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * MainActivity的ViewModel，负责管理UI相关的数据
 *
 * @author AHeng
 * @date 2025/04/23 22:10
 */
@HiltViewModel
public class MainViewModel extends ViewModel {

    private final MutableLiveData<Integer> selectedBottomSheetState = new MutableLiveData<>();

    @Inject
    public MainViewModel() {
    }

    /**
     * 获取选中的底部表单状态LiveData
     */
    public LiveData<Integer> getSelectedBottomSheetState() {
        return selectedBottomSheetState;
    }

    /**
     * 设置底部表单状态
     */
    public void setBottomSheetState(int state) {
        selectedBottomSheetState.setValue(state);
    }

} 