package com.smallsoup.csdn.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.smallsoup.csdn.ui.dto.OrderInfo;
import com.smallsoup.csdn.util.HttpUtils;
import com.smallsoup.csdn.util.UnicodeUtil;

@Component
public class CSDNFileDownloadService {
	static boolean result = false;

	private static String PATH = CSDNFileDownloadService.class.getClassLoader().getResource("").getPath();

	private static final String downloadFilePath = PATH + "/TaoBao/";

	private String orderFromNum = "105694263933097546";
	private String baseUrl = "http://download.csdn.net/download/cspe_703/10140021";
	private String receiveMailAccount = "253960307@qq.com";
	private static final String csdnAccount = "hekunpen4788";
	private static final String csdnPassword = "ll837448792ll";

	public File loginCsdnAndDownload(OrderInfo orderInfo) throws ClientProtocolException, IOException {
		File file = null;
//		loginCsdnPager();
		this.orderFromNum = orderInfo.getOrderNum();
		file = loginedPager(orderInfo.getLinkAddr());

		return file;
	}

	/**
	 * 鐧诲綍椤甸潰
	 */
	public void loginCsdnPager() {
		String html = HttpUtils.sendGet("https://passport.csdn.net/account/login?ref=toolbar");// 杩欎釜鏄櫥褰曠殑椤甸潰
		Document doc = Jsoup.parse(html);
		Element form = doc.select(".user-pass").get(0);
		String lt = form.select("input[name=lt]").get(0).val();
		String execution = form.select("input[name=execution]").get(0).val();
		String _eventId = form.select("input[name=_eventId]").get(0).val();

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", csdnAccount));
		nvps.add(new BasicNameValuePair("password", csdnPassword));
		nvps.add(new BasicNameValuePair("lt", lt));
		nvps.add(new BasicNameValuePair("execution", execution));
		nvps.add(new BasicNameValuePair("_eventId", _eventId));
		String ret = HttpUtils.sendPost("https://passport.csdn.net/account/login?ref=toolbar", nvps);

		System.out.println("ret is " + ret);
		// ret涓細鍖呭惈浠ヤ笅淇℃伅锛岃繘琛屽垽鏂嵆鍙��
		if (ret.indexOf("redirect_back") > -1) {
			System.out.println("鐧婚檰鎴愬姛銆傘�傘�傘�傘��");
			result = true;
		} else if (ret.indexOf("鐧诲綍澶绻�") > -1) {
			System.out.println("鐧诲綍澶绻侊紝璇风◢鍚庡啀璇曘�傘�傘�傘�傘��");
			return;
		} else {
			System.out.println("鐧婚檰澶辫触銆傘�傘�傘�傘��");
			return;
		}
	}

	/**
	 * 鐧婚檰鍚庣殑椤甸潰鑾峰彇锛屽叏灞�鍙互鐢紝姣斿http://my.csdn.net/wgyscsf涔熸槸鍙互鐢ㄧ殑銆�
	 */
	private File loginedPager(String baseUrl) throws IOException, ClientProtocolException {
		// 鏋勯�犻渶瑕佽闂殑椤甸潰
		HttpUriRequest httpUriRequest = new HttpPost(baseUrl);
		// 娣诲姞蹇呰鐨勫ご淇℃伅
		httpUriRequest.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpUriRequest.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		httpUriRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpUriRequest.setHeader("Connection", "keep-alive");
		// 妯℃嫙娴忚鍣紝鍚﹀垯CSDN鏈嶅姟鍣ㄩ檺鍒惰闂�
		httpUriRequest.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
		// 銆愮壒鍒敞鎰忋�戯細杩欎釜涓�瀹氶渶瑕佸拰鐧诲綍鐢ㄥ悓涓�涓�渉ttpClient鈥濓紝涓嶇劧浼氬け璐ャ�傜櫥闄嗕俊鎭叏閮ㄥ湪鈥渉ttpClient鈥濅腑淇濆瓨
		HttpResponse response = HttpUtils.httpClient.execute(httpUriRequest);

		System.out.println("response is " + JSONObject.toJSONString(response));

		InputStream content = response.getEntity().getContent();
		// 灏唅nputstream杞寲涓簉eader锛屽苟浣跨敤缂撳啿璇诲彇锛岃繕鍙寜琛岃鍙栧唴瀹�
		BufferedReader br = new BufferedReader(new InputStreamReader(content));
		String line = "";
		String result = "";
		while ((line = br.readLine()) != null) {
			result += line;
		}
		br.close();

		// 杩欓噷鏄幏鍙栫殑椤电爜锛屽氨鍙互杩涜鐣岄潰瑙ｆ瀽澶勭悊浜嗐��
		System.out.println("result is 111 " + result);

		if (needExecLoginCsdnPagerAgain(result)) {
			String baseurl = Jsoup.parse(result).getElementsByAttributeValue("ref", "canonical").attr("href");
			loginedPager(baseurl);
			return null;
		} else {
			File file = downloadFile(result);
			return file;
		}
	}

