package com.syc.go4lunch.ui.authentication;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.syc.go4lunch.MainActivity;
import com.syc.go4lunch.R;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import static android.app.Activity.RESULT_OK;
import static com.syc.go4lunch.utils.Utils.checkConnection;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class AuthenticationFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private AuthenticationViewModel authenticationViewModel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static final int RC_PERMS_IN = 333;

    @BindView(R.id.authentication_message) TextView authentication_message;
    @BindView(R.id.authentication_result) TextView authentication_result;

    public static AuthenticationFragment newInstance() {

        return new AuthenticationFragment();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        View root = inflater.inflate(R.layout.authentication_fragment, container, false);
        ButterKnife.bind(this, root);

        authenticationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                authentication_message.setText(s);

            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        // TODO: Use the ViewModel


        if(checkConnection(getActivity())){
            startConnect();
        }else{
            authentication_message.setText("Connection failed !!");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }

        // 4 - Handle SignIn Activity response on activity result
        if(requestCode == RC_SIGN_IN){
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
        }

    }

    // 3 - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // SUCCESS
                authentication_result.setText(getString(R.string.connection_succeed));
                openMainActivity();

            } else { // ERRORS
                if (response == null) {
                    authentication_result.setText(getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    authentication_result.setText(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    authentication_result.setText(getString(R.string.error_unknown_error));
                }
            }
        }
    }

    /**
     * launch activity authentication with firebase
     */
    public void startConnect(){

        checkConnection(getActivity());

        permissionGranted();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                // ,new AuthUI.IdpConfig.PhoneBuilder().build()
                ,new AuthUI.IdpConfig.GoogleBuilder().build()
                //,new AuthUI.IdpConfig.FacebookBuilder().build()
                // ,new AuthUI.IdpConfig.TwitterBuilder().build()
                 );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(true)
                        .setLogo(R.drawable.go4lunch_icon)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
        /*
        //mAuth = FirebaseAuth.getInstance();
        //FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null ){
            String idUser = currentUser.getUid();
            openMainActivity();
        }else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
        */
    }

    @AfterPermissionGranted(RC_PERMS_IN)
    public void permissionGranted(){
        String[] perms = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET};
        if(EasyPermissions.hasPermissions(getContext(),perms)){
            Toast.makeText(getContext(), "y a le droit ?",Toast.LENGTH_LONG).show();
        }else{
            EasyPermissions.requestPermissions(this,"besoin d'accorder une permission !!", RC_PERMS_IN, perms );
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms )){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public void openMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
