package com.tcl.idm.controller;

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
import com.tcl.idm.service.GroupUtilService;
import com.tcl.idm.util.HttpRequestUtils;
import com.tcl.idm.util.IdmErrorMessageUtils;

@Controller
@RequestMapping("/")
public class GroupController
{
	final static private Log DEBUGGER = LogFactory.getLog(GroupController.class);

	/**
	 * 创建Group
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/createGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String createGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter createGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end createGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取Group名
		String groupName = "";
		try
		{
			groupName = HttpRequestUtils.getParamValue(idmHttpReq, "groupName");
			if (StringUtils.isEmpty(groupName))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end createGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end createUser");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取Group描述
		String description = "";
		try
		{
			description = HttpRequestUtils.getParamValue(idmHttpReq, "description");
			if (StringUtils.isEmpty(description))
			{
				description = "";
			}
		}
		catch (Exception e)
		{
		}

		// 为用户创建一对新的Group
		ServiceCallResult operateOpenIDMServiceResult = GroupUtilService.createGroup(authResult.getAccountId(),
		        groupName, description);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end createGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end createGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end createGroup");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 删除Group
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/deleteGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String deleteGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter deleteGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end deleteGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取groupId
		String groupId = "";
		try
		{
			groupId = HttpRequestUtils.getParamValue(idmHttpReq, "groupId");
			if (StringUtils.isEmpty(groupId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end createGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end deleteGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 删除Group
		ServiceCallResult operateOpenIDMServiceResult = GroupUtilService
		        .deleteGroup(authResult.getAccountId(), groupId);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end deleteGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end deleteGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end deleteGroup");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 更新Group状态
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/updateGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String updateGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter updateGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end updateGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取groupId
		String groupId = "";
		try
		{
			groupId = HttpRequestUtils.getParamValue(idmHttpReq, "groupId");
			if (StringUtils.isEmpty(groupId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end createGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end updateGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取Group描述
		String description = "";
		try
		{
			description = HttpRequestUtils.getParamValue(idmHttpReq, "description");
			if (StringUtils.isEmpty(description))
			{
				description = "";
			}
		}
		catch (Exception e)
		{
		}

		// 获取Group名
		String groupName = "";
		try
		{
			groupName = HttpRequestUtils.getParamValue(idmHttpReq, "groupName");
			if (StringUtils.isEmpty(groupName))
			{
				groupName = "";
			}
		}
		catch (Exception e)
		{
		}

		// 更新Group
		ServiceCallResult operateOpenIDMServiceResult = GroupUtilService.updateGroup(authResult.getAccountId(),
		        groupId, groupName, description);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end updateGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end updateGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end updateGroup");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 查询Group列表
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/listGroups", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String listGroups(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter listGroups");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end listGroups");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取用户ID
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				userId = "";
			}
		}
		catch (Exception e)
		{
		}

		// 更新Group
		ServiceCallResult operateOpenIDMServiceResult = GroupUtilService.listGroups(authResult.getAccountId(), userId);
		if (null == operateOpenIDMServiceResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end listGroups");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(operateOpenIDMServiceResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(operateOpenIDMServiceResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(operateOpenIDMServiceResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end listGroups");
			return IdmErrorMessageUtils.genErrorMessageInJson(operateOpenIDMServiceResult.getErrorCode(),
			        operateOpenIDMServiceResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end listGroups");
		return operateOpenIDMServiceResult.getResultJsonString();
	}

	/**
	 * 获取Group信息
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/getGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String getGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter getGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end getGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		String groupId = "";
		try
		{
			groupId = HttpRequestUtils.getParamValue(idmHttpReq, "groupId");
			if (StringUtils.isEmpty(groupId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end getGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end getGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取Group
		ServiceCallResult serviceCallResult = GroupUtilService.getGroup(authResult.getAccountId(), groupId);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end getGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end getGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end getGroup");
		return serviceCallResult.getResultJsonString();
	}

	/**
	 * 将用户添加到组中去
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/addUserToGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String addUserToGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter addUserToGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end addUserToGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取GroupID
		String groupId = "";
		try
		{
			groupId = HttpRequestUtils.getParamValue(idmHttpReq, "groupId");
			if (StringUtils.isEmpty(groupId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end addUserToGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end addUserToGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取UserID
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end addUserToGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end addUserToGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 添加用户到组
		ServiceCallResult serviceCallResult = GroupUtilService.addUserToGroup(authResult.getAccountId(), userId,
		        groupId);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end addUserToGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end addUserToGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end addUserToGroup");
		return serviceCallResult.getResultJsonString();
	}

	/**
	 * 将用户添加到组中去
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return
	 */
	@RequestMapping(value = "/removeUserFromGroup", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String removeUserFromGroup(HttpServletRequest request, HttpServletResponse response)
	{
		GroupController.DEBUGGER.debug("enter removeUserFromGroup");
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
			GroupController.DEBUGGER.error(authResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end removeUserFromGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(authResult.getErrorCode(), authResult.getErrorMessage());
		}

		// 获取GroupID
		String groupId = "";
		try
		{
			groupId = HttpRequestUtils.getParamValue(idmHttpReq, "groupId");
			if (StringUtils.isEmpty(groupId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end removeUserFromGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end removeUserFromGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 获取UserID
		String userId = "";
		try
		{
			userId = HttpRequestUtils.getParamValue(idmHttpReq, "userId");
			if (StringUtils.isEmpty(userId))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				GroupController.DEBUGGER.error(CustomErrorCode.InvalidParameter.getMessage());
				GroupController.DEBUGGER.debug("end removeUserFromGroup");
				return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InvalidParameter.getCode(),
				        CustomErrorCode.InvalidParameter.getMessage());
			}
		}
		catch (Exception e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			GroupController.DEBUGGER.error(CustomErrorCode.MissingParameter.getMessage());
			GroupController.DEBUGGER.debug("end removeUserFromGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.MissingParameter.getCode(),
			        CustomErrorCode.MissingParameter.getMessage());
		}

		// 从组中删除用户
		ServiceCallResult serviceCallResult = GroupUtilService.removeUserFromGroup(authResult.getAccountId(), userId,
		        groupId);
		if (null == serviceCallResult)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			GroupController.DEBUGGER.error(CustomErrorCode.InternalError.getMessage());
			GroupController.DEBUGGER.debug("end removeUserFromGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(CustomErrorCode.InternalError.getCode(),
			        CustomErrorCode.InternalError.getMessage());
		}

		// 处理结果
		response.setStatus(serviceCallResult.getHttpResultCode());
		if (!CustomErrorCode.Success.getCode().equalsIgnoreCase(serviceCallResult.getErrorCode()))
		{
			GroupController.DEBUGGER.error(serviceCallResult.getErrorMessage());
			GroupController.DEBUGGER.debug("end removeUserFromGroup");
			return IdmErrorMessageUtils.genErrorMessageInJson(serviceCallResult.getErrorCode(),
			        serviceCallResult.getErrorMessage());
		}

		GroupController.DEBUGGER.debug("end removeUserFromGroup");
		return serviceCallResult.getResultJsonString();
	}

}
