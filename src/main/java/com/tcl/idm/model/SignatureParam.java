package com.tcl.idm.model;

import org.apache.commons.lang.StringUtils;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月17日 上午10:52:47
 */
public class SignatureParam
{
	protected String twsAccessKeyId;

	protected String signatureMethod;

	protected String signatureVersion;

	protected String timestamp;

	protected String signature;

	public boolean checkRequiredArgumentsSuccess()
	{
		if (StringUtils.isEmpty(twsAccessKeyId))
		{
			return false;
		}

		if (StringUtils.isEmpty(signatureMethod))
		{
			return false;
		}

		if (StringUtils.isEmpty(signatureVersion))
		{
			return false;
		}

		if (StringUtils.isEmpty(timestamp))
		{
			return false;
		}

		if (StringUtils.isEmpty(signature))
		{
			return false;
		}

		return true;
	}

	public String getTwsAccessKeyId()
	{
		return twsAccessKeyId;
	}

	public void setTwsAccessKeyId(String twsAccessKeyId)
	{
		this.twsAccessKeyId = twsAccessKeyId;
	}

	public String getSignatureMethod()
	{
		return signatureMethod;
	}

	public void setSignatureMethod(String signatureMethod)
	{
		this.signatureMethod = signatureMethod;
	}

	public String getSignatureVersion()
	{
		return signatureVersion;
	}

	public void setSignatureVersion(String signatureVersion)
	{
		this.signatureVersion = signatureVersion;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	@Override
	public String toString()
	{
		return "SignatureParam [twsAccessKeyId=" + twsAccessKeyId + ", signatureMethod=" + signatureMethod
		        + ", signatureVersion=" + signatureVersion + ", timestamp=" + timestamp + ", signature=" + signature
		        + "]";
	}

}
