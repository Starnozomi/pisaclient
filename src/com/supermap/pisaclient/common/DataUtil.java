package com.supermap.pisaclient.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.supermap.android.commons.EventStatus;
import com.supermap.android.data.EditFeaturesParameters;
import com.supermap.android.data.EditFeaturesResult;
import com.supermap.android.data.EditFeaturesService;
import com.supermap.android.data.EditFeaturesService.EditFeaturesEventListener;
import com.supermap.android.data.EditType;
import com.supermap.android.data.GetFeaturesByBufferParameters;
import com.supermap.android.data.GetFeaturesByBufferService;
import com.supermap.android.data.GetFeaturesByGeometryParameters;
import com.supermap.android.data.GetFeaturesByGeometryService;
import com.supermap.android.data.GetFeaturesByGeometryService.GetFeaturesEventListener;
import com.supermap.android.data.GetFeaturesByIDsParameters;
import com.supermap.android.data.GetFeaturesByIDsService;
import com.supermap.android.data.GetFeaturesBySQLParameters;
import com.supermap.android.data.GetFeaturesBySQLService;
import com.supermap.android.data.GetFeaturesResult;
import com.supermap.android.maps.Point2D;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.GeometryType;
import com.supermap.services.components.commontypes.QueryParameter;
import com.supermap.services.components.commontypes.SpatialQueryMode;
import com.supermap.services.rest.util.JsonConverter;

/**
 * <p>
 * 实现数据编辑和数据查询功能的工具类
 * </p>
 * @author ${Author}
 * @version ${Version}
 * 
 */
