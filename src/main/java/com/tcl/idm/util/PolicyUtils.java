package com.tcl.idm.util;

import java.util.Map;

import net.sf.json.JSONArray;

import com.tcl.idm.model.PolicyDocument;
import com.tcl.idm.model.PolicyEffect;

/**
 * 策略工具类
 * 
 * @author yuanhuan
 * 2014年4月21日 下午3:38:24
 */
public class PolicyUtils
{
	private static boolean isResourceValid(String resource, Map<String, String[]> validResourceMap)
	{
		// 是否是所有资源
		if ("*:*".equals(resource))
		{
			return true;
		}

		// 检查ServiceName是否合法， 格式resource = [ServiceName]:[ApiName]
		String serviceName = resource.split(":")[0].trim();
		if (null == validResourceMap.get(serviceName))
		{
			return false;
		}

		// 检查ApiName是否合法， 格式resource = [ServiceName]:[ApiName]
		String apiName = resource.split(":")[1].trim();
		if ("*".equals(apiName))
		{
			return true;
		}

		String[] validApiNameArr = validResourceMap.get(serviceName);
		boolean validApiNameFlag = false;
		for (int validApiNameArrIndex = 0; validApiNameArrIndex < validApiNameArr.length; validApiNameArrIndex++)
		{
			if (apiName.equals(validApiNameArr[validApiNameArrIndex]))
			{
				validApiNameFlag = true;
				continue;
			}
		}
		if (!validApiNameFlag)
		{
			return false;
		}

		return true;
	}

	public static boolean isPolicyDocumentValid(String policyDocumentString)
	{
		try
		{
			PolicyDocument[] policyDocumentArray = IDMServiceUtils.convertToPolicyDocumentArray(policyDocumentString);
			if (null == policyDocumentArray || policyDocumentArray.length < 1)
			{
				return false;
			}

			// 获取合法的策略资源映射
			Map<String, String[]> validResourceMap = PolicyResourceUtils.getInstance().getValidResourceMap();

			// 遍历policyDocumentString，检查是否合法
			PolicyDocument policyDocument = null;
			for (int policyDocumentArrayIndex = 0; policyDocumentArrayIndex < policyDocumentArray.length; policyDocumentArrayIndex++)
			{
				policyDocument = policyDocumentArray[policyDocumentArrayIndex];

				// 检查effect是否合法
				if (!PolicyEffect.isPolicyEffectValid(policyDocument.getEffect()))
				{
					return false;
				}

				if (!PolicyUtils.isResourceValid(policyDocument.getResource(), validResourceMap))
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	public static boolean isResourceValid(String resource)
	{
		// 获取合法的策略资源映射
		Map<String, String[]> validResourceMap = PolicyResourceUtils.getInstance().getValidResourceMap();

		return PolicyUtils.isResourceValid(resource, validResourceMap);
	}

	public static void main(String[] args)
	{
		PolicyDocument pd1 = new PolicyDocument();
		pd1.setEffect(PolicyEffect.ALLOW);
		pd1.setResource("*:*");
		PolicyDocument pd2 = new PolicyDocument();
		pd2.setEffect(PolicyEffect.ALLOW);
		pd2.setResource("transcode:logo");
		PolicyDocument pd3 = new PolicyDocument();
		pd3.setEffect(PolicyEffect.DENY);
		pd3.setResource("cameratake:*");

		PolicyDocument[] pdArr = new PolicyDocument[3];
		pdArr[0] = pd1;
		pdArr[1] = pd2;
		pdArr[2] = pd3;

		String policyDocumentString = JSONArray.fromObject(pdArr).toString();
		boolean result = PolicyUtils.isPolicyDocumentValid(policyDocumentString);
		System.out.println("isPolicyDocumentValid: " + result);

		System.out.println("TEST 001 isResourceValid: " + PolicyUtils.isResourceValid("*:jhghj"));
		System.out.println("TEST 002 isResourceValid: " + PolicyUtils.isResourceValid("*:*"));
		System.out.println("TEST 003 isResourceValid: " + PolicyUtils.isResourceValid("transcode:*&"));
		System.out.println("TEST 004 isResourceValid: " + PolicyUtils.isResourceValid("transcode:*"));
		System.out.println("TEST 005 isResourceValid: " + PolicyUtils.isResourceValid("transcode:logo"));
		System.out.println("TEST 006 isResourceValid: " + PolicyUtils.isResourceValid("transcode:transcoder"));
		System.out.println("TEST 007 isResourceValid: " + PolicyUtils.isResourceValid("transcode:snapshots"));
		System.out.println("TEST 008 isResourceValid: " + PolicyUtils.isResourceValid("transcode:insertion"));
		System.out.println("TEST 009 isResourceValid: " + PolicyUtils.isResourceValid("transcode:clipper"));
		System.out.println("TEST 010 isResourceValid: " + PolicyUtils.isResourceValid("cameratake:asdfsf"));
		System.out.println("TEST 011 isResourceValid: " + PolicyUtils.isResourceValid("transcode:cameratake"));
		System.out.println("TEST 012 isResourceValid: " + PolicyUtils.isResourceValid("cameratake:*"));
		System.out.println("TEST 013 isResourceValid: " + PolicyUtils.isResourceValid("asdfh:*"));
		System.out.println("TEST 014 isResourceValid: " + PolicyUtils.isResourceValid("asdfh:asdf"));
		System.out.println("TEST 015 isResourceValid: " + PolicyUtils.isResourceValid("cameratake:cameratake"));
	}
}
