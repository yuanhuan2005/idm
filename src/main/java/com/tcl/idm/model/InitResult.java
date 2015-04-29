package com.tcl.idm.model;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月16日 下午5:43:12
 */
public class InitResult
{
	private String adminUserId;

	private String adminUserName;

	private String adminAccessKeyId;

	private String adminSecretAccessKey;

	private String adminPassword;

	private String innerUserId;

	private String innerUserName;

	private String innerAccessKeyId;

	private String innerSecretAccessKey;

	private String innerPassword;

	public String getAdminUserId()
	{
		return adminUserId;
	}

	public void setAdminUserId(String adminUserId)
	{
		this.adminUserId = adminUserId;
	}

	public String getAdminUserName()
	{
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName)
	{
		this.adminUserName = adminUserName;
	}

	public String getAdminAccessKeyId()
	{
		return adminAccessKeyId;
	}

	public void setAdminAccessKeyId(String adminAccessKeyId)
	{
		this.adminAccessKeyId = adminAccessKeyId;
	}

	public String getAdminSecretAccessKey()
	{
		return adminSecretAccessKey;
	}

	public void setAdminSecretAccessKey(String adminSecretAccessKey)
	{
		this.adminSecretAccessKey = adminSecretAccessKey;
	}

	public String getAdminPassword()
	{
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword)
	{
		this.adminPassword = adminPassword;
	}

	public String getInnerUserId()
	{
		return innerUserId;
	}

	public void setInnerUserId(String innerUserId)
	{
		this.innerUserId = innerUserId;
	}

	public String getInnerUserName()
	{
		return innerUserName;
	}

	public void setInnerUserName(String innerUserName)
	{
		this.innerUserName = innerUserName;
	}

	public String getInnerAccessKeyId()
	{
		return innerAccessKeyId;
	}

	public void setInnerAccessKeyId(String innerAccessKeyId)
	{
		this.innerAccessKeyId = innerAccessKeyId;
	}

	public String getInnerSecretAccessKey()
	{
		return innerSecretAccessKey;
	}

	public void setInnerSecretAccessKey(String innerSecretAccessKey)
	{
		this.innerSecretAccessKey = innerSecretAccessKey;
	}

	public String getInnerPassword()
	{
		return innerPassword;
	}

	public void setInnerPassword(String innerPassword)
	{
		this.innerPassword = innerPassword;
	}

	@Override
	public String toString()
	{
		return "InitResult [adminUserId=" + adminUserId + ", adminUserName=" + adminUserName + ", adminAccessKeyId="
		        + adminAccessKeyId + ", adminSecretAccessKey=" + adminSecretAccessKey + ", adminPassword="
		        + adminPassword + ", innerUserId=" + innerUserId + ", innerUserName=" + innerUserName
		        + ", innerAccessKeyId=" + innerAccessKeyId + ", innerSecretAccessKey=" + innerSecretAccessKey
		        + ", innerPassword=" + innerPassword + "]";
	}

}
