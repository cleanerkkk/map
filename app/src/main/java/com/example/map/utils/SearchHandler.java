package com.example.map.utils;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.RegeocodeResult;

public class SearchHandler implements GeocodeSearch.OnGeocodeSearchListener {

    static final String TAG = "SearchHandler";

    private Context context;
    private AMap aMap;
    private GeocodeSearch geocodeSearch;
    private String city;

    public SearchHandler(Context context, AMap aMap, String city) {
        this.context = context;
        this.aMap = aMap;
        this.city = city;

        try {
            this.geocodeSearch = new GeocodeSearch(context);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
    }

    public void initSearchListener(EditText editText) {
        editText.setOnKeyListener((v, keyCode, event) -> {
            // 判断是否是回车键，并且是按键松开事件
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                String address = editText.getText().toString().trim();
                if (address.isEmpty()) {
                    showMsg("请输入地址");
                } else {
                    // 隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // 创建 GeocodeQuery 对象并进行地址查询
                    GeocodeQuery query = new GeocodeQuery(address, "");
                    Log.d(TAG, "查询地址：" + address);
                    //增加间听
                    geocodeSearch.setOnGeocodeSearchListener(this);
                    geocodeSearch.getFromLocationNameAsyn(query);
                }
                return true;  // 返回 true 处理回车事件
            }
            return false;  // 返回 false 允许其他事件处理
        });
    }

    // 显示Toast消息
    private void showMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && !result.getGeocodeAddressList().isEmpty()) {
                Log.d(TAG, "查询到地址：" + result.getGeocodeAddressList().get(0).getProvince());
                LatLonPoint point = result.getGeocodeAddressList().get(0).getLatLonPoint();
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));  // 将地图视图移动到该位置
            } else {
                showMsg("未找到相关地址");
            }
        } else {
            showMsg("地理编码失败，错误代码：" + errorCode);
        }
    }
}




