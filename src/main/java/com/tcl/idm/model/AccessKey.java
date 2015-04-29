package com.tcl.idm.model;

/**
 * 访问密钥
 * 
 * @author yuanhuan
 * 2014年3月27日 下午4:09:36
 */
public class AccessKey
{
	/**
	 * 访问密钥
	 */
	private String accessKeyId;

	/**
	 * 秘密访问密钥
	 */
	private String secretAccessKey;

	/**
	 * 状态：active/inactive
	 */
	private String status;

	/**
	 * 属主ID
	 */
	private String ownerId;

	/**
	 * 创建时间
	 */
	private String createDate;

	public String getAccessKeyId()
	{
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId)
	{
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey()
	{
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey)
	{
		this.secretAccessKey = secretAccessKey;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(String ownerId)
	{
		this.ownerId = ownerId;
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
		return "AccessKey [accessKeyId=" + accessKeyId + ", secretAccessKey=" + secretAccessKey + ", status=" + status
		        + ", ownerId=" + ownerId + ", createDate=" + createDate + "]";
	}

}
