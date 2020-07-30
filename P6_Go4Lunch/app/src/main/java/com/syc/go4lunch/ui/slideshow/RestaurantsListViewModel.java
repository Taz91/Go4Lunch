package com.syc.go4lunch.ui.slideshow;

import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;

import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RestaurantsListViewModel extends ViewModel {

    public MutableLiveData<List<RestaurantModel>> getmRestaurantList() {
        return mRestaurantList;
    }

    private MutableLiveData<List<RestaurantModel>> mRestaurantList;


    public RestaurantsListViewModel() {
        this.mRestaurantList = new MutableLiveData<>();

    }
}
