package com.tcl.idm.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.DBSelectResult;
import com.tcl.idm.model.Group;
import com.tcl.idm.util.DatabaseUtils;

/**
 * Group仓库类，用于操作数据库
 * 
 * @author yuanhuan
 * 2014年3月26日 上午10:57:31
 */
public class GroupRepository
{
	final static private Log DEBUGGER = LogFactory.getLog(GroupRepository.class);

	final static private String TABLE_NAME = "t_idm_group";

	/**
	 * 将数据库查询的ResultSet转换成Group对象
	 * 
	 * @param resultSet
	 * @return
	 */
	public static Group convertResultSetToGroup(ResultSet resultSet)
	{
		Group group = null;
		try
		{
			group = new Group();
			group.setGroupId(resultSet.getString("groupId"));
			group.setGroupName(resultSet.getString("groupName"));
			group.setAccountId(resultSet.getString("accountId"));
			group.setCreateDate(resultSet.getString("createDate"));
			group.setDescription(resultSet.getString("description"));
		}
		catch (Exception e)
		{
			GroupRepository.DEBUGGER.error("Exception: " + e.toString());
		}

		return group;
	}

	/**
	 * 创建一个新的Group
	 * 
	 * @param group 
	 * @return 创建结果，true表示成功，false表示失败
	 */
	public static boolean createGroup(Group group)
	{
		if (null == group)
		{
			return false;
		}

		String sql = "insert into " + GroupRepository.TABLE_NAME
		        + "(groupId,groupName,accountId,createDate,description) values('" + group.getGroupId() + "','"
		        + group.getGroupName() + "','" + group.getAccountId() + "','" + group.getCreateDate() + "','"
		        + group.getDescription() + "')";
		boolean result = DatabaseUtils.insert(sql);
		return result;
	}

	/**
	 * 列出某一个账户所有的Group列表。
	 * 如果指明了用户ID，则查询该用户属于哪些组；如果未指明用户ID，则查询该账户下所有的组。
	 * 
	 * @param accountId 账户名
	 * @param userId 用户名
	 * @return Group列表
	 */
	public static List<Group> listGroups(String accountId, String userId)
	{
		List<Group> groupList = new ArrayList<Group>();
		Group group = null;

		if (StringUtils.isEmpty(accountId))
		{
			GroupRepository.DEBUGGER.error("accountId is null");
			return groupList;
		}

		String sql = "";
		if (StringUtils.isEmpty(userId))
		{
			sql = "select * from " + GroupRepository.TABLE_NAME + " where accountId = '" + accountId + "'";
		}
		else
		{
			sql = "select g.* from " + GroupRepository.TABLE_NAME
			        + " g, t_idm_user_group_map ugm where g.accountId = '" + accountId
			        + "' and g.groupId = ugm.groupId and ugm.userId = '" + userId + "'";
		}
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			while (resultSet.next())
			{
				group = GroupRepository.convertResultSetToGroup(resultSet);
				groupList.add(group);
			}
		}
		catch (Exception e)
		{
			GroupRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return groupList;
	}

	/**
	 * 根据GroupId查询Group信息
	 * 
	 * @param groupId Group ID
	 * @return Group信息
	 */
	public static Group getGroup(String groupId)
	{
		Group group = null;

		if (StringUtils.isEmpty(groupId))
		{
			return group;
		}

		String sql = "select * from " + GroupRepository.TABLE_NAME + " where groupId = '" + groupId + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			if (resultSet.next())
			{
				group = GroupRepository.convertResultSetToGroup(resultSet);
			}
		}
		catch (Exception e)
		{
			GroupRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return group;
	}

	public static void appendDeleteGroupSqls(List<String> sqls, String groupId)
	{
		// 删除该组与用户的关系
		sqls.add("delete from t_idm_user_group_map where groupId = '" + groupId + "'");

		// 删除该组的所有Policy
		sqls.add("delete from t_idm_policy where ownerType = 'group' and  ownerId = '" + groupId + "'");

		// 删除该组
		sqls.add("DELETE FROM " + GroupRepository.TABLE_NAME + " WHERE groupId = '" + groupId + "'");
	}

	/**
	 * 删除Group
	 * 
	 * @param groupId Group ID
	 * @return 删除结果，true表示成功，false表示失败
	 */
	public static boolean deleteGroup(String groupId)
	{
		if (StringUtils.isEmpty(groupId))
		{
			return false;
		}

		List<String> sqls = new ArrayList<String>();
		GroupRepository.appendDeleteGroupSqls(sqls, groupId);

		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}

	/**
	 * 更新Group的状态
	 * 
	 * @param groupId Group ID
	 * @param groupName Group名
	 * @param description 描述
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateGroup(String groupId, String groupName, String description)
	{
		if (StringUtils.isEmpty(groupId))
		{
			GroupRepository.DEBUGGER.error("groupId is null");
			return false;
		}

		if (StringUtils.isEmpty(groupName) && StringUtils.isEmpty(description))
		{
			return true;
		}

		String sql = "";
		if (StringUtils.isEmpty(groupName) && StringUtils.isEmpty(description))
		{
			return true;
		}
		else if (StringUtils.isNotEmpty(groupName) && StringUtils.isEmpty(description))
		{
			sql = "update " + GroupRepository.TABLE_NAME + " set groupName = '" + groupName + "' where groupId = '"
			        + groupId + "'";
		}
		else if (StringUtils.isEmpty(groupName) && StringUtils.isNotEmpty(description))
		{
			sql = "update " + GroupRepository.TABLE_NAME + " set description = '" + description + "' where groupId = '"
			        + groupId + "'";
		}
		else
		{
			sql = "update " + GroupRepository.TABLE_NAME + " set groupName = '" + groupName + "', description = '"
			        + description + "' where groupId = '" + groupId + "'";
		}

		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}
}
