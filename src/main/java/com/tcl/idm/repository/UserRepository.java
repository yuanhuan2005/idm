package com.tcl.idm.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.DBSelectResult;
import com.tcl.idm.model.Group;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.util.DatabaseUtils;

/**
 * User仓库类，用于操作数据库
 * 
 * @author yuanhuan
 * 2014年3月26日 上午10:57:31
 */
public class UserRepository
{
	final static private Log DEBUGGER = LogFactory.getLog(UserRepository.class);

	final static private String TABLE_NAME = "t_idm_user";

	/**
	 * 将数据库查询的ResultSet转换成User对象
	 * 
	 * @param resultSet
	 * @return
	 */
	public static User convertResultSetToUser(ResultSet resultSet)
	{
		User user = null;
		try
		{
			user = new User();
			user.setUserId(resultSet.getString("userId"));
			user.setUserName(resultSet.getString("userName"));
			user.setUserType(resultSet.getString("userType"));
			user.setAccountId(resultSet.getString("accountId"));
			user.setPassword(resultSet.getString("password"));
			user.setCreateDate(resultSet.getString("createDate"));
		}
		catch (Exception e)
		{
			UserRepository.DEBUGGER.error("Exception: " + e.toString());
		}

		return user;
	}

	/**
	 * 创建一个新的User
	 * 
	 * @param user 
	 * @return 创建结果，true表示成功，false表示失败
	 */
	public static boolean createUser(User user)
	{
		if (null == user)
		{
			return false;
		}

		String sql = "insert into " + UserRepository.TABLE_NAME
		        + "(userId,userName,userType,accountId,createDate,password) values('" + user.getUserId() + "','"
		        + user.getUserName() + "','" + user.getUserType() + "','" + user.getAccountId() + "','"
		        + user.getCreateDate() + "','" + user.getPassword() + "')";
		boolean result = DatabaseUtils.insert(sql);
		return result;
	}

	/**
	 * 列出所有的User列表
	 * 
	 * @param userId 用户名
	 * @return 该用户的User列表
	 */
	public static List<User> listUsers(String accountId)
	{
		List<User> userList = new ArrayList<User>();
		User user = null;

		String sql = "select * from " + UserRepository.TABLE_NAME + " where accountId = '" + accountId + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			while (resultSet.next())
			{
				user = UserRepository.convertResultSetToUser(resultSet);
				userList.add(user);
			}
		}
		catch (Exception e)
		{
			UserRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return userList;
	}

	/**
	 * 列出某个类型的User列表
	 * 
	 * @param userId 用户名
	 * @return 该用户的User列表
	 */
	public static List<User> listUsersByType(String userType)
	{
		List<User> userList = new ArrayList<User>();
		if (StringUtils.isEmpty(userType))
		{
			return userList;
		}

		User user = null;
		String sql = "select * from " + UserRepository.TABLE_NAME + " where userType = '" + userType + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			while (resultSet.next())
			{
				user = UserRepository.convertResultSetToUser(resultSet);
				userList.add(user);
			}
		}
		catch (Exception e)
		{
			UserRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return userList;
	}

	/**
	 * 根据UserId查询User信息
	 * 
	 * @param userId User ID
	 * @return User信息
	 */
	public static User getUser(String userId)
	{
		User user = null;

		if (StringUtils.isEmpty(userId))
		{
			return user;
		}

		String sql = "select * from " + UserRepository.TABLE_NAME + " where userId = '" + userId + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			if (resultSet.next())
			{
				user = UserRepository.convertResultSetToUser(resultSet);
			}
		}
		catch (Exception e)
		{
			UserRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return user;
	}

	private static void appendDeleteUserSqls(List<String> sqls, String userId)
	{
		{
			// 删除用户的时候，删除该用户下面的AccessKey
			sqls.add("delete from t_idm_accessKey where userId = '" + userId + "'");

			// 删除用户的时候，从所属组中移除
			sqls.add("delete from " + UserRepository.TABLE_NAME + "_group_map where userId = '" + userId + "'");

			// 删除用户的时候，删除该用户的策略 
			sqls.add("delete from t_idm_policy where ownerType = 'user' and  ownerId = '" + userId + "'");

			// 删除该用户
			sqls.add("delete from " + UserRepository.TABLE_NAME + " where userId = '" + userId + "'");
		}
	}

