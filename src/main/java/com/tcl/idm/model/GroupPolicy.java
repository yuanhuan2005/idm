package com.tcl.idm.model;

import java.util.Arrays;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月14日 下午3:12:48
 */
public class GroupPolicy extends PolicyBase
{
	private String groupId;

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	@Override
	public String toString()
	{
		return "GroupPolicy [groupId=" + groupId + ", policyId=" + policyId + ", policyName=" + policyName
		        + ", policyDocument=" + Arrays.toString(policyDocument) + "]";
	}

}
