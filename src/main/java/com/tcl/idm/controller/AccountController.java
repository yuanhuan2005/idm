package com.tcl.idm.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tcl.idm.auth.AuthenticateService;
import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AccessKeyStatus;
import com.tcl.idm.model.Account;
import com.tcl.idm.model.AuthType;
import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IdmHttpReq;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.service.AccessKeyUtilService;
import com.tcl.idm.service.UserUtilService;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.HttpRequestUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;

@Controller
@RequestMapping("/")
public class AccountController
{
	final static private Log DEBUGGER = LogFactory.getLog(AccountController.class);

	/**
	 * 创建Account
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/createAccount", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String createAccount(HttpServletRequest request, HttpServletResponse response)
	{
		AccountController.DEBUGGER.debug("enter createAccount");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.InnerAdminOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			AccountController.DEBUGGER.error(authResult.getErrorMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取账户名
		String accountName = "";
		try
		{
			accountName = HttpRequestUtils.getParamValue(idmHttpReq, "accountName");
			if (StringUtils.isEmpty(accountName))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				AccountController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				AccountController.DEBUGGER.debug("end createAccount");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			AccountController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取密码
		String password = "";
		try
		{
			password = HttpRequestUtils.getParamValue(idmHttpReq, "password");
			if (StringUtils.isEmpty(password))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				AccountController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				AccountController.DEBUGGER.debug("end createAccount");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			AccountController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 生成accountId，并检查accountId是否已经存在,如果存在的话，则需要重新生成新的accountId
		String accountId = UUID.randomUUID().toString();
		boolean accountIdExistedFlag = (null != UserRepository.getUser(accountId));
		while (accountIdExistedFlag)
		{
			accountId = UUID.randomUUID().toString();
			accountIdExistedFlag = (null != UserRepository.getUser(accountId));
		}

		// 为账户创建一对新的Account
		User accountUser = new User();
		accountUser.setUserId(accountId);
		accountUser.setUserName(accountName);
		accountUser.setAccountId(authResult.getAccountId());
		accountUser.setPassword(AESUtils.encrypt(password));
		accountUser.setUserType(UserType.ACCOUNT);
		accountUser.setCreateDate(DateFormatterUtils.getCurrentUTCTime());
		ServiceCallResult serviceCallResult = UserUtilService.createUser(accountUser);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			AccountController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 为账户创建一对默认的AccessKey，用于后续的操作
		AccessKey accessKey = new AccessKey();
		String newAccessKeyId = CommonService.randomString(AccessKeyUtilService.ACCESS_KEY_LEN);
		String newSecretAccessKey = CommonService.randomString(AccessKeyUtilService.SECRET_ACCESS_KEY_LEN);
		accessKey.setAccessKeyId(newAccessKeyId);
		accessKey.setSecretAccessKey(AESUtils.encrypt(newSecretAccessKey));
		accessKey.setStatus(AccessKeyStatus.ACTIVE);
		accessKey.setOwnerId(accountUser.getUserId());
		accessKey.setCreateDate(DateFormatterUtils.getCurrentUTCTime());
		boolean createAccessKeyResult = AccessKeyRepository.createAccessKey(accessKey);
		if (!createAccessKeyResult)
		{
			// 创建失败则回滚
			UserRepository.deleteUser(accountUser.getUserId());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			AccountController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());

		}

		// 生成响应消息
		Account account = accountUser.convertToAccount();
		account.setDefaultAccessKeyId(accessKey.getAccessKeyId());
		account.setDefaultSecretAccessKey(AESUtils.decrypt(accessKey.getSecretAccessKey()));
		String resultJsonString = JSONObject.fromObject(account).discard("password").toString();

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			AccountController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			AccountController.DEBUGGER.debug("end createAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		AccountController.DEBUGGER.debug("end createAccount");
		return resultJsonString;
	}

	/**
	 * 删除Account
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/deleteAccount", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String deleteAccount(HttpServletRequest request, HttpServletResponse response)
	{
		AccountController.DEBUGGER.debug("enter deleteAccount");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.InnerAdminOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			AccountController.DEBUGGER.error(authResult.getErrorMessage());
			AccountController.DEBUGGER.debug("end deleteAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		String accountId = "";
		try
		{
			accountId = HttpRequestUtils.getParamValue(idmHttpReq, "accountId");
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			AccountController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			AccountController.DEBUGGER.debug("end deleteAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 删除Account
		ServiceCallResult serviceCallResult = UserUtilService.deleteUser(accountId);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			AccountController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			AccountController.DEBUGGER.debug("end deleteAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			AccountController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			AccountController.DEBUGGER.debug("end deleteAccount");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		AccountController.DEBUGGER.debug("end deleteAccount");
		return serviceCallResult.getResultJsonString();
	}

}
