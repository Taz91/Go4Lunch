package com.syc.go4lunch.ui.home;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syc.go4lunch.MainActivity;
import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.home_login_text) TextView loginTexthome;
    @BindView(R.id.home_buttonLocation) FloatingActionButton buttonLocation;

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    // FOR GPS PERMISSION
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERMS = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback GPSLocationCallback;

    //FOR DATA // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;


    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;                   // The entry point to the Fused Location Provider.
    private static final String TAG = MainActivity.class.getSimpleName();
    List<RestaurantModel> restaurantList = new ArrayList<>();
    
    
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loginTexthome.setText(s);
            }
        });

        homeViewModel.getmRestautantList().observe(getViewLifecycleOwner(), new Observer<List<RestaurantModel>>() {
            @Override
            public void onChanged(@Nullable List<RestaurantModel> mrestaurant) {

                //List<RestaurantModel> restaurantList = new ArrayList<>();
                for (RestaurantModel restaurant: restaurantList ) {
                    //myLocation = new LatLng(restaurant.getLatLng().latitude, restaurant.getLatLng().longitude);
                    mMap.addMarker( new MarkerOptions()
                            .position(new LatLng(restaurant.getLatLng().latitude, restaurant.getLatLng().longitude))
                            .title(restaurant.getName())
                            .draggable(true)
                    );
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLastKnowLocation();
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
        });


        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        GPSLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLocations().get(locationResult.getLocations().size()-1);
                Toast.makeText(getContext(), "Vous êtes ici: " + lastLocation.getLatitude() + " / " + lastLocation.getLatitude() + " (Nb locs : " + locationResult.getLocations().size() + " ) ", Toast.LENGTH_LONG).show();
            }
        };


        //updateLocationUI();

        fetchLastKnowLocation();

        // Prompt the user for permission.
        getLocationPermissionNew();
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
                            //List<RestaurantModel> restaurantList = new ArrayList<>();
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

                            homeViewModel.getmRestautantList().postValue(restaurantList);

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
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        /*
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        */
    }

    // --------------------
    // Permission Granted, initiate map
    // --------------------
    private void updateLocationUI() {
        /*
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        */
    }

    // --------------------
    // GET LOCATION USER
    // --------------------

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private void fetchLastKnowLocation(){
        if(! EasyPermissions.hasPermissions(requireActivity(), PERMS)){
            EasyPermissions.requestPermissions(this, "ta pas les droits !! ", RC_LOCATION_PERMS, PERMS);
            return;
        }

        fusedLocationClient  = LocationServices.getFusedLocationProviderClient(requireContext());
        LocationRequest locationRequest = new LocationRequest().setInterval(10000).setFastestInterval(5000).setSmallestDisplacement(50).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, GPSLocationCallback,null);

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng myLocation;
                if (location != null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    myLocation = new LatLng(48.5131, 2.1739);
                }
                //LatLng jardinLux = new LatLng(48.84, 2.337);
                mMap.addMarker( new MarkerOptions()
                        .position(myLocation)
                        .title("Syc in tour effel or savigny")
                        .draggable(true)
                );
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0f));


                
            }
        });

    }
    
    private void getLocationPermissionNew() {
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

}
