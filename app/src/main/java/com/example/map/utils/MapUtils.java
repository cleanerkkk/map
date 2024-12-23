// File: app/src/main/java/com/example/map/utils/MapUtils.java
package com.example.map.utils;

import com.amap.api.maps.MapsInitializer;

public class MapUtils {

    /**
     * 是否打开地形图, 默认为关闭
     * 打开地形图之后，底图会变成3D模式，添加的点线面等覆盖物也会自动带有高程
     * <p>
     * 注意：需要在MapView创建之��调用
     *
     * @param isTerrainEnable true为打开，默认false
     * @since 8.0.0
     */
    public static void setTerrainEnable(boolean isTerrainEnable) {
        MapsInitializer.setTerrainEnable(isTerrainEnable);
    }
}