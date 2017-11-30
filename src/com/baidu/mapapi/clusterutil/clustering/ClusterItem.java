/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.mapapi.clusterutil.clustering;


import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;

import android.os.Bundle;

/**
 * ClusterItem represents a marker on the map.
 */
public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    LatLng getPosition();

    BitmapDescriptor getBitmapDescriptor();
    
    /**
     * 将百度地图自带的接口进行扩充，用于获取额外信息，方便进行Marker的点击事件
     * @author TanRq
     * @return
     */
    Bundle getExtraInfo();
}