package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.AdvInfo;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.Climate;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmPoint;
import com.supermap.pisaclient.entity.LivePoint;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProfProduct;
import com.supermap.pisaclient.entity.SituationInfo;
import com.supermap.pisaclient.entity.SituationPoint;
import com.supermap.pisaclient.entity.VipProductPoint;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.service.ProductLayer.PTYPE;
import com.supermap.pisaclient.service.ProductPoint;

public class OneMapDao 
{
	
	/**
	 * 获取专业产品数据，全市、地区、大户不同视图
	 * @return
	 */
	public static List<ProductPoint> getProfProducts(String starttime,String endtime,String areaCode,int crop)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		if(areaCode == "50"||areaCode == ""){
			params.put("param", "{\"Function\":\"product.monitorproduct.all\",\"CustomParams\":{\"starttime\":\""+ starttime +"\",\"endtime\":\""+ endtime +"\",\"crop\":\""+ crop +"\"},\"Type\":2}");
		}else{
			params.put("param", "{\"Function\":\"product.monitorproduct.area\",\"CustomParams\":{\"starttime\":\""+ starttime +"\",\"endtime\":\""+ endtime +"\",\"areacode\":\"" + areaCode + "\",\"crop\":\""+ crop +"\"},\"Type\":2}");
		}
		
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<ProductPoint> products=new ArrayList<ProductPoint>();
			
