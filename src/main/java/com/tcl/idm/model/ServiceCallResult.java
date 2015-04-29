package com.tcl.idm.model;

import javax.servlet.http.HttpServletResponse;

import com.tcl.idm.util.IdmErrorMessageUtils;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月4日 下午3:50:25
 */
public class ServiceCallResult extends HttpCodeErrorCodeRela
{
	public ServiceCallResult()
	{
		errorCode = CustomErrorCode.InternalError.getCode();
		errorMessage = CustomErrorCode.InternalError.getMessage();
		httpResultCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		resultJsonString = "";
		resultJsonString = IdmErrorMessageUtils.genErrorMessageInJson(errorCode, errorMessage);

	}

	private String resultJsonString;

	public String getResultJsonString()
	{
		return resultJsonString;
	}

	public void setResultJsonString(String resultJsonString)
	{
		this.resultJsonString = resultJsonString;
	}

	@Override
	public String toString()
	{
		return "OperateOpenIDMServiceResult [resultJsonString=" + resultJsonString + ", httpResultCode="
		        + httpResultCode + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}
}
