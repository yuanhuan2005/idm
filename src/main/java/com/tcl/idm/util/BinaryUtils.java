package com.tcl.idm.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BinaryUtils
{
	private static final Log DEBUGGER = LogFactory.getLog(BinaryUtils.class);

	public static String toHex(byte[] data)
	{
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (int i = 0; i < data.length; i++)
		{
			String hex = Integer.toHexString(data[i]);
			if (hex.length() == 1)
			{
				sb.append("0");
			}
			else if (hex.length() == 8)
			{
				hex = hex.substring(6);
			}
			sb.append(hex);
		}
		return sb.toString().toLowerCase(Locale.getDefault());
	}

	public static byte[] fromHex(String hexData)
	{
		byte[] result = new byte[(hexData.length() + 1) / 2];
		String hexNumber = null;
		int stringOffset = 0;
		int byteOffset = 0;
		while (stringOffset < hexData.length())
		{
			hexNumber = hexData.substring(stringOffset, stringOffset + 2);
			stringOffset += 2;
			result[(byteOffset++)] = ((byte) Integer.parseInt(hexNumber, 16));
		}
		return result;
	}

	public static String toBase64(byte[] data)
	{
		byte[] b64 = Base64.encodeBase64(data);
		return new String(b64);
	}

	public static byte[] fromBase64(String b64Data)
	{
		byte[] decoded;
		try
		{
			decoded = Base64.decodeBase64(b64Data.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException uee)
		{
			BinaryUtils.DEBUGGER.warn("Tried to Base64-decode a String with the wrong encoding: ", uee);
			decoded = Base64.decodeBase64(b64Data.getBytes());
		}
		return decoded;
	}

	public static InputStream toStream(ByteBuffer byteBuffer)
	{
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return new ByteArrayInputStream(bytes);
	}
}