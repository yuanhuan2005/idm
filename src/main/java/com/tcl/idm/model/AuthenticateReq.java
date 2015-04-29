package com.tcl.idm.model;

import org.apache.commons.lang.StringUtils;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月18日 下午3:38:34
 */
public class AuthenticateReq extends SignatureParam
{
	private String toBeSignedString;

	private String policyResource;

	private String reqSignature;

	private String reqAccessKeyId;

	private String reqSignatureMethod;

	private String reqSignatureVersion;

	private String reqTimestamp;

	private String reqClientIP;

	public String getToBeSignedString()
	{
		return toBeSignedString;
	}

	public void setToBeSignedString(String toBeSignedString)
	{
		this.toBeSignedString = toBeSignedString;
	}

	public String getPolicyResource()
	{
		return policyResource;
	}

	public void setPolicyResource(String policyResource)
	{
		this.policyResource = policyResource;
	}

	public String getReqSignature()
	{
		return reqSignature;
	}

	public void setReqSignature(String reqSignature)
	{
		this.reqSignature = reqSignature;
	}

	public String getReqAccessKeyId()
	{
		return reqAccessKeyId;
	}

	public void setReqAccessKeyId(String reqAccessKeyId)
	{
		this.reqAccessKeyId = reqAccessKeyId;
	}

	public String getReqSignatureMethod()
	{
		return reqSignatureMethod;
	}

	public void setReqSignatureMethod(String reqSignatureMethod)
	{
		this.reqSignatureMethod = reqSignatureMethod;
	}

	public String getReqSignatureVersion()
	{
		return reqSignatureVersion;
	}

	public void setReqSignatureVersion(String reqSignatureVersion)
	{
		this.reqSignatureVersion = reqSignatureVersion;
	}

	public String getReqTimestamp()
	{
		return reqTimestamp;
	}

	public void setReqTimestamp(String reqTimestamp)
	{
		this.reqTimestamp = reqTimestamp;
	}

	public String getReqClientIP()
	{
		return reqClientIP;
	}

	public void setReqClientIP(String reqClientIP)
	{
		this.reqClientIP = reqClientIP;
	}

	@Override
	public String toString()
	{
		return "AuthenticateReq [toBeSignedString=" + toBeSignedString + ", policyResource=" + policyResource
		        + ", reqSignature=" + reqSignature + ", reqAccessKeyId=" + reqAccessKeyId + ", reqSignatureMethod="
		        + reqSignatureMethod + ", reqSignatureVersion=" + reqSignatureVersion + ", reqTimestamp="
		        + reqTimestamp + ", reqClientIP=" + reqClientIP + ", twsAccessKeyId=" + twsAccessKeyId
		        + ", signatureMethod=" + signatureMethod + ", signatureVersion=" + signatureVersion + ", timestamp="
		        + timestamp + ", signature=" + signature + "]";
	}

	@Override
	public boolean checkRequiredArgumentsSuccess()
	{
		if (!super.checkRequiredArgumentsSuccess())
		{
			return false;
		}

		if (StringUtils.isEmpty(toBeSignedString))
		{
			return false;
		}

		if (StringUtils.isEmpty(policyResource))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqSignature))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqAccessKeyId))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqSignatureMethod))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqSignatureVersion))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqTimestamp))
		{
			return false;
		}

		if (StringUtils.isEmpty(reqClientIP))
		{
			return false;
		}

		return true;

	}
}
