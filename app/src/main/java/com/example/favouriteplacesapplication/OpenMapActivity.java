package com.example.favouriteplacesapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OpenMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static class FavPlaceLocations{
        private double lat;
        private double lon;

        public FavPlaceLocations(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public String toString() {
            return "FavPlaceLocations{" +
                    "lat=" + lat +
                    ", lon=" + lon+
                    '}';
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

    }

    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    FirebaseUser firebaseUser;

    HashMap<String, FavPlaceLocations> userFavPlaces;
    String userId;
    boolean doubleBackToExitPressedOnce = false;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userId = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("users").document(userId);
        userFavPlaces = new HashMap<>();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getAllFavPlaces();
    }

    public void getAllFavPlaces(){
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    HashMap<String, Object> userData = (HashMap<String, Object>) documentSnapshot.getData().get("userFavPlaces");
                    for(Map.Entry<String, Object> entry : userData.entrySet()){
                        try{
                            Map<String, Object> favPlacesMap = (Map<String, Object>) entry.getValue();
                            String name = (String) favPlacesMap.get("nameOfFavPlace");
                            double lat = (double) favPlacesMap.get("favPlaceLat");
                            double lon = (double) favPlacesMap.get("favPlaceLon");
                            userFavPlaces.put(name, new FavPlaceLocations(lat,lon));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                Collection<String> keys = userFavPlaces.keySet();
                Collection<FavPlaceLocations> values = userFavPlaces.values();
                ArrayList<String> userFavPlacesName = new ArrayList<>(keys);
                ArrayList<FavPlaceLocations> userFavPlaceLatLng = new ArrayList<>(values);

                HashMap<String, LatLng> hashMap = new HashMap<>();

                for(int i = 0; i < userFavPlaceLatLng.size(); i++){
                    String name = userFavPlacesName.get(i);
                    double lat = userFavPlaceLatLng.get(i).lat;
                    double lon = userFavPlaceLatLng.get(i).lon;
                    hashMap.put(name, new LatLng(lat,lon));
                }

                ArrayList<Marker> markers = new ArrayList<>();
                for(Map.Entry<String, LatLng> entry : hashMap.entrySet()){
                    String title = entry.getKey();
                    LatLng latLng = entry.getValue();
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                    )));
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 500;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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