package com.supermap.pisaclient.common;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;

public class Smsverification {
 
//	public static void main(String[] args) throws HttpException, IOException {
//		HttpClient client = new HttpClient();
//		PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
//
//		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
//
//		NameValuePair[] data = { new NamevValuePair("Uid", "supermap"), // 注册的用户名
//				new NameValuePair("Key", "4f82c4eaac842edd1d35"), // 注册成功后,登录网站使用的密钥
//				new NameValuePair("smsMob", "123456789"), // 手机号码(应该是发送短信的用户手机号)
//				new NameValuePair("smsText", "验证码：8888") };// 设置短信内容
//                                                       //923970
//		post.setRequestBody(data);
//	
//	    client.executeMethod(post);  
//	    Header[] headers = post.getResponseHeaders();  
//	    int statusCode = post.getStatusCode();  
//	    System.out.println("statusCode:" + statusCode);  
//	    for (Header h : headers) {  
//	        System.out.println(h.toString());  
//	    }  
//	    String result = new String(post.getResponseBodyAsString().getBytes(  
//	            "gbk"));  
//	    System.out.println(result);  
//	    post.releaseConnection(); 
//	
//	}
}