	/**
	 * 删除User
	 * 
	 * @param userId User ID
	 * @return 删除结果，true表示成功，false表示失败
	 */
	public static boolean deleteUser(String userId)
	{
		if (StringUtils.isEmpty(userId))
		{
			return false;
		}

		// 检查用户是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			return false;
		}

		// 根据用户类型来对应删除其关联关系
		List<String> sqls = new ArrayList<String>();
		if (UserType.ACCOUNT.equals(user.getUserType()))
		{
			// 删除该账户旗下的AccessKey
			sqls.add("delete from t_idm_accesskey where userId = '" + userId + "'");

			// 删除账户下的所有组
			List<Group> groupList = GroupRepository.listGroups(userId, "");
			if (null != groupList && !groupList.isEmpty())
			{
				for (Group group : groupList)
				{
					GroupRepository.appendDeleteGroupSqls(sqls, group.getGroupId());
				}
			}

			// 删除账户下的所有用户
			List<User> userList = UserRepository.listUsers(userId);
			if (null != userList && !userList.isEmpty())
			{
				for (User u : userList)
				{
					UserRepository.appendDeleteUserSqls(sqls, u.getUserId());
				}
			}

			// 删除该账户
			sqls.add("delete from " + UserRepository.TABLE_NAME + " where userId = '" + userId + "'");
		}
		else if (UserType.USER.equals(user.getUserType()))
		{
			UserRepository.appendDeleteUserSqls(sqls, userId);
		}
		else
		{
			sqls.add("delete from " + UserRepository.TABLE_NAME + " where userId = '" + userId + "'");
		}

		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}

	/**
	 * 更新User的用户名
	 * 
	 * @param userId User ID
	 * @param userName 用户名
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateUser(String userId, String userName)
	{
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName))
		{
			UserRepository.DEBUGGER.debug("userId=" + userId + ", userName=" + userName);
			UserRepository.DEBUGGER.error("userId or userName is null");
			return false;
		}

		String sql = "update " + UserRepository.TABLE_NAME + " set userName = '" + userName + "' where userId = '"
		        + userId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}

	/**
	 * 更新User的策略
	 * 
	 * @param userId User ID
	 * @param userPolicy 用户策略
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateUserPolicy(String userId, String userPolicy)
	{
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userPolicy))
		{
			UserRepository.DEBUGGER.debug("userId=" + userId + ", userPolicy=" + userPolicy);
			UserRepository.DEBUGGER.error("userId or userPolicy is null");
			return false;
		}

		String sql = "update " + UserRepository.TABLE_NAME + " set userPolicy = '" + userPolicy + "' where userId = '"
		        + userId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}

	/**
	 * 更新User的密码
	 * 
	 * @param userId User ID
	 * @param password 用户密码
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateUserPassword(String userId, String password)
	{
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(password))
		{
			UserRepository.DEBUGGER.debug("userId=" + userId + ", password=" + password);
			UserRepository.DEBUGGER.error("userId or password is null");
			return false;
		}

		String sql = "update " + UserRepository.TABLE_NAME + " set password = '" + password + "' where userId = '"
		        + userId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}

	/**
	 * 更新User信息
	 * 
	 * @param user 用户信息
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateUser(User user)
	{
		if (null == user)
		{
			UserRepository.DEBUGGER.error("user is null");
			return false;
		}

		String sql = "update " + UserRepository.TABLE_NAME + " set userName = '" + user.getUserName()
		        + "', userType = '" + user.getUserType() + "', accountId = '" + user.getAccountId()
		        + "', createDate = '" + user.getCreateDate() + "', password = '" + user.getPassword()
		        + "' where userId = '" + user.getUserId() + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}
}
