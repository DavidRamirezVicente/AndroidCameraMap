package com.example.androidmaps.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.androidmaps.MainActivity;
import com.example.androidmaps.R;
import com.example.androidmaps.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button registerButton;
    private FirebaseAuth mAuth;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.emailRegister);
        editTextPassword = view.findViewById(R.id.password);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Login successfully.", Toast.LENGTH_SHORT).show();
                                            navigateToDashboard();
                                        } else {
                                            Log.w("RegisterFragment", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Authentication created.", Toast.LENGTH_SHORT).show();
                                    navigateToDashboard();
                                } else {
                                    Log.w("RegisterFragment", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }
    private void navigateToDashboard() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), DashboardFragment.class);
            startActivity(intent);
            getActivity().finish();
        }
    }


}
