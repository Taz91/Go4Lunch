package com.syc.go4lunch;
import android.os.Bundle;
import android.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.syc.go4lunch.ui.home.HomeViewModel;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mMap;

    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // ====================================== ConfigureUI
        configureUI();

        //nav_maps
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //getSupportFragmentManager().beginTransaction().replace(R.id.container, AuthenticationFragment.newInstance()).commitNow();

        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.layout.fragment_maps);
        //mapFragment.getMapAsync((OnMapReadyCallback) this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // ===================================================================================================== Configure UI
    public void configureUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder( R.id.nav_home, R.id.nav_gallery, R.id.nav_maps, R.id.nav_slideshow, R.id.nav_slideshow_s)
                .setDrawerLayout(drawer)
                .build();
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
