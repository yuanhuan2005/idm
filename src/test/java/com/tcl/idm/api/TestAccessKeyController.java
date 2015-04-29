package com.tcl.idm.api;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tcl.idm.model.CustomHttpResponse;
import com.tcl.idm.util.DefaultInfo;
import com.tcl.idm.util.HttpRequestUtils;

/**
 * µ•‘™≤‚ ‘”√¿˝
 * 
 * @author yuanhuan
 * 
 */
public class TestAccessKeyController
{
	@Before
	public void initTest()
	{
	}

	@Test
	public void testCreateAccessKeySuccess()
	{
		// create AccessKey
		String createAccessKey = DefaultInfo.IDM_SERVER_URL + "/createAccessKey?twsAccessKeyId="
		        + DefaultInfo.ACCOUNT_ACCESS_KEY_ID + "&userId=" + DefaultInfo.USER_ID;
		CustomHttpResponse result = HttpRequestUtils.sendGetRequest(createAccessKey);
		Assert.assertEquals(result.getResponseMessage(), HttpServletResponse.SC_OK, result.getHttpStatusCode());
		String accessKeyId = JSONObject.fromObject(result.getResponseMessage()).getString("accessKeyId");

		// list all AccessKeys
		String listAllAccessKeysUrl = DefaultInfo.IDM_SERVER_URL + "/listAccessKeys?twsAccessKeyId="
		        + DefaultInfo.ACCOUNT_ACCESS_KEY_ID;
		result = HttpRequestUtils.sendGetRequest(listAllAccessKeysUrl);
		Assert.assertEquals(result.getResponseMessage(), HttpServletResponse.SC_OK, result.getHttpStatusCode());

		// list all AccessKeys
		String listUserAccessKeysUrl = DefaultInfo.IDM_SERVER_URL + "/listAccessKeys?twsAccessKeyId="
		        + DefaultInfo.ACCOUNT_ACCESS_KEY_ID + "&userId=" + DefaultInfo.USER_ID;
		result = HttpRequestUtils.sendGetRequest(listUserAccessKeysUrl);
		Assert.assertEquals(result.getResponseMessage(), HttpServletResponse.SC_OK, result.getHttpStatusCode());

		// update AccessKey
		String updateAccessKeyUrl = DefaultInfo.IDM_SERVER_URL + "/updateAccessKey?twsAccessKeyId="
		        + DefaultInfo.ACCOUNT_ACCESS_KEY_ID + "&accessKeyId=" + accessKeyId + "&status=inactive";
		result = HttpRequestUtils.sendGetRequest(updateAccessKeyUrl);
		Assert.assertEquals(result.getResponseMessage(), HttpServletResponse.SC_OK, result.getHttpStatusCode());

		// delete AccessKey
		String deleteAccessKeyUrl = DefaultInfo.IDM_SERVER_URL + "/deleteAccessKey?twsAccessKeyId="
		        + DefaultInfo.ACCOUNT_ACCESS_KEY_ID + "&accessKeyId=" + accessKeyId;
		result = HttpRequestUtils.sendGetRequest(deleteAccessKeyUrl);
		Assert.assertEquals(result.getResponseMessage(), HttpServletResponse.SC_OK, result.getHttpStatusCode());
	}

	@Test
	public void testDeleteAccessKeySuccess()
	{

	}
}
