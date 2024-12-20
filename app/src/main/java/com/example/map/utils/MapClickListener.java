package com.example.map.utils;

import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.core.LatLonPoint;
import com.example.map.MainActivity;

public class MapClickListener implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "MapClickListener";

    private MainActivity mainActivity;
    private GeocodeSearch geocodeSearch;
    private OnLocationResultListener locationResultListener;

    public MapClickListener(MainActivity mainActivity, OnLocationResultListener listener) {
        this.mainActivity = mainActivity;
        this.locationResultListener = listener;
        try {
            this.geocodeSearch = new GeocodeSearch(mainActivity);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        this.geocodeSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 获取点击位置的经纬度
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);

        // 创建逆地理查询
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        // 异步查询地理信息
        geocodeSearch.getFromLocationAsyn(query);
    }

    //    // 处理逆地理查询结果
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000 && result != null) {
//            RegeocodeAddress address = result.getRegeocodeAddress();
//            String district = address.getProvince();  // 获取行政区名称
//
//            Log.d(TAG, "点击位置的行政区：" + district);
//
//            if (locationResultListener != null) {
//                locationResultListener.onClickLocationResult(address);
//            }
//        } else {
//            Log.e(TAG, "获取行政区信息失败");
//        }
//    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000 && result != null) {
            RegeocodeAddress address = result.getRegeocodeAddress();
            String district = address.getProvince();  // 获取行政区名称

            Log.d(TAG, "点击位置的行政区：" + district);

            if (locationResultListener != null) {
                locationResultListener.onClickLocationResult(address);
            }
        } else {
            Log.e(TAG, "获取行政区信息失败");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    // 定义一个回调接口，用于回传行政区信息
    public interface OnLocationResultListener {
        void onClickLocationResult(RegeocodeAddress address);
    }
}
