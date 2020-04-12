package com.example.favouriteplacesapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {
    EditText nameEditText;
    EditText emailRegistrationEditText;
    EditText passwordRegistrationEditText;
    EditText confirmPasswordRegistrationEditText;
    Button registerButton;
    String name;
    String emailRegistration;
    String passwordRegistration;
    String confirmPasswordRegistration;
    String userId;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void userSignUp() {
        firebaseAuth.createUserWithEmailAndPassword(emailRegistration, passwordRegistration)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert firebaseUser != null;
                            userId = firebaseUser.getUid();
                            addUserDetailsToFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void addUserDetailsToFirebase() {
        Map<String,Object> userDetails = new HashMap<>();
        userDetails.put("userId", userId);
        userDetails.put("userName", name);
        userDetails.put("userEmail", emailRegistration);
        userDetails.put("favPlaceCount", 0);
        Map<String, Object> userFavPlacesMap = new HashMap<>();
        userDetails.put("userFavPlaces", userFavPlacesMap);

        firebaseFirestore.collection("users")
                .document(userId)
                .set(userDetails, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getActivity().finish();
                        Log.i("Info", "User Added");
                        Intent intent = new Intent(getContext(),FavouritePlaces.class);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailRegistrationEditText = view.findViewById(R.id.emailRegistrationEditText);
        passwordRegistrationEditText = view.findViewById(R.id.passwordRegistrationEditText);
        confirmPasswordRegistrationEditText = view.findViewById(R.id.confirmPasswordRegistrationEditText);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString().trim();
                emailRegistration = emailRegistrationEditText.getText().toString().trim();
                passwordRegistration = passwordRegistrationEditText.getText().toString().trim();
                confirmPasswordRegistration = confirmPasswordRegistrationEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(emailRegistration) && !TextUtils.isEmpty(passwordRegistration)
                        && !TextUtils.isEmpty(confirmPasswordRegistration)) {
                    if (passwordRegistration.length() > 6) {
                        if (passwordRegistration.equals(confirmPasswordRegistration)) {
                            userSignUp();
                        } else {
                            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Enter password with length greater than 6", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please Enter All Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}