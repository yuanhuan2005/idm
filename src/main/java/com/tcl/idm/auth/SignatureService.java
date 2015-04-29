package com.tcl.idm.auth;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcl.idm.model.AccessKey;
import com.tcl.idm.model.AuthenticationResult;
import com.tcl.idm.model.CustomErrorCode;
import com.tcl.idm.model.IdmHttpReq;
import com.tcl.idm.model.SignatureParam;
import com.tcl.idm.repository.AccessKeyRepository;
import com.tcl.idm.util.AESUtils;
import com.tcl.idm.util.CommonService;
import com.tcl.idm.util.DateFormatterUtils;
import com.tcl.idm.util.HttpRequestUtils;
import com.tcl.idm.util.LimitsUtils;
import com.tcl.idm.util.SignatureUtils;

/**
 *
 * 
 * @author yuanhuan
 * 2014年4月17日 上午10:05:11
 */
public class SignatureService
{
	final static public Log DEBUGGER = LogFactory.getLog(SignatureService.class);

	final static public String SIGNATURE_METHOD = "HmacSHA256";

	final static public String SIGNATURE_VERSION = "1";

	public static String getHttpGETParamsWithoutSignatue(String queryString)
	{
		String params = "";
		StringBuffer paramsBuffer = new StringBuffer();
		String key = "";
		String value = "";
		if (StringUtils.isEmpty(queryString))
		{
			return "";
		}

		try
		{
			String origParams = queryString;

			// 去掉Signature
			String[] origParamsArr = origParams.split("&");
			if (null != origParamsArr && origParamsArr.length > 0)
			{
				for (int i = 0; i < origParamsArr.length; i++)
				{
					key = origParamsArr[i].split("=")[0];
					value = origParamsArr[i].split("=")[1];
					if ("signature".equals(key))
					{
						continue;
					}

					paramsBuffer.append(key + "=" + value + "&");
				}

				params = paramsBuffer.toString().substring(0, paramsBuffer.toString().length() - 1);
			}
		}
		catch (Exception e)
		{
			SignatureService.DEBUGGER.error("Exception: " + e.toString());
		}

		return params;
	}

	public static String getHttpPOSTParamsWithoutSignatue(String postData)
	{
		String params = "";
		try
		{
			params = JSONObject.fromObject(postData).discard("signature").toString();
		}
		catch (Exception e)
		{
			SignatureService.DEBUGGER.error("Exception: " + e.toString());
		}

		return params;
	}

	/**
	 * 获取Params，不包括Signature
	 * 
	 * @param request
	 * @return
	 */
	public static String getParamsWithoutSignatue(IdmHttpReq idmHttpReq)
	{
		String params = "";

		if (RequestMethod.GET.toString().equals(idmHttpReq.getHttpMethod()))
		{
			return SignatureService.getHttpGETParamsWithoutSignatue(idmHttpReq.getQueryString());
		}

		if (RequestMethod.POST.toString().equals(idmHttpReq.getHttpMethod()))
		{
			return SignatureService.getHttpPOSTParamsWithoutSignatue(idmHttpReq.getPostData());
		}

		return params;
	}

	/**
	 * 从请求中构造签名字符串
	 * 
	 * @param request
	 * @return
	 */
	public static String genSignatureString(IdmHttpReq idmHttpReq)
	{
		StringBuffer signatureStringBuffer = new StringBuffer();

		try
		{
			String httpMethod = idmHttpReq.getHttpMethod();
			String host = idmHttpReq.getHost();
			String uri = idmHttpReq.getUri();
			String params = SignatureService.getParamsWithoutSignatue(idmHttpReq);

			signatureStringBuffer.append(httpMethod);
			signatureStringBuffer.append("\n");
			signatureStringBuffer.append(host);
			signatureStringBuffer.append("\n");
			signatureStringBuffer.append(uri);
			signatureStringBuffer.append("\n");
			signatureStringBuffer.append(params);
		}
		catch (Exception e)
		{
			SignatureService.DEBUGGER.error("Exception: " + e.toString());
			return "";
		}

		SignatureService.DEBUGGER.debug("signatureStringBuffer: \n" + signatureStringBuffer.toString());
		return signatureStringBuffer.toString();
	}