public class DataUtil {
	/**
	 * <p>
	 * 执行IDS查询
	 * </p>
	 * @param url
	 *        数据查询服务地址
	 * @return
	 */
	public static GetFeaturesResult excute_idsQuery(String queryurl) {
		Log.d("Test info ", "Start doIDQuery");
		Log.d("Test info1 ", "queryurl=" + queryurl);
		// 构造查询参数
		int[] ids = { 110, 239 };
		GetFeaturesByIDsParameters parameters = new GetFeaturesByIDsParameters();
		String[] dtnames = { "World:Countries" };
		parameters.datasetNames = dtnames;
		parameters.IDs = ids;
		parameters.toIndex = 500;
		GetFeaturesByIDsService service = new GetFeaturesByIDsService(queryurl);
		MyGetFeaturesEventListener listener = new MyGetFeaturesEventListener();
		service.process(parameters, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetFeaturesResult result = listener.getReult();
		Log.d("excute_idsQuery GetFeaturesResult", JsonConverter.toJson(result));
		return result;
	}

	/**
	 * <p>
	 * 执行几何查询
	 * </p>
	 * @param url
	 * @param point
	 * @return
	 */
	public static GetFeaturesResult excute_geometryQuery(String url, Geometry geometry) {
		Log.d("Test info ", "Start doGeometryQuery");
		Log.d("Test info1 ", "queryurl=" + url);
		// 定义几何查询参数
		GetFeaturesByGeometryParameters params = new GetFeaturesByGeometryParameters();
		String[] datasetNames = new String[] { "World:Countries" };
		params.datasetNames = datasetNames;
		params.geometry = geometry;
		params.spatialQueryMode = SpatialQueryMode.INTERSECT;
		Log.d("GeometryParameters", JsonConverter.toJson(params));

		// 与服务器交互
		GetFeaturesByGeometryService service = new GetFeaturesByGeometryService(url);
		MyGetFeaturesEventListener listener = new MyGetFeaturesEventListener();
		service.process(params, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetFeaturesResult result = listener.getReult();
		Log.d("GetGeometryQueryResult", JsonConverter.toJson(result));
		return result;

	}

	/**
	 * <p>
	 * 执行缓冲区查询
	 * </p>
	 * @param url
	 * @param geometry
	 * @return
	 */
	public static GetFeaturesResult excute_bufferQuery(String url, Geometry geometry) {
		Log.d("Test info ", "Start doBufferQuery");
		Log.d("Test info1 ", "queryurl=" + url);
		// 定义几何查询参数
		GetFeaturesByBufferParameters params = new GetFeaturesByBufferParameters();
		String[] datasetNames = new String[] { "World:Capitals" };
		params.datasetNames = datasetNames;
		params.geometry = geometry;
		params.bufferDistance = 30;
		Log.d("GeometryParameters", JsonConverter.toJson(params));

		// 与服务器交互
		GetFeaturesByBufferService service = new GetFeaturesByBufferService(url);
		MyGetFeaturesEventListener listener = new MyGetFeaturesEventListener();
		service.process(params, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetFeaturesResult result = listener.getReult();
		Log.d("GetBufferQueryResult", JsonConverter.toJson(result));
		return result;

	}

	/**
	 * <p>
	 * 执行SQL查询
	 * </p>
	 * @param url
	 *        data服务地址
	 * @return
	 */
	public static GetFeaturesResult excute_geoSQL(String url) {
		// 定义SQL查询参数
		GetFeaturesBySQLParameters params = new GetFeaturesBySQLParameters();
		String[] datasetNames = new String[] { "World:Countries" };
		params.datasetNames = datasetNames;
		QueryParameter queryParameter = new QueryParameter();
		queryParameter.attributeFilter = "SMID = 240";
		queryParameter.name = "Countries@World";
		params.queryParameter = queryParameter;

		// 与服务器交互
		GetFeaturesBySQLService geoSQLService = new GetFeaturesBySQLService(url);
		MyGetFeaturesEventListener listener = new MyGetFeaturesEventListener();
		geoSQLService.process(params, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetFeaturesResult result = listener.getReult();
		Log.d("SQLQueryResult", JsonConverter.toJson(result));
		return result;

	}

	/**
	 * <p>
	 * 执行添加农田
	 * </p>
	 * @param url
	 *        数据编辑服务地址
	 * @param feature
	 *        要增加或修改的农田要素集合
	 * @return
	 */
	public static boolean excute_geoAdd(String url, Feature f) {
		// 定义添加农田参数
		FarmDao dao = new FarmDao();
		Farm farm = new Farm();
		com.supermap.services.components.commontypes.Point2D point = f.geometry.getCenter();
		farm.longitude = point.x + "";
		farm.latitude = point.y + "";
		List<com.supermap.services.components.commontypes.Point2D> all = Arrays.asList(f.geometry.points);
		List<Point2D> data = new ArrayList<Point2D>();
		StringBuffer sb = new StringBuffer();
		for (com.supermap.services.components.commontypes.Point2D p : all) {
			Point2D p2 = new Point2D();
			p2.x = p.x;
			p2.y = p.y;
			data.add(p2);
			sb.append(p.x + "," + p.y + ",");
		}
		farm.area = DataUtil.getArea(data) + "";
		farm.areaCode = "50011001";
		farm.descript = "测试田块";
		farm.height = "1230";
		String coordinates = sb.toString().trim();
		coordinates = coordinates.substring(0, coordinates.length() - 1);
		farm.coordinates = coordinates;
		farm.userId = 3;
		String res = "1";// dao.addFarmland(farm);
		try {
			Integer.parseInt(res);
			return true;
		} catch (Exception e) {

		}
		return false;

	}

	/**
	 * <p>
	 * 执行选择农田
	 * </p>
	 * @param url
	 *        data服务地址
	 * @param point
	 *        输入的点对象，用来查询农田
	 * @return
	 */
	public static GetFeaturesResult excute_geoSel(String url, Point2D point) {
		// 定义几何查询参数
		GetFeaturesByGeometryParameters params = new GetFeaturesByGeometryParameters();
		String[] datasetNames = new String[] { "Jingjin:Landuse_R" };
		params.datasetNames = datasetNames;
		Geometry geometry = new Geometry();
		com.supermap.services.components.commontypes.Point2D[] points = new com.supermap.services.components.commontypes.Point2D[] { new com.supermap.services.components.commontypes.Point2D(
				point.x, point.y) };
		geometry.points = points;
		geometry.type = GeometryType.POINT;
		params.geometry = geometry;
		params.spatialQueryMode = SpatialQueryMode.INTERSECT;

		// 与服务器交互
		GetFeaturesByGeometryService geoSelService = new GetFeaturesByGeometryService(url);
		MyGetFeaturesEventListener listener = new MyGetFeaturesEventListener();
		geoSelService.process(params, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetFeaturesResult result = listener.getReult();
		return result;

	}

	/**
	 * <p>
	 * 执行编辑农田
	 * </p>
	 * @param url
	 *        数据编辑服务地址
	 * @param feature
	 *        要增加或修改的农田要素集合
	 * @param ids
	 *        要删除或修改的矢量要素 ID 号集合
	 * @return
	 */
	public static EditFeaturesResult excute_geoEdit(String url, Feature[] feature, int[] ids) {
		// 定义添加农田参数
		EditFeaturesParameters editFeatureParam = new EditFeaturesParameters();
		editFeatureParam.editType = EditType.UPDATE;
		editFeatureParam.features = feature;
		editFeatureParam.IDs = ids;
		// 与服务器交互
		EditFeaturesService editFeaturesService = new EditFeaturesService(url);
		MyEditFeaturesEventListener listener = new MyEditFeaturesEventListener();
		editFeaturesService.process(editFeatureParam, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		EditFeaturesResult editResult = listener.getReult();
		return editResult;

	}

	/**
	 * <p>
	 * 执行删除农田
	 * </p>
	 * @param url
	 *        数据编辑服务地址
	 * @param ids
	 *        要删除或修改的矢量要素 ID 号集合
	 * @return
	 */
	public static EditFeaturesResult excute_geoDel(String url, int[] ids) {
		// 定义删除农田参数
		EditFeaturesParameters delFeatureParam = new EditFeaturesParameters();
		delFeatureParam.editType = EditType.DELETE;
		delFeatureParam.IDs = ids;
		// 与服务器交互
		EditFeaturesService editFeaturesService = new EditFeaturesService(url);
		MyEditFeaturesEventListener listener = new MyEditFeaturesEventListener();
		editFeaturesService.process(delFeatureParam, listener);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		EditFeaturesResult delResult = listener.getReult();
		return delResult;

	}

	/**
	 * <p>
	 * 提取构成农田的点集合
	 * </p>
	 * @param geometry
	 * @return
	 */
	public static List<Point2D> getPiontsFromGeometry(Geometry geometry) {
		List<Point2D> geoPoints = new ArrayList<Point2D>();
		com.supermap.services.components.commontypes.Point2D[] points = geometry.points;
		for (com.supermap.services.components.commontypes.Point2D point : points) {
			if (point != null) {
				Point2D geoPoint = new Point2D(point.x, point.y);
				geoPoints.add(geoPoint);
			}
		}
		return geoPoints;
	}

	public static boolean ptInPolygon(Point2D p, List<Point2D> ptPolygon) {
		int nCross = 0;
		int nCount = ptPolygon.size();
		for (int i = 0; i < nCount; i++) {
			Point2D p1 = ptPolygon.get(i);
			if (compare(p, p1)) {
				return true;
			}
			Point2D p2 = ptPolygon.get((i + 1) % nCount);
			if (p1.y == p2.y)
				continue;
			if (p.y < min(p1.y, p2.y))
				continue;
			if (p.y >= max(p1.y, p2.y))
				continue;
			double x = (double) (p.y - p1.y) * (double) (p2.x - p1.x) / (double) (p2.y - p1.y) + p1.x;
			if (x > p.x)
				nCross++;
		}
		return (nCross % 2 == 1);
	}

	public static double min(double x, double y) {
		return x < y ? x : y;
	}

	public static double max(double x, double y) {
		return x > y ? x : y;
	}

	public static boolean compare(Point2D p1, Point2D p2) {
		return p1.x == p2.x && p1.y == p2.y;
	}

	/**
	 * <p>
	 * 判断由data构成的多边形是否包含点gp
	 * </p>
	 * @param gp
	 * @param data
	 * @return
	 */
	public static boolean contians(Point2D gp, List<Point2D> data) {
		int j = 0;
		int N = data.size() - 1;
		boolean oddNodes = false;
		double x = gp.getX();
		double y = gp.getY();
		for (int i = 0; i < N; i++) {
			j++;
			if (j == N) {
				j = 0;
			}
			if (((((Point2D) data.get(i)).getY() >= y) || (((Point2D) data.get(j)).getY() < y))
					&& ((((Point2D) data.get(j)).getY() >= y) || (((Point2D) data.get(i)).getY() < y)))
				continue;
			if (((Point2D) data.get(i)).getX() + (y - ((Point2D) data.get(i)).getY())
					/ (((Point2D) data.get(j)).getY() - ((Point2D) data.get(i)).getY())
					* (((Point2D) data.get(j)).getX() - ((Point2D) data.get(i)).getX()) >= x) {
				continue;
			}
			oddNodes = !oddNodes;
		}
		return oddNodes;
	}

	public static double getArea(List<Point2D> data) {
		double area = 0.0;
		for (int i = 0; i < data.size(); i++) {
			if (i < data.size() - 1) {
				Point2D p1 = data.get(i);
				Point2D p2 = data.get(i + 1);
				area += p1.x * p2.y - p2.x * p1.y;
			} else {
				area += data.get(i).x * data.get(0).y - data.get(0).x * data.get(i).y;
			}

		}
		area = Math.abs(area) / 2.0;
		return area;
	}

	/**
	 * <p>
	 * 修改组成农田的点集合
	 * </p>
	 * @param feature
	 * @param featureMap
	 */
	public static void resetGeometry(Feature feature, Map<String, List<Point2D>> featureMap) {
		if (feature != null && featureMap != null && feature.geometry != null && feature.geometry.parts.length == featureMap.size()) {
			if (featureMap.size() == 1) {
				List<Point2D> gps = featureMap.get(String.valueOf(feature.getID()));
				com.supermap.services.components.commontypes.Point2D[] points = new com.supermap.services.components.commontypes.Point2D[gps
						.size()];
				for (int i = 0; i < gps.size(); i++) {
					Point2D point2D = gps.get(i);
					com.supermap.services.components.commontypes.Point2D geoPoint = new com.supermap.services.components.commontypes.Point2D(
							point2D.x, point2D.y);
					points[i] = geoPoint;
				}
				feature.geometry.points = points;
				feature.geometry.parts[0] = points.length;
			} else {
				List<com.supermap.services.components.commontypes.Point2D> pointList = new ArrayList<com.supermap.services.components.commontypes.Point2D>();
				for (int i = 0; i < featureMap.size(); i++) {
					List<Point2D> gps = featureMap.get(String.valueOf(feature.getID()) + i);
					for (int j = 0; j < gps.size(); j++) {
						Point2D point2D = gps.get(j);
						com.supermap.services.components.commontypes.Point2D geoPoint = new com.supermap.services.components.commontypes.Point2D(
								point2D.x, point2D.y);
						pointList.add(geoPoint);
					}
					feature.geometry.parts[i] = gps.size();
				}
				com.supermap.services.components.commontypes.Point2D[] ps = new com.supermap.services.components.commontypes.Point2D[pointList
						.size()];
				feature.geometry.points = pointList.toArray(ps);
			}
		}
	}

	/**
	 * <p>
	 * 实现查询结果的监听器，自己实现处理结果接口
	 * </p>
	 * @author ${Author}
	 * @version ${Version}
	 * 
	 */
	static class MyGetFeaturesEventListener extends GetFeaturesEventListener {
		private GetFeaturesResult lastResult;

		public MyGetFeaturesEventListener() {
			super();
			// TODO Auto-generated constructor stub
		}

		public GetFeaturesResult getReult() {
			return lastResult;
		}

		@Override
		public void onGetFeaturesStatusChanged(Object sourceObject, EventStatus status) {
			if (sourceObject instanceof GetFeaturesResult) {
				lastResult = (GetFeaturesResult) sourceObject;
			}
		}

	}

	/**
	 * <p>
	 * 实现数据编辑结果的监听器，自己实现处理结果接口
	 * </p>
	 * @author ${Author}
	 * @version ${Version}
	 * 
	 */
	static class MyEditFeaturesEventListener extends EditFeaturesEventListener {
		private EditFeaturesResult lastResult;

		public MyEditFeaturesEventListener() {
			super();
			// TODO Auto-generated constructor stub
		}

		public EditFeaturesResult getReult() {
			return lastResult;
		}

		@Override
		public void onEditFeaturesStatusChanged(Object sourceObject, EventStatus status) {
			if (sourceObject instanceof EditFeaturesResult) {
				lastResult = (EditFeaturesResult) sourceObject;
			}
		}

	}

	public static List<Point2D> copyList(List<Point2D> points) {
		List<Point2D> list = new ArrayList<Point2D>();
		Iterator<Point2D> it = points.iterator();
		while (it.hasNext()) {
			Point2D p = it.next();
			list.add(new Point2D(p.x, p.y));
		}
		return list;
	}

}
