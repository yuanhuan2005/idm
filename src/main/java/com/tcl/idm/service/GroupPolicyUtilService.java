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
import com.tcl.idm.model.GroupPolicy;
import com.tcl.idm.model.Policy;
import com.tcl.idm.model.PolicyDocument;
import com.tcl.idm.model.PolicyOwnerType;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.repository.PolicyRepository;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.LimitsUtils;

public class GroupPolicyUtilService
{
	final static private Log DEBUGGER = LogFactory.getLog(GroupPolicyUtilService.class);

	/**
	 * 创建组的Policy
	 * 
	 * @param groupId
	 * @param policyName
	 * @param policyDocument
	 * @return
	 */
	public static ServiceCallResult createGroupPolicy(String groupId, String policyName, PolicyDocument[] policyDocument)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查组的Policy个数限制是否达到
		int maxNumberPolicysPerGroup = LimitsUtils.getMaxNumberPolicysPerGroup();
		List<Policy> groupPolicyList = PolicyRepository.listGroupPolicys(groupId);
		if (null != groupPolicyList)
		{
			int policyListSize = groupPolicyList.size();
			if (policyListSize >= maxNumberPolicysPerGroup)
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
				serviceCallResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 检查组下面是否已经创建了相同的策略名。同一个组下面的策略名字不可以相同。
		for (Policy policy : groupPolicyList)
		{
			if (policyName.equals(policy.getPolicyName()))
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
				serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 生成PolicyID并检查是否已经存在,如果存在的话，则需要重新生成新的PolicyId
		String policyId = UUID.randomUUID().toString();
		boolean policyIdExistedFlag = (null != PolicyRepository.getPolicy(policyId));
		while (policyIdExistedFlag)
		{
			policyId = UUID.randomUUID().toString();
			policyIdExistedFlag = (null != PolicyRepository.getPolicy(policyId));
		}

		// 创建Policy
		Policy policy = new Policy();
		policy.setPolicyId(policyId);
		policy.setPolicyName(policyName);
		policy.setPolicyDocument(policyDocument);
		policy.setOwnerType(PolicyOwnerType.GROUP);
		policy.setOwnerId(groupId);
		boolean result = PolicyRepository.createPolicy(policy);
		if (!result)
		{
			return serviceCallResult;
		}

		// 构造响应消息
		serviceCallResult.setResultJsonString(JSONObject.fromObject(policy.convertToGroupPolicy()).toString());
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 创建组的Policy
	 * 
	 * @param groupId
	 * @param policyName
	 * @param policyDocument
	 * @return
	 */
	public static ServiceCallResult updateGroupPolicy(String groupId, String policyId, String policyName,
	        PolicyDocument[] policyDocument)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查策略是否存在
		Policy policy = PolicyRepository.getPolicy(policyId);
		if (null == policy)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			GroupPolicyUtilService.DEBUGGER.error(CustomErrorCode.NoSuchEntity.getMessage());
			return serviceCallResult;
		}

		// 判断policy是否属于该组
		if (!PolicyOwnerType.GROUP.equals(policy.getOwnerType()) || !groupId.equals(policy.getOwnerId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查组下面是否已经创建了相同的策略名。同一个组下面的策略名字不可以相同。
		if (StringUtils.isNotEmpty(policyName))
		{
			List<Policy> groupPolicyList = PolicyRepository.listGroupPolicys(groupId);
			for (Policy policyInList : groupPolicyList)
			{
				if (policyName.equals(policyInList.getPolicyName()))
				{
					serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
					serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
					serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
					serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
					        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
					return serviceCallResult;
				}
			}
			policy.setPolicyName(policyName);
		}

		// 构造policyDocument
		if (null != policyDocument)
		{
			policy.setPolicyDocument(policyDocument);
		}

		// 更新
		boolean updateResult = PolicyRepository.updatePolicy(policy);
		if (!updateResult)
		{
			return serviceCallResult;
		}

		// 构造响应消息
		serviceCallResult.setResultJsonString(CommonService.genSuccesResultJsonString());
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 获取组的Policy
	 * 
	 * @param groupId
	 * @param policyId
	 * @return
	 */
	public static ServiceCallResult getGroupPolicy(String groupId, String policyId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查policy是否存在
		Policy policy = PolicyRepository.getPolicy(policyId);
		if (null == policy)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			GroupPolicyUtilService.DEBUGGER.error(CustomErrorCode.NoSuchEntity.getMessage());
			return serviceCallResult;
		}

		// 判断policy是否属于该组
		if (!PolicyOwnerType.GROUP.equals(policy.getOwnerType()) || !groupId.equals(policy.getOwnerId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 构造响应消息
		serviceCallResult.setResultJsonString(JSONObject.fromObject(policy.convertToGroupPolicy()).toString());
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 删除Policy
	 * 
	 * @param groupId
	 * @param policyId
	 * @return
	 */
	public static ServiceCallResult deleteGroupPolicy(String groupId, String policyId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 判断Policy是否存在
		Policy policy = PolicyRepository.getPolicy(policyId);
		if (null == policy)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 判断policy是否属于该组
		if (!PolicyOwnerType.GROUP.equals(policy.getOwnerType()) || !groupId.equals(policy.getOwnerId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 删除policy
		boolean result = PolicyRepository.deletePolicy(policyId);
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
	 * 查询Policy列表
	 * 
	 * @param groupId
	 * @return
	 */
	public static ServiceCallResult listGroupPolicys(String groupId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();
		if (StringUtils.isEmpty(groupId))
		{
			return serviceCallResult;
		}

		// 过滤结果，并且去掉无需返回的参数
		List<Policy> policyList = PolicyRepository.listGroupPolicys(groupId);
		List<GroupPolicy> policyWithoutSecretKeyList = new ArrayList<GroupPolicy>();
		for (Policy policy : policyList)
		{
			policyWithoutSecretKeyList.add(policy.convertToGroupPolicy());
		}
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(JSONArray.fromObject(policyWithoutSecretKeyList).toString());
		return serviceCallResult;
	}
}
