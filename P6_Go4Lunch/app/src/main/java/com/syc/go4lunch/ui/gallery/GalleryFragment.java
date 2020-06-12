package com.syc.go4lunch.ui.gallery;
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

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    @BindView(R.id.login_btn) Button loginButton;
    @BindView(R.id.text_gallery) TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =  ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, root);

        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                textView.setText(s);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsPosition = new Intent(getContext(), MapsActivity.class);
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
