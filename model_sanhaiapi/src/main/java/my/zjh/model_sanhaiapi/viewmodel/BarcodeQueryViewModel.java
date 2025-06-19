package my.zjh.model_sanhaiapi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import my.zjh.model_sanhaiapi.api.BarcodeQueryService;
import my.zjh.model_sanhaiapi.api.RetrofitClient;
import my.zjh.model_sanhaiapi.model.BarcodeQueryResponse;

/**
 * @author AHeng
 * @date 2025/03/31 1:15
 */
public class BarcodeQueryViewModel extends ViewModel {
    private final BarcodeQueryService retrofitClient;
    private final MutableLiveData<BarcodeQueryResponse> responseData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRequestSuccessful = new MutableLiveData<>();
    
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    
    public BarcodeQueryViewModel() {
        retrofitClient = RetrofitClient.getInstance().getBarcodeQueryService();
    }
    
    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        
        super.onCleared();
    }
    
    public LiveData<BarcodeQueryResponse> getResponseData() {
        return responseData;
    }
    
    public LiveData<Boolean> isRequestSuccessful() {
        return isRequestSuccessful;
    }
    
    public void query(String barcode) {
        compositeDisposable.add(
                retrofitClient.query(barcode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(barcodeQueryResponse -> {
                            isRequestSuccessful.setValue(barcodeQueryResponse.getCode() == 1);
                            responseData.setValue(barcodeQueryResponse);
                        }, throwable -> isRequestSuccessful.setValue(false))
        );
        
    }
}
