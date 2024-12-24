package com.example.map.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.map.R;

public class WeatherInfoDialog {

    public static void showWeatherInfoDialog(Context context, String city, String weatherInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_weather_info, null);
        builder.setView(dialogView);

        TextView tvWeatherCity = dialogView.findViewById(R.id.tv_weather_city);
        TextView tvWeatherInfo = dialogView.findViewById(R.id.tv_weather_info);

        tvWeatherCity.setText(city);
        tvWeatherInfo.setText(weatherInfo);

        builder.setPositiveButton("Close", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}