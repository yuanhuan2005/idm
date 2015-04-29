package com.tcl.idm.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tcl.idm.auth.LimitsService;
import com.tcl.idm.auth.PolicyService;
import com.tcl.idm.auth.SignatureService;
import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AccessKeyStatus;
import com.tcl.idm.model.AuthenticateReq;
import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IdmHttpReq;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.HttpRequestUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.LimitsUtils;
import com.tcl.idm.util.SignatureUtils;

@Controller
@RequestMapping("/")
public class AuthenticateController
{
	final static private Log DEBUGGER = LogFactory.getLog(AuthenticateController.class);

	/**
	 * 从请求参数中获取认证参数
	 * 
	 * @param request
	 * @param postData
	 * @return
	 */
	private AuthenticateReq getAuthenticateReq(IdmHttpReq idmHttpReq)
	{
		AuthenticateReq authenticateReq = new AuthenticateReq();
		String policyResource = HttpRequestUtils.getParamValue(idmHttpReq, "policyResource");
		String reqAccessKeyId = HttpRequestUtils.getParamValue(idmHttpReq, "reqAccessKeyId");
		String reqClientIP = HttpRequestUtils.getParamValue(idmHttpReq, "reqClientIP");
		String reqSignature = HttpRequestUtils.getParamValue(idmHttpReq, "reqSignature");
		String reqSignatureMethod = HttpRequestUtils.getParamValue(idmHttpReq, "reqSignatureMethod");
		String reqSignatureVersion = HttpRequestUtils.getParamValue(idmHttpReq, "reqSignatureVersion");
		String reqTimestamp = HttpRequestUtils.getParamValue(idmHttpReq, "reqTimestamp");
		String signature = HttpRequestUtils.getParamValue(idmHttpReq, "signature");
		String signatureMethod = HttpRequestUtils.getParamValue(idmHttpReq, "signatureMethod");
		String signatureVersion = HttpRequestUtils.getParamValue(idmHttpReq, "signatureVersion");
		String timestamp = HttpRequestUtils.getParamValue(idmHttpReq, "timestamp");
		String toBeSignedString = HttpRequestUtils.getParamValue(idmHttpReq, "toBeSignedString");
		String twsAccessKeyId = HttpRequestUtils.getParamValue(idmHttpReq, "twsAccessKeyId");

		authenticateReq.setPolicyResource(policyResource);
		authenticateReq.setReqAccessKeyId(reqAccessKeyId);
		authenticateReq.setReqClientIP(reqClientIP);
		authenticateReq.setReqSignature(reqSignature);
		authenticateReq.setReqSignatureMethod(reqSignatureMethod);
		authenticateReq.setReqSignatureVersion(reqSignatureVersion);
		authenticateReq.setReqTimestamp(reqTimestamp);
		authenticateReq.setSignature(signature);
		authenticateReq.setSignatureMethod(signatureMethod);
		authenticateReq.setSignatureVersion(signatureVersion);
		authenticateReq.setTimestamp(timestamp);
		authenticateReq.setToBeSignedString(toBeSignedString);
		authenticateReq.setTwsAccessKeyId(twsAccessKeyId);

		return authenticateReq;
	}

	/**
	 * 对接口请求参数进行认证 
	 * 
	 * @param request
	 * @return 认证结果
	 */
	private static AuthenticationResult authApiServer(IdmHttpReq idmHttpReq)
	{
		AuthenticateController.DEBUGGER.debug("enter authApiServer");
		AuthenticationResult authenticationResponse = new AuthenticationResult();
		String userId = "";

		String twsAccessKeyId = HttpRequestUtils.getParamValue(idmHttpReq, "twsAccessKeyId");
		if (StringUtils.isEmpty(twsAccessKeyId))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.MissingParameter.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.MissingParameter.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return authenticationResponse;
		}

