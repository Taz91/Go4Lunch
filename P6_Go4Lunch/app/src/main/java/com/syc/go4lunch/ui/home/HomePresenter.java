package com.syc.go4lunch.ui.home;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.syc.go4lunch.api.Go4LunchApi;
import com.syc.go4lunch.ui.slideshow.RestaurantsListViewModel;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomePresenter {
    HomeFragment homeFragment;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //FOR DATA // 1 - Identifier for Sign-In Activity
    //private static final int RC_SIGN_IN = 123;
    
    private FusedLocationProviderClient fusedLocationClient;
    // FOR GPS PERMISSION
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERMS = 100;
    
    HomePresenter (HomeFragment homeFragment){
        this.homeFragment = homeFragment;
        
    }
    
    public void init(RestaurantsListViewModel restaurantsListViewModel){
        Go4LunchApi.getRestaurants(homeFragment.getContext(), restaurantsListViewModel);
        // Prompt the user for permission.
        mLocationPermissionGranted = ContextCompat.checkSelfPermission(homeFragment.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (mLocationPermissionGranted) {
            fetchLastKnowLocation();
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            EasyPermissions.requestPermissions(homeFragment.getActivity(), "ta pas les droits !! ", RC_LOCATION_PERMS, PERMS);
        }
    }
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    public void fetchLastKnowLocation(){
        if(! EasyPermissions.hasPermissions(homeFragment.getActivity(), PERMS)){
            EasyPermissions.requestPermissions(homeFragment.getActivity(), "ta pas les droits !! ", RC_LOCATION_PERMS, PERMS);
            return;
        }

        fusedLocationClient  = LocationServices.getFusedLocationProviderClient(homeFragment.getContext());
        LocationRequest locationRequest = new LocationRequest().setInterval(10000).setFastestInterval(5000).setSmallestDisplacement(50).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLocations().get(locationResult.getLocations().size()-1);
                homeFragment.onLocationUpdated(lastLocation);
            }
        },null);

        fusedLocationClient.getLastLocation().addOnSuccessListener(homeFragment.getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                homeFragment.onLocationUpdated(location);

            }
        });

    }

}
