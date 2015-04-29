package com.tcl.idm.model;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月17日 下午1:18:15
 */
public class IpaddressRequestLimit
{
	private String ipaddress;

	private int requestTimes;

	private String currentMinute;

	public String getIpaddress()
	{
		return ipaddress;
	}

	public void setIpaddress(String ipaddress)
	{
		this.ipaddress = ipaddress;
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
		return "IpaddrRequestLimit [ipaddress=" + ipaddress + ", requestTimes=" + requestTimes + ", currentMinute="
		        + currentMinute + "]";
	}

}
