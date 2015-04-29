package com.tcl.idm.model;

/**
 * API错误码定义
 * @author TCL
 *
 */
public enum CustomErrorCode
{
	@Error(msg = "The request has been successfully processed.")
	Success,

	@Error(msg = "A required parameter for the specified action is not supplied.")
	MissingParameter,

	@Error(msg = "An invalid or out-of-range value was supplied for the input parameter.")
	InvalidParameter,

	@Error(msg = "The request signature we calculated does not match the signature you provided.")
	SignatureDoesNotMatch,

	@Error(msg = "The request was rejected because it attempted to create a resource that already exists.")
	EntityAlreadyExists,

	@Error(msg = "The request was rejected because it referenced an entity that does not exist.")
	NoSuchEntity,

	@Error(msg = "The request was rejected because it attempted to create resources beyond the current account limits.")
	LimitExceeded,

	@Error(msg = "The request was rejected because the policy document was malformed.")
	MalformedPolicyDocument,

	@Error(msg = "The request access is not authorized.")
	Unauthorized,

	@Error(msg = "The provided password does not match the real password.")
	PasswordNotMatch,

	@Error(msg = "The request access key is not active.")
	AccessKeyInactive,

	@Error(msg = "Access key was already active.")
	AccessKeyAlreadyActive,

	@Error(msg = "Access key was already inactive.")
	AccessKeyAlreadyInactive,

	@Error(msg = "The provided credentials could not be validated.")
	AuthFailure,

	@Error(msg = "The request processing has failed because of an unknown error, exception or failure.")
	InternalError,

	@Error(msg = "The request is expired.")
	RequestExpired,

	@Error(msg = "Unknown error.")
	UnknownError;

	/**
	 * 返回错误码
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * 返回错误信息
	 */
	public String getMessage()
	{
		Error error = null;
		try
		{
			error = this.getClass().getField(getCode()).getAnnotation(Error.class);
		}
		catch (Exception e)
		{
			return null;
		}
		return error.msg();
	}
}
