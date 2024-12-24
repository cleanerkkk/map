// File: app/src/main/java/com/example/map/utils/WeatherManager.java
package com.example.map.utils;

import android.content.Context;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;

public class WeatherManager implements OnWeatherSearchListener {

    public interface WeatherCallback {
        void onWeatherInfoReceived(String city, String weatherInfo);
        void onWeatherInfoError(String errorMsg);
    }

    private Context context;
    private WeatherSearch weatherSearch;
    private WeatherCallback callback;

    public WeatherManager(Context context, WeatherCallback callback) {
        this.context = context;
        this.callback = callback;
        try {
            this.weatherSearch = new WeatherSearch(context);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        this.weatherSearch.setOnWeatherSearchListener(this);
    }

    public void searchWeather(String city) {
        WeatherSearchQuery query = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        weatherSearch.setQuery(query);
        weatherSearch.searchWeatherAsyn(); // Asynchronous search
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherSearchResult, int rCode) {
        if (rCode == 1000) {
            if (weatherSearchResult != null && weatherSearchResult.getLiveResult() != null) {
                String weather = weatherSearchResult.getLiveResult().getWeather();
                String temperature = weatherSearchResult.getLiveResult().getTemperature() + "°C";
                String wind = weatherSearchResult.getLiveResult().getWindDirection() + " 风力 " + weatherSearchResult.getLiveResult().getWindPower() + " 级";
                String humidity =  weatherSearchResult.getLiveResult().getHumidity() + "%";
                String weatherInfo = "天气:" + weather + "<br>温度: " + temperature + "<br>风力: " + wind + "<br>湿度: " + humidity;
                callback.onWeatherInfoReceived(weatherSearchResult.getLiveResult().getCity(), weatherInfo);
            } else {
                callback.onWeatherInfoError("No weather information found");
            }
        } else {
            callback.onWeatherInfoError("Weather search failed, error code: " + rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult weatherForecastResult, int rCode) {
        // Not used in this implementation
    }
}