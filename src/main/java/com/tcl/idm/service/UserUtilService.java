package com.tcl.idm.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.LimitsUtils;

public class UserUtilService
{
	final static private Log DEBUGGER = LogFactory.getLog(UserUtilService.class);

	/**
	 * 检查密码是否合法
	 * 
	 * @param password
	 * @return true表示合法，false表示不合法
	 */
	private static boolean isPasswordValid(String password)
	{
		if (StringUtils.isEmpty(password))
		{
			return false;
		}

		// 至少6位
		if (password.length() < 6)
		{
			return false;
		}

		return true;
	}

	/**
	 * 创建User
	 * 
	 * @param user 用户对象
	 * @return
	 */
	public static ServiceCallResult createUser(User user)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 检查用户名是否冲突。在同一个账户下，用户名不能重复。
		List<User> userList = UserRepository.listUsers(user.getAccountId());
		for (User u : userList)
		{
			if (user.getUserName().equals(u.getUserName()))
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
				serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;

			}
		}

		// 检查账户的User个数限制是否达到
		if (UserType.USER.equals(user.getUserType()))
		{
			int maxNumberUsersPerAccount = LimitsUtils.getMaxNumberUsersPerAccount();
			if (null != userList)
			{
				int userListSize = userList.size();
				if (userListSize >= maxNumberUsersPerAccount)
				{
					serviceCallResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
					serviceCallResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
					serviceCallResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
					serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
					        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
					return serviceCallResult;
				}
			}
		}

		// 如果密码非空，检查是否合法
		if (StringUtils.isNotEmpty(user.getPassword()))
		{
			if (!UserUtilService.isPasswordValid(AESUtils.decrypt(user.getPassword())))
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
				serviceCallResult.setErrorCode(CustomErrorCode.InvalidParameter.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.InvalidParameter.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;
			}
		}

		// 创建用户
		boolean result = UserRepository.createUser(user);
		if (!result)
		{
			return serviceCallResult;
		}

		// 构造响应消息
		String resultJsonString = JSONObject.fromObject(user).discard("password").discard("userType").toString();
		UserUtilService.DEBUGGER.debug(user.getUserName() + " is created successfully.");
		serviceCallResult.setResultJsonString(resultJsonString);
		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return serviceCallResult;
	}

	/**
	 * 删除User
	 * 
	 * @param userId 用户ID
	 * @return
	 */
	public static ServiceCallResult deleteUser(String userId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(userId))
		{
			UserUtilService.DEBUGGER.error("userId is null");
			return serviceCallResult;
		}

		// 检查User是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 删除用户
		boolean result = UserRepository.deleteUser(userId);
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
	 * 获取User信息
	 * 
	 * @param userId 用户ID
	 * @return
	 */
	public static ServiceCallResult getUser(String userId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(userId))
		{
			UserUtilService.DEBUGGER.error("userId is null");
			return serviceCallResult;
		}

		// 检查User是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		String jsonString = JSONObject.fromObject(user).discard("userType").discard("userPolicy").discard("password")
		        .toString();
		serviceCallResult.setResultJsonString(jsonString);
		return serviceCallResult;
	}

	/**
	 * 更新User用户名
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @return
	 */
	public static ServiceCallResult updateUser(String userId, String userName)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName))
		{
			UserUtilService.DEBUGGER.error("userId or userName is null");
			return serviceCallResult;
		}

		// 检查User是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查用户名是否冲突。在同一个账户下，用户名不能重复。
		List<User> userList = UserRepository.listUsers(user.getAccountId());
		for (User u : userList)
		{
			if (userName.equals(u.getUserName()))
			{
				serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
				serviceCallResult.setErrorCode(CustomErrorCode.EntityAlreadyExists.getCode());
				serviceCallResult.setErrorMessage(CustomErrorCode.EntityAlreadyExists.getMessage());
				serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
				        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
				return serviceCallResult;

			}
		}

		// 更新userName
		user.setUserName(userName);
		boolean result = UserRepository.updateUser(userId, userName);
		if (!result)
		{
			return serviceCallResult;
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		String jsonString = JSONObject.fromObject(user).discard("userType").discard("userPolicy").discard("password")
		        .toString();
		serviceCallResult.setResultJsonString(jsonString);
		return serviceCallResult;
	}

	/**
	 * 查询User列表
	 * 
	 * @param accountId 账户ID
	 * @return
	 */
	public static ServiceCallResult listUsers(String accountId)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		// 过滤结果，并且去掉无需返回的参数
		List<User> userList = UserRepository.listUsers(accountId);
		List<JSONObject> userWithoutSecretKeyList = new ArrayList<JSONObject>();
		for (User user : userList)
		{
			userWithoutSecretKeyList.add(JSONObject.fromObject(user).discard("userType").discard("userPolicy")
			        .discard("password"));
		}

		serviceCallResult.setErrorCode(CustomErrorCode.Success.getCode());
		serviceCallResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		serviceCallResult.setHttpResultCode(HttpServletResponse.SC_OK);
		serviceCallResult.setResultJsonString(JSONArray.fromObject(userWithoutSecretKeyList).toString());
		return serviceCallResult;
	}

	/**
	 * 更新User用户名
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @return
	 */
	public static ServiceCallResult changePassword(String userId, String oldPassword, String newPassword)
	{
		ServiceCallResult serviceCallResult = new ServiceCallResult();

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword))
		{
			UserUtilService.DEBUGGER.error("userId oldPassword or newPassword is null");
			return serviceCallResult;
		}

		// 检查User是否存在
		User user = UserRepository.getUser(userId);
		if (null == user)
		{
			serviceCallResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 检查旧密码是否匹配
		if (!oldPassword.equals(AESUtils.decrypt(user.getPassword())))
		{
			serviceCallResult.setErrorCode(CustomErrorCode.PasswordNotMatch.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.PasswordNotMatch.getMessage());
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 如果密码非空，检查是否合法
		if (!UserUtilService.isPasswordValid(newPassword))
		{
			serviceCallResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			serviceCallResult.setErrorCode(CustomErrorCode.InvalidParameter.getCode());
			serviceCallResult.setErrorMessage(CustomErrorCode.InvalidParameter.getMessage());
			serviceCallResult.setResultJsonString(IdmErrorMessageUtils.genErrorMessageInJson(
			        serviceCallResult.getErrorCode(), serviceCallResult.getErrorMessage()));
			return serviceCallResult;
		}

		// 更新密码
		boolean result = UserRepository.updateUserPassword(userId, AESUtils.encrypt(newPassword));
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
