package com.example.androidmaps.ui;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Uri> photoUri = new MutableLiveData<>();

    public void setPhotoUri(Uri uri) {
        photoUri.setValue(uri);
    }

    public LiveData<Uri> getPhotoUri() {
        return photoUri;
    }
}
