package com.example.map.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

public class LocationManager implements AMapLocationListener, LocationSource {
    private static final String TAG = "LocationManager";
    private AMapLocationClient mLocationClient;
    private LocationSource.OnLocationChangedListener mListener;






    public LocationManager(Context context, LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        initLocation(context);
    }

    public void initLocation(Context context) {
        try {
            mLocationClient = new AMapLocationClient(context.getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setHttpTimeOut(6000);

        mLocationClient.setLocationListener(this);
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            // 定位成功
            if (mListener != null) {
                mListener.onLocationChanged(aMapLocation);
            }
            stopLocation();
            Log.d(TAG, "定位成功: " + aMapLocation.getAddress());
        } else {
            // 定位失败
            if (mListener != null) {
                mListener.onLocationChanged(null);
            }
        }
    }
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener =  onLocationChangedListener;
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    // 停止定位
    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
        mListener = null;
    }
}
