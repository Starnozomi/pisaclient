package com.supermap.agr.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.agr.common.Constant;

public class HttpHelper {

	private static HttpResponse httpresponse = null;

	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	public static final int CONN_TIMEOUT = 4 * 1000;
	public static final int REQUEST_TIMEOUT = 4 * 1000;
//	public static final int RESPONSE_TIMEOUT = 8 * 1000;
	public static final int RESPONSE_TIMEOUT = 5 * 1000;

	private static final int TIME_OUT = 10 * 1000;
	private static final String CHARSET = "utf-8";
	public static final String FAILURE = "failure";

	public static String loadPic(File file, String strUrl, HashMap<String, String> params) {
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";

		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			OutputStream outputSteam = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputSteam);
			StringBuffer sb = new StringBuffer();

			for (Map.Entry<String, String> m : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"" + m.getKey() + "\"" + LINE_END);
				sb.append(LINE_END);
				sb.append(m.getValue() + LINE_END);
			}

			if (file != null) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
			}

			dos.write(sb.toString().getBytes());
			if (file != null) {
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
			}

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			int res = conn.getResponseCode();
			if (res == 200) {
				return conn.getResponseMessage();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}

	public static String loadPic(File file, String strUrl) {
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";
		DataOutputStream dos =null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			OutputStream outputSteam = conn.getOutputStream();
			dos = new DataOutputStream(outputSteam);
			StringBuffer sb = new StringBuffer();

			if (file != null) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + file.getName() + "\"" + LINE_END);
