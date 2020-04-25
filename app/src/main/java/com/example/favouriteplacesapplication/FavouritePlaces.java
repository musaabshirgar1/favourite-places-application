package com.example.favouriteplacesapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.Timestamp;
import org.w3c.dom.Document;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class FavouritePlaces extends AppCompatActivity implements OpenDialogBox.SendDataToFavouritePlace {

    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    StorageReference storageReference;
    StorageReference fileReference;
    StorageTask storageTask;


    boolean doubleBackToExitPressedOnce = false;

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    ArrayList<FavPlaces> favPlacesArrayList;

    String userId;
    long favPlaceCount;
    double favPlaceLat;
    double favPlaceLon;
    Uri imageUri;
    String nameOfPlace;
    Date currentDate;
    String imageUrl;

    long numberOfFavPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_places);

        progressBar = findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(FavouritePlaces.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userId = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("users").document(userId);
        storageReference = FirebaseStorage.getInstance().getReference(userId);

        recyclerView = findViewById(R.id.recyclerView);

        favPlacesArrayList = new ArrayList<>();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    progressDialog.dismiss();
                    Toast.makeText(FavouritePlaces.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    favPlacesArrayList.clear();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        try {
                            Map<String, Object> userFavPlacesMap = (Map<String, Object>) documentSnapshot.getData().get("userFavPlaces");
                            Log.i("Info", userFavPlacesMap.toString());
                            for (Map.Entry<String, Object> entry : userFavPlacesMap.entrySet()) {

                                Map<String, Object> favPlacesMap = (Map<String, Object>) entry.getValue();
                                long id = (long) favPlacesMap.get("favPlaceId");
                                String name = (String) favPlacesMap.get("nameOfFavPlace");
                                Timestamp curdate = (Timestamp) favPlacesMap.get("currentDate");
                                String date = curdate.toDate().toString();
                                String url = (String) favPlacesMap.get("imageUrl");
                                double latitude = (double) favPlacesMap.get("favPlaceLat");
                                double longitude = (double) favPlacesMap.get("favPlaceLon");
                                List<Address> addresses;
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                String address = addresses.get(0).getAddressLine(0);

                                FavPlaces favPlaces = new FavPlaces();
                                favPlaces.setFavPlaceId(id);
                                favPlaces.setNameOfFavPlace(name);
                                favPlaces.setDateVisitedOfFavPlace(date);
                                favPlaces.setAddressOfFavPlace(address);
                                favPlaces.setImageUrlOfFavPlace(url);
                                favPlaces.setUserId(userId);

                                favPlacesArrayList.add(favPlaces);

                                Log.i("FavPlacesArrayList", favPlacesArrayList.toString() );
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(FavouritePlaces.this, "You don't have any favourite places", Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
                MyAdapter myAdapter = new MyAdapter(favPlacesArrayList,FavouritePlaces.this);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                DividerItemDecoration dividerItemDecoration =
                        new DividerItemDecoration(getApplicationContext(),
                                DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);

            }
        });

    }

    public void takePhoto(View view){
        OpenDialogBox openDialogBox = new OpenDialogBox();
        openDialogBox.show(getSupportFragmentManager(), "Open Dialog Box");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:{
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData(Uri uri, final String nameOfFavouritePlace, Date date, double lat, double lon) {
        if(uri!= null && nameOfFavouritePlace != null && date != null && lat != 0 && lon != 0) {
            imageUri = uri;
            nameOfPlace = nameOfFavouritePlace;
            currentDate = date;
            favPlaceLat = lat;
            favPlaceLon = lon;

            if(storageTask != null && storageTask.isInProgress()){
                Toast.makeText(this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                final String imageName = System.currentTimeMillis() +
                        "." + getFileExtension(imageUri);
                fileReference = storageReference.child(imageName);

                storageTask = fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.i("Info", uri.toString());
                                                imageUrl = uri.toString();
                                                if (imageUrl != null) {
                                                    saveDataInDatabase();
                                                    Toast.makeText(FavouritePlaces.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(FavouritePlaces.this, "No Image Url Found", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                }, 500);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FavouritePlaces.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });
            }

        }
    }

    public void saveDataInDatabase(){

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                favPlaceCount = (long) documentSnapshot.get("favPlaceCount");

                Map<String, Object> usersData = new HashMap<>();
                Map<String, Object> favPlaces = new HashMap<>();
                Map<String, Object> favPlaceIds = new HashMap<>();

                favPlaces.put("favPlaceId", favPlaceCount);
                favPlaces.put("imageUrl", imageUrl);
                favPlaces.put("currentDate", currentDate);
                favPlaces.put("nameOfFavPlace", nameOfPlace);
                favPlaces.put("favPlaceLat", favPlaceLat);
                favPlaces.put("favPlaceLon", favPlaceLon);

                favPlaceIds.put(nameOfPlace, favPlaces);

                long m = favPlaceCount + 1;

                usersData.put("userFavPlaces", favPlaceIds);
                usersData.put("favPlaceCount", m);


                if (userId != null) {
                    firebaseFirestore.collection("users")
                            .document(userId)
                            .set(usersData, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("Info", "Fav Place Added");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void goToMap(View view){
        Intent intent = new Intent(getApplicationContext(), OpenMapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}