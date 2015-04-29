package com.tcl.idm.util;

/**
 * IDM错误消息类
 * 
 * @author yuanhuan
 * 2014年3月28日 上午11:41:10
 */
public class IdmErrorMessageUtils
{
	/**
	 * 构造IDM接口错误消息
	 * 
	 * @param errorCode 错误码
	 * @param errorMsg 错误信息
	 * @return
	 */
	public static String genErrorMessageInJson(String errorCode, String errorMsg)
	{
		return "{\"errorCode\": \"" + errorCode + "\", \"errorMessage\": \"" + errorMsg + "\"}";
	}

}
