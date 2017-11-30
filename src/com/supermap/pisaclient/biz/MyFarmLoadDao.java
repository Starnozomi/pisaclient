/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @MyFarmLoadDao.java - 2014-8-4 上午10:52:30
 */

package com.supermap.pisaclient.biz;

import java.util.concurrent.ExecutionException;

import com.supermap.android.commons.EventStatus;
import com.supermap.android.data.EditFeaturesParameters;
import com.supermap.android.data.EditFeaturesResult;
import com.supermap.android.data.EditFeaturesService;
import com.supermap.android.data.EditFeaturesService.EditFeaturesEventListener;
import com.supermap.android.data.EditType;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.GeometryType;
import com.supermap.services.components.commontypes.Point2D;

public class MyFarmLoadDao {

	public static MyFarmLoadDao dao;

	private MyFarmLoadDao() {

	}

	public static MyFarmLoadDao getInstance() {
		if (dao == null)
			dao = new MyFarmLoadDao();
		return dao;
	}

	public EditFeaturesResult result;

	public void AddFeatures() throws InterruptedException, ExecutionException {
		// 构造数据集编辑参数
		EditFeaturesParameters parameters = new EditFeaturesParameters();
		Point2D[] pts = { new Point2D(-40, 60), new Point2D(-80, 62), new Point2D(-40, 55), new Point2D(-40, 60) };
		Geometry geo = new Geometry();
		geo.type = GeometryType.REGION;
		geo.points = pts;
		Feature feature = new Feature();
		feature.geometry = geo;
		Feature[] features = { feature };
		parameters.editType = EditType.ADD;
		parameters.features = features;
		EditFeaturesService service = new EditFeaturesService(
				"http://MyServerIP:8090/iserver/services/data-world/rest/data/datasources/World/datasets/Countries");
		MyEditFeaturesEventListener listener = new MyEditFeaturesEventListener();
		// 和服务端异步通讯
		service.process(parameters, listener);
		// 等待监听器执行完毕
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 添加要素成功后，获取新增要素的ID值
		int[] desID = result.IDs;
	}

	public void UpdateFeatures() throws InterruptedException, ExecutionException {
		// 构造数据集编辑参数
		EditFeaturesParameters parameters = new EditFeaturesParameters();
		// 编辑SMID =22的要素
		Point2D[] pts = { new Point2D(-40, 60), new Point2D(-80, 62), new Point2D(-40, 55), new Point2D(-40, 60) };
		Geometry geo = new Geometry();
		geo.type = GeometryType.REGION;
		geo.points = pts;
		geo.id = 22;
		Feature feature = new Feature();
		feature.geometry = geo;
		Feature[] features = { feature };
		parameters.editType = EditType.UPDATE;
		parameters.features = features;
		EditFeaturesService service = new EditFeaturesService(
				"http://MyServerIP:8090/iserver/services/data-world/rest/data/datasources/World/datasets/Countries");
		MyEditFeaturesEventListener listener = new MyEditFeaturesEventListener();
		// 和服务端异步通讯
		service.process(parameters, listener);
		// 等待监听器执行完毕
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 结果验证:编辑成功,返回的操作结果的表述为：{"succeed": true}
	}

	public void DeleteFeatures() throws InterruptedException, ExecutionException {
		// 构造数据集编辑参数
		EditFeaturesParameters parameters = new EditFeaturesParameters();
		// 待删除要素的ID
		int[] ids = { 1, 2 };
		parameters.editType = EditType.DELETE;
		parameters.IDs = ids;
		EditFeaturesService service = new EditFeaturesService(
				"http://MyServerIP:8090/iserver/services/data-world/rest/data/datasources/World/datasets/Countries");
		MyEditFeaturesEventListener listener = new MyEditFeaturesEventListener();
		// 和服务端异步通讯
		service.process(parameters, listener);
		// 等待监听器执行完毕
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 结果验证：删除成功,返回的操作结果的表述如下：{"succeed": true}
	}

	// 监听器类
	class MyEditFeaturesEventListener extends EditFeaturesEventListener {
		@Override
		public void onEditFeaturesStatusChanged(Object sourceObject, EventStatus status) {
			// 返回结果
			result = (EditFeaturesResult) sourceObject;
		}
	}
}
