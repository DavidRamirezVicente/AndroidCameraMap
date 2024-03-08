package com.example.androidmaps.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidmaps.R;
import com.example.androidmaps.databinding.FragmentGalleryBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private RecyclerView recyclerView;
    private List<Uri> photoUris = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3)); // Mostrar 3 columnas
        recyclerView.setAdapter(new PhotoAdapter());

        // Obtener referencia al FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Aquí debes tener una referencia a la carpeta en Firebase Storage donde están almacenadas tus fotos
        StorageReference imagesRef = storageRef.child("images");

        // Listar los elementos dentro de la carpeta "images"
        imagesRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                // Obtener la URL de descarga de cada imagen
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Agregar la URL de la imagen a la lista
                    photoUris.add(uri);
                    // Notificar al adaptador que se han añadido nuevos datos
                    recyclerView.getAdapter().notifyDataSetChanged();
                });
            }
        }).addOnFailureListener(e -> {
            // Manejar el error si la lista de imágenes no se puede recuperar
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Uri uri = photoUris.get(position);
            Glide.with(requireContext()).load(uri).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return photoUris.size();
        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
