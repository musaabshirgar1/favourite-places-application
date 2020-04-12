package com.example.favouriteplacesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity{

    TextView typeTextView;
    boolean newUserOrNot = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        typeTextView = findViewById(R.id.typeTextView);


        getSupportFragmentManager().
                beginTransaction()
                .add(R.id.fragment_container, new LoginFragment())
                .commit();

        typeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newUserOrNot){
                    newUserOrNot = true;
                    typeTextView.setText("Old User? Log in");
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container , new RegisterFragment())
                            .commit();
                } else if(newUserOrNot){
                    newUserOrNot = false;
                    typeTextView.setText("New User? Register");
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container , new LoginFragment())
                            .commit();
                }
            }
        });


    }

}