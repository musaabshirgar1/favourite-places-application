package com.example.favouriteplacesapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<String> nameOfFavPlaces;
    List<String> dateVisitedOfFavPlaces;
    List<String> imageUrlOfFavPlaces;
    List<String> addressOfFavPlaces;
    Context context;

    public MyAdapter(List<String> nameOfFavPlaces, List<String> dateVisitedOfFavPlaces, List<String> imageUrlOfFavPlaces, List<String> addressOfFavPlaces, Context context) {
        this.nameOfFavPlaces = nameOfFavPlaces;
        this.dateVisitedOfFavPlaces = dateVisitedOfFavPlaces;
        this.imageUrlOfFavPlaces = imageUrlOfFavPlaces;
        this.addressOfFavPlaces = addressOfFavPlaces;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameOfFavPlaceTextView.setText("Name: " + nameOfFavPlaces.get(position));
        holder.dateVisitedTextView.setText("Date: " + dateVisitedOfFavPlaces.get(position));
        holder.addressOfFavPlaceTextView.setText("Address: " + addressOfFavPlaces.get(position));
        Picasso.with(context)
                .load(imageUrlOfFavPlaces.get(position))
                .placeholder(R.mipmap.ic_launcher)
                .rotate(90)
                .fit()
                .centerCrop()
                .into(holder.favPlaceImageView);

    }

    @Override
    public int getItemCount() {
        return nameOfFavPlaces.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfFavPlaceTextView;
        TextView dateVisitedTextView;
        TextView addressOfFavPlaceTextView;
        ImageView favPlaceImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameOfFavPlaceTextView = itemView.findViewById(R.id.nameOfFavPlaceTextView);
            dateVisitedTextView = itemView.findViewById(R.id.dateVisitedTextView);
            addressOfFavPlaceTextView = itemView.findViewById(R.id.addressOfFavPlaceTextView);
            favPlaceImageView = itemView.findViewById(R.id.favPlaceImageView);

        }
    }
}
