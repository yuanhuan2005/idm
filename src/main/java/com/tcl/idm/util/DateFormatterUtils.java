package com.tcl.idm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatterUtils
{
	final static private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	final static private String MINUTE_FORMAT = "yyyy-MM-dd'T'HH:mm:'00Z'";

	/**
	 * 获取当前分钟时间，分钟和秒钟为0
	 * @return String类型的当前时间
	 */
	public static String getCurrentUTCMinuteTime()
	{
		DateFormat formatter = new SimpleDateFormat(DateFormatterUtils.MINUTE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		String utcDate = formatter.format(new Date());
		return utcDate;
	}

	/**
	 * 获取当前时间
	 * @return String类型的当前时间
	 */
	public static String getCurrentUTCTime()
	{
		DateFormat formatter = new SimpleDateFormat(DateFormatterUtils.DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		String utcDate = formatter.format(new Date());
		return utcDate;
	}

	/**
	 * 将时间字符串转换成Date类型的对象
	 * @param dateString 时间字符串
	 * @return Date对象
	 */
	public static Date convertStringToDate(String dateString)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormatterUtils.DATE_FORMAT);

		try
		{
			return simpleDateFormat.parse(dateString);
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Enter main");

		System.out.println("current UTC time=" + DateFormatterUtils.getCurrentUTCTime());
		System.out.println("current UTC minute time=" + DateFormatterUtils.getCurrentUTCMinuteTime());
		Date date = DateFormatterUtils.convertStringToDate(DateFormatterUtils.getCurrentUTCTime());
		System.out.println("date = " + date.toString());

		System.out.println("System.currentTimeMillis=" + System.currentTimeMillis());
		System.out.println("new Date().getTime()=" + new Date().getTime());
		System.out.println("new UTC Date().getTime()="
		        + DateFormatterUtils.convertStringToDate(DateFormatterUtils.getCurrentUTCTime()).getTime());

		System.out.println("End main");
	}
}
