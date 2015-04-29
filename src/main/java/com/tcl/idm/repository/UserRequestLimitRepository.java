package com.tcl.idm.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.DBSelectResult;
import com.tcl.idm.model.UserRequestLimit;
import com.tcl.idm.util.DatabaseUtils;

/**
 * UserRequestLimit仓库类，用于操作数据库
 * 
 * @author yuanhuan
 * 2014年3月26日 上午10:57:31
 */
public class UserRequestLimitRepository
{
	final static private Log DEBUGGER = LogFactory.getLog(UserRequestLimitRepository.class);

	final static private String TABLE_NAME = "t_idm_user_request_logs";

	/**
	 * 将数据库查询的ResultSet转换成UserRequestLimit对象
	 * 
	 * @param resultSet
	 * @return
	 */
	private static UserRequestLimit convertResultSetToUserRequestLimit(ResultSet resultSet)
	{
		UserRequestLimit userRequestLimit = null;
		try
		{
			userRequestLimit = new UserRequestLimit();
			userRequestLimit.setUserId(resultSet.getString("userId"));
			userRequestLimit.setRequestTimes(resultSet.getInt("requestTimes"));
			userRequestLimit.setCurrentMinute(resultSet.getString("currentMinute"));
		}
		catch (Exception e)
		{
			UserRequestLimitRepository.DEBUGGER.error("Exception: " + e.toString());
		}

		return userRequestLimit;
	}

	/**
	 * 创建一个新的UserRequestLimit
	 * 
	 * @param userRequestLimit 
	 * @return 创建结果，true表示成功，false表示失败
	 */
	public static boolean createUserRequestLimit(UserRequestLimit userRequestLimit)
	{
		if (null == userRequestLimit)
		{
			return false;
		}

		String sql = "insert into " + UserRequestLimitRepository.TABLE_NAME
		        + "(userId,requestTimes,currentMinute) values('" + userRequestLimit.getUserId() + "','"
		        + userRequestLimit.getRequestTimes() + "','" + userRequestLimit.getCurrentMinute() + "')";
		boolean result = DatabaseUtils.insert(sql);
		return result;
	}

	/**
	 * 根据UserRequestLimitId查询UserRequestLimit信息
	 * 
	 * @param userId 
	 * @return UserRequestLimit信息
	 */
	public static UserRequestLimit getUserRequestLimit(String userId)
	{
		UserRequestLimit userRequestLimit = null;

		if (StringUtils.isEmpty(userId))
		{
			return userRequestLimit;
		}

		String sql = "select * from " + UserRequestLimitRepository.TABLE_NAME + " where userId = '" + userId + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			if (resultSet.next())
			{
				userRequestLimit = UserRequestLimitRepository.convertResultSetToUserRequestLimit(resultSet);
			}
		}
		catch (Exception e)
		{
			UserRequestLimitRepository.DEBUGGER.error("Exception: " + e.toString());
		}
		finally
		{
			try
			{
				dbSelectResult.getStatement().close();
			}
			catch (Exception e)
			{
			}
		}

		return userRequestLimit;
	}

	/**
	 * 删除UserRequestLimit
	 * 
	 * @param userId 
	 * @return 删除结果，true表示成功，false表示失败
	 */
	public static boolean deleteUserRequestLimit(String userId)
	{
		if (StringUtils.isEmpty(userId))
		{
			return false;
		}

		String sql = "delete from " + UserRequestLimitRepository.TABLE_NAME + " where userId = '" + userId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}

	/**
	 * 更新UserRequestLimit
	 * 
	 * @param userRequestLimit
	 * @return
	 */
	public static boolean updateUserRequestLimit(UserRequestLimit userRequestLimit)
	{
		if (null == userRequestLimit)
		{
			UserRequestLimitRepository.DEBUGGER.error("userRequestLimit is null");
			return false;
		}

		String sql = "update " + UserRequestLimitRepository.TABLE_NAME + " set requestTimes = "
		        + userRequestLimit.getRequestTimes() + ", currentMinute = '" + userRequestLimit.getCurrentMinute()
		        + "' where userId = '" + userRequestLimit.getUserId() + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}

	/**
	 * 获取当前分钟周期内同一个用户的请求数
	 * 
	 * @param currentMinute
	 * @return
	 */
	public static int getUserRequestLimitCountByCurrentMinute(String userId, String currentMinute)
	{
		int count = 0;

		if (StringUtils.isEmpty(currentMinute))
		{
			return count;
		}

		String sql = "select count(*) as recordCount from " + UserRequestLimitRepository.TABLE_NAME
		        + " where userId = '" + userId + "' and currentMinute = '" + currentMinute + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			if (resultSet.next())
			{
				count = Integer.valueOf(resultSet.getString("recordCount"));
			}
		}
		catch (Exception e)
		{
			UserRequestLimitRepository.DEBUGGER.error("Exception: " + e.toString());
		}
		finally
		{
			try
			{
				dbSelectResult.getStatement().close();
			}
			catch (Exception e)
			{
			}
		}

		return count;
	}

	/**
	 * 删除当前分钟除外的其他记录，只保留当前分钟的记录
	 * 
	 * @param currentMinute
	 * @return
	 */
	public static boolean deleteUserRequestLimitExceptCurrentMinute(String currentMinute)
	{
		if (StringUtils.isEmpty(currentMinute))
		{
			return false;
		}

		String sql = "delete from " + UserRequestLimitRepository.TABLE_NAME + " where currentMinute != '"
		        + currentMinute + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}
}
