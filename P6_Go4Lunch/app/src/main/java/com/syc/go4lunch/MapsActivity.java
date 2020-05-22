package com.syc.go4lunch;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // ====================================== ConfigureUI
        //configureUI();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
