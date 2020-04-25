package com.example.favouriteplacesapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<FavPlaces> favPlacesArrayList;
    Context context;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    public MyAdapter(ArrayList<FavPlaces> favPlacesArrayList, Context context) {
        this.favPlacesArrayList = favPlacesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_view, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        FavPlaces currentFavPlace = favPlacesArrayList.get(position);
        holder.nameOfFavPlaceTextView.setText("Name: " + currentFavPlace.getNameOfFavPlace());
        holder.dateVisitedTextView.setText("Date: " + currentFavPlace.getDateVisitedOfFavPlace());
        holder.addressOfFavPlaceTextView.setText("Address: " + currentFavPlace.getAddressOfFavPlace());
        Picasso.with(context)
                .load(currentFavPlace.getImageUrlOfFavPlace())
                .placeholder(R.mipmap.ic_launcher)
                .rotate(90)
                .fit()
                .centerCrop()
                .into(holder.favPlaceImageView);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final String name = favPlacesArrayList.get(position).getNameOfFavPlace();
                final String id = favPlacesArrayList.get(position).getUserId();

                builder.setMessage("Do you want to delete " + name);
                builder.setCancelable(false);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favPlacesArrayList.remove(position);
                        notifyItemChanged(position);

                        documentReference = firebaseFirestore.collection("users").document(id);
                        documentReference.update("userFavPlaces." + name, FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, name + " is deleted", Toast.LENGTH_SHORT).show();
                                    FieldValue decrement = FieldValue.increment(-1);
                                    documentReference.update("favPlaceCount", decrement);

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return favPlacesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfFavPlaceTextView;
        TextView dateVisitedTextView;
        TextView addressOfFavPlaceTextView;
        ImageView favPlaceImageView;
        Button delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameOfFavPlaceTextView = itemView.findViewById(R.id.nameOfFavPlaceTextView);
            dateVisitedTextView = itemView.findViewById(R.id.dateVisitedTextView);
            addressOfFavPlaceTextView = itemView.findViewById(R.id.addressOfFavPlaceTextView);
            favPlaceImageView = itemView.findViewById(R.id.favPlaceImageView);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
