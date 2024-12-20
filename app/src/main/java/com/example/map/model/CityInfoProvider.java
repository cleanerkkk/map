package com.example.map.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CityInfoProvider {
    private static final Map<String, Map<String, CityInfo>> cityInfoMap = new HashMap<>();
    private static final String TAG = "CityInfoProvider";
    public static void loadCityInfo(Context context) {
        try {
            InputStream is = context.getAssets().open("details.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Map<String, CityInfo>>>() {}.getType();
            cityInfoMap.putAll(gson.fromJson(json, type));
            Log.d(TAG, "loadCityInfo: " + cityInfoMap.get("江苏省").get("南京市"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CityInfo getCityInfo(String province, String city) {
        Map<String, CityInfo> provinceMap = cityInfoMap.get(province);
        if (provinceMap != null) {
            return provinceMap.get(city);
        }
        return null;
    }
}