package com.syc.go4lunch.ui.slideshow;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.stats.StatsEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.syc.go4lunch.MainActivity;
import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//implements ConnectionCallbacks, OnConnectionFailedListener

public class SlideshowFragment extends Fragment {
    private SlideshowViewModel slideshowViewModel;
    private static final String TAG = MainActivity.class.getSimpleName();

    private PlacesClient mPlacesClient;                                                 // The entry point to the Places API.
    private FusedLocationProviderClient mFusedLocationProviderClient;                   // The entry point to the Fused Location Provider.
    // FOR GPS PERMISSION
    protected static final String PERMS = ACCESS_FINE_LOCATION;
    protected static final int RC_LOCATION_PERMS = 100;
    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(48.5131, 2.1739);        //tour eiffel
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known, location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                textView.setText(s);
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        if (mLocationPermissionGranted) {
            String API_KEY = getString(R.string.google_maps_key);
            // ====================================== test google Places
            // Initialize the sdk if necessary
            if(!Places.isInitialized()){
                Places.initialize(getContext(),API_KEY);
            }
            // Create e new Places client instance
            PlacesClient placesClient = Places.createClient(getContext());
            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

            //find restaurants from current places

            //sx https://developers.google.com/places/android-sdk/current-place
            // Use fields to define the data types to return.
            //List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.LAT_LNG );

            // Use the builder to create a FindCurrentPlaceRequest.
            //FindCurrentPlaceRequest requestk = FindCurrentPlaceRequest.newInstance(placeFields);
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

            // Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                placesClient.findCurrentPlace(request).addOnSuccessListener(
                        ((response) -> {
                                            List<RestaurantModel> restaurantList = new ArrayList<>();
                                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                                if(placeLikelihood.getPlace().getTypes() != null ){
                                                    for (Place.Type type : placeLikelihood.getPlace().getTypes() ) {
                                                        if(type == Place.Type.RESTAURANT){
                                                            restaurantList.add(new RestaurantModel(placeLikelihood.getPlace().getAddress(),
                                                                                                    placeLikelihood.getPlace().getName(),
                                                                                                    placeLikelihood.getPlace().getRating() != null ? placeLikelihood.getPlace().getRating(): 0,
                                                                                                    placeLikelihood.getPlace().getLatLng()));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                        )

                ).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            } else {
                // A local method to request required permissions;
                // See https://developer.android.com/training/permissions/requesting
                getLocationPermission();
            }

            // Call findCurrentPlace and handle the response (first check that the user has granted permission).
            /*
            if (ContextCompat.checkSelfPermission(this.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
            */
                        // ===========================================================================================================
                        // exemple google : https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
                        //
                        //
                        // init var à récupérer ...

                        /*
                        PlaceLikelihood{
                            place=
                                    Place{
                                        address=Parc du Champ-de-Mars, 75007 Paris, France,
                                        addressComponents=null,
                                        businessStatus=null,
                                        attributions=null,
                                        id=null,
                                        latLng=null,
                                        name=Tour eiffel,
                                        openingHours=null,
                                        phoneNumber=null,
                                        photoMetadatas=null,
                                        plusCode=null,
                                        priceLevel=null,
                                        rating=4.6,
                                        types=[	MUSEUM, POINT_OF_INTEREST, ESTABLISHMENT],
                                        userRatingsTotal=null,
                                        utcOffsetMinutes=null,
                                        viewport=null,
                                        websiteUri=null},
                            likelihood=0.2020877647399902}
                         */
                        // ===========================================================================================================
            /*
                        FindCurrentPlaceResponse response = task.getResult();

                        String type = "";
                        List<RestaurantModel> restaurantList = new ArrayList<>();

                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                            //ici on devrait charger le modèle !!!!!!!

                            if(placeLikelihood.getPlace().getTypes() != null && placeLikelihood.getPlace().getTypes().contains("RESTAURANT") ){
                                restaurantList.add(new RestaurantModel(placeLikelihood.getPlace().getAddress(), placeLikelihood.getPlace().getName(), placeLikelihood.getPlace().getRating() ));
                            }
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
            } else {
                // A local method to request required permissions;
                // See https://developer.android.com/training/permissions/requesting
                getLocationPermission();
            }
            */

        }
        return root;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                GoogleMap mMap = null;
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Executor) this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the device. The result of the permission request is handled by a callback, onRequestPermissionsResult.
         * google place sdk sources
         */
        if (ContextCompat.checkSelfPermission(this.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //updateLocationUI();
    }

}
