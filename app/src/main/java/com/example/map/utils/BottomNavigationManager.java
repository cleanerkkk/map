package com.example.map.utils;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.map.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationManager {

    public interface OnNavigationItemSelectedListener {
        void onNavigationItemSelected(String mapType);
    }

    public static void setupBottomNavigation(Context context, BottomNavigationView bottomNavigationView, OnNavigationItemSelectedListener listener) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_administrative:
                        listener.onNavigationItemSelected("administrative");
                        return true;
                    case R.id.nav_random:
                        listener.onNavigationItemSelected("random");
                        return true;
                    case R.id.nav_topographic:
                        listener.onNavigationItemSelected("topographic");
                        return true;
                    case R.id.nav_temperature:
                        listener.onNavigationItemSelected("temperature");
                        return true;
                    case R.id.nav_weather:
                        listener.onNavigationItemSelected("weather");
                        return true;
                }
                return false;
            }
        });
    }
}