//				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append("Content-Type: image/jpg;" + LINE_END);
				sb.append(LINE_END);
			}

			dos.write(sb.toString().getBytes());
			if (file != null) {
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
			}

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			int res = conn.getResponseCode();
			if (res == 200) {
				InputStream in = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int index = -1;
				String result = "";
				try {
					while ((index = in.read(buffer)) != -1) {
						result += new String(buffer, 0, index);
					}
				} catch (Exception e) {

				}
				return result;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (dos!=null) {
					dos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return FAILURE;
	}
	
	public static String loadPic(InputStream is, String strUrl) {
		if (is ==null) {
			return FAILURE;
		}
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";
		DataOutputStream dos =null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			OutputStream outputSteam = conn.getOutputStream();
			dos = new DataOutputStream(outputSteam);
			StringBuffer sb = new StringBuffer();
			
//			if (file != null) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + "pic"+System.currentTimeMillis()+".jpg" + "\"" + LINE_END);
//				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append("Content-Type: image/jpg;" + LINE_END);
				sb.append(LINE_END);
//			}

			dos.write(sb.toString().getBytes());
//			if (file != null) {
//				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
//			}

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			int res = conn.getResponseCode();
			if (res == 200) {
				InputStream in = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int index = -1;
				String result = "";
				try {
					while ((index = in.read(buffer)) != -1) {
						result += new String(buffer, 0, index);
					}
				} catch (Exception e) {

				}
				return result;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (dos!=null) {
					dos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return FAILURE;
	}

	public static final BasicHttpParams getHttpParams() {
		BasicHttpParams httpParams = new BasicHttpParams();
		ConnManagerParams.setTimeout(httpParams, CONN_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
		return httpParams;
	}

	public static HttpResponse getLastHtttpResponse() {
		return httpresponse;
	}

	public static JSONArray load(HashMap<String, String> params) throws Exception {
		return load(new DefaultHttpClient(getHttpParams()), null, METHOD_POST, params);
	}

	public static JSONArray load(HashMap<String, String> params, String method) throws Exception {
		return load(new DefaultHttpClient(getHttpParams()), null, method, params);
	}

	public static JSONArray load(String url) throws Exception {
		return load(url, null, METHOD_POST);
	}

	public static JSONArray load(String url, HashMap<String, String> params) throws Exception {
		return load(new DefaultHttpClient(getHttpParams()), url, METHOD_POST, params);
	}

	public static JSONArray load(String url, HashMap<String, String> params, String method) throws Exception {
		return load(new DefaultHttpClient(getHttpParams()), url, method, params);
	}

	public static JSONArray load(HttpClient httpclient, String url, String method, HashMap<String, String> params) throws Exception {
		httpclient.getParams().setParameter("http.socket.timeout", new Integer(30000));
		InputStream is = null;
		String fullUrl = getBaseUrl();
		if (url != null)
			fullUrl = url;
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		httpresponse = null;
		if (params == null) {
			params = new HashMap<String, String>();
		}

		if (params != null) {
			fullUrl = url + "?";
			for (Map.Entry<String, String> m : params.entrySet()) {
				nvp.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				fullUrl += "&" + m.getKey() + "=" + m.getValue();
			}
		}

		if (method == METHOD_GET) {
			HttpGet httpGet = new HttpGet(fullUrl);
			if (null != Constant.SESSIONID) {
				httpGet.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpresponse = httpclient.execute(httpGet);
		} else if (method == METHOD_POST) {
			HttpPost httpPost = new HttpPost(url);
			if (null != Constant.SESSIONID) {
				httpPost.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			httpresponse = httpclient.execute(httpPost);
		}

		if (httpresponse != null) {
			HttpEntity httpEntity = httpresponse.getEntity();
			if (httpEntity != null) {
				is = httpEntity.getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String json = reader.readLine();
				return new JSONArray(json);
			}
		}

		return null;
	}

	public static JSONObject loadOne(HashMap<String, String> params) throws Exception {
		return loadOne(new DefaultHttpClient(getHttpParams()), null, METHOD_POST, params,false);
	}

	public static JSONObject loadOne(HashMap<String, String> params, String method) throws Exception {
		return loadOne(new DefaultHttpClient(getHttpParams()), null, method, params,false);
	}

	
	
	public static JSONObject loadOne(String url) throws Exception {
		return loadOne(url, null, METHOD_POST);
	}

	public static JSONObject loadOne(String url, HashMap<String, String> params) throws Exception {
		return loadOne(new DefaultHttpClient(getHttpParams()), url, METHOD_POST, params,false);
	}

	public static JSONObject loadOne(String url, HashMap<String, String> params, String method) throws Exception {
		return loadOne(new DefaultHttpClient(getHttpParams()), url, method, params,false);
	}
	
	public static JSONObject loadOneGzip(String url, HashMap<String, String> params) throws Exception{
		return loadOne(new DefaultHttpClient(getHttpParams()), url, METHOD_POST, params,true);
	}

	public static byte[] getByteData(String url, String method, HashMap<String, String> params) throws Exception {
		HttpClient httpclient = new DefaultHttpClient(getHttpParams());
		String fullUrl = getBaseUrl();
		if (url != null)
			fullUrl = url;
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		httpresponse = null;
		if (params == null) {
			params = new HashMap<String, String>();
		}

		if (params != null) {
			fullUrl = url + "?";
			for (Map.Entry<String, String> m : params.entrySet()) {
				nvp.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				fullUrl += "&" + m.getKey() + "=" + m.getValue();
			}
		}

		if (method == METHOD_GET) {
			HttpGet httpGet = new HttpGet(fullUrl);
			httpresponse = httpclient.execute(httpGet);
		} else if (method == METHOD_POST) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			httpresponse = httpclient.execute(httpPost);
		}

		CookieStore mCookieStore = ((DefaultHttpClient) httpclient).getCookieStore();
		List<Cookie> cookies = mCookieStore.getCookies();
		for (int i = 0; cookies != null && i < cookies.size(); i++) {
			if ("JSESSIONID".equals(cookies.get(i).getName())) {
				Constant.SESSIONID = cookies.get(i).getValue();
				break;
			}
		}
		if (httpresponse != null) {
			return EntityUtils.toByteArray(httpresponse.getEntity());
		}

		return null;
	}

	public static JSONObject loadOne(HttpClient httpclient, String url, String method, HashMap<String, String> params,boolean gzip) throws Exception {
		InputStream is = null;
		String fullUrl = getBaseUrl();
		if (url != null)
			fullUrl = url;
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		httpresponse = null;
		if (params == null) {
			params = new HashMap<String, String>();
		}

		if (params != null) {
			fullUrl = url + "?";
			for (Map.Entry<String, String> m : params.entrySet()) {
				nvp.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				fullUrl += "&" + m.getKey() + "=" + m.getValue();
			}
		}

		if (method == METHOD_GET) {
			HttpGet httpGet = new HttpGet(fullUrl);
			if (null != Constant.SESSIONID) {
				httpGet.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpresponse = httpclient.execute(httpGet);
		} else if (method == METHOD_POST) {
			HttpPost httpPost = new HttpPost(url);
			if (null != Constant.SESSIONID) {
				httpPost.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			httpresponse = httpclient.execute(httpPost);
		}

		//服务端压缩json，add by heyao
		if (httpresponse != null) {
			HttpEntity httpEntity = httpresponse.getEntity();
			if (httpEntity != null) {
				is = httpEntity.getContent();
				BufferedReader reader = null;
				if(gzip){
					GZIPInputStream gis = new GZIPInputStream(is);	
					 reader = new BufferedReader(new InputStreamReader(gis,"utf-8"));
				}else{
					reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
				}
				String json = reader.readLine();
				JSONObject jo = new JSONObject(json);
				return jo;
				
			}
		}

		return null;
	}

	public static String loadString(HashMap<String, String> params) throws Exception {
		return loadString(new DefaultHttpClient(getHttpParams()), null, METHOD_POST, params);
	}

	public static String loadString(HashMap<String, String> params, String method) throws Exception {
		return loadString(new DefaultHttpClient(getHttpParams()), null, method, params);
	}

	public static String loadString(String url) throws Exception {
		return loadString(url, null, METHOD_POST);
	}

	public static String loadString(String url, String method) throws Exception {
		return loadString(url, null, method);
	}

	public static String loadString(String url, HashMap<String, String> params) throws Exception {
		return loadString(new DefaultHttpClient(getHttpParams()), url, METHOD_POST, params);
	}

	public static String loadString(String url, HashMap<String, String> params, String method) throws Exception {
		return loadString(new DefaultHttpClient(getHttpParams()), url, method, params);
	}

	public static String loadString(HttpClient httpclient, String url, String method, HashMap<String, String> params) throws Exception {
		InputStream is = null;
		String fullUrl = getBaseUrl();

		if (url != null) fullUrl = url;
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		httpresponse = null;

		if (params == null) {
			params = new HashMap<String, String>();
		}

		if (params != null) {
			fullUrl = url + "?";
			for (Map.Entry<String, String> m : params.entrySet()) {
				nvp.add(new BasicNameValuePair(m.getKey(), m.getValue()));
				fullUrl += "&" + m.getKey() + "=" + m.getValue();
			}
		}

		if (method == METHOD_GET) {
			HttpGet httpGet = new HttpGet(fullUrl);
			if (null != Constant.SESSIONID) {
				httpGet.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpresponse = httpclient.execute(httpGet);
		} else if (method == METHOD_POST) {
			HttpPost httpPost = new HttpPost(url);
			if (null != Constant.SESSIONID) {
				httpPost.setHeader("Cookie", "JSESSIONID=" + Constant.SESSIONID);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
			httpresponse = httpclient.execute(httpPost);
		}

		if (httpresponse != null) {
			HttpEntity httpEntity = httpresponse.getEntity();
			if (httpEntity != null) {
				is = httpEntity.getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = reader.readLine();
				StringBuffer sb = new StringBuffer();
				while (line != null) {
					sb.append(line);
					line = reader.readLine();
				}
				return sb.toString();
			}
		}

		return null;
	}

	public static String getMapUrl() {
		return getBaseUrl() + "/TiledCacheService/TiledCacheServlet?";
	}

	public static String getBaseUrl() {
		return BASE_URL + ":" + HttpHelper.PORT;
	}

	public static String getDataBaseUrl() {
		return BASE_URL + ":" + HttpHelper.PORT + "/SPPMDataService/services/data/";
	}

	public static String BASE_URL = "http://192.168.0.125"; 
	public static String FORMAL_URL = "http://www.ynpasc.cn"; 
	public static String PORT = "8088";

	public static String USER_LOGIN = "/loginController.do?ajaxLogin";
	public static String USER_INFO = getBaseUrl() + "/userController.do?getUserInfo";
	
}
