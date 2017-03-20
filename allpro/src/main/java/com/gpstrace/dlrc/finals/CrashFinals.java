package com.gpstrace.dlrc.finals;

/**
 * @异常常量
 * @author YunZ
 *
 */
public class CrashFinals
{
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_JSON = 0x06;
	public final static byte TYPE_IO = 0x07;
	public final static byte TYPE_UNKNOW = 0x08;

	public static final String CRASH_TAG = "CrashHandler";
}
