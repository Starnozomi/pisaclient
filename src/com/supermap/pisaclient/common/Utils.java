/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Utils.java - 2014-3-21 上午10:54:06
 */

package com.supermap.pisaclient.common;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.RoleType;
import com.supermap.pisaclient.http.HttpHelper;

import android.content.Context;

public class Utils {

	public static String getString(Context ctx, int resultId) {
		return ctx.getResources().getString(resultId);
	}

	public static String getDecimal(double value) {
		DecimalFormat format = new DecimalFormat("#0.000000");
		return format.format(value);
	}

	public static String getRoleTypeColor(int userId)
	{
		String colorStr="#6797d3";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"getrolebyuserid\",\"CustomParams\":{\"userid\":"+userId+"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			JSONObject obj = array.getJSONObject(0);//只会返回一个值，不需要循环
			RoleType roleType=new RoleType();//角色类型
			roleType.ID=obj.getInt("id");
			roleType.Code=obj.getString("code");
			roleType.Name=obj.getString("name");
			
			if(roleType.Code.equals("VIP"))
			{
				colorStr="#8552a1";
			}
			else if (roleType.Code.equals("ADMIN"))
			{
				colorStr="#4e72b8";
			}
			else if(roleType.Code.equals("EXPERT"))
			{
				colorStr="#f47920";
			}
			else if(roleType.Code.equals("PTYH"))
			{
				colorStr="#d64f44";
			}
			else
			{
				colorStr="#6797d3";
			}
			return colorStr;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return colorStr;
		}
	}
}
