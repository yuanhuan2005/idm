package com.tcl.idm.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AccessKeyStatus;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.InitResult;
import com.tcl.idm.model.User;
import com.tcl.idm.model.UserType;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.repository.UserRepository;
import com.tcl.idm.service.AccessKeyUtilService;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;

@Controller
@RequestMapping("/")
public class InitController
{
	final static private Log DEBUGGER = LogFactory.getLog(InitController.class);

	/**
	 * 初始化系统
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String init(HttpServletRequest request, HttpServletResponse response)
	{
		InitController.DEBUGGER.debug("enter init");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 安全检查
		String remoteHost = request.getRemoteHost();
		if (!"127.0.0.1".equals(remoteHost) && !"localhost".equals(remoteHost))
		{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			InitController.DEBUGGER.error(CustomErrorCode.AuthFailure.getMessage());
			InitController.DEBUGGER.debug("end init");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.AuthFailure.getCode(),
			        CustomErrorCode.AuthFailure.getMessage());
		}

		// 检查是否需要初始化系统
		List<User> adminUserList = UserRepository.listUsersByType(UserType.ADMIN);
		if (null != adminUserList && !adminUserList.isEmpty())
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			InitController.DEBUGGER.error(CustomErrorCode.EntityAlreadyExists.getMessage());
			InitController.DEBUGGER.debug("end init");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.EntityAlreadyExists.getCode(),
			        CustomErrorCode.EntityAlreadyExists.getMessage());
		}

		// 创建内置超级管理员
		User adminUser = new User();
		adminUser.setUserId(UUID.randomUUID().toString());
		adminUser.setUserName("admin");
		adminUser.setUserType(UserType.ADMIN);
		String currTime = DateFormatterUtils.getCurrentUTCTime();
		adminUser.setCreateDate(currTime);
		String adminUserPassword = CommonService.randomString(8);
		adminUser.setPassword(AESUtils.encrypt(adminUserPassword));
		adminUser.setAccountId("");
		boolean createUserResult = UserRepository.createUser(adminUser);
		String adminUserAccessKeyId = CommonService.randomString(AccessKeyUtilService.ACCESS_KEY_LEN);
		String adminUserSecretAccessKey = CommonService.randomString(AccessKeyUtilService.SECRET_ACCESS_KEY_LEN);
		if (createUserResult)
		{
			// 为其创建AccessKey
			AccessKey adminUserAccessKey = new AccessKey();
			adminUserAccessKey.setAccessKeyId(adminUserAccessKeyId);
			adminUserAccessKey.setSecretAccessKey(AESUtils.encrypt(adminUserSecretAccessKey));
			adminUserAccessKey.setCreateDate(currTime);
			adminUserAccessKey.setStatus(AccessKeyStatus.ACTIVE);
			adminUserAccessKey.setOwnerId(adminUser.getUserId());
			boolean createAccessKeyResult = AccessKeyRepository.createAccessKey(adminUserAccessKey);
			if (!createAccessKeyResult)
			{
				UserRepository.deleteUser(adminUser.getUserId());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				InitController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
				InitController.DEBUGGER.debug("end init");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
				        CustomErrorCode.InternalError.getMessage());
			}
		}
		else
		{
			UserRepository.deleteUser(adminUser.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			InitController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			InitController.DEBUGGER.debug("end init");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 创建内置用户
		User innerUser = new User();
		innerUser.setUserId(UUID.randomUUID().toString());
		innerUser.setUserName("apiserver");
		innerUser.setUserType(UserType.INNER);
		currTime = DateFormatterUtils.getCurrentUTCTime();
		innerUser.setCreateDate(currTime);
		String innerUserPassword = CommonService.randomString(8);
		innerUser.setPassword(AESUtils.encrypt(innerUserPassword));
		innerUser.setAccountId("");
		createUserResult = UserRepository.createUser(innerUser);
		String innerUserAccessKeyId = CommonService.randomString(AccessKeyUtilService.ACCESS_KEY_LEN);
		String innerUserSecretAccessKey = CommonService.randomString(AccessKeyUtilService.SECRET_ACCESS_KEY_LEN);
		if (createUserResult)
		{
			// 为其创建AccessKey
			AccessKey innerUserAccessKey = new AccessKey();
			innerUserAccessKey.setAccessKeyId(innerUserAccessKeyId);
			innerUserAccessKey.setSecretAccessKey(AESUtils.encrypt(innerUserSecretAccessKey));
			innerUserAccessKey.setCreateDate(currTime);
			innerUserAccessKey.setStatus(AccessKeyStatus.ACTIVE);
			innerUserAccessKey.setOwnerId(innerUser.getUserId());
			boolean createAccessKeyResult = AccessKeyRepository.createAccessKey(innerUserAccessKey);
			if (!createAccessKeyResult)
			{
				UserRepository.deleteUser(innerUser.getUserId());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				InitController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
				InitController.DEBUGGER.debug("end init");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
				        CustomErrorCode.InternalError.getMessage());
			}
		}
		else
		{
			UserRepository.deleteUser(innerUser.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			InitController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			InitController.DEBUGGER.debug("end init");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 设置返回值
		InitResult initResult = new InitResult();
		initResult.setAdminUserId(adminUser.getUserId());
		initResult.setAdminUserName(adminUser.getUserName());
		initResult.setAdminPassword(adminUserPassword);
		initResult.setAdminAccessKeyId(adminUserAccessKeyId);
		initResult.setAdminSecretAccessKey(adminUserSecretAccessKey);
		initResult.setInnerUserId(innerUser.getUserId());
		initResult.setInnerUserName(innerUser.getUserName());
		initResult.setInnerPassword(innerUserPassword);
		initResult.setInnerAccessKeyId(innerUserAccessKeyId);
		initResult.setInnerSecretAccessKey(innerUserSecretAccessKey);
		InitController.DEBUGGER.debug("success");
		InitController.DEBUGGER.debug("end init");
		return JSONObject.fromObject(initResult).toString();
	}

}
