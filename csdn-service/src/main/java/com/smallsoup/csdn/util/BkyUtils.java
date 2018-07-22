package com.smallsoup.csdn.util;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class BkyUtils {

	public static void main(String[] args) {
		String userName = "smallsoup1";
		String password = "ll19940109,,..";
		String loginUrl = "https://passport.cnblogs.com/user/signin";
		String dataUrl = "https://home.cnblogs.com/u/whatbeg/followers/";
		HttpClientLogin(userName, password, loginUrl, dataUrl);
		
		getBlogLists();
	}

	private static void getBlogLists() {
		
		String url = "https://www.cnblogs.com/";
		
		
		
	}

	private static void HttpClientLogin(String userName, String password, String loginUrl, String dataUrl) {
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		PostMethod postMethod = new PostMethod(loginUrl);

		NameValuePair[] postData = { new NameValuePair("input1", "hFqPVbcHLuxeGsSQz9q1hjAIVJX43A5Iww8feVLAkNwrCRZenIZjhF9xDFUP29xKzHfkmT/26BrSb0e/pwUO6qRZRrGI5XFIHfBs2P4qBRCEufpfiQhU5GbjRNapqxkuB7MKEc4nWc9kdYhkhbumzDrC29V0/CSlrjbozvGCX4M="),
				new NameValuePair("input2", "Un2r1Zk5EvhpxSNAkba5k11E1NwtfVAHFk10snmpxmdqNIaUGypGHd4JK9bY6AYNi+//KmIAk1WmLvbC1TF/gF4Vzt3zkUPNQiORQKR4rSztyqnL4nxYpx6zvhTi61+MA8aAC4iWTZBQwBt7bDKzTpNfp3e1XepcCEPpyEm7l3s="),
		new NameValuePair("geetest_validdate", "4c0406551b06e40fedab2225838972d8"),
		new NameValuePair("geetest_seccode", "4c0406551b06e40fedab2225838972d8|jordan"),
		new NameValuePair("geetest_challenge", "82ba831f9363fc8fb5beb7fdeaadcdaeap"),
		new NameValuePair("remember", "True") };
		postMethod.setRequestBody(postData);

		try {
			setHeaders(postMethod);
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.executeMethod(postMethod);
			
			
			System.out.println("-------------------------------");
			System.out.println("---" + postMethod.getResponseBodyAsString());
			
			Cookie[] cookies = httpClient.getState().getCookies();
			StringBuffer stringBuffer = new StringBuffer();
			for (Cookie c : cookies) {
				stringBuffer.append(c.toString() + ";");
			}

			GetMethod getMethod = new GetMethod(dataUrl);
			
			setHeaders(getMethod);
			httpClient.executeMethod(getMethod);

			String result = getMethod.getResponseBodyAsString();
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void setHeaders(HttpMethodBase baseMethod){
		baseMethod.setRequestHeader("Cookie", ".CNBlogsCookie=269F48C4ED4A21A49BC498E2B955B50DA762E11028347A85328F80C7640D1E00040FC44F89FDC0CB14F470772B45673B9B352E2DA4F2C9BFDA47C79D41BC2A67FF88DBEE18BE58AF5998FB63A47941E59FAF4376; .Cnblogs.AspNetCore.Cookies=CfDJ8FHXRRtkJWRFtU30nh_M9mAA7YG4NUk3bN74PAwfEGpOVIyVydEwrgZ59V_hHT0M3S_dN0g0kwE2puN41GJDuors47TximK21DvY9brG14z9tq1ZERfqiL22Zg5v54UuWt1VDmczfjXsRHnbNUHSqLhNbq3CZCc4rLhS1IDgqoDiflo8ETRhBERc1DmfQuw3vlg2aYcLwQkC-efqN8fotVeASy-qkG-4S1pgLR6PH_ehvC1Ha6hOGYzXZOEGbIV1144ZPLWVFXD0yeFpF4wqHXTzIWlU1NXqnk7AYUmhkhfV");
		baseMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
//		baseMethod.setRequestHeader("Referer", "https://passport.cnblogs.com/user/signin");
		baseMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
	}
	
	

}