		// 当用户名为空的时候，表示为当前用户自己增加AccessKey，需要在数据库中查询用户名
		AccessKey twsAccessKey = AccessKeyRepository.getAccessKey(twsAccessKeyId);
		if (null == twsAccessKey)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return authenticationResponse;
		}

		// 检查AccessKey是否激活
		if (!AccessKeyStatus.ACTIVE.equals(twsAccessKey.getStatus()))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.AccessKeyInactive.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.AccessKeyInactive.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return authenticationResponse;
		}

		// 获取当前用户信息
		User twsAccessKeyUser = UserRepository.getUser(twsAccessKey.getOwnerId());
		if (null == twsAccessKeyUser)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return authenticationResponse;
		}

		// 检查用户是不是内部预留用户，只有API Server可以调用这个认证接口，并且API Server使用的是
		if (!UserType.INNER.equals(twsAccessKeyUser.getUserType()))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.AuthFailure.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.AuthFailure.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return authenticationResponse;
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
			AuthenticateController.DEBUGGER.error(signCheckResult.getErrorCode() + ": "
			        + signCheckResult.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authApiServer");
			return signCheckResult;
		}

		authenticationResponse.setErrorCode(CustomErrorCode.Success.getCode());
		authenticationResponse.setErrorMessage(CustomErrorCode.Success.getMessage());
		authenticationResponse.setHttpResultCode(HttpServletResponse.SC_OK);
		authenticationResponse.setAccountId(twsAccessKey.getOwnerId());
		authenticationResponse.setUserId(userId);
		AuthenticateController.DEBUGGER.debug(authenticationResponse.getErrorCode() + ": "
		        + authenticationResponse.getErrorMessage());
		AuthenticateController.DEBUGGER.debug("end authApiServer");
		return authenticationResponse;
	}

	/**
	 * 对用户客户端请求进行认证
	 * 
	 * @param authenticateReq
	 * @return
	 */
	private static AuthenticationResult authClient(AuthenticateReq authenticateReq)
	{
		AuthenticateController.DEBUGGER.debug("enter authClient");
		AuthenticationResult authenticationResponse = new AuthenticationResult();

		// 检查AccessKey是否存在
		String reqAccessKeyId = authenticateReq.getReqAccessKeyId();
		AccessKey reqAccessKey = AccessKeyRepository.getAccessKey(reqAccessKeyId);
		if (null == reqAccessKey)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authClient");
			return authenticationResponse;
		}

		// 检查AccessKey对应的用户是否存在
		User reqUser = UserRepository.getUser(reqAccessKey.getOwnerId());
		if (null == reqUser)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authClient");
			return authenticationResponse;
		}

		// 检查客户端请求限制
		String reqClientIP = authenticateReq.getReqClientIP();
		AuthenticationResult clientRequestCheckResult = LimitsService.checkClientRequestLimit(
		        reqAccessKey.getOwnerId(), reqClientIP);
		if (!CustomErrorCode.Success.getCode().equals(clientRequestCheckResult.getErrorCode()))
		{
			AuthenticateController.DEBUGGER.error(clientRequestCheckResult.getErrorCode() + ": "
			        + clientRequestCheckResult.getErrorMessage());
			return clientRequestCheckResult;
		}

		// 检查签名方法和版本是否匹配
		String reqSignatureMethod = authenticateReq.getReqSignatureMethod();
		String reqSignatureVersion = authenticateReq.getReqSignatureVersion();
		if (!SignatureService.SIGNATURE_METHOD.equals(reqSignatureMethod)
		        || !SignatureService.SIGNATURE_VERSION.equals(reqSignatureVersion))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.InvalidParameter.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.InvalidParameter.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authClient");
			return authenticationResponse;
		}

		// 检查时间戳
		String reqTimestamp = authenticateReq.getReqTimestamp();
		int timestampExpiredInMinutes = LimitsUtils.getTimestampExpiredInMinutes();
		Date timestampDate = DateFormatterUtils.convertStringToDate(reqTimestamp);
		long timestampDateMilliseconds = timestampDate.getTime();
		long timestampExpiredDateMilliseconds = timestampDate.getTime() + timestampExpiredInMinutes * 60 * 1000;
		long currentDateMilliseconds = DateFormatterUtils.convertStringToDate(DateFormatterUtils.getCurrentUTCTime())
		        .getTime();
		if (currentDateMilliseconds < timestampDateMilliseconds
		        || currentDateMilliseconds > timestampExpiredDateMilliseconds)
		{
			authenticationResponse.setErrorCode(CustomErrorCode.RequestExpired.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.RequestExpired.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authClient");
			return authenticationResponse;
		}

		// 检查数字签名
		String reqSignature = authenticateReq.getReqSignature();
		String toBeSignedString = authenticateReq.getToBeSignedString();
		String secretAccessKey = AESUtils.decrypt(reqAccessKey.getSecretAccessKey());
		String clientSignature = CommonService.getURLDecodeString(SignatureUtils.calculateRFC2104HMAC(toBeSignedString,
		        secretAccessKey));
		if (StringUtils.isEmpty(clientSignature) || !clientSignature.equals(reqSignature))
		{
			authenticationResponse.setErrorCode(CustomErrorCode.SignatureDoesNotMatch.getCode());
			authenticationResponse.setErrorMessage(CustomErrorCode.SignatureDoesNotMatch.getMessage());
			authenticationResponse.setHttpResultCode(HttpServletResponse.SC_FORBIDDEN);
			AuthenticateController.DEBUGGER.error(authenticationResponse.getErrorCode() + ": "
			        + authenticationResponse.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authClient");
			return authenticationResponse;
		}

		// 检查策略
		AuthenticationResult checkUserPolicyResult = PolicyService.checkUserPolicy(reqAccessKey.getOwnerId(),
		        authenticateReq.getPolicyResource());
		if (!CustomErrorCode.Success.getCode().equals(checkUserPolicyResult.getErrorCode()))
		{
			AuthenticateController.DEBUGGER.error(checkUserPolicyResult.getErrorCode() + ": "
			        + checkUserPolicyResult.getErrorMessage());
			return checkUserPolicyResult;
		}

		// 设置成功返回值
		authenticationResponse.setErrorCode(CustomErrorCode.Success.getCode());
		authenticationResponse.setErrorMessage(CustomErrorCode.Success.getMessage());
		authenticationResponse.setHttpResultCode(HttpServletResponse.SC_OK);
		authenticationResponse.setUserId(reqAccessKey.getOwnerId());
		AuthenticateController.DEBUGGER.debug(authenticationResponse.getErrorCode() + ": "
		        + authenticationResponse.getErrorMessage());
		AuthenticateController.DEBUGGER.debug("end authClient");
		return authenticationResponse;
	}

	/**
	 * authenticate
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String authenticate(HttpServletRequest request, HttpServletResponse response)
	{
		AuthenticateController.DEBUGGER.debug("enter authenticate");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, null);

		AuthenticateReq authenticateReq = getAuthenticateReq(idmHttpReq);
		if (!authenticateReq.checkRequiredArgumentsSuccess())
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			AuthenticateController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			AuthenticateController.DEBUGGER.debug("end authenticate");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateController.authApiServer(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			AuthenticateController.DEBUGGER.error(authResult.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authenticate");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 对API Server的认证已经成功，下面是对用户客户端请求进行认证，此时始终返回200状态码
		response.setStatus(HttpServletResponse.SC_OK);

		// 对用户客户端请求进行认证
		authResult = AuthenticateController.authClient(authenticateReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			AuthenticateController.DEBUGGER.error(authResult.getErrorMessage());
			AuthenticateController.DEBUGGER.debug("end authenticate");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		AuthenticateController.DEBUGGER.debug("end authenticate");
		return CommonService.genSuccesResultJsonString();
	}
}
