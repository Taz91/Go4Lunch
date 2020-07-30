package com.syc.go4lunch.ui.home;
import com.syc.go4lunch.model.RestaurantModel;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MutableLiveData<List<RestaurantModel>> getmRestaurantList() {
        return mRestaurantList;
    }

    private MutableLiveData<List<RestaurantModel>> mRestaurantList;

    public HomeViewModel() {
        mRestaurantList = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("fragment _home : connexion sucess ");
    }

    public LiveData<String> getText() {
        return mText;
    }
}