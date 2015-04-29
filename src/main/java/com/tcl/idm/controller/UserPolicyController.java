package com.tcl.idm.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tcl.idm.auth.AuthenticateService;
import com.tcl.idm.model.AuthType;
import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IdmHttpReq;
import com.tcl.idm.model.ServiceCallResult;
import com.tcl.idm.service.UserPolicyUtilService;
import com.tcl.idm.util.HttpRequestUtils;
import com.tcl.idm.util.IDMServiceUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;
import com.tcl.idm.util.PolicyUtils;

@Controller
@RequestMapping("/")
public class UserPolicyController
{
	final static private Log DEBUGGER = LogFactory.getLog(UserPolicyController.class);

	/**
	 * 创建用户Policy
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/createUserPolicy", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String createUserPolicy(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		UserPolicyController.DEBUGGER.debug("enter createUserPolicy");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.AccountOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			UserPolicyController.DEBUGGER.error(authResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取Policy名
		String policyName = "";
		try
		{
			policyName = HttpRequestUtils.getParamValue(idmHttpReq, "policyName");
			if (StringUtils.isEmpty(policyName))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end createUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取userId
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end createUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取policyDocument
		String policyDocument = "";
		try
		{
			policyDocument = HttpRequestUtils.getParamValue(idmHttpReq, "policyDocument");
			if (StringUtils.isEmpty(policyDocument))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end createUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 检查PolicyDocument是否合法
		if (StringUtils.isEmpty(policyDocument) || !PolicyUtils.isPolicyDocumentValid(policyDocument))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MalformedPolicyDocument.getMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MalformedPolicyDocument.getCode(),
			        CustomErrorCode.MalformedPolicyDocument.getMessage());
		}

		// 为用户创建一对新的Policy
		ServiceCallResult operateOpenIDMServiceResult = UserPolicyUtilService.createUserPolicy(userId, policyName,
		        IDMServiceUtils.convertToPolicyDocumentArray(policyDocument));
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			UserPolicyController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end createUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		UserPolicyController.DEBUGGER.debug("end createUserPolicy");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	* 更新用户Policy
	* 
	* @param request HTTP请求
	* @param response HTTP响应
	* @return
	*/
	@RequestMapping(value = "/updateUserPolicy", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String updateUserPolicy(HttpServletRequest request, HttpServletResponse response)
	{
		UserPolicyController.DEBUGGER.debug("enter updateUserPolicy");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.AccountOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			UserPolicyController.DEBUGGER.error(authResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取policyId
		String policyId = "";
		try
		{
			policyId = HttpRequestUtils.getParamValue(idmHttpReq, "policyId");
			if (StringUtils.isEmpty(policyId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取Policy名
		String policyName = "";
		try
		{
			policyName = HttpRequestUtils.getParamValue(idmHttpReq, "policyName");
		}
		catch (Exception e)
		{
			policyName = "";
		}

		// 获取userId
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取policyDocument
		String policyDocument = "";
		try
		{
			policyDocument = HttpRequestUtils.getParamValue(idmHttpReq, "policyDocument");
		}
		catch (Exception e)
		{
			policyDocument = "";
		}

		// 检查PolicyDocument是否合法
		if (StringUtils.isEmpty(policyDocument) || !PolicyUtils.isPolicyDocumentValid(policyDocument))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MalformedPolicyDocument.getMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MalformedPolicyDocument.getCode(),
			        CustomErrorCode.MalformedPolicyDocument.getMessage());
		}

		// 检查policyName和policyDocument至少一个非空
		if (StringUtils.isEmpty(policyName) && StringUtils.isEmpty(policyDocument))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 更新Policy
		ServiceCallResult operateOpenIDMServiceResult = UserPolicyUtilService.updateUserPolicy(userId, policyId,
		        policyName, IDMServiceUtils.convertToPolicyDocumentArray(policyDocument));
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			UserPolicyController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		UserPolicyController.DEBUGGER.debug("end updateUserPolicy");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 删除Policy
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/deleteUserPolicy", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String deleteUserPolicy(HttpServletRequest request, HttpServletResponse response)
	{
		UserPolicyController.DEBUGGER.debug("enter deleteUserPolicy");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.AccountOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			UserPolicyController.DEBUGGER.error(authResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取userId
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取policyId
		String policyId = "";
		try
		{
			policyId = HttpRequestUtils.getParamValue(idmHttpReq, "policyId");
			if (StringUtils.isEmpty(policyId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 删除Policy
		ServiceCallResult operateOpenIDMServiceResult = UserPolicyUtilService.deleteUserPolicy(userId, policyId);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			UserPolicyController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		UserPolicyController.DEBUGGER.debug("end deleteUserPolicy");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 查询Policy列表
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/listUserPolicys", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String listUserPolicys(HttpServletRequest request, HttpServletResponse response)
	{
		UserPolicyController.DEBUGGER.debug("enter listUserPolicys");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.AccountOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			UserPolicyController.DEBUGGER.error(authResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end listUserPolicys");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取userId
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end listUserPolicys");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end listUserPolicys");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 更新Policy
		ServiceCallResult operateOpenIDMServiceResult = UserPolicyUtilService.listUserPolicys(userId);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			UserPolicyController.DEBUGGER.debug("end listUserPolicys");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			UserPolicyController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end listUserPolicys");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		UserPolicyController.DEBUGGER.debug("end listUserPolicys");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 获取Policy信息
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/getUserPolicy", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String getUserPolicy(HttpServletRequest request, HttpServletResponse response)
	{
		UserPolicyController.DEBUGGER.debug("enter getUserPolicy");
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");

		// 获取IDM请求参数
		IdmHttpReq idmHttpReq = HttpRequestUtils.getIdmHttpReq(request, AuthType.AccountOnly);

		// 进行接口认证
		AuthenticationResult authResult = AuthenticateService.auth(idmHttpReq);
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(authResult.getErrorCode()))
		{
			response.setStatus(authResult.getHttpResultCode());
			UserPolicyController.DEBUGGER.error(authResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end getUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取策略ID
		String policyId = "";
		try
		{
			policyId = HttpRequestUtils.getParamValue(idmHttpReq, "policyId");
			if (StringUtils.isEmpty(policyId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end getUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end getUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取userId
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				UserPolicyController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				UserPolicyController.DEBUGGER.debug("end getUserPolicy");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			UserPolicyController.DEBUGGER.debug("end getUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取Policy
		ServiceCallResult serviceCallResult = UserPolicyUtilService.getUserPolicy(userId, policyId);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			UserPolicyController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			UserPolicyController.DEBUGGER.debug("end getUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			UserPolicyController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			UserPolicyController.DEBUGGER.debug("end getUserPolicy");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		UserPolicyController.DEBUGGER.debug("end getUserPolicy");
		return serviceCallResult.getResultJsonString();
	}
}
