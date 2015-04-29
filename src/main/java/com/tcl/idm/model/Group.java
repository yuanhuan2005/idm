package com.tcl.idm.model;

/**
 * Group实例
 * 
 * @author yuanhuan
 * 2014年4月2日 上午9:43:50
 */
public class Group
{
	/**
	 * 组ID
	 */
	private String groupId;

	/**
	 * 组名
	 */
	private String groupName;

	/**
	 * 所属的账户ID
	 */
	private String accountId;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 创建时间
	 */
	private String createDate;

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}

	@Override
	public String toString()
	{
		return "Group [groupId=" + groupId + ", groupName=" + groupName + ", accountId=" + accountId + ", description="
		        + description + ", createDate=" + createDate + ", getGroupId()=" + getGroupId() + ", getGroupName()="
		        + getGroupName() + ", getAccountId()=" + getAccountId() + ", getDescription()=" + getDescription()
		        + ", getCreateDate()=" + getCreateDate() + "]";
	}

}
