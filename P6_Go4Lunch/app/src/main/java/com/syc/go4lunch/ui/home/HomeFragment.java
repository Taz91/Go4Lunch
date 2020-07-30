package com.syc.go4lunch.ui.home;
import android.location.Location;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syc.go4lunch.MainActivity;
import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;
import com.syc.go4lunch.ui.slideshow.RestaurantsListViewModel;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.home_login_text) TextView loginTexthome;
    @BindView(R.id.home_buttonLocation) FloatingActionButton buttonLocation;

    private HomeViewModel homeViewModel;
    private RestaurantsListViewModel restaurantsListViewModel;

    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private static final String TAG = MainActivity.class.getSimpleName();
    //List<RestaurantModel> restaurantList = new ArrayList<>();

    private HomePresenter homePresenter;

    
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        restaurantsListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        homePresenter = new HomePresenter(this);
        homePresenter.init(restaurantsListViewModel);


        restaurantsListViewModel.getmRestaurantList().observe(getViewLifecycleOwner(), new Observer<List<RestaurantModel>>() {
            @Override
            public void onChanged(@Nullable List<RestaurantModel> mrestaurant) {

                //TODO : delete all marker and add myPosition
                //mMap.clear();

                //List<RestaurantModel> restaurantList = new ArrayList<>();
                for (RestaurantModel restaurant: mrestaurant ) {
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
                homePresenter.fetchLastKnowLocation();
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
        });


        return root;
    }

    public void onLocationUpdated(Location location){
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
        Toast.makeText(getContext(), "Vous êtes ici: " + location.getLatitude() + " / " + location.getLatitude(), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

}
