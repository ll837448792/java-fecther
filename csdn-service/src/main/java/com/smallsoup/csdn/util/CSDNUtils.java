package com.smallsoup.csdn.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

public class CSDNUtils {

	private static final String CSDNACCOUNT = "hekunpen4788";
	private static final String CSDNPASSWORD = "ll19940109,,..";
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

		String html = HttpUtils.sendGet("https://blog.csdn.net/");

		Document doc = Jsoup.parse(html);
		Elements as = doc.select(".nav_com").select("li").select("a");

		// 收集文章a标签
		List<Elements> blogList = Lists.newArrayListWithCapacity(as.size());
		for (Element a : as) {

			if (!FETCHPAGES.contains(a.text())) {
				continue;
			}

			String fetcheUrl = "https://blog.csdn.net" + a.attr("href");
			System.out.println(fetcheUrl);
			String blogHtml = HttpUtils.sendGet(fetcheUrl);

			Document blogDoc = Jsoup.parse(blogHtml);

			Elements blogAs = blogDoc.select(".title").select("h2").select("a");

			System.out.println(blogAs);
			blogList.add(blogAs);
		}

		// 收集完a标签后再登陆,否则会丢掉很多a标签,具体原因不名
		loginCsdnPager();

		BufferedOutputStream bos = null;
		// 评论成功计数器
		int count = 0;
		try {
			// 将评论成功的url打印到文件里
			File file = new File("D:/tmp/successLog/success.log");
			bos = new BufferedOutputStream(new FileOutputStream(file));
			// 爬取所有a标签
			for (Elements blogs : blogList) {

				for (Element blog : blogs) {

					// 拿到文章url
					String href = blog.attr("href");

					// 获取文章url后的ID,在评论时需要用到
					String commitSuffixUrl = href.substring(href.lastIndexOf("/") + 1);

					// 打开文章
					String blogHtml = HttpUtils.sendGet(href);
					System.out.println(blog.text() + "------------" + blog.attr("href"));

					Document blogDoc = Jsoup.parse(blogHtml);
					Elements titleAs = blogDoc.select(".title-box").select("a");

					System.out.println(titleAs);

					if (titleAs != null && !titleAs.isEmpty()) {
						// 评论请求url前缀
						String commitPrefixUrl = titleAs.get(0).attr("href");
						//
						System.out.println(titleAs.text() + "-----------" + commitPrefixUrl);

						// 拼接评论请求url
						String commitUrl = commitPrefixUrl + "/phoenix/comment/submit?id=" + commitSuffixUrl;

						System.out.println("commitUrl ==" + commitUrl);

						// 构造评论请求所需body体
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("replyId", ""));
						nvps.add(new BasicNameValuePair("content",
								"加Wei信smallsoup1免费领取java、python、前端、安卓、数据库、大数据、IOS等学习资料"));

						// 发起评论
						String postRequest = HttpUtils.sendPost(commitUrl, nvps);
						JSONObject jsonObj = JSONObject.parseObject(postRequest);

						System.out.println(postRequest);

						// 评论结果,成功为1
						if (jsonObj.getInteger("result") == 1) {

							String articalUrl = commitPrefixUrl + "/article/details/" + commitSuffixUrl + "\n";
							System.out.println("success articalUrl is " + articalUrl);
							// 将评论成功的url记录到文件
							bos.write(articalUrl.getBytes());
							bos.flush();
							count++;
						} else {
							// 不成功说明请求太快,线程休眠2秒,这里会丢掉评论失败的文章
							if (NEED_LOGIN.equals(UnicodeUtil.unicode2String(jsonObj.getString("content")))) {
								loginCsdnPager();
								continue;
							}else{
								Thread.currentThread().sleep(2 * 60 * 1000);
							}
						}
					} else {
						continue;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("error is " + e);
		} finally {

			if (bos != null) {
				try {
					// 把成功的送书记录到文件
					bos.write((count + "\n").getBytes());
					bos.flush();
					System.out.println("bos will colse");
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("error is " + e);
				}
			}
		}
	}

	/**
	 * 登录csdn页面,评论当然需要登录了
	 * 
	 * @throws Exception
	 */
	public static void loginCsdnPager() throws Exception {
		String html = HttpUtils.sendGet("https://passport.csdn.net/account/login?ref=toolbar");

		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(html);

		Element form = doc.select(".user-pass").get(0);
		String lt = form.select("input[name=lt]").get(0).val();
		String execution = form.select("input[name=execution]").get(0).val();
		String _eventId = form.select("input[name=_eventId]").get(0).val();

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", CSDNACCOUNT));
		nvps.add(new BasicNameValuePair("password", CSDNPASSWORD));
		nvps.add(new BasicNameValuePair("lt", lt));
		nvps.add(new BasicNameValuePair("execution", execution));
		nvps.add(new BasicNameValuePair("_eventId", _eventId));

		System.out.println(nvps);
		// 开始请求CSDN服务器进行登录操作。一个简单封装，直接获取返回结果
		String ret = HttpUtils.sendPost("https://passport.csdn.net/account/login", nvps);

		System.out.println("ret is " + ret);
		// ret中会包含以下信息，进行判断即可。
		if (ret.indexOf("redirect_back") > -1) {
			System.out.println("登陆成功。。。。。");
		} else if (ret.indexOf("登录太频繁") > -1) {
			throw new Exception("登录太频繁，请稍后再试。。。。。");
		} else {
			throw new Exception("登录失败。。。。。");
		}
	}
}
