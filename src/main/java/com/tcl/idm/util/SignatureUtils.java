package com.tcl.idm.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* This class defines common routines for generating
* authentication signatures for AWS Platform requests.
*/
public class SignatureUtils
{
	final static private Log DEBUGGER = LogFactory.getLog(SignatureUtils.class);

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	/**
	 * Computes RFC 2104-compliant HMAC signature.
	 * * @param data
	 * The signed data.
	 * @param key
	 * The signing key.
	 * @return
	 * The Base64-encoded RFC 2104-compliant HMAC signature.
	 * @throws
	 * java.security.SignatureException when signature generation fails
	 */
	public static String calculateRFC2104HMAC(String data, String key)
	{
		String result = "";
		try
		{
			// Get an hmac_sha256 key from the raw key bytes.
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), SignatureUtils.HMAC_SHA256_ALGORITHM);

			// Get an hmac_sha256 Mac instance and initialize with the signing key.
			Mac mac = Mac.getInstance(SignatureUtils.HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);

			// Compute the hmac on input data bytes.
			byte[] rawHmac = mac.doFinal(data.getBytes("UTF8"));

			// Base64-encode the hmac by using the utility in the SDK
			result = BinaryUtils.toBase64(rawHmac);
		}
		catch (Exception e)
		{
			SignatureUtils.DEBUGGER.error("Failed to generate HMAC : " + e.getMessage());
			return result;
		}

		return result;
	}

}