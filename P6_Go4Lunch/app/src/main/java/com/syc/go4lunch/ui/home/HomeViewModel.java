package com.syc.go4lunch.ui.home;
import com.syc.go4lunch.model.RestaurantModel;
import com.syc.go4lunch.ui.slideshow.RestaurantList;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MutableLiveData<List<RestaurantModel>> getmRestautantList() {
        return mRestautantList;
    }

    private MutableLiveData<List<RestaurantModel>> mRestautantList;


    public HomeViewModel() {
        mRestautantList = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("fragment _home : connexion sucess ");
    }

    public LiveData<String> getText() {
        return mText;
    }
}