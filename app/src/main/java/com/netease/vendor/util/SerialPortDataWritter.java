package com.netease.vendor.util;

import com.netease.vendor.serialport.SerialPortInstance;
import com.netease.vendor.util.log.LogUtil;

public class SerialPortDataWritter {

	private static byte[] writeBuffer = new byte[64];;
	private static int numBytes = 0;
	
	public static void writeData(String order) {
		LogUtil.e("write instruction", order);
		String srcStr = order;
		String destStr = "";
		String[] tmpStr = srcStr.split(" ");

		for (int i = 0; i < tmpStr.length; i++) {
			if (tmpStr[i].length() == 0) {
				return;
			} else if (tmpStr[i].length() != 2) {
				return;
			}
		}

		try {
			destStr = hexToAscii(srcStr.replaceAll(" ", ""));
		} catch (IllegalArgumentException e) {
			return;
		}

		numBytes = destStr.length();
		for (int i = 0; i < numBytes; i++) {
			writeBuffer[i] = (byte) destStr.charAt(i);
		}
		try {
			SerialPortInstance.getInstance().getOutputStream().write(writeBuffer, 0, numBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String hexToAscii(String s) {
		int n = s.length();
		StringBuilder sb = new StringBuilder(n / 2);
		for (int i = 0; i < n; i += 2) {
			char a = s.charAt(i);
			char b = s.charAt(i + 1);
			sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
		}
		return sb.toString();
	}
	
	private static int hexToInt(char ch) {
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		return 0;
	}
}
