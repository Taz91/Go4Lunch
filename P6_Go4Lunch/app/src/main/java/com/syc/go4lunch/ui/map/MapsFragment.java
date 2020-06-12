package com.syc.go4lunch.ui.map;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.syc.go4lunch.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import static com.syc.go4lunch.R.layout.fragment_maps;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapsViewModel mapsViewModel;
    private MapView mapView;

    public MapsFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapsViewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        View root = inflater.inflate(fragment_maps, container, false);
        final TextView textMap = root.findViewById(R.id.text_map);
        mapsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                textMap.setText(s);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //this.googleMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap = googleMap;

        // test remplissage via intent
        /*
        String title= "";
        Intent mapsIntent = getIntent();
        if(mapsIntent != null  ){
            title = mapsIntent.getStringExtra("title");
            LatLng sydney = new LatLng(mapsIntent.getDoubleExtra("latitude", -34),mapsIntent.getDoubleExtra("longitude", 151));
            mMap.addMarker(new MarkerOptions().position(sydney).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        */

        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
         mMap.addMarker( new MarkerOptions()
                            .position(sydney)
                            .title("Syc in Sydney")
                            .draggable(true)
                        );
         */
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


    }

}