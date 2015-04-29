package com.tcl.idm.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AccessKeyStatus;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.model.User;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.LimitsUtils;

public class AccessKeyUtilService
{
	final static private Log DEBUGGER = LogFactory.getLog(AccessKeyUtilService.class);

	/**
	 * AccessKey的长度
	 */
	final static public int ACCESS_KEY_LEN = 32;

	/**
	 * SecretAccessKey的长度
	 */
	final static public int SECRET_ACCESS_KEY_LEN = 48;

	/**
	 * 创建AccessKey
	 * 
	 * @param userId
	 * @return
	 */
	public static ServiceCallResult createAccessKey(String userId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查用户是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查用户的AccessKey个数限制是否达到
		int maxNumberAccessKeysPerUser = LimitsUtils.getMaxNumberAccessKeysPerUser();
		List<AccessKey> accessKeyList = AccessKeyRepository.listAccessKeys(userId);
		if (null != accessKeyList)
		{
			int accessKeyListSize = accessKeyList.size();
			if (accessKeyListSize >= maxNumberAccessKeysPerUser)
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
				serviceCallResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 遍历所有AccessKey列表，查询用户已经激活的AccessKey的个数，避免超过限制
		String accessKeyStatus = AccessKeyStatus.ACTIVE;
		int userAKActiveNum = 1;
		for (AccessKey ak : accessKeyList)
		{
			if (AccessKeyStatus.ACTIVE.equals(ak.getStatus()))
			{
				userAKActiveNum++;
			}
		}

		// 检查是否超过Active的限制
		if (userAKActiveNum > LimitsUtils.getMaxNumberActiveAccessKeysPerUser())
		{
			accessKeyStatus = AccessKeyStatus.INACTIVE;
		}

		// 创建AccessKey
		AccessKey accessKey = new AccessKey();
		String newAccessKeyId = CommonService.randomString(AccessKeyUtilService.ACCESS_KEY_LEN);

		// 检查AccessKeyID是否已经存在,如果存在的话，则需要重新生成新的AccessKeyId
		boolean accessKeyIdExistedFlag = (null != AccessKeyRepository.getAccessKey(newAccessKeyId));
		while (accessKeyIdExistedFlag)
		{
			newAccessKeyId = CommonService.randomString(AccessKeyUtilService.ACCESS_KEY_LEN);
			accessKeyIdExistedFlag = (null != AccessKeyRepository.getAccessKey(newAccessKeyId));
		}

		String newSecretAccessKey = CommonService.randomString(AccessKeyUtilService.SECRET_ACCESS_KEY_LEN);
		accessKey.setAccessKeyId(newAccessKeyId);
		accessKey.setSecretAccessKey(AESUtils.encrypt(newSecretAccessKey));
		accessKey.setStatus(accessKeyStatus);
		accessKey.setOwnerId(userId);
		accessKey.setCreateDate(DateFormatterUtils.getCurrentUTCTime());
		boolean result = AccessKeyRepository.createAccessKey(accessKey);
		if (!result)
		{
			return serviceCallResult;
		}

		// 构造响应消息
		accessKey.setSecretAccessKey(newSecretAccessKey);
		JSONObject json = JSONObject.fromObject(accessKey);
		serviceCallResult.setResultJsonString(json.toString());
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 删除AccessKey
	 * 
	 * @param userId
	 * @param accessKeyId
	 * @return
	 */
	public static ServiceCallResult deleteAccessKey(String userId, String accessKeyId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 判断AccessKey是否存在
		AccessKey accessKey = AccessKeyRepository.getAccessKey(accessKeyId);
		if (null == accessKey)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 查询用户信息
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		String userIdOfAccessKeyId = accessKey.getOwnerId();
		User userOfAccessKeyId = UserRepository.getUser(userIdOfAccessKeyId);

		// 判断accessKey是否属于该用户
		if (!userId.equals(userIdOfAccessKeyId) && !userId.equals(userOfAccessKeyId.getAccountId()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 删除AccessKey
		boolean result = AccessKeyRepository.deleteAccessKey(accessKeyId);
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
	 * 更新AccessKey状态
	 * 
	 * @param accessKeyId
	 * @param status
	 * @return
	 */
	public static ServiceCallResult updateAccessKey(String accessKeyId, String status)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(status))
		{
			AccessKeyUtilService.DEBUGGER.error("accessKeyId or status is null");
			return serviceCallResult;
		}

		// 检查AccessKey是否存在
		AccessKey accessKey = AccessKeyRepository.getAccessKey(accessKeyId);
		if (null == accessKey)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查状态是否合法
		if (!AccessKeyStatus.isStatusValid(status))
		{
			serviceCallResult.setErrorCode(CustomErrorCode.InvalidParameter.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.InvalidParameter.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查请求更新的状态是否与现在的状态一样，如果一样的话则返回错误
		if (status.equals(accessKey.getStatus()))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setResultJsonString("");
			if (AccessKeyStatus.ACTIVE.equals(status))
			{
				serviceCallResult.setErrorCode(CustomErrorCode.AccessKeyAlreadyActive.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.AccessKeyAlreadyActive.getMessage());
			}
			else
			{
				serviceCallResult.setErrorCode(CustomErrorCode.AccessKeyAlreadyInactive.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.AccessKeyAlreadyInactive.getMessage());
			}

			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 如果请求激活的话，需要检查是否超过限制
		if (AccessKeyStatus.ACTIVE.equals(status))
		{
			// 遍历所有AccessKey列表，查询用户已经激活的AccessKey的个数，避免超过限制
			String userId = accessKey.getOwnerId();
			List<AccessKey> accessKeyList = AccessKeyRepository.listAccessKeys(userId);
			int userAKActiveNum = 1;
			for (AccessKey ak : accessKeyList)
			{
				if (AccessKeyStatus.ACTIVE.equals(ak.getStatus()))
				{
					userAKActiveNum++;
				}
			}

			// 检查是否超过限制
			if (userAKActiveNum > LimitsUtils.getMaxNumberActiveAccessKeysPerUser())
			{
				serviceCallResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 更新状态
		accessKey.setStatus(status);
		boolean result = AccessKeyRepository.updateAccessKey(accessKey.getAccessKeyId(), accessKey.getStatus());
		if (!result)
		{
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		String jsonString = JSONObject.fromObject(accessKey).discard("secretAccessKey").toString();
		serviceCallResult.setResultJsonString(jsonString);
		return serviceCallResult;
	}

	/**
	 * 查询AccessKey列表
	 * 
	 * @param userId
	 * @return
	 */
	public static ServiceCallResult listAccessKeys(String userId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();
		if (StringUtils.isEmpty(userId))
		{
			return serviceCallResult;
		}

		// 过滤结果，并且去掉无需返回的参数
		List<AccessKey> accessKeyList = AccessKeyRepository.listAccessKeys(userId);
		List<JSONObject> accessKeyWithoutSecretKeyList = new ArrayList<JSONObject>();
		for (AccessKey accessKey : accessKeyList)
		{
			accessKeyWithoutSecretKeyList.add(JSONObject.fromObject(accessKey).discard("secretAccessKey"));
		}
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(JSONArray.fromObject(accessKeyWithoutSecretKeyList).toString());
		return serviceCallResult;
	}
}
