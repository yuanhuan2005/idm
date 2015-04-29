package com.tcl.idm.model;

import org.apache.commons.lang.StringUtils;

/**
 * 用户类型
 * 
 * @author yuanhuan
 * 2014年3月28日 上午11:32:15
 */
public class UserType
{
	/**
	 * 超级管理员用户
	 */
	final public static String ADMIN = "admin";

	/**
	 * 账户
	 */
	final public static String ACCOUNT = "account";

	/**
	 * 用户
	 */
	final public static String USER = "user";

	/**
	 * 内部用户，预留给内部组件使用
	 */
	final public static String INNER = "inner";

	/**
	 * 判断类型是否合法
	 * 
	 * @param userType 状态
	 * @return true表示合法，false表示不合法
	 */
	public static boolean isTypeValid(String userType)
	{
		if (StringUtils.isEmpty(userType))
		{
			return false;
		}

		if (UserType.ACCOUNT.equals(userType) || UserType.USER.equals(userType) || UserType.ADMIN.equals(userType)
		        || UserType.INNER.equals(userType))
		{
			return true;
		}

		return false;
	}
}
