package com.tcl.idm.model;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月24日 下午1:03:40
 */
public class IdmHttpReq
{
	private String queryString;

	private String postData;

	private String host;

	private String uri;

	private String httpMethod;

	private String clientIpAddr;

	private AuthType authType;

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	public String getPostData()
	{
		return postData;
	}

	public void setPostData(String postData)
	{
		this.postData = postData;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public String getHttpMethod()
	{
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod)
	{
		this.httpMethod = httpMethod;
	}

	public String getClientIpAddr()
	{
		return clientIpAddr;
	}

	public void setClientIpAddr(String clientIpAddr)
	{
		this.clientIpAddr = clientIpAddr;
	}

	public AuthType getAuthType()
	{
		return authType;
	}

	public void setAuthType(AuthType authType)
	{
		this.authType = authType;
	}

	@Override
	public String toString()
	{
		return "IdmHttpReq [queryString=" + queryString + ", postData=" + postData + ", host=" + host + ", uri=" + uri
		        + ", httpMethod=" + httpMethod + ", clientIpAddr=" + clientIpAddr + ", authType=" + authType + "]";
	}

}
