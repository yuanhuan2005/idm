package com.tcl.idm.model;

/**
 * 用户类
 * 
 * @author yuanhuan
 * 2014年3月20日 下午12:58:23
 */
public class User
{
	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 用户类型：account/user
	 */
	private String userType;

	/**
	 * 所属的账户ID
	 */
	private String accountId;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 创建时间
	 */
	private String createDate;

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserType()
	{
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
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
		return "User [userId=" + userId + ", userName=" + userName + ", userType=" + userType + ", accountId="
		        + accountId + ", password=" + password + ", createDate=" + createDate + "]";
	}

	public Account convertToAccount()
	{
		Account account = new Account();
		account.setAccountId(userId);
		account.setAccountName(userName);
		account.setCreateDate(createDate);
		account.setPassword(password);
		account.setDefaultAccessKeyId("");
		account.setDefaultSecretAccessKey("");
		return account;
	}
}
