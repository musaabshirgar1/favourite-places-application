package com.example.favouriteplaces;

public class User {
    int favPlaceId;

    public User(int favPlaceId) {
        this.favPlaceId = favPlaceId;
    }

    public int getFavPlaceId() {
        return favPlaceId;
    }

    public void setFavPlaceId(int favPlaceId) {
        this.favPlaceId = favPlaceId;
    }
}
