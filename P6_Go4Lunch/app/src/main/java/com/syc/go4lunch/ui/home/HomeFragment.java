package com.syc.go4lunch.ui.home;
import com.syc.go4lunch.MapsActivity;
import com.syc.go4lunch.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    @BindView(R.id.login_btn) Button loginButton;
    @BindView(R.id.login_texthome) TextView loginTexthome;

    //FOR DATA // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                loginTexthome.setText(s);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsPosition = new Intent(getContext(),MapsActivity.class);
                mapsPosition.putExtra("latitude", -34);
                mapsPosition.putExtra("longitude", 151);
                mapsPosition.putExtra("title", "live in Sydney");
                if(mapsPosition != null  ){
                    ContextCompat.startActivity(getContext() ,mapsPosition,null);
                }
            }
        });

        return root;
    }

}
