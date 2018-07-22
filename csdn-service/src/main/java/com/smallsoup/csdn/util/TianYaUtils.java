package com.smallsoup.csdn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class TianYaUtils {
	private static final String TIANYA_ACCOUNT = "ty_殇183";
	private static final String TIANYA_PASSWORD = "ll19940109,,..";
	private static final String NEED_LOGIN = "需要登录后操作";

	private static final List<String> FETCHPAGES = Arrays.asList(new String[] { "编程语言", "计算机基础", "运维", "物联网", "移动开发",
			"前端", "游戏开发", "程序人生", "数据库", "区块链", "云计算/大数据", "人工智能" });

	public static void main(String[] args) throws Exception {
		enterBlogPage();
	}

	/**
	 * 获取文章url所在的a标签,以及打来文章发起评论
	 * @throws Exception
	 */
	private static void enterBlogPage() throws Exception {


		// 收集完a标签后再登陆,否则会丢掉很多a标签,具体原因不名
		loginTianYaPager();

	}
	/**
	 * 登录tianya页面,评论当然需要登录了
	 * 
	 * @throws Exception
	 */
	public static void loginTianYaPager() throws Exception {
		

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("vwriter", TIANYA_ACCOUNT));
		nvps.add(new BasicNameValuePair("vpassword", TIANYA_PASSWORD));
		nvps.add(new BasicNameValuePair("action", "f11.1532274564847.14508,,,,d1.undefined.5,u2.undefined.0,b3.7.1,b6.8.1704,d8.undefined.796,u9.undefined.1,b10.11.1714,b12.12.13|cf0c052a301d0df99a22a05d66770bbf|6f8f57715090da2632453988d9a1501b|Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36|4|9|v2.2"));
		nvps.add(new BasicNameValuePair("rmflag", "1"));
		nvps.add(new BasicNameValuePair("__sid", "1#3#1.0#b646954d-0be4-425b-b431-45c262da710f"));
		nvps.add(new BasicNameValuePair("returnURL", "http://bbs.tianya.cn/#loginAction"));
		nvps.add(new BasicNameValuePair("fowardURL", "http://bbs.tianya.cn/"));
//		nvps.add(new BasicNameValuePair("lt", lt));
//		nvps.add(new BasicNameValuePair("execution", execution));
//		nvps.add(new BasicNameValuePair("_eventId", _eventId));
		
		String html = HttpUtils.sendPost("https://passport.tianya.cn/login", nvps);

		
		System.out.println(html);
//		Document doc = Jsoup.parse(html);

	}
}
