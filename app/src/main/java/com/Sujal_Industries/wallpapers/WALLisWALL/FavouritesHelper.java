package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class FavouritesHelper {

    private SharedPreferences sp;
    private String wall_name;
    private HashMap<String, Boolean> favs;

    private FavouritesHelper() {
    }

    public FavouritesHelper(SharedPreferences sp, String wall_name) {
        this.sp = sp;
        this.wall_name = wall_name;
    }

    public void addFavourite() {
        favs = getFavourites();
        favs.put(wall_name, true);
        setFavourties();

    }

    public void removeFav() {
        favs = getFavourites();
        favs.put(wall_name, false);
        setFavourties();
    }

    public void setFavourties() {
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

    public boolean isFav() {
        favs = getFavourites();
        if (favs.containsKey(wall_name)) {
            return favs.get(wall_name);
        } else {
            return false;
        }
    }
}
