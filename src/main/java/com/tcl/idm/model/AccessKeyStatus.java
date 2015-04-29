package com.tcl.idm.model;

import org.apache.commons.lang.StringUtils;

/**
 * AccessKey状态
 * 
 * @author yuanhuan
 * 2014年3月28日 上午11:32:15
 */
public class AccessKeyStatus
{
	/**
	 * 激活状态
	 */
	final public static String ACTIVE = "active";

	/**
	 * 无效状态
	 */
	final public static String INACTIVE = "inactive";

	/**
	 * 判断状态是否合法
	 * 
	 * @param status 状态
	 * @return true表示合法，false表示不合法
	 */
	public static boolean isStatusValid(String status)
	{
		if (StringUtils.isEmpty(status))
		{
			return false;
		}

		if (AccessKeyStatus.ACTIVE.equals(status) || AccessKeyStatus.INACTIVE.equals(status))
		{
			return true;
		}

		return false;
	}
}
