package com.tcl.idm.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密解密服务类
 * 
 * @author yuanhuan
 * 2014年4月1日 下午4:19:44
 */
public class AESUtils
{
	final private static String PASSWORD = "G34DF_sd*&G^&Vhf897HK@$JHui32";

	final private static String SECURE_RANDOM_KEY = "SHA1PRNG";

	/**
	 * 加密
	 * 
	 * @param content 需要加密的内容
	 * @param password  加密密码
	 * @return
	 */
	public static String encrypt(String content)
	{
		try
		{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom secureRandom = SecureRandom.getInstance(AESUtils.SECURE_RANDOM_KEY);
			secureRandom.setSeed(AESUtils.PASSWORD.getBytes());
			kgen.init(128, secureRandom);
			//			kgen.init(128, new SecureRandom(AESUtils.PASSWORD.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			String encryptResultStr = AESUtils.parseByte2HexStr(result);
			return encryptResultStr;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**解密
	 * @param content  待解密内容
	 * @param password 解密密钥
	 * @return
	 */
	public static String decrypt(String content)
	{
		try
		{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance(AESUtils.SECURE_RANDOM_KEY);
			secureRandom.setSeed(AESUtils.PASSWORD.getBytes());
			kgen.init(128, secureRandom);
			//			kgen.init(128, new SecureRandom(AESUtils.PASSWORD.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptFrom = AESUtils.parseHexStr2Byte(content);
			byte[] result = cipher.doFinal(decryptFrom);
			return new String(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++)
		{
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**将16进制转换为二进制
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr)
	{
		if (hexStr.length() < 1)
		{
			return null;
		}
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++)
		{
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static void main(String[] args)
	{
		//加密
		String encryptResult = AESUtils.encrypt("123456");
		System.out.println("encryptResult: " + encryptResult);

		//解密
		String decryptResult = AESUtils
		        .decrypt("ECCCEDF3DCCC46FBFDC53233BF9D92DAA70FE93ED1507F4AA4982B07A699816CD4B64D513BDFA4349F6DB3B8DD0F8A5D7A926104DF67C0215F4FE5F42CFC777B");
		System.out.println("decryptResult: " + decryptResult);
	}
}