package com.tcl.idm.util;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月4日 下午5:20:36
 */
public class LimitsUtils
{
	private static final String LIMITS_PROPERTIES_FILE = "limits.properties";

	/**
	 * 获取每个账户可以创建User的最大个数
	 * 
	 * @return 最大个数
	 */
	public static int getMaxNumberUsersPerAccount()
	{
		int defaultMaxNumberUsersPerAccount = 500;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberUsersPerAccount"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberUsersPerAccount;
		}
	}

	/**
	 * 获取每个用户可以创建AccessKey的最大个数
	 * 
	 * @return 最大个数
	 */
	public static int getMaxNumberAccessKeysPerUser()
	{
		int defaultMaxNumberAccessKeysPerUser = 10;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberAccessKeysPerUser"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberAccessKeysPerUser;
		}
	}

	/**
	 * 获取每个用户可以激活AccessKey的最大个数
	 * 
	 * @return 最大个数
	 */
	public static int getMaxNumberActiveAccessKeysPerUser()
	{
		int defaultMaxNumberActiveAccessKeysPerUser = 10;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberActiveAccessKeysPerUser"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberActiveAccessKeysPerUser;
		}
	}

	/**
	 * 获取每个IP在每个分钟中最多可以请求的次数
	 * 
	 * @return 最大个数
	 */
	public static int getMaxTimesOfSameIPRequestPerMinute()
	{
		int defaultMaxTimesOfSameIPRequestPerMinute = 1000;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxTimesOfSameIPRequestPerMinute"));
		}
		catch (Exception e)
		{
			return defaultMaxTimesOfSameIPRequestPerMinute;
		}
	}

	/**
	 * 获取同一个用户在一分钟内最多请求的次数
	 * 
	 * @return 最大个数
	 */
	public static int getMaxTimesOfSameUserAccessSameServicePerMinute()
	{
		int defaultMaxTimesOfSameUserAccessSameServicePerMinute = 100;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxTimesOfSameUserAccessSameServicePerMinute"));
		}
		catch (Exception e)
		{
			return defaultMaxTimesOfSameUserAccessSameServicePerMinute;
		}
	}

	/**
	 * 获取时间戳过期时间，单位：分钟。
	 * 
	 * @return 过期时间
	 */
	public static int getTimestampExpiredInMinutes()
	{
		int defaultTimestampExpiredInMinutes = 5;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "timestampExpiredInMinutes"));
		}
		catch (Exception e)
		{
			return defaultTimestampExpiredInMinutes;
		}
	}

	/**
	 * 获取一个账户可以创建的组的最大个数
	 * 
	 * @return
	 */
	public static int getMaxNumberGroupsPerAccount()
	{
		int defaultMaxNumberGroupsPerAccount = 50;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberGroupsPerAccount"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberGroupsPerAccount;
		}
	}

	/**
	 * 获取每个组可以包含的用户最大个数
	 * 
	 * @return
	 */
	public static int getMaxNumberUsersPerGroup()
	{
		int defaultMaxNumberUsersPerGroup = 100;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberUsersPerGroup"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberUsersPerGroup;
		}
	}

	/**
	 * 获取每个用户可以属于的组的最大个数
	 * 
	 * @return
	 */
	public static int getMaxNumberGroupsPerUser()
	{
		int defaultMaxNumberGroupsPerUser = 10;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberGroupsPerUser"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberGroupsPerUser;
		}
	}

	/**
	 * 获取每个用户最大可以创建的策略个数
	 * 
	 * @return
	 */
	public static int getMaxNumberPolicysPerUser()
	{
		int defaultMaxNumberPolicysPerUser = 5;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberPolicysPerUser"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberPolicysPerUser;
		}
	}

	/**
	 * 获取每个组可以创建的策略个数
	 * 
	 * @return
	 */
	public static int getMaxNumberPolicysPerGroup()
	{
		int defaultMaxNumberPolicysPerGroup = 20;
		try
		{
			return Integer.valueOf(CommonService.getBaseConfValue(LimitsUtils.LIMITS_PROPERTIES_FILE,
			        "maxNumberPolicysPerGroup"));
		}
		catch (Exception e)
		{
			return defaultMaxNumberPolicysPerGroup;
		}
	}

}
