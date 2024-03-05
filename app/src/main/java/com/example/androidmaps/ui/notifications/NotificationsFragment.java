package com.example.androidmaps.ui.notifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidmaps.databinding.FragmentNotificationsBinding;
import com.example.androidmaps.ui.SharedViewModel;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private SharedViewModel sharedViewModel;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private List<Uri> photoUris = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        imageView = binding.imageView;

        linearLayout = binding.linearLayoutHorizontal;
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getPhotoUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                photoUris.add(uri);
                showPhotos();
            }
        });


        return root;
    }

    private void showPhotos() {
        // Limpiar el LinearLayout antes de agregar nuevas fotos
        linearLayout.removeAllViews();

        // Recorrer la lista de URIs y crear un ImageView para cada una
        for (Uri uri : photoUris) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageURI(uri);

            // Agregar el ImageView al LinearLayout
            linearLayout.addView(imageView);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}