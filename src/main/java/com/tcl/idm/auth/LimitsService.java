package com.tcl.idm.auth;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IpaddressRequestLimit;
import com.tcl.idm.model.UserRequestLimit;
import com.tcl.idm.repository.IpaddressRequestLimitRepository;
import com.tcl.idm.repository.UserRequestLimitRepository;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.LimitsUtils;

/**
 * 系统限制服务类
 * 
 * @author yuanhuan
 * 2014年3月28日 下午4:08:02
 */
public class LimitsService
{

	@SuppressWarnings("unused")
	final static private Log DEBUGGER = LogFactory.getLog(LimitsService.class);

	/** 
	 * 创建是否超出IP请求限制
	 * 
	 * @param ipaddress
	 * @return
	 */
	private static AuthenticationResult checkIpaddressRequestLimit(String ipaddress)
	{
		AuthenticationResult ipaddrRequestCheckResult = new AuthenticationResult();
		int maxTimesOfSameIPRequestPerMinute = LimitsUtils.getMaxTimesOfSameIPRequestPerMinute();
		String realCurrentMinute = DateFormatterUtils.getCurrentUTCMinuteTime();
		IpaddressRequestLimitRepository.deleteIpaddressRequestLimitExceptCurrentMinute(realCurrentMinute);
		int requestTimes = IpaddressRequestLimitRepository.getIpaddressRequestLimitCountByCurrentMinute(ipaddress,
		        realCurrentMinute);

		if (requestTimes >= maxTimesOfSameIPRequestPerMinute)
		{
			// 超出了请求限制，则拒绝请求
			ipaddrRequestCheckResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
			ipaddrRequestCheckResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
			ipaddrRequestCheckResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			return ipaddrRequestCheckResult;
		}

		// 没有超出请求限制
		IpaddressRequestLimit ipaddressRequestLimit = new IpaddressRequestLimit();
		ipaddressRequestLimit.setIpaddress(ipaddress);
		ipaddressRequestLimit.setRequestTimes(1);
		ipaddressRequestLimit.setCurrentMinute(realCurrentMinute);
		IpaddressRequestLimitRepository.createIpaddressRequestLimit(ipaddressRequestLimit);

		// 设置返回值
		ipaddrRequestCheckResult.setErrorCode(CustomErrorCode.Success.getCode());
		ipaddrRequestCheckResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		ipaddrRequestCheckResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return ipaddrRequestCheckResult;
	}

	/** 
	 * 创建是否超出用户请求限制
	 * 
	 * @param userId
	 * @return
	 */
	private static AuthenticationResult checkUserRequestLimit(String userId)
	{
		AuthenticationResult ipaddrRequestCheckResult = new AuthenticationResult();
		int maxTimesOfSameUserAccessSameServicePerMinute = LimitsUtils
		        .getMaxTimesOfSameUserAccessSameServicePerMinute();
		String realCurrentMinute = DateFormatterUtils.getCurrentUTCMinuteTime();
		UserRequestLimitRepository.deleteUserRequestLimitExceptCurrentMinute(realCurrentMinute);
		int requestTimes = UserRequestLimitRepository
		        .getUserRequestLimitCountByCurrentMinute(userId, realCurrentMinute);

		UserRequestLimit userRequestLimit = new UserRequestLimit();
		if (requestTimes >= maxTimesOfSameUserAccessSameServicePerMinute)
		{
			// 超出了请求限制，则拒绝请求
			ipaddrRequestCheckResult.setErrorCode(CustomErrorCode.LimitExceeded.getCode());
			ipaddrRequestCheckResult.setErrorMessage(CustomErrorCode.LimitExceeded.getMessage());
			ipaddrRequestCheckResult.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			return ipaddrRequestCheckResult;
		}

		// 没有超出请求限制
		userRequestLimit.setUserId(userId);
		userRequestLimit.setRequestTimes(1);
		userRequestLimit.setCurrentMinute(realCurrentMinute);
		UserRequestLimitRepository.createUserRequestLimit(userRequestLimit);

		// 设置返回值
		ipaddrRequestCheckResult.setErrorCode(CustomErrorCode.Success.getCode());
		ipaddrRequestCheckResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		ipaddrRequestCheckResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return ipaddrRequestCheckResult;
	}

	/**
	 * 检查客户端请求限制
	 * 
	 * @return
	 */
	public static AuthenticationResult checkClientRequestLimit(String userId, String ipaddress)
	{
		// 检查IP请求限制
		AuthenticationResult clientRequestCheckResult = LimitsService.checkIpaddressRequestLimit(ipaddress);
		if (!CustomErrorCode.Success.getCode().equals(clientRequestCheckResult.getErrorCode()))
		{
			return clientRequestCheckResult;
		}

		// 检查用户请求限制
		clientRequestCheckResult = LimitsService.checkUserRequestLimit(userId);
		if (!CustomErrorCode.Success.getCode().equals(clientRequestCheckResult.getErrorCode()))
		{
			return clientRequestCheckResult;
		}

		return clientRequestCheckResult;
	}
}
