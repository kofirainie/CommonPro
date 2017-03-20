package com.gpstrace.dlrc.model;

/**
 * @闪屏概要返回
 * @author YunZ
 * 
 */
public class ResponseSplashSummary extends BaseResponse
{
	// region fields

	protected SplashSummary data;

	// endregion

	// region propertys

	/**
	 * @获取概要
	 * @return
	 */
	public SplashSummary getSummary()
	{
		return this.data;
	}

	// endregion
}
