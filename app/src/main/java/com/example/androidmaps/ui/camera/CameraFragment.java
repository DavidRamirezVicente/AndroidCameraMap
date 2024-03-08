package com.example.androidmaps.ui.camera;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidmaps.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageButton photo, flipCamera;
    private ImageView minatura;
    private Uri lastPhotoUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private int cameraFacing = CameraSelector.LENS_FACING_BACK;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startCamera(cameraFacing);
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        previewView = binding.preview;
        photo = binding.photo;
        minatura= binding.imageView2;
        minatura.setVisibility(View.INVISIBLE);
        flipCamera = binding.rotate;

        startCamera(cameraFacing);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraPermission()){
                    capturePhoto();
                }else {
                    requestCameraPermission();
                }
            }
        });
        flipCamera.setOnClickListener(view -> {

            cameraFacing = (cameraFacing == CameraSelector.LENS_FACING_BACK) ?
                    CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
            startCamera(cameraFacing);
        });

        return root;
    }
    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAudioPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasWriteExternalStoragePermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void requestAudioPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
    }

    private void requestWriteExternalStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    private void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> processCameraProviderListenableFuture = ProcessCameraProvider.getInstance(requireContext());
        processCameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = processCameraProviderListenableFuture.get();
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    imageCapture = new ImageCapture.Builder()
                            .setTargetRotation(previewView.getDisplay().getRotation())
                            .build();
                    provider.unbindAll();

                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraFacing).build();
                    Camera camera = provider.bindToLifecycle(CameraFragment.this, cameraSelector, preview, imageCapture);


                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
        /*if (lastPhotoUri != null) {
            minatura.setVisibility(View.VISIBLE);
            minatura.setImageURI(lastPhotoUri);
            minatura.setTag(lastPhotoUri);
        } else {
            minatura.setVisibility(View.GONE);
        }*/
    }
    private void capturePhoto() {
        if (imageCapture == null) {
            Toast.makeText(requireContext(), "Error: ImageCapture no está disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSS", Locale.getDefault()).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "CameraX");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(requireContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        minatura.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Foto guardada exitosamente", Toast.LENGTH_SHORT).show();
                        Uri saveURI = outputFileResults.getSavedUri();
                        minatura.setImageURI(saveURI);
                        minatura.setTag(saveURI);
                        lastPhotoUri = saveURI;
                        uploadImageToFirebaseStorage(saveURI);
                    }


                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(requireContext(), "Error al guardar la foto: ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "La URI de la imagen es nula", Toast.LENGTH_SHORT).show();
            return;
        }


        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageName = "image_" + timestamp + ".jpg";


        StorageReference imageRef = storageReference.child("images/" + imageName);


        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(requireContext(), "Imagen subida exitosamente a Firebase Storage", Toast.LENGTH_SHORT).show();
                    // Aquí puedes obtener la URL de descarga de la imagen subida
                    // imageRef.getDownloadUrl().addOnSuccessListener...
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al subir la imagen a Firebase Storage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}