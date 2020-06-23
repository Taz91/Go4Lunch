package com.syc.go4lunch.ui.slideshow;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.syc.go4lunch.MainActivity;
import com.syc.go4lunch.R;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class RestaurantList extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

        private SlideshowViewModel slideshowViewModel;

        private static final String TAG = MainActivity.class.getSimpleName();
        private GoogleMap mMap;
        private CameraPosition mCameraPosition;

        // The entry point to the Places API.
        private PlacesClient mPlacesClient;
        // The entry point to the Fused Location Provider.
        private FusedLocationProviderClient mFusedLocationProviderClient;

        // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
        private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
        private static final int DEFAULT_ZOOM = 15;
        private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private boolean mLocationPermissionGranted;

        // The geographical location where the device is currently located. That is, the last-known, location retrieved by the Fused Location Provider.
        private Location mLastKnownLocation;

        // Keys for storing activity state.
        private static final String KEY_CAMERA_POSITION = "camera_position";
        private static final String KEY_LOCATION = "location";

        // Used for selecting the current place.
        private static final int M_MAX_ENTRIES = 5;
        private String[] mLikelyPlaceNames;
        private String[] mLikelyPlaceAddresses;
        private List[] mLikelyPlaceAttributions;
        private LatLng[] mLikelyPlaceLatLngs;



        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
            View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
            final TextView textView = root.findViewById(R.id.text_slideshow);
            slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {

                    textView.setText(s);
                }
            });

            // ====================================== test google Places
            // Initialize the sdk if necessary
            if(!Places.isInitialized()){
                Places.initialize(getContext(),getString(R.string.google_api_key));
            }
            // Create e new Places client instance
            PlacesClient placesClient = Places.createClient(getContext());

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();

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
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
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
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
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
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
            }
            updateLocationUI();
        }



        /**
         * Prompts the user to select the current place from a list of likely places, and shows the
         * current place on the map - provided the user has granted location permission.
         */
        private void showCurrentPlace() {
            if (mMap == null) {
                return;
            }

            if (mLocationPermissionGranted) {
                // Use fields to define the data types to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG);

                // Use the builder to create a FindCurrentPlaceRequest.
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Get the likely places - that is, the businesses and other points of interest that
// are the best match for the device's current location.
                @SuppressWarnings("MissingPermission") final
                Task<FindCurrentPlaceResponse> placeResult =
                        mPlacesClient.findCurrentPlace(request);
                placeResult.addOnCompleteListener (new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FindCurrentPlaceResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new List[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                                // Build a list of likely places to show the user.
                                mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                                mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                                mLikelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                        .getAttributions();
                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.
                            //MainActivity.this.openPlacesDialog();
                        }
                        else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            } else {
                // The user has not granted permission.
                //Log.i(TAG, "The user did not grant location permission.");

                // Add a default marker, because the user hasn't selected a place.
                //mMap.addMarker(new MarkerOptions().title(getString(R.string.default_info_title)).position(mDefaultLocation).snippet(getString(R.string.default_info_snippet)));

                // Prompt the user for permission.
                getLocationPermission();
            }
        }

        /**
         * Displays a form allowing the user to select a place from a list of likely places.
         */
        private void openPlacesDialog() {
            // Ask the user to choose the place where they are now.
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // The "which" argument contains the position of the selected item.
                    LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                    String markerSnippet = mLikelyPlaceAddresses[which];
                    if (mLikelyPlaceAttributions[which] != null) {
                        markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                    }

                    // Add a marker for the selected place, with an info window showing information about that place.
                    //mMap.addMarker(new MarkerOptions().title(mLikelyPlaceNames[which]).position(markerLatLng).snippet(markerSnippet));

                    // Position the map's camera at the location of the marker.
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, DEFAULT_ZOOM));
                }
            };

            // Display the dialog.
            //AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(R.string.pick_place).setItems(mLikelyPlaceNames, listener).show();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
        /**
         * Updates the map's UI settings based on whether the user has granted location permission.
         */
    private void updateLocationUI() {
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
    }

}