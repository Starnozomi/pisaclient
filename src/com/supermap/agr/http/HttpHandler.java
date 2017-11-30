package com.supermap.agr.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class HttpHandler {
	/** ��Ϊ����� */
	private static final String TAG = "HttpUtils������";
	public static String JSESSIONID; // ����һ����̬���ֶΣ�����sessionID
	private static HttpParams httpParams;
	private static DefaultHttpClient httpClient;

	public static HttpClient getHttpClient() throws Exception {
		// ���� HttpParams ���������� HTTP ������һ���ֲ��Ǳ���ģ�
		httpParams = new BasicHttpParams();
		// �������ӳ�ʱ�� Socket ��ʱ���Լ� Socket �����С
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 100000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		// �����ض���ȱʡΪ true
		HttpClientParams.setRedirecting(httpParams, true);
		// ���� user agent
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		// ����һ�� HttpClient ʵ��
		// ע�� HttpClient httpClient = new HttpClient(); ��Commons HttpClient
		// �е��÷����� Android 1.5 ��������Ҫʹ�� Apache ��ȱʡʵ�� DefaultHttpClient
		httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}

	public static JSONObject postrequestJ(String url, List<NameValuePair> params) {
		// ��������Խ���һЩ���?�����ʱ������Բ����������ȵ�
		JSONObject object = new JSONObject(); // ����JSONObject����
		try {
			
			HttpClient httpClient = getHttpClient(); // ����HttpClient����
			HttpPost post = new HttpPost("http://" + url); // ��������·��
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8")); // �����������
			if (null != JSESSIONID) {
				post.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
			}
			//�����û�ʶ�� post.addHeader("User-Agent", User_Agent);
			/*
			 * // ����HttpParams���� HttpParams httpParams = post.getParams(); //
			 * ���ö�ȡ��ʱ
			 * httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			 * 1000000); // ��������ʱ
			 * httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,
			 * 1000000);
			 */
			
			HttpResponse httpResponse = httpClient.execute(post); // ���������û���
			HttpEntity httpEntity = httpResponse.getEntity(); // ȡ�÷�����Ϣ
			if (httpEntity != null) { // ���httpEntity��Ϊ��
				String result = EntityUtils.toString(httpEntity);
				result.trim();
				object = new JSONObject(result);
				CookieStore cookieStore = ((DefaultHttpClient) httpClient).getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					if ("JSESSIONID".equals(cookies.get(i).getName())) {
						JSESSIONID = cookies.get(i).getValue();
						break;
					}
				}
				Log.i(TAG, "post����ɹ�:" + result);
			} else {
				Log.e(TAG, "post����ʧ��");
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return object;
	}
	
	public static JSONObject postrequestT(String url, List<NameValuePair> params) {
		// ��������Խ���һЩ���?�����ʱ������Բ����������ȵ�
		JSONObject object = new JSONObject(); // ����JSONObject����
		try {
			
			HttpClient httpClient = getHttpClient(); // ����HttpClient����
			HttpPost post = new HttpPost("http://" + url); // ��������·��
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8")); // �����������
			if (null != JSESSIONID) {
				post.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
			}
			
			HttpResponse httpResponse = httpClient.execute(post); // ���������û���
			HttpEntity httpEntity = httpResponse.getEntity(); // ȡ�÷�����Ϣ
			if (httpEntity != null) { // ���httpEntity��Ϊ��
				String result = EntityUtils.toString(httpEntity);
				result.trim();
				object = new JSONObject(result);
				CookieStore cookieStore = ((DefaultHttpClient) httpClient).getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					if ("JSESSIONID".equals(cookies.get(i).getName())) {
						JSESSIONID = cookies.get(i).getValue();
						break;
					}
				}
				Log.i(TAG, "post����ɹ�:" + result);
			} else {
				Log.e(TAG, "post����ʧ��");
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return object;
	}
	
	public static JSONObject postrequestS(String url, List<NameValuePair> params) {
		// ��������Խ���һЩ���?�����ʱ������Բ����������ȵ�
		JSONObject object = new JSONObject();
		try {
			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			// ��������·��
			HttpPost post = new HttpPost("http://" + url);
			// �����������
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			if (null != JSESSIONID) {
				post.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
			}
			// ���������û���
			HttpResponse httpResponse = httpClient.execute(post);
			// ȡ�÷�����Ϣ
			HttpEntity httpEntity = httpResponse.getEntity();
			// ���httpEntity��Ϊ��
			if (httpEntity != null) {
				String result = EntityUtils.toString(httpEntity);
				JSONObject jb = (JSONObject) httpEntity;
				result = result.trim();
				object = jb;//new JSONObject(jb);
			} else {
				Log.e(TAG, "post����ʧ��");
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return object;
	}

	public static JSONArray postrequestJA(String url, List<NameValuePair> params) {
		// ��������Խ���һЩ���?�����ʱ������Բ����������ȵ�
		JSONArray ja = new JSONArray();
		try {
			// ����JSONObject����

			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			// ��������·��
			HttpPost post = new HttpPost("http://" + url);
			// �����������
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			if (null != JSESSIONID) {
				post.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
			}
			/*
			 * �����û�ʶ�� post.addHeader("User-Agent", User_Agent);
			 *
			 /*
			 * // ����HttpParams���� HttpParams httpParams = post.getParams(); //
			 * ���ö�ȡ��ʱ
			 * httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			 * 1000000); // ��������ʱ
			 * httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,
			 * 1000000);
			 */
			// ���������û���
			HttpResponse httpResponse = httpClient.execute(post);

			// ȡ�÷�����Ϣ
			HttpEntity httpEntity = httpResponse.getEntity();
			// ���httpEntity��Ϊ��
			if (httpEntity != null) {
				String result = EntityUtils.toString(httpEntity);
				result=result.trim();
				ja = new JSONArray(result);
				CookieStore cookieStore = ((DefaultHttpClient) httpClient).getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					if ("JSESSIONID".equals(cookies.get(i).getName())) {
						JSESSIONID = cookies.get(i).getValue();
						break;
					}
				}
				Log.i(TAG, "post����ɹ�:" + result);
			} else {
				Log.e(TAG, "post����ʧ��");
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return ja;
	}

//	public static boolean postrequestB(String url, List<NameValuePair> params) {
//		JSONObject object = postrequestJ(url, params);
//		String success = null;
//		String msg = null;
//		try {
//			 success = object.getString("success").toString();
//			 msg = object.getString("msg").toString();
//		} catch (JSONException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		if(success.equals("1") || success.equals("true")){
//			return true;
//		}else{
//			Toast.makeText(MyApplication.getInstance(),msg, 1000);
//			return false;
//		}
//	}
	
	public static boolean postrequestC(String url, List<NameValuePair> params) {
		JSONObject object = postrequestJ(url, params);
		int pass = -1;
		try {
			 pass = object.getInt("pass");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if( pass == -1) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ���imagepath��ȡbitmap
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getBitmap(String imagePath){
		if(!(imagePath.length()>5)){
			return null;
	    }

	File cache_file = new File(new File(Environment.getExternalStorageDirectory(), "xxxx"),"cachebitmap");

	cache_file = new File(cache_file,getMD5(imagePath));

	if(cache_file.exists()){
	    return BitmapFactory.decodeFile(getBitmapCache(imagePath));
	}else{
		try {
			URL url = new URL(imagePath);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			if (conn.getResponseCode() == 200) {
					InputStream inStream = conn.getInputStream();
					File file = new File(new File(Environment.getExternalStorageDirectory(), "xxxx"),"cachebitmap");
					if(!file.exists())file.mkdirs();
					file = new File(file,getMD5(imagePath));
					FileOutputStream out = new FileOutputStream(file);

					byte buff[] = new byte[1024];

					int len = 0;

					while((len = inStream.read(buff))!=-1){
						out.write(buff,0,len);
					}

					out.close();
					inStream.close();

					return BitmapFactory.decodeFile(getBitmapCache(imagePath));
			}

		} catch (Exception e) {}

	}

	return null;

	}

	/**
	 * ��ȡ����
	 * @param url
	 * @return
	 */
	public static String getBitmapCache(String url){

	File file = new File(new File(Environment.getExternalStorageDirectory(), "xxxx"),"cachebitmap");

	file = new File(file,getMD5(url));

	if(file.exists()){

	return file.getAbsolutePath();

	}

	return null;

	}

	//����ΪMD5

	public static String getMD5(String content) {

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");

			digest.update(content.getBytes());

			return getHashString(digest);
			
		} catch (Exception e) {

		}
		return null;
	}



	private static String getHashString(MessageDigest digest) {

	        StringBuilder builder = new StringBuilder();
	        for (byte b : digest.digest()) {

	            builder.append(Integer.toHexString((b >> 4) & 0xf));

	            builder.append(Integer.toHexString(b & 0xf));

	        }
	        return builder.toString().toLowerCase();

	    }



}
