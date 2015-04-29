package com.tcl.idm.model;

import java.util.Arrays;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月14日 下午3:12:48
 */
public class PolicyBase
{
	protected String policyId;

	protected String policyName;

	protected PolicyDocument[] policyDocument;

	public String getPolicyId()
	{
		return policyId;
	}

	public void setPolicyId(String policyId)
	{
		this.policyId = policyId;
	}

	public String getPolicyName()
	{
		return policyName;
	}

	public void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}

	public PolicyDocument[] getPolicyDocument()
	{
		return policyDocument;
	}

	public void setPolicyDocument(PolicyDocument[] policyDocument)
	{
		this.policyDocument = policyDocument;
	}

	@Override
	public String toString()
	{
		return "PolicyBase [policyId=" + policyId + ", policyName=" + policyName + ", policyDocument="
		        + Arrays.toString(policyDocument) + "]";
	}

}
