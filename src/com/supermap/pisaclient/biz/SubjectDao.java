package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.http.HttpHelper;

public class SubjectDao {

	
	public List<Subject> getSubjects(int parentId){
		List<Subject> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("parentId", parentId);
			jsonParam.put("Function", "adv.subject.get");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<Subject>();
			Subject subject = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				subject = new Subject();
				if (obj.opt("subjectId")!=null) {
					subject.subjectId = obj.optInt("subjectId");
				}
				if (obj.opt("name")!=null) {
					subject.name = obj.optString("name");
				}
				if (obj.opt("parentId")!=null) {
					subject.parentId = obj.optInt("parentId");
				}
				list.add(subject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
			return null;
		}
		return list;
	}
}