			ProductPoint product=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				jo= array.getJSONObject(i);
				product = new ProductPoint();
				product.content = jo.has("content") ? jo.getString("content"):"";
				product.lat = jo.has("lat") ? jo.getDouble("lat") : null;
				product.lon = jo.has("lon") ? jo.getDouble("lon") : null;
				products.add(product);
				/*vipUser=new VipUser();
				jo= array.getJSONObject(i);
				vipUser.setId(jo.get("id")!=null?jo.getInt("id"):null);
				vipUser.setLongitude(jo.get("longitude")!=null?jo.getDouble("longitude"):null);
				vipUser.setLatitude(jo.get("latitude")!=null?jo.getDouble("latitude"):null);
				vipUser.setName(jo.get("name")!=null?jo.getString("name"):null);
				products.add(products);*/
				
			}
			return products;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<ProductPoint>();
		}
	}
	
	
	
	/**
	 * 获取大户点数据信息
	 * @return
	 */
	public static List<VipUser> getVipUser(String areaCode)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		//params.put("param", "{\"Function\":\"monitor.getvips\",\"CustomParams\":{\"areacode\":\"" + areaCode + "\"},\"Type\":2}");
		params.put("param", "{\"Function\":\"monitor.getvips\",\"CustomParams\":{\"areacode\":\"53\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<VipUser> vips=new ArrayList<VipUser>();
			
			VipUser vipUser=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				vipUser=new VipUser();
				jo= array.getJSONObject(i);
				vipUser.setId(jo.get("id")!=null?jo.getInt("id"):null);
				vipUser.setLongitude(jo.get("longitude")!=null?jo.getDouble("longitude"):null);
				vipUser.setLatitude(jo.get("latitude")!=null?jo.getDouble("latitude"):null);
				vipUser.setName(jo.get("name")!=null?jo.getString("name"):null);
				vipUser.setUserid(jo.get("userid") != null ? jo.getInt("userid"):null);
				vips.add(vipUser);
			}
			return vips;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<VipUser>();
		}
	}
	
	public static List<AdvInfo> getAdvInfos(String areaCode)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"monitor.getadvinfos\",\"CustomParams\":{\"areacode\":\"" + areaCode + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<AdvInfo> infos=new ArrayList<AdvInfo>();
			
			AdvInfo advInfo=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				advInfo=new AdvInfo();
				jo= array.getJSONObject(i);
				advInfo.setId(jo.get("id")!=null?jo.getInt("id"):null);
				advInfo.setLongitude(jo.get("longitude")!=null?jo.getDouble("longitude"):null);
				advInfo.setLatitude(jo.get("latitude")!=null?jo.getDouble("latitude"):null);
				advInfo.setName(jo.get("qestion")!=null?jo.getString("qestion"):null);
				infos.add(advInfo);
			}
			return infos;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<AdvInfo>();
		}
	}
	
	
	/**********************
	 * 获取服务产品列表
	 * @param dt
	 * @return
	 */
	public static List<Product> getServiceProduct(String dt){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"meteomap.product.productlist\",\"CustomParams\":{\"dt\":\"" + dt + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<Product> products=new ArrayList<Product>();
			
			Product p=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				p=new Product();
				jo= array.getJSONObject(i);
				p.ProductID = jo.getInt("productid");
				p.ProductTitle = jo.getString("producttitle");
				products.add(p);
			}
			return products;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<Product>();
		}
	}
	
	
	/********
	 * 获取VIP服务产品分布
	 * @param dt
	 * @return
	 */
	public static List<ProductPoint> getVipProductPoint(Integer productid){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"meteomap.product.vip\",\"CustomParams\":{\"productid\":\"" + productid + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<ProductPoint> products=new ArrayList<ProductPoint>();
			
			ProductPoint p=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				p=new ProductPoint();
				jo= array.getJSONObject(i);
				p.id = jo.getInt("id");
				p.content = jo.getString("content");
				p.productId  = jo.getInt("profProductId");
				p.productTime = jo.getString("ProductTime");
				p.lon = jo.getDouble("longitude");
				p.lat = jo.getDouble("latitude");
				p.productTitle = jo.getString("ProductTitle");
				products.add(p);
			}
			return products;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<ProductPoint>();
		}
	}
	
	public static List<Climate> getFarmClimates(String areacode,Integer month){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"app.getClimate\",\"CustomParams\":{\"areacode\":\"" + areacode + "\",\"month\":\"" + month + "\",\"climateType\":0},\"Type\":2}");
		
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			List<Climate> climates=new ArrayList<Climate>();
			Climate c;
			JSONObject jo;
			
			for(int i=0;i<array.length();i++)
			{
				c=new Climate();
				jo= array.getJSONObject(i);
				c.setAreaCode(jo.getString("areaCode"));
				c.setClimateType(jo.getString("climateType"));
				c.setMonth(jo.getString("month"));
				c.setYear_value(jo.getString("year_value"));
				c.setMonth_value(jo.getString("month_value"));
				climates.add(c);
			}
			return climates;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<Climate>();
		}
	}
	
	
	public static List<ProductPoint> getProductPointByTimeSpan(String starttime, String endtime, int farmid, String areacode){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"meteomap.product.timespan\",\"CustomParams\":{\"startTime\":\"" + starttime + "\",\"endTime\":\"" + endtime + "\",\"farm\":"+ farmid +",\"areacode\":'"+ areacode +"'},\"Type\":2}");
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<ProductPoint> products=new ArrayList<ProductPoint>();
			ProductPoint p;
			JSONObject jo;
			
			for(int i=0;i<array.length();i++) {
				p=new ProductPoint();
				jo= array.getJSONObject(i);
				p.id = jo.getInt("id");
				p.content = jo.getString("content");
				p.productId  = jo.getInt("productID");
				p.productTime = jo.getString("ProductTime");
				p.lon = jo.getDouble("longitude");
				p.lat = jo.getDouble("latitude");
				p.productTitle = jo.getString("ProductTitle");
				p.type = jo.getInt("ptype");
				p.farmName = jo.getString("farmname");
				products.add(p);
			}
			return products;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProductPoint>();
		}
	}
	

	
	/**
	 * 获取农田点信息
	 * @return
	 */
	public static List<FarmPoint> getFarmPoint(String areaCode) {
		HashMap<String, String> params = new HashMap<String, String>();
		//params.put("param", "{\"Function\":\"monitor.getallfarmland\",\"CustomParams\":{\"areacode\":\"" + areaCode + "\"},\"Type\":2}");
		params.put("param", "{\"Function\":\"monitor.getallfarmland\",\"CustomParams\":{\"areacode\":\"53\"},\"Type\":2}");
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<FarmPoint> farms=new ArrayList<FarmPoint>();
			
			FarmPoint farm=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				farm=new FarmPoint();
				jo= array.getJSONObject(i);
				farm.setId(jo.get("id")!=null?jo.getInt("id"):null);
				farm.setLongitude(jo.get("longitude")!=null?jo.getDouble("longitude"):null);
				farm.setLatitude(jo.get("latitude")!=null?jo.getDouble("latitude"):null);
				farm.setDescript(jo.get("descript")!=null?jo.getString("descript"):null);
				farms.add(farm);
			}
			return farms;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<FarmPoint>();
		}
	}
	
	/**
	 * 获取农情点的信息
	 * @return
	 */
	public static List<SituationPoint> getSituationPoint(String areaCode)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		//params.put("param", "{\"Function\":\"monitor.getallagrinfo\",\"CustomParams\":{\"areacode\":\"" + areaCode + "\"},\"Type\":2}");
		params.put("param", "{\"Function\":\"monitor.getallagrinfo\",\"CustomParams\":{\"areacode\":\"53\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<SituationPoint> situations=new ArrayList<SituationPoint>();
			
			SituationPoint situation=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				situation=new SituationPoint();
				jo= array.getJSONObject(i);
				situation.setId(jo.get("id")!=null?jo.getInt("id"):null);
				situation.setLongitude(jo.get("longitude")!=null?jo.getDouble("longitude"):null);
				situation.setLatitude(jo.get("latitude")!=null?jo.getDouble("latitude"):null);
				situations.add(situation);
			}
			return situations;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<SituationPoint>();
		}
	}
	

	public static List<LivePoint> getLivePoints(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"liveInfo.getAll.indexLocation\",\"CustomParams\":{\"userId\":\"" + 1 + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<LivePoint> points = new ArrayList<LivePoint>();
			
			LivePoint point=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				point= new LivePoint();
				jo= array.getJSONObject(i);
				point.setId(jo.get("id")!=null?jo.getInt("id"):null);
				point.setLongitude(jo.get("lng")!=null?jo.getDouble("lng"):null);
				point.setLatitude(jo.get("lat")!=null?jo.getDouble("lat"):null);
				point.setName(jo.getString("livename")!=null?jo.getString("livename"):null);
				
				points.add(point);
			}
			return points;
		}  
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<LivePoint>();
		}
	}
	
	public static List<AreaPoint> getAreaPoints(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"station.list\",\"CustomParams\":{\"stationType\":\"" + 1 + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<AreaPoint> areaPoints = new ArrayList<AreaPoint>();
			
			AreaPoint areaPoint=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				areaPoint=new AreaPoint();
				jo= array.getJSONObject(i);
				areaPoint.setId(jo.get("station_id")!=null?jo.getInt("station_id"):null);
				areaPoint.setLongitude(jo.get("lon")!=null?jo.getDouble("lon"):null);
				areaPoint.setLatitude(jo.get("lat")!=null?jo.getDouble("lat"):null);
				areaPoint.setName(jo.getString("station_name")!=null?jo.getString("station_name"):null);
				areaPoint.setCode(jo.getString("aeracode")!=null?jo.getString("aeracode"):null);
				areaPoints.add(areaPoint);
			}
			return areaPoints;
		}  
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<AreaPoint>();
		}
	}
	
	/**
	 * 获取大户的详细信息
	 * @param id 大户的id
	 * @return
	 */
	public static VipUserInfo getVipUserInfo(int id)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"monitor.getvipone\",\"CustomParams\":{\"id\":"+ id +"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			List<VipUserInfo> vipUserInfos=new ArrayList<VipUserInfo>();
			VipUserInfo info=new VipUserInfo();
			JSONObject jo=array.getJSONObject(0);
			info.setBelongsTownship(jo.get("BelongsTownship")!=null?jo.getString("BelongsTownship"):"暂无");
			info.setBelongsVillage(jo.get("BelongsVillage")!=null?jo.getString("BelongsVillage"):"暂无");
			info.setBusinessScope(jo.get("BusinessScope")!=null?jo.getString("BusinessScope"):"暂无");
			info.setCountyName(jo.get("CountyName")!=null?jo.getString("CountyName"):"暂无");
			info.setId(jo.get("id")!=null?jo.getInt("id"):null);
			info.setLeaderName(jo.get("LeaderName")!=null?jo.getString("LeaderName"):"暂无");
			info.setLeaderPhone(jo.get("LeaderPhone")!=null?jo.getString("LeaderPhone"):"暂无");
			info.setName(jo.get("name")!=null?jo.getString("name"):"暂无");
			
			info.setAreaScale((jo.get("AreaScale")!=null?jo.getString("AreaScale")+"亩":"暂无"));
			info.setAnnualProductionValue((jo.get("AnnualProductionValue")!=null?jo.getString("AnnualProductionValue"):"暂无"));
			info.setProdOperaCount((jo.get("ProdOperaCount")!=null?jo.getString("ProdOperaCount"):"暂无"));
			info.setTechCount((jo.get("TechCount")!=null?jo.getString("TechCount"):"暂无"));
			info.setTechAverageAge((jo.get("TechAverageAge")!=null?jo.getString("TechAverageAge"):"暂无"));
			return info;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			VipUserInfo info=new VipUserInfo();
			info.setBelongsTownship("暂无");
			info.setBelongsVillage("暂无");
			info.setBusinessScope("暂无");
			info.setCountyName("暂无");
			info.setLeaderName("暂无");
			info.setLeaderPhone("暂无");
			info.setName("暂无");
			info.setAreaScale("暂无");
			info.setAnnualProductionValue("暂无");
			info.setProdOperaCount("暂无");
			info.setTechCount("暂无");
			info.setTechAverageAge("暂无");
			return info;
		}
	}
	
	/**
	 * 获取农田点的详细信息
	 * @param id
	 * @return
	 */
	public static FarmInfo getFarmInfo(int id)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"monitor.getfarmlandone\",\"CustomParams\":{\"id\":\"" + id + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			FarmInfo info=new FarmInfo();
			JSONObject jo=array.getJSONObject(0);
			info.setAreaname(jo.get("areaname")!=null?jo.getString("areaname"):"暂无");
			info.setCrop(jo.get("crop")!=null?jo.getString("crop"):"暂无");
			info.setDescript(jo.get("descript")!=null?jo.getString("descript"):"暂无");
			info.setLatitude(jo.get("latitude")!=null?jo.getString("latitude"):"暂无");
			info.setLongitude(jo.get("longitude")!=null?jo.getString("longitude"):"暂无");
			info.setShowName(jo.get("showName")!=null?jo.getString("showName"):"暂无");
			return info;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			FarmInfo info=new FarmInfo();
			info.setAreaname("暂无");
			info.setCrop("暂无");
			info.setDescript("暂无");
			info.setLatitude("暂无");
			info.setLongitude("暂无");
			info.setShowName("暂无");
			return info;
		}
	}
	
	/**
	 * 获取农情点的详细信息
	 * @param id
	 * @return
	 */
	public static SituationInfo getSituationInfo(int id)
	{
		HashMap<String, String> paramsInfo = new HashMap<String, String>();
		HashMap<String, String> paramsImage = new HashMap<String, String>();
		paramsInfo.put("param", "{\"Function\":\"monitor.getagrinfoone\",\"CustomParams\":{\"id\":\"" + id + "\"},\"Type\":2}");
		paramsImage.put("param", "{\"Function\":\"monitor.getagrimgs\",\"CustomParams\":{\"id\":\"" + id + "\"},\"Type\":2}");
		try 
		{
			JSONArray arrayInfo = HttpHelper.load(HttpHelper.FUNCRION_QUERY, paramsInfo);
			JSONArray arrayImage = HttpHelper.load(HttpHelper.FUNCRION_QUERY, paramsImage);
			
			SituationInfo info=new SituationInfo();
			
			JSONObject jo=arrayInfo.getJSONObject(0);
			
			info.setCropName(jo.get("cropname")!=null?jo.getString("cropname"):"暂无");
			info.setDescript(jo.get("descript")!=null?jo.getString("descript"):"暂无");
			info.setId(jo.get("id")!=null?jo.getString("id"):"暂无");
			info.setPubertyName(jo.get("pubertyname")!=null?jo.getString("pubertyname"):"暂无");
			info.setUploadTime(jo.get("uploadTime")!=null?jo.getString("uploadTime"):"暂无");
			info.setPublishName(jo.get("username")!=null?jo.getString("username"):"暂无");
			
			List<String> images=new ArrayList<String>();
			if(arrayImage.length()==0)
			{
				info.setImages(images);
			}
			else
			{
				for(int i=0;i<arrayImage.length();i++)
				{
					jo=arrayImage.getJSONObject(i);
					images.add(HttpHelper.getBaseUrl()+"/"+jo.getString("imageURLContent"));
				}
				info.setImages(images);
			}
			
			return info;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			SituationInfo info=new SituationInfo();
			info.setCropName("暂无");
			info.setDescript("暂无");
			info.setId("暂无");
			info.setImages(new ArrayList<String>());
			info.setPubertyName("暂无");
			info.setPublishName("暂无");
			info.setUploadTime("暂无");
			return info;
		}
	}
	
	
	public static AreaPoint getAreaPointInfo(int id)
	{
		HashMap<String, String> paramsInfo = new HashMap<String, String>();
		paramsInfo.put("param", "{\"Function\":\"monitor.getagrinfoone\",\"CustomParams\":{\"id\":\"" + id + "\"},\"Type\":2}");
		try 
		{
			JSONArray arrayInfo = HttpHelper.load(HttpHelper.FUNCRION_QUERY, paramsInfo);
		
			
			AreaPoint info=new AreaPoint();
			
			JSONObject jo=arrayInfo.getJSONObject(0);
			
			
			
			List<String> images=new ArrayList<String>();
			
			
			return info;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			AreaPoint info=new AreaPoint();
		
			return info;
		}
	}
}
