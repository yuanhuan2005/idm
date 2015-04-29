package com.tcl.idm.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月21日 下午3:44:29
 */
public class PolicyResourceUtils
{
	Map<String, String[]> validResourceMap = new HashMap<String, String[]>();

	private static class SingletonHolder
	{
		private static final PolicyResourceUtils INSTANCE = new PolicyResourceUtils();
	}

	private PolicyResourceUtils()
	{
	}

	public static final PolicyResourceUtils getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public Map<String, String[]> getValidResourceMap()
	{
		// add service: jobs
		String jobsKey = "jobs";
		String[] jobsValue = { "createJob", "listJobs", "getJob", "getVideoInfo" };
		validResourceMap.put(jobsKey, jobsValue);

		return validResourceMap;
	}

}
