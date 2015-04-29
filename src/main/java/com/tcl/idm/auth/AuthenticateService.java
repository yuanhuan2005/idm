package com.tcl.idm.auth;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AccessKeyStatus;
import com.tcl.idm.model.AuthType;
import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IdmHttpReq;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.util.HttpRequestUtils;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月4日 下午3:52:24
 */
public class AuthenticateService
{

	final static private Log DEBUGGER = LogFactory.getLog(AuthenticateService.class);

	private static AuthenticationResult checkUser(String accountId, String userId, AuthType authType)
	{
		AuthenticationResult authenticationResponse = new AuthenticationResult();
		authenticationResponse.setHttpResultCode(HttpServletResponse.SC_OK);
		authenticationResponse.setErrorCode(CustomErrorCode.Success.getCode());
		authenticationResponse.setErrorMessage(CustomErrorCode.Success.getMessage());

		if (StringUtils.isNotEmpty(userId))
		{
			User user = UserRepository.getUser(userId);
			if (null == user)
			{
				authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
				authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
				authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
				return authenticationResponse;
			}

			// 检查用户是否属于该账户
			if (!accountId.equals(user.getAccountId()))
			{
				authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
				authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
				authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
				return authenticationResponse;
			}
		}

		return authenticationResponse;
	}

	/**
	 * 对接口请求参数进行认证 
	 * 
	 * @param request
	 * @return 认证结果
	 */
	public static AuthenticationResult auth(IdmHttpReq idmHttpReq)
	{
		AuthenticateService.DEBUGGER.debug("enter auth");
		AuthenticationResult authenticationResponse = new AuthenticationResult();
		String userId = "";

		String twsAccessKeyId = HttpRequestUtils.getParamValue(idmHttpReq, "twsAccessKeyId");
		if (StringUtils.isEmpty(twsAccessKeyId))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.MissingParameter.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.MissingParameter.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			return authenticationResponse;
		}

		// 查询AccessKey
		AccessKey twsAccessKey = AccessKeyRepository.getAccessKey(twsAccessKeyId);
		if (null == twsAccessKey)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			return authenticationResponse;
		}

		// 检查AccessKey是否激活
		if (!AccessKeyStatus.ACTIVE.equals(twsAccessKey.getStatus()))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.AccessKeyInactive.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.AccessKeyInactive.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			return authenticationResponse;
		}

		// 获取当前用户信息
		User twsAccessKeyUser = UserRepository.getUser(twsAccessKey.getOwnerId());
		if (null == twsAccessKeyUser)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			return authenticationResponse;
		}

		// 判断是否只有Account类型的用户才可以操作
		switch (idmHttpReq.getAuthType())
		{
			case AccountOnly:
				// 检查当前用户是否为Account类型
				if (!(UserType.ACCOUNT.equals(twsAccessKeyUser.getUserType())))
				{
					authenticationResponse.setErrorCode(CustomErrorCode.AuthFailure.getCode());
					authenticationResponse.setErrorMessage(CustomErrorCode.AuthFailure.getMessage());
					authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
					AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
					        + authenticationResponse.getErrorMessage());
					return authenticationResponse;
				}
				break;

			case UserOnly:
				// 检查当前用户是否为User
				if (!(UserType.USER.equals(twsAccessKeyUser.getUserType())))
				{
					authenticationResponse.setErrorCode(CustomErrorCode.AuthFailure.getCode());
					authenticationResponse.setErrorMessage(CustomErrorCode.AuthFailure.getMessage());
					authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
					AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
					        + authenticationResponse.getErrorMessage());
					return authenticationResponse;
				}
				break;

			case InnerAdminOnly:
				// 检查当前用户是否为超级管理员
				if (!(UserType.ADMIN.equals(twsAccessKeyUser.getUserType())))
				{
					authenticationResponse.setErrorCode(CustomErrorCode.AuthFailure.getCode());
					authenticationResponse.setErrorMessage(CustomErrorCode.AuthFailure.getMessage());
					authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
					return authenticationResponse;
				}
				break;

			case BothUserAndAccount:
				// 检查当前用户是否为User或者Account
				if (!(UserType.USER.equals(twsAccessKeyUser.getUserType()) || UserType.ACCOUNT.equals(twsAccessKeyUser
				        .getUserType())))
				{
					authenticationResponse.setErrorCode(CustomErrorCode.AuthFailure.getCode());
					authenticationResponse.setErrorMessage(CustomErrorCode.AuthFailure.getMessage());
					authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
					AuthenticateService.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
					        + authenticationResponse.getErrorMessage());
					return authenticationResponse;
				}
				break;

			default:
				break;
		}

		// 获取用户ID并检查用户是否属于该账户
		AuthenticationResult checkResult = null;
		userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
		if (StringUtils.isNotEmpty(userId))
		{
			checkResult = AuthenticateService.checkUser(twsAccessKey.getOwnerId(), userId, idmHttpReq.getAuthType());
			if (!CustomErrorCode.Success.getCode().equals(checkResult.getErrorCode()))
			{
				AuthenticateService.DEBUGGER.error(checkResult.getErrorCode() + ": " + checkResult.getErrorMessage());
				return checkResult;
			}
		}
		else
		{
			// 获取账户ID并检查该账户是否属于系统超级管理员
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "accountId");
			checkResult = AuthenticateService.checkUser(twsAccessKey.getOwnerId(), userId, idmHttpReq.getAuthType());
			if (!CustomErrorCode.Success.getCode().equals(checkResult.getErrorCode()))
			{
				AuthenticateService.DEBUGGER.error(checkResult.getErrorCode() + ": " + checkResult.getErrorMessage());
				return checkResult;
			}
		}

		// 设置用户ID
		if (StringUtils.isEmpty(userId))
		{
			userId = twsAccessKey.getOwnerId();
		}

		// 检查数字签名
		AuthenticationResult signCheckResult = SignatureService.checkSignature(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equals(signCheckResult.getErrorCode()))
		{
			AuthenticateService.DEBUGGER.error(signCheckResult.getErrorCode() + ": "
			        + signCheckResult.getErrorMessage());
			return signCheckResult;
		}

		// 检查客户端请求限制
		AuthenticationResult clientRequestCheckResult = LimitsService.checkClientRequestLimit(
		        twsAccessKey.getOwnerId(), idmHttpReq.getClientIpAddr());
		if (!CustomErrorCode.Success.getCode().equals(clientRequestCheckResult.getErrorCode()))
		{
			AuthenticateService.DEBUGGER.error(clientRequestCheckResult.getErrorCode() + ": "
			        + clientRequestCheckResult.getErrorMessage());
			return clientRequestCheckResult;
		}

		authenticationResponse.setErrorCode(CustomErrorCode.Success.getCode());
		authenticationResponse.setErrorMessage(CustomErrorCode.Success.getMessage());
		authenticationResponse.setHttpResultCode(HttpServletResponse.SC_OK);
		authenticationResponse.setAccountId(twsAccessKey.getOwnerId());
		authenticationResponse.setUserId(userId);
		AuthenticateService.DEBUGGER.debug(authenticationResponse.getErrorCode() + ": "
		        + authenticationResponse.getErrorMessage());
		AuthenticateService.DEBUGGER.debug("end auth");
		return authenticationResponse;
	}
}
