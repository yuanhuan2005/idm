package com.tcl.idm.model;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月4日 下午3:50:25
 */
public class HttpCodeErrorCodeRela
{
	protected int httpResultCode;

	protected String errorCode;

	protected String errorMessage;

	public int getHttpResultCode()
	{
		return httpResultCode;
	}

	public void setHttpResultCode(int httpResultCode)
	{
		this.httpResultCode = httpResultCode;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString()
	{
		return "AuthenticationResult [httpResultCode=" + httpResultCode + ", errorCode=" + errorCode
		        + ", errorMessage=" + errorMessage + "]";
	}

}
