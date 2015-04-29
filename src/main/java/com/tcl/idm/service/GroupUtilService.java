package com.tcl.idm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.Group;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.repository.GroupRepository;
import com.tcl.idm.repository.UserGroupRepository;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.LimitsUtils;

public class GroupUtilService
{
	final static private Log DEBUGGER = LogFactory.getLog(GroupUtilService.class);

	/**
	 * 创建Group
	 * 
	 * @param accountId
	 * @param groupName
	 * @param description
	 * @return
	 */
	public static ServiceCallResult createGroup(String accountId, String groupName, String description)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查GroupName是否存在, 同一个账户下，组名不能重复
		List<Group> groupList = GroupRepository.listGroups(accountId, "");
		if (null != groupList && !groupList.isEmpty())
		{
			for (Group group : groupList)
			{
				if (groupName.equals(group.getGroupName()))
				{
					serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
					serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
					serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
					serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
					        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
					return serviceCallResult;
				}
			}
		}

		// 检查账户的Group个数限制是否达到
		int maxNumberGroupsPerAccount = LimitsUtils.getMaxNumberGroupsPerAccount();
		if (null != groupList)
		{
			int groupListSize = groupList.size();
			if (groupListSize >= maxNumberGroupsPerAccount)
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
				serviceCallResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 创建Group
		Group group = new Group();
		String newGroupId = UUID.randomUUID().toString();

		// 检查GroupID是否已经存在,如果存在的话，则需要重新生成新的GroupId
		boolean groupIdExistedFlag = (null != GroupRepository.getGroup(newGroupId));
		while (groupIdExistedFlag)
		{
			newGroupId = UUID.randomUUID().toString();
			groupIdExistedFlag = (null != GroupRepository.getGroup(newGroupId));
		}

		// 构造Group对象
		group.setGroupId(newGroupId);
		group.setGroupName(groupName);
		group.setAccountId(accountId);
		group.setDescription(description);
		group.setCreateDate(DateFormatterUtils.getCurrentUTCTime());
		boolean result = GroupRepository.createGroup(group);
		if (!result)
		{
			return serviceCallResult;
		}

		// 构造响应消息
		JSONObject json = JSONObject.fromObject(group).discard("groupPolicy").discard("accountId");
		serviceCallResult.setResultJsonString(json.toString());
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 删除Group
	 * 
	 * @param accountId
	 * @param groupId
	 * @return
	 */
	public static ServiceCallResult deleteGroup(String accountId, String groupId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 判断Group是否存在
		Group group = GroupRepository.getGroup(groupId);
		if (null == group)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断group是否属于该账户
		if (!accountId.equals(group.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 删除组
		boolean result = GroupRepository.deleteGroup(groupId);
		if (!result)
		{
			return serviceCallResult;
		}

		// 设置返回消息
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(CommonService.genSuccesResultJsonString());
		return serviceCallResult;
	}

	/**
	 * 更新Group
	 * 
	 * @param accountId
	 * @param groupId
	 * @param groupName
	 * @param description
	 * @return
	 */
	public static ServiceCallResult updateGroup(String accountId, String groupId, String groupName, String description)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(groupId))
		{
			GroupUtilService.DEBUGGER.error("groupId or status is null");
			return serviceCallResult;
		}

		if (StringUtils.isEmpty(groupName) && StringUtils.isEmpty(description))
		{
			GroupUtilService.DEBUGGER.error("groupName or description is null");
			serviceCallResult.setErrorCode(CustomErrorCode.MissingParameter.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.MissingParameter.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;

		}

		// 检查Group是否存在
		Group group = GroupRepository.getGroup(groupId);
		if (null == group)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断group是否属于该账户
		if (!accountId.equals(group.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查GroupName是否存在, 同一个账户下，组名不能重复
		List<Group> groupList = GroupRepository.listGroups(accountId, "");
		if (null != groupList && !groupList.isEmpty())
		{
			for (Group g : groupList)
			{
				if (groupName.equals(g.getGroupName()))
				{
					serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
					serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
					serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
					serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
					        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
					return serviceCallResult;
				}
			}
		}

		// 更新Group
		boolean result = GroupRepository.updateGroup(groupId, groupName, description);
		if (!result)
		{
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		Group newGroup = GroupRepository.getGroup(groupId);
		String jsonString = JSONObject.fromObject(newGroup).discard("accountId").discard("groupPolicy").toString();
		serviceCallResult.setResultJsonString(jsonString);
		return serviceCallResult;
	}

	/**
	 * 查询Group列表
	 * 
	 * @param accountId
	 * @param userId
	 * @return
	 */
	public static ServiceCallResult listGroups(String accountId, String userId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();
		if (StringUtils.isEmpty(accountId))
		{
			return serviceCallResult;
		}

		// 过滤结果，并且去掉无需返回的参数
		List<Group> groupList = GroupRepository.listGroups(accountId, userId);
		List<JSONObject> groupWithoutSecretKeyList = new ArrayList<JSONObject>();
		for (Group group : groupList)
		{
			groupWithoutSecretKeyList.add(JSONObject.fromObject(group).discard("accountId").discard("groupPolicy"));
		}
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(JSONArray.fromObject(groupWithoutSecretKeyList).toString());
		return serviceCallResult;
	}

	/**
	 * 获取Group信息
	 * 
	 * @param groupId 用户ID
	 * @return
	 */
	public static ServiceCallResult getGroup(String accountId, String groupId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(accountId) || StringUtils.isEmpty(groupId))
		{
			GroupUtilService.DEBUGGER.error("accountId or groupId is null");
			return serviceCallResult;
		}

		// 检查Group是否存在
		Group group = GroupRepository.getGroup(groupId);
		if (null == group)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断group是否属于该账户
		if (!accountId.equals(group.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		String jsonString = JSONObject.fromObject(group).discard("accountId").discard("groupPolicy").toString();
		serviceCallResult.setResultJsonString(jsonString);
		return serviceCallResult;
	}

	/**
	 * 将用户添加到组中去
	 * 
	 * @param accountId
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public static ServiceCallResult addUserToGroup(String accountId, String userId, String groupId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(accountId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId))
		{
			GroupUtilService.DEBUGGER.error("accountId userId or groupId is null");
			return serviceCallResult;
		}

		// 检查Group是否存在
		Group group = GroupRepository.getGroup(groupId);
		if (null == group)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断group是否属于该账户
		if (!accountId.equals(group.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查用户是否已经加入到组了
		if (UserGroupRepository.isUserAlreadyAddedToGroup(userId, groupId))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 添加用户到组
		boolean result = UserGroupRepository.addUserToGroup(userId, groupId);
		if (!result)
		{
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(CommonService.genSuccesResultJsonString());
		return serviceCallResult;
	}

	/**
	 * 将一个用户从组中删除。
	 * 
	 * @param accountId
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public static ServiceCallResult removeUserFromGroup(String accountId, String userId, String groupId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(accountId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId))
		{
			GroupUtilService.DEBUGGER.error("accountId userId or groupId is null");
			return serviceCallResult;
		}

		// 检查Group是否存在
		Group group = GroupRepository.getGroup(groupId);
		if (null == group)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断group是否属于该账户
		if (!accountId.equals(group.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查用户是否已经加入到组了
		if (!UserGroupRepository.isUserAlreadyAddedToGroup(userId, groupId))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		//从组中删除用户
		boolean result = UserGroupRepository.removeUserFromGroup(userId, groupId);
		if (!result)
		{
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(CommonService.genSuccesResultJsonString());
		return serviceCallResult;
	}
}
