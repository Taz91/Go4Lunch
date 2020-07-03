package com.syc.go4lunch.ui.home;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syc.go4lunch.R;
import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loginTexthome.setText(s);
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

        /*
        LatLng chatelet = new LatLng(48.86, 2.34);
        mMap.addMarker( new MarkerOptions()
                .position(chatelet)
                .title("Syc in Chatelet")
                .draggable(true)
        );
        LatLng jardinLux = new LatLng(48.84, 2.337);
        mMap.addMarker( new MarkerOptions()
                .position(jardinLux)
                .title("Syc in Jardin Luxembour")
                .draggable(true)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chatelet));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jardinLux));
        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);
        */
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

}
