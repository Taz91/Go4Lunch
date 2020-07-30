package com.syc.go4lunch.api;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;
import com.syc.go4lunch.ui.slideshow.RestaurantsListViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Go4LunchApi {
    private static final String TAG = "Go4LunchApi";

    public static void getRestaurants(Context context, RestaurantsListViewModel restaurantsListViewModel) {
        FusedLocationProviderClient mFusedLocationProviderClient;

        String API_KEY = context.getString(R.string.google_maps_key);
        // ====================================== test google Places
        // Initialize the sdk if necessary
        if (!Places.isInitialized()) {
            Places.initialize(context, API_KEY);
        }
        // Create e new Places client instance
        PlacesClient placesClient = Places.createClient(context);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        //find restaurants from current places

        //sx https://developers.google.com/places/android-sdk/current-place
        // Use fields to define the data types to return.
        //List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        //FindCurrentPlaceRequest requestk = FindCurrentPlaceRequest.newInstance(placeFields);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(
                    ((response) -> {
                        List<RestaurantModel> restaurantList = new ArrayList<>();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            if (placeLikelihood.getPlace().getTypes() != null) {
                                for (Place.Type type : placeLikelihood.getPlace().getTypes()) {
                                    if (type == Place.Type.RESTAURANT) {
                                        restaurantList.add(new RestaurantModel(placeLikelihood.getPlace().getAddress(),
                                                placeLikelihood.getPlace().getName(),
                                                placeLikelihood.getPlace().getRating() != null ? placeLikelihood.getPlace().getRating() : 0,
                                                placeLikelihood.getPlace().getLatLng()));
                                    }
                                }
                            }
                        }

                        restaurantsListViewModel.getmRestaurantList().postValue(restaurantList);

                    }
                    )

            ).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        }
    }


}
