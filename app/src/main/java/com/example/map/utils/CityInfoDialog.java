package com.example.map.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.map.R;
import com.example.map.model.CityInfo;
import com.example.map.utils.WeatherManager;

public class CityInfoDialog {

    public static void showCityInfoDialog(Context context, CityInfo cityInfo, String mapType)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("城市信息");


        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_city_info, null);
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

        switch (mapType) {
            case "administrative":
                cityInfoText =
                        "<span style='color:black;'><strong>人文介绍</strong></span>：" + cityInfo.getCulturalIntroduction() + "<br>" +
                                "<span style='color:black;'><strong>历史</strong></span>：" + cityInfo.getHistory() + "<br>" +
                                "<span style='color:black;'><strong>农业</strong></span>：" + cityInfo.getAgriculture() + "<br>" +
                                "<span style='color:black;'><strong>工业</strong></span>：" + cityInfo.getIndustry() + "<br>";
                break;
            case "topographic":
                cityInfoText =
                        "<span style='color:black;'><strong>自然介绍</strong></span>：" + cityInfo.getNaturalFeatures() + "<br>" +
                                "<span style='color:black;'><strong>地形地貌</strong></span>：" + cityInfo.getTopography() + "<br>";
                break;
            case "climate":
                cityInfoText =
                        "<span style='color:black;'><strong>气候</strong></span>：" + cityInfo.getClimate() + "<br>"+
                        "<span style='color:black;'><strong>水文</strong></span>：" + cityInfo.getHydrology();
                break;
            case "weather":
                showWeatherInfoDialog(context, cityInfo.getCityName(), cityInfoText);
                break;

        }

        String cityName = "<span style='text-align:center, color:black'><b>" + cityInfo.getCityName() + "</b></span>";
        CharSequence c = Html.fromHtml(cityName, Html.FROM_HTML_MODE_LEGACY);
        tvCityName.setText(c);
        CharSequence charSequence = Html.fromHtml(cityInfoText, Html.FROM_HTML_MODE_LEGACY);
        tvCityInfo.setText(charSequence);

        builder.setPositiveButton("关闭", null);


        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);

        dialog.show();
    }


    public static void showWeatherInfoDialog(Context context, String city, String weatherInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_city_info, null);
        builder.setView(dialogView);

        TextView tvCityName = dialogView.findViewById(R.id.tv_city_name);
        TextView tvCityInfo = dialogView.findViewById(R.id.tv_city_info);

        String cityName = "<span style='text-align:center; color:black'><b>" + city + "</b></span>";
        CharSequence c = Html.fromHtml(cityName, Html.FROM_HTML_MODE_LEGACY);
        tvCityName.setText(c);
        CharSequence charSequence = Html.fromHtml(weatherInfo, Html.FROM_HTML_MODE_LEGACY);
        tvCityInfo.setText(charSequence);

        builder.setPositiveButton("关闭", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show();
    }
}