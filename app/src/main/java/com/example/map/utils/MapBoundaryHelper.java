package com.example.map.utils;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MapBoundaryHelper {

    private static final String TAG = "MapBoundaryHelper";

    private AMap aMap;

    public MapBoundaryHelper(AMap aMap) {
        this.aMap = aMap;
    }

    public String Province;


    private List<DistrictItem> loadCityBoundaries(Context context, String districtName) {
        List<DistrictItem> cityBoundaries = new ArrayList<>();
        File directory = null;
        AssetManager assetManager = context.getAssets();
        try{
            String[] files = assetManager.list(districtName);
            if (files == null) return  cityBoundaries;
            Gson gson = new Gson();
            for (String file : files) {
                InputStream is = assetManager.open(districtName + "/" + file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                DistrictItem districtItem = gson.fromJson(reader, DistrictItem.class);
                cityBoundaries.add(districtItem);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cityBoundaries;
    }

    /**
     * 绘制行政区边界,省级单位
     */
    public void drawBoundary(Context context,String province) {
        String fileName = province + "省";
        List<DistrictItem> cityBoundaries = loadCityBoundaries(context, fileName);
        for (DistrictItem districtItem : cityBoundaries) {
            // 获取行政区的边界
            String[] boundary = districtItem.districtBoundary();
            Log.d(TAG, "drawBoundary: " + boundary.length + districtItem.getName());
            for (String polyline : boundary) {
                String[] points = polyline.split(";");
                PolygonOptions polygonOptions = new PolygonOptions();
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (String point : points) {
                    String[] coords = point.split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
                    polygonOptions.add(latLng);
                    boundsBuilder.include(latLng);
                }

                // 设置边界的样式
                polygonOptions.strokeWidth(4) // 边界线宽
                        .strokeColor(Color.BLUE) // 边界线颜色
                        .fillColor(Color.argb(50, 0, 0, 255)); // 区域填充色（可根据需要调整）

                aMap.addPolygon(polygonOptions);
            }
        }
    }

    /**
     * 查询行政区边界，针对省级,然后保存到document里，取出来再用，不然api并发太高，出问题
     */
    public void queryDistrictBoundary(Context context, String districtName) {
        DistrictSearch districtSearch = null;
        try {
            districtSearch = new DistrictSearch(context);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(districtName);
        query.setShowBoundary(true);
        districtSearch.setQuery(query);

        districtSearch.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
            @Override
            public void onDistrictSearched(DistrictResult districtResult) {
                if (districtResult == null || districtResult.getDistrict() == null) {
                    Log.e(TAG, "行政区查询失败或无结果");
                    return;
                }
                if ("province".equals(districtResult.getDistrict().get(0).getLevel())) {
                    Province = districtResult.getDistrict().get(0).getName();
                }
                for (DistrictItem districtItem : districtResult.getDistrict()) {
                    String level = districtItem.getLevel();
                    if ("city".equals(level)) {
                        Log.d(TAG, "已达到市级行政区：" + districtItem.getName());
//                        drawBoundary(districtItem); // 绘制市级行政区边界
                        saveCityBoundaries(context, districtItem, Province);
                        continue;
                    }
                    Log.d(TAG, "要继续查询" + districtItem.getName());
                    // 如果不是市级，继续查询子行政区
                    if (districtItem.getSubDistrict() != null && !districtItem.getSubDistrict().isEmpty()) {
                        for (DistrictItem subDistrict : districtItem.getSubDistrict()) {
                            queryDistrictBoundary(context, subDistrict.getName());
                        }
                    }
                }
            }
        });

        districtSearch.searchDistrictAsyn();
    }

    public void saveCityBoundaries(Context context, DistrictItem cityItem, String districtName) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), districtName);
        // 如果文件夹不存在，则创建它
        if (!directory.exists()) {
            boolean isDirCreated = directory.mkdirs();  // 创建文件夹
            if (isDirCreated) {
                // 文件夹创建成功
                Log.d(TAG, "ok ");
            } else {
                // 文件夹创建失败
                Log.d(TAG, "createFileInDirectory: ");
            }
        }
        Log.d(TAG, "省份 " + districtName);
        File cityFile = new File(directory, cityItem.getName() + "_boundary.json");
        if (cityFile.exists()) {
            Log.d(TAG, "文件已存在: " + cityFile.getAbsolutePath());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(cityFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(cityItem, writer);
            writer.close();
            Log.d(TAG, "成功保存到外部存储: " + cityItem.getName());
        } catch (IOException e) {
            Log.e(TAG, "保存失败: " + e.getMessage());
        }
    }


}

