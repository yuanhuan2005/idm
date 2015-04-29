package com.tcl.idm.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.DBSelectResult;
import com.tcl.idm.util.DatabaseUtils;

/**
 * AccessKey仓库类，用于操作数据库
 * 
 * @author yuanhuan
 * 2014年3月26日 上午10:57:31
 */
public class AccessKeyRepository
{
	final static private Log DEBUGGER = LogFactory.getLog(AccessKeyRepository.class);

	final static private String TABLE_NAME = "t_idm_accesskey";

	/**
	 * 将数据库查询的ResultSet转换成AccessKey对象
	 * 
	 * @param resultSet
	 * @return
	 */
	private static AccessKey convertResultSetToAccessKey(ResultSet resultSet)
	{
		AccessKey accessKey = null;
		try
		{
			accessKey = new AccessKey();
			accessKey.setAccessKeyId(resultSet.getString("accessKeyId"));
			accessKey.setSecretAccessKey(resultSet.getString("secretAccessKey"));
			accessKey.setStatus(resultSet.getString("status"));
			accessKey.setOwnerId(resultSet.getString("ownerId"));
			accessKey.setCreateDate(resultSet.getString("createDate"));
		}
		catch (Exception e)
		{
			AccessKeyRepository.DEBUGGER.error("Exception: " + e.toString());
		}

		return accessKey;
	}

	/**
	 * 创建一个新的AccessKey
	 * 
	 * @param accessKey 
	 * @return 创建结果，true表示成功，false表示失败
	 */
	public static boolean createAccessKey(AccessKey accessKey)
	{
		if (null == accessKey)
		{
			return false;
		}

		String sql = "insert into " + AccessKeyRepository.TABLE_NAME
		        + "(accessKeyId,secretAccessKey,status,ownerId,createDate) values('" + accessKey.getAccessKeyId()
		        + "','" + accessKey.getSecretAccessKey() + "','" + accessKey.getStatus() + "','"
		        + accessKey.getOwnerId() + "','" + accessKey.getCreateDate() + "')";
		boolean result = DatabaseUtils.insert(sql);
		return result;
	}

	/**
	 * 列出某一个用户所有的AccessKey列表
	 * 
	 * @param ownerId 用户名
	 * @return 该用户的AccessKey列表
	 */
	public static List<AccessKey> listAccessKeys(String ownerId)
	{
		List<AccessKey> accessKeyList = new ArrayList<AccessKey>();
		AccessKey accessKey = null;

		if (StringUtils.isEmpty(ownerId))
		{
			AccessKeyRepository.DEBUGGER.error("ownerId is null");
			return accessKeyList;
		}

		String sql = "select * from " + AccessKeyRepository.TABLE_NAME + " where ownerId = '" + ownerId + "'";
		ResultSet resultSet = null;
		DBSelectResult dbSelectResult = null;

		try
		{
			dbSelectResult = DatabaseUtils.select(sql);
			resultSet = dbSelectResult.getResultSet();
			while (resultSet.next())
			{
				accessKey = AccessKeyRepository.convertResultSetToAccessKey(resultSet);
				accessKeyList.add(accessKey);
			}
		}
		catch (Exception e)
		{
			AccessKeyRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return accessKeyList;
	}

	/**
	 * 根据AccessKeyId查询AccessKey信息
	 * 
	 * @param accessKeyId AccessKey ID
	 * @return AccessKey信息
	 */
	public static AccessKey getAccessKey(String accessKeyId)
	{
		AccessKey accessKey = null;

		if (StringUtils.isEmpty(accessKeyId))
		{
			return accessKey;
		}

		String sql = "select * from " + AccessKeyRepository.TABLE_NAME + " where accessKeyId = '" + accessKeyId + "'";
		DBSelectResult dbSelectResult = DatabaseUtils.select(sql);
		ResultSet resultSet = dbSelectResult.getResultSet();
		try
		{
			if (resultSet.next())
			{
				accessKey = AccessKeyRepository.convertResultSetToAccessKey(resultSet);
			}
		}
		catch (Exception e)
		{
			AccessKeyRepository.DEBUGGER.error("Exception: " + e.toString());
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

		return accessKey;
	}

	/**
	 * 删除AccessKey
	 * 
	 * @param accessKeyId AccessKey ID
	 * @return 删除结果，true表示成功，false表示失败
	 */
	public static boolean deleteAccessKey(String accessKeyId)
	{
		if (StringUtils.isEmpty(accessKeyId))
		{
			return false;
		}

		String sql = "delete from " + AccessKeyRepository.TABLE_NAME + " where accessKeyId = '" + accessKeyId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}

	/**
	 * 删除某个用户的所有AccessKey
	 * 
	 * @param ownerId User ID
	 * @return 删除结果，true表示成功，false表示失败
	 */
	public static boolean deleteAccessKeysOfUser(String ownerId)
	{
		if (StringUtils.isEmpty(ownerId))
		{
			return false;
		}

		String sql = "delete from " + AccessKeyRepository.TABLE_NAME + " where ownerId = '" + ownerId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.delete(sqls);
		return result;
	}

	/**
	 * 更新AccessKey的状态
	 * 
	 * @param accessKeyId AccessKey ID
	 * @param status AccessKey的状态
	 * @return 更新结果，true表示成功，false表示失败
	 */
	public static boolean updateAccessKey(String accessKeyId, String status)
	{
		if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(status))
		{
			AccessKeyRepository.DEBUGGER.debug("accessKeyId=" + accessKeyId + ", status=" + status);
			AccessKeyRepository.DEBUGGER.error("accessKeyId or status is null");
			return false;
		}

		String sql = "update " + AccessKeyRepository.TABLE_NAME + " set status = '" + status
		        + "' where accessKeyId = '" + accessKeyId + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		boolean result = DatabaseUtils.update(sqls);
		return result;
	}
}