	private boolean needExecLoginCsdnPagerAgain(String html) {
		System.out.println("needExecLoginCsdnPagerAgain....");

		if (null == Jsoup.parse(html).getElementById("vip_btn")) {
			return true;
		}
		return false;
	}

	private File downloadFile(String result) throws ClientProtocolException, IOException {

		String[] urlAndTitle = getVipBtnHrefUrl(result);

		// 鏋勯�犻渶瑕佽闂殑椤甸潰
		HttpUriRequest httpUriRequest = new HttpPost(urlAndTitle[0]);

		CloseableHttpResponse response = HttpUtils.httpClient.execute(httpUriRequest);
		HttpEntity entity = response.getEntity();
		// 杩欓噷鏄幏鍙栫殑椤电爜锛屽氨鍙互杩涜鐣岄潰瑙ｆ瀽澶勭悊浜嗐��
		System.out.println("response is " + response);

		System.out.println("response json is " + JSONObject.toJSONString(response));

		// 鑾峰彇涓嬭浇閾炬帴
		Header[] allHeaders = response.getHeaders("Location");

		// 娌℃湁鑾峰彇鍒颁笅杞介摼鎺ュ垯锛岄��鍑�
		if (ArrayUtils.isEmpty(allHeaders)) {

			InputStream content = response.getEntity().getContent();
			// 灏唅nputstream杞寲涓簉eader锛屽苟浣跨敤缂撳啿璇诲彇锛岃繕鍙寜琛岃鍙栧唴瀹�
			BufferedReader br = new BufferedReader(new InputStreamReader(content));
			String line = "";
			String result1 = "";
			while ((line = br.readLine()) != null) {
				result1 += line;
			}

			System.out.println("result1 is " + result1);
			br.close();
			return null;
		}

		System.out.println("value is " + allHeaders[0].getValue());
		HttpUriRequest httpUriRequest1 = new HttpGet(allHeaders[0].getValue());
		// httpUriRequest1.setHeader("Content-type", "UTF-8");

		response = HttpUtils.httpClient.execute(httpUriRequest1);
		response.setHeader("Content-Type", "application/json");
		System.out.println("response1 is " + response);
		// System.out.println("response1 json is " +
		// JSONObject.toJSONString(response1));
		HttpEntity entity1 = response.getEntity();
		String filename = null;

		// 鑾峰彇鏂囦欢鍚�
		Header[] headers = response.getHeaders("Content-Disposition");
		System.out.println(headers[0].getElements()[0].getParameters()[0].getName());
		filename = headers[0].getElements()[0].getParameters()[0].getValue();
		System.out.println("Before filename is " + filename);
		filename = UnicodeUtil.ascii2native(filename);
		System.out.println("After filename is " + filename);

		// long contentLength = entity1.getContentLength();
		InputStream is = entity1.getContent();
		// 鏍规嵁InputStream 涓嬭浇鏂囦欢
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int r = 0;
		long totalRead = 0;
		while ((r = is.read(buffer)) > 0) {
			output.write(buffer, 0, r);
			totalRead += r;
			/*
			 * if (progress != null) {// 鍥炶皟杩涘害 progress.onProgress((int)
			 * (totalRead * 100 / contentLength)); }
			 */
		}

		System.err.println("downloadFilePath is " + downloadFilePath);
		File file = createDirAndFile(filename, urlAndTitle[1]);
		// 鍦ㄥ唴瀛樹腑鍒涘缓涓�涓枃浠跺璞★紝娉ㄦ剰锛氭鏃惰繕娌℃湁鍦ㄧ‖鐩樺搴旂洰褰曚笅鍒涘缓瀹炲疄鍦ㄥ湪鐨勬枃浠�

		FileOutputStream fos = new FileOutputStream(file);
		output.writeTo(fos);
		output.flush();
		output.close();
		fos.close();
		EntityUtils.consume(entity);
		System.out.println("鏂囦欢鍚嶄负: " + file.getName());
		System.out.println("鏂囦欢澶у皬涓�: " + GetFileSize(file.length()));
		try {
			// sendEmailService.sendEmail(new Email(toList, subject, file));
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return file;

	}

	private String[] getVipBtnHrefUrl(String html) throws ClientProtocolException, IOException {
		System.out.println("getVipBtnHrefUrl....");
		String[] urlAndTitle = new String[2];
		Document doc = Jsoup.parse(html);

		Element aLabel = doc.getElementById("vip_btn");

		String url = aLabel.attr("href");

		String title = doc.getElementsByTag("title").text();

		urlAndTitle[0] = url;
		urlAndTitle[1] = title;
		System.out.println("url is " + url);
		System.out.println("title is " + title);
		return urlAndTitle;
	}

	private File createDirAndFile(String filename, String title) {
		String dir = downloadFilePath + orderFromNum + "鈥斺��" + title;

		// 鍦ㄥ唴瀛樹腑鍒涘缓涓�涓枃浠跺璞★紝娉ㄦ剰锛氭鏃惰繕娌℃湁鍦ㄧ‖鐩樺搴旂洰褰曚笅鍒涘缓瀹炲疄鍦ㄥ湪鐨勬枃浠�
		File f = new File(dir, filename);
		if (f.exists()) {
			// 鏂囦欢宸茬粡瀛樺湪锛岃緭鍑烘枃浠剁殑鐩稿叧淇℃伅
			System.out.println("鏂囦欢宸茬粡瀛樺湪  and absolutePath is " + f.getAbsolutePath());
			System.out.println("鏂囦欢鍚嶄负: " + f.getName());
			System.out.println("鏂囦欢澶у皬涓�: " + GetFileSize(f.length()));
		} else {
			// 鍏堝垱寤烘枃浠舵墍鍦ㄧ殑鐩綍
			f.getParentFile().mkdirs();
			try {
				// 鍒涘缓鏂版枃浠�
				f.createNewFile();

				System.out.println("create success and absolutePath is " + f.getAbsolutePath());
			} catch (IOException e) {
				System.out.println("鍒涘缓鏂版枃浠舵椂鍑虹幇浜嗛敊璇�傘�傘��");
				e.printStackTrace();
			}
		}

		return f;
	}

	private String GetFileSize(long fileS) {
		String size = "";
		DecimalFormat df = new DecimalFormat("#.00");
		if (fileS < 1024) {
			size = df.format((double) fileS) + "BT";
		} else if (fileS < 1048576) {
			size = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			size = df.format((double) fileS / 1048576) + "MB";
		} else {
			size = df.format((double) fileS / 1073741824) + "GB";
		}
		return size;
	}

}
