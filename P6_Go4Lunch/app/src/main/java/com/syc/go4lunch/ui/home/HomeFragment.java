package com.syc.go4lunch.ui.home;
import com.syc.go4lunch.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    //@BindView(R.id.main_news) CoordinatorLayout mainNews;
    @BindView(R.id.login_btn) Button loginButton;
    @BindView(R.id.login_texthome) TextView loginTexthome;

    //FOR DATA // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);


        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                loginTexthome.setText(s);
            }
        });
        return root;
    }

}