	public static SignatureParam getSignatureParam(IdmHttpReq idmHttpReq)
	{
		SignatureParam signatureParam = new SignatureParam();

		String twsAccessKeyId = "";
		String signatureMethod = "";
		String signatureVersion = "";
		String timestamp = "";
		String signature = "";
		try
		{
			twsAccessKeyId = HttpRequestUtils.getParamValue(idmHttpReq, "twsAccessKeyId");
			signatureMethod = HttpRequestUtils.getParamValue(idmHttpReq, "signatureMethod");
			signatureVersion = HttpRequestUtils.getParamValue(idmHttpReq, "signatureVersion");
			timestamp = HttpRequestUtils.getParamValue(idmHttpReq, "timestamp");
			signature = HttpRequestUtils.getParamValue(idmHttpReq, "signature");
			if (StringUtils.isEmpty(twsAccessKeyId) || StringUtils.isEmpty(signatureMethod)
			        || StringUtils.isEmpty(signatureVersion) || StringUtils.isEmpty(timestamp)
			        || StringUtils.isEmpty(signature))
			{
				SignatureService.DEBUGGER.error("miss parameter");
				return signatureParam;
			}
		}
		catch (Exception e)
		{
			SignatureService.DEBUGGER.error("Exception: " + e.toString());
			return signatureParam;
		}

		signatureParam.setSignature(signature);
		signatureParam.setSignatureMethod(signatureMethod);
		signatureParam.setSignatureVersion(signatureVersion);
		signatureParam.setTimestamp(timestamp);
		signatureParam.setTwsAccessKeyId(twsAccessKeyId);
		return signatureParam;
	}

	/**
	 * 检查数字签名
	 * 
	 * @return
	 */
	public static AuthenticationResult checkSignature(IdmHttpReq idmHttpReq)
	{
		AuthenticationResult signCheckResult = new AuthenticationResult();
		String signatureString = SignatureService.genSignatureString(idmHttpReq);
		SignatureParam signatureParam = SignatureService.getSignatureParam(idmHttpReq);

		if (!signatureParam.checkRequiredArgumentsSuccess())
		{
			signCheckResult.setErrorCode(CustomErrorCode.MissingParameter.getCode());
			signCheckResult.setErrorMessage(CustomErrorCode.MissingParameter.getMessage());
			signCheckResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			return signCheckResult;
		}

		// 检查签名方法和版本是否匹配
		String signatureMethod = signatureParam.getSignatureMethod();
		String signatureVersion = signatureParam.getSignatureVersion();
		if (!SignatureService.SIGNATURE_METHOD.equals(signatureMethod)
		        || !SignatureService.SIGNATURE_VERSION.equals(signatureVersion))
		{
			signCheckResult.setErrorCode(CustomErrorCode.InvalidParameter.getCode());
			signCheckResult.setErrorMessage(CustomErrorCode.InvalidParameter.getMessage());
			signCheckResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			return signCheckResult;
		}

		// 检查时间戳是否合法
		long timestampExpiredInMilliseconds = LimitsUtils.getTimestampExpiredInMinutes() * 60 * 1000;
		long timestampDateMilliseconds = DateFormatterUtils.convertStringToDate(signatureParam.getTimestamp())
		        .getTime();
		String currentUTCTimeString = DateFormatterUtils.getCurrentUTCTime();
		SignatureService.DEBUGGER.debug("currentUTCTimeString: " + currentUTCTimeString);
		long currentDateMilliseconds = DateFormatterUtils.convertStringToDate(currentUTCTimeString).getTime();
		if (timestampDateMilliseconds < (currentDateMilliseconds - timestampExpiredInMilliseconds)
		        || timestampDateMilliseconds > (currentDateMilliseconds + timestampExpiredInMilliseconds))
		{
			signCheckResult.setErrorCode(CustomErrorCode.RequestExpired.getCode());
			signCheckResult.setErrorMessage(CustomErrorCode.RequestExpired.getMessage());
			signCheckResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			return signCheckResult;
		}

		// 查找AccessKey
		String accessKeyId = signatureParam.getTwsAccessKeyId();
		AccessKey accessKey = AccessKeyRepository.getAccessKey(accessKeyId);
		if (null == accessKey)
		{
			signCheckResult.setErrorCode(CustomErrorCode.NoSuchEntity.getCode());
			signCheckResult.setErrorMessage(CustomErrorCode.NoSuchEntity.getMessage());
			signCheckResult.setHttpResultCode(HttpServletResponse.SC_NOT_FOUND);
			return signCheckResult;
		}

		// 在服务端重新生成签名与客户端传过来的的签名进行对比
		String clientSignature = signatureParam.getSignature();
		String serverSignature = SignatureUtils.calculateRFC2104HMAC(signatureString,
		        AESUtils.decrypt(accessKey.getSecretAccessKey()));
		try
		{
			serverSignature = CommonService.getURLDecodeString(serverSignature);
		}
		catch (Exception e)
		{
		}

		if (!clientSignature.equals(serverSignature))
		{
			signCheckResult.setErrorCode(CustomErrorCode.SignatureDoesNotMatch.getCode());
			signCheckResult.setErrorMessage(CustomErrorCode.SignatureDoesNotMatch.getMessage());
			signCheckResult.setHttpResultCode(HttpServletResponse.SC_BAD_REQUEST);
			return signCheckResult;
		}

		// 设置成功消息
		signCheckResult.setErrorCode(CustomErrorCode.Success.getCode());
		signCheckResult.setErrorMessage(CustomErrorCode.Success.getMessage());
		signCheckResult.setHttpResultCode(HttpServletResponse.SC_OK);
		return signCheckResult;
	}
}
