package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class FavouritesHelper {

    private SharedPreferences sp;
    private HashMap<String, Boolean> favs;

    private FavouritesHelper() {
    }

    public FavouritesHelper(SharedPreferences sp) {
        this.sp = sp;
    }

    public void addFavourite(String wall_name) {
        favs = getFavourites();
        favs.put(wall_name, true);
        setFavourties();

    }

    public void removeFav(String wall_name) {
        favs = getFavourites();
        favs.put(wall_name, false);
        setFavourties();
    }

    private void setFavourties() {
        SharedPreferences.Editor pe = sp.edit();
        Gson gson = new Gson();
        String j = gson.toJson(favs);
        pe.putString("Favourites", j);
        pe.apply();
    }


    public HashMap<String, Boolean> getFavourites() {
        Gson gson = new Gson();
        String j = sp.getString("Favourites", null);
        if (j != null) {
            Type stringBooleanMap = new TypeToken<HashMap<String, Boolean>>() {
            }.getType();
            return gson.fromJson(j, stringBooleanMap);
        } else {
            return new HashMap<>();
        }
    }

    public boolean isFav(String wall_name) {
        favs = getFavourites();
        if (favs.containsKey(wall_name)) {
            return favs.get(wall_name);
        } else {
            return false;
        }
    }
}
