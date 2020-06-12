package com.syc.go4lunch.ui.authentication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthenticationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AuthenticationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("fragment authentification -- ");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
