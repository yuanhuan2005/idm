package com.tcl.idm.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.tcl.idm.model.PolicyDocument;

/**
 * IDM工具类
 * 
 * @author yuanhuan
 * 2014年4月4日 上午9:38:08
 */
public class IDMServiceUtils
{
	public static String genAccessKey()
	{
		return CommonService.randomString(32);
	}

	public static String genSecretAccessKey()
	{
		return CommonService.randomString(48);
	}

	public static String genOpenIDMObjectId()
	{
		return CommonService.randomString(32);
	}

	public static PolicyDocument[] convertToPolicyDocumentArray(String policyDocumentJsonString)
	{
		PolicyDocument[] policyDocumentArr = null;

		try
		{
			JSONArray policyDocumentJSONArray = JSONArray.fromObject(policyDocumentJsonString);
			if (null != policyDocumentJSONArray && policyDocumentJSONArray.size() > 0)
			{
				policyDocumentArr = new PolicyDocument[policyDocumentJSONArray.size()];
				PolicyDocument policyDocument = null;
				for (int i = 0; i < policyDocumentJSONArray.size(); i++)
				{
					policyDocument = new PolicyDocument();
					policyDocument.setEffect(((JSONObject) policyDocumentJSONArray.get(i)).getString("effect"));
					policyDocument.setResource(((JSONObject) policyDocumentJSONArray.get(i)).getString("resource"));
					policyDocumentArr[i] = policyDocument;
				}
			}
		}
		catch (Exception e)
		{
		}

		return policyDocumentArr;
	}

}
