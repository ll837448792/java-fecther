package com.smallsoup.csdn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

public class FileUtils {

	public static void main(String[] args) throws IOException {

		String responseBody = "{\"Id\":0,\"IsSuccess\":true,\"Message\":\"请先登录！\",\"Data\":null}";
		JSONObject jsonObj = JSONObject.parseObject(responseBody);
		// {"Id":0,"IsSuccess":false,"Message":"请先登录！","Data":null}
		// 评论结果,成功为1
		if (jsonObj.getBooleanValue("IsSuccess")) {
			System.out.println("---------------SUCCESS---");
		} else {
			System.out.println("---------------FIAL---");
		}

	}

	public static void testReadFile() throws IOException {
		// String html = "<span id=\"post_comment_count\">...</span>) "
		// + "<a href =\"https://i.cnblogs.com/Edi"
		// + "tPosts.aspx?postid=9348074\" rel=\"nofollow\">编辑</a> <a href=\"#\"
		// onclick=\"AddToWz(9348074);return false;\">收藏</a></div>";

		File file = new File("D:/tmp/successLog/1.html");

		FileInputStream fis = new FileInputStream(file);

		Long filelength = file.length(); // 获取文件长度
		byte[] filecontent = new byte[filelength.intValue()];
		int temp = 0;
		while ((temp = fis.read(filecontent)) != -1) {

		}
		String fileContentArr = new String(filecontent);
		System.out.println(fileContentArr);
		Pattern p = Pattern.compile(".*postid=(\\d+)\".*");
		System.out.println("------------begin---------------");
		System.out.println("");
		Matcher m = p.matcher(fileContentArr);

		while (m.find()) {
			System.out.println("m.group():" + m.group()); // 打印所有

			System.out.println("-----postID-----" + m.group(1));

		}

		System.out.println("------------end---------------");
	}

}
