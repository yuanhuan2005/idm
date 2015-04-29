package com.tcl.idm.model;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月4日 下午3:50:25
 */
public class AuthenticationResult extends HttpCodeErrorCodeRela
{
	public AuthenticationResult()
	{
		errorCode = CustomErrorCode.InternalError.getCode();
		errorMessage = CustomErrorCode.InternalError.getMessage();
		httpResultCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		accountId = "";
		userId = "";
	}

	private String accountId;

	private String userId;

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	@Override
	public String toString()
	{
		return "AuthenticationResult [accountId=" + accountId + ", userId=" + userId + ", httpResultCode="
		        + httpResultCode + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}

}
