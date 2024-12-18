package com.example.map;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.map.databinding.ActivityMainBinding;
import com.example.map.utils.LocationManager;
import com.example.map.utils.MapBoundaryManager;

public class MainActivity extends AppCompatActivity implements   GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapClickListener, AMap.OnMapLongClickListener {

//    private static final int REQUEST_PERMISSION_CODE = 100;


    private GeocodeSearch geocodeSearch;

    private static final int PARSE_SUCCESS_CODE = 1000;
    private ActivityMainBinding binding;

    // 声明地图控制器
    private AMap aMap = null;

    private LocationManager locationManager;
    // 声明地图定位监听
    private LocationManager.OnLocationChangedListener mListener = null;



    private static final String TAG = "MainActivity";
    // 请求权限意图
    private ActivityResultLauncher<String> requestPermission;

    public AMapLocationClient mLocationClient = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            // 权限申请结果
            Log.d(TAG, "权限申请结果: " + result);
            showMsg("权限申请结果: " + result);
        });
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        locationManager = new LocationManager(this, mListener );

        binding.mapView.onCreate(savedInstanceState);
        initSearch();
        initMap();
    }
    @Override
    protected void onResume() {
        super.onResume();

        binding.mapView.onResume();

        // 检查是否已经获取到定位权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 获取到权限
            Log.d(TAG, "onResume: 已获取到权限");
            showMsg("已获取到权限");
            locationManager.startLocation();
        } else {
            // 请求定位权限
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }
    private void showMsg(CharSequence llw) {
        Toast.makeText(this, llw, Toast.LENGTH_SHORT).show();
    }



    private void initSearch() {

        try {
            geocodeSearch = new GeocodeSearch(this);

            geocodeSearch.setOnGeocodeSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    private void latLonToAddress(LatLng latLng) {

        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        latLonToAddress(latLng);
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        showMsg("长按了地图" + latLng.longitude + "," + latLng.latitude);
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        if (aMap == null) {
            aMap = binding.mapView.getMap();
            aMap.setOnMapClickListener(this);
            aMap.setOnMapLongClickListener(this);
            aMap.setMyLocationEnabled(true);
            MapBoundaryManager boundaryHelper = new MapBoundaryManager(aMap);
            boundaryHelper.drawBoundary(this, "江苏");

        }
    }







    @Override
    protected void onPause() {
        super.onPause();
        // 绑定生命周期 onPause
        binding.mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 绑定生命周期 onSaveInstanceState
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 绑定生命周期 onDestroy
        binding.mapView.onDestroy();
    }






    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {

        if(rCode == PARSE_SUCCESS_CODE){
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();

            showMsg("地址："+regeocodeAddress.getFormatAddress());
        }else {
            showMsg("获取地址失败");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}

