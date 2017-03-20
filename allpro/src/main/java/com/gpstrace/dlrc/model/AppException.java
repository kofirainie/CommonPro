package com.gpstrace.dlrc.model;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpException;

import com.gpstrace.dlrc.finals.CrashFinals;


/**
 * @异常类
 * @author YunZ
 *
 */
public class AppException extends Exception
{
	// region fields

	private byte mType;// 异常类型
	private int mCode;// 异常码

	// endregion

	// region propertys

	/**
	 * @获取异常码
	 * @除了Http异常的code外，其他code默认为0
	 * @return
	 */
	public int getCode()
	{
		return this.mCode;
	}

	/**
	 * @获取异常类型
	 * @return
	 */
	public byte getType()
	{
		return this.mType;
	}

	// endregion

	// region constructor

	/**
	 * @构造函数
	 * @param type
	 * @异常类型
	 * @param code
	 * @异常码
	 * @param excp
	 * @捕获的异常
	 */
	private AppException(byte type, int code, Exception excp)
	{
		super(excp);
		this.mType = type;
		this.mCode = code;
	}

	// endregion

	// region methods

	/**
	 * @Http状态码异常
	 * @param code
	 * @return
	 */
	public static AppException getHttpExp(int code)
	{
		return new AppException(CrashFinals.TYPE_HTTP_CODE, code, null);
	}

	/**
	 * @Http异常
	 * @param ex
	 * @return
	 */
	public static AppException getHttpExp(Exception ex)
	{
		return new AppException(CrashFinals.TYPE_HTTP_ERROR, 0, ex);
	}

	/**
	 * @Socket异常
	 * @param ex
	 * @return
	 */
	public static AppException getSocketExp(Exception ex)
	{
		return new AppException(CrashFinals.TYPE_SOCKET, 0, ex);
	}

	/**
	 * @网络异常
	 * @param e
	 * @return
	 */
	public static AppException getNetworkExp(Exception ex)
	{
		if (ex instanceof UnknownHostException
				|| ex instanceof ConnectException)
		{
			return new AppException(CrashFinals.TYPE_NETWORK, 0, ex);
		} else if (ex instanceof HttpException
				|| ex instanceof IllegalStateException)
		{
			return getHttpExp(ex);
		} else if (ex instanceof SocketException)
		{
			return getSocketExp(ex);
		}
		return getHttpExp(ex);
	}

	/**
	 * @XML处理异常
	 * @param e
	 * @return
	 */
	public static AppException getXmlExp(Exception ex)
	{
		return new AppException(CrashFinals.TYPE_XML, 0, ex);
	}

	/**
	 * @Json处理异常
	 * @param e
	 * @return
	 */
	public static AppException getJsonExp(Exception ex)
	{
		return new AppException(CrashFinals.TYPE_JSON, 0, ex);
	}

	/**
	 * @获取IO异常
	 * @param ex
	 * @return
	 */
	public static AppException getIoExp(Exception ex)
	{
		if (ex instanceof UnknownHostException
				|| ex instanceof ConnectException)
		{
			return new AppException(CrashFinals.TYPE_NETWORK, 0, ex);
		} else if (ex instanceof IOException)
		{
			return new AppException(CrashFinals.TYPE_IO, 0, ex);
		}
		return getUnknowExp(ex);
	}

	/**
	 * @捕获未知异常
	 * @param ex
	 * @return
	 */
	public static AppException getUnknowExp(Exception ex)
	{
		return new AppException(CrashFinals.TYPE_UNKNOW, 0, ex);
	}

	// endregion

}
