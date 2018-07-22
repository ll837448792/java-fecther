package com.smallsoup.csdn.util;

public class UnicodeUtil {
	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);

			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}

		return unicode.toString();
	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {

		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {

			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}

	public static String ascii2native(String asciicode) {
		String[] asciis = asciicode.split("\\\\u");
		String nativeValue = asciis[0];
		try {
			for (int i = 1; i < asciis.length; i++) {
				String code = asciis[i];
				nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
				if (code.length() > 4) {
					nativeValue += code.substring(4, code.length());
				}
			}
		} catch (NumberFormatException e) {
			return asciicode;
		}
		return nativeValue;
	}

	public static void main(String[] args) {
		String test = "最代码网站地址:www.zuidaima.com";

		String unicode = string2Unicode(test);

		String string = unicode2String(unicode);

		System.out.println(unicode);

		System.out.println(string);

		String test1 = "全国交通咨询模拟+课程设计.rar";
		String test2 = "\\u5168\\u56fd\\u4ea4\\u901a\\u54a8\\u8be2\\u6a21\\u62df\\u2b\\u8bfe\\u7a0b\\u8bbe\\u8ba1.rar";

		System.out.println(unicode2String(test2));

	}
}
