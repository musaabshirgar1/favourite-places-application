package com.example.favouriteplacesapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class OpenDialogBox extends AppCompatDialogFragment {

    private static final int CAMERA_REQUEST_CODE = 222;
    private static final int IMAGE_PICK_CAMERA_CODE = 555;
    private static final int LOCATION_REQUEST_CODE = 333;

    EditText nameOfPlaceEditText;
    String nameOfPlace;
    ImageButton takePhoto;
    ImageView placeImageView;
    Date currentTime;
    double latitude;
    double longitude;
    LocationManager locationManager;
    LocationListener locationListener;

    SendDataToFavouritePlace sendDataToFavouritePlace;

    String[] locationPermission;
    String[] cameraPermission;
    Uri imageUri;

    public interface SendDataToFavouritePlace{
       void sendData(Uri uri, String nameOfFavouritePlace, Date date, double lat, double lon);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            sendDataToFavouritePlace = (SendDataToFavouritePlace) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_dialog_box, null);
        nameOfPlaceEditText = view.findViewById(R.id.nameOfPlaceEditText);
        takePhoto = view.findViewById(R.id.takePhoto);
        placeImageView = view.findViewById(R.id.placeImageView);
        currentTime = Calendar.getInstance().getTime();


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){
                    Log.i("Info", location.getLatitude() + ","+ location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    locationManager.removeUpdates(locationListener);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickCamera();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setMessage("Add Your New Favourite Place")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameOfPlace = nameOfPlaceEditText.getText().toString().trim();
                        if(imageUri != null) {
                            if(!checkLocationPermission()) {
                                requestLocationPermission();
                            } else{
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                Location userLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if(latitude != 0 && longitude != 0) {
                                    sendDataToFavouritePlace.sendData(imageUri, nameOfPlace, currentTime,latitude,longitude);
                                } else if (userLastKnownLocation!= null) {
                                    sendDataToFavouritePlace.sendData(imageUri, nameOfPlace, currentTime, userLastKnownLocation.getLatitude(), userLastKnownLocation.getLongitude());
                                } else {
                                    Toast.makeText(getActivity(), "Location Not Found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Click A Photo Of Your Fav Place", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),locationPermission,LOCATION_REQUEST_CODE);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED)
                &&
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Favourite Place");
        imageUri = Objects.requireNonNull(getActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeStorageAccepted) {
                    pickCamera();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length > 0){
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(locationAccepted){
                    if(checkLocationPermission()) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                placeImageView.setImageURI(imageUri);
            }
        }
    }
}
