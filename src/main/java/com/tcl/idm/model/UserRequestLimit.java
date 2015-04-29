package com.tcl.idm.model;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月17日 下午1:18:15
 */
public class UserRequestLimit
{
	private String userId;

	private int requestTimes;

	private String currentMinute;

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public int getRequestTimes()
	{
		return requestTimes;
	}

	public void setRequestTimes(int requestTimes)
	{
		this.requestTimes = requestTimes;
	}

	public String getCurrentMinute()
	{
		return currentMinute;
	}

	public void setCurrentMinute(String currentMinute)
	{
		this.currentMinute = currentMinute;
	}

	@Override
	public String toString()
	{
		return "UserRequestLimit [userId=" + userId + ", requestTimes=" + requestTimes + ", currentMinute="
		        + currentMinute + "]";
	}

}
