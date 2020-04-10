package com.example.favouriteplacesapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    EditText emailLoginEditText;
    EditText passwordLoginEditText;
    Button loginButton;
    String emailLogin;
    String passwordLogin;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailLoginEditText = view.findViewById(R.id.emailLoginEditText);
        passwordLoginEditText = view.findViewById(R.id.passwordLoginEditText);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin = emailLoginEditText.getText().toString().trim();
                passwordLogin = passwordLoginEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(emailLogin) && !TextUtils.isEmpty(passwordLogin)){
                    userLogIn();
                } else {
                    Toast.makeText(getContext(), "Please Enter All Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void userLogIn() {
        firebaseAuth.signInWithEmailAndPassword(emailLogin,passwordLogin)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getContext(), "Welcome To Favourite Places", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), FavouritePlaces.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}