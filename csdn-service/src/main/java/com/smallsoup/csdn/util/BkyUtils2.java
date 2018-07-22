package com.smallsoup.csdn.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

public class BkyUtils2 {

	private static final Logger LOGGER = Logger.getLogger(BkyUtils2.class);

	private static HttpClient httpClient = new HttpClient();
	private static final String BOLG_APP_REG = "^https://www.cnblogs.com/(.*)/archive/\\d{4}/\\d{2}/\\d{2}/.*.html(#\\d)?$";

	/**
	 * 因为是多行匹配所以,这里不能加^和$
	 */
	private static final String POSTID_REG = ".*postid=(\\d+)\".*";
	private static final String NEED_LOGIN = "请先登录！";
	private static final String TOO_FREQUENTLY = "发布失败！您发评论太频繁啦！";

	private static final String USERNAME = "smallsoup1";
	private static final String PASSWORD = "ll19940109,,..";
	private static final String LOGINURL = "https://passport.cnblogs.com/user/signin";
	private static final String TESTURL = "https://home.cnblogs.com/u/whatbeg/followers/";
	private static final String COMMIT_BODY = "加Wei信smallsoup1免费领取java、python、前端、安卓、数据库、大数据、IOS等学习资料";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	static {

		// 同时输出到控制台和一个文件的实例
		/* 用log4包加载配置文件 */
		// PropertyConfigurator.configure("\\log4j.properties");
		/* 用java自带peoperties加载配置文件 */
		Properties props = new Properties();
		try {
			System.out.println("加载配置文件");
			props.load(BkyUtils2.class.getClassLoader().getResourceAsStream("log4j.properties"));
			String fileLocation = (String) props.get("log4j.appender.root.File");
			System.out.println(fileLocation);

			// String format = SDF.format(new Date());
			// props.setProperty("log4j.appender.root.File", fileLocation +
			// "root_" + format + ".log");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("主函数调用debug");
	}

	public static void main(String[] args) throws Exception {

		LOGGER.info("---------------main begin-------------------------");

		List<String> blogUrlLists = getBlogUrlLists();

		Map<String, PostMethod> postCommitBatchMap = new HashMap<>();

		for (String url : blogUrlLists) {

			GetMethod getMethod = new GetMethod(url);

			httpClient.executeMethod(getMethod);

			String html = getMethod.getResponseBodyAsString();

			collectEveryCatagorys(url, html, postCommitBatchMap);

		}

		// 先登录
		HttpClientLogin(USERNAME, PASSWORD, LOGINURL, TESTURL);

		// 然后评论
		postCommitBatch(postCommitBatchMap);

	}

	private static void collectEveryCatagorys(String url, String html, Map<String, PostMethod> postCommitBatchMap)
			throws HttpException, IOException {

		collect(html, postCommitBatchMap);

		String reg = "^.*cate/(\\d+)/.*$";
		String categoryId = "";

		Pattern p = Pattern.compile(reg);

		Matcher m = p.matcher(url);

		while (m.find()) {
			categoryId = m.group(1);
		}

		if ("".equals(categoryId)) {
			return;
		}

		String postUrl = "https://www.cnblogs.com/mvc/AggSite/PostList.aspx";

		for (int i = 2; i < 201; i++) {

			PostMethod postMethod = new PostMethod(postUrl);
			
			NameValuePair[] postData = { new NameValuePair("CategoryId", categoryId),
					new NameValuePair("CategoryType", "TopSiteCategory"),
					new NameValuePair("ItemListActionName", "PostList"), new NameValuePair("PageIndex", i + ""),
					new NameValuePair("ParentCategoryId", 0 + ""), new NameValuePair("TotalPostCount", 4000 + "") };

			postMethod.setRequestBody(postData);

			httpClient.executeMethod(postMethod);

			String responseBody = postMethod.getResponseBodyAsString();
			
			collect(responseBody, postCommitBatchMap);
		}
	}
	
	private static void collect(String html, Map<String, PostMethod> postCommitBatchMap) throws HttpException, IOException{
		Document doc = Jsoup.parse(html);

		Elements as = doc.select(".post_item_body").select(".titlelnk");

		for (Element a : as) {

			String blogUrl = a.attr("href");
			System.out.println(blogUrl);

			// 获取blogApp
			String blogApp = getBlogApp(blogUrl);

			// 获取postId
			GetMethod getMethod = new GetMethod(blogUrl);
			httpClient.executeMethod(getMethod);

			String postHtml = getMethod.getResponseBodyAsString();

			// 解析postId
			String postId = getPostId(postHtml);

			if (StringUtils.isEmpty(postId) || StringUtils.isEmpty(blogApp)) {
				continue;
			}

			PostMethod postMethod = new PostMethod("https://www.cnblogs.com/mvc/PostComment/Add.aspx");

			NameValuePair[] postData = { new NameValuePair("blogApp", blogApp), new NameValuePair("postId", postId),
					new NameValuePair("body", COMMIT_BODY), new NameValuePair("parentCommentId", "0") };
			postMethod.setRequestBody(postData);

			postCommitBatchMap.put(blogUrl, postMethod);
		}
	}

	/**
	 * 根据收集好的url发起评论请求
	 * 
	 * @param postCommitBatchMap
	 * @throws Exception
	 */
	private static void postCommitBatch(Map<String, PostMethod> postCommitBatchMap) throws Exception {

		Iterator<Map.Entry<String, PostMethod>> it = postCommitBatchMap.entrySet().iterator();

		// 评论成功计数器
		int count = 0;
		try {
			// 6次成功休息2分后再评论,否则提示频繁
			int successCount = 0;
			while (it.hasNext()) {

				if (successCount >= 6) {
					Thread.currentThread().sleep(2 * 60 * 1000);
					successCount = 0;
				}

				Map.Entry<String, PostMethod> entry = (Map.Entry<String, PostMethod>) it.next();

				String blogUrl = (String) entry.getKey();

				PostMethod postMethod = (PostMethod) entry.getValue();
				// 不加这句中文会乱码
				postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
				setHeaders(postMethod);
				httpClient.executeMethod(postMethod);

				String responseBody = postMethod.getResponseBodyAsString();

				System.out.println("--------responseBody--------------" + responseBody);
				JSONObject jsonObj = JSONObject.parseObject(responseBody);
				// {"Id":0,"IsSuccess":false,"Message":"请先登录！","Data":null}
				// 评论结果,成功为1
				if (jsonObj.getBooleanValue("IsSuccess")) {
					successCount++;
					// 将评论成功返回体
					LOGGER.info(responseBody);
					LOGGER.info(blogUrl);
					System.out.println("success articalUrl is " + blogUrl);
					// 将评论成功的url记录到文件
					count++;
				} else {
					// 把错误记录到文件
					LOGGER.error(responseBody);
					LOGGER.error(blogUrl);
					// 不成功说明请求太快,线程休眠2秒,这里会丢掉评论失败的文章
					if (NEED_LOGIN.equals(jsonObj.getString("Message"))) {
						HttpClientLogin(USERNAME, PASSWORD, LOGINURL, TESTURL);
						continue;
					} else if (TOO_FREQUENTLY.equals(jsonObj.getString("Message"))) { // 太频繁
						Thread.currentThread().sleep(2 * 60 * 1000);
					} else { // 其他错误
						// 把错误记录到文件
						LOGGER.error("other error is" + responseBody);
						throw new Exception("other error is " + responseBody);
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error("Exception is {}", e);
			System.out.println("error is " + e);
		} finally {
			// 把成功的送书记录到文件
			LOGGER.error("successCount is " + count);
			System.out.println("bos will colse");
		}
	}

	private static String getBlogApp(String url) {

		Pattern p = Pattern.compile(BOLG_APP_REG);

		Matcher m = p.matcher(url);

		while (m.find()) {
			System.out.println("--------BlogApp------" + m.group()); // 打印所有

			return m.group(1);
		}

		return null;
	}

	/**
	 * 解析评论所必须参数postId
	 * 
	 * @param html
	 * @return postId
	 */
	private static String getPostId(String html) {

		// 因为是多行匹配所以,这里不能加^和$
		Pattern p = Pattern.compile(POSTID_REG);
		Matcher m = p.matcher(html);

		while (m.find()) {
			System.out.println("m.group():" + m.group()); // 打印所有

			System.out.println("-----postID-----" + m.group(1));
			return m.group(1);

		}
		return null;
	}

	/**
	 * 收集博客分类url,即https://www.cnblogs.com/网址左边侧栏"网站分类"
	 * 
	 * @return 收集好的分类对应的url集合
	 * @throws HttpException
	 * @throws IOException
	 */
	private static List<String> getBlogUrlLists() throws HttpException, IOException {

		String url = "https://www.cnblogs.com/";

		GetMethod getMethod = new GetMethod(url);
		httpClient.executeMethod(getMethod);

		String html = getMethod.getResponseBodyAsString();

		// System.out.println(html);

		Document doc = Jsoup.parse(html);

		Elements as = doc.select("#cate_item").select("a");

		List<String> urlList = Lists.newArrayList();
		for (Element a : as) {
			urlList.add("https://www.cnblogs.com" + a.attr("href"));
			System.out.println(a.text() + " -----collect------ " + a.attr("href"));
		}

		System.out.println("==========" + urlList + "=============");

		return urlList;
	}

	/**
	 * 登录以及访问测试页面
	 * 
	 * @param userName
	 *            博客园用户名
	 * @param password
	 *            博客园密码
	 * @param loginUrl
	 *            登录url
	 * @param dataUrl
	 *            测试url
	 */
	private static void HttpClientLogin(String userName, String password, String loginUrl, String dataUrl) {
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		PostMethod postMethod = new PostMethod(loginUrl);

		NameValuePair[] postData = {
				new NameValuePair("input1",
						"hFqPVbcHLuxeGsSQz9q1hjAIVJX43A5Iww8feVLAkNwrCRZenIZjhF9xDFUP29xKzHfkmT/26BrSb0e/pwUO6qRZRrGI5XFIHfBs2P4qBRCEufpfiQhU5GbjRNapqxkuB7MKEc4nWc9kdYhkhbumzDrC29V0/CSlrjbozvGCX4M="),
				new NameValuePair("input2",
						"Un2r1Zk5EvhpxSNAkba5k11E1NwtfVAHFk10snmpxmdqNIaUGypGHd4JK9bY6AYNi+//KmIAk1WmLvbC1TF/gF4Vzt3zkUPNQiORQKR4rSztyqnL4nxYpx6zvhTi61+MA8aAC4iWTZBQwBt7bDKzTpNfp3e1XepcCEPpyEm7l3s="),
				new NameValuePair("geetest_validdate", "4c0406551b06e40fedab2225838972d8"),
				new NameValuePair("geetest_seccode", "4c0406551b06e40fedab2225838972d8|jordan"),
				new NameValuePair("geetest_challenge", "82ba831f9363fc8fb5beb7fdeaadcdaeap"),
				new NameValuePair("remember", "True") };
		postMethod.setRequestBody(postData);

		try {
			setHeaders(postMethod);
			httpClient.executeMethod(postMethod);

			System.out.println("-------------------------------");
			System.out.println("---" + postMethod.getResponseBodyAsString());

			GetMethod getMethod = new GetMethod(dataUrl);
			setHeaders(getMethod);
			httpClient.executeMethod(getMethod);

			String result = getMethod.getResponseBodyAsString();
			// System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置请求头
	 */
	private static void setHeaders(HttpMethodBase baseMethod) {

		// https://home.cnblogs.com/的cookie
		baseMethod.setRequestHeader("Cookie",
				".CNBlogsCookie=E69DF3825FB6BEA3525669F7C196E0480461C641B3301AFD60EED443B84EB74D6721C68210B874919C300134C6BD0458781CE9C71FFC54A4D8FDFC165563BD540D3CD0CBE4A33D703067F772671AE2CD4ECCD39A; .Cnblogs.AspNetCore.Cookies=CfDJ8FHXRRtkJWRFtU30nh_M9mB-Bug7NYG5Yc3W0s4ebRSRAJd-_W1pcdIp7yErf64H1NPcK3I2tMmtCrMJDlH5Uvkem6cFzhM1iYnGkLoZCVBXRty7vGuLjKmMS1GdVBbPo90y0yBm2HRcNI5yvZDkHcPb7ayhfJSQ9mqBoL8S1U9zFuvvOqJ1LxhS0CJ2phC0aDlDngWNoYK29stQOifr7hIzm006R0kNCAa0UjJJOMId3Xh5nW7X3IB7h8x81dswei5oYF3k3AxTX7TkO8H2L0EacbJ78Ughe22OTgO7mn0dI01L1qsWP9K-keBZdGfJ3A");

		baseMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		// baseMethod.setRequestHeader("Referer",
		// "https://passport.cnblogs.com/user/signin");
		baseMethod.setRequestHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
	}

}
