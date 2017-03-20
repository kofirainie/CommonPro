package com.gpstrace.dlrc.model;

/**
 * @请求闪屏页文章内容
 * @author YunZ
 * 
 */
public class RequestSplash
{
	// region fields

	protected int version = 1;// 版本号

	// endregion

	/**
	 * @采用Get方式获取闪屏概要
	 * @return
	 */
	public String requestSummary()
	{
		String cmdStr = "?company_code=youcai&version=" + version;

		return cmdStr;
	}
}
