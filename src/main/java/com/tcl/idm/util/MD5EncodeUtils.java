package com.tcl.idm.util;

import java.security.MessageDigest;

/**
 * MD5算法类
 * 
 * @author yuanhuan
 * 2014年3月28日 上午11:43:29
 */
public class MD5EncodeUtils
{
	private static String byteArrayToHex(byte[] bytes)
	{
		// 字符数组，用来存放十六进制字符
		char[] hexReferChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		// 一个字节占8位，一个十六进制字符占4位；十六进制字符数组的长度为字节数组长度的两倍
		char[] hexChars = new char[bytes.length * 2];
		int index = 0;
		for (byte b : bytes)
		{
			// 取字节的高4位
			hexChars[index++] = hexReferChars[b >>> 4 & 0xf];
			// 取字节的低4位
			hexChars[index++] = hexReferChars[b & 0xf];
		}
		return new String(hexChars);
	}

	public static String encodeStrByMd5(String str)
	{
		String md5Str = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 使用指定byte[]更新摘要
			md.update(str.getBytes());
			// 完成计算，返回结果数组
			byte[] b = md.digest();
			md5Str = MD5EncodeUtils.byteArrayToHex(b);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return md5Str;
	}
}
