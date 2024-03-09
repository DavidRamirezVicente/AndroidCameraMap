package com.example.androidmaps;

import android.os.Bundle;

import com.example.androidmaps.ui.register.RegisterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.androidmaps.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth auth;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_maps, R.id.navigation_camera, R.id.navigation_gallery,R.id.navigation_register)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }

        /*logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });*/

    }

