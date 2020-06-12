package com.syc.go4lunch;
import com.syc.go4lunch.ui.authentication.AuthenticationFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AuthenticationActivity extends AppCompatActivity {
    //private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AuthenticationFragment.newInstance())
                    .commitNow();
        }
    }

}
