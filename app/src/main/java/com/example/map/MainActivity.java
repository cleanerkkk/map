package com.example.map;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;

import com.amap.api.maps.model.LatLng;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.example.map.databinding.ActivityMainBinding;
import com.example.map.model.CityInfo;
import com.example.map.model.CityInfoProvider;
import com.example.map.utils.CityInfoDialog;
import com.example.map.utils.LocationManager;
import com.example.map.utils.MapBoundaryManager;
import com.example.map.utils.MapClickListener;
import com.example.map.utils.SearchHandler;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MapClickListener.OnLocationResultListener {

    private static final String TAG = "MainActivity";
    MapBoundaryManager boundaryHelper = null;
    private ActivityMainBinding binding;
    private AMap aMap = null;
    private LocationManager locationManager;
    private EditText searchEditText;
    private SearchHandler searchHandler;


    // 请求权限意图
    private ActivityResultLauncher<String> requestPermission;

    private Button showMenuButton = null;
    private String menuMode = "province";
    private String city = "南京市";

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
        CityInfoProvider.loadCityInfo(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        locationManager = new LocationManager(this, aMapLocation -> {

        });


        // 选择省/市菜单
        showMenuButton = findViewById(R.id.showMenuButton);
        // 设置按钮点击事件
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 PopupMenu 对象
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                // 加载菜单资源
                getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

                // 设置默认选中的菜单项
                Menu menu = popupMenu.getMenu();
                MenuItem normalItem = menu.findItem(R.id.province_menu);
                normalItem.setChecked(true);  // 设置默认选中

                // 显示菜单
                popupMenu.show();

                // 设置菜单项点击事件监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.province_menu:
                                //省级
                                menuMode = "province";
                                return true;
                            case R.id.city_menu:
                                //市级
                                menuMode = "city";
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

        binding.mapView.onCreate(savedInstanceState);

        initMap();
        searchEditText = findViewById(R.id.et_address);
        searchHandler = new SearchHandler(this, aMap, city);
        searchHandler.initSearchListener(searchEditText);



    }



    private void showMsg(CharSequence llw) {
        Toast.makeText(this, llw, Toast.LENGTH_SHORT).show();
    }


    private void initMap() {
        if (aMap == null) {
            aMap = binding.mapView.getMap();
            MapClickListener mapClickListener = new MapClickListener(this, this); // 使用回调来获取点击位置的行政区
            aMap.setOnMapClickListener(mapClickListener);

            aMap.setLocationSource(locationManager);
            aMap.setMyLocationEnabled(true);

            boundaryHelper = new MapBoundaryManager(aMap);
//            boundaryHelper.drawBoundary(this, "江苏");
        }
    }


//     点击地图时返回行政区信息
    @Override
    public void onClickLocationResult(@NonNull RegeocodeAddress address) {
        String district = null;
        String province = address.getProvince();
        String city = address.getCity();
        if ("province".equals(menuMode)){
            district = address.getProvince();
            showMsg("选择了"+district );
            boundaryHelper.clearBoundaries();
            if ("江苏省".equals(district)){
                updateMenuMode();
                boundaryHelper.drawBoundary(this, district);
            }
        }
        else{
            district = address.getCity();
            showMsg("选择了" + district);
            if (address.getProvince().equals("江苏省")) {
                LatLonPoint center = boundaryHelper.getCenter(this, district, address.getProvince());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(center.getLatitude(), center.getLongitude())));
                CityInfo cityInfo = CityInfoProvider.getCityInfo(province, city);
                if (cityInfo != null) {
//                    showCityInfoDialog(cityInfo);
                    CityInfoDialog.showCityInfoDialog(this, cityInfo);
                } else {
                    showMsg("未找到相关城市信息");
                }
            }
        }
    }

    private void updateMenuMode() {
        // 获取当前的 PopupMenu 实例
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, showMenuButton);
        getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

        // 设置菜单项的选中状态
        Menu menu = popupMenu.getMenu();

        // 设置市级菜单为选中状态
        MenuItem cityItem = menu.findItem(R.id.city_menu);
        cityItem.setChecked(true);  // 设置市级为选中

        // 如果需要，设置省级菜单项取消选中
        MenuItem provinceItem = menu.findItem(R.id.province_menu);
        provinceItem.setChecked(false);  // 取消省级选中
        menuMode = "city";
        // 显示菜单
//        popupMenu.show();
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

    private void showCityInfoDialog(@NonNull CityInfo cityInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_city_info, null);
        builder.setView(dialogView);

        TextView tvCityName = dialogView.findViewById(R.id.tv_city_name);
        TextView tvCityInfo = dialogView.findViewById(R.id.tv_city_info);

        String cityInfoText =
                "<span style='color:black;'><strong>人文介绍</strong></span>：" + cityInfo.getCulturalIntroduction() + "<br>" +
                "<span style='color:black;'><strong>历史</strong></span>：" + cityInfo.getHistory() + "<br>" +
                "<span style='color:black;'><strong>农业</strong></span>：" + cityInfo.getAgriculture() + "<br>" +
                "<span style='color:black;'><strong>工业</strong></span>：" + cityInfo.getIndustry() + "<br>" +
                "<span style='color:black;'><strong>自然介绍</strong></span>：" + cityInfo.getNaturalFeatures() + "<br>" +
                "<span style='color:black;'><strong>气候</strong></span>：" + cityInfo.getClimate() + "<br>" +
                "<span style='color:black;'><strong>地形地貌</strong></span>：" + cityInfo.getTopography() + "<br>" +
                "<span style='color:black;'><strong>水文</strong></span>：" + cityInfo.getHydrology();
        String cityName = "<span style='text-align:center;'><b>" + cityInfo.getCityName() + "</b></span>";
        CharSequence c = Html.fromHtml(cityName, 1);
        tvCityName.setText(c);
        CharSequence charSequence = Html.fromHtml(cityInfoText, 1);
        tvCityInfo.setText(charSequence);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show();

        // 点击对话框外部关闭对话框
        dialog.setCanceledOnTouchOutside(true);
    }


}







